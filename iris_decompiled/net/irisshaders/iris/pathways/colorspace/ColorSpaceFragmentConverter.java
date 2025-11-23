/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.blaze3d.platform.GlStateManager
 *  org.apache.commons.io.IOUtils
 *  org.joml.Matrix4f
 */
package net.irisshaders.iris.pathways.colorspace;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.program.Program;
import net.irisshaders.iris.gl.program.ProgramBuilder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.pathways.FullScreenQuadRenderer;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;
import net.irisshaders.iris.pathways.colorspace.ColorSpaceConverter;
import net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;

public class ColorSpaceFragmentConverter
implements ColorSpaceConverter {
    private int width;
    private int height;
    private ColorSpace colorSpace;
    private Program program;
    private GlFramebuffer framebuffer;
    private int swapTexture;
    private int target;

    public ColorSpaceFragmentConverter(int width, int height, ColorSpace colorSpace) {
        this.rebuildProgram(width, height, colorSpace);
    }

    @Override
    public void rebuildProgram(int width, int height, ColorSpace colorSpace) {
        String source;
        String vertexSource;
        if (this.program != null) {
            this.program.destroy();
            this.program = null;
            this.framebuffer.destroy();
            this.framebuffer = null;
            GlStateManager._deleteTexture((int)this.swapTexture);
            this.swapTexture = 0;
        }
        this.width = width;
        this.height = height;
        this.colorSpace = colorSpace;
        try {
            vertexSource = new String(IOUtils.toByteArray((InputStream)Objects.requireNonNull(this.getClass().getResourceAsStream("/colorSpace.vsh"))), StandardCharsets.UTF_8);
            source = new String(IOUtils.toByteArray((InputStream)Objects.requireNonNull(this.getClass().getResourceAsStream("/colorSpace.csh"))), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<StringPair> defineList = new ArrayList<StringPair>();
        defineList.add(new StringPair("CURRENT_COLOR_SPACE", String.valueOf(colorSpace.ordinal())));
        for (ColorSpace space : ColorSpace.values()) {
            defineList.add(new StringPair(space.name(), String.valueOf(space.ordinal())));
        }
        source = JcppProcessor.glslPreprocessSource(source, defineList);
        ProgramBuilder builder = ProgramBuilder.begin("colorSpaceFragment", vertexSource, null, source, (ImmutableSet<Integer>)ImmutableSet.of());
        builder.uniformMatrix(UniformUpdateFrequency.ONCE, "projection", () -> new Matrix4f(2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f));
        builder.addDynamicSampler(() -> this.target, "readImage");
        this.swapTexture = GlStateManager._genTexture();
        IrisRenderSystem.texImage2D(this.swapTexture, 3553, 0, 32856, width, height, 0, 6408, 5121, null);
        this.framebuffer = new GlFramebuffer();
        this.framebuffer.addColorAttachment(0, this.swapTexture);
        this.program = builder.build();
    }

    @Override
    public void process(int targetImage) {
        if (this.colorSpace == ColorSpace.SRGB) {
            return;
        }
        this.target = targetImage;
        this.program.use();
        this.framebuffer.bind();
        FullScreenQuadRenderer.INSTANCE.render();
        Program.unbind();
        this.framebuffer.bindAsReadBuffer();
        IrisRenderSystem.copyTexSubImage2D(targetImage, 3553, 0, 0, 0, 0, 0, this.width, this.height);
    }
}

