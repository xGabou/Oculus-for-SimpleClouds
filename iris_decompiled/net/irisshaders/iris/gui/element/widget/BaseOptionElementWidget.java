/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.TextColor
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element.widget;

import java.util.Optional;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.widget.CommentedElementWidget;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;

public abstract class BaseOptionElementWidget<T extends OptionMenuElement>
extends CommentedElementWidget<T> {
    protected static final Component SET_TO_DEFAULT = Component.m_237115_((String)"options.iris.setToDefault").m_130940_(ChatFormatting.GREEN);
    protected static final Component DIVIDER = Component.m_237113_((String)": ");
    protected MutableComponent unmodifiedLabel;
    protected ShaderPackScreen screen;
    protected NavigationController navigation;
    protected Component trimmedLabel;
    protected Component valueLabel;
    protected boolean usedKeyboard;
    private MutableComponent label;
    private boolean isLabelTrimmed;
    private int maxLabelWidth;
    private int valueSectionWidth;

    public BaseOptionElementWidget(T element) {
        super(element);
    }

    @Override
    public void init(ShaderPackScreen screen, NavigationController navigation) {
        this.screen = screen;
        this.navigation = navigation;
        this.valueLabel = null;
        this.trimmedLabel = null;
    }

    protected final void setLabel(MutableComponent label) {
        this.label = label.m_6881_().m_7220_(DIVIDER);
        this.unmodifiedLabel = label;
    }

    protected final void updateRenderParams(int minValueSectionWidth) {
        this.usedKeyboard = this.m_93696_();
        if (this.valueLabel == null) {
            this.valueLabel = this.createValueLabel();
        }
        Font font = Minecraft.m_91087_().f_91062_;
        this.valueSectionWidth = Math.max(minValueSectionWidth, font.m_92852_((FormattedText)this.valueLabel) + 8);
        this.maxLabelWidth = this.bounds.f_263770_() - 8 - this.valueSectionWidth;
        if (this.trimmedLabel == null || font.m_92852_((FormattedText)this.label) > this.maxLabelWidth != this.isLabelTrimmed) {
            this.updateLabels();
        }
        this.isLabelTrimmed = font.m_92852_((FormattedText)this.label) > this.maxLabelWidth;
    }

    protected final void renderOptionWithValue(GuiGraphics guiGraphics, boolean hovered, float sliderPosition, int sliderWidth) {
        GuiUtil.bindIrisWidgetsTexture();
        GuiUtil.drawButton(guiGraphics, this.bounds.f_263846_().f_263719_(), this.bounds.f_263846_().f_263694_(), this.bounds.f_263770_(), this.bounds.f_263800_(), hovered, false);
        GuiUtil.drawButton(guiGraphics, this.bounds.m_264095_(ScreenDirection.RIGHT) - (this.valueSectionWidth + 2), this.bounds.f_263846_().f_263694_() + 2, this.valueSectionWidth, this.bounds.f_263800_() - 4, false, true);
        if (sliderPosition >= 0.0f) {
            int sliderSpace = this.valueSectionWidth - 4 - sliderWidth;
            int sliderPos = this.bounds.m_264095_(ScreenDirection.RIGHT) - this.valueSectionWidth + (int)(sliderPosition * (float)sliderSpace);
            GuiUtil.drawButton(guiGraphics, sliderPos, this.bounds.f_263846_().f_263694_() + 4, sliderWidth, this.bounds.f_263800_() - 8, false, false);
        }
        Font font = Minecraft.m_91087_().f_91062_;
        guiGraphics.m_280430_(font, this.trimmedLabel, this.bounds.f_263846_().f_263719_() + 6, this.bounds.f_263846_().f_263694_() + 7, 0xFFFFFF);
        guiGraphics.m_280430_(font, this.valueLabel, this.bounds.m_264095_(ScreenDirection.RIGHT) - 2 - (int)((double)this.valueSectionWidth * 0.5) - (int)((double)font.m_92852_((FormattedText)this.valueLabel) * 0.5), this.bounds.f_263846_().f_263694_() + 7, 0xFFFFFF);
    }

    protected final void renderOptionWithValue(GuiGraphics guiGraphics, boolean hovered) {
        this.renderOptionWithValue(guiGraphics, hovered, -1.0f, 0);
    }

    protected final void tryRenderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered) {
        if (Screen.m_96638_()) {
            this.renderTooltip(guiGraphics, SET_TO_DEFAULT, mouseX, mouseY, hovered);
        } else if (this.isLabelTrimmed && !this.screen.isDisplayingComment()) {
            this.renderTooltip(guiGraphics, (Component)this.unmodifiedLabel, mouseX, mouseY, hovered);
        }
    }

    protected final void renderTooltip(GuiGraphics guiGraphics, Component text, int mouseX, int mouseY, boolean hovered) {
        if (hovered) {
            ShaderPackScreen.TOP_LAYER_RENDER_QUEUE.add(() -> GuiUtil.drawTextPanel(Minecraft.m_91087_().f_91062_, guiGraphics, text, mouseX + 2, mouseY - 16));
        }
    }

    protected final void updateLabels() {
        this.trimmedLabel = this.createTrimmedLabel();
        this.valueLabel = this.createValueLabel();
    }

    protected final Component createTrimmedLabel() {
        MutableComponent label = GuiUtil.shortenText(Minecraft.m_91087_().f_91062_, this.label.m_6881_(), this.maxLabelWidth);
        if (this.isValueModified()) {
            label = label.m_130938_(style -> style.m_131148_(TextColor.m_131266_((int)16763210)));
        }
        return label;
    }

    protected abstract Component createValueLabel();

    public abstract boolean applyNextValue();

    public abstract boolean applyPreviousValue();

    public abstract boolean applyOriginalValue();

    public abstract boolean isValueModified();

    @Nullable
    public abstract String getCommentKey();

    @Override
    public Optional<Component> getCommentTitle() {
        return Optional.of(this.unmodifiedLabel);
    }

    @Override
    public Optional<Component> getCommentBody() {
        return Optional.ofNullable(this.getCommentKey()).map(key -> I18n.m_118936_((String)key) ? Component.m_237115_((String)key) : null);
    }

    @Override
    public boolean m_6375_(double mx, double my, int button) {
        if (button == 0 || button == 1) {
            boolean refresh = false;
            if (Screen.m_96638_()) {
                refresh = this.applyOriginalValue();
            }
            if (!refresh) {
                refresh = button == 0 ? this.applyNextValue() : this.applyPreviousValue();
            }
            if (refresh) {
                this.navigation.refresh();
            }
            GuiUtil.playButtonClickSound();
            return true;
        }
        return super.m_6375_(mx, my, button);
    }

    @Override
    public boolean m_7933_(int keycode, int scancode, int modifiers) {
        if (keycode == 257) {
            boolean refresh;
            boolean bl = Screen.m_96637_() ? this.applyOriginalValue() : (refresh = Screen.m_96638_() ? this.applyPreviousValue() : this.applyNextValue());
            if (refresh) {
                this.navigation.refresh();
            }
            GuiUtil.playButtonClickSound();
            return true;
        }
        return false;
    }
}

