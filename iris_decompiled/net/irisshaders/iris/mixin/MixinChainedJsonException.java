/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.ChainedJsonException
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.helpers.FakeChainedJsonException;
import net.minecraft.server.ChainedJsonException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ChainedJsonException.class})
public class MixinChainedJsonException {
    @Inject(method={"forException"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$changeShaderParseException(Exception exception, CallbackInfoReturnable<ChainedJsonException> cir) {
        if (exception instanceof ShaderCompileException) {
            ShaderCompileException e = (ShaderCompileException)exception;
            cir.setReturnValue((Object)new FakeChainedJsonException(e));
        }
    }
}

