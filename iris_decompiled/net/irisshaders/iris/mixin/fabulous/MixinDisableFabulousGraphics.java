/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.GraphicsStatus
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.renderer.LevelRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.fabulous;

import net.irisshaders.iris.Iris;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinDisableFabulousGraphics {
    @Inject(method={"onResourceManagerReload"}, at={@At(value="HEAD")})
    private void iris$disableFabulousGraphicsOnResourceReload(CallbackInfo ci) {
        this.iris$disableFabulousGraphics();
    }

    @Inject(method={"allChanged"}, at={@At(value="HEAD")})
    private void iris$disableFabulousGraphicsOnLevelRendererReload(CallbackInfo ci) {
        this.iris$disableFabulousGraphics();
    }

    @Unique
    private void iris$disableFabulousGraphics() {
        Options options = Minecraft.m_91087_().f_91066_;
        if (!Iris.getIrisConfig().areShadersEnabled()) {
            return;
        }
        if (options.m_232060_().m_231551_() == GraphicsStatus.FABULOUS) {
            options.m_232060_().m_231514_((Object)GraphicsStatus.FANCY);
        }
    }
}

