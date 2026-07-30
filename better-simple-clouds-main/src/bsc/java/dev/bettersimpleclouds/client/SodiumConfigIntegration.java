package dev.bettersimpleclouds.client;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.structure.BooleanOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import dev.bettersimpleclouds.client.gui.BscOptionsScreen;
import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

/**
 * Surfaces every Better Simple Clouds <b>video/visual</b> option inside Sodium's video settings (and therefore
 * Reese's Sodium Options), split into pages, with the mod's icon on its entry. The non-video options (cloud-type
 * blacklist, debug) live in the mod's own options screen, reachable from here through an external page link -
 * so every option has exactly one home.
 *
 * <p><b>Self-gating:</b> referenced nowhere in our own code; Sodium's config scanner finds it via the
 * {@link ConfigEntryPointForge} annotation, so nothing loads it when Sodium is absent. Each option binds straight to
 * a shared {@link BetterSimpleCloudsConfig} setter/getter (edits apply immediately and persist). Every stateful
 * option needs a storage handler or Sodium 0.8 refuses to build it; our setters already save, so it's a no-op.</p>
 */
@ConfigEntryPointForge("bettersimpleclouds")
public final class SodiumConfigIntegration implements ConfigEntryPoint {

    private static ResourceLocation id(final String path) {
        return ResourceLocation.fromNamespaceAndPath("bettersimpleclouds", path);
    }

    private static Component blocks(final int value) {
        return Component.literal(value + " blocks");
    }

    private static Component percent(final int value) {
        return Component.literal(value + "%");
    }

    @Override
    public void registerConfigLate(final ConfigBuilder builder) {
        // ---------- Performance ----------
        final BooleanOptionBuilder optimizeMesh = builder
            .createBooleanOption(id("cloud_optimize_mesh"))
            .setName(Component.literal("Concurrent Mesh Gen (experimental)"))
            .setTooltip(Component.literal(
                "Force Simple Clouds' concurrent (fixed-section) cloud mesh generation: big FPS gain, but it gives each "
                    + "cloud chunk a fixed mesh slot so big/storm clouds OVERFLOW and flicker / lose faces. Off by "
                    + "default for that reason. Turn on only with small/uniform clouds."))
            .setImpact(OptionImpact.HIGH)
            .setDefaultValue(false)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudOptimizeMeshGeneration,
                BetterSimpleCloudsConfig::cloudOptimizeMeshGeneration);

