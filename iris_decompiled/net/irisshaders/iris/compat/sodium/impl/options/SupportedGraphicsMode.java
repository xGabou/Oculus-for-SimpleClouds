/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.GraphicsStatus
 */
package net.irisshaders.iris.compat.sodium.impl.options;

import net.minecraft.client.GraphicsStatus;

public enum SupportedGraphicsMode {
    FAST,
    FANCY;


    public static SupportedGraphicsMode fromVanilla(GraphicsStatus vanilla) {
        if (vanilla == GraphicsStatus.FAST) {
            return FAST;
        }
        return FANCY;
    }

    public GraphicsStatus toVanilla() {
        if (this == FAST) {
            return GraphicsStatus.FAST;
        }
        return GraphicsStatus.FANCY;
    }
}

