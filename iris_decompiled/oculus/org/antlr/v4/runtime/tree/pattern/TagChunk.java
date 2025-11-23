/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.pattern;

import oculus.org.antlr.v4.runtime.tree.pattern.Chunk;

class TagChunk
extends Chunk {
    private final String tag;
    private final String label;

    public TagChunk(String tag) {
        this(null, tag);
    }

    public TagChunk(String label, String tag) {
        if (tag == null || tag.isEmpty()) {
            throw new IllegalArgumentException("tag cannot be null or empty");
        }
        this.label = label;
        this.tag = tag;
    }

    public final String getTag() {
        return this.tag;
    }

    public final String getLabel() {
        return this.label;
    }

    public String toString() {
        if (this.label != null) {
            return this.label + ":" + this.tag;
        }
        return this.tag;
    }
}

