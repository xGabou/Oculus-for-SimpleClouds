/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.gui;

import net.irisshaders.iris.gui.screen.HudHideable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ForgeGui.class})
public class MixinForgeGui {
    @Shadow
    public Minecraft getMinecraft() {
        return null;
    }

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void iris$handleHudHidingScreens(GuiGraphics pGui0, float pFloat1, CallbackInfo ci) {
        Screen screen = this.getMinecraft().f_91080_;
        if (screen instanceof HudHideable) {
            ci.cancel();
        }
    }
}

