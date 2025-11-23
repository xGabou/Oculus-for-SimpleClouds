/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion$DeviceResources
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshFormats
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshFormats;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.IrisModelVertexFormats;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderRegion.DeviceResources.class})
public class MixinRenderRegionArenas {
    @Redirect(method={"<init>"}, remap=false, at=@At(value="FIELD", target="Lme/jellysquid/mods/sodium/client/render/chunk/vertex/format/ChunkMeshFormats;COMPACT:Lme/jellysquid/mods/sodium/client/render/chunk/vertex/format/ChunkVertexType;", remap=false))
    private ChunkVertexType iris$useExtendedStride() {
        return WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat() ? IrisModelVertexFormats.MODEL_VERTEX_XHFP : ChunkMeshFormats.COMPACT;
    }
}

