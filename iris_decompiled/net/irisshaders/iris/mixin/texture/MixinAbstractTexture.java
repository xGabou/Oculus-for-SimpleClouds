/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.texture;

import net.irisshaders.iris.texture.TextureTracker;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractTexture.class})
public class MixinAbstractTexture {
    @Shadow
    protected int f_117950_;

    @Inject(method={"getId()I"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/platform/TextureUtil;generateTextureId()I", shift=At.Shift.BY, by=2)})
    private void iris$afterGenerateId(CallbackInfoReturnable<Integer> cir) {
        TextureTracker.INSTANCE.trackTexture(this.f_117950_, (AbstractTexture)this);
    }
}

