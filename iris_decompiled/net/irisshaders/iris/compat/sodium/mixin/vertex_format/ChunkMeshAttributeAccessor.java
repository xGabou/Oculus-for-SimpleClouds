/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ChunkMeshAttribute.class})
public interface ChunkMeshAttributeAccessor {
    @Invoker(value="<init>")
    public static ChunkMeshAttribute createChunkMeshAttribute(String name, int ordinal) {
        throw new AssertionError();
    }
}

