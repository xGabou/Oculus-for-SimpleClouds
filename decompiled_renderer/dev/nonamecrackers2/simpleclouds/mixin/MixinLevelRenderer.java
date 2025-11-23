/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nullable
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class}, priority=1001)
public class MixinLevelRenderer {
    @Shadow
    @Nullable
    private ClientLevel f_109465_;

    @Inject(method={"renderClouds"}, at={@At(value="HEAD")}, cancellable=true)
    public void simpleclouds$overrideCloudRendering_renderClouds(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_)) {
            ci.cancel();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="TAIL")})
    public void simpleclouds$injectCustomCloudRenderingPost_renderLevel(PoseStack stack, float partialTick, long l, boolean flag, Camera camera, GameRenderer renderer, LightTexture texture, Matrix4f projMat, CallbackInfo ci) {
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_)) {
            SimpleCloudsRenderer.getInstance().renderAfterLevel(stack, RenderSystem.getProjectionMatrix(), partialTick, camera.m_90583_().f_82479_, camera.m_90583_().f_82480_, camera.m_90583_().f_82481_);
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    public void simpleclouds$injectCustomCloudRenderingPre_renderLevel(PoseStack stack, float partialTick, long l, boolean flag, Camera camera, GameRenderer renderer, LightTexture texture, Matrix4f projMat, CallbackInfo ci) {
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_)) {
            SimpleCloudsRenderer.getInstance().renderBeforeLevel(stack, RenderSystem.getProjectionMatrix(), partialTick, camera.m_90583_().f_82479_, camera.m_90583_().f_82480_, camera.m_90583_().f_82481_);
        }
    }

    @Inject(method={"renderSky"}, at={@At(value="RETURN")})
    public void simpleclouds$injectCustomCloudRenderingAfterSky_renderSky(PoseStack stack, Matrix4f projMat, float partialTick, Camera camera, boolean flag, Runnable fogSetup, CallbackInfo ci) {
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_)) {
            SimpleCloudsRenderer.getInstance().renderAfterSky(stack, RenderSystem.getProjectionMatrix(), partialTick, camera.m_90583_().f_82479_, camera.m_90583_().f_82480_, camera.m_90583_().f_82481_);
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;")})
    public void simpleclouds$injectCustomCloudRenderingBeforeWeather_renderLevel(PoseStack stack, float partialTick, long l, boolean flag, Camera camera, GameRenderer renderer, LightTexture texture, Matrix4f projMat, CallbackInfo ci) {
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_)) {
            SimpleCloudsRenderer.getInstance().renderBeforeWeather(stack, projMat, partialTick, camera.m_90583_().f_82479_, camera.m_90583_().f_82480_, camera.m_90583_().f_82481_);
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V")})
    public void simpleclouds$injectCustomWeatherRendering_renderLevel(PoseStack stack, float partialTick, long l, boolean flag, Camera camera, GameRenderer renderer, LightTexture texture, Matrix4f projMat, CallbackInfo ci) {
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_)) {
            SimpleCloudsRenderer.getInstance().renderWeather(texture, partialTick, camera.m_90583_().f_82479_, camera.m_90583_().f_82480_, camera.m_90583_().f_82481_);
        }
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void simpleclouds$tickCloudRenderer_tick(CallbackInfo ci) {
        SimpleCloudsRenderer.getInstance().baseTick();
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_)) {
            SimpleCloudsRenderer.getInstance().tick();
        }
    }

    @Inject(method={"renderSnowAndRain"}, at={@At(value="HEAD")}, cancellable=true)
    public void simpleclouds$overrideRainRendering_renderSnowAndRain(LightTexture texture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (SimpleCloudsRenderer.canRenderInDimension(this.f_109465_) && SimpleCloudsCompatHelper.renderCustomRain()) {
            ci.cancel();
        }
    }

    @ModifyConstant(method={"tickRain"}, constant={@Constant(floatValue=0.2f, ordinal=0)})
    public float simpleclouds$modifyRainSoundVolume_tickRain(float value) {
        return value * this.f_109465_.m_46722_(0.0f);
    }

    @ModifyConstant(method={"tickRain"}, constant={@Constant(floatValue=0.1f, ordinal=0)})
    public float simpleclouds$modifyAboveRainSoundVolume_tickRain(float value) {
        return value * this.f_109465_.m_46722_(0.0f);
    }
}

