package dev.bettersimpleclouds.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Better Simple Clouds' two configs:
 *
 * <ul>
 *   <li><b>CLIENT</b> ({@code bettersimpleclouds-client.toml}) &mdash; every video/visual option. These are surfaced
 *       in Sodium's video settings (and Reese's Sodium Options) via the Sodium config integration, NOT in the mod's
 *       own options screen, so each option has exactly one home.</li>
 *   <li><b>COMMON</b> ({@code bettersimpleclouds-common.toml}) &mdash; gameplay/behaviour options that the
 *       (integrated) server must also read, currently the cloud-type spawn blacklist. These are edited from the
 *       mod's own options screen (Mods &rarr; Better Simple Clouds &rarr; Config).</li>
 * </ul>
 *
 * <p>All accessors guard {@link ModConfigSpec#isLoaded()} and fall back to the default, so they are safe to call
 * before the configs have loaded. Every value sets an explicit {@code translation(...)} key defined in
 * {@code assets/bettersimpleclouds/lang/en_us.json}.</p>
 */
public final class BetterSimpleCloudsConfig {

    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec COMMON_SPEC;

    /** Prefix for every option's display-name translation key (see the lang file). */
    private static final String T = "bettersimpleclouds.config.";

    /** Default in-cloud view distance, in blocks (the opaque shell sits here; vanilla fog ends here). */
    public static final int IN_CLOUD_VIEW_DIST_DEFAULT = 32;

    /** The one cloud type blocked from spawning out of the box (the tiny 'itty bitty' clouds). */
    public static final List<String> DEFAULT_BLOCKED_CLOUD_TYPES = List.of("simpleclouds:itty_bitty");

    // --- COMMON: cloud spawning (server-authoritative in normal play) ---
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_CLOUD_TYPES;

    // --- COMMON: ReTerraForged worldgen guard ---
    private static final ModConfigSpec.BooleanValue RTF_GUARD_EARLY_SAMPLING;

    // --- CLIENT: performance ---
    private static final ModConfigSpec.BooleanValue PERF_CONCURRENT_MESH;

    // --- CLIENT: far-cloud LOD ---
    private static final ModConfigSpec.IntValue LOD_MAX_LEVEL;
    private static final ModConfigSpec.DoubleValue LOD_BIG_STRENGTH;
    private static final ModConfigSpec.DoubleValue LOD_SMALL_STRENGTH;
    private static final ModConfigSpec.DoubleValue LOD_SMALL_CULL;
    private static final ModConfigSpec.IntValue LOD_MIN_NEIGHBORS;

    // --- CLIENT: storm fog (distant rain) ---
    private static final ModConfigSpec.BooleanValue STORM_RAIN_BEHIND_CLOUDS;
    private static final ModConfigSpec.BooleanValue STORM_DISABLE_RAIN_BLUR;

    // --- CLIENT: cloud transparency ---
    private static final ModConfigSpec.BooleanValue FIX_TRANSPARENT_EDGES;
    private static final ModConfigSpec.IntValue TRANSPARENT_EDGE_SMOOTHING;

    // --- CLIENT: in-cloud immersion ---
    private static final ModConfigSpec.BooleanValue IN_CLOUD_ENABLED;
    private static final ModConfigSpec.BooleanValue IN_CLOUD_SOLID;
    private static final ModConfigSpec.BooleanValue IN_CLOUD_FILL_ENABLED;
    private static final ModConfigSpec.IntValue IN_CLOUD_VIEW_DIST;
    private static final ModConfigSpec.DoubleValue IN_CLOUD_HAZE;
    private static final ModConfigSpec.BooleanValue IN_CLOUD_SHELL;
    private static final ModConfigSpec.BooleanValue IN_CLOUD_FOG;
    private static final ModConfigSpec.DoubleValue IN_CLOUD_DENSITY;
    private static final ModConfigSpec.IntValue IN_CLOUD_VPAD;
    private static final ModConfigSpec.BooleanValue IN_CLOUD_DEBUG;

    // --- CLIENT: cloud appearance ---
    private static final ModConfigSpec.IntValue SOFT_TERRAIN_FADE;
    private static final ModConfigSpec.BooleanValue NIGHT_CLOUDS;
    private static final ModConfigSpec.IntValue MOON_GLOW;
    private static final ModConfigSpec.IntValue MOON_GLOW_SHARPNESS;
    private static final ModConfigSpec.IntValue NIGHT_CLOUDS_STRENGTH;
    private static final ModConfigSpec.BooleanValue IN_CLOUD_SHADER_MATCH;
    private static final ModConfigSpec.DoubleValue IN_CLOUD_SHADER_TINT;
    private static final ModConfigSpec.DoubleValue IN_CLOUD_SHADER_BRIGHT;
    private static final ModConfigSpec.DoubleValue IN_CLOUD_SHADER_SAT;
    private static final ModConfigSpec.BooleanValue IN_CLOUD_EDGE_FADE;
    private static final ModConfigSpec.IntValue IN_CLOUD_EDGE_FADE_PCT;
    private static final ModConfigSpec.DoubleValue IN_CLOUD_FOG_RESIST;
    private static final ModConfigSpec.BooleanValue CLOUD_FOG_OWN_RANGE;
    private static final ModConfigSpec.IntValue CLOUD_FOG_START_PCT;
    private static final ModConfigSpec.IntValue CLOUD_FOG_END_PCT;

    static {
        // ================================ COMMON ================================
        final ModConfigSpec.Builder c = new ModConfigSpec.Builder();

        c.comment("Cloud spawning. Read on both sides (the server decides spawning in normal play).")
            .translation(T + "section.spawning")
            .push("spawning");
        BLOCKED_CLOUD_TYPES = c
            .comment("Cloud types that are blocked from EVER spawning, by id (a blacklist - any type from Simple",
                     "Clouds or a datapack can be listed). Blocking works in three layers, all live: (1) Simple",
                     "Clouds' own spawn roll re-rolls to another type, so natural spawn counts are unchanged; (2) the",
                     "region-creation gate refuses a blocked type no matter who requests it, including clouds added",
                     "through the Simple Clouds API by other mods (e.g. Project Atmosphere); (3) a periodic sweep",
                     "(every 40 ticks) removes any blocked region that already exists (e.g. loaded from a save or a",
                     "server without this option). On a server, the server's list is the one that matters in normal",
                     "play. Edit in-game from Mods -> Better Simple Clouds -> Config, where every known cloud type",
                     "can be toggled. Default blocks only the tiny 'itty bitty' clouds.")
            .translation(T + "spawning.blockedCloudTypes")
            .defineListAllowEmpty("blockedCloudTypes", DEFAULT_BLOCKED_CLOUD_TYPES,
                () -> "simpleclouds:", BetterSimpleCloudsConfig::isValidCloudTypeId);
        c.pop();

        c.comment("ReTerraForged worldgen compatibility").translation(T + "section.rtf").push("reterraforged");
        RTF_GUARD_EARLY_SAMPLING = c
            .comment("Guard against a crash where another mod (e.g. ProjectAtmosphere) samples biomes on server start,",
                     "before ReTerraForged's generator context is initialized, causing a NullPointerException in",
                     "ReTerraForged's CellSampler. With this on, those premature samples return a neutral value instead",
                     "of crashing the server; ReTerraForged works normally once worldgen is initialized. Leave on.",
                     "(Only relevant when ReTerraForged is installed; harmless otherwise.)")
            .translation(T + "rtf.guardEarlyBiomeSampling")
            .define("guardEarlyBiomeSampling", true);
        c.pop();

        COMMON_SPEC = c.build();

        // ================================ CLIENT ================================
        final ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.comment("Cloud mesh performance - reduces the cost of Simple Clouds' per-frame cloud mesh generation.")
            .translation(T + "section.performance")
            .push("performance");
        PERF_CONCURRENT_MESH = b
            .comment("EXPERIMENTAL, OFF by default - big FPS gain but GLITCHES BIG CLOUDS. Forces Simple Clouds'",
                     "concurrent (fixed-section) cloud mesh generation. Simple Clouds normally meshes clouds one GPU",
                     "dispatch per chunk with a blocking CPU<->GPU sync after EVERY chunk (its 'Concurrent Compute",
                     "Dispatches' option), which serializes the GPU and can eat most of the frame at HIGH detail. The",
                     "fast path removes those syncs - BUT it gives each cloud chunk a fixed, equal-size mesh slot, and a",
                     "dense chunk in a big/storm cloud needs far more than its share, so it overflows and drops faces at",
                     "random each frame: constant twitching, flattened cloud bottoms, and missing in-cloud interior. This",
                     "is inherent to the fixed-section design (Simple Clouds ships it off for the same reason) and can't",
                     "be fixed by enlarging buffers, so it's off here too. Turn on ONLY if you want raw FPS and your",
                     "clouds are small/uniform. Toggling rebuilds the mesh generator, so you can compare on/off live.")
            .translation(T + "performance.optimizeMeshGeneration")
            .define("optimizeMeshGeneration", false);
        b.pop();

        b.comment("Far-cloud LOD - tame how the cloud mesh coarsens in the distance, so far clouds don't degrade",
                  "into big single cubes.")
            .translation(T + "section.lod")
            .push("lod");
        LOD_MAX_LEVEL = b
            .comment("Cap the cloud LOD level (coarseness): clouds NEVER render coarser than this - the coarser LOD rings",
                     "are rebuilt from more, finer cubes instead, so far clouds keep their shape rather than becoming",
                     "single cubes. This does NOT cull anything; it adds geometry the lower you set it. 0 = off (stock",
                     "LOD, cheapest). 1 = forces the finest, hugely expensive; 2-3 = a good middle ground. Applies live -",
                     "changing it rebuilds the cloud mesh (a brief hitch); no F3+T or restart needed.")
            .translation(T + "lod.maxLodLevel")
            .defineInRange("maxLodLevel", 0, 0, 6);
        LOD_BIG_STRENGTH = b
            .comment("How aggressively LOD softens BIG clouds (the huge, connected/storm masses) with distance - thins",
                     "their wispy edges as they coarsen. 0 = leave big clouds alone (usual choice - they're the dramatic",
                     "ones you want to keep). Higher = even big clouds thin down far away. Never removes a cloud's core,",
                     "so clouds always still render.")
            .translation(T + "lod.bigCloudLodStrength")
            .defineInRange("bigCloudLodStrength", 0.0, 0.0, 1.0);
        LOD_SMALL_STRENGTH = b
            .comment("How aggressively LOD softens SMALL clouds (anything that isn't a huge connected mass, even if",
                     "large in shape) with distance. Raise this to declutter the far sky - small clouds thin out as they",
                     "coarsen instead of littering the horizon - while big clouds (above) stay. Never removes a cloud's",
                     "core, so clouds always still render. Keyed off cloud storminess, so it targets the right clouds.")
            .translation(T + "lod.smallCloudLodStrength")
            .defineInRange("smallCloudLodStrength", 0.0, 0.0, 1.0);
        LOD_SMALL_CULL = b
            .comment("Cull far SMALL clouds outright. Beyond the nearest (full-detail) ring, any cloud whose storminess",
                     "is below this threshold is dropped entirely - the real fix for far small clouds that render as",
                     "single cubes (the LOD cap can't help them: a small cloud is only a few cells, so finer cells are",
                     "still a few cubes). Big storm clouds (storminess at/above the threshold) are untouched. 0 = off;",
                     "~0.3-0.5 removes calm/small clouds far away; 1.0 would remove everything but the stormiest.")
            .translation(T + "lod.farSmallCloudCull")
            .defineInRange("farSmallCloudCull", 0.0, 0.0, 1.0);
        LOD_MIN_NEIGHBORS = b
            .comment("Remove small isolated \"floating\" cloud clusters from far clouds, at ANY LOD-coarsened ring (not",
                     "just the furthest one). A cube is removed only if its WHOLE connected cluster is at most this many",
                     "cells, so a stray single cube or a tiny blob drifting off in an LOD chunk is deleted as a whole,",
                     "while big formations (large connected clusters, including their thin edges and tendrils) are left",
                     "completely intact - this can only ever delete a small floater, never erode or punch holes in a real",
                     "cloud. 0 = off; 1 = remove only single cubes; 2-6 = also remove blobs up to that many cubes (more",
                     "aggressive). Full-detail clouds right around the camera are never touched. Enabling this flood-fills",
                     "far cloud cells - an opt-in mesh-gen cost that grows with the value.")
            .translation(T + "lod.minClusterNeighbors")
            .defineInRange("minClusterNeighbors", 0, 0, 6);
        b.pop();

        b.comment("Storm fog (distant rain) - how Simple Clouds' far rain wall behaves.")
            .translation(T + "section.storm")
            .push("storm");
        STORM_RAIN_BEHIND_CLOUDS = b
            .comment("Make the distant rain (storm fog) render over the CLOUDS behind it - so it stays consistent and the",
                     "shaderpack's distance blur applies to it - while STILL being cut off by real terrain. Simple Clouds'",
                     "far rain is a screen-space effect that stops where the view ray hits the depth it samples, and it",
                     "reads the CLOUD layer's depth: so by default, wherever a cloud sits behind the rain, the blurry rain",
                     "is cut off and disappears. With this on, the rain instead tests against the world (terrain) depth -",
                     "terrain in front still cuts it off (rain only renders over what's actually behind it in the world),",
                     "but clouds no longer do. Only affects the shader render path; applies live. Off = stock Simple Clouds",
                     "(distant rain cut off behind clouds).")
            .translation(T + "storm.rainBehindClouds")
            .define("rainBehindClouds", true);
        STORM_DISABLE_RAIN_BLUR = b
            .comment("Fully disable the blur on Simple Clouds' distant rain (storm fog). The far rain is rendered low-res",
                     "and then run through a box blur that is blended over the WHOLE screen, which smears its softness onto",
                     "the edges of everything - including a near rain mod's close particles (e.g. Pretty Rain). With this on",
                     "that blur pass is skipped entirely, so the distant rain stays crisp and stops bleeding onto the near",
                     "rain. The far rain still renders; only its blur is removed. Applies live.")
            .translation(T + "storm.disableRainBlur")
            .define("disableRainBlur", false);
        b.pop();

        b.comment("Cloud transparency - fix how clouds' translucent edges render.")
            .translation(T + "section.transparency")
            .push("transparency");
        FIX_TRANSPARENT_EDGES = b
            .comment("Fix the mottled, 'inside and outside coincide' look of a cloud's soft translucent edges when you see",
                     "it through another cloud (worst on big clouds). Simple Clouds blends those edges with weighted-blended",
                     "OIT whose per-fragment weight falls off steeply with distance (pow(1-z,3)): across a big cloud's thick",
                     "translucent fringe a NEAR cube's weight dwarfs a FAR cube's, so the order-independent blend is dominated",
                     "per-patch and reads as faces darker/lighter in a noise pattern (like the cloud's inside and outside",
                     "coincide). With this on, Better Simple Clouds swaps in its own copy of the transparency shader that",
                     "flattens that depth weighting (strength below) so near and far fringe contribute evenly and the noise",
                     "clears. Only while the camera is OUTSIDE clouds - the in-cloud interior fill is left on stock weighting.",
                     "Applies live. Off = stock Simple Clouds transparency. (Small clouds have thin fringes, so they already",
                     "look fine either way.)")
            .translation(T + "transparency.fixTransparentEdges")
            .define("fixTransparentEdges", true);
        TRANSPARENT_EDGE_SMOOTHING = b
            .comment("How strongly the transparent-edge fix above flattens the OIT depth weighting, as a percent. 0 = stock",
                     "Simple Clouds weighting (no change even with the fix on); 100 = fully flat, near and far fringe weighted",
                     "the same (smoothest, though very distant fringe then contributes a little more). ~70-90 clears the",
                     "big-cloud noise while keeping some front-to-back ordering. Only matters when the fix is on. Applies live.")
            .translation(T + "transparency.edgeSmoothing")
            .defineInRange("transparentEdgeSmoothing", 80, 0, 100);
        b.pop();

        b.comment("In-cloud immersion - makes flying through a Simple Clouds cloud feel like being inside one: solid",
                  "faces so it isn't see-through, a filled interior that ramps to an opaque view-distance shell (so you",
                  "can't see across a huge cloud), drifting motes, and optional fog.")
            .translation(T + "section.immersion")
            .push("immersion");
        IN_CLOUD_ENABLED = b
            .comment("Master switch for the whole in-cloud immersion effect (interior fill, motes, fog).")
            .translation(T + "immersion.enabled")
            .define("enabled", true);
        IN_CLOUD_SOLID = b
            .comment("Make Simple Clouds generate ALL cloud faces, so clouds aren't see-through from inside or above.",
                     "Simple Clouds normally culls faces pointing away from the camera to save performance, which leaves",
                     "the far side of a cloud (and every face once you're inside) with no geometry - you see straight",
                     "through. This regenerates those faces (near the camera only, so distant clouds don't turn into",
                     "giant cubes). Turn off to keep Simple Clouds' default culling.")
            .translation(T + "immersion.solidCloudFaces")
            .define("solidCloudFaces", true);
        IN_CLOUD_FILL_ENABLED = b
            .comment("Fill a big cloud's otherwise-hollow interior while you're inside it. Simple Clouds emits",
                     "translucent cubes for the solid cells around you that ramp to an OPAQUE shell at the view distance",
                     "below, so you genuinely can't see across a huge cloud (it works under any shaderpack because it's",
                     "real geometry). Only the actual cloud is filled, never open air. Applies live; all the values",
                     "below tune it without a restart.")
            .translation(T + "immersion.interiorFillEnabled")
            .define("interiorFillEnabled", true);
        IN_CLOUD_VIEW_DIST = b
            .comment("How far (in blocks) you can see while inside a cloud. Translucent haze fills the space within this",
                     "distance and an opaque cloud shell sits right at it, cutting off the view like fog underwater. The",
                     "in-cloud fog (below) also uses this distance. Lower = thicker, more enclosed; higher = airier.")
            .translation(T + "immersion.viewDistanceBlocks")
            .defineInRange("viewDistanceBlocks", IN_CLOUD_VIEW_DIST_DEFAULT, 8, 96);
        IN_CLOUD_HAZE = b
            .comment("How thick the interior haze is, per cloud cell. It builds up the further you look (exponential",
                     "fog), so this is really your in-cloud visibility dial: LOWER = see further / clearer, HIGHER =",
                     "murkier / see less. Independent of the view distance above (which only sets where the haze stops",
                     "and the shell sits). The haze shares the cloud's own colour. Try 0.05-0.15 to see well inside.")
            .translation(T + "immersion.interiorHazeOpacity")
            .defineInRange("interiorHazeOpacity", 0.12, 0.0, 1.0);
        IN_CLOUD_SHELL = b
            .comment("Draw a solid opaque cloud wall right at the view distance, so you truly can't see across a huge",
                     "cloud (it also hides distant cloud chunks). Turn OFF for haze only - then how far you see is set",
                     "purely by the haze opacity above, with no hard cut-off (airier, but you may see distant clouds).")
            .translation(T + "immersion.solidViewShell")
            .define("solidViewShell", true);
        IN_CLOUD_FOG = b
            .comment("Also draw real distance fog (cloud-coloured) while the camera is inside a cloud, ending at the",
                     "view distance above. This is what limits the view when shaders are OFF; under an Iris shaderpack",
                     "the pack controls fog and may ignore it, which is why the opaque shell above exists. Only ever",
                     "applied while you're actually inside a cloud.")
            .translation(T + "immersion.fogEnabled")
            .define("fogEnabled", true);
        IN_CLOUD_DENSITY = b
            .comment("Multiplier on how many drifting motes fill the cloud. 1.0 = default; 0 = none. The count still",
                     "scales with the cloud's storminess, so calm clouds stay sparse no matter what this is set to.")
            .translation(T + "immersion.density")
            .defineInRange("motesDensity", 1.0, 0.0, 3.0);
        IN_CLOUD_VPAD = b
            .comment("Soft-edge padding, in blocks, above and below the cloud layer - how gradually the effect fades in",
                     "and out as you cross the top and bottom of the cloud. The layer's real height is detected",
                     "automatically; this only controls the softness of that vertical edge.")
            .translation(T + "immersion.verticalPaddingBlocks")
            .defineInRange("verticalPaddingBlocks", 12, 0, 64);
        IN_CLOUD_DEBUG = b
            .comment("Show a small diagnostic line at the top-left while in a world: the current envelopment and",
                     "storminess, the horizontal/vertical detection factors, your camera Y, the detected cloud layer",
                     "[bottom..top], and the cloud type at the camera. Handy for finding clouds; turn off when happy.")
            .translation(T + "immersion.debugOverlay")
            .define("debugOverlay", false);
        b.pop();

        b.comment("Cloud appearance - how clouds blend into the scene, especially under an Iris shaderpack.")
            .translation(T + "section.appearance")
            .push("appearance");
        SOFT_TERRAIN_FADE = b
            .comment("Soften where clouds meet terrain, in blocks. Simple Clouds' clouds are solid cubes that know nothing",
                     "about the world, so where one cuts into a mountain you get a hard polygonal edge - and because the",
                     "cloud face and the terrain then sit at almost exactly the same depth, they Z-FIGHT: the intersection",
                     "flickers as the depth test flips between cloud and terrain from pixel to pixel and frame to frame.",
                     "With this on, a cloud fades out as it approaches the terrain behind it, so the cut becomes a soft",
                     "gradient AND the flickering stops (the cloud contributes nothing exactly where the two coincide).",
                     "This is the distance over which it fades: 0 = off (stock hard edge); ~8-16 is a natural haze; higher",
                     "makes clouds shy away from terrain more. REQUIRES A SHADERPACK (Iris): only then does Simple Clouds",
                     "render clouds after the world, which is what makes the terrain depth available to fade against. With",
                     "shaders off this does nothing, no matter what it's set to.")
            .translation(T + "appearance.softTerrainFade")
            .defineInRange("softTerrainFadeBlocks", 12, 0, 64);
        NIGHT_CLOUDS = b
            .comment("Make clouds look good at night instead of flat grey blobs. At night Simple Clouds darkens the cloud",
                     "colour to a near-uniform dark grey, which flattens out the subtle per-cube shading that gives a cloud",
                     "its shape - so clouds read as featureless blobs. With this on, the cloud shaders re-open that crushed",
                     "dark range, lift the exposure a little and add a cool moonlit cast, so the cloud's form is visible",
                     "again and it looks moonlit rather than muddy. Only affects night (fades in after dusk, gone by day -",
                     "daytime clouds are untouched), and works with or without a shaderpack. On by default.")
            .translation(T + "appearance.nightClouds")
            .define("betterNightClouds", true);
        NIGHT_CLOUDS_STRENGTH = b
            .comment("How strong the night look is, as a percent. 100 = the default moonlit grade; lower = closer to",
                     "Simple Clouds' darker vanilla night; higher (up to 200) = brighter, more lifted night clouds. 0 = off",
                     "(same as turning the toggle above off). Only matters when Better Night Clouds is on.")
            .translation(T + "appearance.nightCloudsStrength")
            .defineInRange("nightCloudBrightness", 100, 0, 200);
        MOON_GLOW = b
            .comment("Moonlight scattering THROUGH the clouds - the silver lining. Cloud droplets scatter light",
                     "overwhelmingly FORWARDS, which is why a cloud drifting across the moon lights up along its edge",
                     "instead of just blocking it. Simple Clouds has no notion of the moon at all, so a cloud crossing",
                     "a full moon simply goes flat grey. This adds a physically-shaped forward-scattering lobe around",
                     "the moon's direction, so clouds near it glow and clouds away from it do not. It already scales",
                     "itself by moon PHASE (a full moon is ~4.4x a quarter moon, and a new moon gives nothing), by the",
                     "moon's altitude, by rain, and by time of day - so it is silent by day and in a storm without any",
                     "extra tuning. 0 = off.")
            .translation(T + "appearance.cloudMoonGlow")
            .defineInRange("cloudMoonGlow", 75, 0, 200);
        MOON_GLOW_SHARPNESS = b
            .comment("How tightly the moon glow hugs the moon, as a scattering asymmetry percent. 76 is a realistic",
                     "cloud-droplet value: sharply peaked forward, so the lining reads as a bright RIM on the cloud in",
                     "front of the moon. Lower spreads the light into a broader wash over more of the sky; higher",
                     "concentrates it into a tighter, more dramatic halo right at the moon.")
            .translation(T + "appearance.cloudMoonGlowSharpness")
            .defineInRange("cloudMoonGlowSharpness", 76, 0, 95);
        IN_CLOUD_SHADER_MATCH = b
            .comment("Make the clouds blend into a shaderpack-lit scene instead of looking like flat white cut-outs.",
                     "Simple Clouds renders clouds with its own shader (Iris can't run them through the pack), so they",
                     "miss the pack's atmosphere/tonemap. This tints distant clouds toward the scene's sky colour and",
                     "tweaks exposure/saturation (values below). OFF by default so clouds keep their true colour; turn on",
                     "only if you want to experiment - it's an approximation, not true pack rendering.")
            .translation(T + "appearance.cloudShaderMatch")
            .define("cloudShaderMatch", false);
        IN_CLOUD_SHADER_TINT = b
            .comment("How much the clouds are tinted toward the scene's sky/atmosphere colour. 0 = none (pure cloud",
                     "colour); higher = clouds pick up the sky's hue more (better aerial perspective, less flat-white).")
            .translation(T + "appearance.cloudSkyTint")
            .defineInRange("cloudSkyTint", 0.25, 0.0, 1.0);
        IN_CLOUD_SHADER_BRIGHT = b
            .comment("Cloud exposure multiplier. 1.0 = unchanged (true colour). Under shaders, clouds can look too",
                     "bright/washed next to the tonemapped terrain; below 1.0 dims them to match. Above 1.0 brightens.")
            .translation(T + "appearance.cloudBrightness")
            .defineInRange("cloudBrightness", 1.0, 0.2, 2.0);
        IN_CLOUD_SHADER_SAT = b
            .comment("Cloud colour saturation. 1.0 = unchanged; below 1 greys them out, above 1 deepens the tint.")
            .translation(T + "appearance.cloudSaturation")
            .defineInRange("cloudSaturation", 1.0, 0.0, 2.0);
        IN_CLOUD_EDGE_FADE = b
            .comment("Soften the far edge of the cloud field. Simple Clouds fades distant clouds in its own shader, but",
                     "an Iris shaderpack replaces that shader, so the clouds just hard-cut at the render edge. This",
                     "instead thins the cloud geometry to nothing over the last stretch before the edge, so the cloud",
                     "field peters out at the horizon under any shaderpack. Independent of the master switch above.")
            .translation(T + "appearance.farEdgeFade")
            .define("farEdgeFade", true);
        IN_CLOUD_EDGE_FADE_PCT = b
            .comment("How much of the cloud render distance the far-edge fade covers, as a percent. 25 = clouds start",
                     "thinning at 75% of the render distance and are gone by 100%. Higher = a longer, gentler fade;",
                     "lower = clouds stay full further out but the fade is more abrupt.")
            .translation(T + "appearance.farEdgeFadePercent")
            .defineInRange("farEdgeFadePercent", 25, 0, 90);
        IN_CLOUD_FOG_RESIST = b
            .comment("EXPERIMENTAL far-cloud fog resistance. Simple Clouds fades distant clouds into the fog colour,",
                     "which washes out the clouds near the render edge. This holds that fade back so far/edge clouds",
                     "stay vivid. 0 = stock fade; 1.0 = clouds keep their full colour all the way to the edge (can look",
                     "like they don't sit in the haze). A testing dial for improving far clouds.")
            .translation(T + "appearance.farCloudFogResist")
            .defineInRange("farCloudFogResist", 0.0, 0.0, 1.0);
        CLOUD_FOG_OWN_RANGE = b
            .comment("Fog the clouds over their OWN distance instead of the terrain's. The cloud shader is handed the",
                     "terrain fog range, but clouds are drawn over a far bigger area than you can see terrain across -",
                     "so every cloud past the terrain fog end clamps to fully fogged and the whole sky flattens into one",
                     "flat colour with no depth. This replaces that range with the two percentages below, taken from the",
                     "cloud field's own extent, so clouds keep a real near-to-far gradient. Matters most with fog mods",
                     "that pull the terrain fog in hard (Better Fog does, especially at night). Off = stock behaviour.")
            .translation(T + "appearance.cloudFogOwnRange")
            .define("cloudFogOwnRange", true);
        CLOUD_FOG_START_PCT = b
            .comment("Where cloud fog STARTS, as a percent of the cloud field's extent. This is the important one: at 0",
                     "the fog begins at the camera and washes every cloud, near ones included, which is what makes the",
                     "cloud field read as a single flat colour. Pushing the start out keeps near clouds clean and lets",
                     "only the distant ones haze off, which is what gives the sky depth. Must be below the end percent.")
            .translation(T + "appearance.cloudFogStartPercent")
            .defineInRange("cloudFogStartPercent", 35, 0, 95);
        CLOUD_FOG_END_PCT = b
            .comment("Where cloud fog reaches FULL, as a percent of the cloud field's extent. 100 = a cloud at the very",
                     "edge of the field is completely fogged out. Lower pulls the haze inward so clouds disappear",
                     "sooner; higher keeps the farthest clouds partly visible.")
            .translation(T + "appearance.cloudFogEndPercent")
            .defineInRange("cloudFogEndPercent", 100, 10, 100);
        b.pop();

        CLIENT_SPEC = b.build();
    }

    /** @return true if {@code o} is a syntactically valid {@code namespace:path} cloud-type id string. */
    public static boolean isValidCloudTypeId(final Object o) {
        return o instanceof String s && ResourceLocation.tryParse(s) != null;
    }

    // ================================ COMMON accessors ================================

    /**
     * @return the raw blocked cloud-type id strings (may contain ids of types that aren't installed right now).
     *         This is the config's own cached list instance - callers use its identity to invalidate their caches
     *         (see {@code BlockedCloudTypes}) - so do not mutate it.
     */
    public static List<? extends String> blockedCloudTypeIds() {
        return COMMON_SPEC.isLoaded() ? BLOCKED_CLOUD_TYPES.get() : DEFAULT_BLOCKED_CLOUD_TYPES;
    }

    /** Sets and persists the blocked cloud-type id list (used by the options screen's blacklist editor). */
    public static void setBlockedCloudTypeIds(final List<String> ids) {
        BLOCKED_CLOUD_TYPES.set(new ArrayList<>(ids));
        BLOCKED_CLOUD_TYPES.save();
    }

    // --- ReTerraForged (COMMON) ---

    /** @return true if ReTerraForged's CellSampler should return a neutral value (instead of crashing) when its worldgen context isn't ready. */
    public static boolean reterraforgedGuardEarlySampling() {
        return !COMMON_SPEC.isLoaded() || RTF_GUARD_EARLY_SAMPLING.get();
    }

    /** Sets and persists whether ReTerraForged's early biome sampling is guarded. */
    public static void setReterraforgedGuardEarlySampling(final boolean value) {
        RTF_GUARD_EARLY_SAMPLING.set(value);
        RTF_GUARD_EARLY_SAMPLING.save();
    }

    // ================================ CLIENT accessors ================================

    /** @return true if Simple Clouds' concurrent (fixed-section) cloud mesh generation should be forced on (experimental; default off - it glitches big clouds). */
    public static boolean cloudOptimizeMeshGeneration() {
        return CLIENT_SPEC.isLoaded() && PERF_CONCURRENT_MESH.get();
    }

    /** Sets and persists whether the experimental concurrent (fixed-section) cloud mesh generation is forced on. */
    public static void setCloudOptimizeMeshGeneration(final boolean value) {
        PERF_CONCURRENT_MESH.set(value);
        PERF_CONCURRENT_MESH.save();
    }

    /** @return cap on the cloud LOD level / coarseness ({@code 0} = off, no cap); coarser rings render finer instead. */
    public static int cloudMaxLodLevel() {
        return CLIENT_SPEC.isLoaded() ? LOD_MAX_LEVEL.get() : 0;
    }

    /** Sets and persists the cloud LOD-level cap (video-settings slider). */
    public static void setCloudMaxLodLevel(final int level) {
        LOD_MAX_LEVEL.set(Math.max(0, Math.min(6, level)));
        LOD_MAX_LEVEL.save();
    }

    /** @return how aggressively LOD softens big/"huge connected" clouds with distance, {@code [0,1]} ({@code 0} = off). */
    public static float bigCloudLodStrength() {
        return (float) (CLIENT_SPEC.isLoaded() ? LOD_BIG_STRENGTH.get() : 0.0);
    }

    /** @return big-cloud LOD softening as a 0-100 percent (video-settings slider). */
    public static int bigCloudLodPercent() {
        return Math.round(bigCloudLodStrength() * 100.0F);
    }

    /** Sets and persists big-cloud LOD softening from a 0-100 percent (video-settings slider). */
    public static void setBigCloudLodPercent(final int percent) {
        LOD_BIG_STRENGTH.set(Math.max(0, Math.min(100, percent)) / 100.0);
        LOD_BIG_STRENGTH.save();
    }

    /** @return how aggressively LOD softens small clouds with distance, {@code [0,1]} ({@code 0} = off). */
    public static float smallCloudLodStrength() {
        return (float) (CLIENT_SPEC.isLoaded() ? LOD_SMALL_STRENGTH.get() : 0.0);
    }

    /** @return small-cloud LOD softening as a 0-100 percent (video-settings slider). */
    public static int smallCloudLodPercent() {
        return Math.round(smallCloudLodStrength() * 100.0F);
    }

    /** Sets and persists small-cloud LOD softening from a 0-100 percent (video-settings slider). */
    public static void setSmallCloudLodPercent(final int percent) {
        LOD_SMALL_STRENGTH.set(Math.max(0, Math.min(100, percent)) / 100.0);
        LOD_SMALL_STRENGTH.save();
    }

    /** @return storminess threshold below which small clouds are culled at far LOD, {@code [0,1]} ({@code 0} = off). */
    public static float farSmallCloudCull() {
        return (float) (CLIENT_SPEC.isLoaded() ? LOD_SMALL_CULL.get() : 0.0);
    }

    /** @return far small-cloud cull threshold as a 0-100 percent (video-settings slider). */
    public static int farSmallCloudCullPercent() {
        return Math.round(farSmallCloudCull() * 100.0F);
    }

    /** Sets and persists the far small-cloud cull threshold from a 0-100 percent (video-settings slider). */
    public static void setFarSmallCloudCullPercent(final int percent) {
        LOD_SMALL_CULL.set(Math.max(0, Math.min(100, percent)) / 100.0);
        LOD_SMALL_CULL.save();
    }

    /**
     * @return max connected size (in cells) of a floating cloud cluster to remove at far LOD ({@code 0} = off; {@code 1}
     *         = remove only single cubes). Despite the legacy config key/method name, the value is a cluster-size cap,
     *         not a neighbour count - see {@code cube_mesh.comp} {@code micInSmallCluster}.
     */
    public static int cloudMinClusterNeighbors() {
        return CLIENT_SPEC.isLoaded() ? LOD_MIN_NEIGHBORS.get() : 0;
    }

    /** Sets and persists the max connected size (cells) of a floating cloud cluster to remove at far LOD. */
    public static void setCloudMinClusterNeighbors(final int maxClusterCells) {
        LOD_MIN_NEIGHBORS.set(Math.max(0, Math.min(6, maxClusterCells)));
        LOD_MIN_NEIGHBORS.save();
    }

    /** @return true if the distant rain (storm fog) should ignore cloud depth so it stays visible behind clouds (shaders). */
    public static boolean stormFogRainBehindClouds() {
        return !CLIENT_SPEC.isLoaded() || STORM_RAIN_BEHIND_CLOUDS.get();
    }

    /** Sets and persists whether the distant rain stays visible behind clouds. */
    public static void setStormFogRainBehindClouds(final boolean value) {
        STORM_RAIN_BEHIND_CLOUDS.set(value);
        STORM_RAIN_BEHIND_CLOUDS.save();
    }

    /** @return true if Simple Clouds' distant-rain (storm fog) box blur should be skipped entirely. */
    public static boolean disableDistantRainBlur() {
        return CLIENT_SPEC.isLoaded() && STORM_DISABLE_RAIN_BLUR.get();
    }

    /** Sets and persists whether the distant-rain blur is disabled. */
    public static void setDisableDistantRainBlur(final boolean value) {
        STORM_DISABLE_RAIN_BLUR.set(value);
        STORM_DISABLE_RAIN_BLUR.save();
    }

    /** @return true if the transparent cloud pass should use the flattened-OIT-weight shader (fixes the mottled edges). */
    public static boolean fixTransparentCloudEdges() {
        return !CLIENT_SPEC.isLoaded() || FIX_TRANSPARENT_EDGES.get();
    }

    /** Sets and persists whether the transparent cloud edge fix is on. */
    public static void setFixTransparentCloudEdges(final boolean value) {
        FIX_TRANSPARENT_EDGES.set(value);
        FIX_TRANSPARENT_EDGES.save();
    }

    /** @return the transparent-edge OIT weight-flatten strength as a 0-100 percent. */
    public static int transparentEdgeSmoothingPercent() {
        return CLIENT_SPEC.isLoaded() ? TRANSPARENT_EDGE_SMOOTHING.get() : 80;
    }

    /** Sets and persists the transparent-edge smoothing strength from a 0-100 percent. */
    public static void setTransparentEdgeSmoothingPercent(final int percent) {
        TRANSPARENT_EDGE_SMOOTHING.set(Math.max(0, Math.min(100, percent)));
        TRANSPARENT_EDGE_SMOOTHING.save();
    }

    /**
     * @return the effective {@code [0,1]} value fed to the transparency shader's {@code MicWeightFlatten}: the smoothing
     *         percent when the edge fix is on, else {@code 0} (stock Simple Clouds weighting).
     */
    public static float transparentEdgeWeightFlatten() {
        return fixTransparentCloudEdges() ? transparentEdgeSmoothingPercent() / 100.0F : 0.0F;
    }

    // --- In-cloud immersion ---

    /** @return true if the in-cloud immersion effect is enabled at all. */
    public static boolean inCloudEnabled() {
        return !CLIENT_SPEC.isLoaded() || IN_CLOUD_ENABLED.get();
    }

    /** Sets and persists whether the whole in-cloud immersion effect is enabled. */
    public static void setInCloudEnabled(final boolean value) {
        IN_CLOUD_ENABLED.set(value);
        IN_CLOUD_ENABLED.save();
    }

    /** @return true if Simple Clouds should generate all (near-camera) cloud faces so clouds are solid, not see-through. */
    public static boolean inCloudSolidFaces() {
        return !CLIENT_SPEC.isLoaded() || IN_CLOUD_SOLID.get();
    }

    /** Sets and persists whether Simple Clouds generates solid (near-camera) faces. */
    public static void setInCloudSolidFaces(final boolean value) {
        IN_CLOUD_SOLID.set(value);
        IN_CLOUD_SOLID.save();
    }

    /** @return true if a cloud's hollow interior should be filled (haze ramping to an opaque view-distance shell). */
    public static boolean inCloudInteriorFillEnabled() {
        return !CLIENT_SPEC.isLoaded() || IN_CLOUD_FILL_ENABLED.get();
    }

    /** Sets and persists whether the cloud interior is filled. */
    public static void setInCloudInteriorFillEnabled(final boolean value) {
        IN_CLOUD_FILL_ENABLED.set(value);
        IN_CLOUD_FILL_ENABLED.save();
    }

    /** @return how far (blocks) you can see inside a cloud: haze within, opaque shell + fog at this distance. */
    public static int inCloudViewDistanceBlocks() {
        return CLIENT_SPEC.isLoaded() ? IN_CLOUD_VIEW_DIST.get() : IN_CLOUD_VIEW_DIST_DEFAULT;
    }

    /** Sets and persists the in-cloud view distance (blocks), clamped to range. */
    public static void setInCloudViewDistanceBlocks(final int blocks) {
        IN_CLOUD_VIEW_DIST.set(Math.max(8, Math.min(96, blocks)));
        IN_CLOUD_VIEW_DIST.save();
    }

    /** @return constant per-cell opacity of the translucent interior haze (your in-cloud visibility dial). */
    public static float inCloudHazeOpacity() {
        return (float) (CLIENT_SPEC.isLoaded() ? IN_CLOUD_HAZE.get() : 0.12);
    }

    /** @return the interior haze opacity as a 0-100 percent. */
    public static int inCloudHazePercent() {
        return Math.round(inCloudHazeOpacity() * 100.0F);
    }

    /** Sets and persists the interior haze opacity, taken as a 0-100 percent. */
    public static void setInCloudHazePercent(final int percent) {
        IN_CLOUD_HAZE.set(Math.max(0, Math.min(100, percent)) / 100.0);
        IN_CLOUD_HAZE.save();
    }

    /** @return true if the opaque view-distance shell (hard cut-off) should be drawn; false for haze only. */
    public static boolean inCloudSolidViewShell() {
        return !CLIENT_SPEC.isLoaded() || IN_CLOUD_SHELL.get();
    }

    /** Sets and persists whether the opaque view-distance shell is drawn. */
    public static void setInCloudSolidViewShell(final boolean value) {
        IN_CLOUD_SHELL.set(value);
        IN_CLOUD_SHELL.save();
    }

    /** @return true if real cloud-coloured distance fog should also be drawn while inside a cloud (mainly for no-shaders). */
    public static boolean inCloudFogEnabled() {
        return !CLIENT_SPEC.isLoaded() || IN_CLOUD_FOG.get();
    }

    /** Sets and persists whether in-cloud fog is drawn. */
    public static void setInCloudFogEnabled(final boolean value) {
        IN_CLOUD_FOG.set(value);
        IN_CLOUD_FOG.save();
    }

    /** @return multiplier on the in-cloud mote count (still scaled by storminess on top of this). */
    public static float inCloudDensity() {
        return (float) (CLIENT_SPEC.isLoaded() ? IN_CLOUD_DENSITY.get() : 1.0);
    }

    /** @return in-cloud mote density as a 0-300 percent. */
    public static int inCloudDensityPercent() {
        return Math.round(inCloudDensity() * 100.0F);
    }

    /** Sets and persists in-cloud mote density from a 0-300 percent. */
    public static void setInCloudDensityPercent(final int percent) {
        IN_CLOUD_DENSITY.set(Math.max(0, Math.min(300, percent)) / 100.0);
        IN_CLOUD_DENSITY.save();
    }

    /** @return soft-edge padding in blocks above and below the (auto-detected) cloud layer. */
    public static int inCloudVerticalPaddingBlocks() {
        return CLIENT_SPEC.isLoaded() ? IN_CLOUD_VPAD.get() : 12;
    }

    /** Sets and persists the in-cloud vertical edge padding (blocks). */
    public static void setInCloudVerticalPaddingBlocks(final int blocks) {
        IN_CLOUD_VPAD.set(Math.max(0, Math.min(64, blocks)));
        IN_CLOUD_VPAD.save();
    }

    /** @return true if the in-cloud diagnostic overlay should be shown. */
    public static boolean inCloudDebugOverlay() {
        return CLIENT_SPEC.isLoaded() && IN_CLOUD_DEBUG.get();
    }

    /** Sets and persists whether the in-cloud diagnostic overlay is shown. */
    public static void setInCloudDebugOverlay(final boolean value) {
        IN_CLOUD_DEBUG.set(value);
        IN_CLOUD_DEBUG.save();
    }

    // --- Cloud appearance ---

    /**
     * @return distance in blocks over which clouds fade out as they approach terrain ({@code 0} = off / stock hard
     *         edge). Fed to the cloud shader's {@code MicSoftFade}; only meaningful on the shader-support pipeline.
     */
    public static int softTerrainFadeBlocks() {
        return CLIENT_SPEC.isLoaded() ? SOFT_TERRAIN_FADE.get() : 12;
    }

    /** Sets and persists the soft terrain-intersection fade distance, in blocks. */
    public static void setSoftTerrainFadeBlocks(final int blocks) {
        SOFT_TERRAIN_FADE.set(Math.max(0, Math.min(64, blocks)));
        SOFT_TERRAIN_FADE.save();
    }

    /** @return true if clouds should get the night grade (moonlit lift) instead of looking like flat dark grey blobs. */
    public static boolean cloudNightBoost() {
        return !CLIENT_SPEC.isLoaded() || NIGHT_CLOUDS.get();
    }

    /** Sets and persists whether the night grade is applied to clouds. */
    public static void setCloudNightBoost(final boolean value) {
        NIGHT_CLOUDS.set(value);
        NIGHT_CLOUDS.save();
    }

    /** @return the night-grade strength as a {@code [0,2]} multiplier fed to the shader ({@code MicNightStrength}). */
    public static float cloudNightBoostStrength() {
        return (float) (CLIENT_SPEC.isLoaded() ? NIGHT_CLOUDS_STRENGTH.get() : 100) / 100.0F;
    }

    /** @return the night-grade strength as a 0-200 percent (video-settings slider). */
    public static int cloudNightBoostPercent() {
        return CLIENT_SPEC.isLoaded() ? NIGHT_CLOUDS_STRENGTH.get() : 100;
    }

    /** Sets and persists the night-grade strength from a 0-200 percent (video-settings slider). */
    public static void setCloudNightBoostPercent(final int percent) {
        NIGHT_CLOUDS_STRENGTH.set(Math.max(0, Math.min(200, percent)));
        NIGHT_CLOUDS_STRENGTH.save();
    }

    /** @return true if clouds should be nudged to blend into a shaderpack-lit scene (sky tint / exposure). */
    public static boolean inCloudShaderMatch() {
        return CLIENT_SPEC.isLoaded() && IN_CLOUD_SHADER_MATCH.get();
    }

    /** Sets and persists whether clouds are matched to the shader-lit scene. */
    public static void setInCloudShaderMatch(final boolean value) {
        IN_CLOUD_SHADER_MATCH.set(value);
        IN_CLOUD_SHADER_MATCH.save();
    }

    /** @return how much clouds are tinted toward the scene sky colour, {@code [0,1]}. */
    public static float inCloudShaderSkyTint() {
        return (float) (CLIENT_SPEC.isLoaded() ? IN_CLOUD_SHADER_TINT.get() : 0.25);
    }

    /** @return cloud sky-tint as a 0-100 percent. */
    public static int inCloudShaderSkyTintPercent() {
        return Math.round(inCloudShaderSkyTint() * 100.0F);
    }

    /** Sets and persists the cloud sky-tint from a 0-100 percent. */
    public static void setInCloudShaderSkyTintPercent(final int percent) {
        IN_CLOUD_SHADER_TINT.set(Math.max(0, Math.min(100, percent)) / 100.0);
        IN_CLOUD_SHADER_TINT.save();
    }

    /** @return cloud exposure multiplier (below 1 dims clouds to match tonemapped terrain). */
    public static float inCloudShaderBrightness() {
        return (float) (CLIENT_SPEC.isLoaded() ? IN_CLOUD_SHADER_BRIGHT.get() : 1.0);
    }

    /** @return cloud brightness as a 20-200 percent. */
    public static int inCloudShaderBrightnessPercent() {
        return Math.round(inCloudShaderBrightness() * 100.0F);
    }

    /** Sets and persists the cloud brightness from a 20-200 percent. */
    public static void setInCloudShaderBrightnessPercent(final int percent) {
        IN_CLOUD_SHADER_BRIGHT.set(Math.max(20, Math.min(200, percent)) / 100.0);
        IN_CLOUD_SHADER_BRIGHT.save();
    }

    /** @return cloud colour saturation multiplier (1 = unchanged). */
    public static float inCloudShaderSaturation() {
        return (float) (CLIENT_SPEC.isLoaded() ? IN_CLOUD_SHADER_SAT.get() : 1.0);
    }

    /** @return cloud saturation as a 0-200 percent. */
    public static int inCloudShaderSaturationPercent() {
        return Math.round(inCloudShaderSaturation() * 100.0F);
    }

    /** Sets and persists the cloud saturation from a 0-200 percent. */
    public static void setInCloudShaderSaturationPercent(final int percent) {
        IN_CLOUD_SHADER_SAT.set(Math.max(0, Math.min(200, percent)) / 100.0);
        IN_CLOUD_SHADER_SAT.save();
    }

    /** @return true if the cloud field should thin out toward the far render edge (works under any shaderpack). */
    public static boolean inCloudFarEdgeFade() {
        return !CLIENT_SPEC.isLoaded() || IN_CLOUD_EDGE_FADE.get();
    }

    /** Sets and persists whether the cloud field thins out at the far render edge. */
    public static void setInCloudFarEdgeFade(final boolean value) {
        IN_CLOUD_EDGE_FADE.set(value);
        IN_CLOUD_EDGE_FADE.save();
    }

    /** @return percent of the cloud render distance over which the far-edge fade ramps clouds out. */
    public static int inCloudFarEdgeFadePercent() {
        return CLIENT_SPEC.isLoaded() ? IN_CLOUD_EDGE_FADE_PCT.get() : 25;
    }

    /** Sets and persists the far-edge fade range percent. */
    public static void setInCloudFarEdgeFadePercent(final int percent) {
        IN_CLOUD_EDGE_FADE_PCT.set(Math.max(0, Math.min(90, percent)));
        IN_CLOUD_EDGE_FADE_PCT.save();
    }

    /** @return far-cloud fog resistance, {@code [0,1]} ({@code 0} = stock fog fade; higher keeps far clouds vivid). */
    public static float farCloudFogResist() {
        return (float) (CLIENT_SPEC.isLoaded() ? IN_CLOUD_FOG_RESIST.get() : 0.0);
    }

    /** @return far-cloud fog resistance as a 0-100 percent. */
    public static int farCloudFogResistPercent() {
        return Math.round(farCloudFogResist() * 100.0F);
    }

    /** Sets and persists far-cloud fog resistance from a 0-100 percent. */
    public static void setFarCloudFogResistPercent(final int percent) {
        IN_CLOUD_FOG_RESIST.set(Math.max(0, Math.min(100, percent)) / 100.0);
        IN_CLOUD_FOG_RESIST.save();
    }

    /** @return the moon-glow (forward scattering) master strength, {@code 0} = off. */
    public static float cloudMoonGlowStrength() {
        return (CLIENT_SPEC.isLoaded() ? MOON_GLOW.get() : 75) / 100.0F;
    }

    /** @return the moon glow strength as a 0-200 percent. */
    public static int cloudMoonGlowPercent() {
        return CLIENT_SPEC.isLoaded() ? MOON_GLOW.get() : 75;
    }

    /** Sets and persists the moon glow strength from a 0-200 percent. */
    public static void setCloudMoonGlowPercent(final int percent) {
        MOON_GLOW.set(Math.max(0, Math.min(200, percent)));
        MOON_GLOW.save();
    }

    /** @return the scattering asymmetry {@code g}, {@code [0,0.95]} - how tightly the glow hugs the moon. */
    public static float cloudMoonGlowSharpness() {
        return (CLIENT_SPEC.isLoaded() ? MOON_GLOW_SHARPNESS.get() : 76) / 100.0F;
    }

    /** @return the glow sharpness as a 0-95 percent. */
    public static int cloudMoonGlowSharpnessPercent() {
        return CLIENT_SPEC.isLoaded() ? MOON_GLOW_SHARPNESS.get() : 76;
    }

    /** Sets and persists the glow sharpness from a 0-95 percent. */
    public static void setCloudMoonGlowSharpnessPercent(final int percent) {
        MOON_GLOW_SHARPNESS.set(Math.max(0, Math.min(95, percent)));
        MOON_GLOW_SHARPNESS.save();
    }

    private BetterSimpleCloudsConfig() {}

    /** @return true if clouds should be fogged over their own extent rather than the terrain fog range. */
    public static boolean cloudFogOwnRange() {
        return !CLIENT_SPEC.isLoaded() || CLOUD_FOG_OWN_RANGE.get();
    }

    /** Sets and persists whether cloud fog uses its own range. */
    public static void setCloudFogOwnRange(final boolean value) {
        CLOUD_FOG_OWN_RANGE.set(value);
        CLOUD_FOG_OWN_RANGE.save();
    }

    /** @return where cloud fog starts, as a percent of the cloud field's extent. */
    public static int cloudFogStartPercent() {
        return CLIENT_SPEC.isLoaded() ? CLOUD_FOG_START_PCT.get() : 35;
    }

    /** Sets and persists the cloud-fog start percent. */
    public static void setCloudFogStartPercent(final int percent) {
        CLOUD_FOG_START_PCT.set(Math.max(0, Math.min(95, percent)));
        CLOUD_FOG_START_PCT.save();
    }

    /** @return where cloud fog reaches full, as a percent of the cloud field's extent. */
    public static int cloudFogEndPercent() {
        return CLIENT_SPEC.isLoaded() ? CLOUD_FOG_END_PCT.get() : 100;
    }

    /** Sets and persists the cloud-fog end percent. */
    public static void setCloudFogEndPercent(final int percent) {
        CLOUD_FOG_END_PCT.set(Math.max(10, Math.min(100, percent)));
        CLOUD_FOG_END_PCT.save();
    }
}
