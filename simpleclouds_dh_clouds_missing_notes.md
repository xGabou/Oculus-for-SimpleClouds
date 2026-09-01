# SimpleClouds + Oculus + Distant Horizons Notes

## NeoForge Port: Final DH Depth, Weather, Reload, And Release Log Cleanup

New evidence:

- The `1.21.1` NeoForge branch already had an initial DH/Iris integration and already used Iris dependency `implementation "curse.maven:irisshaders-455508:6661598"`.
- The NeoForge branch was still missing several final Forge-side fixes:
  - local SimpleClouds weather effects could still collapse when SimpleClouds internally reported vanilla weather;
  - the composite depth-only FBO still used color draw/read buffer state in some paths;
  - the composite shader still used older scene-depth logic;
  - DH/no-DH pipelines still mutated the main color attachment instead of temporarily swapping and restoring the depth attachment;
  - `glCopyTexSubImage2D` fallback paths still left `GL_READ_BUFFER` as `GL_NONE`;
  - temporary pipeline trace logging was still present;
  - F3+T/resource reload did not reset composite GL resources.

Patch:

- Ported the regional SimpleClouds weather calculation into `SimpleCloudsUniforms` and `SimpleCloudsIrisWeatherCompat`.
- Added `SimpleCloudsWorldEffectsWeatherMixin` with the NeoForge/1.21.1 `WorldEffects.renderPost(Matrix4f, ...)` signature.
- Fixed `FinalCloudCompositeHandler` depth capture FBO setup to use `GL_NONE` draw/read buffers and check framebuffer completeness.
- Updated the final composite shader to sample scene depth, discard hidden cloud pixels, and write adjusted cloud depth.
- Switched scene-depth selection to prefer current captured/combined/original scene depth before falling back to cloud depth.
- Restored the previous framebuffer depth attachment after lightning/debug passes in both DH and no-DH pipelines.
- Restored the source framebuffer `GL_READ_BUFFER` after depth-copy fallbacks in both DH and no-DH pipelines.
- Removed the `SC_PipelineTracer` debug print.
- Added a NeoForge `RegisterClientReloadListenersEvent` handler that resets composite GL resources after resource reload.

Why:

- These are the final stabilized Forge-side fixes ported to the NeoForge/Iris branch, adjusted for 1.21.1 API differences.
- The changes target the known cloud visibility, terrain translucency, weather darkening/rain/lightning, F3+T, and release log-spam issues without reintroducing the temporary diagnostics.

Expected behavior:

- Clouds should composite with scene depth under Iris and DH instead of clipping through nearby blocks/trees.
- Terrain should not become translucent from framebuffer attachment leaks.
- SimpleClouds rain, thunder, and darkening should continue visually even if SimpleClouds internally flips to vanilla weather mode.
- F3+T should recreate the composite GL resources instead of leaving clouds hidden until world restart.
- Release logs should no longer contain the temporary `[OFSC TRACE] getRenderPipeline` spam.

Observed/result:

- NeoForge build succeeded on 2026-06-02 with `.\gradlew.bat jar`.
- Output jar: `G:\mods\Oculus-for-SImpleClouds-neoforge\build\libs\oculus_for_simpleclouds-0.0.2.jar`.

## NeoForge Crash Guard: Empty Lightning Buffer In DH And No-DH Paths

New evidence:

- The client can crash in both the DH and no-DH pipelines with `java.lang.IllegalStateException: BufferBuilder was empty`.
- The stack traces point to `ShaderAwareDhPipeline.renderLightning(...)` and `ShaderAwareNoDhPipeline.renderLightning(...)`.
- The crash happens even when `hasLightningToRender()` is true, which means the builder can still end up with zero submitted vertices after the per-bolt distance/fade filtering.

Patch:

- Replaced the unconditional `buildOrThrow()` call in both lightning render paths with `build()`.
- Guarded the draw call so `BufferUploader.drawWithShader(...)` only runs when `build()` returns a non-null mesh.

Why:

- `BufferBuilder.buildOrThrow()` is the direct source of the crash when lightning state exists but no quads are emitted.
- Using the non-throwing build path preserves normal lightning rendering while making the empty case a no-op instead of a fatal exception.

Expected behavior:

- DH and non-DH rendering should no longer crash when the lightning buffer is empty.
- If no lightning geometry is actually produced for a frame, the renderer should skip the draw cleanly.

## NeoForge 1.21.1 Port: Exact GL State Restoration And Safe Cloud Lighting

Date: 2026-09-01

New evidence:

- The Forge 1.20.1 fix in commit `23a5d4a` identified raw OpenGL state leaks in the final cloud composite, scene-depth copies, DH depth merges, and lightning rendering.
- Those same passes and partial restoration patterns were present on the NeoForge 1.21.1 branch, so later translucent/weather rendering could inherit OFSC texture, framebuffer, blend, depth, cull, or mask state.
- SimpleClouds 1.21.1 uses a simpler stock `clouds.vsh`/`clouds.fsh` interface than the customized 1.20.1 branch. Copying the complete 1.20.1 shaders would introduce uniforms and varyings that are not part of the 1.21.1 contract.

Patch:

- Ported `GlStateSnapshot` and converted the 1.21.1 final composite, depth-copy, DH depth-merge/combine, and related setup passes to restore exact incoming OpenGL state and all texture units they use.
- Removed the unnecessary final-composite main-depth attachment replacement and restricted the lightning depth swap to frames that actually contain lightning.
- Preserved the 1.21.1 `Tesselator.begin(...)`/nullable `MeshData` API and its existing empty-lightning guard while adding exact lightning blend/depth/fog restoration.
- Removed the client launcher enforcement classes and calls, matching the source commit's behavior, and bumped the NeoForge mod version to `1.1.3`.
- Ported only the defensive lighting changes onto the actual SimpleClouds 1.21.1 stock shader interface: finite normalization, clamped face indices/brightness, a small darkness floor, and non-finite color fallback.

Why:

- Exact state restoration prevents the cloud pass from corrupting water, rain, and other later translucent rendering under Iris/NeoForge.
- Basing the shader override on the 1.21.1 dependency preserves its expected uniforms and vertex/fragment interface while addressing the black cloud-face artifact.

Expected behavior:

- Water and weather passes should receive the texture/depth/blend state that was active before OFSC rendering.
- Sparse opaque black cloud faces should remain visibly dark instead of becoming black or non-finite.
- Frames without lightning should not replace the main framebuffer's depth attachment.

Observed/result:

- `gradlew.bat compileJava` succeeded on NeoForge 1.21.1 on 2026-09-01.
- Only the existing `BindingManagerBindingZeroMixin` metadata warnings were reported.
- In-game cloud, water, and rain behavior remains to be validated in the affected shader-pack/world setup.
