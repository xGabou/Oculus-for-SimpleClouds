/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 */
package net.irisshaders.iris.fantastic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class IrisParticleRenderTypes {
    public static final ParticleRenderType OPAQUE_TERRAIN = new ParticleRenderType(){

        public void m_6505_(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask((boolean)true);
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)TextureAtlas.f_118259_);
            bufferBuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85813_);
        }

        public void m_6294_(Tesselator tesselator) {
            tesselator.m_85914_();
        }

        public String toString() {
            return "OPAQUE_TERRAIN_SHEET";
        }
    };
}

