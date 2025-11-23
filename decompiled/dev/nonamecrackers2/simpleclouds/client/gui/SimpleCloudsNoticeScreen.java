/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.nonamecrackers2.simpleclouds.client.gui;

import dev.nonamecrackers2.simpleclouds.client.gui.SimpleCloudsInfoScreen;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class SimpleCloudsNoticeScreen
extends SimpleCloudsInfoScreen {
    private final Component text;

    public SimpleCloudsNoticeScreen(Component text) {
        super((Component)Component.m_237115_((String)"gui.simpleclouds.notice.title").m_130948_(Style.f_131099_.m_131162_(Boolean.valueOf(true)).m_131136_(Boolean.valueOf(true))), 3);
        this.text = text;
    }

    @Override
    protected void generateButtons(GridLayout.RowHelper row) {
        super.generateButtons(row);
        row.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237115_((String)"gui.simpleclouds.notice.close.title"), b -> this.m_7379_()).m_252780_(100).m_253136_());
    }

    @Override
    protected void generateText(List<FormattedCharSequence> text, int maxWidth) {
        text.addAll(this.f_96547_.m_92923_((FormattedText)this.text, maxWidth));
    }
}

