/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.qualifier;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import io.github.douira.glsl_transformer.ast.data.TypeUtil;
import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifierPart;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.stream.Stream;
import oculus.org.antlr.v4.runtime.Token;

public class StorageQualifier
extends TypeQualifierPart {
    protected ChildNodeList<Identifier> typeNames;
    public StorageType storageType;

    private StorageQualifier(Stream<Identifier> typeNames, StorageType storageType) {
        this.typeNames = ChildNodeList.collect(typeNames, this);
        this.storageType = storageType;
    }

    public StorageQualifier(Stream<Identifier> typeNames) {
        this.typeNames = ChildNodeList.collect(typeNames, this);
        this.storageType = StorageType.SUBROUTINE;
    }

    public StorageQualifier(StorageType storageType) {
        this.typeNames = null;
        this.storageType = storageType;
    }

    public ChildNodeList<Identifier> getTypeNames() {
        return this.typeNames;
    }

    @Override
    public TypeQualifierPart.QualifierType getQualifierType() {
        return TypeQualifierPart.QualifierType.STORAGE;
    }

    @Override
    public <R> R typeQualifierPartAccept(ASTVisitor<R> visitor) {
        return visitor.visitStorageQualifier(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterStorageQualifier(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitStorageQualifier(this);
    }

    @Override
    public StorageQualifier clone() {
        return new StorageQualifier(StorageQualifier.clone(this.typeNames), this.storageType);
    }

    @Override
    public StorageQualifier cloneInto(Root root) {
        return (StorageQualifier)super.cloneInto(root);
    }

    public static enum StorageType implements TokenTyped
    {
        CONST(11),
        IN(4),
        OUT(5),
        INOUT(6),
        CENTROID(16),
        PATCH(25),
        SAMPLE(24),
        UNIFORM(2),
        VARYING(19),
        ATTRIBUTE(17),
        BUFFER(3),
        SHARED(20),
        RESTRICT(27),
        VOLATILE(18),
        COHERENT(26),
        READONLY(28),
        WRITEONLY(29),
        SUBROUTINE(30),
        DEVICECOHERENT(31),
        QUEUEFAMILYCOHERENT(32),
        WORKGROUPCOHERENT(33),
        SUBGROUPCOHERENT(34),
        NONPRIVATE(35),
        RAY_PAYLOAD_EXT(36),
        RAY_PAYLOAD_IN_EXT(37),
        HIT_ATTRIBUTE_EXT(38),
        CALLABLE_DATA_EXT(39),
        CALLABLE_DATA_IN_EXT(40);

        public final int tokenType;

        private StorageType(int tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static StorageType fromToken(Token token) {
            return (StorageType)TypeUtil.enumFromToken((TokenTyped[])StorageType.values(), (Token)token);
        }
    }
}

