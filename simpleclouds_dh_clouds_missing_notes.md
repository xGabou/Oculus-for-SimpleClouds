# SimpleClouds + Oculus + DH Missing Clouds Notes

Date: 2026-05-26

## Confirmed Working Fix

Status: clouds render again with Distant Horizons enabled.

The fix that made clouds show up was forcing the shader-aware pipeline when shaders are active:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/mixin/SimpleCloudsForcePipelineMixin.java
src/main/java/net/Gabou/oculus_for_simpleclouds/mixin/SimpleCloudsPipelineSelectMixin.java
```

Both paths must select `ShaderAwareDhPipeline.INSTANCE` when DH is loaded, not only `ShaderAwareNoDhPipeline.INSTANCE` when DH is absent.

Core rule:

```java
if (CompatHelper.areShadersRunning()) {
    return SimpleCloudsMod.dhLoaded()
            ? ShaderAwareDhPipeline.INSTANCE
            : ShaderAwareNoDhPipeline.INSTANCE;
}
```

Why this fixed it:

- The cloud regions and mesh were already valid.
- The missing part was that the OFSC DH-aware render path was not actually being used.
- Once `getRenderPipeline()` and the render-before-level tail assignment both select `ShaderAwareDhPipeline`, the clouds render with DH.

## Follow-up Visual Breakage

Observed after clouds started rendering:

- Clouds beyond vanilla render distance could appear over/through nearby trees.
- After staying in-game for a while, the terrain could look semi-transparent or washed by a translucent cloud layer.

Most likely cause:

- The shader-aware pipelines already call `renderer.doFinalCompositePass(...)`.
- `FinalCloudCompositeHandler` was also compositing the cloud target again at Forge `Stage.AFTER_LEVEL`.
- That means the cloud target could be applied twice: once by SimpleClouds' own final composite and once by OFSC's fullscreen composite.

Patch:

- Added `FinalCloudCompositeHandler.markSimpleCloudsCompositeThisFrame()`.
- `ShaderAwareDhPipeline` and `ShaderAwareNoDhPipeline` mark the frame immediately after `renderer.doFinalCompositePass(...)`.
- `FinalCloudCompositeHandler.onRenderStage(...)` now skips its extra fullscreen composite when SimpleClouds already composited this frame.

Files:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareDhPipeline.java
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareNoDhPipeline.java
```

Result after testing:

- Terrain transparency/wash issue stopped.
- Clouds disappeared again.

Interpretation:

- SimpleClouds' own `renderer.doFinalCompositePass(...)` is not sufficient/visible in this Oculus + DH setup.
- The OFSC `FinalCloudCompositeHandler` fullscreen composite is the pass that made clouds visible.
- The correct direction is not "skip OFSC composite when SimpleClouds composited"; it is "avoid SimpleClouds final composite in the shader-aware pipelines and let OFSC composite exactly once."

Follow-up patch:

- Removed `renderer.doFinalCompositePass(...)` from `ShaderAwareDhPipeline` and `ShaderAwareNoDhPipeline`.
- Removed the `markSimpleCloudsCompositeThisFrame()` gating path from `FinalCloudCompositeHandler`.
- `FinalCloudCompositeHandler` now composites once at `Stage.AFTER_LEVEL`.

Expected result:

- Clouds should become visible again because the OFSC composite runs.
- Terrain should stay non-transparent because the cloud target is no longer composited twice.

Result after testing:

- Clouds are visible again.
- Terrain still breaks after a long time.
- Clouds still clip through trees inside vanilla render distance.
- User observation: tree occlusion behaves normally only when the tree is outside vanilla render distance.

Interpretation:

- This points away from empty cloud meshes or missing DH callbacks.
- The vanilla-distance scene depth used by the OFSC composite is likely unstable or stale.
- `captureVanillaDepthSource(...)` was using the main scene depth texture directly when the depth attachment was already a texture (`source=external/direct`). That is a live reference, not a snapshot. Oculus/shader passes can mutate or replace its contents before the later `Stage.AFTER_LEVEL` composite.

Follow-up patch:

- For vanilla scene depth texture attachments, copy/blit the depth attachment into `capturedSceneDepthTex` immediately instead of keeping a live reference.
- Prefer `capturedSceneDepthTex` during the final OFSC composite when it was captured this frame.
- Preserve and restore the viewport in `FinalCloudCompositeHandler.compositeClouds()`.
- Preserve and restore the previous bound `GL_TEXTURE_2D` in `ensureSceneDepthTarget()`.

Files:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

Expected result:

- Trees inside vanilla render distance should occlude clouds because the composite uses a stable pre-cloud scene-depth snapshot.
- Long-session terrain corruption should be less likely because the composite restores viewport and no longer relies on a live depth texture that later passes may mutate.

## Depth Color Diagnostic

User requested a color-based depth test to identify which side is breaking.

Patch:

- Added `ofsc.debug.depthColorMode` / `ofsc.debug.depthColors` support to the Java-built OFSC composite shader in `FinalCloudCompositeHandler`.
- This does not edit external shaderpack files.
- In debug color mode the composite uses `GL_ALWAYS` so colors are visible even for pixels that would normally fail depth testing.

Enable with JVM args:

```text
-Dofsc.debug.depthColorMode=1
```

Color meaning for mode `1`:

- Green: cloud depth is closer than scene depth, so cloud should render.
- Red: scene depth is closer than cloud depth, so cloud should be hidden by terrain/tree.
- Cyan/blue: scene depth reads as sky/empty, so the scene depth source is missing that occluder.
- Yellow: cloud depth is invalid/clear.
- Magenta: cloud and scene depths are very close, likely edge/bias territory.

Extra modes:

```text
-Dofsc.debug.depthColorMode=2
```

Shows scene depth as grayscale where cloud pixels exist.

```text
-Dofsc.debug.depthColorMode=3
```

Shows cloud depth as grayscale where cloud pixels exist.

How to interpret the current tree clipping bug:

- If clipping trees show cyan/blue, the selected scene-depth texture does not contain the tree depth.
- If clipping trees show red, scene depth is present and the issue is in normal discard/depth-test state.
- If clipping trees show green, the cloud depth is being considered closer than the tree, so the cloud depth copy/projection is suspect.

Follow-up:

- User wanted the color diagnostic enabled by default and will remove it later.
- Changed `DEBUG_DEPTH_COLOR_MODE` default from `0` to `1`.
- It can still be disabled with:

```text
-Dofsc.debug.depthColorMode=0
```

Result:

