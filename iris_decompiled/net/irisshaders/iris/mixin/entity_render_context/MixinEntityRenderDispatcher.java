/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.Object2IntFunction
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.monster.ZombieVillager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.irisshaders.iris.layer.EntityRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
    @ModifyVariable(method={"render"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift=At.Shift.AFTER), allow=1, require=1)
    private MultiBufferSource iris$beginEntityRender(MultiBufferSource bufferSource, Entity entity) {
        int intId;
        ZombieVillager zombie;
        if (!(bufferSource instanceof Groupable)) {
            return bufferSource;
        }
        Object2IntFunction<NamespacedId> entityIds = WorldRenderingSettings.INSTANCE.getEntityIds();
        if (entityIds == null) {
            return bufferSource;
        }
        if (entity instanceof ZombieVillager && (zombie = (ZombieVillager)entity).m_34408_() && WorldRenderingSettings.INSTANCE.hasVillagerConversionId()) {
            intId = entityIds.applyAsInt((Object)new NamespacedId("minecraft", "zombie_villager_converting"));
        } else {
            ResourceLocation entityId = BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_());
            intId = entityIds.applyAsInt((Object)new NamespacedId(entityId.m_135827_(), entityId.m_135815_()));
        }
        CapturedRenderingState.INSTANCE.setCurrentEntity(intId);
        return type -> bufferSource.m_6299_((RenderType)OuterWrappedRenderType.wrapExactlyOnce("iris:is_entity", type, EntityRenderStateShard.INSTANCE));
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")})
    private void iris$endEntityRender(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentEntity(0);
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(0);
    }
}

