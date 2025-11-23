/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.function.Function;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.element.IrisElementRow;
import net.irisshaders.iris.gui.element.IrisObjectSelectionList;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public class ShaderPackSelectionList
extends IrisObjectSelectionList<BaseEntry> {
    private static final Component PACK_LIST_LABEL = Component.m_237115_((String)"pack.iris.list.label").m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY});
    private final ShaderPackScreen screen;
    private final TopButtonRowEntry topButtonRow;
    private final WatchService watcher;
    private final WatchKey key;
    private boolean keyValid;
    private ShaderPackEntry applied = null;

    public ShaderPackSelectionList(ShaderPackScreen screen, Minecraft client, int width, int height, int top, int bottom, int left, int right) {
        super(client, width, height, top, bottom, left, right, 20);
        WatchKey key1;
        WatchService watcher1;
        this.screen = screen;
        this.topButtonRow = new TopButtonRowEntry(this, Iris.getIrisConfig().areShadersEnabled());
        try {
            watcher1 = FileSystems.getDefault().newWatchService();
            key1 = Iris.getShaderpacksDirectory().register(watcher1, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            this.keyValid = true;
        }
        catch (IOException e) {
            Iris.logger.error("Couldn't register file watcher!", e);
            watcher1 = null;
            key1 = null;
            this.keyValid = false;
        }
        this.key = key1;
        this.watcher = watcher1;
        this.refresh();
    }

    public boolean m_7933_(int pContainerEventHandler0, int pInt1, int pInt2) {
        if (pContainerEventHandler0 == 265 && this.m_7222_() == this.m_264567_()) {
            return true;
        }
        return super.m_7933_(pContainerEventHandler0, pInt1, pInt2);
    }

    public void m_88315_(GuiGraphics pAbstractSelectionList0, int pInt1, int pInt2, float pFloat3) {
        if (this.keyValid) {
            for (WatchEvent<?> event : this.key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                this.refresh();
                break;
            }
            this.keyValid = this.key.reset();
        }
        super.m_88315_(pAbstractSelectionList0, pInt1, pInt2, pFloat3);
    }

    public void close() throws IOException {
        if (this.key != null) {
            this.key.cancel();
        }
        if (this.watcher != null) {
            this.watcher.close();
        }
    }

    public int m_5759_() {
        return Math.min(308, this.f_93388_ - 50);
    }

    protected int m_7610_(int index) {
        return super.m_7610_(index) + 2;
    }

    public void refresh() {
        List<String> names;
        this.m_93516_();
        try {
            names = Iris.getShaderpacksDirectoryManager().enumerate();
        }
        catch (Throwable e) {
            Iris.logger.error("Error reading files while constructing selection UI", e);
            this.addLabelEntries(new Component[]{Component.m_237119_(), Component.m_237113_((String)"There was an error reading your shaderpacks directory").m_130944_(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}), Component.m_237119_(), Component.m_237113_((String)"Check your logs for more information."), Component.m_237113_((String)"Please file an issue report including a log file."), Component.m_237113_((String)"If you are able to identify the file causing this, please include it in your report as well."), Component.m_237113_((String)"Note that this might be an issue with folder permissions; ensure those are correct first.")});
            return;
        }
        this.m_7085_(this.topButtonRow);
        this.topButtonRow.allowEnableShadersButton = !names.isEmpty();
        int index = 0;
        for (String name : names) {
            this.addPackEntry(++index, name);
        }
        this.addLabelEntries(PACK_LIST_LABEL);
    }

    public void addPackEntry(int index, String name) {
        ShaderPackEntry entry = new ShaderPackEntry(index, this, name);
        Iris.getIrisConfig().getShaderPackName().ifPresent(currentPackName -> {
            if (name.equals(currentPackName)) {
                this.m_6987_(entry);
                this.m_7522_((GuiEventListener)entry);
                this.m_93494_(entry);
                this.setApplied(entry);
            }
        });
        this.m_7085_(entry);
    }

    public void addLabelEntries(Component ... lines) {
        for (Component text : lines) {
            this.m_7085_(new LabelEntry(text));
        }
    }

    public void select(String name) {
        for (int i = 0; i < this.m_5773_(); ++i) {
            BaseEntry entry = (BaseEntry)this.m_93500_(i);
            if (!(entry instanceof ShaderPackEntry) || !((ShaderPackEntry)entry).packName.equals(name)) continue;
            this.m_6987_(entry);
            return;
        }
    }

    public ShaderPackEntry getApplied() {
        return this.applied;
    }

    public void setApplied(ShaderPackEntry entry) {
        this.applied = entry;
    }

    public TopButtonRowEntry getTopButtonRow() {
        return this.topButtonRow;
    }

    public class ShaderPackEntry
    extends BaseEntry {
        private final String packName;
        private final ShaderPackSelectionList list;
        private final int index;
        private ScreenRectangle bounds = ScreenRectangle.m_264427_();
        private boolean focused;

        public ShaderPackEntry(int index, ShaderPackSelectionList list, String packName) {
            this.packName = packName;
            this.list = list;
            this.index = index;
        }

        public ScreenRectangle m_264198_() {
            return this.bounds;
        }

        public boolean isApplied() {
            return this.list.getApplied() == this;
        }

        public boolean isSelected() {
            return this.list.m_93511_() == this;
        }

        public String getPackName() {
            return this.packName;
        }

        public void m_6311_(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.bounds = new ScreenRectangle(x, y, entryWidth, entryHeight);
            Font font = Minecraft.m_91087_().f_91062_;
            int color = 0xFFFFFF;
            Object name = this.packName;
            if (hovered) {
                GuiUtil.bindIrisWidgetsTexture();
                GuiUtil.drawButton(guiGraphics, x - 2, y - 2, entryWidth, entryHeight + 4, hovered, false);
            }
            boolean shadersEnabled = this.list.getTopButtonRow().shadersEnabled;
            if (font.m_92852_((FormattedText)Component.m_237113_((String)name).m_130940_(ChatFormatting.BOLD)) > this.list.m_5759_() - 3) {
                name = font.m_92834_((String)name, this.list.m_5759_() - 8) + "...";
            }
            MutableComponent text = Component.m_237113_((String)name);
            if (this.m_5953_(mouseX, mouseY)) {
                text = text.m_130940_(ChatFormatting.BOLD);
            }
            if (shadersEnabled && this.isApplied()) {
                color = 16773731;
            }
            if (!shadersEnabled && !this.m_5953_(mouseX, mouseY)) {
                color = 0xA2A2A2;
            }
            guiGraphics.m_280653_(font, (Component)text, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, color);
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            return this.doThing();
        }

        public boolean m_7933_(int keycode, int pInt1, int pInt2) {
            if (keycode != 257) {
                return false;
            }
            return this.doThing();
        }

        private boolean doThing() {
            boolean didAnything = false;
            if (!this.list.getTopButtonRow().shadersEnabled) {
                this.list.getTopButtonRow().setShadersEnabled(true);
                didAnything = true;
            }
            if (!this.isSelected()) {
                this.list.select(this.index);
                didAnything = true;
            }
            ShaderPackSelectionList.this.screen.m_7522_((GuiEventListener)ShaderPackSelectionList.this.screen.getBottomRowOption());
            return didAnything;
        }

        @Nullable
        public ComponentPath m_264064_(FocusNavigationEvent pGuiEventListener0) {
            return !this.m_93696_() ? ComponentPath.m_264401_((GuiEventListener)this) : null;
        }

        public boolean m_93696_() {
            return this.list.m_7222_() == this;
        }
    }

    public static class TopButtonRowEntry
    extends BaseEntry {
        private static final Component NONE_PRESENT_LABEL = Component.m_237115_((String)"options.iris.shaders.nonePresent").m_130940_(ChatFormatting.GRAY);
        private static final Component SHADERS_DISABLED_LABEL = Component.m_237115_((String)"options.iris.shaders.disabled");
        private static final Component SHADERS_ENABLED_LABEL = Component.m_237115_((String)"options.iris.shaders.enabled");
        private final ShaderPackSelectionList list;
        public boolean allowEnableShadersButton = true;
        public boolean shadersEnabled;

        public TopButtonRowEntry(ShaderPackSelectionList list, boolean shadersEnabled) {
            this.list = list;
            this.shadersEnabled = shadersEnabled;
        }

        public void setShadersEnabled(boolean shadersEnabled) {
            this.shadersEnabled = shadersEnabled;
            this.list.screen.refreshScreenSwitchButton();
        }

        public void m_6311_(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            GuiUtil.bindIrisWidgetsTexture();
            GuiUtil.drawButton(guiGraphics, x - 2, y - 2, entryWidth, entryHeight + 2, hovered, !this.allowEnableShadersButton);
            guiGraphics.m_280653_(Minecraft.m_91087_().f_91062_, this.getEnableDisableLabel(), x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xFFFFFF);
        }

        private Component getEnableDisableLabel() {
            return this.allowEnableShadersButton ? (this.shadersEnabled ? SHADERS_ENABLED_LABEL : SHADERS_DISABLED_LABEL) : NONE_PRESENT_LABEL;
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (this.allowEnableShadersButton) {
                this.setShadersEnabled(!this.shadersEnabled);
                GuiUtil.playButtonClickSound();
                return true;
            }
            return false;
        }

        public boolean m_7933_(int keycode, int scancode, int modifiers) {
            if (keycode == 257 && this.allowEnableShadersButton) {
                this.setShadersEnabled(!this.shadersEnabled);
                GuiUtil.playButtonClickSound();
                return true;
            }
            return false;
        }

        @Nullable
        public ComponentPath m_264064_(FocusNavigationEvent pGuiEventListener0) {
            return !this.m_93696_() ? ComponentPath.m_264401_((GuiEventListener)this) : null;
        }

        public boolean m_93696_() {
            return this.list.m_7222_() == this;
        }

        public static class EnableShadersButtonElement
        extends IrisElementRow.TextButtonElement {
            private int centerX;

            public EnableShadersButtonElement(Component text, Function<IrisElementRow.TextButtonElement, Boolean> onClick) {
                super(text, onClick);
            }

            @Override
            public void renderLabel(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta, boolean hovered) {
                int textX = this.centerX - (int)((double)this.font.m_92852_((FormattedText)this.text) * 0.5);
                int textY = y + (int)((double)(height - 8) * 0.5);
                guiGraphics.m_280430_(this.font, this.text, textX, textY, 0xFFFFFF);
            }
        }
    }

    public static class LabelEntry
    extends BaseEntry {
        private final Component label;

        public LabelEntry(Component label) {
            this.label = label;
        }

        public void m_6311_(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            guiGraphics.m_280653_(Minecraft.m_91087_().f_91062_, this.label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xC2C2C2);
        }
    }

    public static abstract class BaseEntry
    extends AbstractSelectionList.Entry<BaseEntry> {
        protected BaseEntry() {
        }
    }
}

