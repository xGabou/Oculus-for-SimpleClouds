# Codex Report: Fix Simple Clouds terrain clipping with Distant Horizons and shaders

## Context

Repository to modify first:

```text
xGabou/Oculus-for-SimpleClouds
```

Related shaderpack repository for fallback test:

```text
xGabou/Shaders-for-simpleClouds
```

## Problem

When **Distant Horizons** and **shaders** are enabled, Simple Clouds behave inconsistently with terrain depth.

Observed behavior:

```text
Very close to the player: clouds respect terrain
Middle range, roughly 5 blocks to vanilla render distance: clouds clip through terrain
Far DH distance: clouds respect terrain again
```

This does **not** look like a simple reverse depth issue. Do not focus on `detectReverseDepth()` first.

The distance pattern suggests the issue is more likely caused by one of these:

```text
1. The final cloud composite selects the wrong scene depth texture
2. DH depth merge overwrites or damages vanilla terrain depth inside the cloud target
3. The shaderpack uses DH terrain position as the cloud ray direction
```

The first two should be fixed in `Oculus-for-SimpleClouds`. The third is a fallback check in `Shaders-for-simpleClouds`.

---

# Required investigation order

## Test 1: Force final composite to use cloud target depth

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

Find:

```java
private static int selectPrimarySceneDepthTex(int originalDepthTex, int cloudDepthTex) {
    if (combinedValidThisFrame && combinedSceneDepthTex > 0) {
        return combinedSceneDepthTex;
    }
    if (externalSceneDepthTex > 0) {
        return externalSceneDepthTex;
    }
    if (originalDepthTex > 0) {
        return originalDepthTex;
    }
    if (capturedSceneDepthTex > 0) {
        return capturedSceneDepthTex;
    }
    return cloudDepthTex;
}
```

Temporarily replace it with:

```java
private static int selectPrimarySceneDepthTex(int originalDepthTex, int cloudDepthTex) {
    return cloudDepthTex;
}
```

### Expected result

If middle range clipping improves, then `FinalCloudCompositeHandler` is selecting the wrong depth source during the custom final composite.

### Permanent fix direction

Replace the current selection priority with a safer shader plus DH aware selection.

Suggested permanent logic:

```java
private static int selectPrimarySceneDepthTex(int originalDepthTex, int cloudDepthTex) {
    if (combinedValidThisFrame && combinedSceneDepthTex > 0) {
        return combinedSceneDepthTex;
    }

    if (capturedThisFrame && capturedSceneDepthTex > 0) {
        return capturedSceneDepthTex;
    }

    if (cloudDepthTex > 0) {
        return cloudDepthTex;
    }

    if (originalDepthTex > 0) {
        return originalDepthTex;
    }

    if (externalSceneDepthTex > 0) {
        return externalSceneDepthTex;
    }

    return -1;
}
```

Also update:

```java
private static String selectPrimarySceneDepthSource(int originalDepthTex, int cloudDepthTex)
```

to match the same priority order.

### Important

Do not prefer `externalSceneDepthTex` over `cloudDepthTex` unless it is proven to contain the correct current frame terrain depth. The clipping pattern suggests `externalSceneDepthTex` or `originalDepthTex` may be incomplete or stale for the middle distance.

---

## Test 2: Prevent DH empty depth from overwriting cloud target depth

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareDhPipeline.java
```

Find this method:

```java
private static boolean ensureDepthMergeProgram()
```

Inside it, find this fragment shader string:

```java
String fragmentSrc = "#version 150\nin vec2 vUv;uniform sampler2D uDepth;void main(){float d=texture(uDepth,vUv).r;gl_FragDepth=d;}";
```

Replace it with:

```java
String fragmentSrc =
        "#version 150\n" +
        "in vec2 vUv;" +
        "uniform sampler2D uDepth;" +
        "void main(){" +
        "float d=texture(uDepth,vUv).r;" +
        "if (d >= 0.999999) discard;" +
        "gl_FragDepth=d;" +
        "}";
```

### Why

The current merge shader blindly writes DH depth into the cloud target:

```java
gl_FragDepth = d;
```

If DH returns sky or invalid depth for pixels that still have vanilla terrain, the merge pass can damage the cloud target depth. That would explain why far DH terrain works, near terrain works, but middle vanilla range clips.

### Expected result

If middle range clipping improves, the DH merge pass was overwriting valid vanilla terrain depth with invalid DH depth.

### Permanent fix direction

Make DH depth merging reject invalid DH depth before writing to the cloud framebuffer.

A minimal safe version for normal depth is:

```glsl
float d = texture(uDepth, vUv).r;
if (d >= 0.999999) discard;
gl_FragDepth = d;
```

If needed later, add a helper for reverse depth, but do not modify reverse depth detection unless testing proves it is needed.

---

## Test 3: Disable DH merge to confirm whether merge damages vanilla depth

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareDhPipeline.java
```

Find this section:

