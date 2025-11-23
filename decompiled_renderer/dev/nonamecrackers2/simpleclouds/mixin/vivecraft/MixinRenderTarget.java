/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.nonamecrackers2.simpleclouds.mixin.vivecraft;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(value={RenderTarget.class}, priority=1001)
public class MixinRenderTarget {
    @Redirect(method={"modify$zcd000$vivecraft$noViewportChangeOnClear"}, at=@At(value="INVOKE", target="Lorg/vivecraft/client_xr/render_pass/RenderPassType;isWorldOnly()Z", remap=false))
    public boolean simpleclouds$disableNoViewportChangeOnClear(boolean flag) {
        return false;
    }
}

