/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.pattern;

import oculus.org.antlr.v4.runtime.tree.pattern.Chunk;

class TextChunk
extends Chunk {
    private final String text;

    public TextChunk(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        this.text = text;
    }

    public final String getText() {
        return this.text;
    }

    public String toString() {
        return "'" + this.text + "'";
    }
}

