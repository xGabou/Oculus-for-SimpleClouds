package dev.bettersimpleclouds.immersion.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;
import dev.bettersimpleclouds.immersion.CloudMoonGlow;
import dev.bettersimpleclouds.immersion.CloudNightGrade;
import dev.bettersimpleclouds.immersion.CloudSceneGrade;
import dev.bettersimpleclouds.immersion.CloudSoftFade;
import dev.bettersimpleclouds.immersion.InteriorFillState;

import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.shader.SimpleCloudsShaders;
import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;

/**
 * Fixes the mottled, "see the inside and the outside at once" look of a cloud's translucent edges when you view it
 * through another cloud (worst on big clouds) - by flattening the weighted-blended OIT weight in the transparent pass.
 *
 * <p>Simple Clouds blends the soft cloud edges with weighted-blended OIT ({@code clouds_transparency.fsh}), whose
 * per-fragment weight is {@code premul.a * 3000 * pow(1 - z, 3)} - a steep cubic falloff with distance. Across a BIG
 * cloud's thick translucent fringe, a near fringe cube's weight dwarfs a far one's (tens of times over), so the
 * order-independent blend gets dominated per-patch by whichever fringe cube happens to be nearest at each pixel and
 * reads as faces darker/lighter in a noise pattern - the "inside and outside coincide" look. (Small clouds have a thin
 * fringe with little depth spread, so they're unaffected - matching the report that it's only big clouds. Face culling
 * is <i>not</i> the cause: the opaque pass leaves {@code GL_BACK} culling enabled, so the transparent cubes are already
 * single-sided.)</p>
 *
 * <p>Better Simple Clouds swaps in its own copy of the transparency shader ({@link SimpleCloudsShadersMixin}) that softens
 * that exponent toward {@code 0.5} by a {@code MicWeightFlatten} uniform. This mixin feeds that uniform each frame from
 * the config: the smoothing strength when {@link BetterSimpleCloudsConfig#fixTransparentCloudEdges()} is on, else {@code 0}
 * (byte-for-byte stock weighting). Set at the {@code HEAD} of {@code renderCloudsTransparency}, before Simple Clouds
 * applies (uploads) the shader, exactly like {@link CloudShaderMatchMixin} feeds the opaque shader.</p>
 *
 * <p>The fix is held fully on until the camera is half-enveloped and then eased out as it sinks into a cloud, where the
 * interior fill's own translucent haze is meant to read as designed. It is a ramp rather than a threshold on purpose:
 * {@code envelopment} is not a density reading (it is a 2D cloud-region footprint times a camera-Y band test, so it sits
 * near 1.0 anywhere at cloud altitude over a region, including in clear gaps viewed from outside), so an early cut-off
 * switched the fix off precisely where its artifact is visible. This mixin also feeds the soft terrain fade, since the
 * fringe and interior haze are transparent-pass geometry that the opaque pass' fade never reaches. Gated on
 * {@code simpleclouds} (client) via the immersion patch's mixin plugin; {@code remap = false}.</p>
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsRendererTransparencyWeightMixin {

    @Inject(
        method = "renderCloudsTransparency(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;"
            + "Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
        at = @At("HEAD"))
    private static void bsc$feedTransparencyWeight(final CloudMeshGenerator generator, final PoseStack stack,
                                                   final Matrix4f projMat, final float fogStart, final float fogEnd,
                                                   final float partialTick, final float r, final float g, final float b,
                                                   final Frustum frustum, final boolean ditherFade,
                                                   final CallbackInfo ci) {
        final SingleSSBOShaderInstance shader = SimpleCloudsShaders.getCloudsTransparencyShader();
        if (shader == null)
            return;

        // Scene grade + night grade + soft terrain fade: all global for the frame, so set them regardless of the
        // envelopment ramp below. The terrain fade matters here because the interior-fill haze and the soft fringe are
        // transparent-pass geometry - the opaque pass' fade does not reach them.
        //
        // The scene grade MUST be fed here with the same values CloudShaderMatchMixin feeds the opaque pass. The fringe
        // wraps the body, so grading only the body modulates it by fringe alpha - which is the cloud noise field - and
        // stencils that noise across every cloud face. See CloudSceneGrade. Both passes are handed the same fogEnd
        // (each reads SimpleCloudsRenderer.getFogEnd()), so they stay matched even when a fog mod clamps it.
        CloudSceneGrade.feed(shader, partialTick, fogEnd);
        CloudNightGrade.feed(shader, Minecraft.getInstance().level, partialTick);
        CloudMoonGlow.feed(shader, Minecraft.getInstance().level, partialTick);
        CloudSoftFade.feed(shader);

        // Deep inside a cloud the interior fill's own haze cubes are meant to read as designed, so EASE the edge fix out
        // rather than switching it off. This used to be `envelopment > 0.02F -> flatten = 0`, which was a bug on two
        // counts: envelopment is not a density reading (it is a 2D cloud-region footprint times a camera-Y band test, so
        // it pins near 1.0 anywhere at cloud altitude over a region - including in clear gaps while looking at a cloud
        // from outside), and it eases at 0.18/tick so it clears 0.02 on the very first tick. Between them, the edge fix
        // was switched off in one frame exactly where the artifact it exists to fix is visible. Holding it fully on until
        // half-enveloped and easing out over 0.5 -> 0.9 also removes that pop from the outside->inside seam.
        float flatten = BetterSimpleCloudsConfig.transparentEdgeWeightFlatten();
        if (BetterSimpleCloudsConfig.inCloudEnabled())
            flatten *= 1.0F - Mth.clamp((InteriorFillState.envelopment - 0.5F) / 0.4F, 0.0F, 1.0F);
        shader.safeGetUniform("MicWeightFlatten").set(flatten);

        // The extent the OIT weight's depth term is normalized over. Simple Clouds hardcodes 1000 blocks, which only
        // spreads fragments across z if the cloud field is actually that deep; fogEnd is the field's real size (Simple
        // Clouds derives the mesh cull distance from it), so a fog mod that shrinks the field shrinks this with it and
        // the depth term keeps ordering near over far. The shader clamps to 1000, so this is stock at normal field
        // sizes and only engages once the field is genuinely smaller. Not gated on a config: it is a correction that
        // restores Simple Clouds' intended weighting, not a feature.
        shader.safeGetUniform("MicWeightScale").set(fogEnd);
    }
}
