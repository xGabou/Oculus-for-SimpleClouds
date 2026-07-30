# Better Simple Clouds: Iris Compatibility and Cloud Interior Analysis

Date: 2026-07-30

## Scope

This document records how the added `better-simple-clouds-main` project:

- makes Simple Clouds look compatible with Iris shaderpacks;
- creates a visible cloud interior;
- differs from the current Oculus for Simple Clouds implementation;
- could be ported from NeoForge 1.21.1 to Forge 1.20.1;
- conflicts with the current Oculus + Distant Horizons rendering fixes.

This was a read-only source investigation. No rendering fix was applied or tested as part of this analysis.

Before this analysis, `simpleclouds_dh_clouds_missing_notes.md` was reviewed. The compute-generated interior geometry
approach described below has not previously been attempted in this project.

## Main Finding

Better Simple Clouds does **not** patch every Iris shaderpack or inject code into each shaderpack's GLSL.

Instead, it relies on an important property of Simple Clouds:

- Simple Clouds renders its cloud mesh with its own custom instanced shaders.
- Iris does not replace that custom Simple Clouds draw with the shaderpack's terrain/cloud programs.
- Better Simple Clouds replaces or modifies the shaders owned by Simple Clouds itself.
- The cloud interior is emitted as real Simple Clouds mesh geometry.

Because the interior is real geometry inside the existing Simple Clouds render path, it works independently of the
active Iris shaderpack. The shaderpack does not need to understand or explicitly support the added cubes.

The resulting cloud lighting is a generic visual match to the shader-lit scene, not exact integration with every
shaderpack's private lighting calculations.

## How Shaderpack Compatibility Works

### 1. It swaps Simple Clouds' own shaders

Relevant file:

```text
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/mixin/SimpleCloudsShadersMixin.java
```

`SimpleCloudsShadersMixin` intercepts the `ResourceLocation` passed while Simple Clouds registers its cloud shader
instances.

It redirects:

```text
simpleclouds:clouds
```

to:

```text
bettersimpleclouds:clouds
```

and redirects:

```text
simpleclouds:clouds_transparency
```

to:

```text
bettersimpleclouds:clouds_transparency
```

The replacement shader JSON files continue using Simple Clouds' original vertex shaders. This preserves the SSBO
geometry expansion and only replaces the fragment stage.

The Simple Clouds cloud-shadow shaders remain unchanged.

### 2. It applies a generic scene-matching grade

Relevant files:

```text
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/mixin/CloudShaderMatchMixin.java
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/CloudSceneGrade.java
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/CloudNightGrade.java
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/CloudMoonGlow.java
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/CloudSoftFade.java
```

Before the opaque cloud pass uploads its uniforms, Better Simple Clouds supplies values for:

- sky tint;
- overall brightness/exposure;
- saturation;
- night grading;
- moon glow;
- fog resistance;
- far-edge fading;
- optional soft terrain intersection.

It supplies matching values to the transparent pass as well.

Keeping the opaque body and transparent fringe synchronized is important. The transparent fringe wraps the opaque
body, and its alpha comes from the same cloud-noise field. Applying a color treatment to only one pass makes the
difference appear as noise-pattern mottling over cloud faces.

### 3. It does not depend on Iris internals

The Better Simple Clouds source contains no substantial Iris pipeline patch that rewrites shaderpack programs.
Iris is only an optional dependency in `neoforge.mods.toml`.

The mod's own comments explicitly explain that Simple Clouds draws with its own shader and Iris does not replace that
custom instanced draw.

Therefore, "works with every shaderpack" means:

- cloud rendering stays within a shaderpack-independent Simple Clouds draw;
- the mod applies a generic grade that can be tuned to sit better in a shader-lit scene;
- the interior view limiter is real geometry that survives regardless of how the shaderpack handles fog.

It does **not** mean the clouds receive exact custom lighting, shadows, or atmospheric calculations from every
shaderpack.

## How the Cloud Interior Works

The interior effect is primarily a mesh-generation solution, not a fullscreen fog or composite trick.

### 1. Camera envelopment detection

Relevant file:

```text
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/CloudEnvelopment.java
```

Better Simple Clouds computes an `envelopment` value in the range `[0, 1]`.

Horizontal membership is calculated from the Simple Clouds region fade:

```text
presence = 1 - fade
```

