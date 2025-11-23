/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 */
package dev.nonamecrackers2.simpleclouds.client.renderer;

import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.MultiRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.shader.SimpleCloudsShaders;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class SimpleCloudsDebugOverlayRenderer {
    public static void render(ForgeGui gui, GuiGraphics stack, float partialTicks, float width, float height) {
        Minecraft mc = gui.getMinecraft();
        if (SimpleCloudsRenderer.canRenderInDimension(gui.getMinecraft().f_91073_) && mc.f_91066_.f_92063_ && (mc.f_91074_ == null || mc.f_91074_.m_8088_() >= 2 || !ClientCloudManager.isAvailableServerSide())) {
            MultiRegionCloudMeshGenerator meshGenerator;
            int id;
            float displaySize = 50.0f;
            CloudMeshGenerator cloudMeshGenerator = SimpleCloudsRenderer.getInstance().getMeshGenerator();
            if (cloudMeshGenerator instanceof MultiRegionCloudMeshGenerator && (id = (meshGenerator = (MultiRegionCloudMeshGenerator)cloudMeshGenerator).getCloudRegionTextureId()) != -1) {
                RenderSystem.setShader(SimpleCloudsShaders::getCloudRegionTexShader);
                Matrix4f matrix4f = stack.m_280168_().m_85850_().m_252922_();
                BufferBuilder bufferbuilder = Tesselator.m_85913_().m_85915_();
                bufferbuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85817_);
                bufferbuilder.m_252986_(matrix4f, displaySize, height - displaySize, -100.0f).m_7421_(0.0f, 0.0f).m_5752_();
                bufferbuilder.m_252986_(matrix4f, displaySize, height, -100.0f).m_7421_(0.0f, 1.0f).m_5752_();
                bufferbuilder.m_252986_(matrix4f, displaySize * 2.0f, height, -100.0f).m_7421_(1.0f, 1.0f).m_5752_();
                bufferbuilder.m_252986_(matrix4f, displaySize * 2.0f, height - displaySize, -100.0f).m_7421_(1.0f, 0.0f).m_5752_();
                ShaderInstance shader = RenderSystem.getShader();
                int lod = meshGenerator.getLodConfig().getLods().length;
                shader.m_173356_("LodLevel").m_142617_(lod);
                shader.m_173356_("TotalCloudTypes").m_142617_(meshGenerator.getTotalCloudTypes());
                ProgramManager.m_85578_((int)shader.m_108943_());
                int loc = Uniform.m_85624_((int)shader.m_108943_(), (CharSequence)"TexRegionSampler");
                Uniform.m_85616_((int)loc, (int)0);
                RenderSystem.activeTexture((int)33984);
                GL11.glBindTexture((int)35866, (int)id);
                BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)bufferbuilder.m_231175_());
            }
            RenderSystem.setShaderTexture((int)0, (int)SimpleCloudsRenderer.getInstance().getStormFogShadowMap().getColorTexId());
            RenderSystem.setShader(GameRenderer::m_172817_);
            Matrix4f matrix4f = stack.m_280168_().m_85850_().m_252922_();
            BufferBuilder bufferbuilder = Tesselator.m_85913_().m_85915_();
            bufferbuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85817_);
            bufferbuilder.m_252986_(matrix4f, 0.0f, height - displaySize, -100.0f).m_7421_(0.0f, 0.0f).m_5752_();
            bufferbuilder.m_252986_(matrix4f, 0.0f, height, -100.0f).m_7421_(0.0f, 1.0f).m_5752_();
            bufferbuilder.m_252986_(matrix4f, displaySize, height, -100.0f).m_7421_(1.0f, 1.0f).m_5752_();
            bufferbuilder.m_252986_(matrix4f, displaySize, height - displaySize, -100.0f).m_7421_(1.0f, 0.0f).m_5752_();
            BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)bufferbuilder.m_231175_());
        }
    }
}

