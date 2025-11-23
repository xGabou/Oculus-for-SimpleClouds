/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Slice
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.layer.IsOutlineRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.pathways.HandRenderer;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shadows.frustum.fallback.NonCullingFrustum;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.IrisTimeUniforms;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    private static final String RENDER = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V";
    private static final String CLEAR = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V";
    private static final String RENDER_SKY = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V";
    private static final String RENDER_CLOUDS = "Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FDDD)V";
    private static final String RENDER_WEATHER = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V";
    @Shadow
    @Final
    private Minecraft f_109461_;
    @Unique
    private WorldRenderingPipeline pipeline;
    @Shadow
    private RenderBuffers f_109464_;
    @Shadow
    private int f_109477_;
    @Shadow
    private Frustum f_172938_;
    @Shadow
    @Nullable
    private ClientLevel f_109465_;

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    private void iris$setupPipeline(PoseStack poseStack, float tickDelta, long startTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo callback) {
        DHCompat.checkFrame();
        IrisTimeUniforms.updateTime();
        CapturedRenderingState.INSTANCE.setGbufferModelView(poseStack.m_85850_().m_252922_());
        CapturedRenderingState.INSTANCE.setGbufferProjection(projection);
        CapturedRenderingState.INSTANCE.setTickDelta(tickDelta);
        CapturedRenderingState.INSTANCE.setRealTickDelta(tickDelta);
        CapturedRenderingState.INSTANCE.setCloudTime(((float)this.f_109477_ + tickDelta) * 0.03f);
        SystemTimeUniforms.COUNTER.beginFrame();
        SystemTimeUniforms.TIMER.beginFrame(startTime);
        this.pipeline = Iris.getPipelineManager().preparePipeline(Iris.getCurrentDimension());
        if (this.pipeline.shouldDisableFrustumCulling()) {
            this.f_172938_ = new NonCullingFrustum();
        }
        boolean bl = Minecraft.m_91087_().f_90980_ = !this.pipeline.shouldDisableOcclusionCulling();
        if (Iris.shouldActivateWireframe() && this.f_109461_.m_91090_()) {
            IrisRenderSystem.setPolygonMode(6913);
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", shift=At.Shift.AFTER, remap=false)})
    private void iris$beginLevelRender(PoseStack poseStack, float tickDelta, long startTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo callback) {
        this.pipeline.beginLevelRendering();
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V"}, at={@At(value="RETURN", shift=At.Shift.BEFORE)})
    private void iris$endLevelRender(PoseStack poseStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback) {
        HandRenderer.INSTANCE.renderTranslucent(poseStack, tickDelta, camera, gameRenderer, this.pipeline);
        Minecraft.m_91087_().m_91307_().m_6182_("iris_final");
        this.pipeline.finalizeLevelRendering();
        this.pipeline = null;
        if (Iris.shouldActivateWireframe() && this.f_109461_.m_91090_()) {
            IrisRenderSystem.setPolygonMode(6914);
        }
    }

    @Inject(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")})
    private void iris$renderTerrainShadows(PoseStack poseStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback) {
        this.pipeline.renderShadows((LevelRendererAccessor)((Object)this), camera);
    }

    @ModifyVariable(method={"renderSky"}, at=@At(value="HEAD"), index=5, argsOnly=true)
    private boolean iris$alwaysRenderSky(boolean value) {
        return false;
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")})
    private void iris$beginSky(PoseStack poseStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo callback) {
        this.pipeline.setPhase(WorldRenderingPhase.CUSTOM_SKY);
        RenderSystem.setShader(GameRenderer::m_172808_);
    }

    @Inject(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/FogRenderer;levelFogColor()V")})
    private void iris$renderSky$beginNormalSky(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.SKY);
    }

    @Inject(method={"renderSky"}, at={@At(value="FIELD", target="Lnet/minecraft/client/renderer/LevelRenderer;SUN_LOCATION:Lnet/minecraft/resources/ResourceLocation;")})
    private void iris$setSunRenderStage(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.SUN);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F")})
    private void iris$setSunsetRenderStage(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.SUNSET);
    }

    @Inject(method={"renderSky"}, at={@At(value="FIELD", target="Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;")})
    private void iris$setMoonRenderStage(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.MOON);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F")})
    private void iris$setStarRenderStage(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.STARS);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;")})
    private void iris$setVoidRenderStage(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.VOID);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F")}, slice={@Slice(from=@At(value="FIELD", target="Lcom/mojang/math/Axis;YP:Lcom/mojang/math/Axis;"))})
    private void iris$renderSky$tiltSun(PoseStack poseStack, Matrix4f projectionMatrix, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        poseStack.m_252781_(Axis.f_252403_.m_252977_(this.pipeline.getSunPathRotation()));
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", shift=At.Shift.AFTER)})
    private void iris$endSky(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FDDD)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/DimensionSpecialEffects;renderClouds(Lnet/minecraft/client/multiplayer/ClientLevel;IFLcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)Z", shift=At.Shift.AFTER)})
    private void iris$beginClouds(PoseStack poseStack, Matrix4f projection, float tickDelta, double x, double y, double z, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.CLOUDS);
    }

    @Inject(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FDDD)V"}, at={@At(value="RETURN")})
    private void iris$endClouds(PoseStack poseStack, Matrix4f projection, float tickDelta, double x, double y, double z, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderChunkLayer"}, at={@At(value="HEAD")})
    private void iris$beginTerrainLayer(RenderType renderType, PoseStack poseStack, double d, double e, double f, Matrix4f projectionMatrix, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.fromTerrainRenderType(renderType));
    }

    @Inject(method={"renderChunkLayer"}, at={@At(value="RETURN")})
    private void iris$endTerrainLayer(RenderType renderType, PoseStack poseStack, double d, double e, double f, Matrix4f projectionMatrix, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/DimensionSpecialEffects;renderSnowAndRain(Lnet/minecraft/client/multiplayer/ClientLevel;IFLnet/minecraft/client/renderer/LightTexture;DDD)Z", shift=At.Shift.AFTER)})
    private void iris$beginWeather(LightTexture arg, float tickDelta, double x, double y, double z, CallbackInfo callback) {
        this.pipeline.setPhase(WorldRenderingPhase.RAIN_SNOW);
    }

    @ModifyArg(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V", ordinal=0, remap=false))
    private boolean iris$writeRainAndSnowToDepthBuffer(boolean depthMaskEnabled) {
        if (this.pipeline.shouldWriteRainAndSnowToDepthBuffer()) {
            return true;
        }
        return depthMaskEnabled;
    }

    @Inject(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V"}, at={@At(value="RETURN")})
    private void iris$endWeather(LightTexture arg, float tickDelta, double x, double y, double z, CallbackInfo callback) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(Lnet/minecraft/client/Camera;)V")})
    private void iris$beginWorldBorder(PoseStack poseStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo callback) {
        this.pipeline.setPhase(WorldRenderingPhase.WORLD_BORDER);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(Lnet/minecraft/client/Camera;)V", shift=At.Shift.AFTER)})
    private void iris$endWorldBorder(PoseStack poseStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo callback) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V")})
    private void iris$setDebugRenderStage(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.DEBUG);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V", shift=At.Shift.AFTER)})
    private void iris$resetDebugRenderStage(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @ModifyArg(method={"renderLevel"}, at=@At(value="INVOKE", target="net/minecraft/client/renderer/MultiBufferSource$BufferSource.getBuffer (Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), slice=@Slice(from=@At(value="CONSTANT", args={"stringValue=outline"}), to=@At(value="INVOKE", target="net/minecraft/client/renderer/LevelRenderer.renderHitOutline (Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")))
    private RenderType iris$beginBlockOutline(RenderType type) {
        return new OuterWrappedRenderType("iris:is_outline", type, IsOutlineRenderStateShard.INSTANCE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="CONSTANT", args={"stringValue=translucent"})})
    private void iris$beginTranslucents(PoseStack poseStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo ci) {
        this.pipeline.beginHand();
        HandRenderer.INSTANCE.renderSolid(poseStack, tickDelta, camera, gameRenderer, this.pipeline);
        Minecraft.m_91087_().m_91307_().m_6182_("iris_pre_translucent");
        this.pipeline.beginTranslucents();
    }
}

