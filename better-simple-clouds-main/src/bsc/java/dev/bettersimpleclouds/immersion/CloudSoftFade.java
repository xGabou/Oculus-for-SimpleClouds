package dev.bettersimpleclouds.immersion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;

import com.mojang.blaze3d.pipeline.RenderTarget;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;

/**
 * Feeds the soft terrain-intersection fade into a cloud shader: the scene depth texture, the fade distance, and the
 * near/far planes needed to linearize that depth. Shared by the opaque and transparent passes so the cloud body and its
 * translucent fringe always fade together (grading only one leaves an un-faded ring around a faded body).
 *
 * <p>Cloud cubes know nothing about the world, so where one cuts into terrain you get a hard polygonal edge and the two
 * near-coincident surfaces z-fight. Fading a cloud out as it approaches the terrain behind it turns the cut into a
 * gradient and stops the fighting, because the cloud contributes nothing exactly where the surfaces coincide.</p>
 *
 * <p><b>near/far are passed explicitly and must never be recovered from {@code ProjMat}.</b> Minecraft post-multiplies
 * the view-bob matrix into the projection, so {@code ProjMat} is {@code P * Bob} and {@code ProjMat[2][2]} is
 * {@code c * cos(bobAngle)} rather than the perspective coefficient {@code c}. Simple Clouds additionally overrides
 * {@code getDepthFar} globally (to {@code cloudAreaMaxRadius * 8}), and at that far plane the reconstruction is so
 * ill-conditioned that the tiny bob term dominates and flips the sign - which deleted every cloud while the player was
 * walking (the bob is only non-zero on the ground, which is why flying looked fine).</p>
 *
 * <p>Only meaningful on {@link CloudsRenderPipeline#SHADER_SUPPORT}, which copies the main framebuffer's depth into the
 * cloud target <i>before</i> drawing clouds - so the world has already rendered and the main target still holds the
 * terrain depth we sample. The default (no-shaders) pipeline draws clouds <i>first</i>, so there is no terrain depth to
 * read and we push {@code 0}. Every failure path sets {@code MicSoftFade = 0}, which makes the shader keep stock solid
 * clouds: an unusable reading must never be able to erase the cloud field.</p>
 */
public final class CloudSoftFade {

    /** {@code GameRenderer.getProjectionMatrix} hardcodes the near plane at 0.05. */
    private static final float NEAR = 0.05F;

    private CloudSoftFade() {}

    /** Sets {@code MicSceneDepth} / {@code MicSoftFade} / {@code MicNear} / {@code MicFar} for this frame. */
    public static void feed(final SingleSSBOShaderInstance shader) {
        final Minecraft mc = Minecraft.getInstance();
        final int fade = BetterSimpleCloudsConfig.softTerrainFadeBlocks();
        final SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
        final boolean shaderPipeline = renderer != null
            && renderer.getRenderPipeline() == CloudsRenderPipeline.SHADER_SUPPORT;
        // Simple Clouds sizes the cloud target from this same helper, so it is the target gl_FragCoord is relative to -
        // our texelFetch has to sample the matching texture or the lookup is offset (matters under Vivecraft).
        final RenderTarget main = SimpleCloudsCompatHelper.getMainRenderTarget();
        // Simple Clouds' MixinGameRenderer hooks getDepthFar's HEAD, so this is the exact far plane that
        // GameRenderer.getProjectionMatrix used to build the ProjMat these clouds are drawn with.
        final float far = mc.gameRenderer != null ? mc.gameRenderer.getDepthFar() : 0.0F;
        // !(far > NEAR) also rejects NaN.
        if (fade <= 0 || !shaderPipeline || main == null || !(far > NEAR)) {
            shader.safeGetUniform("MicSoftFade").set(0.0F);
            return;
        }
        shader.setSampler("MicSceneDepth", main.getDepthTextureId());
        shader.safeGetUniform("MicNear").set(NEAR);
        shader.safeGetUniform("MicFar").set(far);
        shader.safeGetUniform("MicSoftFade").set((float) fade);
    }
}
