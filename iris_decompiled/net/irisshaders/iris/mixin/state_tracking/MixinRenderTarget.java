/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  net.minecraft.client.Minecraft
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.state_tracking;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.irisshaders.iris.Iris;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderTarget.class})
public class MixinRenderTarget {
    @Inject(method={"bindWrite(Z)V"}, at={@At(value="RETURN")})
    private void iris$onBindFramebuffer(boolean bl, CallbackInfo ci) {
        boolean mainBound = this == Minecraft.m_91087_().m_91385_();
        Iris.getPipelineManager().getPipeline().ifPresent(pipeline -> pipeline.getRenderTargetStateListener().setIsMainBound(mainBound));
    }
}

