/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gl.program;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.image.ImageHolder;
import net.irisshaders.iris.gl.program.ComputeProgram;
import net.irisshaders.iris.gl.program.Program;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.sampler.GlSampler;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.shader.GlShader;
import net.irisshaders.iris.gl.shader.ProgramCreator;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.gl.texture.TextureType;
import org.jetbrains.annotations.Nullable;

public class ProgramBuilder
extends ProgramUniforms.Builder
implements SamplerHolder,
ImageHolder {
    private final int program;
    private final ProgramSamplers.Builder samplers;
    private final ProgramImages.Builder images;

    private ProgramBuilder(String name, int program, ImmutableSet<Integer> reservedTextureUnits) {
        super(name, program);
        this.program = program;
        this.samplers = ProgramSamplers.builder(program, reservedTextureUnits);
        this.images = ProgramImages.builder(program);
    }

    public static ProgramBuilder begin(String name, @Nullable String vertexSource, @Nullable String geometrySource, @Nullable String fragmentSource, ImmutableSet<Integer> reservedTextureUnits) {
        RenderSystem.assertOnRenderThread();
        GlShader vertex = ProgramBuilder.buildShader(ShaderType.VERTEX, name + ".vsh", vertexSource);
        GlShader geometry = geometrySource != null ? ProgramBuilder.buildShader(ShaderType.GEOMETRY, name + ".gsh", geometrySource) : null;
        GlShader fragment = ProgramBuilder.buildShader(ShaderType.FRAGMENT, name + ".fsh", fragmentSource);
        int programId = geometry != null ? ProgramCreator.create(name, vertex, geometry, fragment) : ProgramCreator.create(name, vertex, fragment);
        vertex.destroy();
        if (geometry != null) {
            geometry.destroy();
        }
        fragment.destroy();
        return new ProgramBuilder(name, programId, reservedTextureUnits);
    }

    public static ProgramBuilder beginCompute(String name, @Nullable String source, ImmutableSet<Integer> reservedTextureUnits) {
        RenderSystem.assertOnRenderThread();
        if (!IrisRenderSystem.supportsCompute()) {
            throw new IllegalStateException("This PC does not support compute shaders, but it's attempting to be used???");
        }
        GlShader compute = ProgramBuilder.buildShader(ShaderType.COMPUTE, name + ".csh", source);
        int programId = ProgramCreator.create(name, compute);
        compute.destroy();
        return new ProgramBuilder(name, programId, reservedTextureUnits);
    }

    private static GlShader buildShader(ShaderType shaderType, String name, @Nullable String source) {
        try {
            return new GlShader(shaderType, name, source);
        }
        catch (ShaderCompileException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Failed to compile " + shaderType + " shader for program " + name, e);
        }
    }

    public void bindAttributeLocation(int index, String name) {
        IrisRenderSystem.bindAttributeLocation(this.program, index, name);
    }

    public Program build() {
        return new Program(this.program, super.buildUniforms(), this.samplers.build(), this.images.build());
    }

    public ComputeProgram buildCompute() {
        return new ComputeProgram(this.program, super.buildUniforms(), this.samplers.build(), this.images.build());
    }

    @Override
    public void addExternalSampler(int textureUnit, String ... names) {
        this.samplers.addExternalSampler(textureUnit, names);
    }

    @Override
    public boolean hasSampler(String name) {
        return this.samplers.hasSampler(name);
    }

    @Override
    public boolean addDefaultSampler(IntSupplier sampler, String ... names) {
        return this.samplers.addDefaultSampler(sampler, names);
    }

    @Override
    public boolean addDefaultSampler(TextureType type, IntSupplier texture, ValueUpdateNotifier notifier, GlSampler sampler, String ... names) {
        return this.samplers.addDefaultSampler(type, texture, notifier, sampler, names);
    }

    @Override
    public boolean addDynamicSampler(IntSupplier sampler, String ... names) {
        return this.samplers.addDynamicSampler(sampler, names);
    }

    @Override
    public boolean addDynamicSampler(TextureType type, IntSupplier texture, GlSampler sampler, String ... names) {
        return this.samplers.addDynamicSampler(type, texture, sampler, names);
    }

    @Override
    public boolean addDynamicSampler(IntSupplier sampler, ValueUpdateNotifier notifier, String ... names) {
        return this.samplers.addDynamicSampler(sampler, notifier, names);
    }

    @Override
    public boolean addDynamicSampler(TextureType type, IntSupplier texture, ValueUpdateNotifier notifier, GlSampler sampler, String ... names) {
        return this.samplers.addDynamicSampler(type, texture, notifier, sampler, names);
    }

    @Override
    public boolean hasImage(String name) {
        return this.images.hasImage(name);
    }

    @Override
    public void addTextureImage(IntSupplier textureID, InternalTextureFormat internalFormat, String name) {
        this.images.addTextureImage(textureID, internalFormat, name);
    }
}

