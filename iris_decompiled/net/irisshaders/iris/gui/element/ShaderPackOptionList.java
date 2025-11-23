/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.ContainerObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.FileDialogUtil;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.IrisContainerObjectSelectionList;
import net.irisshaders.iris.gui.element.IrisElementRow;
import net.irisshaders.iris.gui.element.widget.AbstractElementWidget;
import net.irisshaders.iris.gui.element.widget.OptionMenuConstructor;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShaderPackOptionList
extends IrisContainerObjectSelectionList<BaseEntry> {
    private final List<AbstractElementWidget<?>> elementWidgets = new ArrayList();
    private final ShaderPackScreen screen;
    private final NavigationController navigation;
    private OptionMenuContainer container;

    public ShaderPackOptionList(ShaderPackScreen screen, NavigationController navigation, ShaderPack pack, Minecraft client, int width, int height, int top, int bottom, int left, int right) {
        super(client, width, height, top, bottom, left, right, 24);
        this.navigation = navigation;
        this.screen = screen;
        this.applyShaderPack(pack);
    }

    public void applyShaderPack(ShaderPack pack) {
        this.container = pack.getMenuContainer();
    }

    public void rebuild() {
        this.m_93516_();
        this.m_93410_(0.0);
        OptionMenuConstructor.constructAndApplyToScreen(this.container, this.screen, this, this.navigation);
    }

    public void refresh() {
        this.elementWidgets.forEach(widget -> widget.init(this.screen, this.navigation));
    }

    public int m_5759_() {
        return Math.min(400, this.f_93388_ - 12);
    }

    public void addHeader(Component text, boolean backButton) {
        this.m_7085_((AbstractSelectionList.Entry)new HeaderEntry(this.screen, this.navigation, text, backButton));
    }

    public void addWidgets(int columns, List<AbstractElementWidget<?>> elements) {
        this.elementWidgets.addAll(elements);
        ArrayList<AbstractElementWidget<Object>> row = new ArrayList();
        for (AbstractElementWidget<?> element : elements) {
            row.add(element);
            if (row.size() < columns) continue;
            this.m_7085_((AbstractSelectionList.Entry)new ElementRowEntry(this.screen, this.navigation, row));
            row = new ArrayList();
        }
        if (!row.isEmpty()) {
            while (row.size() < columns) {
                row.add(AbstractElementWidget.EMPTY);
            }
            this.m_7085_((AbstractSelectionList.Entry)new ElementRowEntry(this.screen, this.navigation, row));
        }
    }

    public NavigationController getNavigation() {
        return this.navigation;
    }

    public static class HeaderEntry
    extends BaseEntry {
        public static final Component BACK_BUTTON_TEXT = Component.m_237113_((String)"< ").m_7220_((Component)Component.m_237115_((String)"options.iris.back").m_130940_(ChatFormatting.ITALIC));
        public static final MutableComponent RESET_BUTTON_TEXT_INACTIVE = Component.m_237115_((String)"options.iris.reset").m_130940_(ChatFormatting.GRAY);
        public static final MutableComponent RESET_BUTTON_TEXT_ACTIVE = Component.m_237115_((String)"options.iris.reset").m_130940_(ChatFormatting.YELLOW);
        public static final MutableComponent RESET_HOLD_SHIFT_TOOLTIP = Component.m_237115_((String)"options.iris.reset.tooltip.holdShift").m_130940_(ChatFormatting.GOLD);
        public static final MutableComponent RESET_TOOLTIP = Component.m_237115_((String)"options.iris.reset.tooltip").m_130940_(ChatFormatting.RED);
        public static final MutableComponent IMPORT_TOOLTIP = Component.m_237115_((String)"options.iris.importSettings.tooltip").m_130938_(style -> style.m_131148_(TextColor.m_131266_((int)5089023)));
        public static final MutableComponent EXPORT_TOOLTIP = Component.m_237115_((String)"options.iris.exportSettings.tooltip").m_130938_(style -> style.m_131148_(TextColor.m_131266_((int)16547133)));
        private static final int MIN_SIDE_BUTTON_WIDTH = 42;
        private static final int BUTTON_HEIGHT = 16;
        private final ShaderPackScreen screen;
        @Nullable
        private final IrisElementRow backButton;
        private final IrisElementRow utilityButtons = new IrisElementRow();
        private final IrisElementRow.TextButtonElement resetButton;
        private final IrisElementRow.IconButtonElement importButton;
        private final IrisElementRow.IconButtonElement exportButton;
        private final Component text;

        public HeaderEntry(ShaderPackScreen screen, NavigationController navigation, Component text, boolean hasBackButton) {
            super(navigation);
            this.backButton = hasBackButton ? new IrisElementRow().add(new IrisElementRow.TextButtonElement(BACK_BUTTON_TEXT, this::backButtonClicked), Math.max(42, Minecraft.m_91087_().f_91062_.m_92852_((FormattedText)BACK_BUTTON_TEXT) + 8)) : null;
            this.resetButton = new IrisElementRow.TextButtonElement((Component)RESET_BUTTON_TEXT_INACTIVE, this::resetButtonClicked);
            this.importButton = new IrisElementRow.IconButtonElement(GuiUtil.Icon.IMPORT, GuiUtil.Icon.IMPORT_COLORED, this::importSettingsButtonClicked);
            this.exportButton = new IrisElementRow.IconButtonElement(GuiUtil.Icon.EXPORT, GuiUtil.Icon.EXPORT_COLORED, this::exportSettingsButtonClicked);
            this.utilityButtons.add(this.importButton, 15).add(this.exportButton, 15).add(this.resetButton, Math.max(42, Minecraft.m_91087_().f_91062_.m_92852_((FormattedText)RESET_BUTTON_TEXT_INACTIVE) + 8));
            this.screen = screen;
            this.text = text;
        }

        public void m_6311_(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean shiftDown;
            guiGraphics.m_280509_(x - 3, y + entryHeight - 2, x + entryWidth, y + entryHeight - 1, 0x66BEBEBE);
            Font font = Minecraft.m_91087_().f_91062_;
            guiGraphics.m_280653_(font, this.text, x + (int)((double)entryWidth * 0.5), y + 5, 0xFFFFFF);
            GuiUtil.bindIrisWidgetsTexture();
            if (this.backButton != null) {
                this.backButton.render(guiGraphics, x, y, 16, mouseX, mouseY, tickDelta, hovered);
            }
            this.resetButton.disabled = !(shiftDown = Screen.m_96638_()) && !this.resetButton.m_93696_();
            this.resetButton.text = !this.resetButton.disabled ? RESET_BUTTON_TEXT_ACTIVE : RESET_BUTTON_TEXT_INACTIVE;
            this.utilityButtons.renderRightAligned(guiGraphics, x + entryWidth - 3, y, 16, mouseX, mouseY, tickDelta, hovered);
            if (this.resetButton.isHovered() || this.resetButton.m_93696_()) {
                MutableComponent tooltip = !this.resetButton.disabled ? RESET_TOOLTIP : RESET_HOLD_SHIFT_TOOLTIP;
                this.queueBottomRightAnchoredTooltip(guiGraphics, this.resetButton.m_264198_().m_264095_(ScreenDirection.RIGHT), this.resetButton.m_264198_().f_263846_().f_263694_(), font, (Component)tooltip);
            }
            if (this.importButton.isHovered() || this.importButton.m_93696_()) {
                this.queueBottomRightAnchoredTooltip(guiGraphics, this.importButton.m_264198_().m_264095_(ScreenDirection.RIGHT), this.importButton.m_264198_().f_263846_().f_263694_(), font, (Component)IMPORT_TOOLTIP);
            }
            if (this.exportButton.isHovered() || this.exportButton.m_93696_()) {
                this.queueBottomRightAnchoredTooltip(guiGraphics, this.exportButton.m_264198_().m_264095_(ScreenDirection.RIGHT), this.exportButton.m_264198_().f_263846_().f_263694_(), font, (Component)EXPORT_TOOLTIP);
            }
        }

        private void queueBottomRightAnchoredTooltip(GuiGraphics guiGraphics, int x, int y, Font font, Component text) {
            ShaderPackScreen.TOP_LAYER_RENDER_QUEUE.add(() -> GuiUtil.drawTextPanel(font, guiGraphics, text, x - (font.m_92852_((FormattedText)text) + 10), y - 16));
        }

        public List<? extends GuiEventListener> m_6702_() {
            if (this.backButton != null) {
                return ImmutableList.copyOf((Iterable)Iterables.concat(this.utilityButtons.children(), this.backButton.children()));
            }
            return ImmutableList.copyOf(this.utilityButtons.children());
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            boolean backButtonResult = this.backButton != null && this.backButton.mouseClicked(mouseX, mouseY, button);
            boolean utilButtonResult = this.utilityButtons.mouseClicked(mouseX, mouseY, button);
            return backButtonResult || utilButtonResult;
        }

        public boolean m_7933_(int keycode, int scancode, int modifiers) {
            if (this.backButton != null && this.backButton.keyPressed(keycode, scancode, modifiers)) {
                return true;
            }
            return this.utilityButtons.keyPressed(keycode, scancode, modifiers);
        }

        public List<? extends NarratableEntry> m_142437_() {
            return ImmutableList.of();
        }

        private boolean backButtonClicked(IrisElementRow.TextButtonElement button) {
            this.navigation.back();
            GuiUtil.playButtonClickSound();
            return true;
        }

        private boolean resetButtonClicked(IrisElementRow.TextButtonElement button) {
            if (Screen.m_96638_()) {
                Iris.resetShaderPackOptionsOnNextReload();
                this.screen.applyChanges();
                GuiUtil.playButtonClickSound();
                return true;
            }
            return false;
        }

        private boolean importSettingsButtonClicked(IrisElementRow.IconButtonElement button) {
            GuiUtil.playButtonClickSound();
            if (!Iris.getCurrentPack().isPresent()) {
                return false;
            }
            if (Minecraft.m_91087_().m_91268_().m_85440_()) {
                this.screen.displayNotification((Component)Component.m_237115_((String)"options.iris.mustDisableFullscreen").m_130940_(ChatFormatting.RED).m_130940_(ChatFormatting.BOLD));
                return false;
            }
            ShaderPackScreen originalScreen = this.screen;
            FileDialogUtil.fileSelectDialog(FileDialogUtil.DialogType.OPEN, "Import Shader Settings from File", Iris.getShaderpacksDirectory().resolve(Iris.getCurrentPackName() + ".txt"), "Shader Pack Settings (.txt)", "*.txt").whenComplete((path, err) -> {
                if (err != null) {
                    Iris.logger.error("Error selecting shader settings from file", (Throwable)err);
                    return;
                }
                if (Minecraft.m_91087_().f_91080_ == originalScreen) {
                    path.ifPresent(originalScreen::importPackOptions);
                }
            });
            return true;
        }

        private boolean exportSettingsButtonClicked(IrisElementRow.IconButtonElement button) {
            GuiUtil.playButtonClickSound();
            if (!Iris.getCurrentPack().isPresent()) {
                return false;
            }
            if (Minecraft.m_91087_().m_91268_().m_85440_()) {
                this.screen.displayNotification((Component)Component.m_237115_((String)"options.iris.mustDisableFullscreen").m_130940_(ChatFormatting.RED).m_130940_(ChatFormatting.BOLD));
                return false;
            }
            FileDialogUtil.fileSelectDialog(FileDialogUtil.DialogType.SAVE, "Export Shader Settings to File", Iris.getShaderpacksDirectory().resolve(Iris.getCurrentPackName() + ".txt"), "Shader Pack Settings (.txt)", "*.txt").whenComplete((path, err) -> {
                if (err != null) {
                    Iris.logger.error("Error selecting file to export shader settings", (Throwable)err);
                    return;
                }
                path.ifPresent(p -> {
                    Properties toSave = new Properties();
                    Path sourceTxtPath = Iris.getShaderpacksDirectory().resolve(Iris.getCurrentPackName() + ".txt");
                    if (Files.exists(sourceTxtPath, new LinkOption[0])) {
                        try (InputStream in2 = Files.newInputStream(sourceTxtPath, new OpenOption[0]);){
                            toSave.load(in2);
                        }
                        catch (IOException in2) {
                            // empty catch block
                        }
                    }
                    try (OutputStream out = Files.newOutputStream(p, new OpenOption[0]);){
                        toSave.store(out, null);
                    }
                    catch (IOException e) {
                        Iris.logger.error("Error saving properties to \"" + p + "\"", e);
                    }
                });
            });
            return true;
        }
    }

    public static class ElementRowEntry
    extends BaseEntry {
        private final List<AbstractElementWidget<?>> widgets;
        private final ShaderPackScreen screen;
        private int cachedWidth;
        private int cachedPosX;

        public ElementRowEntry(ShaderPackScreen screen, NavigationController navigation, List<AbstractElementWidget<?>> widgets) {
            super(navigation);
            this.screen = screen;
            this.widgets = widgets;
        }

        public void m_6311_(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.cachedWidth = entryWidth;
            this.cachedPosX = x;
            int totalWidthWithoutMargins = entryWidth - 2 * (this.widgets.size() - 1);
            float singleWidgetWidth = (float)(totalWidthWithoutMargins -= 3) / (float)this.widgets.size();
            for (int i = 0; i < this.widgets.size(); ++i) {
                AbstractElementWidget<?> widget = this.widgets.get(i);
                boolean widgetHovered = hovered && this.getHoveredWidget(mouseX) == i || this.m_7222_() == widget;
                widget.bounds = new ScreenRectangle(x + (int)((singleWidgetWidth + 2.0f) * (float)i), y, (int)singleWidgetWidth, entryHeight + 2);
                widget.render(guiGraphics, mouseX, mouseY, tickDelta, widgetHovered);
                this.screen.setElementHoveredStatus(widget, widgetHovered);
            }
        }

        public int getHoveredWidget(int mouseX) {
            float positionAcrossWidget = (float)Mth.m_14045_((int)(mouseX - this.cachedPosX), (int)0, (int)this.cachedWidth) / (float)this.cachedWidth;
            return Mth.m_14045_((int)((int)Math.floor((float)this.widgets.size() * positionAcrossWidget)), (int)0, (int)(this.widgets.size() - 1));
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            return this.widgets.get(this.getHoveredWidget((int)mouseX)).m_6375_(mouseX, mouseY, button);
        }

        public boolean m_6348_(double mouseX, double mouseY, int button) {
            return this.widgets.get(this.getHoveredWidget((int)mouseX)).m_6348_(mouseX, mouseY, button);
        }

        @NotNull
        public List<? extends GuiEventListener> m_6702_() {
            return ImmutableList.copyOf(this.widgets);
        }

        @NotNull
        public List<? extends NarratableEntry> m_142437_() {
            return ImmutableList.copyOf(this.widgets);
        }
    }

    public static abstract class BaseEntry
    extends ContainerObjectSelectionList.Entry<BaseEntry> {
        protected final NavigationController navigation;

        protected BaseEntry(NavigationController navigation) {
            this.navigation = navigation;
        }
    }
}

