/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.texture;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ResourceLocation.class}, priority=1010)
public class MixinResourceLocation {
    @Inject(method={"isValidPath"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$blockDUMMY(String string, CallbackInfoReturnable<Boolean> cir) {
        if (string.equals("DUMMY")) {
            cir.setReturnValue((Object)false);
        }
    }

    @Inject(method={"validPathChar"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$allowInvalidPaths(char c, CallbackInfoReturnable<Boolean> cir) {
        if (c >= 'A' && c <= 'Z') {
            cir.setReturnValue((Object)true);
        }
    }
}

