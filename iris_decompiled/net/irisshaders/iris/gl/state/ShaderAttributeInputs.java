/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 */
package net.irisshaders.iris.gl.state;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

public class ShaderAttributeInputs {
    private boolean color;
    private boolean tex;
    private boolean overlay;
    private boolean light;
    private boolean normal;
    private boolean newLines;
    private boolean glint;
    private boolean text;

    public ShaderAttributeInputs(VertexFormat format, boolean isFullbright, boolean isLines, boolean glint, boolean text) {
        if (format == DefaultVertexFormat.f_166851_ && !isLines) {
            this.newLines = true;
        }
        this.text = text;
        this.glint = glint;
        format.m_166911_().forEach(name -> {
            if ("Color".equals(name)) {
                this.color = true;
            }
            if ("UV0".equals(name)) {
                this.tex = true;
            }
            if ("UV1".equals(name)) {
                this.overlay = true;
            }
            if ("UV2".equals(name) && !isFullbright) {
                this.light = true;
            }
            if ("Normal".equals(name)) {
                this.normal = true;
            }
        });
    }

    public ShaderAttributeInputs(boolean color, boolean tex, boolean overlay, boolean light, boolean normal) {
        this.color = color;
        this.tex = tex;
        this.overlay = overlay;
        this.light = light;
        this.normal = normal;
    }

    public boolean hasColor() {
        return this.color;
    }

    public boolean hasTex() {
        return this.tex;
    }

    public boolean hasOverlay() {
        return this.overlay;
    }

    public boolean hasLight() {
        return this.light;
    }

    public boolean hasNormal() {
        return this.normal;
    }

    public boolean isNewLines() {
        return this.newLines;
    }

    public boolean isGlint() {
        return this.glint;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.color ? 1231 : 1237);
        result = 31 * result + (this.tex ? 1231 : 1237);
        result = 31 * result + (this.overlay ? 1231 : 1237);
        result = 31 * result + (this.light ? 1231 : 1237);
        result = 31 * result + (this.normal ? 1231 : 1237);
        result = 31 * result + (this.newLines ? 1231 : 1237);
        result = 31 * result + (this.glint ? 1231 : 1237);
        result = 31 * result + (this.text ? 1231 : 1237);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ShaderAttributeInputs other = (ShaderAttributeInputs)obj;
        if (this.color != other.color) {
            return false;
        }
        if (this.tex != other.tex) {
            return false;
        }
        if (this.overlay != other.overlay) {
            return false;
        }
        if (this.light != other.light) {
            return false;
        }
        if (this.normal != other.normal) {
            return false;
        }
        if (this.newLines != other.newLines) {
            return false;
        }
        if (this.glint != other.glint) {
            return false;
        }
        return this.text == other.text;
    }

    public boolean isText() {
        return this.text;
    }
}