The result is slightly sharpened while preserving a soft region edge.

Vertical membership uses the cloud type's actual noise range:

```java
bottom = cloudBase + noiseConfig.getStartHeight() * 8;
top = cloudBase + noiseConfig.getEndHeight() * 8;
```

The vertical band has configurable padding and a smooth transition.

The horizontal and vertical values are multiplied and eased over time. This prevents the effect from abruptly
appearing or disappearing when crossing a cloud boundary.

### 2. State passed to the GPU compute shader

Relevant files:

```text
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/InteriorFillState.java
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/mixin/CloudMeshGeneratorFillMixin.java
```

`InteriorFillState` exposes:

- `MicFillStrength`: smoothed envelopment;
- `MicViewDist`: interior view distance in cloud-cell units;
- `MicHazeOpacity`: opacity contributed by each interior haze cell;
- `MicShell`: whether to create the opaque view-distance shell;
- `MicSolidNearCells`: radius in which away-facing cloud faces should be kept.

`CloudMeshGeneratorFillMixin`:

1. redirects Simple Clouds' `cube_mesh` compute shader to the Better Simple Clouds copy;
2. injects at the tail of `CloudMeshGenerator.prepareMeshGen`;
3. uploads the `MicFill*` uniforms before mesh generation.

### 3. The compute shader fills genuinely hollow cells

Relevant file:

```text
better-simple-clouds-main/src/bsc/resources/assets/bettersimpleclouds/shaders/compute/cube_mesh.comp
```

For a cell where cloud noise is positive, the compute shader first creates the normal Simple Clouds surface geometry.

While the camera is enveloped, it then considers adding interior geometry.

It only fills a cell when:

```glsl
MicFillStrength > 0.0 && !micHasOpaqueFace(...)
```

`micHasOpaqueFace(...)` mirrors the normal surface-face checks. This prevents the fill from being emitted on a cell
that already has visible opaque cloud geometry.

That check is important because placing the haze cube or shell at exactly the same position as an existing surface
would create coincident geometry, depth disagreement, shimmering, and patterned faces.

### 4. Translucent interior haze

For hollow solid cells inside `MicViewDist`, the shader emits transparent cubes with:

```glsl
alpha = MicFillStrength * MicHazeOpacity;
```

The opacity is constant per cell. Looking through more cells accumulates opacity, creating an approximate exponential
fog falloff.

This haze is rendered by Simple Clouds' existing weighted-blended transparency pass.

### 5. Opaque view-distance shell

At the configured view distance, the shader emits an opaque layer for approximately 2.5 cloud cells.

The shell:

- prevents the player from seeing through a very large cloud;
- works even when an Iris shaderpack ignores Forge or vanilla fog events;
- uses real opaque geometry;
- emits only camera-facing faces to avoid coincident internal shell faces and z-fighting.

This opaque shell is the main reason the view limiter remains reliable under arbitrary shaderpacks.

### 6. Solid nearby cloud faces

Relevant file:

```text
better-simple-clouds-main/src/bsc/java/dev/bettersimpleclouds/immersion/mixin/SimpleCloudsRendererSolidFacesMixin.java
```

Simple Clouds normally skips cube faces whose normals point away from the camera. From inside a cloud, the surrounding
outer surface commonly points away from the camera, causing the cloud to look hollow or see-through.

The mixin forces `setTestFacesFacingAway(true)` after Simple Clouds applies its normal client option, but only while
the camera is enveloped.

The compute shader further restricts the extra faces to a nearby radius. This prevents doubled geometry and giant
low-LOD cloud cubes across the entire render distance.

## Portability to Forge 1.20.1

Better Simple Clouds targets:

```text
Minecraft 1.21.1
NeoForge 21.1
Java 21
```

The current Oculus for Simple Clouds project targets:

```text
Minecraft 1.20.1
Forge 47
Java 17
Simple Clouds 0.7.3
```

The important Simple Clouds 0.7.3 hooks already exist in the local decompiled 1.20.1 source:

- `CloudMeshGenerator.createShader(...)`;
- `CloudMeshGenerator.prepareMeshGen(...)`;
- the protected `CloudMeshGenerator.shader` field;
- `ComputeShader.forUniform(...)`;
- `SimpleCloudsRenderer.prepareMeshGenerator(...)`;
- `CloudMeshGenerator.setTestFacesFacingAway(...)`;
- `SimpleCloudsRenderer.renderCloudsOpaque(...)`;
- `SimpleCloudsRenderer.renderCloudsTransparency(...)`;
- `SimpleCloudsShaders.registerShaders(...)`.