- User saw all clouds as cyan everywhere.
- This means the selected scene-depth sample is sky/empty for every cloud pixel.

Interpretation:

- The failure is now narrowed to scene-depth acquisition/selection, not cloud mesh or cloud depth.
- Need to identify which scene-depth source is blank: combined, captured, external, original, or cloud fallback.

Follow-up diagnostic patch:

- Debug color mode now tints sky/empty scene-depth pixels by selected source:
  - Blue: `combined` depth is selected and blank.
  - Cyan: `captured` depth is selected and blank.
  - Purple: `external` depth is selected and blank.
  - White: `original` depth is selected and blank.
  - Orange: cloud-depth fallback is selected.
- While depth color mode is active, the composite no longer swaps the main FBO depth attachment. This reduces debug-mode side effects while we inspect color output.
- Scene-depth selection logs are enabled automatically when depth color mode is active.

Result:

- User reported the diagnostic color is blue.
- Blue means `combined` scene depth is selected and reads as sky/empty.

Interpretation:

- The combined DH+vanilla depth texture is currently invalid/blank for the composite.
- This explains cloud clipping: the composite is testing clouds against an empty scene depth.
- Since the captured vanilla snapshot is produced before the composite and should contain vanilla-distance trees/terrain, it should be tried before the combined texture.

Follow-up patch:

- Demoted `combinedSceneDepthTex` in `selectPrimarySceneDepthTex(...)`.
- New selection order:
  - captured-this-frame vanilla depth snapshot
  - external live scene depth
  - original main depth
  - combined depth
  - stale captured depth
  - cloud depth fallback

Expected result:

- Diagnostic should no longer be blue.
- If captured depth is valid, trees inside vanilla render distance should show red where they occlude clouds.
- If it becomes cyan, captured depth is also blank and the capture path is the next target.

Result:

- User reported the diagnostic color is cyan.
- Cyan means `capturedSceneDepthTex` is selected and blank.

Interpretation:

- Both the combined depth and the copied captured-depth snapshot are blank.
- The next diagnostic is to test the live scene depth attachment (`external`) and the main render target depth (`original`) before the captured snapshot.

Follow-up diagnostic patch:

- Preserve `externalSceneDepthTex` even when the snapshot copy succeeds.
- Changed scene-depth selection order again for diagnosis:
  - external live scene depth
  - original main render-target depth
  - captured-this-frame snapshot
  - combined depth
  - stale captured snapshot
  - cloud depth fallback

Expected result:

- Purple means the live external depth is selected and blank.
- White means the main render target original depth is selected and blank.
- Red/green/magenta would mean the live/original depth contains real geometry and the bug is narrower than capture.

Result:

- User reported the diagnostic color is white.
- White means the main render-target `original` depth is selected and blank.
- `external` was not selected, so the current capture path did not expose a separate external texture for this frame.

Interpretation:

- Combined, captured, and original depth have all tested blank.
- The remaining useful question is whether the cloud target depth contains anything useful after cloud rendering.

Follow-up diagnostic patch:

- Changed default depth debug mode from single-source mode `1` to band scanner mode `4`.
- Mode `4` displays five vertical source bands across cloud pixels:
  - left 20%: combined
  - 20-40%: captured
  - 40-60%: external
  - 60-80%: original
  - right 20%: cloud target depth
- Source colors still mean sky/blank for that source:
  - Blue: combined blank
  - Cyan: captured blank
  - Purple: external blank
  - White: original blank
  - Orange: cloud target depth blank
- Dim/dark source color means that source texture was unavailable.
- Red/green/magenta in any band means that source has non-sky depth at that pixel.

Expected result:

- If the rightmost cloud-depth band is orange/yellow, even the cloud target depth is blank/invalid.
- If the rightmost cloud-depth band is magenta or non-sky, cloud depth exists and the scene-depth acquisition path is the broken piece.

Result:

- User reported bands left to right:
  - Blue: combined blank
  - Cyan: captured blank
  - Dark purple: external unavailable
  - White: original blank
  - Pink/magenta: cloud target depth exists

Interpretation:

- Every scene-depth source is blank/unavailable at the time OFSC is rendering/compositing clouds.
- The cloud target depth itself is valid, but it only proves cloud depth exists; it does not prove vanilla terrain/tree depth was copied into the cloud target.
- This points at depth capture timing. By the time the shader-aware cloud path asks for main scene depth, Oculus/shader rendering has already cleared/replaced/invalidated the vanilla scene depth.

Follow-up patch:

- Added early scene-depth capture in `FinalCloudCompositeHandler.onRenderStage(...)` at:
  - `AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS`
  - `AFTER_CUTOUT_BLOCKS`
  - `AFTER_BLOCK_ENTITIES`
  - `AFTER_TRIPWIRE_BLOCKS`
- Added `FinalCloudCompositeHandler.getCapturedSceneDepthSource()`.
- `ShaderAwareDhPipeline` and `ShaderAwareNoDhPipeline` now use the early captured depth if it is available, only falling back to capturing during their later cloud pass if not.

Expected result:

- The captured band should stop being cyan if one of the earlier terrain stages still has valid depth.
- If captured becomes non-sky/red in front of trees, vanilla render-distance terrain/tree occlusion should start working again.

Result:

- User reported the same bands again:
  - Blue, cyan, purple, white, magenta.
- Early capture from `mc.getMainRenderTarget()` still did not find usable scene depth.

Interpretation:

- The earlier Forge render stages are not enough if we only read Minecraft's main render target.
- With Oculus/Iris shaders, terrain depth is likely in the currently bound shader framebuffer during those stages, not in `mc.getMainRenderTarget()`.
- Also, copied depth targets must not reuse a depth-stencil internal format as a depth-only texture.

Follow-up patch:

- Early stage capture now tries the currently bound draw framebuffer (`GL_DRAW_FRAMEBUFFER_BINDING`) first.
- If the active framebuffer capture fails, it falls back to `mc.getMainRenderTarget()`.
- `ensureSceneDepthTarget(...)` now always allocates `capturedSceneDepthTex` as `GL_DEPTH_COMPONENT32F`, avoiding invalid depth-stencil-to-depth texture allocation.

Expected result:

- If Oculus' active shader framebuffer contains real terrain depth, the captured band should stop being cyan.
- If it stays cyan and external stays purple, we need to locate Oculus/Iris' internal depth texture instead of relying on framebuffer attachments.

Result:

- User reported a regression:
  - no clouds
  - terrain became partially translucent, with liquids visible through terrain

Interpretation:

- The active draw framebuffer capture path is unsafe. Binding/querying/blitting the currently bound shader framebuffer during Forge render stages can leak or disturb Oculus/Iris framebuffer state.
- This matches the new terrain/liquid-through-terrain symptom.

Rollback:

- Removed the active `GL_DRAW_FRAMEBUFFER_BINDING` capture path.
- Early capture now only uses `mc.getMainRenderTarget()` again, restoring the previously working diagnostic behavior.
- Kept the band-scanner diagnostic and the `GL_DEPTH_COMPONENT32F` captured texture allocation.

Expected result:

- Clouds should return to the previous diagnostic state.
- The liquid-through-terrain regression from active framebuffer capture should be gone.

## Confirmed Facts

- Cloud sync is working.
  - Example log:
    - `[OFSC DEBUG] Cloud snapshot received: seed=-7068582401418971962 packetRegions=4 managerRegions=4 sync=true`
    - `[OFSC DEBUG] Cloud region update received: regions=4`
- Mesh generation is working.
  - Example log:
    - `[OFSC DEBUG] renderBeforeLevel: canRender=true shaders=true dhLoaded=true generateMesh=true renderClouds=true regions=2 meshStatus=(NORMAL,NORMAL) opaqueVerts=41245 transparentVerts=0`
- Therefore the missing cloud issue is not server-side cloud data and not mesh generation.
- The full `latest.log` did not show:
  - `DH beforeRender callback`
  - `DH afterRender callback`
  - `DH clouds render`
- `latest.log` also does not contain the `[OFSC ...]` `System.out` debug lines that appear in the console, so console output is currently more useful for OFSC diagnostics than the file log.
- That means the generated cloud mesh exists, but the DH render callback path is not invoking the cloud draw/composite pipeline.

## Current Suspect

The most concerning confirmed source issue was pipeline selection:

- `SC_PipelineTracer` redirected the constructor for `DetermineCloudRenderPipelineEvent`, but `SimpleCloudsRenderer.renderBeforeLevel` still writes its original local pipeline (`SHADER_SUPPORT`) into `renderPipelineThisPass`.
- Since the event's `overridenPipeline` remains null unless a listener changes it, the redirected constructor alone does not select `ShaderAwareDhPipeline`.
- The existing `SimpleCloudsForcePipelineMixin` only forced `ShaderAwareNoDhPipeline` when DH was not loaded, so the DH path kept using SimpleClouds' normal shader-support pipeline.
- Result: `renderBeforeLevel` diagnostics can show valid cloud meshes while the OFSC DH-aware render/composite path never runs.

Patch now forces `getRenderPipeline()` to return:

```java
SimpleCloudsMod.dhLoaded() ? ShaderAwareDhPipeline.INSTANCE : ShaderAwareNoDhPipeline.INSTANCE
```

when shaders are running, and the tail pipeline assignment now mirrors that choice.

Secondary suspect:

SimpleClouds registers DH callbacks through:

```java
DhApiEventRegister.on(...)
```

With the current DH/Oculus stack, that path appears not to fire.

Oculus/Iris' own DH integration uses the newer/current DH API style:

```java
DhApi.events.bind(...)
```

So this mod now adds a direct bridge that binds the SimpleClouds DH handlers through `DhApi.events.bind(...)`.

## Current Patch

Forced shader-aware pipeline selection in:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/mixin/SimpleCloudsForcePipelineMixin.java
src/main/java/net/Gabou/oculus_for_simpleclouds/mixin/SimpleCloudsPipelineSelectMixin.java
```

Added:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareDhEventBridge.java
```

It binds:

```java
DhApi.events.bind(DhApiBeforeRenderPassEvent.class, new SimpleCloudsDhSetupHandler());
DhApi.events.bind(DhApiBeforeApplyShaderRenderEvent.class, new SimpleCloudsBeforeDhRenderHandler());
DhApi.events.bind(DhApiAfterRenderEvent.class, new SimpleCloudsAfterDhRenderHandler());
```

Wired from:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/Oculus_for_simpleclouds.java
```

Follow-up:

- The first bridge attempt did not print its registration line during `FMLClientSetupEvent`.
- The bridge is now also called lazily from `SC_PipelineTracer.renderBeforeLevel`, because that render path is confirmed active by the mesh diagnostics.
- `ShaderAwareDhEventBridge.register()` now logs both successful registration and skipped/failed registration.

Compile status:

```text
./gradlew.bat compileJava
BUILD SUCCESSFUL
```

## Next Log Checks

After launch, check for:

```text
[OFSC TRACE] getRenderPipeline returns net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline
[OFSC DEBUG] Registered DH cloud render events through DhApi.events.bind
[OFSC WARN] DH render callbacks are not firing; using afterLevel fallback cloud render.
[OFSC DEBUG] DH beforeRender callback
[OFSC DEBUG] DH afterRender callback
[OFSC DEBUG] DH clouds render
```

Expected interpretation:

- Pipeline trace does not show `ShaderAwareDhPipeline`: force mixin is not applied or old classes are being launched.
- Pipeline trace shows `ShaderAwareDhPipeline` plus fallback warning: DH events are absent, but clouds should render through the after-level fallback.
- Registration line missing: bridge did not run or DH not detected by Forge `ModList`.
- Registration line present, callbacks missing: DH is not firing these events in the current render path.
- `DH beforeRender` present but `DH afterRender` missing: DH render pass starts but after-render event is not firing or is blocked.
- Both callbacks present but no clouds: inspect `ShaderAwareDhPipeline.afterDistantHorizonsRender(...)`, cloud target color/depth, and final composite.
- `DH clouds render` present with nonzero `opaqueVerts`: draw path runs; issue is likely framebuffer/composite/depth attachment.

## Other Known Noise

The shaderpack currently logs:

```text
Unknown variable: BIOME_PALE_GARDEN
```

This is a shaderpack/Oculus custom uniform issue. It should be fixed separately, but it does not explain the missing DH cloud callbacks.

## Ruled Out

- Empty server cloud regions: ruled out by `packetRegions` / `managerRegions`.
- Mesh generation producing zero vertices: ruled out by `meshStatus=(NORMAL,NORMAL)` and `opaqueVerts=41245`.
- Interior fog suppressing cloud mesh: suppression methods were forced to return false during testing, and mesh generation still reports valid vertices.

## Refresh Command Observation

User observation:

- Running `/simpleclouds clouds refresh` changes the bug.
- If there are no clouds above the area, or if the dense cloud layer disappears after refresh, the terrain/cloud clipping issue also disappears.
- Sometimes clouds are not visible at all after refresh.

Interpretation:

- This is new evidence that the issue is not only terrain depth/composite state.
- Cloud regeneration/sync state can affect whether the problematic cloud layer exists or is rendered.
- The existing `SimpleCloudsClientPacketHandlerMixin` is now suspicious because it forces `ClientCloudManager.receivedSync=false` after every non-snapshot `UpdateCloudManagerPacket`, undoing SimpleClouds' own `setReceivedSync()` call.
- That approach has not been attempted or ruled out in earlier notes.

## Attempt: Stop Forcing Client Cloud Sync False After Manager Updates

Reason:

- `/simpleclouds clouds refresh` can make the clipping disappear when dense clouds disappear, and can also make clouds vanish.
- Decompilation of SimpleClouds `SimpleCloudsClientPacketHandler.handleUpdateCloudManagerPacket(...)` shows vanilla SimpleClouds calls `ClientCloudManager.setReceivedSync()` for every manager update.
- Our mixin was immediately undoing that for every non-`SendCloudManagerPacket` by writing `receivedSync=false`.
- That can leave the client using local/client cloud config and `ClientCloudManager.isAvailableServerSide()` state after refresh/region updates instead of the synced server state.

Patch:

- Removed `oculus_for_simpleclouds$doNotEnableServerSyncForMovementPackets(...)` from `SimpleCloudsClientPacketHandlerMixin`.
- Kept snapshot logging.
- Extended region-update logging to include current manager region count and `receivedSync` state.

Files:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/mixin/SimpleCloudsClientPacketHandlerMixin.java
```

