package dev.bettersimpleclouds.immersion;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;

/**
 * Applies the cloud-relative fog range to Simple Clouds' <b>screen-space world fog</b> pass, the second place cloud fog
 * is decided.
 *
 * <p>{@link CloudSceneGrade} rebuilds the range for the cloud <em>shaders</em>, but that is only half the story:
 * {@code SimpleCloudsRenderer.doScreenSpaceWorldFog} runs a post-pass over the finished cloud buffer and feeds it the
 * fog uniforms straight from the render system &mdash;</p>
 *
 * <pre>
 * effect.safeGetUniform("FogStart").set(RenderSystem.getShaderFogStart());
 * effect.safeGetUniform("FogEnd").set(RenderSystem.getShaderFogEnd());
 * </pre>
 *
 * <p>&mdash; i.e. the <em>terrain</em> fog. So however carefully the cloud shaders fog the clouds, this pass then fogs
 * them again over the terrain's much shorter range. It goes unnoticed by day, when that range is long enough to be
 * nearly transparent out where the clouds are, and becomes obvious at night: fog mods routinely pull the terrain fog
 * right in after dark (Better Fog has separate, much shorter night distances), and every cloud past the vanilla render
 * distance is then buried.</p>
 *
 * <p>Swapping the render-system fog for the cloud-relative range for the duration of that one call &mdash; and putting
 * it straight back &mdash; makes the pass agree with the cloud shaders without touching the terrain fog, which is
 * already finished being drawn by the time clouds render.</p>
 */
public final class CloudWorldFogRange {

    private static float savedStart;
    private static float savedEnd;
    private static boolean active;

    private static final Logger LOGGER = LogUtils.getLogger();
    private static volatile boolean logged;

    /**
     * Swaps in the cloud-relative fog range. Must be paired with {@link #pop()}.
     *
     * <p>Deliberately not re-entrant: a nested push would capture already-swapped values as the "original" and the
     * restore would leave the terrain fog wrong for the rest of the frame.</p>
     */
    public static void push() {
        if (active || !BetterSimpleCloudsConfig.cloudFogOwnRange())
            return;

        final SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
        if (renderer == null)
            return;

        // Simple Clouds' own cloud fog end - the extent the cloud field actually spans.
        final float extent = renderer.getFogEnd();
        if (!Float.isFinite(extent) || extent <= 0.0F)
            return;

        final float start = extent * (BetterSimpleCloudsConfig.cloudFogStartPercent() / 100.0F);
        final float end = extent * (BetterSimpleCloudsConfig.cloudFogEndPercent() / 100.0F);
        if (!(end > start))
            return; // a zero-width range would make the pass step instantly; leave it stock

        savedStart = RenderSystem.getShaderFogStart();
        savedEnd = RenderSystem.getShaderFogEnd();
        active = true;

        RenderSystem.setShaderFogStart(start);
        RenderSystem.setShaderFogEnd(end);

        if (!logged) {
            logged = true;
            LOGGER.info("[Better Simple Clouds] screen-space cloud fog: terrain {}..{} -> cloud {}..{} blocks "
                + "(extent {}, {}%..{}%)", Math.round(savedStart), Math.round(savedEnd),
                Math.round(start), Math.round(end), Math.round(extent),
                BetterSimpleCloudsConfig.cloudFogStartPercent(), BetterSimpleCloudsConfig.cloudFogEndPercent());
        }
    }

    /** Restores the terrain fog. Safe to call when {@link #push()} did nothing. */
    public static void pop() {
        if (!active)
            return;
        active = false;
        RenderSystem.setShaderFogStart(savedStart);
        RenderSystem.setShaderFogEnd(savedEnd);
    }

    private CloudWorldFogRange() {}
}
