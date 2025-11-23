/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  org.joml.Vector2f
 *  org.joml.Vector3i
 */
package net.irisshaders.iris.gl.program;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.ProgramManager;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.FilledIndirectPointer;
import org.joml.Vector2f;
import org.joml.Vector3i;

public final class ComputeProgram
extends GlResource {
    private final ProgramUniforms uniforms;
    private final ProgramSamplers samplers;
    private final ProgramImages images;
    private final int[] localSize = new int[3];
    private Vector3i absoluteWorkGroups;
    private Vector2f relativeWorkGroups;
    private float cachedWidth;
    private float cachedHeight;
    private Vector3i cachedWorkGroups;
    private FilledIndirectPointer indirectPointer;

    ComputeProgram(int program, ProgramUniforms uniforms, ProgramSamplers samplers, ProgramImages images) {
        super(program);
        IrisRenderSystem.getProgramiv(program, 33383, this.localSize);
        this.uniforms = uniforms;
        this.samplers = samplers;
        this.images = images;
    }

    public static void unbind() {
        ProgramUniforms.clearActiveUniforms();
        ProgramManager.m_85578_((int)0);
    }

    public void setWorkGroupInfo(Vector2f relativeWorkGroups, Vector3i absoluteWorkGroups, FilledIndirectPointer indirectPointer) {
        this.relativeWorkGroups = relativeWorkGroups;
        this.absoluteWorkGroups = absoluteWorkGroups;
        this.indirectPointer = indirectPointer;
    }

    public Vector3i getWorkGroups(float width, float height) {
        if (this.indirectPointer != null) {
            return null;
        }
        if (this.cachedWidth != width || this.cachedHeight != height || this.cachedWorkGroups == null) {
            this.cachedWidth = width;
            this.cachedHeight = height;
            this.cachedWorkGroups = this.absoluteWorkGroups != null ? this.absoluteWorkGroups : (this.relativeWorkGroups != null ? new Vector3i((int)Math.ceil(Math.ceil(width * this.relativeWorkGroups.x) / (double)this.localSize[0]), (int)Math.ceil(Math.ceil(height * this.relativeWorkGroups.y) / (double)this.localSize[1]), 1) : new Vector3i((int)Math.ceil(width / (float)this.localSize[0]), (int)Math.ceil(height / (float)this.localSize[1]), 1));
        }
        return this.cachedWorkGroups;
    }

    public void use() {
        ProgramManager.m_85578_((int)this.getGlId());
        this.uniforms.update();
        this.samplers.update();
        this.images.update();
    }

    public void dispatch(float width, float height) {
        if (!Iris.getPipelineManager().getPipeline().map(WorldRenderingPipeline::allowConcurrentCompute).orElse(false).booleanValue()) {
            IrisRenderSystem.memoryBarrier(8232);
        }
        if (this.indirectPointer != null) {
            IrisRenderSystem.bindBuffer(37102, this.indirectPointer.buffer());
            IrisRenderSystem.dispatchComputeIndirect(this.indirectPointer.offset());
        } else {
            IrisRenderSystem.dispatchCompute(this.getWorkGroups(width, height));
        }
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

