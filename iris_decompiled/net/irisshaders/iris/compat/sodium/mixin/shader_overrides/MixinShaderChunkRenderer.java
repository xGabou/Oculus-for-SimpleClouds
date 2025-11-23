/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  me.jellysquid.mods.sodium.client.gl.device.RenderDevice
 *  me.jellysquid.mods.sodium.client.gl.shader.GlProgram
 *  me.jellysquid.mods.sodium.client.render.chunk.ShaderChunkRenderer
 *  me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface
 *  me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.shader_overrides;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.client.gl.device.RenderDevice;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkProgramOverrides;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkShaderInterface;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.ShaderChunkRendererExt;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ShaderChunkRenderer.class})
public class MixinShaderChunkRenderer
implements ShaderChunkRendererExt {
    @Shadow(remap=false)
    @Final
    protected ChunkVertexType vertexType;
    @Unique
    private IrisChunkProgramOverrides irisChunkProgramOverrides;
    @Unique
    private GlProgram<IrisChunkShaderInterface> override;
    @Shadow(remap=false)
    private GlProgram<ChunkShaderInterface> activeProgram;

    @Inject(method={"<init>"}, at={@At(value="RETURN")}, remap=false)
    private void iris$onInit(RenderDevice device, ChunkVertexType vertexType, CallbackInfo ci) {
        this.irisChunkProgramOverrides = new IrisChunkProgramOverrides();
    }

    @Inject(method={"begin"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void iris$begin(TerrainRenderPass pass, CallbackInfo ci) {
        this.override = this.irisChunkProgramOverrides.getProgramOverride(pass, this.vertexType);
        this.irisChunkProgramOverrides.bindFramebuffer(pass);
        if (this.override == null) {
            return;
        }
        ci.cancel();
        this.activeProgram = null;
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            RenderSystem.disableCull();
        }
        pass.startDrawing();
        this.override.bind();
        ((IrisChunkShaderInterface)((Object)this.override.getInterface())).setupState();
    }

    @Inject(method={"end"}, at={@At(value="HEAD")}, remap=false, cancellable=true)
    private void iris$onEnd(TerrainRenderPass pass, CallbackInfo ci) {
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        this.irisChunkProgramOverrides.unbindFramebuffer();
        if (this.override != null) {
            ((IrisChunkShaderInterface)((Object)this.override.getInterface())).restore();
            this.override.unbind();
            pass.endDrawing();
            this.override = null;
            ci.cancel();
        }
    }

    @Inject(method={"delete"}, at={@At(value="HEAD")}, remap=false)
    private void iris$onDelete(CallbackInfo ci) {
        this.irisChunkProgramOverrides.deleteShaders();
    }

    @Override
    public GlProgram<IrisChunkShaderInterface> iris$getOverride() {
        return this.override;
    }
}

