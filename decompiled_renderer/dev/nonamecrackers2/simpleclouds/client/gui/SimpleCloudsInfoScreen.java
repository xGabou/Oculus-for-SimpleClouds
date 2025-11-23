/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.layouts.FrameLayout
 *  net.minecraft.client.gui.layouts.GridLayout
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraftforge.fml.loading.ImmediateWindowHandler
 *  nonamecrackers2.crackerslib.client.util.GUIUtils
 */
package dev.nonamecrackers2.simpleclouds.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import nonamecrackers2.crackerslib.client.util.GUIUtils;

public abstract class SimpleCloudsInfoScreen
extends Screen {
    protected static final int PADDING = 20;
    protected Component openGLVersion;
    protected List<FormattedCharSequence> text;
    protected int totalTextHeight;
    protected final int buttonCount;

    protected SimpleCloudsInfoScreen(Component title, int buttonCount) {
        super(title);
        this.buttonCount = buttonCount;
    }

    protected abstract void generateText(List<FormattedCharSequence> var1, int var2);

    protected void generateButtons(GridLayout.RowHelper row) {
        row.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237115_((String)"gui.crackerslib.screen.config.github"), b -> GUIUtils.openLink((String)"https://github.com/nonamecrackers2/simple-clouds-new/issues")).m_252780_(100).m_253136_());
        row.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237115_((String)"gui.crackerslib.screen.config.discord"), b -> GUIUtils.openLink((String)"https://discord.com/invite/cracker-s-modded-community-987817685293355028")).m_252780_(100).m_253136_());
    }

    protected void m_7856_() {
        this.openGLVersion = Component.m_237113_((String)("OpenGL " + ImmediateWindowHandler.getGLVersion()));
        this.text = Lists.newArrayList();
        int textMaxWidth = Mth.m_14143_((float)((float)this.f_96543_ / 1.5f));
        this.generateText(this.text, textMaxWidth);
        int n = this.text.size();
        Objects.requireNonNull(this.f_96547_);
        this.totalTextHeight = n * (9 + 2);
        GridLayout layout = new GridLayout().m_267612_(10);
        GridLayout.RowHelper row = layout.m_264606_(this.buttonCount);
        this.generateButtons(row);
        layout.m_264036_();
        FrameLayout.m_264159_((LayoutElement)layout, (int)0, (int)(this.f_96544_ - 40), (int)this.f_96543_, (int)40);
        layout.m_264134_(x$0 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.m_142416_((GuiEventListener)x$0);
        });
    }

    public void m_88315_(GuiGraphics stack, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(stack);
        super.m_88315_(stack, mouseX, mouseY, partialTick);
        stack.m_280653_(this.f_96547_, this.m_96636_(), this.f_96543_ / 2, 20, -1);
        stack.m_280430_(this.f_96547_, this.openGLVersion, 20, 20, -1);
        Objects.requireNonNull(this.f_96547_);
        int top = 20 + 9;
        int y = top + (this.f_96544_ - top - 40) / 2 - this.totalTextHeight / 2;
        for (FormattedCharSequence text : this.text) {
            stack.m_280364_(this.f_96547_, text, this.f_96543_ / 2, y, -1);
            Objects.requireNonNull(this.f_96547_);
            y += 9 + 2;
        }
    }
}

