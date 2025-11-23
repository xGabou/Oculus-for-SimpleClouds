/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  org.joml.Matrix4f
 */
package dev.nonamecrackers2.simpleclouds.client.framebuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public class FrameBufferUtils {
    public static void blitTargetPreservingAlpha(RenderTarget target, int width, int height) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask((boolean)false);
        GlStateManager._viewport((int)0, (int)0, (int)width, (int)height);
        RenderSystem.disableBlend();
        Minecraft minecraft = Minecraft.m_91087_();
        ShaderInstance shaderinstance = minecraft.f_91063_.f_172635_;
        shaderinstance.m_173350_("DiffuseSampler", (Object)target.m_83975_());
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, (float)width, (float)height, 0.0f, 1000.0f, 3000.0f);
        RenderSystem.setProjectionMatrix((Matrix4f)matrix4f, (VertexSorting)VertexSorting.f_276633_);
        if (shaderinstance.f_173308_ != null) {
            shaderinstance.f_173308_.m_5679_(new Matrix4f().translation(0.0f, 0.0f, -2000.0f));
        }
        if (shaderinstance.f_173309_ != null) {
            shaderinstance.f_173309_.m_5679_(matrix4f);
        }
        shaderinstance.m_173363_();
        float f = width;
        float f1 = height;
        float f2 = (float)target.f_83917_ / (float)target.f_83915_;
        float f3 = (float)target.f_83918_ / (float)target.f_83916_;
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tesselator.m_85915_();
        bufferbuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
        bufferbuilder.m_5483_(0.0, (double)f1, 0.0).m_7421_(0.0f, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bufferbuilder.m_5483_((double)f, (double)f1, 0.0).m_7421_(f2, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bufferbuilder.m_5483_((double)f, 0.0, 0.0).m_7421_(f2, f3).m_6122_(255, 255, 255, 255).m_5752_();
        bufferbuilder.m_5483_(0.0, 0.0, 0.0).m_7421_(0.0f, f3).m_6122_(255, 255, 255, 255).m_5752_();
        BufferUploader.m_231209_((BufferBuilder.RenderedBuffer)bufferbuilder.m_231175_());
        shaderinstance.m_173362_();
        GlStateManager._depthMask((boolean)true);
    }
}

