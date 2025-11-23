/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.shaders.ProgramManager
 */
package net.irisshaders.iris.gl.program;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.ProgramManager;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;

public final class Program
extends GlResource {
    private final ProgramUniforms uniforms;
    private final ProgramSamplers samplers;
    private final ProgramImages images;

    Program(int program, ProgramUniforms uniforms, ProgramSamplers samplers, ProgramImages images) {
        super(program);
        this.uniforms = uniforms;
        this.samplers = samplers;
        this.images = images;
    }

    public static void unbind() {
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        ProgramManager.m_85578_((int)0);
    }

    public void use() {
        IrisRenderSystem.memoryBarrier(8232);
        ProgramManager.m_85578_((int)this.getGlId());
        this.uniforms.update();
        this.samplers.update();
        this.images.update();
    }

    @Override
    public void destroyInternal() {
        GlStateManager.glDeleteProgram((int)this.getGlId());
    }

    @Deprecated
    public int getProgramId() {
        return this.getGlId();
    }

    public int getActiveImages() {
        return this.images.getActiveImages();
    }
}

