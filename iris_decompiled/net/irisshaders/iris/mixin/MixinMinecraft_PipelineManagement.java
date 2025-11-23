/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.Iris;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Minecraft.class})
public class MixinMinecraft_PipelineManagement {
    @Inject(method={"clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V"}, at={@At(value="HEAD")})
    public void iris$trackLastDimensionOnLeave(Screen arg, CallbackInfo ci) {
        Iris.lastDimension = Iris.getCurrentDimension();
    }

    @Inject(method={"setLevel"}, at={@At(value="HEAD")})
    private void iris$trackLastDimensionOnLevelChange(@Nullable ClientLevel level, CallbackInfo ci) {
        Iris.lastDimension = Iris.getCurrentDimension();
    }

    @Inject(method={"updateLevelInEngines"}, at={@At(value="HEAD")})
    private void iris$resetPipeline(@Nullable ClientLevel level, CallbackInfo ci) {
        if (Iris.getCurrentDimension() != Iris.lastDimension) {
            Iris.logger.info("Reloading pipeline on dimension change: " + Iris.lastDimension + " => " + Iris.getCurrentDimension());
            Iris.getPipelineManager().destroyPipeline();
            if (level != null) {
                Iris.getPipelineManager().preparePipeline(Iris.getCurrentDimension());
            }
        }
    }
}

