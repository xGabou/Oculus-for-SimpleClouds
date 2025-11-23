/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import oculus.org.antlr.v4.runtime.Token;

public class Identifier
extends ASTNode {
    private String name;

    public Identifier(String name) {
        this.name = name;
        Identifier.validateContents(name);
    }

    public Identifier(Token token) {
        this(token.getText());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (this.name.equals(name)) {
            return;
        }
        Identifier.validateContents(name);
        this.getRoot().unregisterIdentifierRename(this);
        this.name = name;
        this.getRoot().registerIdentifierRename(this);
    }

    public void _setNameInternal(String name) {
        this.getRoot().unregisterFastRename(this);
        this.name = name;
        this.getRoot().registerFastRename(this);
    }

    public static final void validateContents(String str) {
        if (str.length() == 0) {
            throw new IllegalArgumentException("Identifier cannot be empty");
        }
        if (!Character.isLetter(str.charAt(0)) && str.charAt(0) != '_') {
            throw new IllegalArgumentException("Identifier must start with a letter or underscore");
        }
        for (int i = 1; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_') continue;
            throw new IllegalArgumentException("Invalid identifier name: " + str);
        }
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIdentifier(this);
    }

    @Override
    public Identifier clone() {
        return new Identifier(this.name);
    }

    @Override
    public Identifier cloneInto(Root root) {
        return (Identifier)super.cloneInto(root);
    }
}

