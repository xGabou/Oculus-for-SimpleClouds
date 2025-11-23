/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package net.irisshaders.iris.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class OldImageButton
extends Button {
    protected final ResourceLocation resourceLocation;
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int yDiffTex;
    protected final int textureWidth;
    protected final int textureHeight;

    public OldImageButton(int pImageButton0, int pInt1, int pInt2, int pInt3, int pInt4, int pInt5, ResourceLocation pResourceLocation6, Button.OnPress pButton$OnPress7) {
        this(pImageButton0, pInt1, pInt2, pInt3, pInt4, pInt5, pInt3, pResourceLocation6, 256, 256, pButton$OnPress7);
    }

    public OldImageButton(int pImageButton0, int pInt1, int pInt2, int pInt3, int pInt4, int pInt5, int pInt6, ResourceLocation pResourceLocation7, Button.OnPress pButton$OnPress8) {
        this(pImageButton0, pInt1, pInt2, pInt3, pInt4, pInt5, pInt6, pResourceLocation7, 256, 256, pButton$OnPress8);
    }

    public OldImageButton(int pImageButton0, int pInt1, int pInt2, int pInt3, int pInt4, int pInt5, int pInt6, ResourceLocation pResourceLocation7, int pInt8, int pInt9, Button.OnPress pButton$OnPress10) {
        this(pImageButton0, pInt1, pInt2, pInt3, pInt4, pInt5, pInt6, pResourceLocation7, pInt8, pInt9, pButton$OnPress10, CommonComponents.f_237098_);
    }

    public OldImageButton(int pImageButton0, int pInt1, int pInt2, int pInt3, int pInt4, int pInt5, int pInt6, ResourceLocation pResourceLocation7, int pInt8, int pInt9, Button.OnPress pButton$OnPress10, Component pComponent11) {
        super(pImageButton0, pInt1, pInt2, pInt3, pComponent11, pButton$OnPress10, f_252438_);
        this.textureWidth = pInt8;
        this.textureHeight = pInt9;
        this.xTexStart = pInt4;
        this.yTexStart = pInt5;
        this.yDiffTex = pInt6;
        this.resourceLocation = pResourceLocation7;
    }

    public void m_87963_(GuiGraphics pImageButton0, int pInt1, int pInt2, float pFloat3) {
        this.m_280322_(pImageButton0, this.resourceLocation, this.m_252754_(), this.m_252907_(), this.xTexStart, this.yTexStart, this.yDiffTex, this.f_93618_, this.f_93619_, this.textureWidth, this.textureHeight);
    }

    public void m_280322_(GuiGraphics pAbstractWidget0, ResourceLocation pResourceLocation1, int pInt2, int pInt3, int pInt4, int pInt5, int pInt6, int pInt7, int pInt8, int pInt9, int pInt10) {
        int lvInt12 = pInt5;
        if (!this.m_142518_()) {
            lvInt12 = pInt5 + pInt6 * 2;
        } else if (this.m_198029_()) {
            lvInt12 = pInt5 + pInt6;
        }
        RenderSystem.enableDepthTest();
        pAbstractWidget0.m_280163_(pResourceLocation1, pInt2, pInt3, (float)pInt4, (float)lvInt12, pInt7, pInt8, pInt9, pInt10);
    }
}

