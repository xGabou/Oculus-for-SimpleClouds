/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  org.lwjgl.opengl.GL43C
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.blending.DepthColorStorage;
import net.irisshaders.iris.vertices.ImmediateState;
import org.lwjgl.opengl.GL43C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GlStateManager.class}, remap=false)
public class MixinGlStateManager_DepthColorOverride {
    @Inject(method={"_colorMask"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$colorMaskLock(boolean red, boolean green, boolean blue, boolean alpha, CallbackInfo ci) {
        if (DepthColorStorage.isDepthColorLocked()) {
            DepthColorStorage.deferColorMask(red, green, blue, alpha);
            ci.cancel();
        }
    }

    @Inject(method={"_depthMask"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$depthMaskLock(boolean enable, CallbackInfo ci) {
        if (DepthColorStorage.isDepthColorLocked()) {
            DepthColorStorage.deferDepthEnable(enable);
            ci.cancel();
        }
    }

    @Redirect(method={"_drawElements"}, at=@At(value="INVOKE", target="Lorg/lwjgl/opengl/GL11;glDrawElements(IIIJ)V"), remap=false)
    private static void iris$modify(int mode, int count, int type, long indices) {
        if (mode == 4 && ImmediateState.usingTessellation) {
            mode = 14;
        }
        GL43C.glDrawElements((int)mode, (int)count, (int)type, (long)indices);
    }

    @Inject(method={"_glUseProgram"}, at={@At(value="TAIL")}, remap=false)
    private static void iris$resetTessellation(int pInt0, CallbackInfo ci) {
        ImmediateState.usingTessellation = false;
    }
}

