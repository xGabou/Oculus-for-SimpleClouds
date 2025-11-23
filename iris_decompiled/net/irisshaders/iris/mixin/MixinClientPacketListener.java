/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.game.ClientboundLoginPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPacketListener.class})
public class MixinClientPacketListener {
    @Shadow
    private Minecraft f_104888_;

    @Inject(method={"handleLogin"}, at={@At(value="TAIL")})
    private void iris$showUpdateMessage(ClientboundLoginPacket a, CallbackInfo ci) {
        if (this.f_104888_.f_91074_ == null) {
            return;
        }
        Iris.getStoredError().ifPresent(e -> this.f_104888_.f_91074_.m_5661_((Component)Component.m_237115_((String)(e instanceof ShaderCompileException ? "iris.load.failure.shader" : "iris.load.failure.generic")).m_7220_((Component)Component.m_237113_((String)"Copy Info").m_130938_(arg -> arg.m_131162_(Boolean.valueOf(true)).m_131140_(ChatFormatting.BLUE).m_131142_(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, e.getMessage())))), false));
        if (Iris.loadedIncompatiblePack()) {
            Minecraft.m_91087_().f_91065_.m_168684_(10, 70, 140);
            Iris.logger.warn("Incompatible pack for DH!");
            Minecraft.m_91087_().f_91074_.m_5661_((Component)Component.m_237113_((String)"This pack doesn't have DH support.").m_130944_(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.RED}), false);
            Minecraft.m_91087_().f_91074_.m_5661_((Component)Component.m_237113_((String)"Distant Horizons (DH) chunks won't show up. This isn't a bug, get another shader.").m_130940_(ChatFormatting.RED), false);
        }
    }
}

