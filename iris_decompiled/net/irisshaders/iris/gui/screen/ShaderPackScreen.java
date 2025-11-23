/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.OldImageButton;
import net.irisshaders.iris.gui.element.ShaderPackOptionList;
import net.irisshaders.iris.gui.element.ShaderPackSelectionList;
import net.irisshaders.iris.gui.element.widget.AbstractElementWidget;
import net.irisshaders.iris.gui.element.widget.CommentedElementWidget;
import net.irisshaders.iris.gui.screen.HudHideable;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class ShaderPackScreen
extends Screen
implements HudHideable {
    public static final Set<Runnable> TOP_LAYER_RENDER_QUEUE = new HashSet<Runnable>();
    private static final Component SELECT_TITLE = Component.m_237115_((String)"pack.iris.select.title").m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
    private static final Component CONFIGURE_TITLE = Component.m_237115_((String)"pack.iris.configure.title").m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
    private static final int COMMENT_PANEL_WIDTH = 314;
    private static final String development = "Development Environment";
    private final Screen parent;
    private final MutableComponent irisTextComponent;
    private ShaderPackSelectionList shaderPackList;
    @Nullable
    private ShaderPackOptionList shaderOptionList = null;
    @Nullable
    private NavigationController navigation = null;
    private Button screenSwitchButton;
    private Component notificationDialog = null;
    private int notificationDialogTimer = 0;
    @Nullable
    private AbstractElementWidget<?> hoveredElement = null;
    private Optional<Component> hoveredElementCommentTitle = Optional.empty();
    private List<FormattedCharSequence> hoveredElementCommentBody = new ArrayList<FormattedCharSequence>();
    private int hoveredElementCommentTimer = 0;
    private boolean optionMenuOpen = false;
    private boolean dropChanges = false;
    private MutableComponent developmentComponent;
    private MutableComponent updateComponent;
    private boolean guiHidden = false;
    private float guiButtonHoverTimer = 0.0f;
    private Button openFolderButton;

    public ShaderPackScreen(Screen parent) {
        super((Component)Component.m_237115_((String)"options.iris.shaderPackSelection.title"));
        this.parent = parent;
        String irisName = "Oculus " + Iris.getVersion();
        if (!FMLEnvironment.production) {
            this.developmentComponent = Component.m_237113_((String)development).m_130940_(ChatFormatting.GOLD);
        }
        this.irisTextComponent = Component.m_237113_((String)irisName).m_130940_(ChatFormatting.GRAY);
        this.refreshForChangedPack();
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.f_96541_.f_91073_ == null) {
            this.m_280273_(guiGraphics);
        } else if (!this.guiHidden) {
            guiGraphics.m_280024_(0, 0, this.f_96543_, this.f_96544_, 1327702819, 1327702819);
        }
        if (Screen.m_96637_() && InputConstants.m_84830_((long)Minecraft.m_91087_().m_91268_().m_85439_(), (int)68)) {
            Minecraft.m_91087_().m_91152_((Screen)new ConfirmScreen(option -> {
                Iris.setDebug(option);
                Minecraft.m_91087_().m_91152_((Screen)this);
            }, (Component)Component.m_237113_((String)"Shader debug mode toggle"), (Component)Component.m_237113_((String)"Debug mode helps investigate problems and shows shader errors. Would you like to enable it?"), (Component)Component.m_237113_((String)"Yes"), (Component)Component.m_237113_((String)"No")));
        }
        if (!this.guiHidden) {
            if (this.optionMenuOpen && this.shaderOptionList != null) {
                this.shaderOptionList.m_88315_(guiGraphics, mouseX, mouseY, delta);
            } else {
                this.shaderPackList.m_88315_(guiGraphics, mouseX, mouseY, delta);
            }
        }
        float previousHoverTimer = this.guiButtonHoverTimer;
        super.m_88315_(guiGraphics, mouseX, mouseY, delta);
        if (previousHoverTimer == this.guiButtonHoverTimer) {
            this.guiButtonHoverTimer = 0.0f;
        }
        if (!this.guiHidden) {
            guiGraphics.m_280653_(this.f_96547_, this.f_96539_, (int)((double)this.f_96543_ * 0.5), 8, 0xFFFFFF);
            if (this.notificationDialog != null && this.notificationDialogTimer > 0) {
                guiGraphics.m_280653_(this.f_96547_, this.notificationDialog, (int)((double)this.f_96543_ * 0.5), 21, 0xFFFFFF);
            } else if (this.optionMenuOpen) {
                guiGraphics.m_280653_(this.f_96547_, CONFIGURE_TITLE, (int)((double)this.f_96543_ * 0.5), 21, 0xFFFFFF);
            } else {
                guiGraphics.m_280653_(this.f_96547_, SELECT_TITLE, (int)((double)this.f_96543_ * 0.5), 21, 0xFFFFFF);
            }
            if (this.isDisplayingComment()) {
                int panelHeight = Math.max(50, 18 + this.hoveredElementCommentBody.size() * 10);
                int x = (int)(0.5 * (double)this.f_96543_) - 157;
                int y = this.f_96544_ - (panelHeight + 4);
                GuiUtil.drawPanel(guiGraphics, x, y, 314, panelHeight);
                guiGraphics.m_280430_(this.f_96547_, this.hoveredElementCommentTitle.orElse((Component)Component.m_237119_()), x + 4, y + 4, 0xFFFFFF);
                for (int i = 0; i < this.hoveredElementCommentBody.size(); ++i) {
                    guiGraphics.m_280648_(this.f_96547_, this.hoveredElementCommentBody.get(i), x + 4, y + 16 + i * 10, 0xFFFFFF);
                }
            }
        }
        for (Runnable render : TOP_LAYER_RENDER_QUEUE) {
            render.run();
        }
        TOP_LAYER_RENDER_QUEUE.clear();
        if (this.developmentComponent != null) {
            guiGraphics.m_280430_(this.f_96547_, (Component)this.developmentComponent, 2, this.f_96544_ - 10, 0xFFFFFF);
            guiGraphics.m_280430_(this.f_96547_, (Component)this.irisTextComponent, 2, this.f_96544_ - 20, 0xFFFFFF);
        } else if (this.updateComponent != null) {
            guiGraphics.m_280430_(this.f_96547_, (Component)this.updateComponent, 2, this.f_96544_ - 10, 0xFFFFFF);
            guiGraphics.m_280430_(this.f_96547_, (Component)this.irisTextComponent, 2, this.f_96544_ - 20, 0xFFFFFF);
        } else {
            guiGraphics.m_280430_(this.f_96547_, (Component)this.irisTextComponent, 2, this.f_96544_ - 10, 0xFFFFFF);
        }
    }

    protected void m_7856_() {
        super.m_7856_();
        int bottomCenter = this.f_96543_ / 2 - 50;
        int topCenter = this.f_96543_ / 2 - 76;
        boolean inWorld = this.f_96541_.f_91073_ != null;
        this.m_169411_((GuiEventListener)this.shaderPackList);
        this.m_169411_((GuiEventListener)this.shaderOptionList);
        this.shaderPackList = new ShaderPackSelectionList(this, this.f_96541_, this.f_96543_, this.f_96544_, 32, this.f_96544_ - 58, 0, this.f_96543_);
        if (Iris.getCurrentPack().isPresent() && this.navigation != null) {
            ShaderPack currentPack = Iris.getCurrentPack().get();
            this.shaderOptionList = new ShaderPackOptionList(this, this.navigation, currentPack, this.f_96541_, this.f_96543_, this.f_96544_, 32, this.f_96544_ - 58, 0, this.f_96543_);
            this.navigation.setActiveOptionList(this.shaderOptionList);
            this.shaderOptionList.rebuild();
        } else {
            this.optionMenuOpen = false;
            this.shaderOptionList = null;
        }
        if (inWorld) {
            this.shaderPackList.m_93488_(false);
            if (this.shaderOptionList != null) {
                this.shaderOptionList.m_93488_(false);
            }
        }
        this.m_169413_();
        if (!this.guiHidden) {
            if (this.optionMenuOpen && this.shaderOptionList != null) {
                this.m_142416_((GuiEventListener)this.shaderOptionList);
            } else {
                this.m_142416_((GuiEventListener)this.shaderPackList);
            }
            this.m_142416_((GuiEventListener)Button.m_253074_((Component)CommonComponents.f_130655_, button -> this.m_7379_()).m_252987_(bottomCenter + 104, this.f_96544_ - 27, 100, 20).m_253136_());
            this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"options.iris.apply"), button -> this.applyChanges()).m_252987_(bottomCenter, this.f_96544_ - 27, 100, 20).m_253136_());
            this.m_142416_((GuiEventListener)Button.m_253074_((Component)CommonComponents.f_130656_, button -> this.dropChangesAndClose()).m_252987_(bottomCenter - 104, this.f_96544_ - 27, 100, 20).m_253136_());
            this.openFolderButton = Button.m_253074_((Component)Component.m_237115_((String)"options.iris.openShaderPackFolder"), button -> this.openShaderPackFolder()).m_252987_(topCenter - 78, this.f_96544_ - 51, 152, 20).m_253136_();
            this.m_142416_((GuiEventListener)this.openFolderButton);
            this.screenSwitchButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"options.iris.shaderPackList"), button -> {
                this.optionMenuOpen = !this.optionMenuOpen;
                this.applyChanges();
                this.m_7522_((GuiEventListener)this.shaderPackList.m_7222_());
                this.m_7856_();
            }).m_252987_(topCenter + 78, this.f_96544_ - 51, 152, 20).m_253136_());
            this.refreshScreenSwitchButton();
        }
        if (inWorld) {
            MutableComponent showOrHide = this.guiHidden ? Component.m_237115_((String)"options.iris.gui.show") : Component.m_237115_((String)"options.iris.gui.hide");
            float endOfLastButton = (float)this.f_96543_ / 2.0f + 154.0f;
            float freeSpace = (float)this.f_96543_ - endOfLastButton;
            int x = freeSpace > 100.0f ? this.f_96543_ - 50 : (freeSpace < 20.0f ? this.f_96543_ - 20 : (int)(endOfLastButton + freeSpace / 2.0f) - 10);
            OldImageButton showHideButton = new OldImageButton(x, this.f_96544_ - 39, 20, 20, this.guiHidden ? 20 : 0, 146, 20, GuiUtil.IRIS_WIDGETS_TEX, 256, 256, button -> {
                this.guiHidden = !this.guiHidden;
                this.m_7856_();
            }, (Component)showOrHide);
            showHideButton.m_257544_(Tooltip.m_257550_((Component)showOrHide));
            showHideButton.m_257427_(10);
            this.m_142416_((GuiEventListener)showHideButton);
        }
        this.hoveredElement = null;
        this.hoveredElementCommentTimer = 0;
    }

    public void refreshForChangedPack() {
        if (Iris.getCurrentPack().isPresent()) {
            ShaderPack currentPack = Iris.getCurrentPack().get();
            this.navigation = new NavigationController(currentPack.getMenuContainer());
            if (this.shaderOptionList != null) {
                this.shaderOptionList.applyShaderPack(currentPack);
                this.shaderOptionList.rebuild();
            }
        } else {
            this.navigation = null;
        }
        this.refreshScreenSwitchButton();
    }

    public void refreshScreenSwitchButton() {
        if (this.screenSwitchButton != null) {
            this.screenSwitchButton.m_93666_((Component)(this.optionMenuOpen ? Component.m_237115_((String)"options.iris.shaderPackList") : Component.m_237115_((String)"options.iris.shaderPackSettings")));
            this.screenSwitchButton.f_93623_ = this.optionMenuOpen || this.shaderPackList.getTopButtonRow().shadersEnabled;
        }
    }

    public void m_86600_() {
        super.m_86600_();
        if (this.notificationDialogTimer > 0) {
            --this.notificationDialogTimer;
        }
        this.hoveredElementCommentTimer = this.hoveredElement != null ? ++this.hoveredElementCommentTimer : 0;
    }

    public boolean m_7933_(int key, int j, int k) {
        if (key == 256) {
            if (this.guiHidden) {
                this.guiHidden = false;
                this.m_7856_();
                return true;
            }
            if (this.navigation != null && this.navigation.hasHistory()) {
                this.navigation.back();
                return true;
            }
            if (this.optionMenuOpen) {
                this.optionMenuOpen = false;
                this.m_7856_();
                return true;
            }
        } else if (key == 258) {
            if (!this.optionMenuOpen) {
                this.shaderPackList.m_7933_(257, 0, 0);
            }
            this.optionMenuOpen = !this.optionMenuOpen;
            this.applyChanges();
            this.m_7856_();
            this.m_7522_(null);
        }
        return this.guiHidden || super.m_7933_(key, j, k);
    }

    public void m_7400_(List<Path> paths) {
        if (this.optionMenuOpen) {
            this.onOptionMenuFilesDrop(paths);
        } else {
            this.onPackListFilesDrop(paths);
        }
    }

    public void onPackListFilesDrop(List<Path> paths) {
        List packs = paths.stream().filter(Iris::isValidShaderpack).toList();
        for (Path pack : packs) {
            String fileName = pack.getFileName().toString();
            try {
                Iris.getShaderpacksDirectoryManager().copyPackIntoDirectory(fileName, pack);
            }
            catch (FileAlreadyExistsException e) {
                this.notificationDialog = Component.m_237110_((String)"options.iris.shaderPackSelection.copyErrorAlreadyExists", (Object[])new Object[]{fileName}).m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
                this.notificationDialogTimer = 100;
                this.shaderPackList.refresh();
                return;
            }
            catch (IOException e) {
                Iris.logger.warn("Error copying dragged shader pack", e);
                this.notificationDialog = Component.m_237110_((String)"options.iris.shaderPackSelection.copyError", (Object[])new Object[]{fileName}).m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
                this.notificationDialogTimer = 100;
                this.shaderPackList.refresh();
                return;
            }
        }
        this.shaderPackList.refresh();
        if (packs.isEmpty()) {
            if (paths.size() == 1) {
                String fileName = paths.get(0).getFileName().toString();
                this.notificationDialog = Component.m_237110_((String)"options.iris.shaderPackSelection.failedAddSingle", (Object[])new Object[]{fileName}).m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            } else {
                this.notificationDialog = Component.m_237115_((String)"options.iris.shaderPackSelection.failedAdd").m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            }
        } else if (packs.size() == 1) {
            String packName = ((Path)packs.get(0)).getFileName().toString();
            this.notificationDialog = Component.m_237110_((String)"options.iris.shaderPackSelection.addedPack", (Object[])new Object[]{packName}).m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.YELLOW});
            this.shaderPackList.select(packName);
        } else {
            this.notificationDialog = Component.m_237110_((String)"options.iris.shaderPackSelection.addedPacks", (Object[])new Object[]{packs.size()}).m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.YELLOW});
        }
        this.notificationDialogTimer = 100;
    }

    public void displayNotification(Component component) {
        this.notificationDialog = component;
        this.notificationDialogTimer = 100;
    }

    public void onOptionMenuFilesDrop(List<Path> paths) {
        if (paths.size() != 1) {
            this.notificationDialog = Component.m_237115_((String)"options.iris.shaderPackOptions.tooManyFiles").m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            this.notificationDialogTimer = 100;
            return;
        }
        this.importPackOptions(paths.get(0));
    }

    public void importPackOptions(Path settingFile) {
        try (InputStream in = Files.newInputStream(settingFile, new OpenOption[0]);){
            Properties properties = new Properties();
            properties.load(in);
            Iris.queueShaderPackOptionsFromProperties(properties);
            this.notificationDialog = Component.m_237110_((String)"options.iris.shaderPackOptions.importedSettings", (Object[])new Object[]{settingFile.getFileName().toString()}).m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.YELLOW});
            this.notificationDialogTimer = 100;
            if (this.navigation != null) {
                this.navigation.refresh();
            }
        }
        catch (Exception e) {
            Iris.logger.error("Error importing shader settings file \"" + settingFile.toString() + "\"", e);
            this.notificationDialog = Component.m_237110_((String)"options.iris.shaderPackOptions.failedImport", (Object[])new Object[]{settingFile.getFileName().toString()}).m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            this.notificationDialogTimer = 100;
        }
    }

    public void m_7379_() {
        if (!this.dropChanges) {
            this.applyChanges();
        } else {
            this.discardChanges();
        }
        try {
            this.shaderPackList.close();
        }
        catch (IOException e) {
            Iris.logger.error("Failed to safely close shaderpack selection!", e);
        }
        this.f_96541_.m_91152_(this.parent);
    }

    private void dropChangesAndClose() {
        this.dropChanges = true;
        this.m_7379_();
    }

    public void applyChanges() {
        String previousPackName;
        ShaderPackSelectionList.BaseEntry base = (ShaderPackSelectionList.BaseEntry)this.shaderPackList.m_93511_();
        boolean enabled = this.shaderPackList.getTopButtonRow().shadersEnabled;
        boolean previousShadersEnabled = Iris.getIrisConfig().areShadersEnabled();
        if (enabled != previousShadersEnabled) {
            IrisApi.getInstance().getConfig().setShadersEnabledAndApply(enabled);
        }
        if (!(base instanceof ShaderPackSelectionList.ShaderPackEntry)) {
            return;
        }
        ShaderPackSelectionList.ShaderPackEntry entry = (ShaderPackSelectionList.ShaderPackEntry)base;
        this.shaderPackList.setApplied(entry);
        String name = entry.getPackName();
        if (!name.equals(Iris.getCurrentPackName())) {
            Iris.clearShaderPackOptionQueue();
        }
        if (!name.equals(previousPackName = (String)Iris.getIrisConfig().getShaderPackName().orElse(null)) || !Iris.getShaderPackOptionQueue().isEmpty() || Iris.shouldResetShaderPackOptionsOnNextReload()) {
            Iris.getIrisConfig().setShaderPackName(name);
            IrisApi.getInstance().getConfig().setShadersEnabledAndApply(enabled);
        }
        this.refreshForChangedPack();
    }

    private void discardChanges() {
        Iris.clearShaderPackOptionQueue();
    }

    private void openShaderPackFolder() {
        CompletableFuture.runAsync(() -> Util.m_137581_().m_137648_(Iris.getShaderpacksDirectoryManager().getDirectoryUri()));
    }

    public void setElementHoveredStatus(AbstractElementWidget<?> widget, boolean hovered) {
        if (hovered && widget != this.hoveredElement) {
            this.hoveredElement = widget;
            if (widget instanceof CommentedElementWidget) {
                this.hoveredElementCommentTitle = ((CommentedElementWidget)widget).getCommentTitle();
                Optional<Component> commentBody = ((CommentedElementWidget)widget).getCommentBody();
                if (!commentBody.isPresent()) {
                    this.hoveredElementCommentBody.clear();
                } else {
                    String rawCommentBody = commentBody.get().getString();
                    if (rawCommentBody.endsWith(".")) {
                        rawCommentBody = rawCommentBody.substring(0, rawCommentBody.length() - 1);
                    }
                    List splitByPeriods = Arrays.stream(rawCommentBody.split("\\. [ ]*")).map(Component::m_237113_).toList();
                    this.hoveredElementCommentBody = new ArrayList<FormattedCharSequence>();
                    for (MutableComponent text : splitByPeriods) {
                        this.hoveredElementCommentBody.addAll(this.f_96547_.m_92923_((FormattedText)text, 306));
                    }
                }
            } else {
                this.hoveredElementCommentTitle = Optional.empty();
                this.hoveredElementCommentBody.clear();
            }
            this.hoveredElementCommentTimer = 0;
        } else if (!hovered && widget == this.hoveredElement) {
            this.hoveredElement = null;
            this.hoveredElementCommentTitle = Optional.empty();
            this.hoveredElementCommentBody.clear();
            this.hoveredElementCommentTimer = 0;
        }
    }

    public boolean isDisplayingComment() {
        return this.hoveredElementCommentTimer > 20 && this.hoveredElementCommentTitle.isPresent() && !this.hoveredElementCommentBody.isEmpty();
    }

    public Button getBottomRowOption() {
        return this.openFolderButton;
    }
}

