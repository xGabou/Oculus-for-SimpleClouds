/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.joml.Matrix4f
 */
package dev.nonamecrackers2.simpleclouds.client.renderer.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.DefaultPipeline;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.ShaderSupportPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;

public interface CloudsRenderPipeline {
    public static final CloudsRenderPipeline DEFAULT = new DefaultPipeline();
    public static final CloudsRenderPipeline SHADER_SUPPORT = new ShaderSupportPipeline();

    public void prepare(Minecraft var1, SimpleCloudsRenderer var2, PoseStack var3, Matrix4f var4, float var5, double var6, double var8, double var10, Frustum var12);

    public void afterSky(Minecraft var1, SimpleCloudsRenderer var2, PoseStack var3, Matrix4f var4, float var5, double var6, double var8, double var10, Frustum var12);

    public void beforeWeather(Minecraft var1, SimpleCloudsRenderer var2, PoseStack var3, Matrix4f var4, float var5, double var6, double var8, double var10, Frustum var12);

    public void afterLevel(Minecraft var1, SimpleCloudsRenderer var2, PoseStack var3, Matrix4f var4, float var5, double var6, double var8, double var10, Frustum var12);

    default public void beforeDistantHorizonsApplyShader(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack dhModelViewStack, Matrix4f dhProjMat, float partialTick, double camX, double camY, double camZ, Frustum frustum, int dhFrameBufferId) {
    }

    default public void afterDistantHorizonsRender(Minecraft mc, SimpleCloudsRenderer renderer, PoseStack dhModelViewStack, Matrix4f dhProjMat, float partialTick, double camX, double camY, double camZ, Frustum frustum, int dhFrameBufferId) {
    }
}

