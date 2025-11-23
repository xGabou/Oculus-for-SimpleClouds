/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexBuffer$Usage
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 */
package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.helpers.VertexBufferHelper;

public class FullScreenQuadRenderer {
    public static final FullScreenQuadRenderer INSTANCE = new FullScreenQuadRenderer();
    private final VertexBuffer quad;

    private FullScreenQuadRenderer() {
        BufferBuilder bufferBuilder = new BufferBuilder(DefaultVertexFormat.f_85817_.m_86020_() * 4);
        bufferBuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85817_);
        bufferBuilder.m_5483_(0.0, 0.0, 0.0).m_7421_(0.0f, 0.0f).m_5752_();
        bufferBuilder.m_5483_(1.0, 0.0, 0.0).m_7421_(1.0f, 0.0f).m_5752_();
        bufferBuilder.m_5483_(1.0, 1.0, 0.0).m_7421_(1.0f, 1.0f).m_5752_();
        bufferBuilder.m_5483_(0.0, 1.0, 0.0).m_7421_(0.0f, 1.0f).m_5752_();
        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.m_231175_();
        this.quad = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.quad.m_85921_();
        this.quad.m_231221_(renderedBuffer);
        VertexBuffer.m_85931_();
    }

    public void render() {
        this.begin();
        this.renderQuad();
        this.end();
    }

    public void begin() {
        ((VertexBufferHelper)this.quad).saveBinding();
        RenderSystem.disableDepthTest();
        BufferUploader.m_166835_();
        this.quad.m_85921_();
    }

    public void renderQuad() {
        IrisRenderSystem.overridePolygonMode();
        this.quad.m_166882_();
        IrisRenderSystem.restorePolygonMode();
    }

    public void end() {
        RenderSystem.enableDepthTest();
        ((VertexBufferHelper)this.quad).restoreBinding();
    }
}