Expected result:

- After `/simpleclouds clouds refresh`, client cloud manager should stay synced instead of being forced back to unsynced by OFSC.
- If clouds disappear after refresh, logs should now show whether region packets arrived and whether `sync=true` remained true.

Compile status:

```text
./gradlew.bat compileJava
BUILD SUCCESSFUL
```

Observed in-game result:

- User clarified this was the wrong target.
- The relevant behavior is: `/simpleclouds clouds refresh` fixes the translucent terrain only because it removes/changes the dense cloud layer causing the issue.
- With this sync attempt, clouds did not show at all.

Rollback:

- Restored the previous `receivedSync=false` movement-packet guard in `SimpleCloudsClientPacketHandlerMixin`.
- Kept the expanded region-update logging because it is diagnostic-only.

New interpretation:

- Do not pursue cloud-manager sync as the primary fix unless new evidence appears.
- Focus on the cloud render target/composite path: terrain becomes translucent only while the problematic dense cloud layer is present, so a cloud color/alpha/depth state is likely being composited over terrain incorrectly.

## Attempt: Preserve Main Framebuffer Alpha During OFSC Composite

Reason:

- User clarified `/simpleclouds clouds refresh` fixes the translucent-terrain symptom because it removes/changes the dense cloud layer; the missing clouds after refresh are not the main issue.
- The OFSC fullscreen composite was using premultiplied RGB blending but also blending into the main framebuffer alpha channel:
  - RGB: `ONE, ONE_MINUS_SRC_ALPHA`
  - Alpha: `ONE, ONE_MINUS_SRC_ALPHA`
- A dense cloud layer can therefore modify the main color target alpha over large screen areas. Shaderpacks/post passes can interpret that alpha later, matching the observed terrain translucency/liquids visible through terrain.
- This alpha-preservation approach was not previously attempted in the notes.

Patch:

- Changed the OFSC composite blend state to preserve destination alpha while still premultiplying cloud RGB over the scene:
  - RGB: `GL_ONE, GL_ONE_MINUS_SRC_ALPHA`
  - Alpha: `GL_ZERO, GL_ONE`

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

Expected result:

- Dense clouds can still composite visually over terrain.
- The main framebuffer alpha is no longer reduced/rewritten by clouds, so later shaderpack/translucent passes should not make terrain look semi-transparent or let liquids show through solid terrain.

Observed in-game result:

- User reported new depth-band colors left to right: blue, gray/blue-ish, darker magenta, white, full bright magenta.
- Clouds are now visible as diagnostic-colored cloud silhouettes.
- This confirms the cloud target/composite path is alive after the Iris depth-source patch.
- Terrain issue remains, but user asked to focus on clouds first.

## Attempt: Composite Clouds Even When Scene Depth Capture Fails + Skip Storm Fog With Shaders

New evidence:

- User confirmed terrain translucency is directly linked to whether a server-side cloud region is above the player.
- Client-side clouds are still not visible, but server-side cloud presence can still trigger the broken translucent terrain/fog behavior.
- Current logs show cloud rendering is happening into the cloud target every frame:
  - `DH clouds render: cloudDepth=48 color=47 size=1024x768`
  - `opaqueVerts=184297 transparentVerts=131482`
- Current logs show `Scene depth select` only once, so the final OFSC composite is usually not running even though the cloud target is populated.

Reason:

- `FinalCloudCompositeHandler.compositeClouds()` required `capturedThisFrame=true`. If terrain depth capture fails or gets reset later, clouds are rendered into the offscreen target but skipped during final composite, making them invisible.
- The translucent-terrain effect following server-side cloud presence matches SimpleClouds storm fog/screen-space fog running while the actual cloud mesh composite is missing. Storm fog is a post-process tied to cloud regions, not to whether OFSC successfully composites the cloud target.
- This exact composite-gate + storm-fog-disable approach was not previously attempted.

Patch:

- `FinalCloudCompositeHandler` now runs the final cloud composite whenever a selectable scene depth texture exists, instead of requiring `capturedThisFrame`. This lets the populated cloud target reach the screen even if the snapshot depth capture failed.
- `ShaderAwareDhPipeline` and `ShaderAwareNoDhPipeline` now skip SimpleClouds storm fog while shaders are active by default.
- Added `-Dofsc.enableStormFogWithShaders=true` as an escape hatch to re-enable storm fog if needed.

