/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.ContainerObjectSelectionList
 *  net.minecraft.client.gui.components.ContainerObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  nonamecrackers2.crackerslib.client.gui.widget.config.ConfigListItem
 *  nonamecrackers2.crackerslib.client.util.RenderUtil
 */
package dev.nonamecrackers2.simpleclouds.client.gui.widget;

import com.google.common.collect.Lists;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableNoiseSettings;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigListItem;
import nonamecrackers2.crackerslib.client.util.RenderUtil;

public class LayerEditor
extends ContainerObjectSelectionList<Entry> {
    private static final int ROW_HEIGHT = 30;
    private final ModifiableNoiseSettings settings;
    private final Runnable onChanged;

    public LayerEditor(ModifiableNoiseSettings settings, Minecraft mc, int x, int y, int width, int height, Runnable onChanged) {
        super(mc, width, height, y, y + height, 30);
        this.m_93507_(x);
        this.m_93488_(false);
        this.m_93496_(false);
        this.settings = settings;
        this.onChanged = onChanged;
        this.buildEntries();
    }

    public void buildEntries() {
        this.m_93516_();
        for (AbstractNoiseSettings.Param parameter : AbstractNoiseSettings.Param.values()) {
            this.m_7085_((AbstractSelectionList.Entry)new Entry(parameter));
        }
    }

    protected void m_7733_(GuiGraphics graphics) {
        graphics.m_280509_(this.f_93393_, this.f_93390_, this.f_93392_, this.f_93391_, -1728053248);
    }

    public int m_5759_() {
        return this.getWidth() - 5;
    }

    protected int m_5756_() {
        return this.getLeft() + this.getWidth() - 5;
    }

    protected boolean m_7987_(int index) {
        return Objects.equals(this.m_93511_(), this.m_6702_().get(index));
    }

    public class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
        private static final int TEXT_WIDTH = 40;
        private final List<AbstractWidget> widgets = Lists.newArrayList();
        private final AbstractNoiseSettings.Param parameter;
        private final Font font;
        private final Component name;
        private final List<Component> tooltip;
        private final EditBox box;
        private int prevLeft;
        private int prevTop;

        public Entry(AbstractNoiseSettings.Param parameter) {
            this.font = ((LayerEditor)LayerEditor.this).f_93386_.f_91062_;
            this.parameter = parameter;
            MutableComponent fullName = Component.m_237115_((String)("gui.simpleclouds.noise_settings.param." + parameter.toString().toLowerCase() + ".name"));
            Component squishedName = ConfigListItem.shortenText((Component)fullName, (int)40);
            EditBox box = new EditBox(this.font, 0, 0, 60, 20, CommonComponents.f_237098_);
            box.m_94144_(String.valueOf(LayerEditor.this.settings.getParam(parameter)));
            box.m_94151_(v -> {
                try {
                    float value = Float.parseFloat(v);
                    if (value < parameter.getMinInclusive() || parameter.getMaxInclusive() < value) {
                        box.m_94202_(ChatFormatting.RED.m_126665_().intValue());
                    } else {
                        box.m_94202_(-1);
                    }
                    LayerEditor.this.settings.setParam(parameter, value);
                }
                catch (NumberFormatException e) {
                    box.m_94202_(ChatFormatting.RED.m_126665_().intValue());
                    return;
                }
                LayerEditor.this.onChanged.run();
            });
            this.name = squishedName;
            this.tooltip = Lists.newArrayList((Object[])new Component[]{fullName, Component.m_237110_((String)"gui.simpleclouds.noise_settings.param.range", (Object[])new Object[]{Float.valueOf(parameter.getMinInclusive()), Float.valueOf(parameter.getMaxInclusive())}).m_130940_(ChatFormatting.GRAY)});
            this.box = box;
            this.widgets.add((AbstractWidget)box);
        }

        public List<? extends GuiEventListener> m_6702_() {
            return this.widgets;
        }

        public List<? extends NarratableEntry> m_142437_() {
            return this.widgets;
        }

        public void m_6311_(GuiGraphics stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean selected, float partialTicks) {
            this.prevLeft = left;
            this.prevTop = top;
            stack.m_280637_(left, top, width - 5, height, -1426063361);
            if (RenderUtil.isMouseInBounds((int)mouseX, (int)mouseY, (int)left, (int)top, (int)60, (int)height)) {
                stack.m_280666_(this.font, this.tooltip, mouseX, mouseY);
            }
            int n = top + height / 2;
            Objects.requireNonNull(this.font);
            stack.m_280430_(this.font, this.name, left + 5, n - 9 / 2, -1);
            this.box.m_253211_(top + height / 2 - this.box.m_93694_() / 2);
            this.box.m_252865_(left + 20 + 40);
            this.box.m_93674_(width - 30 - 40);
            this.box.m_88315_(stack, mouseX, mouseY, partialTicks);
        }

        public boolean m_6375_(double mouseX, double mouseY, int clickType) {
            if (super.m_6375_(mouseX, mouseY, clickType)) {
                return true;
            }
            if (clickType == 0) {
                LayerEditor.this.m_6987_((AbstractSelectionList.Entry)this);
                return true;
            }
            return false;
        }

        public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (super.m_7979_(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
            if (button != 0) {
                return false;
            }
            if (RenderUtil.isMouseInBounds((int)((int)mouseX), (int)((int)mouseY), (int)this.prevLeft, (int)this.prevTop, (int)60, (int)30)) {
                float value = LayerEditor.this.settings.getParam(this.parameter);
                value = (float)((double)value + dragX);
                LayerEditor.this.settings.setParam(this.parameter, value);
                this.box.m_94144_(String.valueOf(LayerEditor.this.settings.getParam(this.parameter)));
                LayerEditor.this.onChanged.run();
                return true;
            }
            return false;
        }
    }
}

