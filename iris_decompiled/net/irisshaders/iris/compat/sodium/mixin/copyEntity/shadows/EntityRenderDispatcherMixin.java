/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils
 *  net.caffeinemc.mods.sodium.api.math.MatrixHelper
 *  net.caffeinemc.mods.sodium.api.util.ColorABGR
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.lwjgl.system.MemoryStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.copyEntity.shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
    @Unique
    private static final int SHADOW_COLOR = ColorABGR.pack((float)1.0f, (float)1.0f, (float)1.0f);

    @Inject(method={"renderBlockShadow"}, at={@At(value="HEAD")}, cancellable=true)
    private static void renderShadowPartFast(PoseStack.Pose entry, VertexConsumer vertices, ChunkAccess chunk, LevelReader world, BlockPos pos, double x, double y, double z, float radius, float opacity, CallbackInfo ci) {
        VertexBufferWriter writer = VertexConsumerUtils.convertOrLog((VertexConsumer)vertices);
        if (writer == null) {
            return;
        }
        ci.cancel();
        BlockPos blockPos = pos.m_7495_();
        BlockState blockState = world.m_8055_(blockPos);
        if (blockState.m_60799_() == RenderShape.INVISIBLE || !blockState.m_60838_((BlockGetter)world, blockPos)) {
            return;
        }
        int light = world.m_46803_(pos);
        if (light <= 3) {
            return;
        }
        VoxelShape voxelShape = blockState.m_60808_((BlockGetter)world, blockPos);
        if (voxelShape.m_83281_()) {
            return;
        }
        float brightness = LightTexture.m_234316_((DimensionType)world.m_6042_(), (int)light);
        float alpha = (float)(((double)opacity - (y - (double)pos.m_123342_()) / 2.0) * 0.5 * (double)brightness);
        if (alpha >= 0.0f) {
            if (alpha > 1.0f) {
                alpha = 1.0f;
            }
            AABB box = voxelShape.m_83215_();
            float minX = (float)((double)pos.m_123341_() + box.f_82288_ - x);
            float maxX = (float)((double)pos.m_123341_() + box.f_82291_ - x);
            float minY = (float)((double)pos.m_123342_() + box.f_82289_ - y);
            float minZ = (float)((double)pos.m_123343_() + box.f_82290_ - z);
            float maxZ = (float)((double)pos.m_123343_() + box.f_82293_ - z);
            EntityRenderDispatcherMixin.renderShadowPart(entry, writer, radius, alpha, minX, maxX, minY, minZ, maxZ);
        }
    }

    @Unique
    private static void renderShadowPart(PoseStack.Pose matrices, VertexBufferWriter writer, float radius, float alpha, float minX, float maxX, float minY, float minZ, float maxZ) {
        float size = 0.5f * (1.0f / radius);
        float u1 = -minX * size + 0.5f;
        float u2 = -maxX * size + 0.5f;
        float v1 = -minZ * size + 0.5f;
        float v2 = -maxZ * size + 0.5f;
        Matrix3f matNormal = matrices.m_252943_();
        Matrix4f matPosition = matrices.m_252922_();
        int color = ColorABGR.withAlpha((int)SHADOW_COLOR, (float)alpha);
        int normal = MatrixHelper.transformNormal((Matrix3f)matNormal, (Direction)Direction.UP);
        try (MemoryStack stack = MemoryStack.stackPush();){
            long buffer;
            long ptr = buffer = stack.nmalloc(144);
            EntityRenderDispatcherMixin.writeShadowVertex(ptr, matPosition, minX, minY, minZ, u1, v1, color, normal);
            EntityRenderDispatcherMixin.writeShadowVertex(ptr += 36L, matPosition, minX, minY, maxZ, u1, v2, color, normal);
            EntityRenderDispatcherMixin.writeShadowVertex(ptr += 36L, matPosition, maxX, minY, maxZ, u2, v2, color, normal);
            EntityRenderDispatcherMixin.writeShadowVertex(ptr += 36L, matPosition, maxX, minY, minZ, u2, v1, color, normal);
            ptr += 36L;
            writer.push(stack, buffer, 4, ModelVertex.FORMAT);
        }
    }

    @Unique
    private static void writeShadowVertex(long ptr, Matrix4f matPosition, float x, float y, float z, float u, float v, int color, int normal) {
        float xt = MatrixHelper.transformPositionX((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        float yt = MatrixHelper.transformPositionY((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        float zt = MatrixHelper.transformPositionZ((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        ModelVertex.write((long)ptr, (float)xt, (float)yt, (float)zt, (int)color, (float)u, (float)v, (int)0xF000F0, (int)OverlayTexture.f_118083_, (int)normal);
    }
}

