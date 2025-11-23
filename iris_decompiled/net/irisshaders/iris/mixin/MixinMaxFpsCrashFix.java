/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.Options
 *  net.minecraft.client.Options$FieldAccess
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.Slice
 */
package net.irisshaders.iris.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value={Options.class})
public abstract class MixinMaxFpsCrashFix {
    @Redirect(method={"processOptions"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Options$FieldAccess;process(Ljava/lang/String;Lnet/minecraft/client/OptionInstance;)V"), slice=@Slice(from=@At(value="CONSTANT", args={"stringValue=maxFps"}), to=@At(value="CONSTANT", args={"stringValue=graphicsMode"})), allow=1)
    private void iris$resetFramerateLimit(Options.FieldAccess instance, String name, OptionInstance<Integer> option) {
        if ((Integer)option.m_231551_() == 0) {
            option.m_231514_((Object)120);
        }
        instance.m_213982_(name, option);
    }
}

