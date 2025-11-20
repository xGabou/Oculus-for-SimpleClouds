# Complementary Reimagined – SimpleClouds Integration Log

## Latest update (SimpleClouds blending request)
- Introduced a rain atmosphere module (`/shaders/lib/atmospherics/rain/rainCommon.glsl` and `rainSplashes.glsl`) plus new gbuffers/composite hooks so rain streaks respect depth, wind shear, and time-based variance while adding screen-space ripples and splashes tied to SimpleClouds weather intensity.
- Added wet-surface compositing with puddle gloss/fresnel/ripple overlays, horizon tint layers, and a shared atmospheric height fade helper to keep storms grounded while drying out smoothly between showers.
- Added `/shaders/lib/util/sc_bridge.glsl` to centralize the `sc_simpleCloudState` and `sc_simpleCloudType` uniform handling plus helper functions (visibility, thickness, storm factors, type-based shading curves).
- Added wet-surface compositing with puddle gloss/fresnel/ripple overlays, horizon tint layers, and a shared atmospheric height fade helper to keep storms grounded while drying out smoothly between showers.
- Introduced a comprehensive atmospheric enhancement suite: configurable storm boost/camera shake, sun-scattering arcs, cloud rim tinting via SimpleClouds data, chromatic highlight/altitude grading, lightning-aware flashes, lens dirt, heat haze, micro sky noise, and moon halo polish. All features live in modular GLSL helpers with dedicated shader options.
- Optional cloud shadow casting integrates SimpleClouds coverage directly into `shadow.glsl`, ray-marching a coarse density volume and darkening the sun shadow map when enabled via the new ATM_CLOUD_SHADOWS toggle.
- Raised entity baseline exposure so mobs remain visible under dense tree/ storm coverage and corrected the cloud-shadow world position sampling so SimpleClouds opacity actually registers in the sun shadow map.
- Rain and snow particles now borrow more of the current sky tint: both the dedicated weather pass and physics-based particles blend in skyColor so precipitation inherits ambient light instead of rendering as flat white/gray streaks.
- Simplified cloud shadow quality handling: removed the recursive option/uniform and now clamp the mode at compile time via `CLOUD_SHADOW_QUALITY_MODE`, preventing the circular reference crash some users saw when `cloudShadowQuality` pointed back to itself.
- Updated the volumetric cloud stack (`/shaders/lib/atmospherics/clouds/mainClouds.glsl`, `reimaginedClouds.glsl`, `unboundClouds.glsl`) to scale density/opacity, sampling thickness, distance falloff, and shading with the SimpleClouds fade/density/storm inputs while keeping Complementary’s native logic.
- Fed the SimpleClouds modifiers through the shared color pipelines (`/shaders/lib/colors/cloudColors.glsl`, `lightAndAmbientColors.glsl`, `skyColors.glsl`) plus `/shaders/lib/atmospherics/sky.glsl` so sky brightness and weather coloration now darken/tint with storminess rather than being overridden.
- Reworked the `lightAndAmbientColors.glsl` adjustments so the storm/thickness multipliers are folded into the vector declarations instead of using global-scope `*=` statements, keeping Iris’s AST parser happy while preserving the intended visual change.

## Future notes
- Add the next request summary here so this log tracks every manual edit applied on top of upstream Complementary.
