/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.TheEndPortalRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.entity.TheEndPortalBlockEntity
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TheEndPortalRenderer.class})
public class MixinTheEndPortalRenderer {
    @Unique
    private static final float RED = 0.075f;
    @Unique
    private static final float GREEN = 0.15f;
    @Unique
    private static final float BLUE = 0.2f;

    @Shadow
    protected float m_142491_() {
        return 0.75f;
    }

    @Shadow
    protected float m_142489_() {
        return 0.375f;
    }

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void iris$onRender(TheEndPortalBlockEntity entity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay, CallbackInfo ci) {
        if (!Iris.getCurrentPack().isPresent()) {
            return;
        }
        ci.cancel();
        VertexConsumer vertexConsumer = multiBufferSource.m_6299_(RenderType.m_110446_((ResourceLocation)TheEndPortalRenderer.f_112627_));
        Matrix4f pose = poseStack.m_85850_().m_252922_();
        Matrix3f normal = poseStack.m_85850_().m_252943_();
        float progress = SystemTimeUniforms.TIMER.getFrameTimeCounter() * 0.01f % 1.0f;
        float topHeight = this.m_142491_();
        float bottomHeight = this.m_142489_();
        this.quad(entity, vertexConsumer, pose, normal, Direction.UP, progress, overlay, light, 0.0f, topHeight, 1.0f, 1.0f, topHeight, 1.0f, 1.0f, topHeight, 0.0f, 0.0f, topHeight, 0.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.DOWN, progress, overlay, light, 0.0f, bottomHeight, 1.0f, 0.0f, bottomHeight, 0.0f, 1.0f, bottomHeight, 0.0f, 1.0f, bottomHeight, 1.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.NORTH, progress, overlay, light, 0.0f, topHeight, 0.0f, 1.0f, topHeight, 0.0f, 1.0f, bottomHeight, 0.0f, 0.0f, bottomHeight, 0.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.WEST, progress, overlay, light, 0.0f, topHeight, 1.0f, 0.0f, topHeight, 0.0f, 0.0f, bottomHeight, 0.0f, 0.0f, bottomHeight, 1.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.SOUTH, progress, overlay, light, 0.0f, topHeight, 1.0f, 0.0f, bottomHeight, 1.0f, 1.0f, bottomHeight, 1.0f, 1.0f, topHeight, 1.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.EAST, progress, overlay, light, 1.0f, topHeight, 1.0f, 1.0f, bottomHeight, 1.0f, 1.0f, bottomHeight, 0.0f, 1.0f, topHeight, 0.0f);
    }

    @Unique
    private void quad(TheEndPortalBlockEntity entity, VertexConsumer vertexConsumer, Matrix4f pose, Matrix3f normal, Direction direction, float progress, int overlay, int light, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        if (!entity.m_6665_(direction)) {
            return;
        }
        float nx = direction.m_122429_();
        float ny = direction.m_122430_();
        float nz = direction.m_122431_();
        vertexConsumer.m_252986_(pose, x1, y1, z1).m_85950_(0.075f, 0.15f, 0.2f, 1.0f).m_7421_(0.0f + progress, 0.0f + progress).m_86008_(overlay).m_85969_(light).m_252939_(normal, nx, ny, nz).m_5752_();
        vertexConsumer.m_252986_(pose, x2, y2, z2).m_85950_(0.075f, 0.15f, 0.2f, 1.0f).m_7421_(0.0f + progress, 0.2f + progress).m_86008_(overlay).m_85969_(light).m_252939_(normal, nx, ny, nz).m_5752_();
        vertexConsumer.m_252986_(pose, x3, y3, z3).m_85950_(0.075f, 0.15f, 0.2f, 1.0f).m_7421_(0.2f + progress, 0.2f + progress).m_86008_(overlay).m_85969_(light).m_252939_(normal, nx, ny, nz).m_5752_();
        vertexConsumer.m_252986_(pose, x4, y4, z4).m_85950_(0.075f, 0.15f, 0.2f, 1.0f).m_7421_(0.2f + progress, 0.0f + progress).m_86008_(overlay).m_85969_(light).m_252939_(normal, nx, ny, nz).m_5752_();
    }
}

