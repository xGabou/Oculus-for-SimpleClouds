/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute
 *  org.apache.commons.lang3.ArrayUtils
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.IrisChunkMeshAttributes;
import net.irisshaders.iris.compat.sodium.mixin.vertex_format.ChunkMeshAttributeAccessor;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkMeshAttribute.class})
public class MixinChunkMeshAttribute {
    @Shadow(remap=false)
    @Final
    @Mutable
    private static ChunkMeshAttribute[] $VALUES;

    static {
        int baseOrdinal = $VALUES.length;
        IrisChunkMeshAttributes.NORMAL = ChunkMeshAttributeAccessor.createChunkMeshAttribute("NORMAL", baseOrdinal);
        IrisChunkMeshAttributes.TANGENT = ChunkMeshAttributeAccessor.createChunkMeshAttribute("TANGENT", baseOrdinal + 1);
        IrisChunkMeshAttributes.MID_TEX_COORD = ChunkMeshAttributeAccessor.createChunkMeshAttribute("MID_TEX_COORD", baseOrdinal + 2);
        IrisChunkMeshAttributes.BLOCK_ID = ChunkMeshAttributeAccessor.createChunkMeshAttribute("BLOCK_ID", baseOrdinal + 3);
        IrisChunkMeshAttributes.MID_BLOCK = ChunkMeshAttributeAccessor.createChunkMeshAttribute("MID_BLOCK", baseOrdinal + 4);
        $VALUES = (ChunkMeshAttribute[])ArrayUtils.addAll((Object[])$VALUES, (Object[])new ChunkMeshAttribute[]{IrisChunkMeshAttributes.NORMAL, IrisChunkMeshAttributes.TANGENT, IrisChunkMeshAttributes.MID_TEX_COORD, IrisChunkMeshAttributes.BLOCK_ID, IrisChunkMeshAttributes.MID_BLOCK});
    }
}

