/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.shaders.Uniform
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Uniform.class})
public class MixinUniform {
    @Inject(method={"glGetUniformLocation"}, at={@At(value="RETURN")}, cancellable=true)
    private static void iris$glGetUniformLocation(int programId, CharSequence name, CallbackInfoReturnable<Integer> cir) {
        int location = (Integer)cir.getReturnValue();
        if (location == -1 && name.equals("Sampler0") && (location = GlStateManager._glGetUniformLocation((int)programId, (CharSequence)"tex")) == -1 && (location = GlStateManager._glGetUniformLocation((int)programId, (CharSequence)"gtexture")) == -1) {
            location = GlStateManager._glGetUniformLocation((int)programId, (CharSequence)"texture");
        }
        if ((Integer)cir.getReturnValue() == -1 && location != -1) {
            cir.setReturnValue((Object)location);
        }
    }
}

