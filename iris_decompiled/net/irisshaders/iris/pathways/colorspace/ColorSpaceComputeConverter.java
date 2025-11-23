/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.io.IOUtils
 */
package net.irisshaders.iris.pathways.colorspace;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.program.ComputeProgram;
import net.irisshaders.iris.gl.program.ProgramBuilder;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;
import net.irisshaders.iris.pathways.colorspace.ColorSpaceConverter;
import net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor;
import org.apache.commons.io.IOUtils;

public class ColorSpaceComputeConverter
implements ColorSpaceConverter {
    private int width;
    private int height;
    private ColorSpace colorSpace;
    private ComputeProgram program;
    private int target;

    public ColorSpaceComputeConverter(int width, int height, ColorSpace colorSpace) {
        this.rebuildProgram(width, height, colorSpace);
    }

    @Override
    public void rebuildProgram(int width, int height, ColorSpace colorSpace) {
        String source;
        if (this.program != null) {
            this.program.destroy();
            this.program = null;
        }
        this.width = width;
        this.height = height;
        this.colorSpace = colorSpace;
        try {
            source = new String(IOUtils.toByteArray((InputStream)Objects.requireNonNull(this.getClass().getResourceAsStream("/colorSpace.csh"))), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<StringPair> defineList = new ArrayList<StringPair>();
        defineList.add(new StringPair("COMPUTE", ""));
        defineList.add(new StringPair("CURRENT_COLOR_SPACE", String.valueOf(colorSpace.ordinal())));
        for (ColorSpace space : ColorSpace.values()) {
            defineList.add(new StringPair(space.name(), String.valueOf(space.ordinal())));
        }
        source = JcppProcessor.glslPreprocessSource(source, defineList);
        ProgramBuilder builder = ProgramBuilder.beginCompute("colorSpaceCompute", source, (ImmutableSet<Integer>)ImmutableSet.of());
        builder.addTextureImage(() -> this.target, InternalTextureFormat.RGBA8, "readImage");
        this.program = builder.buildCompute();
    }

    @Override
    public void process(int targetImage) {
        if (this.colorSpace == ColorSpace.SRGB) {
            return;
        }
        this.target = targetImage;
        this.program.use();
        IrisRenderSystem.dispatchCompute(this.width / 8, this.height / 8, 1);
        IrisRenderSystem.memoryBarrier(40);
        ComputeProgram.unbind();
    }
}