        // ---------- Far-cloud detail ----------
        final IntegerOptionBuilder maxLod = builder
            .createIntegerOption(id("cloud_max_lod"))
            .setName(Component.literal("Cap Cloud LOD (finer far)"))
            .setTooltip(Component.literal(
                "Clouds never render coarser than this LOD level - coarser rings are rebuilt from more, finer cubes so "
                    + "far clouds keep their shape instead of becoming single cubes. Does NOT cull; adds geometry the "
                    + "lower you set it. 0 = off (stock, cheapest); 2-3 = a good middle ground; 1 = finest (costly). "
                    + "Applies live - rebuilds the cloud mesh when changed (brief hitch)."))
            .setRange(0, 6, 1)
            .setDefaultValue(0)
            .setImpact(OptionImpact.HIGH)
            .setValueFormatter(value -> value == 0 ? Component.literal("Off") : Component.literal("LOD " + value))
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudMaxLodLevel, BetterSimpleCloudsConfig::cloudMaxLodLevel);

        final IntegerOptionBuilder bigLodStrength = builder
            .createIntegerOption(id("cloud_lod_big"))
            .setName(Component.literal("LOD Soften: Big Clouds"))
            .setTooltip(Component.literal(
                "How aggressively LOD thins BIG clouds (huge connected/storm masses) with distance. 0% = leave them "
                    + "alone (usual). Higher = even big clouds thin far away. Never removes a cloud's core."))
            .setRange(0, 100, 5)
            .setDefaultValue(0)
            .setImpact(OptionImpact.MEDIUM)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setBigCloudLodPercent, BetterSimpleCloudsConfig::bigCloudLodPercent);

        final IntegerOptionBuilder smallLodStrength = builder
            .createIntegerOption(id("cloud_lod_small"))
            .setName(Component.literal("LOD Soften: Small Clouds"))
            .setTooltip(Component.literal(
                "How aggressively LOD thins SMALL clouds (anything not a huge connected mass) with distance. Raise to "
                    + "declutter the far sky while big clouds stay. Never removes a cloud's core."))
            .setRange(0, 100, 5)
            .setDefaultValue(0)
            .setImpact(OptionImpact.MEDIUM)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setSmallCloudLodPercent, BetterSimpleCloudsConfig::smallCloudLodPercent);

        final IntegerOptionBuilder smallCull = builder
            .createIntegerOption(id("cloud_small_cull"))
            .setName(Component.literal("Cull Far Small Clouds"))
            .setTooltip(Component.literal(
                "The real fix for far small clouds that look like single cubes: beyond the nearest ring, drop clouds "
                    + "whose storminess is below this threshold entirely. Big storm clouds stay. 0% = off; ~30-50% "
                    + "clears calm/small clouds far away. Also helps performance."))
            .setRange(0, 100, 5)
            .setDefaultValue(0)
            .setImpact(OptionImpact.MEDIUM)
            .setValueFormatter(value -> value == 0 ? Component.literal("Off") : SodiumConfigIntegration.percent(value))
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setFarSmallCloudCullPercent,
                BetterSimpleCloudsConfig::farSmallCloudCullPercent);

        final IntegerOptionBuilder minCluster = builder
            .createIntegerOption(id("cloud_min_cluster"))
            .setName(Component.literal("Remove Single Far Cubes"))
            .setTooltip(Component.literal(
                "Remove small isolated floating cloud clusters from far (LOD-coarsened) clouds. A cube is dropped only "
                    + "if its WHOLE connected cluster is this many cubes or fewer, so stray single cubes and tiny blobs "
                    + "drifting off in LOD chunks vanish whole, while big formations (and their thin edges/tendrils) are "
                    + "left fully intact - it can never erode a real cloud or leave holes. 0 = off; 1 = remove only "
                    + "single cubes; 2-6 = also remove blobs up to that many cubes. Full-detail clouds near the camera "
                    + "are never affected. Cost grows with the value."))
            .setRange(0, 6, 1)
            .setDefaultValue(0)
            .setImpact(OptionImpact.MEDIUM)
            .setValueFormatter(value -> value == 0 ? Component.literal("Off")
                : Component.literal(value == 1 ? "Single cubes" : "Up to " + value + " cubes"))
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudMinClusterNeighbors,
                BetterSimpleCloudsConfig::cloudMinClusterNeighbors);

        // ---------- Storm fog (distant rain) ----------
        final BooleanOptionBuilder rainBehindClouds = builder
            .createBooleanOption(id("storm_rain_behind_clouds"))
            .setName(Component.literal("Rain Visible Behind Clouds"))
            .setTooltip(Component.literal(
                "Make Simple Clouds' distant rain (storm fog) blur over clouds and far scenery just like over open sky. "
                    + "The far rain normally stops at the cloud layer's depth, so it gets cut off wherever a cloud (or "
                    + "far LOD terrain) sits; this makes the rain ignore that and render its full distance. Its own "
                    + "fades + the storm shadow map still control where it shows. Shader render path only."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setStormFogRainBehindClouds,
                BetterSimpleCloudsConfig::stormFogRainBehindClouds);

        final BooleanOptionBuilder disableRainBlur = builder
            .createBooleanOption(id("disable_rain_blur"))
            .setName(Component.literal("Disable Distant Rain Blur"))
            .setTooltip(Component.literal(
                "Fully disable the blur on Simple Clouds' distant rain (storm fog). The far rain is rendered low-res then "
                    + "box-blurred over the whole screen, smearing its softness onto the edges of everything - including a "
                    + "near rain mod's close particles (e.g. Pretty Rain). With this on the blur pass is skipped, so the "
                    + "far rain stays crisp and stops bleeding onto the near rain. Applies live."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(false)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setDisableDistantRainBlur,
                BetterSimpleCloudsConfig::disableDistantRainBlur);

        // ---------- Transparency ----------
        final BooleanOptionBuilder fixTransparentEdges = builder
            .createBooleanOption(id("fix_transparent_edges"))
            .setName(Component.literal("Fix Transparent Cloud Edges"))
            .setTooltip(Component.literal(
                "Fix the mottled, 'inside and outside coincide' look of a cloud's soft translucent edges when seen "
                    + "through another cloud (worst on big clouds). Simple Clouds blends those edges with weighted-blended "
                    + "OIT whose weight falls off steeply with distance, so across a big cloud's thick fringe a near cube's "
                    + "weight dwarfs a far one's and the blend reads as faces darker/lighter in a noise pattern. This swaps "
                    + "in a shader that flattens that depth weighting (strength below) so the noise clears. Only while "
                    + "outside clouds. Off = stock Simple Clouds transparency."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setFixTransparentCloudEdges,
                BetterSimpleCloudsConfig::fixTransparentCloudEdges);

        final IntegerOptionBuilder transparentEdgeSmoothing = builder
            .createIntegerOption(id("transparent_edge_smoothing"))
            .setName(Component.literal("Transparent Edge Smoothing"))
            .setTooltip(Component.literal(
                "How strongly the fix above flattens the OIT depth weighting. 0% = stock weighting (no change even with "
                    + "the fix on); 100% = fully flat (near and far fringe weighted the same - smoothest). ~70-90% clears "
                    + "the big-cloud noise while keeping some depth ordering. Only matters when the fix is on. Applies live."))
            .setRange(0, 100, 5)
            .setDefaultValue(80)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setTransparentEdgeSmoothingPercent,
                BetterSimpleCloudsConfig::transparentEdgeSmoothingPercent);

        // ---------- In-cloud immersion ----------
        final BooleanOptionBuilder inCloudEnabled = builder
            .createBooleanOption(id("incloud_enabled"))
            .setName(Component.literal("In-Cloud Immersion"))
            .setTooltip(Component.literal(
                "Master switch for the whole in-cloud effect: solid faces, interior fill, drifting motes and fog while "
                    + "the camera is inside a Simple Clouds cloud."))
            .setImpact(OptionImpact.MEDIUM)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudEnabled, BetterSimpleCloudsConfig::inCloudEnabled);

        final BooleanOptionBuilder solidFaces = builder
            .createBooleanOption(id("incloud_solid_faces"))
            .setName(Component.literal("Solid Cloud Faces"))
            .setTooltip(Component.literal(
                "Generate the cloud faces that point away from the camera (near you only), so clouds aren't "
                    + "see-through from the inside. Off = Simple Clouds' default culling."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudSolidFaces, BetterSimpleCloudsConfig::inCloudSolidFaces);

        final BooleanOptionBuilder fill = builder
            .createBooleanOption(id("incloud_fill"))
            .setName(Component.literal("Fill Cloud Interior"))
            .setTooltip(Component.literal(
                "Fill a cloud's hollow interior while you're inside it: translucent haze near you ramping to a solid "
                    + "cloud shell at the view distance, so you can't see across a huge cloud."))
            .setImpact(OptionImpact.MEDIUM)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudInteriorFillEnabled,
                BetterSimpleCloudsConfig::inCloudInteriorFillEnabled);

        final IntegerOptionBuilder viewDist = builder
            .createIntegerOption(id("incloud_view_distance"))
            .setName(Component.literal("In-Cloud View Distance"))
            .setTooltip(Component.literal(
                "How far (in blocks) you can see while inside a cloud. Haze fills within this distance and an opaque "
                    + "shell (and fog, with shaders off) cuts the view off at it. Lower = thicker and more enclosed."))
            .setRange(8, 96, 4)
            .setDefaultValue(BetterSimpleCloudsConfig.IN_CLOUD_VIEW_DIST_DEFAULT)
            .setImpact(OptionImpact.MEDIUM)
            .setValueFormatter(SodiumConfigIntegration::blocks)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudViewDistanceBlocks,
                BetterSimpleCloudsConfig::inCloudViewDistanceBlocks);

        final IntegerOptionBuilder haze = builder
            .createIntegerOption(id("incloud_haze"))
            .setName(Component.literal("Interior Haze / Visibility"))
            .setTooltip(Component.literal(
                "How thick the haze is per cloud cell - it builds up the further you look, so this is your in-cloud "
                    + "visibility dial: LOWER = see further/clearer, HIGHER = murkier. Independent of view distance."))
            .setRange(0, 100, 1)
            .setDefaultValue(12)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudHazePercent, BetterSimpleCloudsConfig::inCloudHazePercent);

        final BooleanOptionBuilder shell = builder
            .createBooleanOption(id("incloud_shell"))
            .setName(Component.literal("View-Distance Shell"))
            .setTooltip(Component.literal(
                "Draw a solid opaque cloud wall at the view distance so you truly can't see across a huge cloud (also "
                    + "hides distant cloud chunks). Off = haze only, with no hard cut-off."))
            .setImpact(OptionImpact.MEDIUM)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudSolidViewShell, BetterSimpleCloudsConfig::inCloudSolidViewShell);

        final BooleanOptionBuilder fog = builder
            .createBooleanOption(id("incloud_fog"))
            .setName(Component.literal("In-Cloud Fog (no shaders)"))
            .setTooltip(Component.literal(
                "Also draw cloud-coloured distance fog while inside a cloud. This is what limits the view with shaders "
                    + "OFF; an Iris shaderpack controls fog itself and may ignore it (the shell still works there)."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudFogEnabled, BetterSimpleCloudsConfig::inCloudFogEnabled);

        final IntegerOptionBuilder motes = builder
            .createIntegerOption(id("incloud_motes"))
            .setName(Component.literal("Mote Density"))
            .setTooltip(Component.literal(
                "How many drifting motes fill the cloud interior. 100% = default; 0% = none. Still scaled by the "
                    + "cloud's storminess, so calm clouds stay sparse regardless."))
            .setRange(0, 300, 10)
            .setDefaultValue(100)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudDensityPercent, BetterSimpleCloudsConfig::inCloudDensityPercent);

        final IntegerOptionBuilder vpad = builder
            .createIntegerOption(id("incloud_vpad"))
            .setName(Component.literal("Vertical Edge Softness"))
            .setTooltip(Component.literal(
                "Soft-edge padding, in blocks, above and below the cloud layer - how gradually the in-cloud effect "
                    + "fades in and out as you cross the top and bottom of the cloud."))
            .setRange(0, 64, 2)
            .setDefaultValue(12)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::blocks)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudVerticalPaddingBlocks,
                BetterSimpleCloudsConfig::inCloudVerticalPaddingBlocks);

        // ---------- Cloud appearance ----------
        final IntegerOptionBuilder softTerrain = builder
            .createIntegerOption(id("soft_terrain_fade"))
            .setName(Component.literal("Soft Terrain Edge"))
            .setTooltip(Component.literal(
                "Soften where clouds cut into terrain. Cloud cubes know nothing about the world, so a cloud meeting a "
                    + "mountain gives a hard edge - and since the two surfaces then sit at nearly the same depth they "
                    + "Z-FIGHT, making the intersection flicker. Fading the cloud out near terrain turns the cut into a "
                    + "gradient and stops the flicker. 0 = off (stock); 8-16 blocks is a natural haze. NEEDS A "
                    + "SHADERPACK (Iris) - only then do clouds render after the world, which is what makes terrain depth "
                    + "available. Does nothing with shaders off."))
            .setRange(0, 64, 2)
            .setDefaultValue(12)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(value -> value == 0 ? Component.literal("Off") : SodiumConfigIntegration.blocks(value))
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setSoftTerrainFadeBlocks,
                BetterSimpleCloudsConfig::softTerrainFadeBlocks);

        final BooleanOptionBuilder nightClouds = builder
            .createBooleanOption(id("night_clouds"))
            .setName(Component.literal("Better Night Clouds"))
            .setTooltip(Component.literal(
                "Make clouds look good at night instead of flat grey blobs. Simple Clouds darkens night clouds to a "
                    + "near-uniform dark grey that flattens the shading giving a cloud its shape; this re-opens that "
                    + "crushed range, lifts exposure a little and adds a cool moonlit cast so the form reads again. "
                    + "Only affects night (fades in after dusk; daytime is untouched) and works with or without shaders."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudNightBoost, BetterSimpleCloudsConfig::cloudNightBoost);

        final IntegerOptionBuilder nightBrightness = builder
            .createIntegerOption(id("night_clouds_strength"))
            .setName(Component.literal("Night Cloud Brightness"))
            .setTooltip(Component.literal(
                "How strong the night look is. 100% = the default moonlit grade; lower = closer to Simple Clouds' "
                    + "darker vanilla night; higher = brighter, more lifted night clouds. 0% = off. Only matters when "
                    + "Better Night Clouds is on."))
            .setRange(0, 200, 5)
            .setDefaultValue(100)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(value -> value == 0 ? Component.literal("Off") : SodiumConfigIntegration.percent(value))
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudNightBoostPercent,
                BetterSimpleCloudsConfig::cloudNightBoostPercent);

        final BooleanOptionBuilder shaderMatch = builder
            .createBooleanOption(id("incloud_shader_match"))
            .setName(Component.literal("Match Clouds to Shaders"))
            .setTooltip(Component.literal(
                "Blend clouds into a shaderpack-lit scene (sky tint + exposure below) instead of flat white cut-outs. "
                    + "An approximation - the pack can't truly render Simple Clouds - but it helps them fit the scene."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(false)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudShaderMatch, BetterSimpleCloudsConfig::inCloudShaderMatch);

        final IntegerOptionBuilder skyTint = builder
            .createIntegerOption(id("incloud_sky_tint"))
            .setName(Component.literal("Cloud Sky Tint"))
            .setTooltip(Component.literal(
                "How much clouds pick up the scene's sky/atmosphere colour. Higher = better aerial perspective, less "
                    + "flat white. (If far clouds look greyish without shaders, lower this.)"))
            .setRange(0, 100, 5)
            .setDefaultValue(25)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudShaderSkyTintPercent,
                BetterSimpleCloudsConfig::inCloudShaderSkyTintPercent);

        final IntegerOptionBuilder brightness = builder
            .createIntegerOption(id("incloud_brightness"))
            .setName(Component.literal("Cloud Brightness"))
            .setTooltip(Component.literal(
                "Cloud exposure. Under shaders clouds often look too bright next to the tonemapped terrain; below 100% "
                    + "dims them to match."))
            .setRange(20, 200, 5)
            .setDefaultValue(100)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudShaderBrightnessPercent,
                BetterSimpleCloudsConfig::inCloudShaderBrightnessPercent);

        final IntegerOptionBuilder saturation = builder
            .createIntegerOption(id("incloud_saturation"))
            .setName(Component.literal("Cloud Saturation"))
            .setTooltip(Component.literal(
                "Cloud colour saturation. 100% = unchanged; below greys them out, above deepens the tint."))
            .setRange(0, 200, 10)
            .setDefaultValue(100)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudShaderSaturationPercent,
                BetterSimpleCloudsConfig::inCloudShaderSaturationPercent);

        final IntegerOptionBuilder moonGlow = builder
            .createIntegerOption(id("cloud_moon_glow"))
            .setName(Component.literal("Moonlight Through Clouds"))
            .setTooltip(Component.literal(
                "The silver lining. Cloud droplets scatter light overwhelmingly FORWARDS, which is why a cloud "
                    + "drifting across the moon lights up along its edge instead of just blocking it - Simple Clouds "
                    + "has no notion of the moon, so it goes flat grey instead. Scales itself by moon PHASE (a full "
                    + "moon is ~4.4x a quarter; a new moon gives nothing), altitude, rain and time of day, so it is "
                    + "silent by day and in a storm without tuning."))
            .setRange(0, 200, 5)
            .setDefaultValue(75)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(value -> value == 0 ? Component.literal("Off") : SodiumConfigIntegration.percent(value))
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudMoonGlowPercent,
                BetterSimpleCloudsConfig::cloudMoonGlowPercent);

        final IntegerOptionBuilder moonGlowSharpness = builder
            .createIntegerOption(id("cloud_moon_glow_sharpness"))
            .setName(Component.literal("Moon Glow Tightness"))
            .setTooltip(Component.literal(
                "How tightly the glow hugs the moon, as a scattering asymmetry. 76% is a realistic cloud-droplet "
                    + "value: sharply peaked forward, so the lining reads as a bright RIM on the cloud in front of the "
                    + "moon. Lower spreads it into a broad wash across more of the sky; higher concentrates it into a "
                    + "tighter, more dramatic halo."))
            .setRange(0, 95, 5)
            .setDefaultValue(76)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudMoonGlowSharpnessPercent,
                BetterSimpleCloudsConfig::cloudMoonGlowSharpnessPercent);

        final BooleanOptionBuilder edgeFade = builder
            .createBooleanOption(id("incloud_edge_fade"))
            .setName(Component.literal("Soft Cloud Far Edge"))
            .setTooltip(Component.literal(
                "Fade clouds smoothly into the scene fog colour toward the far render edge so they melt into the "
                    + "horizon instead of hard-cutting. Works with or without shaders."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudFarEdgeFade, BetterSimpleCloudsConfig::inCloudFarEdgeFade);

        final IntegerOptionBuilder edgeFadeRange = builder
            .createIntegerOption(id("incloud_edge_fade_range"))
            .setName(Component.literal("Far Edge Fade Range"))
            .setTooltip(Component.literal(
                "How much of the cloud render distance the far-edge fade covers. 25% = clouds start fading at 75% of "
                    + "the distance and are gone by the edge. Higher = a longer, gentler fade."))
            .setRange(0, 90, 5)
            .setDefaultValue(25)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setInCloudFarEdgeFadePercent,
                BetterSimpleCloudsConfig::inCloudFarEdgeFadePercent);

        final IntegerOptionBuilder fogResist = builder
            .createIntegerOption(id("cloud_fog_resist"))
            .setName(Component.literal("Far Cloud Fog Resistance (exp.)"))
            .setTooltip(Component.literal(
                "EXPERIMENTAL: hold back Simple Clouds' distance-fog wash so far/edge clouds stay vivid instead of "
                    + "fading into the haze. 0% = stock; 100% = clouds keep full colour to the edge. A dial for testing "
                    + "far-cloud looks."))
            .setRange(0, 100, 5)
            .setDefaultValue(0)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setFarCloudFogResistPercent,
                BetterSimpleCloudsConfig::farCloudFogResistPercent);

        final BooleanOptionBuilder cloudFogOwnRange = builder
            .createBooleanOption(id("cloud_fog_own_range"))
            .setName(Component.literal("Cloud Fog Uses Cloud Distance"))
            .setTooltip(Component.literal(
                "Fog the clouds over their own distance instead of the terrain's. Clouds are drawn far past where you "
                    + "can see terrain, so the terrain fog range clamps nearly every cloud to fully fogged and the sky "
                    + "goes one flat colour. This rebuilds the range from the cloud field's own extent so the sky keeps "
                    + "depth. Most noticeable with fog mods that pull terrain fog in hard."))
            .setImpact(OptionImpact.LOW)
            .setDefaultValue(true)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudFogOwnRange,
                BetterSimpleCloudsConfig::cloudFogOwnRange);

        final IntegerOptionBuilder cloudFogStart = builder
            .createIntegerOption(id("cloud_fog_start"))
            .setName(Component.literal("Cloud Fog Start"))
            .setTooltip(Component.literal(
                "Where cloud fog begins, as a percent of the cloud field's extent. The important one: at 0% fog starts "
                    + "at the camera and washes near clouds too, which is what makes the sky look like a single flat "
                    + "colour. Pushing it out keeps near clouds clean so only distant ones haze off."))
            .setRange(0, 95, 5)
            .setDefaultValue(35)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudFogStartPercent,
                BetterSimpleCloudsConfig::cloudFogStartPercent);

        final IntegerOptionBuilder cloudFogEnd = builder
            .createIntegerOption(id("cloud_fog_end"))
            .setName(Component.literal("Cloud Fog End"))
            .setTooltip(Component.literal(
                "Where cloud fog reaches full, as a percent of the cloud field's extent. 100% = a cloud at the very "
                    + "edge is fully fogged out. Lower makes clouds disappear sooner; higher keeps the farthest clouds "
                    + "partly visible."))
            .setRange(10, 100, 5)
            .setDefaultValue(100)
            .setImpact(OptionImpact.LOW)
            .setValueFormatter(SodiumConfigIntegration::percent)
            .setStorageHandler(() -> {})
            .setBinding(BetterSimpleCloudsConfig::setCloudFogEndPercent,
                BetterSimpleCloudsConfig::cloudFogEndPercent);

        // ===================== Pages (each shows as its own category in Reese's) =====================
        final OptionPageBuilder cloudsPage = builder.createOptionPage()
            .setName(Component.literal("Clouds"))
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.literal("Performance"))
                .addOption(optimizeMesh))
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.literal("Far-Cloud Detail"))
                .addOption(maxLod)
                .addOption(bigLodStrength)
                .addOption(smallLodStrength)
                .addOption(smallCull)
                .addOption(minCluster))
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.literal("Storm Fog (Distant Rain)"))
                .addOption(rainBehindClouds)
                .addOption(disableRainBlur))
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.literal("Cloud Fog"))
                .addOption(cloudFogOwnRange)
                .addOption(cloudFogStart)
                .addOption(cloudFogEnd))
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.literal("Transparency"))
                .addOption(fixTransparentEdges)
                .addOption(transparentEdgeSmoothing));

        final OptionPageBuilder inCloudPage = builder.createOptionPage()
            .setName(Component.literal("In-Cloud"))
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.literal("Immersion"))
                .addOption(inCloudEnabled)
                .addOption(solidFaces)
                .addOption(fill)
                .addOption(viewDist)
                .addOption(haze)
                .addOption(shell)
                .addOption(fog)
                .addOption(motes)
                .addOption(vpad))
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.literal("Appearance"))
                .addOption(softTerrain)
                .addOption(nightClouds)
                .addOption(nightBrightness)
                .addOption(moonGlow)
                .addOption(moonGlowSharpness)
                .addOption(shaderMatch)
                .addOption(skyTint)
                .addOption(brightness)
                .addOption(saturation)
                .addOption(edgeFade)
                .addOption(edgeFadeRange)
                .addOption(fogResist));

        builder.registerOwnModOptions()
            .setName("Better Simple Clouds")
            .setNonTintedIcon(ResourceLocation.fromNamespaceAndPath("bettersimpleclouds", "textures/gui/mod_icon.png"))
            .addPage(cloudsPage)
            .addPage(inCloudPage)
            // The non-video options (cloud-type blacklist, debug) live in the mod's own screen; link it from here.
            .addPage(builder.createExternalPage()
                .setName(Component.literal("Spawning & More"))
                .setScreenConsumer(current -> Minecraft.getInstance().setScreen(
                    new BscOptionsScreen(current))));
    }
}
