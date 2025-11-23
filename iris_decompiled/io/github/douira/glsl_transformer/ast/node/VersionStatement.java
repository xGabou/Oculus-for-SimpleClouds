/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node;

import io.github.douira.glsl_transformer.ast.node.Profile;
import io.github.douira.glsl_transformer.ast.node.Version;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;

public class VersionStatement
extends ASTNode {
    public Version version;
    public Profile profile;

    public VersionStatement(Version version, Profile profile) {
        this.version = version;
        this.profile = profile;
    }

    public static VersionStatement getDefault() {
        return new VersionStatement(Version.GLSL11, null);
    }

    public Profile getNormalizedProfile() {
        return this.profile == null ? (this.version.number >= 150 ? Profile.CORE : Profile.COMPATIBILITY) : this.profile;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVersionStatement(this);
    }

    @Override
    public VersionStatement clone() {
        return new VersionStatement(this.version, this.profile);
    }

    @Override
    public VersionStatement cloneInto(Root root) {
        return (VersionStatement)super.cloneInto(root);
    }
}

