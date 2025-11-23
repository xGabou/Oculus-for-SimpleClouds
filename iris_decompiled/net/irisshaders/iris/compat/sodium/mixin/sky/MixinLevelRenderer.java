/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.sky;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.Iris;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @Shadow
    @Final
    private Minecraft f_109461_;

    @Inject(method={"renderSky"}, at={@At(value="HEAD")}, cancellable=true)
    private void preRenderSky(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (!Iris.getCurrentPack().isPresent()) {
            boolean useThickFog;
            Vec3 cameraPosition = camera.m_90583_();
            Entity cameraEntity = camera.m_90592_();
            boolean isSubmersed = camera.m_167685_() != FogType.NONE;
            boolean hasBlindness = cameraEntity instanceof LivingEntity && ((LivingEntity)cameraEntity).m_21023_(MobEffects.f_19610_);
            boolean bl2 = useThickFog = this.f_109461_.f_91073_.m_104583_().m_5781_(Mth.m_14107_((double)cameraPosition.m_7096_()), Mth.m_14107_((double)cameraPosition.m_7098_())) || this.f_109461_.f_91065_.m_93090_().m_93715_();
            if (isSubmersed || hasBlindness || useThickFog) {
                ci.cancel();
            }
        }
    }
}

