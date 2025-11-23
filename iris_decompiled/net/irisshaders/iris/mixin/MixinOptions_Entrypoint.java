/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Options
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.Iris;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Options.class}, priority=990)
public class MixinOptions_Entrypoint {
    @Unique
    private static boolean iris$initialized;

    @Inject(method={"load()V"}, at={@At(value="HEAD")})
    private void iris$beforeLoadOptions(CallbackInfo ci) {
        if (iris$initialized) {
            return;
        }
        iris$initialized = true;
        Iris.onEarlyInitialize();
    }
}

