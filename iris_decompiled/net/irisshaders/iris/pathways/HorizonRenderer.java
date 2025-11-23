/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexBuffer$Usage
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  org.joml.Matrix4f
 */
package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public class HorizonRenderer {
    private static final float TOP = 16.0f;
    private static final float BOTTOM = -16.0f;
    private static final double COS_22_5 = Math.cos(Math.toRadians(22.5));
    private static final double SIN_22_5 = Math.sin(Math.toRadians(22.5));
    private VertexBuffer buffer;
    private int currentRenderDistance;

    public HorizonRenderer() {
        this.currentRenderDistance = Minecraft.m_91087_().f_91066_.m_193772_();
        this.rebuildBuffer();
    }

    private void rebuildBuffer() {
        if (this.buffer != null) {
            this.buffer.close();
        }
        BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
        buffer.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85814_);
        this.buildHorizon(this.currentRenderDistance * 16, (VertexConsumer)buffer);
        BufferBuilder.RenderedBuffer renderedBuffer = buffer.m_231175_();
        this.buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.buffer.m_85921_();
        this.buffer.m_231221_(renderedBuffer);
        VertexBuffer.m_85931_();
    }

    private void buildQuad(VertexConsumer consumer, double x1, double z1, double x2, double z2) {
        consumer.m_5483_(x1, -16.0, z1);
        consumer.m_5752_();
        consumer.m_5483_(x1, 16.0, z1);
        consumer.m_5752_();
        consumer.m_5483_(x2, 16.0, z2);
        consumer.m_5752_();
        consumer.m_5483_(x2, -16.0, z2);
        consumer.m_5752_();
    }

    private void buildHalf(VertexConsumer consumer, double adjacent, double opposite, boolean invert) {
        if (invert) {
            adjacent = -adjacent;
            opposite = -opposite;
        }
        this.buildQuad(consumer, adjacent, -opposite, opposite, -adjacent);
        this.buildQuad(consumer, adjacent, opposite, adjacent, -opposite);
        this.buildQuad(consumer, opposite, adjacent, adjacent, opposite);
        this.buildQuad(consumer, -opposite, adjacent, opposite, adjacent);
    }

    private void buildOctagonalPrism(VertexConsumer consumer, double adjacent, double opposite) {
        this.buildHalf(consumer, adjacent, opposite, false);
        this.buildHalf(consumer, adjacent, opposite, true);
    }

    private void buildRegularOctagonalPrism(VertexConsumer consumer, double radius) {
        this.buildOctagonalPrism(consumer, radius * COS_22_5, radius * SIN_22_5);
    }

    private void buildBottomPlane(VertexConsumer consumer, int radius) {
        for (int x = -radius; x <= radius; x += 64) {
            for (int z = -radius; z <= radius; z += 64) {
                consumer.m_5483_((double)(x + 64), -16.0, (double)z);
                consumer.m_5752_();
                consumer.m_5483_((double)x, -16.0, (double)z);
                consumer.m_5752_();
                consumer.m_5483_((double)x, -16.0, (double)(z + 64));
                consumer.m_5752_();
                consumer.m_5483_((double)(x + 64), -16.0, (double)(z + 64));
                consumer.m_5752_();
            }
        }
    }

    private void buildTopPlane(VertexConsumer consumer, int radius) {
        for (int x = -radius; x <= radius; x += 64) {
            for (int z = -radius; z <= radius; z += 64) {
                consumer.m_5483_((double)(x + 64), 16.0, (double)z);
                consumer.m_5752_();
                consumer.m_5483_((double)(x + 64), 16.0, (double)(z + 64));
                consumer.m_5752_();
                consumer.m_5483_((double)x, 16.0, (double)(z + 64));
                consumer.m_5752_();
                consumer.m_5483_((double)x, 16.0, (double)z);
                consumer.m_5752_();
            }
        }
    }

    private void buildHorizon(int radius, VertexConsumer consumer) {
        if (radius > 256) {
            radius = 256;
        }
        this.buildRegularOctagonalPrism(consumer, radius);
        this.buildTopPlane(consumer, 384);
        this.buildBottomPlane(consumer, 384);
    }

    public void renderHorizon(Matrix4f modelView, Matrix4f projection, ShaderInstance shader) {
        if (this.currentRenderDistance != Minecraft.m_91087_().f_91066_.m_193772_()) {
            this.currentRenderDistance = Minecraft.m_91087_().f_91066_.m_193772_();
            this.rebuildBuffer();
        }
        this.buffer.m_85921_();
        this.buffer.m_253207_(modelView, projection, shader);
        VertexBuffer.m_85931_();
    }

    public void destroy() {
        this.buffer.close();
    }
}

