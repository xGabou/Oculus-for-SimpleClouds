/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.layouts.FrameLayout
 *  net.minecraft.client.gui.layouts.GridLayout
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.layouts.LayoutSettings
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.apache.commons.lang3.exception.ExceptionUtils
 */
package net.irisshaders.iris.gui.debug;

import java.io.IOException;
import java.util.Objects;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.debug.DebugTextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class DebugLoadFailedGridScreen
extends Screen {
    private final Exception exception;
    private final Screen parent;

    public DebugLoadFailedGridScreen(Screen parent, Component arg, Exception exception) {
        super(arg);
        this.parent = parent;
        this.exception = exception;
    }

    protected void m_7856_() {
        super.m_7856_();
        GridLayout widget = new GridLayout();
        LayoutSettings layoutSettings = widget.m_264626_().m_264070_().m_264356_();
        LayoutSettings layoutSettings4 = widget.m_264626_().m_264070_().m_264311_(30).m_264356_();
        LayoutSettings layoutSettings2 = widget.m_264626_().m_264070_().m_264311_(30).m_264463_();
        LayoutSettings layoutSettings3 = widget.m_264626_().m_264070_().m_264311_(30).m_264443_();
        int numWidgets = 0;
        Objects.requireNonNull(this.f_96547_);
        widget.m_264167_((LayoutElement)new DebugTextWidget(0, 0, this.f_96543_ - 80, 9 * 15, this.f_96547_, this.exception), ++numWidgets, 0, 1, 2, layoutSettings);
        widget.m_264167_((LayoutElement)Button.m_253074_((Component)Component.m_237115_((String)"menu.returnToGame"), arg2 -> this.f_96541_.m_91152_(this.parent)).m_252780_(100).m_253136_(), ++numWidgets, 0, 1, 2, layoutSettings2);
        widget.m_264167_((LayoutElement)Button.m_253074_((Component)Component.m_237113_((String)"Reload pack"), arg2 -> {
            Minecraft.m_91087_().m_91152_(this.parent);
            try {
                Iris.reload();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).m_252780_(100).m_253136_(), numWidgets, 0, 1, 2, layoutSettings3);
        widget.m_264167_((LayoutElement)Button.m_253074_((Component)Component.m_237113_((String)"Copy error"), arg2 -> this.f_96541_.f_91068_.m_90911_(ExceptionUtils.getStackTrace((Throwable)this.exception))).m_252780_(100).m_253136_(), numWidgets, 0, 1, 2, layoutSettings4);
        widget.m_264036_();
        FrameLayout.m_264159_((LayoutElement)widget, (int)0, (int)0, (int)this.f_96543_, (int)this.f_96544_);
        widget.m_264134_(x$0 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.m_142416_((GuiEventListener)x$0);
        });
    }
}

