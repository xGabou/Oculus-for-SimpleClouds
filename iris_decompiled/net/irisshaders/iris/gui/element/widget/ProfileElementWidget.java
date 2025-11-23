/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 */
package net.irisshaders.iris.gui.element.widget;

import java.util.Optional;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.widget.BaseOptionElementWidget;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.Profile;
import net.irisshaders.iris.shaderpack.option.ProfileSet;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuProfileElement;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public class ProfileElementWidget
extends BaseOptionElementWidget<OptionMenuProfileElement> {
    private static final MutableComponent PROFILE_LABEL = Component.m_237115_((String)"options.iris.profile");
    private static final MutableComponent PROFILE_CUSTOM = Component.m_237115_((String)"options.iris.profile.custom").m_130940_(ChatFormatting.YELLOW);
    private Profile next;
    private Profile previous;
    private Component profileLabel;

    public ProfileElementWidget(OptionMenuProfileElement element) {
        super(element);
    }

    @Override
    public void init(ShaderPackScreen screen, NavigationController navigation) {
        super.init(screen, navigation);
        this.setLabel(PROFILE_LABEL);
        ProfileSet profiles = ((OptionMenuProfileElement)this.element).profiles;
        OptionSet options = ((OptionMenuProfileElement)this.element).options;
        OptionValues pendingValues = ((OptionMenuProfileElement)this.element).getPendingOptionValues();
        ProfileSet.ProfileResult result = profiles.scan(options, pendingValues);
        this.next = result.next;
        this.previous = result.previous;
        Optional<String> profileName = result.current.map(p -> p.name);
        this.profileLabel = (Component)profileName.map(name -> GuiUtil.translateOrDefault(Component.m_237113_((String)name), "profile." + name, new Object[0])).orElse(PROFILE_CUSTOM);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, boolean hovered) {
        this.updateRenderParams(this.bounds.f_263770_() - (Minecraft.m_91087_().f_91062_.m_92852_((FormattedText)PROFILE_LABEL) + 16));
        this.renderOptionWithValue(guiGraphics, hovered || this.m_93696_());
    }

    @Override
    protected Component createValueLabel() {
        return this.profileLabel;
    }

    @Override
    public Optional<Component> getCommentTitle() {
        return Optional.of(PROFILE_LABEL);
    }

    @Override
    public String getCommentKey() {
        return "profile.comment";
    }

    @Override
    public boolean applyNextValue() {
        if (this.next == null) {
            return false;
        }
        Iris.queueShaderPackOptionsFromProfile(this.next);
        return true;
    }

    @Override
    public boolean applyPreviousValue() {
        if (this.previous == null) {
            return false;
        }
        Iris.queueShaderPackOptionsFromProfile(this.previous);
        return true;
    }

    @Override
    public boolean applyOriginalValue() {
        return false;
    }

    @Override
    public boolean isValueModified() {
        return false;
    }
}

