/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gui.options.OptionImpl
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.compat.sodium.mixin.options;

import java.util.function.BooleanSupplier;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import net.irisshaders.iris.compat.sodium.impl.options.OptionImplExtended;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={OptionImpl.class})
public class MixinOptionImpl
implements OptionImplExtended {
    @Unique
    private BooleanSupplier iris$dynamicallyEnabled;

    @Override
    public void iris$dynamicallyEnable(BooleanSupplier enabled) {
        this.iris$dynamicallyEnabled = enabled;
    }

    @Inject(method={"isAvailable()Z"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void iris$dynamicallyEnable(CallbackInfoReturnable<Boolean> cir) {
        if (this.iris$dynamicallyEnabled != null) {
            cir.setReturnValue((Object)this.iris$dynamicallyEnabled.getAsBoolean());
        }
    }
}

