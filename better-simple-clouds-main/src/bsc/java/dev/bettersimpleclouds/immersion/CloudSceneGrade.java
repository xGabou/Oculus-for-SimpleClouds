package dev.bettersimpleclouds.immersion;

import com.mojang.blaze3d.shaders.AbstractUniform;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;

/**
 * Feeds the "sit nicely in the scene" colour grade - aerial sky tint, exposure, saturation, the soft far edge, and the
 * far-cloud fog resistance - into a cloud shader.
 *
 * <p><b>Both cloud passes must be fed identically.</b> A cloud's opaque body is drawn in one pass and its translucent
 * fringe in another, and the fringe wraps the body: every opaque face is seen <i>through</i> the fringe shell. So any
 * colour treatment applied to one pass and not the other does not just "miss the fringe" - it modulates the body's
 * colour by the fringe's coverage. And the fringe's coverage <i>is</i> the cloud noise field: {@code cube_mesh.comp}
 * emits a fringe cube exactly where the noise sits in {@code (-TransparencyFade, 0)} and derives its alpha from that
 * same noise. The result is the noise field stencilled onto every cloud face as mottling - the "patterns you see inside
 * a cloud" - at a contrast equal to the mismatch between the passes.</p>
 *
 * <p>This is the same invariant {@link CloudNightGrade} documents: grade uniformly, or the grade paints the noise. It
 * is centralised here so the opaque and transparent mixins cannot drift apart - a uniform added to one pass but not the
 * other is precisely the bug.</p>
 *
 * <h2>Why this stopped being invisible: Better Fog</h2>
 *
 * <p>The mismatch was always present but harmless, because both ramps below are keyed off {@code FogEnd} and Simple
 * Clouds sets {@code fogEnd = cloudRenderDistance}, floored at {@code 2867} blocks. Over a 2867-block ramp a cloud a
 * couple of hundred blocks away picks up {@code aerial ~= 0.015} - a few tenths of a percent of tint - so body and
 * fringe agreed to within rounding, and the far clouds where they genuinely diverged were already fogged to
 * {@code FogColor} by then.</p>
 *
 * <p>Better Fog's Simple Clouds compat clamps that: it shadows {@code SimpleCloudsRenderer.fogStart/fogEnd} and, right
 * before {@code setCullDistance}, pulls {@code fogEnd} down to {@code FogEngine.cloudFogEndCap()} so the cloud field
 * ends at its own fog horizon rather than beyond it. That is reasonable on its own terms - but the cap is
 * <b>day/night asymmetric</b>. Its default profile fogs day to {@code 0.9 x} render distance and night to
 * {@code 0.6 x}; the cap formula returns {@code -1} (no clamp) once the fog reaches ~{@code 0.9 x}, so by day nothing
 * happens, while at night {@code fogEnd} collapses from 2867 to ~{@code 0.8 x} render distance (~205 blocks at 16
 * chunks). That is a ~14x compression of both ramps, at night only: {@code aerial} at 200 blocks jumps from ~0.015 to
 * ~1.0, so the configured sky tint applies at nearly <i>full</i> strength to the body across the whole visible cloud
 * field - and the fringe, which had none of it, stencils the noise field over every face. Hence "clouds look weird at
 * night, like noise patterns on faces", only with Better Fog installed, and only after dusk.</p>
 *
 * <p>Feeding both passes the same values fixes it at the root and needs no knowledge of Better Fog: the ramps may be
 * compressed to whatever a fog mod likes, and the body and fringe still agree, so there is nothing for the fringe to
 * stencil. It holds for any mod that legitimately drives Simple Clouds' fog range ({@code distantfog}, Distant Horizons
 * and Simple Clouds' own storm darkening all move it too).</p>
 */
public final class CloudSceneGrade {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CloudSceneGrade() {}

    /**
     * Sets {@code MicSkyColor} / {@code MicSkyTint} / {@code MicBrightness} / {@code MicSaturation} /
     * {@code MicEdgeFade*} / {@code MicFogResist} on the given cloud shader for this frame. Safe to call
     * unconditionally; pushes the no-op defaults when the level is missing or the features are off, so the shader is
     * left behaving exactly like stock Simple Clouds.
     *
     * @param fogEnd the cloud fog end for this pass, as handed to {@code renderCloudsOpaque} /
     *               {@code renderCloudsTransparency}. Both passes are handed the same value (each reads
     *               {@code SimpleCloudsRenderer.getFogEnd()}), including any clamp a fog mod applied - which is what
     *               keeps the two passes in agreement.
     */
    public static void feed(final SingleSSBOShaderInstance shader, final float partialTick, final float fogEnd) {
        final AbstractUniform tint = shader.safeGetUniform("MicSkyTint");
        final AbstractUniform brightness = shader.safeGetUniform("MicBrightness");
        final AbstractUniform saturation = shader.safeGetUniform("MicSaturation");
        final AbstractUniform edge = shader.safeGetUniform("MicEdgeFadeStrength");

        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;

        // Far-cloud fog resistance is independent of shader-match / far-edge-fade, so set it unconditionally; 0 = stock.
        shader.safeGetUniform("MicFogResist").set(BetterSimpleCloudsConfig.farCloudFogResist());


        final boolean match = BetterSimpleCloudsConfig.inCloudShaderMatch();
        final boolean farEdge = BetterSimpleCloudsConfig.inCloudFarEdgeFade();
        if (level == null || mc.gameRenderer == null || (!match && !farEdge)) {
            tint.set(0.0F);          // no-op defaults -> identical to Simple Clouds' own shader
            brightness.set(1.0F);
            saturation.set(1.0F);
            edge.set(0.0F);
            return;
        }

        // The scene sky colour is the blend target for the shader-match tint.
        final Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        final Vec3 sky = level.getSkyColor(cam, partialTick);
        shader.safeGetUniform("MicSkyColor").set((float) sky.x, (float) sky.y, (float) sky.z);

        // Shader-match: tint / exposure / saturation so clouds sit in the lit scene.
        tint.set(match ? BetterSimpleCloudsConfig.inCloudShaderSkyTint() : 0.0F);
        brightness.set(match ? BetterSimpleCloudsConfig.inCloudShaderBrightness() : 1.0F);
        saturation.set(match ? BetterSimpleCloudsConfig.inCloudShaderSaturation() : 1.0F);

        // Far-edge fade: dissolve clouds into the fog colour over the outer part of the fog range (fragment-side, so
        // it's smooth and works under shaders - no scattered geometry). fogEnd is where the cloud field ends: Simple
        // Clouds derives the mesh cull distance from this same value, so the fade always lands on the real horizon even
        // when a fog mod has pulled it in.
        if (farEdge) {
            final float fadeRange = BetterSimpleCloudsConfig.inCloudFarEdgeFadePercent() / 100.0F;
            edge.set(1.0F);
            shader.safeGetUniform("MicEdgeFadeStart").set(fogEnd * (1.0F - fadeRange));
            shader.safeGetUniform("MicEdgeFadeEnd").set(fogEnd);
        } else {
            edge.set(0.0F);
        }
    }

}
