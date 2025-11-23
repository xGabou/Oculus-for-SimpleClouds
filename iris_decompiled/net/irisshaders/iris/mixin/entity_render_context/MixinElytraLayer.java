/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.ElytraLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.PlayerModelPart
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={ElytraLayer.class})
public abstract class MixinElytraLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    @Unique
    private static final NamespacedId ELYTRA_CAPE_LOCATION = new NamespacedId("minecraft", "elytra_with_cape");

    public MixinElytraLayer(RenderLayerParent<T, M> pRenderLayer0) {
        super(pRenderLayer0);
    }

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void changeId(PoseStack pElytraLayer0, MultiBufferSource pMultiBufferSource1, int pInt2, T pLivingEntity3, float pFloat4, float pFloat5, float pFloat6, float pFloat7, float pFloat8, float pFloat9, CallbackInfo ci, ItemStack lvItemStack11, ResourceLocation lvResourceLocation12) {
        AbstractClientPlayer player;
        if (WorldRenderingSettings.INSTANCE.getItemIds() == null) {
            return;
        }
        if (pLivingEntity3 instanceof AbstractClientPlayer && (player = (AbstractClientPlayer)pLivingEntity3).m_108561_() != null && player.m_36170_(PlayerModelPart.CAPE)) {
            CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt((Object)ELYTRA_CAPE_LOCATION));
            return;
        }
        ResourceLocation location = BuiltInRegistries.f_257033_.m_7981_((Object)Items.f_42741_);
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt((Object)new NamespacedId(location.m_135827_(), location.m_135815_())));
    }

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"}, at={@At(value="RETURN")})
    private void changeId2(CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(0);
    }
}

