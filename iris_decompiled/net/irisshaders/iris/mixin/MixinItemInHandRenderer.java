/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.ItemInHandRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.pathways.HandRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemInHandRenderer.class})
public class MixinItemInHandRenderer {
    @Inject(method={"renderArmWithItem"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$skipTranslucentHands(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            if (HandRenderer.INSTANCE.isRenderingSolid() && HandRenderer.INSTANCE.isHandTranslucent(interactionHand)) {
                ci.cancel();
            } else if (!HandRenderer.INSTANCE.isRenderingSolid() && !HandRenderer.INSTANCE.isHandTranslucent(interactionHand)) {
                ci.cancel();
            }
        }
    }
}

