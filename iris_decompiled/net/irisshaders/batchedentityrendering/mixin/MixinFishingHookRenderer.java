/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.FishingHookRenderer
 *  net.minecraft.world.entity.projectile.FishingHook
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FishingHookRenderer.class})
public class MixinFishingHookRenderer {
    @Inject(method={"render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal=1)})
    private void capture(FishingHook arg, float f, float g, PoseStack arg2, MultiBufferSource arg3, int i, CallbackInfo ci) {
        VertexConsumer consumer = arg3.m_6299_(RenderType.m_173247_());
        consumer.m_5483_(0.0, 0.0, 0.0).m_6122_(0, 0, 0, 255).m_5601_(0.0f, 0.0f, 0.0f).m_5752_();
    }
}

