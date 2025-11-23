/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$BooleanState
 *  org.lwjgl.opengl.GL11
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.BooleanStateExtended;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GlStateManager.BooleanState.class})
public class MixinBooleanState
implements BooleanStateExtended {
    @Shadow
    public boolean f_84586_;
    @Shadow
    @Final
    private int f_84585_;
    @Unique
    private boolean stateUnknown;

    @Inject(method={"setEnabled"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$setUnknownState(boolean enable, CallbackInfo ci) {
        if (this.stateUnknown) {
            ci.cancel();
            this.f_84586_ = enable;
            this.stateUnknown = false;
            if (enable) {
                GL11.glEnable((int)this.f_84585_);
            } else {
                GL11.glDisable((int)this.f_84585_);
            }
        }
    }

    @Override
    public void setUnknownState() {
        this.stateUnknown = true;
    }
}

