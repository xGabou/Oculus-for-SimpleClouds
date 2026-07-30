package dev.bettersimpleclouds.immersion;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.client.renderer.ShaderInstance;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

/**
 * Replaces the fog range on every cloud shader with one built from the cloud field's own extent, so clouds fade over
 * the distance they are actually drawn across instead of the terrain's.
 *
 * <h2>Why the terrain range is wrong for clouds</h2>
 *
 * <p>Simple Clouds hands its cloud shaders the ordinary terrain fog. Clouds, though, are drawn several times further
 * out than terrain is visible &mdash; and further still when a mod drives the cloud distance from a LOD renderer's.
 * Everything past the terrain fog end therefore clamps to fully fogged, and the cloudscape collapses into one flat
 * sheet of fog colour. It is worst exactly when the terrain fog is shortest: after dark, where fog mods routinely use
 * much nearer night distances, every cloud past the vanilla render distance disappears.</p>
 *
 * <h2>Where this is applied</h2>
 *
 * <p>Hooked on {@code SimpleCloudsRenderer.prepareShader}, which is the single place Simple Clouds pushes the fog
 * uniforms and is called for <em>every</em> cloud shader &mdash; the opaque pass, the transparency pass, the shadow
 * map and the remaining passes &mdash; under either cloud pipeline. An earlier attempt hooked the opaque render call
 * instead, which only runs on one of the two pipelines, so it silently did nothing wherever the other was in force.</p>
 *
 * <p>Overwriting the stock {@code FOG_START} / {@code FOG_END} uniforms (rather than feeding separate ones the shader
 * has to opt into) means this also works when Simple Clouds' own shaders are in use rather than this mod's copies.</p>
 */
public final class CloudFogRange {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static volatile boolean logged;

    /**
     * Overwrites {@code shader}'s fog range with the cloud-relative one.
     *
     * @param fogEnd the cloud fog end Simple Clouds computed for this pass &mdash; already in cloud scale, so the
     *               configured percentages are taken straight from it.
     */
    public static void feed(final ShaderInstance shader, final float fogEnd) {
        if (shader == null || !BetterSimpleCloudsConfig.cloudFogOwnRange())
            return;
        if (!Float.isFinite(fogEnd) || fogEnd <= 0.0F)
            return;

        final float newStart = fogEnd * (BetterSimpleCloudsConfig.cloudFogStartPercent() / 100.0F);
        final float newEnd = fogEnd * (BetterSimpleCloudsConfig.cloudFogEndPercent() / 100.0F);
        // A zero-width or inverted range makes the fog step instantly; leave the stock values rather than band.
        if (!(newEnd > newStart))
            return;

        // Written in two places on purpose, because the stock uniforms alone are not enough.
        //
        // Vanilla's ShaderInstance.setDefaultUniforms re-reads FogStart/FogEnd straight from the render system just
        // before drawing, so anything written here into the stock uniforms is silently replaced by the TERRAIN fog by
        // the time the clouds are actually drawn - which is exactly why setting only those looked correct in the log
        // and changed nothing on screen. MicCloudFogStart/MicCloudFogEnd are this mod's own uniforms; vanilla knows
        // nothing about them, so they survive to the draw, and this mod's cloud shaders prefer them whenever
        // MicCloudFogEnd is above zero.
        shader.safeGetUniform("MicCloudFogStart").set(newStart);
        shader.safeGetUniform("MicCloudFogEnd").set(newEnd);

        // Still set the stock ones for any pass running a shader without our uniforms (Simple Clouds' own copies, the
        // shadow map): harmless where they are overwritten, correct where they are not.
        final Uniform start = shader.FOG_START;
        final Uniform end = shader.FOG_END;
        if (start != null)
            start.set(newStart);
        if (end != null)
            end.set(newEnd);

        if (!logged) {
            logged = true;
            LOGGER.info("[Better Simple Clouds] cloud fog range {}..{} blocks ({}%..{}% of the {}-block cloud extent) "
                + "- clouds now fade over their own distance, not the terrain's.",
                Math.round(newStart), Math.round(newEnd),
                BetterSimpleCloudsConfig.cloudFogStartPercent(), BetterSimpleCloudsConfig.cloudFogEndPercent(),
                Math.round(fogEnd));
        }
    }

    private CloudFogRange() {}
}
