/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.armortrim.ArmorTrim
 *  net.minecraft.world.item.armortrim.TrimMaterial
 *  org.spongepowered.asm.mixin.Mixin
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
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={HumanoidArmorLayer.class})
public abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
extends RenderLayer<T, M> {
    private int backupValue = 0;

    public MixinHumanoidArmorLayer(RenderLayerParent<T, M> pRenderLayer0) {
        super(pRenderLayer0);
    }

    @Inject(method={"renderArmorPiece"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/model/HumanoidModel;copyPropertiesTo(Lnet/minecraft/client/model/HumanoidModel;)V")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void changeId(PoseStack pHumanoidArmorLayer0, MultiBufferSource pMultiBufferSource1, T pLivingEntity2, EquipmentSlot pEquipmentSlot3, int pInt4, A pHumanoidModel5, CallbackInfo ci, ItemStack lvItemStack7, Item item, ArmorItem lvArmorItem8) {
        if (WorldRenderingSettings.INSTANCE.getItemIds() == null) {
            return;
        }
        ResourceLocation location = BuiltInRegistries.f_257033_.m_7981_((Object)lvArmorItem8);
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt((Object)new NamespacedId(location.m_135827_(), location.m_135815_())));
    }

    @Inject(method={"renderTrim"}, at={@At(value="HEAD")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void changeTrimTemp(ArmorMaterial pHumanoidArmorLayer0, PoseStack pPoseStack1, MultiBufferSource pMultiBufferSource2, int pInt3, ArmorTrim pArmorTrim4, A pHumanoidModel5, boolean pBoolean6, CallbackInfo ci) {
        if (WorldRenderingSettings.INSTANCE.getItemIds() == null) {
            return;
        }
        this.backupValue = CapturedRenderingState.INSTANCE.getCurrentRenderedItem();
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt((Object)new NamespacedId("minecraft", "trim_" + ((TrimMaterial)pArmorTrim4.m_266210_().m_203334_()).f_265854_())));
    }

    @Inject(method={"renderTrim"}, at={@At(value="TAIL")})
    private void changeTrimTemp2(ArmorMaterial pHumanoidArmorLayer0, PoseStack pPoseStack1, MultiBufferSource pMultiBufferSource2, int pInt3, ArmorTrim pArmorTrim4, A pHumanoidModel5, boolean pBoolean6, CallbackInfo ci) {
        if (WorldRenderingSettings.INSTANCE.getItemIds() == null) {
            return;
        }
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(this.backupValue);
        this.backupValue = 0;
    }

    @Inject(method={"renderArmorPiece"}, at={@At(value="TAIL")})
    private void changeId2(CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(0);
    }
}

