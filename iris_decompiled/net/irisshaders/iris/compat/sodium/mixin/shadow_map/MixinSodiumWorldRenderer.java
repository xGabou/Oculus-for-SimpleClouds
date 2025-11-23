/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer
 *  me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.server.level.BlockDestructionProgress
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.compat.sodium.mixin.shadow_map;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import java.util.SortedSet;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import net.irisshaders.iris.compat.sodium.mixin.shadow_map.SodiumWorldRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SodiumWorldRenderer.class})
public abstract class MixinSodiumWorldRenderer {
    private static int beList = 0;
    private static boolean renderLightsOnly;

    @Shadow
    private static void renderBlockEntity(PoseStack matrices, RenderBuffers bufferBuilders, Long2ObjectMap<SortedSet<BlockDestructionProgress>> blockBreakingProgressions, float tickDelta, MultiBufferSource.BufferSource immediate, double x, double y, double z, BlockEntityRenderDispatcher dispatcher, BlockEntity entity) {
        throw new IllegalStateException("maybe get Mixin?");
    }

    @Inject(method={"renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;)V"}, at={@At(value="HEAD")}, remap=false)
    private void resetEntityList(PoseStack matrices, RenderBuffers bufferBuilders, Long2ObjectMap<SortedSet<BlockDestructionProgress>> blockBreakingProgressions, float tickDelta, MultiBufferSource.BufferSource immediate, double x, double y, double z, BlockEntityRenderDispatcher blockEntityRenderer, CallbackInfo ci) {
        beList = 0;
    }

    @Redirect(method={"renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;)V"}, at=@At(value="INVOKE", target="Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;renderBlockEntity(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"), remap=false)
    private void addToList(PoseStack bufferBuilder, RenderBuffers entry, Long2ObjectMap<SortedSet<BlockDestructionProgress>> transformer, float stage, MultiBufferSource.BufferSource matrices, double bufferBuilders, double blockBreakingProgressions, double tickDelta, BlockEntityRenderDispatcher immediate, BlockEntity x) {
        if (!renderLightsOnly || x.m_58900_().m_60791_() > 0) {
            MixinSodiumWorldRenderer.renderBlockEntity(bufferBuilder, entry, transformer, stage, matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x);
            ++beList;
        }
    }

    @Redirect(method={"renderGlobalBlockEntities"}, at=@At(value="INVOKE", target="Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;renderBlockEntity(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"), remap=false)
    private void addToList2(PoseStack bufferBuilder, RenderBuffers entry, Long2ObjectMap<SortedSet<BlockDestructionProgress>> transformer, float stage, MultiBufferSource.BufferSource matrices, double bufferBuilders, double blockBreakingProgressions, double tickDelta, BlockEntityRenderDispatcher immediate, BlockEntity x) {
        if (!renderLightsOnly || x.m_58900_().m_60791_() > 0) {
            MixinSodiumWorldRenderer.renderBlockEntity(bufferBuilder, entry, transformer, stage, matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x);
            ++beList;
        }
    }

    @Inject(method={"isEntityVisible"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void iris$overrideEntityCulling(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            cir.setReturnValue((Object)true);
        }
    }

    @Redirect(method={"setupTerrain"}, remap=false, at=@At(value="INVOKE", target="Lme/jellysquid/mods/sodium/client/render/chunk/RenderSectionManager;needsUpdate()Z", remap=false))
    private boolean iris$forceChunkGraphRebuildInShadowPass(RenderSectionManager instance) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            return true;
        }
        return instance.needsUpdate();
    }

    static {
        ShadowRenderingState.setBlockEntityRenderFunction((shadowRenderer, bufferSource, modelView, camera, cameraX, cameraY, cameraZ, tickDelta, hasEntityFrustum, lightsOnly) -> {
            renderLightsOnly = lightsOnly;
            ((SodiumWorldRendererAccessor)SodiumWorldRenderer.instance()).invokeRenderBlockEntities(modelView, Minecraft.m_91087_().m_91269_(), (Long2ObjectMap<SortedSet<BlockDestructionProgress>>)Long2ObjectMaps.emptyMap(), tickDelta, bufferSource, cameraX, cameraY, cameraZ, Minecraft.m_91087_().m_167982_());
            ((SodiumWorldRendererAccessor)SodiumWorldRenderer.instance()).invokeRenderGlobalBlockEntities(modelView, Minecraft.m_91087_().m_91269_(), (Long2ObjectMap<SortedSet<BlockDestructionProgress>>)Long2ObjectMaps.emptyMap(), tickDelta, bufferSource, cameraX, cameraY, cameraZ, Minecraft.m_91087_().m_167982_());
            renderLightsOnly = false;
            return beList;
        });
    }
}

