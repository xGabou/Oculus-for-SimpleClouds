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
