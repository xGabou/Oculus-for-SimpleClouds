/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.texture;

public class TextureScaleOverride {
    public final boolean isXRelative;
    public final boolean isYRelative;
    public float relativeX;
    public float relativeY;
    public int sizeX;
    public int sizeY;

    public TextureScaleOverride(String xValue, String yValue) {
        if (xValue.contains(".")) {
            this.relativeX = Float.parseFloat(xValue);
            this.isXRelative = true;
        } else {
            this.sizeX = Integer.parseInt(xValue);
            this.isXRelative = false;
        }
        if (yValue.contains(".")) {
            this.relativeY = Float.parseFloat(yValue);
            this.isYRelative = true;
        } else {
            this.sizeY = Integer.parseInt(yValue);
            this.isYRelative = false;
        }
    }

    public int getX(int originalX) {
        if (this.isXRelative) {
            return (int)((float)originalX * this.relativeX);
        }
        return this.sizeX;
    }

    public int getY(int originalY) {
        if (this.isYRelative) {
            return (int)((float)originalY * this.relativeY);
        }
        return this.sizeY;
    }
}

