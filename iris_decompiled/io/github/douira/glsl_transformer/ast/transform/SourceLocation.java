/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

public class SourceLocation {
    public static final int NONE = -1;
    public static final SourceLocation PLACEHOLDER = new SourceLocation();
    public final int line;
    public final int source;

    public SourceLocation(int line, int source) {
        this.line = line;
        this.source = source;
    }

    public SourceLocation(int line) {
        this(line, -1);
    }

    public SourceLocation() {
        this(-1);
    }

    public static SourceLocation fromPrevious(SourceLocation previous, int line) {
        if (previous == null) {
            return new SourceLocation(line);
        }
        if (previous.line == line) {
            return previous;
        }
        return new SourceLocation(line, previous.source);
    }

    public static SourceLocation fromPrevious(SourceLocation previous, int line, int source) {
        if (previous == null) {
            return new SourceLocation(line, source);
        }
        if (previous.line == line && previous.source == source) {
            return previous;
        }
        return new SourceLocation(line, source == -1 ? previous.source : source);
    }

    public boolean hasLine() {
        return this.line != -1;
    }

    public boolean hasSource() {
        return this.source != -1;
    }

    public String toLineDirective() {
        return "#line " + this.line + (String)(this.hasSource() ? " " + this.source : "") + "\n";
    }
}

