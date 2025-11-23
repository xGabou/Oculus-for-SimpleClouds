/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.screen.HudHideable;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Gui.class})
public class MixinGui {
    @Shadow
    @Final
    private Minecraft f_92986_;

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void iris$handleHudHidingScreens(GuiGraphics pGui0, float pFloat1, CallbackInfo ci) {
        Screen screen = this.f_92986_.f_91080_;
        if (screen instanceof HudHideable) {
            ci.cancel();
        }
    }

    @Inject(method={"renderVignette"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$disableVignetteRendering(GuiGraphics pGui0, Entity pEntity1, CallbackInfo ci) {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null && !pipeline.shouldRenderVignette()) {
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            ci.cancel();
        }
    }
}

