/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.SolidBucketItem
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class}, priority=1010)
public abstract class MixinItemRenderer {
    @Unique
    private int previousBeValue;

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void changeId(ItemStack pItemRenderer0, ItemDisplayContext pItemTransforms$TransformType1, boolean pBoolean2, PoseStack pPoseStack3, MultiBufferSource pMultiBufferSource4, int pInt5, int pInt6, BakedModel pBakedModel7, CallbackInfo ci) {
        this.iris$setupId(pItemRenderer0);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Unique
    private void iris$setupId(ItemStack pItemRenderer0) {
        if (WorldRenderingSettings.INSTANCE.getItemIds() == null) {
            return;
        }
        Item item = pItemRenderer0.m_41720_();
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            if (!(pItemRenderer0.m_41720_() instanceof SolidBucketItem)) {
                if (WorldRenderingSettings.INSTANCE.getBlockStateIds() == null) {
                    return;
                }
                this.previousBeValue = CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity();
                CapturedRenderingState.INSTANCE.setCurrentBlockEntity(1);
                CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getBlockStateIds().getOrDefault((Object)blockItem.m_40614_().m_49966_(), 0));
                return;
            }
        }
        ResourceLocation location = BuiltInRegistries.f_257033_.m_7981_((Object)pItemRenderer0.m_41720_());
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt((Object)new NamespacedId(location.m_135827_(), location.m_135815_())));
    }

    @Inject(method={"render"}, at={@At(value="RETURN")})
    private void changeId3(CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(0);
        CapturedRenderingState.INSTANCE.setCurrentBlockEntity(this.previousBeValue);
        this.previousBeValue = 0;
    }
}

