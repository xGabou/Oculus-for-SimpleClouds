/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.Slice
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package net.irisshaders.iris.mixin.integrationtest;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={RenderTarget.class})
public class MixinRenderTarget_StencilBufferTest {
    private static final boolean STENCIL = true;

    @ModifyArgs(method={"createBuffers"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal=0))
    public void init(Args args) {
        args.set(2, (Object)36013);
        args.set(6, (Object)34041);
        args.set(7, (Object)36269);
    }

    @ModifyArgs(method={"createBuffers"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V"), slice=@Slice(from=@At(value="FIELD", target="Lcom/mojang/blaze3d/pipeline/RenderTarget;useDepth:Z", ordinal=1)))
    public void init2(Args args) {
        args.set(1, (Object)33306);
    }
}

