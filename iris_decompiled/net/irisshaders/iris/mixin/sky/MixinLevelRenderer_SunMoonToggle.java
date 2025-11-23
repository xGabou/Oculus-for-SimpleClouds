/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Camera
 *  net.minecraft.client.renderer.LevelRenderer
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Slice
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.sky;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer_SunMoonToggle {
    @Unique
    private void iris$emptyBuilder() {
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_231175_().m_231200_();
        builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85814_);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/BufferBuilder;end()Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;")}, slice={@Slice(from=@At(value="FIELD", target="net/minecraft/client/renderer/LevelRenderer.SUN_LOCATION : Lnet/minecraft/resources/ResourceLocation;"), to=@At(value="FIELD", target="net/minecraft/client/renderer/LevelRenderer.MOON_LOCATION : Lnet/minecraft/resources/ResourceLocation;"))}, allow=1)
    private void iris$beforeDrawSun(PoseStack arg, Matrix4f arg2, float f, Camera arg3, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (!Iris.getPipelineManager().getPipeline().map(WorldRenderingPipeline::shouldRenderSun).orElse(true).booleanValue()) {
            this.iris$emptyBuilder();
        }
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/BufferBuilder;end()Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;")}, slice={@Slice(from=@At(value="FIELD", target="net/minecraft/client/renderer/LevelRenderer.MOON_LOCATION : Lnet/minecraft/resources/ResourceLocation;"), to=@At(value="INVOKE", target="net/minecraft/client/multiplayer/ClientLevel.getStarBrightness (F)F"))}, allow=1)
    private void iris$beforeDrawMoon(PoseStack arg, Matrix4f arg2, float f, Camera arg3, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (!Iris.getPipelineManager().getPipeline().map(WorldRenderingPipeline::shouldRenderMoon).orElse(true).booleanValue()) {
            this.iris$emptyBuilder();
        }
    }
}

