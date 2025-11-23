/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.CrashReport
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraftforge.client.extensions.IForgeMinecraft
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import dev.nonamecrackers2.simpleclouds.client.gui.SimpleCloudsErrorScreen;
import dev.nonamecrackers2.simpleclouds.client.gui.SimpleCloudsNoticeScreen;
import dev.nonamecrackers2.simpleclouds.client.mesh.RendererInitializeResult;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.BindingManager;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManagerHolder;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.extensions.IForgeMinecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public abstract class MixinMinecraft
implements IForgeMinecraft {
    @Inject(method={"fillReport"}, at={@At(value="HEAD")})
    public void simpleclouds$appendCrashReportDetails_fillReport(CrashReport report, CallbackInfoReturnable<CrashReport> ci) {
        SimpleCloudsRenderer.getOptionalInstance().ifPresent(renderer -> renderer.fillReport(report));
        BindingManager.fillReport(report);
    }

    @Inject(method={"setInitialScreen"}, at={@At(value="HEAD")}, cancellable=true)
    public void simpleclouds$beforeMainTitleScreen_setInitialScreen(CallbackInfo ci) {
        RendererInitializeResult result;
        SimpleCloudsRenderer renderer;
        SimpleCloudsNoticeScreen notice = SimpleCloudsCompatHelper.createNotice();
        if (notice != null) {
            this.pushGuiLayer(notice);
            ci.cancel();
        }
        if ((renderer = (SimpleCloudsRenderer)SimpleCloudsRenderer.getOptionalInstance().orElse(null)) != null && (result = renderer.getInitialInitializationResult()) != null && result.getState() == RendererInitializeResult.State.ERROR) {
            this.pushGuiLayer(new SimpleCloudsErrorScreen(renderer.getInitialInitializationResult()));
            ci.cancel();
        }
    }

    @Inject(method={"setLevel"}, at={@At(value="TAIL")})
    public void simpleclouds$onClientLevelChange_setLevel(@Nullable ClientLevel level, CallbackInfo ci) {
        if (level instanceof CloudManagerHolder) {
            SimpleCloudsRenderer.getOptionalInstance().ifPresent(renderer -> {
                ClientCloudManager manager = (ClientCloudManager)CloudManager.get(level);
                renderer.onCloudManagerChange(manager);
            });
        }
    }

    @Shadow
    public abstract void m_91152_(@Nullable Screen var1);
}

