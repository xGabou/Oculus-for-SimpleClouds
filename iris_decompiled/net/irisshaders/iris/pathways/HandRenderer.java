/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.client.model.data.ModelData
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.mixin.GameRendererAccessor;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class HandRenderer {
    public static final HandRenderer INSTANCE = new HandRenderer();
    public static final float DEPTH = 0.125f;
    private final FullyBufferedMultiBufferSource bufferSource = new FullyBufferedMultiBufferSource();
    private boolean ACTIVE;
    private boolean renderingSolid;

    private void setupGlState(GameRenderer gameRenderer, Camera camera, PoseStack poseStack, float tickDelta) {
        PoseStack.Pose pose = poseStack.m_85850_();
        Matrix4f scaleMatrix = new Matrix4f().scale(1.0f, 1.0f, 0.125f);
        scaleMatrix.mul((Matrix4fc)gameRenderer.m_253088_(((GameRendererAccessor)gameRenderer).invokeGetFov(camera, tickDelta, false)));
        gameRenderer.m_252879_(scaleMatrix);
        pose.m_252922_().identity();
        pose.m_252943_().identity();
        ((GameRendererAccessor)gameRenderer).invokeBobHurt(poseStack, tickDelta);
        if (((Boolean)Minecraft.m_91087_().f_91066_.m_231830_().m_231551_()).booleanValue()) {
            ((GameRendererAccessor)gameRenderer).invokeBobView(poseStack, tickDelta);
        }
    }

    private boolean canRender(Camera camera, GameRenderer gameRenderer) {
        return ((GameRendererAccessor)gameRenderer).getRenderHand() && !camera.m_90594_() && camera.m_90592_() instanceof Player && !((GameRendererAccessor)gameRenderer).getPanoramicMode() && !Minecraft.m_91087_().f_91066_.f_92062_ && (!(camera.m_90592_() instanceof LivingEntity) || !((LivingEntity)camera.m_90592_()).m_5803_()) && Minecraft.m_91087_().f_91072_.m_105295_() != GameType.SPECTATOR;
    }

    public boolean isHandTranslucent(InteractionHand hand) {
        Item item = Minecraft.m_91087_().f_91074_.m_6844_(hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND).m_41720_();
        if (item instanceof BlockItem) {
            BlockState state = ((BlockItem)item).m_40614_().m_49966_();
            BakedModel model = Minecraft.m_91087_().m_91289_().m_110910_(state);
            return model.getRenderTypes(state, Minecraft.m_91087_().f_91073_.f_46441_, ModelData.EMPTY).contains(RenderType.m_110466_());
        }
        return false;
    }

    public boolean isAnyHandTranslucent() {
        return this.isHandTranslucent(InteractionHand.MAIN_HAND) || this.isHandTranslucent(InteractionHand.OFF_HAND);
    }

    public void renderSolid(PoseStack poseStack, float tickDelta, Camera camera, GameRenderer gameRenderer, WorldRenderingPipeline pipeline) {
        if (!this.canRender(camera, gameRenderer) || !IrisApi.getInstance().isShaderPackInUse()) {
            return;
        }
        this.ACTIVE = true;
        pipeline.setPhase(WorldRenderingPhase.HAND_SOLID);
        poseStack.m_85836_();
        Minecraft.m_91087_().m_91307_().m_6180_("iris_hand");
        this.setupGlState(gameRenderer, camera, poseStack, tickDelta);
        this.renderingSolid = true;
        gameRenderer.f_109055_.m_109314_(tickDelta, poseStack, this.bufferSource.getUnflushableWrapper(), Minecraft.m_91087_().f_91074_, Minecraft.m_91087_().m_91290_().m_114394_(camera.m_90592_(), tickDelta));
        Minecraft.m_91087_().m_91307_().m_7238_();
        this.bufferSource.readyUp();
        this.bufferSource.m_109911_();
        gameRenderer.m_252879_(CapturedRenderingState.INSTANCE.getGbufferProjection());
        poseStack.m_85849_();
        this.renderingSolid = false;
        pipeline.setPhase(WorldRenderingPhase.NONE);
        this.ACTIVE = false;
    }

    public void renderTranslucent(PoseStack poseStack, float tickDelta, Camera camera, GameRenderer gameRenderer, WorldRenderingPipeline pipeline) {
        if (!(this.canRender(camera, gameRenderer) && this.isAnyHandTranslucent() && IrisApi.getInstance().isShaderPackInUse())) {
            return;
        }
        this.ACTIVE = true;
        pipeline.setPhase(WorldRenderingPhase.HAND_TRANSLUCENT);
        poseStack.m_85836_();
        Minecraft.m_91087_().m_91307_().m_6180_("iris_hand_translucent");
        this.setupGlState(gameRenderer, camera, poseStack, tickDelta);
        gameRenderer.f_109055_.m_109314_(tickDelta, poseStack, (MultiBufferSource.BufferSource)this.bufferSource, Minecraft.m_91087_().f_91074_, Minecraft.m_91087_().m_91290_().m_114394_(camera.m_90592_(), tickDelta));
        poseStack.m_85849_();
        Minecraft.m_91087_().m_91307_().m_7238_();
        gameRenderer.m_252879_(CapturedRenderingState.INSTANCE.getGbufferProjection());
        this.bufferSource.m_109911_();
        pipeline.setPhase(WorldRenderingPhase.NONE);
        this.ACTIVE = false;
    }

    public boolean isActive() {
        return this.ACTIVE;
    }

    public boolean isRenderingSolid() {
        return this.renderingSolid;
    }

    public FullyBufferedMultiBufferSource getBufferSource() {
        return this.bufferSource;
    }
}

