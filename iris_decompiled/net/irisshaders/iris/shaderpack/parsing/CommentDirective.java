/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.parsing;

public class CommentDirective {
    private final Type type;
    private final String directive;
    private final int location;

    CommentDirective(Type type, String directive, int location) {
        this.type = type;
        this.directive = directive;
        this.location = location;
    }

    public Type getType() {
        return this.type;
    }

    public String getDirective() {
        return this.directive;
    }

    public int getLocation() {
        return this.location;
    }

    public static enum Type {
        DRAWBUFFERS,
        RENDERTARGETS;

    }
}

