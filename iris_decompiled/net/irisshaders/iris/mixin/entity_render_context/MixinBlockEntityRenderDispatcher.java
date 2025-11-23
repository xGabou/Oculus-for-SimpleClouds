/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.irisshaders.iris.layer.BlockEntityRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BlockEntityRenderDispatcher.class})
public class MixinBlockEntityRenderDispatcher {
    private static final String RUN_REPORTED = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;tryRender(Lnet/minecraft/world/level/block/entity/BlockEntity;Ljava/lang/Runnable;)V";

    @ModifyVariable(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/entity/BlockEntityType;isValid(Lnet/minecraft/world/level/block/state/BlockState;)Z"), allow=1, require=1, argsOnly=true)
    private MultiBufferSource iris$wrapBufferSource(MultiBufferSource bufferSource, BlockEntity blockEntity) {
        if (!(bufferSource instanceof Groupable)) {
            return bufferSource;
        }
        BlockState state = blockEntity.m_58900_();
        Object2IntMap<BlockState> blockStateIds = WorldRenderingSettings.INSTANCE.getBlockStateIds();
        if (blockStateIds == null) {
            return bufferSource;
        }
        int intId = blockStateIds.getOrDefault((Object)state, -1);
        CapturedRenderingState.INSTANCE.setCurrentBlockEntity(intId);
        return type -> bufferSource.m_6299_((RenderType)OuterWrappedRenderType.wrapExactlyOnce("iris:is_block_entity", type, BlockEntityRenderStateShard.INSTANCE));
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;tryRender(Lnet/minecraft/world/level/block/entity/BlockEntity;Ljava/lang/Runnable;)V", shift=At.Shift.AFTER)})
    private void iris$afterRender(BlockEntity blockEntity, float tickDelta, PoseStack matrix, MultiBufferSource bufferSource, CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentBlockEntity(0);
    }
}

