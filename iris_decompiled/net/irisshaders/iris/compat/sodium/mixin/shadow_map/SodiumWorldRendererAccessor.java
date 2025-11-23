/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.server.level.BlockDestructionProgress
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.compat.sodium.mixin.shadow_map;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.SortedSet;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={SodiumWorldRenderer.class})
public interface SodiumWorldRendererAccessor {
    @Invoker
    public void invokeRenderBlockEntities(PoseStack var1, RenderBuffers var2, Long2ObjectMap<SortedSet<BlockDestructionProgress>> var3, float var4, MultiBufferSource.BufferSource var5, double var6, double var8, double var10, BlockEntityRenderDispatcher var12);

    @Invoker
    public void invokeRenderGlobalBlockEntities(PoseStack var1, RenderBuffers var2, Long2ObjectMap<SortedSet<BlockDestructionProgress>> var3, float var4, MultiBufferSource.BufferSource var5, double var6, double var8, double var10, BlockEntityRenderDispatcher var12);
}

