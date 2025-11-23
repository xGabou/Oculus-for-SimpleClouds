/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntFunction
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 */
package net.irisshaders.iris.uniforms;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.irisshaders.iris.api.v0.item.IrisItemLightProvider;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public final class IdMapUniforms {
    private IdMapUniforms() {
    }

    public static void addIdMapUniforms(FrameUpdateNotifier notifier, UniformHolder uniforms, IdMap idMap, boolean isOldHandLight) {
        HeldItemSupplier mainHandSupplier = new HeldItemSupplier(InteractionHand.MAIN_HAND, idMap.getItemIdMap(), isOldHandLight);
        HeldItemSupplier offHandSupplier = new HeldItemSupplier(InteractionHand.OFF_HAND, idMap.getItemIdMap(), false);
        notifier.addListener(mainHandSupplier::update);
        notifier.addListener(offHandSupplier::update);
        uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "heldItemId", mainHandSupplier::getIntID).uniform1i(UniformUpdateFrequency.PER_FRAME, "heldItemId2", offHandSupplier::getIntID).uniform1i(UniformUpdateFrequency.PER_FRAME, "heldBlockLightValue", mainHandSupplier::getLightValue).uniform1i(UniformUpdateFrequency.PER_FRAME, "heldBlockLightValue2", offHandSupplier::getLightValue).uniform3f(UniformUpdateFrequency.PER_FRAME, "heldBlockLightColor", mainHandSupplier::getLightColor).uniform3f(UniformUpdateFrequency.PER_FRAME, "heldBlockLightColor2", offHandSupplier::getLightColor);
    }

    private static class HeldItemSupplier {
        private final InteractionHand hand;
        private final Object2IntFunction<NamespacedId> itemIdMap;
        private final boolean applyOldHandLight;
        private int intID;
        private int lightValue;
        private Vector3f lightColor;

        HeldItemSupplier(InteractionHand hand, Object2IntFunction<NamespacedId> itemIdMap, boolean shouldApplyOldHandLight) {
            this.hand = hand;
            this.itemIdMap = itemIdMap;
            this.applyOldHandLight = shouldApplyOldHandLight && hand == InteractionHand.MAIN_HAND;
        }

        private void invalidate() {
            this.intID = -1;
            this.lightValue = 0;
            this.lightColor = IrisItemLightProvider.DEFAULT_LIGHT_COLOR;
        }

        public void update() {
            LocalPlayer player = Minecraft.m_91087_().f_91074_;
            if (player == null) {
                this.invalidate();
                return;
            }
            ItemStack heldStack = player.m_21120_(this.hand);
            if (heldStack == null) {
                this.invalidate();
                return;
            }
            Item heldItem = heldStack.m_41720_();
            if (heldItem == null) {
                this.invalidate();
                return;
            }
            ResourceLocation heldItemId = BuiltInRegistries.f_257033_.m_7981_((Object)heldItem);
            this.intID = this.itemIdMap.applyAsInt((Object)new NamespacedId(heldItemId.m_135827_(), heldItemId.m_135815_()));
            IrisItemLightProvider lightProvider = (IrisItemLightProvider)heldItem;
            this.lightValue = lightProvider.getLightEmission((Player)Minecraft.m_91087_().f_91074_, heldStack);
            if (this.applyOldHandLight) {
                lightProvider = this.applyOldHandLighting(player, lightProvider);
            }
            this.lightColor = lightProvider.getLightColor((Player)Minecraft.m_91087_().f_91074_, heldStack);
        }

        private IrisItemLightProvider applyOldHandLighting(@NotNull LocalPlayer player, IrisItemLightProvider existing) {
            ItemStack offHandStack = player.m_21120_(InteractionHand.OFF_HAND);
            if (offHandStack == null) {
                return existing;
            }
            Item offHandItem = offHandStack.m_41720_();
            if (offHandItem == null) {
                return existing;
            }
            IrisItemLightProvider lightProvider = (IrisItemLightProvider)offHandItem;
            int newEmission = lightProvider.getLightEmission((Player)Minecraft.m_91087_().f_91074_, offHandStack);
            if (this.lightValue < newEmission) {
                this.lightValue = newEmission;
                return lightProvider;
            }
            return existing;
        }

        public int getIntID() {
            return this.intID;
        }

        public int getLightValue() {
            return this.lightValue;
        }

        public Vector3f getLightColor() {
            return this.lightColor;
        }
    }
}

