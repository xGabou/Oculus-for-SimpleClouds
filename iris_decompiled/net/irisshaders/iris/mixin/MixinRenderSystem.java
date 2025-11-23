/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.samplers.IrisSamplers;
import net.irisshaders.iris.texture.TextureTracker;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={RenderSystem.class})
public class MixinRenderSystem {
    @Inject(method={"initRenderer"}, at={@At(value="RETURN")}, remap=false)
    private static void iris$onRendererInit(int debugVerbosity, boolean alwaysFalse, CallbackInfo ci) {
        Iris.duringRenderSystemInit();
        GLDebug.reloadDebugState();
        IrisRenderSystem.initRenderer();
        IrisSamplers.initRenderer();
        Iris.onRenderSystemInit();
    }

    @Inject(method={"_setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/AbstractTexture;getId()I", shift=At.Shift.AFTER)}, locals=LocalCapture.CAPTURE_FAILHARD)
    private static void _setShaderTexture(int unit, ResourceLocation resourceLocation, CallbackInfo ci, TextureManager lv, AbstractTexture tex) {
        TextureTracker.INSTANCE.onSetShaderTexture(unit, tex.m_117963_());
    }

    @Inject(method={"_setShaderTexture(II)V"}, at={@At(value="RETURN")}, remap=false)
    private static void _setShaderTexture(int unit, int glId, CallbackInfo ci) {
        TextureTracker.INSTANCE.onSetShaderTexture(unit, glId);
    }
}