The method descriptors used by the Better Simple Clouds mixins are substantially compatible with the 1.20.1
classes.

Expected mechanical port changes include:

- `net.neoforged.*` imports to `net.minecraftforge.*`;
- NeoForge client tick event types to the Forge 1.20.1 equivalents;
- NeoForge mod registration/config APIs to Forge equivalents;
- `ResourceLocation.fromNamespaceAndPath(...)` to the 1.20.1 constructor or equivalent helper;
- Java 21 syntax/API avoidance where necessary.

The rendering model itself does not prevent the port.

## Differences From the Current Interior Implementation

### Current detector uses the wrong vertical measurement

Relevant file:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/interiorfog/InteriorCloudState.java
```

The current detector rejects a cloud when:

```java
type.stormStart() <= 0.0F
```

and calculates the cloud ceiling as:

```java
cloudTop = cloudBottom + type.stormStart() * CLOUD_SCALE;
```

`stormStart` is not the cloud's full vertical geometry extent. This can:

- reject calm/non-storm cloud types;
- report an incorrect cloud ceiling;
- activate or deactivate the interior effect at the wrong height.

Better Simple Clouds uses:

```java
type.noiseConfig().getStartHeight()
type.noiseConfig().getEndHeight()
```

which matches the actual mesh's vertical noise range.

### Current opaque-inside mixin does not generate an interior

Relevant file:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/mixin/SimpleCloudsOpaqueInsideMixin.java
```

The current mixin intercepts the transparent pass while the camera is inside a cloud and renders the already-existing
transparent geometry using opaque/source-replace blending and depth writes.

This changes how existing fringe cubes are drawn, but it does not:

- generate missing internal cubes;
- create a view-distance shell;
- fill genuinely hollow cells;
- ensure all required away-facing surface faces exist.

Better Simple Clouds solves the problem earlier, during GPU mesh generation.

### Current fog path may be ignored by shaderpacks

Relevant files:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/interiorfog/InteriorCloudClientEvents.java
src/main/java/net/Gabou/oculus_for_simpleclouds/interiorfog/InteriorCloudShaderFog.java
```

Forge viewport fog events can modify the vanilla fog path, but an Oculus/Iris shaderpack may implement its own fog and
ignore those values.

`InteriorCloudShaderFog` feeds fog uniforms to the Simple Clouds shader itself, but that only changes cloud fragments.
It does not necessarily fill or limit the view of the shaderpack-rendered world.

Better Simple Clouds treats fog as optional polish and uses the opaque geometry shell as the shaderpack-independent
view limiter.

## Conflicts With the Current Oculus/DH Pipeline

### 1. Transparent clouds are disabled by default

Relevant files:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareDhPipeline.java
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareNoDhPipeline.java
```

Both custom shader-aware pipelines only render Simple Clouds' transparency target when:

```text
-Dofsc.enableTransparentCloudsWithShaders=true
```

The default is false.

This was introduced because the transparent cloud path correlated with leaked framebuffer/depth state and eventual
terrain/water corruption.

Consequences for the Better Simple Clouds interior:

- the opaque shell can render through the opaque cloud pass;
- solid nearby cloud faces can render;
- the translucent haze cubes will be generated but will not be drawn while the transparent pass is disabled.

The transparent pass must not simply be re-enabled without retesting the previously observed terrain/water corruption.

### 2. `SimpleCloudsOpaqueInsideMixin` conflicts with the haze

The current `SimpleCloudsOpaqueInsideMixin` replaces the normal transparent render behavior while inside a cloud.

If the Better Simple Clouds haze cubes are ported, this mixin would also affect those cubes. It would turn the intended
weighted translucent haze into an opaque/source-replace pass with depth writes.

The current opaque-inside replacement should therefore be removed or disabled when the compute-generated interior is
introduced.

### 3. Better Simple Clouds' soft terrain fade should not be ported blindly

`CloudSoftFade` assumes the native Simple Clouds `SHADER_SUPPORT` pipeline can provide a valid main framebuffer depth
texture before clouds draw.

The existing investigation in `simpleclouds_dh_clouds_missing_notes.md` proved that in the current Oculus + Distant
Horizons setup:

- multiple normal scene-depth sources were blank or unavailable;
- the cloud target's own depth was valid;
- the final working occlusion fix was a conservative scene-depth-present mask in the custom final composite;
- raw cloud-vs-scene depth comparisons were unreliable.

The Better Simple Clouds soft-intersection fade uses exactly the kind of scene-depth reconstruction that caused
problems in this project. It should remain disabled during the initial interior port.

### 4. Fragment shader replacement must be merged with existing changes

This project already supplies Simple Clouds shader resources under:

```text
src/main/resources/assets/simpleclouds/shaders/
```

Those shaders include current Oculus/Simple Clouds lighting and compatibility work.

Copying the Better Simple Clouds fragment shaders wholesale would overwrite or bypass existing functionality.
If its scene grade is desired, the relevant uniforms and shader blocks must be merged into the existing shaders and
fed alongside the existing `SimpleCloudsUniforms` and sun-lighting paths.

The compute-interior feature can be ported independently of the fragment shader appearance changes.

## Recommended Port Strategy

### Stage 1: Correct detection

Port the `CloudEnvelopment` logic to Forge 1.20.1:

- use region fade for horizontal membership;
- use `noiseConfig().getStartHeight()/getEndHeight()` for vertical membership;
- remove the `stormStart() <= 0` requirement;
- smooth the final envelopment value over time.

This can replace or correct the current `InteriorCloudState`.

### Stage 2: Add opaque geometry only

Port the compute-shader additions needed for:

- `MicFillStrength`;
- `MicViewDist`;
- `MicShell`;
- `MicSolidNearCells`;
- `micHasOpaqueFace(...)`;
- `createSolidCube(...)`;
- the opaque view-distance shell.

Also port the nearby solid-face behavior.

Initially leave `MicHazeOpacity` at zero or do not emit haze cubes. This allows the interior shell and solid surfaces to
be tested without enabling the known-problematic transparent pass.

### Stage 3: Remove conflicting current behavior

Disable or replace:

```text
SimpleCloudsOpaqueInsideMixin
```

The compute-generated geometry should be rendered normally through the existing opaque pass. It should not rely on
re-rendering transparent fringe cubes as opaque.

### Stage 4: Keep the proven final composite

Do not change the current working cloud visibility and terrain-occlusion strategy during the initial port:

- keep `ShaderAwareDhPipeline`/`ShaderAwareNoDhPipeline`;
- keep the custom once-per-frame final composite;
- keep the scene-depth-present mask;
- do not reintroduce the previously failed raw depth comparison.

The interior work should be isolated to cloud detection and mesh generation first.

### Stage 5: Repair and enable transparent haze separately

After the opaque shell is stable:

1. audit all framebuffer, blend, depth-mask, depth-function, draw-buffer, and viewport state around the transparent
   cloud pass;
2. ensure the exact previous state and framebuffer attachments are restored;
3. test long sessions, water, terrain, cumulus regions, F3+T, DH on/off, and shader reloads;
4. only then enable the transparent pass and add the Better Simple Clouds interior haze cubes.

This should be treated as a separate fix attempt and recorded in `simpleclouds_dh_clouds_missing_notes.md`.

### Stage 6: Optionally merge appearance grading

The Better Simple Clouds scene grade can be considered after the geometry is stable.

If ported:

- merge it into the existing cloud fragment shaders instead of replacing them wholesale;
- feed identical grading values to opaque and transparent passes;
- preserve the existing Oculus/Simple Clouds sun, weather, and composite behavior;
- treat shader matching as generic grading rather than true per-shaderpack integration.

## Suggested Minimum Initial Port

The safest useful first implementation is:

```text
correct envelopment detection
    +
near-camera away-facing surfaces
    +
opaque compute-generated view-distance shell
    +
existing working Oculus/DH composite
```

Defer:

```text
transparent interior haze
soft terrain intersection depth sampling
wholesale Better Simple Clouds fragment shader replacement
```

This captures the shaderpack-independent part of Better Simple Clouds' design while avoiding the two systems already
known to be unstable in the current project: the transparent cloud pass and reconstructed scene-depth comparison.

## Licensing Note

The added Better Simple Clouds project declares the MIT license. If code or shader sections are ported, preserve the
applicable copyright and MIT attribution/license notice.

