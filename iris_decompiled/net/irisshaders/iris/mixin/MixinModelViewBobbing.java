/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  net.minecraft.client.renderer.GameRenderer
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.Slice
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public class MixinModelViewBobbing {
    @Unique
    private Matrix4f bobbingEffectsModel;
    @Unique
    private boolean areShadersOn;

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    private void iris$saveShadersOn(float pGameRenderer0, long pLong1, PoseStack pPoseStack2, CallbackInfo ci) {
        this.areShadersOn = IrisApi.getInstance().isShaderPackInUse();
    }

    @ModifyArg(method={"renderLevel"}, index=0, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private PoseStack iris$separateViewBobbing(PoseStack stack) {
        if (!this.areShadersOn) {
            return stack;
        }
        stack.m_85836_();
        stack.m_85850_().m_252922_().identity();
        return stack;
    }

    @Redirect(method={"renderLevel"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;"), slice=@Slice(from=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"), to=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;resetProjectionMatrix(Lorg/joml/Matrix4f;)V")))
    private PoseStack.Pose iris$saveBobbing(PoseStack stack) {
        if (!this.areShadersOn) {
            return stack.m_85850_();
        }
        this.bobbingEffectsModel = new Matrix4f((Matrix4fc)stack.m_85850_().m_252922_());
        stack.m_85849_();
        return stack.m_85850_();
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;resetProjectionMatrix(Lorg/joml/Matrix4f;)V")})
    private void iris$applyBobbingToModelView(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) {
        if (!this.areShadersOn) {
            return;
        }
        matrix.m_85850_().m_252922_().mul((Matrix4fc)this.bobbingEffectsModel);
        this.bobbingEffectsModel = null;
    }
}

