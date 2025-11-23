/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.AbstractSelectionList
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 */
package net.irisshaders.iris.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class IrisObjectSelectionList<E extends AbstractSelectionList.Entry<E>>
extends AbstractSelectionList<E> {
    public IrisObjectSelectionList(Minecraft client, int width, int height, int top, int bottom, int left, int right, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.f_93393_ = left;
        this.f_93392_ = right;
    }

    protected int m_5756_() {
        return this.f_93388_ - 6;
    }

    public void select(int entry) {
        this.m_6987_(this.m_93500_(entry));
    }

    public void m_142291_(NarrationElementOutput p0) {
    }
}

