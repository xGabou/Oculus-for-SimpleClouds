/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractScrollWidget
 *  net.minecraft.client.gui.components.MultiLineTextWidget
 *  net.minecraft.client.gui.layouts.GridLayout
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.layouts.LayoutSettings
 *  net.minecraft.client.gui.layouts.SpacerElement
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package net.irisshaders.iris.gui.debug;

import java.util.Objects;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class DebugTextWidget
extends AbstractScrollWidget {
    private final Font font;
    private final Content content;

    public DebugTextWidget(int i, int j, int k, int l, Font arg, Exception exception) {
        super(i, j, k, l, (Component)Component.m_237119_());
        this.font = arg;
        this.content = this.buildContent(exception);
    }

    private Content buildContent(Exception exception) {
        if (exception instanceof ShaderCompileException) {
            ShaderCompileException sce = (ShaderCompileException)exception;
            return this.buildContentShader(sce);
        }
        ContentBuilder lv = new ContentBuilder(this.containerWidth());
        StackTraceElement[] elements = exception.getStackTrace();
        lv.addHeader(this.font, (Component)Component.m_237113_((String)"Error: "));
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        if (exception.getMessage() != null) {
            lv.addLine(this.font, (Component)Component.m_237113_((String)exception.getMessage()));
        }
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        lv.addHeader(this.font, (Component)Component.m_237113_((String)"Stack trace: "));
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        for (int i = 0; i < elements.length; ++i) {
            StackTraceElement element = elements[i];
            if (element == null) continue;
            lv.addLine(this.font, (Component)Component.m_237113_((String)element.toString()));
            if (i >= elements.length - 1) continue;
            Objects.requireNonNull(this.font);
            lv.addSpacer(9);
        }
        return lv.build();
    }

    private Content buildContentShader(ShaderCompileException sce) {
        ContentBuilder lv = new ContentBuilder(this.containerWidth());
        lv.addHeader(this.font, (Component)Component.m_237113_((String)("Shader compile error in " + sce.getFilename() + ": ")));
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        lv.addLine(this.font, (Component)Component.m_237113_((String)sce.getError()));
        return lv.build();
    }

    protected int m_239019_() {
        return this.content.container().m_93694_();
    }

    protected boolean m_239656_() {
        return this.m_239019_() > this.f_93619_;
    }

    protected double m_239725_() {
        Objects.requireNonNull(this.font);
        return 9.0;
    }

    protected void m_239197_(GuiGraphics arg, int i, int j, float f) {
        int k = this.m_252907_() + this.m_239244_();
        int l = this.m_252754_() + this.m_239244_();
        arg.m_280168_().m_85836_();
        arg.m_280168_().m_85837_((double)l, (double)k, 0.0);
        this.content.container().m_264134_(element -> element.m_88315_(arg, i, j, f));
        arg.m_280168_().m_85849_();
    }

    protected void m_168797_(NarrationElementOutput arg) {
        arg.m_169146_(NarratedElementType.TITLE, this.content.narration());
    }

    private int containerWidth() {
        return this.f_93618_ - this.m_240012_();
    }

    record Content(GridLayout container, Component narration) {
    }

    static class ContentBuilder {
        private final int width;
        private final GridLayout grid;
        private final GridLayout.RowHelper helper;
        private final LayoutSettings alignHeader;
        private final MutableComponent narration = Component.m_237119_();

        public ContentBuilder(int i) {
            this.width = i;
            this.grid = new GridLayout();
            this.grid.m_264211_().m_264463_();
            this.helper = this.grid.m_264606_(1);
            this.helper.m_264139_((LayoutElement)SpacerElement.m_264527_((int)i));
            this.alignHeader = this.helper.m_264551_().m_264356_().m_264215_(32);
        }

        public void addLine(Font arg, Component arg2) {
            this.addLine(arg, arg2, 0);
        }

        public void addLine(Font arg, Component arg2, int i) {
            this.helper.m_264206_((LayoutElement)new MultiLineTextWidget(this.width, 1, arg2, arg), this.helper.m_264551_().m_264154_(i));
            this.narration.m_7220_(arg2).m_130946_("\n");
        }

        public void addHeader(Font arg, Component arg2) {
            this.helper.m_264206_((LayoutElement)new MultiLineTextWidget(this.width - 64, 1, arg2, arg).m_269484_(true), this.alignHeader);
            this.narration.m_7220_(arg2).m_130946_("\n");
        }

        public void addSpacer(int i) {
            this.helper.m_264139_((LayoutElement)SpacerElement.m_264252_((int)i));
        }

        public Content build() {
            this.grid.m_264036_();
            return new Content(this.grid, (Component)this.narration);
        }
    }
}