Files:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareDhPipeline.java
src/main/java/net/Gabou/oculus_for_simpleclouds/dh/ShaderAwareNoDhPipeline.java
```

Expected result:

- Clouds should become visible again because final composite no longer depends on a successful captured-depth snapshot.
- Terrain should stop becoming translucent when a server-side cloud region is above the player because the storm fog post-process is skipped under shaders.
- If terrain remains translucent, the next target is the main framebuffer depth attachment swap to `cloudTarget.getDepthTextureId()` before lightning.

Observed in-game result:

- Pending user test.

## Attempt: Use Oculus/Iris Pipeline Depth Texture Instead of Minecraft Main Depth

New focus:

- User pointed out the core problem is earlier than compositing: every scene-depth source tested so far was null, unavailable, or sampled as empty.
- Prior band diagnostics showed:
  - combined blank
  - captured blank
  - external unavailable
  - original blank
  - cloud target depth valid
- This means the cloud target exists, but the terrain/tree scene depth source used for occlusion is not the real Oculus/Iris terrain depth texture.

Reason:

- The previous capture path only inspected `Minecraft.getMainRenderTarget()` and its framebuffer depth attachment.
- With Oculus/Iris shaders, the main Minecraft depth texture can exist but be cleared/stale/unused while Iris keeps the real gbuffer depth in its internal `RenderTargets`.
- Local Oculus 1.8.0 classes expose `Iris.getPipelineManager().getPipeline()` and `IrisRenderingPipeline` contains a private `renderTargets` field. `RenderTargets.getDepthTexture()` is the likely current Iris scene depth texture.
- This Iris render-target depth source was not previously attempted.

Patch:

- Added reflective access to `IrisRenderingPipeline.renderTargets`.
- Query `RenderTargets.getDepthTexture()`, `getCurrentWidth()`, and `getCurrentHeight()`.
- Prefer this Iris pipeline depth texture as the external scene-depth source when available.
- Use this Iris depth source for vanilla-depth copy into the SimpleClouds cloud target.
- Added depth-capture logging with sample reads so logs show whether the selected texture is actually non-empty.

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

Expected result:

- Logs should show `Depth capture: mode=iris_pipeline ...` or `iris_pipeline_composite ...`.
- If the sample values are not all clear depth (`1.0` for normal depth), this confirms the previous textures were empty because they were not Iris' real scene depth.
- If samples are still all clear depth, the next step is to inspect Iris' pre-translucent/no-hand depth copies or hook the exact terrain pass where Iris copies depth.

Compile status:

```text
./gradlew.bat compileJava
BUILD SUCCESSFUL
```

Observed in-game result:

- Pending user test.

## Attempt: Disable Depth Band Diagnostic By Default After Clouds Reappeared

Reason:

- User now sees cloud silhouettes, but they are still diagnostic colors from `DEBUG_DEPTH_COLOR_MODE=4`, not real cloud shading.
- In debug color mode the composite uses `GL_ALWAYS` and draws tinted alpha, so it is not a valid test for final cloud appearance or terrain blending.
- The band colors still provide useful evidence:
  - left blue: combined depth still blank
  - right bright magenta: cloud target depth is valid
  - other bands need log samples to interpret precisely
- This step is not a new depth-source fix; it removes the diagnostic overlay so the actual cloud composite can be tested.

Patch:

- Changed default `ofsc.debug.depthColorMode` from `4` to `0`.
- The diagnostic can still be re-enabled with `-Dofsc.debug.depthColorMode=4`.

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

Expected result:

- Clouds should render with their real color instead of blue/cyan/magenta diagnostic bands.
- If clouds disappear again with debug mode off, the issue is the normal discard/depth-test path, not mesh generation or target compositing.

Observed in-game result:

- User confirmed clouds now render with normal textures.
- Terrain transparency remains.
- Clouds still clip through blocks from roughly 3 blocks away out to max render distance.
- This means the cloud target/composite path works, but normal scene-depth occlusion is still wrong.

## Attempt: Correct Normal-Depth Occlusion Bias and Comparison

Reason:

- User confirmed clouds now have normal textures, so the missing-cloud issue is mostly resolved.
- Remaining cloud issue: clouds clip through blocks from about 3 blocks away to max render distance.
- Current composite shader was wrong for normal depth:
  - It computed `fragD = cloudD - eps`, which moves the cloud depth closer to the camera.
  - It hid clouds only when `cloudD - eps > sceneD`, meaning a cloud slightly behind terrain could still pass.
  - Java passed `uBias=0.001`, which is very large in depth-buffer space and made the permissive comparison worse.
- This exact shader comparison/bias correction was suggested earlier but not actually applied in the current code.

Patch:

- Reduced Java `uBias` from `0.001F` to `0.00001F`.
- Added `fwidth(cloudD) * 2.0` padding in the shader.
- Changed normal-depth hide rule to:
  - `hidden = cloudD >= sceneD - eps`
- Changed reverse-depth hide rule to:
  - `hidden = cloudD <= sceneD + eps`
- Changed normal-depth output to write the cloud slightly farther, not closer:
  - `gl_FragDepth = min(cloudD + eps, 1.0)`
- Changed reverse-depth output to the corresponding farther value:
  - `gl_FragDepth = max(cloudD - eps, 0.0)`

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

Expected result:

- Blocks/terrain with valid Iris scene depth should occlude clouds instead of clouds bleeding through them.
- If clouds still clip through blocks, the next target is UV/viewport mismatch between the Iris depth texture and the cloud composite screen UVs, not the comparison rule.

Observed in-game result:

- Pending user test.

## Diagnostic: Default Occlusion Color Mode

Reason:

- User confirmed the previous occlusion comparison patch did not fix clipping.
- Stop guessing and inspect what the composite actually believes per pixel.
- This diagnostic is different from the previous 5-band source scanner: it uses the currently selected scene depth source and colors only the occlusion decision.

Patch:

- Default `ofsc.debug.depthColorMode` is now `5`. No JVM argument is needed.
- Added shader mode `5`:
  - Blue: selected scene depth is sky/empty at that cloud pixel. This means the depth source/UV/timing is missing the block there.
  - Red: scene depth is closer than cloud depth; the cloud should be occluded by terrain/block.
  - Green: cloud depth is closer than scene depth; the shader thinks the cloud is legitimately in front.
  - Magenta: cloud and scene depth are nearly equal; bias/edge case.
  - Yellow: cloud depth is invalid/clear.

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

How to interpret the next screenshot/test:

- If clipping areas are blue, the selected Iris scene depth does not contain the blocks at those pixels, or UVs do not line up.
- If clipping areas are green, cloud depth is wrong or the cloud target depth was not seeded with terrain depth correctly.
- If clipping areas are red, the depth comparison says the block should hide the cloud; the issue is later GL depth/write/blend state.
- If clipping areas are magenta, reduce/tune bias or handle near-plane precision.

Observed in-game result:

- User tested: clouds still clip through blocks.
- Color diagnostic mode 5 showed:
  - Blue where selected scene depth is sky/empty.
  - Green in the area where clouds would visibly clip through blocks.
  - Red where clouds stop clipping.
  - Some magenta at boundaries.
- Interpretation: current selected scene depth sometimes says the cloud is in front of terrain where the user expects terrain to occlude it. Need compare Iris depth variants before another fix.

## Diagnostic: Compare Iris Depth Variants By Occlusion Decision

Reason:

- Mode 5 proved the normal selected scene depth produces green in clipping areas, meaning the shader thinks `cloudDepth` is closer than `sceneDepth`.
- That can be caused by wrong depth source/timing, UV mismatch, or cloud depth being wrong.
- Instead of guessing, compare multiple available depth textures side-by-side with the same occlusion decision.

Patch:

- Default `ofsc.debug.depthColorMode` is now `6`. No JVM argument is needed.
- Added uniforms for Iris depth variants:
  - `uDepthIrisNoTranslucents` from `RenderTargets.getDepthTextureNoTranslucents()`
  - `uDepthIrisNoHand` from `RenderTargets.getDepthTextureNoHand()`
- Mode 6 displays five vertical bands, each using the same color meaning:
  - 0-20%: Iris current depth (`RenderTargets.getDepthTexture()`, also current external)
  - 20-40%: Iris pre-translucent depth
  - 40-60%: Iris no-hand depth
  - 60-80%: Minecraft original main depth
  - 80-100%: cloud target depth used as scene depth sanity check
- Colors per band:
  - Blue: that source is sky/empty at the cloud pixel
  - Red: that source would hide the cloud
  - Green: that source thinks the cloud is in front
  - Magenta: near equal/bias edge
  - Yellow: cloud depth invalid
  - Dark gray: source unavailable

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

What to look for:

- If one of the first three Iris bands is red where the current band is green, use that Iris depth variant for final occlusion.
- If all Iris bands are green over clipping blocks, cloud depth is probably wrong/too close or the cloud target was not seeded with scene depth correctly.
- If bands change from red/green at a diagonal unrelated to geometry, UV/viewport scaling is wrong.

Observed in-game result:

- User screenshot showed terrain-occlusion bands still mostly blue/green where nearby ice should produce red.
- User specifically noted the ice should be red and asked about the far-right magenta.
- Interpretation: far-right magenta is expected because that band compares cloud depth against cloud depth, so near-equal magenta is normal. Real issue is earlier bands sampling terrain depth too far/empty.

## Diagnostic: Check Reverse-Depth And Y-Flip Against Iris Current Depth

Reason:

- Mode 6 showed the scene-depth source still gives blue/green where nearby terrain should occlude clouds.
- Two likely causes remain before changing code behavior:
  - The Iris depth texture UVs are vertically flipped relative to the fullscreen composite.
  - Reverse-depth detection is wrong at the composite stage.
- This diagnostic checks those two axes directly with the same Iris current depth texture.

Patch:

- Default `ofsc.debug.depthColorMode` is now `7`. No JVM argument is needed.
- Mode 7 displays five vertical bands:
  - 0-20%: Iris current depth, normal-depth comparison, normal UV
  - 20-40%: Iris current depth, forced reverse-depth comparison, normal UV
  - 40-60%: Iris current depth, normal-depth comparison, flipped Y UV
  - 60-80%: Iris current depth, forced reverse-depth comparison, flipped Y UV
  - 80-100%: cloud depth sanity band; magenta here is expected because cloud depth is compared to itself
- Colors keep the same meaning:
  - Blue: sampled scene depth is sky/empty
  - Red: sampled scene depth would hide the cloud
  - Green: sampled scene depth says cloud is in front
  - Magenta: near equal/bias edge
  - Yellow: cloud depth invalid
  - Dark gray: source unavailable

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

How to use result:

- If band 2 or 4 becomes red where band 1 is green/blue, reverse-depth handling is wrong.
- If band 3 or 4 becomes red where band 1 is green/blue, the Iris depth UV is Y-flipped.
- If all first four bands remain green/blue over ice that should occlude, the Iris current depth texture itself is not the right source/timing and we need a different hook/source.

Observed in-game result:

- User reported none of the five mode-7 bands are 100% correct.
- Band 1 (Iris current, normal UV, normal depth) is the best, but still wrong: it displays/hides things behind clouds incorrectly except very close to the player (~3 blocks).
- Band 2 and band 4 are almost always red and become green up close, so forced reverse-depth is wrong.
- Band 3 is the current view inverted, so Y-flip is wrong.
- Band 5 is always magenta, as expected for cloud-depth compared against itself.
- Interpretation: simple reverse-depth and Y-flip mistakes are ruled out. The best available scene source is still not directly comparable to cloud depth or is captured at the wrong stage/space.


## Diagnostic: Raw Per-Pixel Scene/Cloud Depth Delta

Reason:

- Mode 7 already checked reverse depth and Y-flip and ruled them out.
- This is not repeating that test: mode 8 shows the raw sampled depth values and the per-pixel delta directly, instead of only showing the final occlusion decision.
- The goal is to prove whether the selected Iris current depth and the cloud depth are numerically comparable at the exact pixels where clipping happens.

Patch:

- Default `ofsc.debug.depthColorMode` is now `8`. No JVM argument is needed.
- Mode 8 displays four vertical bands:
  - 0-25%: selected Iris current scene depth as grayscale; blue means scene depth is sky/empty.
  - 25-50%: cloud target depth as grayscale; yellow means cloud depth is invalid/clear.
  - 50-75%: amplified signed delta `cloudDepth - sceneDepth`; red means cloud is behind scene, green means cloud is in front, blue means scene is sky, magenta means near-equal.
  - 75-100%: sign-only version of the same delta, with the same colors.

File:

```text
src/main/java/net/Gabou/oculus_for_simpleclouds/client/FinalCloudCompositeHandler.java
```

How to use result:

- If the clipping area is blue in the first/third/fourth band, the selected scene depth does not contain terrain there.
- If scene grayscale changes with terrain but the delta is green where blocks should hide clouds, the cloud depth is in a different depth space or is seeded incorrectly.
- If delta is red where clipping still happens in normal mode, the issue is not the comparison; it is later GL state/composite behavior.
- If cloud depth is yellow, the cloud depth target is invalid/clear at those pixels.

Build/copy:

- `gradlew.bat jar` succeeded on 2026-05-27.
- Rebuilt jar should be copied into the test instance for in-game validation.

## Fix Attempt: Snapshot Iris Scene Depth Before Clouds Instead Of Sampling Live Iris Depth

New evidence:

- Mode 8 showed the selected scene-depth band containing cloud-shaped data and far/white depth where terrain should occlude clouds.
- That means the live Iris depth texture being sampled at final composite time is not a stable scene-only depth source; it can be updated/cleared/polluted after the terrain stage.
- This is different from the previous source/reverse/Y-flip tests: those compared live depth variants, while this freezes a copy before the cloud composite.

Patch:

- `captureVanillaDepthSource` now snapshots the Iris pipeline depth texture into OFSC's own `capturedSceneDepthTex` using the existing depth-copy fullscreen shader.
- The snapshot is stored in the depth-only capture FBO and marked `capturedThisFrame=true`.
- Scene-depth selection now prefers the captured snapshot before the live external Iris texture.
- Mode 8 now visualizes the actually selected scene-depth source via `uSceneSource`, instead of hardcoding the live external Iris texture.

Expected behavior:

- Mode 8 band 1 should stop showing live cloud-shaped depth pollution.
- Terrain/tree/ice pixels that should occlude clouds should no longer appear as far/white just because the live Iris texture changed later.
- If clipping remains, the next evidence should distinguish snapshot timing from cloud-depth mismatch.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

## Fix Attempt: Prefer Main RenderTarget Depth Snapshot Before Iris RenderTargets

New evidence:

- User reported mode 8 still shows the tree as white in the left scene-depth band after the Iris snapshot change.
- Because raw depth is non-linear, distant terrain can look white even when valid, but this still means the previous Iris-only snapshot did not give a clearly usable terrain mask for the clipping case.
- Previous attempts tested live `original` and live Iris depth variants; this attempt snapshots the actual Minecraft main render target depth attachment first, before falling back to Iris.

Patch:

- `captureVanillaDepthSource` now first tries to snapshot the bound/main `RenderTarget` depth attachment via `captureRenderTargetDepthSource`.
- Only if the main render target depth capture fails does it fall back to Iris `RenderTargets.getDepthTexture()` and snapshot that.
- Capture logs now distinguish:
  - `main_fbo_snapshot`
  - `main_fbo_direct_fallback`
  - `main_fbo_blit`
  - Iris fallback modes
- Existing mode 8 remains enabled by default and still visualizes the selected depth source.

Expected behavior:

- If the main render target contains cutout/terrain depth at the stage we capture, mode 8 should use that instead of live/pipeline Iris depth.
- The log should show `Depth capture: mode=main_fbo_snapshot` or `main_fbo_blit` before final composite.
- If it still falls back to Iris, the main target does not expose usable depth at that stage and the next fix must move the hook/timing rather than compare different Iris textures.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

## Fix Attempt: Stop Failed Main-FBO Blit From Selecting Empty Captured Depth

New evidence from logs:

- The current run produced OpenGL errors: `GL_INVALID_OPERATION error generated. Depth formats do not match.`
- The same run selected `source=captured tex=108` for final composite even though the main-FBO depth blit can fail on depth-format mismatch.
- This explains clouds disappearing even in color debug: the failed snapshot path could leave `capturedSceneDepthTex` empty/stale and still mark/select it.

Patch:

- Main render target texture depth is now snapshotted with the fullscreen depth-copy shader instead of `glBlitFramebuffer`, avoiding depth-format mismatch on texture depth attachments.
- `main_fbo_direct_fallback` no longer sets `capturedThisFrame=true`, so a failed capture cannot cause final composite to prefer an empty captured texture.
- `blitDepthAttachmentToResolveTarget` now checks the GL error immediately and only updates captured metadata after a successful blit.

Expected behavior:

- The `Depth formats do not match` error should stop for the main texture-depth path.
- Mode 8/color debug should show clouds again if cloud color is being rendered.
- If clouds are still missing, the next check is whether `cloudColorTex` has alpha/color after the DH cloud render pass.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

Observed in-game result:

- User reported clouds are back after the failed-blit/captured-depth selection fix.
- This confirms the prior missing-cloud regression was caused by selecting a bad captured depth after the main-FBO depth-format mismatch path.
- Remaining issue is still cloud-vs-terrain occlusion accuracy, not cloud mesh/color rendering.

## Fix Attempt: Skip Empty SimpleClouds Transparency Pass

New evidence:

- User found the terrain/translucency issue appears only when positioned above roughly Y=125 while cloud height is 256, and goes away below that height.
- In the latest logs, cloud rendering is active with `opaqueVerts=161407` but `transparentVerts=0`, while `generator.transparencyEnabled()` is still true.
- That means the shader-aware pipelines were still calling `copyDepthFromCloudsToTransparency()` and binding the cloud transparency target even when there were no transparent cloud vertices to render.
- This has not been tried before; previous fixes focused on scene/cloud depth source selection and captured-depth correctness.

Patch:

- In both `ShaderAwareDhPipeline` and `ShaderAwareNoDhPipeline`, the transparent cloud depth-copy/render pass now runs only when:
  - `generator.transparencyEnabled()` is true, and
  - `transparentVerts > 0`
- Opaque cloud rendering is unchanged.
- Mode 8 remains enabled by default for depth diagnostics.

Expected behavior:

- When logs show `transparentVerts=0`, the pipeline will no longer touch the SimpleClouds transparency target/depth path for that frame.
- If the terrain/liquid translucency was caused by the empty transparency path leaking state/depth, it should stop while clouds remain visible.
- If the terrain issue remains, the next target is the main FBO depth attachment swap/restore path around cloud lightning or final composite.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

Observed in-game result:

- User reported the Y>125 terrain/translucency issue seems fixed after skipping the empty transparency pass when `transparentVerts=0`.
- Keep this as the current working terrain-state fix unless new evidence shows a remaining main-FBO depth attachment leak.

## Diagnostic: Multi-Stage Main RenderTarget Depth Capture

New evidence:

- User screenshot in mode 8 showed the selected scene-depth source missing/empty across large regions where terrain should occlude clouds; band 4 stayed green/blue instead of red.
- This points to a timing/source problem, not a bias problem.
- Previous attempts tested live depth sources and one selected captured source; this diagnostic captures multiple render stages separately to find where terrain/tree depth first appears and whether it later disappears.

Patch:

- Default `ofsc.debug.depthColorMode` is now `9`.
- Added five per-frame main RenderTarget depth snapshots:
  - stage 0: `AFTER_SOLID_BLOCKS`
  - stage 1: `AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS`
  - stage 2: `AFTER_CUTOUT_BLOCKS`
  - stage 3: `AFTER_BLOCK_ENTITIES`
  - stage 4: `AFTER_TRIPWIRE_BLOCKS`
- Each snapshot copies the current main depth texture into a dedicated OFSC depth texture via fullscreen shader copy, avoiding depth-format blit mismatch.
- Mode 9 displays five vertical bands, left to right, one for each stage above.
- Colors use the same occlusion decision as before:
  - red: that stage depth would hide the cloud
  - green: that stage depth says the cloud is in front
  - blue: that stage depth is sky/empty
  - magenta: near-equal/bias edge
  - yellow: cloud depth invalid
  - dark gray: stage capture unavailable
- Stage capture logs print once per second in mode 9: `Stage depth capture: stage=... tex=... size=...`.

Expected behavior:

- At a tree/block pixel that should be in front of a cloud, at least one stage band should become red.
- If solid is blue/green but cutout or later is red, final occlusion should use that later stage.
- If all five bands are blue/green over visible terrain, the main depth target we can access does not contain the required terrain depth in this shader pipeline, and the next fix must use a different hook/source outside RenderLevelStageEvent.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

Observed in-game result:

- User reported all five mode-9 stage bands are wrong.
- Screenshot shows the bands mostly green over the cloud/terrain overlap, with blue only where the sampled scene depth is sky/empty.
- Interpretation update: the main issue is probably not just the RenderLevelStageEvent capture timing. The stage scene-depth textures are available, but the cloud depth compares as closer than terrain almost everywhere.
- This points to a depth-space mismatch between SimpleClouds cloud depth and the main/Iris scene depth, or a cloud-depth generation problem, rather than a missing cutout/tree stage alone.

## Diagnostic: Depth Transform / Inversion Comparison

New evidence:

- User reported all five mode-9 render-stage snapshots are wrong, mostly green where terrain should occlude clouds.
- This means stage timing alone is not the issue: the available scene depth and cloud depth are being compared in incompatible ways, or one side is inverted/transformed relative to the other.
- This diagnostic is not a repeat of mode 7 reverse-depth testing. Mode 7 changed the comparison convention for a live Iris source; mode 10 tests explicit transformations of the cloud and scene depth values before comparison.

Patch:

- Default `ofsc.debug.depthColorMode` is now `10`.
- Added `uNear` and `uFar` uniforms from `GameRenderer.PROJECTION_Z_NEAR` and `Minecraft.gameRenderer.getDepthFar()`.
- Mode 10 uses the latest tripwire-stage scene snapshot when available, otherwise the selected scene source.
- Mode 10 displays five vertical bands:
  - 0-20%: raw cloud depth vs raw scene depth
  - 20-40%: inverted cloud depth vs raw scene depth
  - 40-60%: raw cloud depth vs inverted scene depth
  - 60-80%: inverted cloud depth vs inverted scene depth
  - 80-100%: linearized raw cloud depth vs linearized raw scene depth
- Colors keep the same meaning:
  - red: scene is closer and should hide the cloud
  - green: transformed comparison says the cloud is closer
  - blue: scene is sky/empty
  - magenta: near-equal/bias edge
  - yellow: cloud depth invalid
  - dark gray: scene source unavailable

Expected behavior:

- If one transformed band turns red where raw bands are green, that tells us which transform must be applied in the real composite.
- If all bands remain green over terrain that should occlude, the cloud depth being written by SimpleClouds is not representing comparable camera depth for these pixels; the fix must change how the cloud target depth is seeded/written, not just the final composite comparison.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

Observed in-game result:

- User reported all five mode-10 transform bands still look the same/green over the clipping area.
- Interpretation: simple value inversion/linearization is not identifying a usable depth transform. The next diagnostic should ignore cloud depth entirely and test whether scene depth alone can act as a terrain mask.

## Diagnostic: Scene-Depth Mask Only

Correction/new evidence:

- After checking the source, the interrupted depth-transform patch was not present in `FinalCloudCompositeHandler.java`; the jar the user tested was still effectively the multi-stage mode-9 diagnostic.
- The reported result is still valid for mode 9: all five stage comparison bands were wrong/green.
- Instead of repeating transform guesses, this diagnostic checks whether the captured scene depth can work as a simple terrain mask without using cloud depth at all.

Patch:

- Default `ofsc.debug.depthColorMode` is now `11`.
- Mode 11 uses the same five stage snapshots as mode 9:
  - 0-20%: `AFTER_SOLID_BLOCKS`
  - 20-40%: `AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS`
  - 40-60%: `AFTER_CUTOUT_BLOCKS`
  - 60-80%: `AFTER_BLOCK_ENTITIES`
  - 80-100%: `AFTER_TRIPWIRE_BLOCKS`
- Mode 11 ignores cloud depth for the decision:
  - red: scene depth exists, so a terrain-mask composite would hide the cloud there
  - blue: scene depth is sky/empty, so clouds would show there
  - yellow: cloud depth is invalid/clear
  - dark gray: stage capture unavailable

Expected behavior:

- If terrain/tree pixels become red while sky/cloud-only pixels are blue, a pragmatic final fix is to use scene-depth masking instead of comparing cloud depth.
- If the terrain/tree pixels are still blue, then even scene-depth masking cannot work from these captured sources.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

## Fix Attempt: Use Scene-Depth Mask For Final Cloud Composite

New evidence:

- User reported mode 11 showed every object that should appear in front of clouds as red, with no green.
- That proves scene depth alone is a usable occlusion mask, while cloud-depth comparison is the unreliable part.
- This is not repeating previous depth comparisons; it deliberately avoids cloud-depth ordering for final occlusion.

Patch:

- Default `ofsc.debug.depthColorMode` is now `0`, so colors are disabled by default.
- Final composite now discards cloud pixels whenever selected scene depth is present/non-sky.
- Cloud depth is still sampled for validity/fragment depth, but it no longer decides terrain occlusion in normal mode.
- Debug modes remain available with `-Dofsc.debug.depthColorMode=<mode>` if needed.

Expected behavior:

- Blocks, terrain, trees, and other scene geometry should render in front of clouds.
- Clouds should still render where the scene depth is sky/empty.
- Potential tradeoff: if a cloud should appear in front of distant terrain, this conservative mask can hide it, but it matches the evidence and fixes the clipping-through-blocks case.

Observed/result:

- Build succeeded on 2026-05-27.
- Copied to the CurseForge test instance for validation.

Observed in-game result:

- User reported: "omg it works" after the scene-depth mask final composite fix.
- Current working fix: keep debug mode disabled by default and use scene-depth-present/non-sky masking for final cloud occlusion instead of raw cloud-vs-scene depth comparison.
- Current known tradeoff: conservative mask can hide clouds anywhere scene depth exists, but it resolves clouds rendering through blocks/trees with the tested shader/DH setup.
