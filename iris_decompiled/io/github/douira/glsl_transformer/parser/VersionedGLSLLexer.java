/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.parser;

import io.github.douira.glsl_transformer.ast.node.Version;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.Lexer;

public abstract class VersionedGLSLLexer
extends Lexer {
    public Version version = Version.latest;
    public boolean enableCustomDirective = false;
    public boolean enableIncludeDirective = false;

    public VersionedGLSLLexer(CharStream input) {
        super(input);
    }

    public VersionedGLSLLexer() {
    }

    protected boolean isAfter(int atLeast) {
        return this.version.number >= atLeast;
    }
}

