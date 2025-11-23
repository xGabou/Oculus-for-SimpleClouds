/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package dev.nonamecrackers2.simpleclouds.client.keybind;

import dev.nonamecrackers2.simpleclouds.client.gui.CloudPreviewerScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SimpleCloudsKeybinds {
    public static final KeyMapping OPEN_GEN_PREVIEWER = new KeyMapping("simpleclouds.key.openGenPreviewer", 301, "simpleclouds.key.categories.main");
    public static final KeyMapping OPEN_DEBUG = new KeyMapping("simpleclouds.key.openDebug", 301, "simpleclouds.key.categories.main");

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_GEN_PREVIEWER);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.m_91087_();
        while (OPEN_GEN_PREVIEWER.m_90859_()) {
            mc.m_91152_((Screen)new CloudPreviewerScreen(null));
        }
    }
}

