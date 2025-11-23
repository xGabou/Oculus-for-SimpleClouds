/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.MultiLineLabel
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package net.irisshaders.iris.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class FeatureMissingErrorScreen
extends Screen {
    private final Screen parent;
    private final FormattedText messageTemp;
    private MultiLineLabel message;

    public FeatureMissingErrorScreen(Screen parent, Component title, Component message) {
        super(title);
        this.parent = parent;
        this.messageTemp = message;
    }

    protected void m_7856_() {
        super.m_7856_();
        this.message = MultiLineLabel.m_94341_((Font)this.f_96547_, (FormattedText)this.messageTemp, (int)(this.f_96543_ - 50));
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)CommonComponents.f_130660_, arg -> this.f_96541_.m_91152_(this.parent)).m_252987_(this.f_96543_ / 2 - 100, 140, 200, 20).m_253136_());
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.m_280273_(guiGraphics);
        guiGraphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 90, 0xFFFFFF);
        this.message.m_6514_(guiGraphics, this.f_96543_ / 2, 110, 9, 0xFFFFFF);
        super.m_88315_(guiGraphics, mouseX, mouseY, delta);
    }
}

