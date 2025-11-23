/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  net.minecraft.client.renderer.ChunkBufferBuilderPack
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Map;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ChunkBufferBuilderPack.class})
public interface ChunkBufferBuilderPackAccessor {
    @Accessor
    public Map<RenderType, BufferBuilder> getBuilders();
}

