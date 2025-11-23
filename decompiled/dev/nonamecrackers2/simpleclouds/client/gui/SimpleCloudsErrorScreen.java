/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.nonamecrackers2.simpleclouds.client.gui;

import dev.nonamecrackers2.simpleclouds.client.gui.SimpleCloudsInfoScreen;
import dev.nonamecrackers2.simpleclouds.client.mesh.RendererInitializeResult;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class SimpleCloudsErrorScreen
extends SimpleCloudsInfoScreen {
    private static final Component DESCRIPTION = Component.m_237115_((String)"gui.simpleclouds.error_screen.description");
    private final RendererInitializeResult result;
    private Path crashReportsFolder;

    public SimpleCloudsErrorScreen(RendererInitializeResult result) {
        super((Component)Component.m_237115_((String)"gui.simpleclouds.error_screen.title").m_130948_(Style.f_131099_.m_131162_(Boolean.valueOf(true)).m_131136_(Boolean.valueOf(true))), 3);
        this.result = result;
    }

    @Override
    protected void generateText(List<FormattedCharSequence> text, int maxWidth) {
        text.addAll(this.f_96547_.m_92923_((FormattedText)DESCRIPTION, maxWidth));
        if (!this.result.getErrors().isEmpty()) {
            RendererInitializeResult.Error error = this.result.getErrors().get(this.result.getErrors().size() - 1);
            text.add(FormattedCharSequence.f_13691_);
            text.addAll(this.f_96547_.m_92923_((FormattedText)error.text(), maxWidth));
            if (this.result.getErrors().size() > 1) {
                text.add(FormattedCharSequence.f_13691_);
                text.addAll(this.f_96547_.m_92923_((FormattedText)Component.m_237115_((String)"gui.simpleclouds.error_screen.multiple"), maxWidth));
            }
        } else {
            text.add(Component.m_237115_((String)"gui.simpleclouds.error_screen.no_errors").m_7532_());
        }
    }

    @Override
    protected void generateButtons(GridLayout.RowHelper row) {
        super.generateButtons(row);
        Button button = (Button)row.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237115_((String)"gui.simpleclouds.error_screen.button.crash_report"), b -> {
            List<Path> list = this.result.getSavedCrashReportPaths();
            if (list != null && list.size() == 1) {
                Util.m_137581_().m_137648_(list.get(0).toUri());
            } else {
                Util.m_137581_().m_137648_(this.crashReportsFolder.toUri());
            }
        }).m_252780_(100).m_253136_());
        button.f_93623_ = this.result.getSavedCrashReportPaths() != null && !this.result.getSavedCrashReportPaths().isEmpty();
    }

    @Override
    protected void m_7856_() {
        this.crashReportsFolder = this.f_96541_.f_91069_.toPath().resolve("crash-reports");
        super.m_7856_();
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (super.m_7933_(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 82 && Screen.m_96637_()) {
            this.f_96541_.m_91391_().thenRunAsync(() -> {
                SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getOptionalInstance().orElse(null);
                if (renderer == null) {
                    return;
                }
                RendererInitializeResult result = renderer.getInitialInitializationResult();
                if (result != null && result.getState() == RendererInitializeResult.State.ERROR) {
                    this.f_96541_.m_91152_((Screen)new SimpleCloudsErrorScreen(renderer.getInitialInitializationResult()));
                } else {
                    this.f_96541_.m_91152_(null);
                }
            }, (Executor)this.f_96541_);
            return true;
        }
        return false;
    }

    public boolean m_6913_() {
        return false;
    }

    public void m_7379_() {
    }
}

