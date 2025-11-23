/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.statelisteners;

import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderSystem.class})
public class MixinRenderSystem {
    private static Runnable fogStartListener;
    private static Runnable fogEndListener;

    @Inject(method={"_setShaderFogStart"}, at={@At(value="FIELD", target="Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogStart:F", shift=At.Shift.AFTER)})
    private static void iris$onFogStart(float start, CallbackInfo ci) {
        if (fogStartListener != null) {
            fogStartListener.run();
        }
    }

    @Inject(method={"_setShaderFogEnd"}, at={@At(value="FIELD", target="Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogEnd:F", shift=At.Shift.AFTER)})
    private static void iris$onFogEnd(float end, CallbackInfo ci) {
        if (fogEndListener != null) {
            fogEndListener.run();
        }
    }

    static {
        StateUpdateNotifiers.fogStartNotifier = listener -> {
            fogStartListener = listener;
        };
        StateUpdateNotifiers.fogEndNotifier = listener -> {
            fogEndListener = listener;
        };
    }
}

