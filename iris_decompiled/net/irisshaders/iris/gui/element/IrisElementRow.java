/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.irisshaders.iris.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IrisElementRow {
    private final Map<Element, Integer> elements = new HashMap<Element, Integer>();
    private final List<Element> orderedElements = new ArrayList<Element>();
    private final int spacing;
    private int x;
    private int y;
    private int width;
    private int height;

    public IrisElementRow(int spacing) {
        this.spacing = spacing;
    }

    public IrisElementRow() {
        this(1);
    }

    public IrisElementRow add(Element element, int width) {
        if (!this.orderedElements.contains(element)) {
            this.orderedElements.add(element);
        }
        this.elements.put(element, width);
        this.width += width + this.spacing;
        return this;
    }

    public void setWidth(Element element, int width) {
        if (!this.elements.containsKey(element)) {
            return;
        }
        this.width -= this.elements.get(element) + 2;
        this.add(element, width);
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int height, int mouseX, int mouseY, float tickDelta, boolean rowHovered) {
        this.x = x;
        this.y = y;
        this.height = height;
        int currentX = x;
        for (Element element : this.orderedElements) {
            int currentWidth = this.elements.get(element);
            element.render(guiGraphics, currentX, y, currentWidth, height, mouseX, mouseY, tickDelta, rowHovered && this.sectionHovered(currentX, currentWidth, mouseX, mouseY));
            currentX += currentWidth + this.spacing;
        }
    }

    public void renderRightAligned(GuiGraphics guiGraphics, int x, int y, int height, int mouseX, int mouseY, float tickDelta, boolean hovered) {
        this.render(guiGraphics, x - this.width, y, height, mouseX, mouseY, tickDelta, hovered);
    }

    private boolean sectionHovered(int sectionX, int sectionWidth, double mx, double my) {
        return mx > (double)sectionX && mx < (double)(sectionX + sectionWidth) && my > (double)this.y && my < (double)(this.y + this.height);
    }

    private Optional<Element> getHovered(double mx, double my) {
        int currentX = this.x;
        for (Element element : this.orderedElements) {
            int currentWidth = this.elements.get(element);
            if (this.sectionHovered(currentX, currentWidth, mx, my)) {
                return Optional.of(element);
            }
            currentX += currentWidth + this.spacing;
        }
        return Optional.empty();
    }

    private Optional<Element> getFocused() {
        return this.orderedElements.stream().filter(Element::m_93696_).findFirst();
    }

    public boolean mouseClicked(double mx, double my, int button) {
        return this.getHovered(mx, my).map(element -> element.m_6375_(mx, my, button)).orElse(false);
    }

    public boolean mouseReleased(double mx, double my, int button) {
        return this.getHovered(mx, my).map(element -> element.m_6348_(mx, my, button)).orElse(false);
    }

    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        return this.getFocused().map(element -> element.m_7933_(keycode, scancode, modifiers)).orElse(false);
    }

    public List<? extends GuiEventListener> children() {
        return ImmutableList.copyOf(this.orderedElements);
    }

    public static abstract class Element
    implements GuiEventListener {
        public boolean disabled = false;
        private boolean hovered = false;
        private boolean focused;
        private ScreenRectangle bounds = ScreenRectangle.m_264427_();

        public void render(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta, boolean hovered) {
            this.bounds = new ScreenRectangle(x, y, width, height);
            GuiUtil.bindIrisWidgetsTexture();
            GuiUtil.drawButton(guiGraphics, x, y, width, height, this.isHovered() || this.m_93696_(), this.disabled);
            this.hovered = hovered;
            this.renderLabel(guiGraphics, x, y, width, height, mouseX, mouseY, tickDelta, hovered);
        }

        public abstract void renderLabel(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8, boolean var9);

        public boolean isHovered() {
            return this.hovered;
        }

        public boolean m_93696_() {
            return this.focused;
        }

        public void m_93692_(boolean focused) {
            this.focused = focused;
        }

        @Nullable
        public ComponentPath m_264064_(FocusNavigationEvent pGuiEventListener0) {
            return !this.m_93696_() ? ComponentPath.m_264401_((GuiEventListener)this) : null;
        }

        @NotNull
        public ScreenRectangle m_264198_() {
            return this.bounds;
        }
    }

    public static class TextButtonElement
    extends ButtonElement<TextButtonElement> {
        protected final Font font;
        public Component text;

        public TextButtonElement(Component text, Function<TextButtonElement, Boolean> onClick) {
            super(onClick);
            this.font = Minecraft.m_91087_().f_91062_;
            this.text = text;
        }

        @Override
        public void renderLabel(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta, boolean hovered) {
            int textX = x + (int)((double)(width - this.font.m_92852_((FormattedText)this.text)) * 0.5);
            int textY = y + (int)((double)(height - 8) * 0.5);
            guiGraphics.m_280430_(this.font, this.text, textX, textY, 0xFFFFFF);
        }
    }

    public static class IconButtonElement
    extends ButtonElement<IconButtonElement> {
        public GuiUtil.Icon icon;
        public GuiUtil.Icon hoveredIcon;

        public IconButtonElement(GuiUtil.Icon icon, GuiUtil.Icon hoveredIcon, Function<IconButtonElement, Boolean> onClick) {
            super(onClick);
            this.icon = icon;
            this.hoveredIcon = hoveredIcon;
        }

        public IconButtonElement(GuiUtil.Icon icon, Function<IconButtonElement, Boolean> onClick) {
            this(icon, icon, onClick);
        }

        @Override
        public void renderLabel(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta, boolean hovered) {
            int iconX = x + (int)((double)(width - this.icon.getWidth()) * 0.5);
            int iconY = y + (int)((double)(height - this.icon.getHeight()) * 0.5);
            GuiUtil.bindIrisWidgetsTexture();
            if (!this.disabled && (hovered || this.m_93696_())) {
                this.hoveredIcon.draw(guiGraphics, iconX, iconY);
            } else {
                this.icon.draw(guiGraphics, iconX, iconY);
            }
        }
    }

    public static abstract class ButtonElement<T extends ButtonElement<T>>
    extends Element {
        private final Function<T, Boolean> onClick;

        protected ButtonElement(Function<T, Boolean> onClick) {
            this.onClick = onClick;
        }

        public boolean m_6375_(double mx, double my, int button) {
            if (this.disabled) {
                return false;
            }
            if (button == 0) {
                return this.onClick.apply(this);
            }
            return super.m_6375_(mx, my, button);
        }

        public boolean m_7933_(int keycode, int scancode, int modifiers) {
            if (keycode == 257) {
                return this.onClick.apply(this);
            }
            return false;
        }
    }
}

