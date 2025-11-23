/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.irisshaders.iris.targets.Blaze3dRenderTargetExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderTarget.class})
public class MixinRenderTarget
implements Blaze3dRenderTargetExt {
    @Shadow
    protected int f_83924_;
    @Unique
    private int iris$depthBufferVersion;
    @Unique
    private int iris$colorBufferVersion;

    @Inject(method={"destroyBuffers()V"}, at={@At(value="HEAD")})
    private void iris$onDestroyBuffers(CallbackInfo ci) {
        ++this.iris$depthBufferVersion;
        ++this.iris$colorBufferVersion;
    }

    @Override
    public int iris$getDepthBufferVersion() {
        return this.iris$depthBufferVersion;
    }

    @Override
    public int iris$getColorBufferVersion() {
        return this.iris$colorBufferVersion;
    }
}

