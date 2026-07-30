package dev.bettersimpleclouds.immersion.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;

import dev.bettersimpleclouds.immersion.CloudMoonGlow;
import dev.bettersimpleclouds.immersion.CloudNightGrade;
import dev.bettersimpleclouds.immersion.CloudSceneGrade;
import dev.bettersimpleclouds.immersion.CloudSoftFade;

import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.shader.SimpleCloudsShaders;
import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;

/**
 * Feeds the "match the shader-lit scene" parameters into the cloud fragment shader each frame, so opaque clouds blend
 * into the scene under an Iris shaderpack instead of looking like flat white cut-outs. Works because Simple Clouds
 * draws its clouds with its own shader (Iris doesn't replace that custom instanced draw), and we've swapped the
 * fragment stage for ours via {@link SimpleCloudsShadersMixin}.
 *
 * <p>Set at the {@code HEAD} of {@code renderCloudsOpaque}, before Simple Clouds applies the shader (which uploads the
 * uniforms). The values themselves come from {@link CloudSceneGrade} / {@link CloudNightGrade} / {@link CloudSoftFade},
 * which {@link SimpleCloudsRendererTransparencyWeightMixin} also calls at the head of the transparent pass - so the
 * cloud body and its translucent fringe are always graded identically. That is not tidiness: the fringe wraps the body,
 * so a uniform fed to one pass and not the other modulates the body by the fringe's coverage, which is the cloud noise
 * field, and paints that noise across every cloud face. See {@link CloudSceneGrade}.</p>
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class CloudShaderMatchMixin {

    @Inject(
        method = "renderCloudsOpaque(Ldev/nonamecrackers2/simpleclouds/client/mesh/generator/CloudMeshGenerator;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FFFFFFLnet/minecraft/client/renderer/culling/Frustum;Z)V",
        at = @At("HEAD"))
    private static void bsc$feedCloudShaderMatch(final CloudMeshGenerator generator, final PoseStack stack,
                                                 final Matrix4f projMat, final float fogStart, final float fogEnd,
                                                 final float partialTick, final float r, final float g, final float b,
                                                 final Frustum frustum, final boolean ditherFade,
                                                 final CallbackInfo ci) {
        final SingleSSBOShaderInstance shader = SimpleCloudsShaders.getCloudsShader();
        if (shader == null)
            return;
        // Every one of these is fed to the transparent pass too, with the same values - see CloudSceneGrade.
        CloudSceneGrade.feed(shader, partialTick, fogEnd);
        CloudNightGrade.feed(shader, Minecraft.getInstance().level, partialTick);
        CloudMoonGlow.feed(shader, Minecraft.getInstance().level, partialTick);
        CloudSoftFade.feed(shader);
    }

}
