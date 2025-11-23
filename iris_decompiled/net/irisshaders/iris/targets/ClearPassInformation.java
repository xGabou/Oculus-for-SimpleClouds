/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.targets;

import org.joml.Vector4f;

public class ClearPassInformation {
    private final Vector4f color;
    private final int width;
    private final int height;

    public ClearPassInformation(Vector4f vector4f, int width, int height) {
        this.color = vector4f;
        this.width = width;
        this.height = height;
    }

    public Vector4f getColor() {
        return this.color;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ClearPassInformation)) {
            return false;
        }
        ClearPassInformation information = (ClearPassInformation)obj;
        return information.color.equals((Object)this.color) && information.height == this.height && information.width == this.width;
    }
}

