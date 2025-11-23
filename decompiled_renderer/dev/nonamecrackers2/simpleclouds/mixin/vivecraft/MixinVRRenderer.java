/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.nonamecrackers2.simpleclouds.mixin.vivecraft;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"org.vivecraft.client_vr.provider.VRRenderer"}, remap=false)
public class MixinVRRenderer {
    @Inject(method={"setupRenderConfiguration"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V")})
    public void simpleclouds$reloadRenderer_setupRenderConfiguration(CallbackInfo ci) {
        SimpleCloudsRenderer.getOptionalInstance().ifPresent(renderer -> renderer.m_6213_(Minecraft.m_91087_().m_91098_()));
    }
}

