/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package net.irisshaders.iris.gui.element.screen;

import net.minecraft.network.chat.Component;

public record ElementWidgetScreenData(Component heading, boolean backButton) {
    public static final ElementWidgetScreenData EMPTY = new ElementWidgetScreenData((Component)Component.m_237119_(), true);
}

