/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeBinding
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat
 *  me.jellysquid.mods.sodium.client.gl.buffer.GlBuffer
 *  me.jellysquid.mods.sodium.client.gl.device.RenderDevice
 *  me.jellysquid.mods.sodium.client.gl.tessellation.TessellationBinding
 *  me.jellysquid.mods.sodium.client.render.chunk.DefaultChunkRenderer
 *  me.jellysquid.mods.sodium.client.render.chunk.ShaderChunkRenderer
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeBinding;
import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat;
import me.jellysquid.mods.sodium.client.gl.buffer.GlBuffer;
import me.jellysquid.mods.sodium.client.gl.device.RenderDevice;
import me.jellysquid.mods.sodium.client.gl.tessellation.TessellationBinding;
import me.jellysquid.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.IrisChunkMeshAttributes;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={DefaultChunkRenderer.class})
public abstract class MixinRegionChunkRenderer
extends ShaderChunkRenderer {
    public MixinRegionChunkRenderer(RenderDevice device, ChunkVertexType vertexType) {
        super(device, vertexType);
    }

    @Redirect(remap=false, method={"createRegionTessellation"}, at=@At(value="INVOKE", target="Lme/jellysquid/mods/sodium/client/gl/tessellation/TessellationBinding;forVertexBuffer(Lme/jellysquid/mods/sodium/client/gl/buffer/GlBuffer;[Lme/jellysquid/mods/sodium/client/gl/attribute/GlVertexAttributeBinding;)Lme/jellysquid/mods/sodium/client/gl/tessellation/TessellationBinding;"))
    private TessellationBinding iris$onInit(GlBuffer buffer, GlVertexAttributeBinding[] attributes) {
        if (!WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return TessellationBinding.forVertexBuffer((GlBuffer)buffer, (GlVertexAttributeBinding[])attributes);
        }
        GlVertexFormat vertexFormat = this.vertexFormat;
        attributes = new GlVertexAttributeBinding[]{new GlVertexAttributeBinding(1, vertexFormat.getAttribute((Enum)ChunkMeshAttribute.POSITION_MATERIAL_MESH)), new GlVertexAttributeBinding(2, vertexFormat.getAttribute((Enum)ChunkMeshAttribute.COLOR_SHADE)), new GlVertexAttributeBinding(3, vertexFormat.getAttribute((Enum)ChunkMeshAttribute.BLOCK_TEXTURE)), new GlVertexAttributeBinding(4, vertexFormat.getAttribute((Enum)ChunkMeshAttribute.LIGHT_TEXTURE)), new GlVertexAttributeBinding(14, vertexFormat.getAttribute((Enum)IrisChunkMeshAttributes.MID_BLOCK)), new GlVertexAttributeBinding(11, vertexFormat.getAttribute((Enum)IrisChunkMeshAttributes.BLOCK_ID)), new GlVertexAttributeBinding(12, vertexFormat.getAttribute((Enum)IrisChunkMeshAttributes.MID_TEX_COORD)), new GlVertexAttributeBinding(13, vertexFormat.getAttribute((Enum)IrisChunkMeshAttributes.TANGENT)), new GlVertexAttributeBinding(10, vertexFormat.getAttribute((Enum)IrisChunkMeshAttributes.NORMAL))};
        return TessellationBinding.forVertexBuffer((GlBuffer)buffer, (GlVertexAttributeBinding[])attributes);
    }
}

