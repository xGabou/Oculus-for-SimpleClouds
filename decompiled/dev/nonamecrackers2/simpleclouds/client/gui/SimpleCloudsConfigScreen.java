/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen
 *  nonamecrackers2.crackerslib.client.gui.title.TitleLogo
 */
package dev.nonamecrackers2.simpleclouds.client.gui;

import dev.nonamecrackers2.simpleclouds.client.gui.CloudPreviewerScreen;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.client.gui.title.TitleLogo;

public class SimpleCloudsConfigScreen
extends ConfigHomeScreen {
    public SimpleCloudsConfigScreen(String modid, Map<ModConfig.Type, ForgeConfigSpec> specs, TitleLogo title, boolean isWorldLoaded, boolean hasSinglePlayerServer, Screen previous, List<Supplier<AbstractButton>> extraButtons, int totalColumns) {
        super(modid, specs, title, isWorldLoaded, hasSinglePlayerServer, previous, extraButtons, totalColumns);
    }

    protected void m_7856_() {
        super.m_7856_();
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.button.title"), b -> this.f_96541_.m_91152_((Screen)new CloudPreviewerScreen((Screen)this))).m_252794_(5, 5).m_252780_(100).m_253136_());
    }
}

