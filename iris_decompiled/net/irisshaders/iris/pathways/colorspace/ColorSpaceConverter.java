/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.pathways.colorspace;

import net.irisshaders.iris.pathways.colorspace.ColorSpace;

public interface ColorSpaceConverter {
    public void rebuildProgram(int var1, int var2, ColorSpace var3);

    public void process(int var1);
}

