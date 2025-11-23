/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.Holder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 */
package net.irisshaders.iris.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public final class GuiUtil {
    public static final ResourceLocation IRIS_WIDGETS_TEX = new ResourceLocation("iris", "textures/gui/widgets.png");
    private static final Component ELLIPSIS = Component.m_237113_((String)"...");

    private GuiUtil() {
    }

    private static Minecraft client() {
        return Minecraft.m_91087_();
    }

    public static void bindIrisWidgetsTexture() {
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)IRIS_WIDGETS_TEX);
    }

    public static void drawButton(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, boolean disabled) {
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int vOffset = disabled ? 46 : (hovered ? 86 : 66);
        RenderSystem.enableBlend();
        guiGraphics.m_280163_(IRIS_WIDGETS_TEX, x, y, 0.0f, (float)vOffset, halfWidth, halfHeight, 256, 256);
        guiGraphics.m_280163_(IRIS_WIDGETS_TEX, x + halfWidth, y, (float)(200 - (width - halfWidth)), (float)vOffset, width - halfWidth, halfHeight, 256, 256);
        guiGraphics.m_280163_(IRIS_WIDGETS_TEX, x, y + halfHeight, 0.0f, (float)(vOffset + (20 - (height - halfHeight))), halfWidth, height - halfHeight, 256, 256);
        guiGraphics.m_280163_(IRIS_WIDGETS_TEX, x + halfWidth, y + halfHeight, (float)(200 - (width - halfWidth)), (float)(vOffset + (20 - (height - halfHeight))), width - halfWidth, height - halfHeight, 256, 256);
    }

    public static void drawPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int borderColor = -555819298;
        int innerColor = -570425344;
        guiGraphics.m_285944_(RenderType.m_286086_(), x, y, x + width, y + 1, borderColor);
        guiGraphics.m_285944_(RenderType.m_286086_(), x, y + height - 1, x + width, y + height, borderColor);
        guiGraphics.m_285944_(RenderType.m_286086_(), x, y + 1, x + 1, y + height - 1, borderColor);
        guiGraphics.m_285944_(RenderType.m_286086_(), x + width - 1, y + 1, x + width, y + height - 1, borderColor);
        guiGraphics.m_285944_(RenderType.m_286086_(), x + 1, y + 1, x + width - 1, y + height - 1, innerColor);
    }

    public static void drawTextPanel(Font font, GuiGraphics guiGraphics, Component text, int x, int y) {
        GuiUtil.drawPanel(guiGraphics, x, y, font.m_92852_((FormattedText)text) + 8, 16);
        guiGraphics.m_280430_(font, text, x + 4, y + 4, 0xFFFFFF);
    }

    public static MutableComponent shortenText(Font font, MutableComponent text, int width) {
        if (font.m_92852_((FormattedText)text) > width) {
            return Component.m_237113_((String)font.m_92834_(text.getString(), width - font.m_92852_((FormattedText)ELLIPSIS))).m_7220_(ELLIPSIS).m_6270_(text.m_7383_());
        }
        return text;
    }

    public static MutableComponent translateOrDefault(MutableComponent defaultText, String translationDesc, Object ... format) {
        if (I18n.m_118936_((String)translationDesc)) {
            return Component.m_237110_((String)translationDesc, (Object[])format);
        }
        return defaultText;
    }

    public static void playButtonClickSound() {
        GuiUtil.client().m_91106_().m_120367_((SoundInstance)SimpleSoundInstance.m_263171_((Holder)SoundEvents.f_12490_, (float)1.0f));
    }

    public static class Icon {
        public static final Icon SEARCH = new Icon(0, 0, 7, 8);
        public static final Icon CLOSE = new Icon(7, 0, 5, 6);
        public static final Icon REFRESH = new Icon(12, 0, 10, 10);
        public static final Icon EXPORT = new Icon(22, 0, 7, 8);
        public static final Icon EXPORT_COLORED = new Icon(29, 0, 7, 8);
        public static final Icon IMPORT = new Icon(22, 8, 7, 8);
        public static final Icon IMPORT_COLORED = new Icon(29, 8, 7, 8);
        private final int u;
        private final int v;
        private final int width;
        private final int height;

        public Icon(int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }

        public void draw(GuiGraphics guiGraphics, int x, int y) {
            RenderSystem.enableBlend();
            guiGraphics.m_280163_(IRIS_WIDGETS_TEX, x, y, (float)this.u, (float)this.v, this.width, this.height, 256, 256);
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}

