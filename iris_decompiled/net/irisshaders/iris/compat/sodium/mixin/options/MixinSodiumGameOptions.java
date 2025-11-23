/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gui.SodiumGameOptions
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.options;

import java.io.IOException;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.irisshaders.iris.Iris;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SodiumGameOptions.class})
public class MixinSodiumGameOptions {
    @Inject(method={"writeToDisk"}, at={@At(value="RETURN")}, remap=false)
    private static void iris$writeIrisConfig(CallbackInfo ci) {
        try {
            if (Iris.getIrisConfig() != null) {
                Iris.getIrisConfig().save();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

