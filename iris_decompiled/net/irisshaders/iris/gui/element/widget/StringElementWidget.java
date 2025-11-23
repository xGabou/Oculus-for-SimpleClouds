/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.TextColor
 */
package net.irisshaders.iris.gui.element.widget;

import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.widget.BaseOptionElementWidget;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.option.StringOption;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuStringOptionElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public class StringElementWidget
extends BaseOptionElementWidget<OptionMenuStringOptionElement> {
    protected final StringOption option;
    protected String appliedValue;
    protected int valueCount;
    protected int valueIndex;
    protected MutableComponent prefix;
    protected MutableComponent suffix;

    public StringElementWidget(OptionMenuStringOptionElement element) {
        super(element);
        this.option = element.option;
    }

    @Override
    public void init(ShaderPackScreen screen, NavigationController navigation) {
        super.init(screen, navigation);
        String actualPendingValue = ((OptionMenuStringOptionElement)this.element).getPendingOptionValues().getStringValueOrDefault(this.option.getName());
        this.appliedValue = ((OptionMenuStringOptionElement)this.element).getAppliedOptionValues().getStringValueOrDefault(this.option.getName());
        this.prefix = Component.m_237113_((String)(Language.m_128107_().m_6722_("prefix." + this.option.getName()) ? Language.m_128107_().m_6834_("prefix." + this.option.getName()) : ""));
        this.suffix = Component.m_237113_((String)(Language.m_128107_().m_6722_("suffix." + this.option.getName()) ? Language.m_128107_().m_6834_("suffix." + this.option.getName()) : ""));
        this.setLabel(GuiUtil.translateOrDefault(Component.m_237113_((String)this.option.getName()), "option." + this.option.getName(), new Object[0]));
        ImmutableList<String> values = this.option.getAllowedValues();
        this.valueCount = values.size();
        this.valueIndex = values.indexOf(actualPendingValue);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, boolean hovered) {
        this.updateRenderParams(0);
        this.renderOptionWithValue(guiGraphics, hovered || this.m_93696_());
        if (this.usedKeyboard) {
            this.tryRenderTooltip(guiGraphics, this.bounds.m_264095_(ScreenDirection.RIGHT), this.bounds.f_263846_().f_263694_(), hovered);
        } else {
            this.tryRenderTooltip(guiGraphics, mouseX, mouseY, hovered);
        }
    }

    private void increment(int amount) {
        this.valueIndex = Math.max(this.valueIndex, 0);
        this.valueIndex = Math.floorMod(this.valueIndex + amount, this.valueCount);
    }

    @Override
    protected Component createValueLabel() {
        return this.prefix.m_6881_().m_7220_((Component)GuiUtil.translateOrDefault(Component.m_237113_((String)this.getValue()).m_7220_((Component)this.suffix), "value." + this.option.getName() + "." + this.getValue(), new Object[0])).m_130938_(style -> style.m_131148_(TextColor.m_131266_((int)0x6688FF)));
    }

    @Override
    public String getCommentKey() {
        return "option." + this.option.getName() + ".comment";
    }

    public String getValue() {
        if (this.valueIndex < 0) {
            return this.appliedValue;
        }
        return (String)this.option.getAllowedValues().get(this.valueIndex);
    }

    protected void queue() {
        Iris.getShaderPackOptionQueue().put(this.option.getName(), this.getValue());
    }

    @Override
    public boolean applyNextValue() {
        this.increment(1);
        this.queue();
        return true;
    }

    @Override
    public boolean applyPreviousValue() {
        this.increment(-1);
        this.queue();
        return true;
    }

    @Override
    public boolean applyOriginalValue() {
        this.valueIndex = this.option.getAllowedValues().indexOf((Object)this.option.getDefaultValue());
        this.queue();
        return true;
    }

    @Override
    public boolean isValueModified() {
        return !this.appliedValue.equals(this.getValue());
    }
}

