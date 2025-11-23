/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2IntFunction
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
    private static final String RENDER_SHADOW = "renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V";
    private static final String RENDER_BLOCK_SHADOW = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderBlockShadow(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;DDDFF)V";
    @Unique
    private static final NamespacedId shadowId = new NamespacedId("minecraft", "entity_shadow");
    @Unique
    private static final NamespacedId flameId = new NamespacedId("minecraft", "entity_flame");
    @Unique
    private static int cachedId;

    @Inject(method={"renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$maybeSuppressEntityShadow(PoseStack poseStack, MultiBufferSource bufferSource, Entity entity, float opacity, float tickDelta, LevelReader level, float radius, CallbackInfo ci) {
        if (!MixinEntityRenderDispatcher.iris$maybeSuppressShadow(ci)) {
            Object2IntFunction<NamespacedId> entityIds = WorldRenderingSettings.INSTANCE.getEntityIds();
            if (entityIds == null) {
                return;
            }
            cachedId = CapturedRenderingState.INSTANCE.getCurrentRenderedEntity();
            CapturedRenderingState.INSTANCE.setCurrentEntity(entityIds.getInt((Object)shadowId));
        }
    }

    @Inject(method={"renderShadow"}, at={@At(value="RETURN")})
    private static void restoreShadow(PoseStack pPoseStack0, MultiBufferSource pMultiBufferSource1, Entity pEntity2, float pFloat3, float pFloat4, LevelReader pLevelReader5, float pFloat6, CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentEntity(cachedId);
        cachedId = 0;
    }

    @Inject(method={"renderBlockShadow"}, at={@At(value="HEAD")}, cancellable=true)
    private static void renderBlockShadow(PoseStack.Pose pPoseStack$Pose0, VertexConsumer pVertexConsumer1, ChunkAccess pChunkAccess2, LevelReader pLevelReader3, BlockPos pBlockPos4, double pDouble5, double pDouble6, double pDouble7, float pFloat8, float pFloat9, CallbackInfo ci) {
        MixinEntityRenderDispatcher.iris$maybeSuppressShadow(ci);
    }

    @Inject(method={"renderOffsetShadow"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false, expect=0)
    private static void iris$maybeSuppressEntityShadow(PoseStack poseStack, MultiBufferSource bufferSource, Entity entity, float opacity, float tickDelta, LevelReader level, float radius, Vec3 offset, CallbackInfo ci) {
        MixinEntityRenderDispatcher.iris$maybeSuppressShadow(ci);
    }

    @Unique
    private static boolean iris$maybeSuppressShadow(CallbackInfo ci) {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null && pipeline.shouldDisableVanillaEntityShadows()) {
            ci.cancel();
            return true;
        }
        return false;
    }

    @Inject(method={"renderFlame"}, at={@At(value="HEAD")})
    private void iris$setFlameId(PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity, CallbackInfo ci) {
        Object2IntFunction<NamespacedId> entityIds = WorldRenderingSettings.INSTANCE.getEntityIds();
        if (entityIds == null) {
            return;
        }
        cachedId = CapturedRenderingState.INSTANCE.getCurrentRenderedEntity();
        CapturedRenderingState.INSTANCE.setCurrentEntity(entityIds.getInt((Object)flameId));
    }

    @Inject(method={"renderFlame"}, at={@At(value="RETURN")})
    private void restoreFlameId(PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity, CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentEntity(cachedId);
        cachedId = 0;
    }
}