```java
DepthSource dhDepthSource = FinalCloudCompositeHandler.captureDhDepthSource(dhFbo, vanillaW > 0 ? vanillaW : cloudTarget.width, vanillaH > 0 ? vanillaH : cloudTarget.height);
int dhDepthTex = dhDepthSource.isValid() ? dhDepthSource.getDepthTexture() : -1;
boolean mergedDh = mergeDhDepthIntoCloudDepth(cloudTarget, dhDepthSource);
```

Temporarily replace:

```java
boolean mergedDh = mergeDhDepthIntoCloudDepth(cloudTarget, dhDepthSource);
```

with:

```java
boolean mergedDh = false;
```

### Expected result

If middle range clipping improves when DH merge is disabled, then the merge is corrupting the cloud target depth.

If far terrain starts failing but middle terrain works, that confirms:

```text
vanilla depth copy is good
DH depth merge is the damaging step
```

### Permanent fix direction

Keep DH merge enabled, but only write valid DH terrain depth into cloud target depth. See Test 2.

---

## Test 4: Verify that the cloud target receives vanilla depth before DH merge

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareDhPipeline.java
```

Find:

```java
DepthSource vanillaDepthSource = FinalCloudCompositeHandler.captureVanillaDepthSource(mainTarget);
boolean copiedVanillaDepth = FinalCloudCompositeHandler.copyDepthToTarget(cloudTarget, vanillaDepthSource);
```

Add a debug log behind the existing debug system after the copy:

```java
debug(String.format(
        "Vanilla depth copy: copied=%s source=%s sourceTex=%d sourceSize=%dx%d cloudDepth=%d cloudSize=%dx%d",
        copiedVanillaDepth,
        vanillaDepthSource == null ? "null" : vanillaDepthSource.getSourceName(),
        vanillaDepthSource == null ? -1 : vanillaDepthSource.getDepthTexture(),
        vanillaDepthSource == null ? -1 : vanillaDepthSource.getWidth(),
        vanillaDepthSource == null ? -1 : vanillaDepthSource.getHeight(),
        cloudTarget.getDepthTextureId(),
        cloudTarget.width,
        cloudTarget.height
));
```

### Expected result

This should confirm whether the vanilla depth copy is actually succeeding when shaders and DH are active.

If `copiedVanillaDepth` is false, fix the copy path before touching shaderpack code.

---

# Fallback shaderpack check

Only do this after the OFSC depth/composite checks above.

Repository:

```text
xGabou/Shaders-for-simpleClouds
```

File:

```text
shaders/program/deferred1.glsl
```

Find this block:

```glsl
#ifdef DISTANT_HORIZONS
    float cloudDhDepth = texelFetch(dhDepthTex, texelCoord, 0).r;
    if (cloudDhDepth < 1.0) {
        vec4 cloudScreenPosDH = vec4(texCoord, cloudDhDepth, 1.0);
        vec4 cloudViewPosDH = dhProjectionInverse * (cloudScreenPosDH * 2.0 - 1.0);
        cloudViewPosDH /= cloudViewPosDH.w;

        float cloudDhDistance = length(cloudViewPosDH.xyz);
        if (z0 >= 1.0 || cloudDhDistance < cloudSceneDistance) {
            cloudSceneDistance = cloudDhDistance;
            cloudScenePlayerPos = ViewToPlayer(cloudViewPosDH.xyz);
        }
    }
#endif
```

Modify it to:

```glsl
#ifdef DISTANT_HORIZONS
    float cloudDhDepth = texelFetch(dhDepthTex, texelCoord, 0).r;
    if (cloudDhDepth < 1.0) {
        vec4 cloudScreenPosDH = vec4(texCoord, cloudDhDepth, 1.0);
        vec4 cloudViewPosDH = dhProjectionInverse * (cloudScreenPosDH * 2.0 - 1.0);
        cloudViewPosDH /= cloudViewPosDH.w;

        float cloudDhDistance = length(cloudViewPosDH.xyz);
        if (z0 >= 1.0 || cloudDhDistance < cloudSceneDistance) {
            cloudSceneDistance = cloudDhDistance;
        }
    }
#endif
```

### Why

`cloudScenePlayerPos` is later used as the cloud ray direction. DH terrain depth should only provide an occlusion distance. It should not replace the original pixel ray direction.

---

# Final expected fix

The most likely permanent fix is in `Oculus-for-SimpleClouds`:

```text
1. Make final composite select the correct current frame depth
2. Prevent DH empty depth from overwriting vanilla terrain depth
3. Keep debug logs behind existing debug flags
```

Do not refactor unrelated rendering code.

Do not change cloud lighting.

Do not change cloud mesh generation.

Do not change reverse depth detection unless a test directly proves it is involved.

---

# Validation checklist

Test all of these:

```text
Shaders off, DH off
Shaders on, DH off
Shaders off, DH on
Shaders on, DH on
```

For the final case, verify terrain occlusion at:

```text
0 to 5 blocks
5 blocks to vanilla render distance
beyond vanilla render distance in DH terrain
```

Pass condition:

```text
Clouds must not render through terrain at any of the three ranges.
```

Also verify:

```text
Transparent clouds still render
Opaque clouds still render
Lightning still renders
Storm fog still renders
No excessive flickering during camera movement
No broken cloud color or missing cloud framebuffer
```
