/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  org.apache.commons.io.IOUtils
 *  org.joml.Matrix4f
 */
package net.irisshaders.iris.pathways;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.program.Program;
import net.irisshaders.iris.gl.program.ProgramBuilder;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.texture.DepthCopyStrategy;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.gl.texture.PixelType;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.pathways.FullScreenQuadRenderer;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;

public class CenterDepthSampler {
    private static final double LN2 = Math.log(2.0);
    private final Program program;
    private final GlFramebuffer framebuffer;
    private final int texture = GlStateManager._genTexture();
    private final int altTexture = GlStateManager._genTexture();
    private boolean hasFirstSample;
    private boolean everRetrieved;
    private boolean destroyed;

    public CenterDepthSampler(IntSupplier depthSupplier, float halfLife) {
        ProgramBuilder builder;
        this.framebuffer = new GlFramebuffer();
        InternalTextureFormat format = InternalTextureFormat.R32F;
        this.setupColorTexture(this.texture, format);
        this.setupColorTexture(this.altTexture, format);
        RenderSystem.bindTexture((int)0);
        this.framebuffer.addColorAttachment(0, this.texture);
        try {
            String fsh = new String(IOUtils.toByteArray((InputStream)Objects.requireNonNull(this.getClass().getResourceAsStream("/centerDepth.fsh"))), StandardCharsets.UTF_8);
            String vsh = new String(IOUtils.toByteArray((InputStream)Objects.requireNonNull(this.getClass().getResourceAsStream("/centerDepth.vsh"))), StandardCharsets.UTF_8);
            builder = ProgramBuilder.begin("centerDepthSmooth", vsh, null, fsh, (ImmutableSet<Integer>)ImmutableSet.of((Object)0, (Object)1, (Object)2));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        builder.addDynamicSampler(depthSupplier, "depth");
        builder.addDynamicSampler(() -> this.altTexture, "altDepth");
        builder.uniform1f(UniformUpdateFrequency.PER_FRAME, "lastFrameTime", SystemTimeUniforms.TIMER::getLastFrameTime);
        builder.uniform1f(UniformUpdateFrequency.ONCE, "decay", () -> 1.0 / ((double)halfLife * 0.1 / LN2));
        builder.uniformMatrix(UniformUpdateFrequency.ONCE, "projection", () -> new Matrix4f(2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f));
        this.program = builder.build();
    }

    public void sampleCenterDepth() {
        if (this.hasFirstSample && !this.everRetrieved || this.destroyed) {
            return;
        }
        this.hasFirstSample = true;
        this.framebuffer.bind();
        this.program.use();
        RenderSystem.viewport((int)0, (int)0, (int)1, (int)1);
        FullScreenQuadRenderer.INSTANCE.render();
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        DepthCopyStrategy.fastest(false).copy(this.framebuffer, this.texture, null, this.altTexture, 1, 1);
        Minecraft.m_91087_().m_91385_().m_83947_(true);
    }

    public void setupColorTexture(int texture, InternalTextureFormat format) {
        IrisRenderSystem.texImage2D(texture, 3553, 0, format.getGlFormat(), 1, 1, 0, format.getPixelFormat().getGlFormat(), PixelType.FLOAT.getGlFormat(), null);
        IrisRenderSystem.texParameteri(texture, 3553, 10241, 9729);
        IrisRenderSystem.texParameteri(texture, 3553, 10240, 9729);
        IrisRenderSystem.texParameteri(texture, 3553, 10242, 33071);
        IrisRenderSystem.texParameteri(texture, 3553, 10243, 33071);
    }

    public int getCenterDepthTexture() {
        return this.altTexture;
    }

    public void setUsage(boolean usage) {
        this.everRetrieved |= usage;
    }

    public void destroy() {
        GlStateManager._deleteTexture((int)this.texture);
        GlStateManager._deleteTexture((int)this.altTexture);
        this.framebuffer.destroy();
        this.program.destroy();
        this.destroyed = true;
    }
}

