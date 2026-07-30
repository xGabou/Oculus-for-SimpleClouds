package dev.bettersimpleclouds.immersion.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;
import dev.bettersimpleclouds.immersion.InteriorFillState;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;

/**
 * Makes Simple Clouds' distant rain (the "storm fog") render <b>over the clouds behind it</b> - while still being cut
 * off by real terrain - so it stays consistent and the shaderpack's distance blur applies to it.
 *
 * <p>The storm fog is a screen-space ray-march ({@code storm_fog.fsh}) that samples a depth texture and stops the march
 * as soon as the ray passes scene geometry ({@code if (sceneDepth < rayDepth) break;}). Simple Clouds binds the
 * <b>cloud target's</b> depth to that sampler. By the time the storm fog runs, that depth holds the clouds Simple
 * Clouds just drew (and, with "clouds behind LOD" on, the LOD terrain too), so the rain march terminates at whatever
 * sits behind it and the blurry rain only shows where there's open sky behind - wherever a cloud sits behind the rain,
 * the rain (and the shader blur on it) disappears.</p>
 *
 * <p>So right before the storm fog samples that depth, this copies the <b>main (world) depth</b> - which holds the real
 * terrain but <i>not</i> the clouds (clouds render to a separate target) - into the cloud target. The ray-march then
 * still stops at real terrain in front of the rain (so the rain only renders over what's actually behind it in the
 * world), but the clouds no longer cut it off, so distant rain renders over the clouds behind it and the pack's
 * distance blur applies to it. Uses Simple Clouds' own {@link SimpleCloudsRenderer#copyDepthFromMainToClouds()}, which
 * is stencil- and GL-error-safe with a fallback flag. Overwriting the cloud target's depth here is safe: the cloud
 * colour was already composited to the main buffer, the storm fog is the last thing in the cloud pass, and the target
 * is rebuilt next frame.</p>
 *
 * <p>Runs only on the {@link CloudsRenderPipeline#SHADER_SUPPORT} pipeline (Iris, and the one our Voxy patch forces).
 * Gated on {@code simpleclouds} (in-cloud immersion mixin config) and the {@code simpleclouds_storm.rainBehindClouds}
 * config. Off = stock Simple Clouds (rain cut off behind clouds).</p>
 *
 * <p><b>In-cloud exception:</b> while the camera is inside a cloud, the in-cloud immersion fill (haze / opaque shell)
 * is <i>real cloud geometry</i> drawn to the cloud target, so it holds the depth that keeps the distant rain out of
 * the interior you're standing in. If we overwrote that with the world depth here, the far rain would depth-test only
 * against terrain and draw straight over the immersion fill/shell. So when {@link InteriorFillState#envelopment
 * enveloped} (immersion on and the camera inside a cloud), we skip the copy and let the rain test against the cloud
 * depth again - the fill/shell then occludes it. Outside a cloud, nothing changes: rain still renders behind clouds.</p>
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class StormFogRainBehindCloudsMixin {

    @Shadow
    public abstract CloudsRenderPipeline getRenderPipeline();

    @Shadow
    public abstract void copyDepthFromMainToClouds();

    private static final Logger BSC_LOGGER = LogUtils.getLogger();
    private static boolean bsc$logged;

    @Inject(method = "doStormPostProcessing", at = @At("HEAD"))
    private void bsc$rainBehindClouds(final Matrix4f camMat, final float partialTick, final Matrix4f projMat,
                                      final double camX, final double camY, final double camZ,
                                      final float r, final float g, final float b, final CallbackInfo ci) {
        if (!BetterSimpleCloudsConfig.stormFogRainBehindClouds())
            return;
        if (this.getRenderPipeline() != CloudsRenderPipeline.SHADER_SUPPORT)
            return;
        // Inside a cloud, the in-cloud immersion fill/shell is cloud geometry that must keep the far rain out of the
        // interior. Overwriting the cloud depth here would let the rain draw over it, so skip the copy while enveloped
        // (envelopment is only > 0 when in-cloud immersion is enabled and the camera is inside a cloud). The rain then
        // depth-tests against the cloud target (fill/shell included) and is occluded, exactly as it should be.
        if (BetterSimpleCloudsConfig.inCloudEnabled() && InteriorFillState.envelopment > 0.02F)
            return;
        // Put the world (terrain, no clouds) depth into the cloud target, whose depth the storm-fog ray-march samples.
        // Terrain in front then still cuts the rain off (it only renders over what's actually behind it in the world),
        // but clouds no longer do - so distant rain renders over the clouds behind it and the shaderpack's distance
        // blur applies to it. Simple Clouds' own copy is stencil-/GL-error-safe.
        this.copyDepthFromMainToClouds();
        if (!bsc$logged) {
            bsc$logged = true;
            BSC_LOGGER.info("[Better Simple Clouds] Storm-fog rain-behind-clouds active: rain depth-tests against world "
                + "terrain (renders over clouds, still occluded by terrain).");
        }
    }
}
