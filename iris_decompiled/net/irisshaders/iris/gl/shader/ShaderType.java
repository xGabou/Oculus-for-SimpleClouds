/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.shader;

public enum ShaderType {
    VERTEX(35633),
    GEOMETRY(36313),
    FRAGMENT(35632),
    COMPUTE(37305),
    TESSELATION_CONTROL(36488),
    TESSELATION_EVAL(36487);

    public final int id;

    private ShaderType(int id) {
        this.id = id;
    }
}

