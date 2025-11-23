/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.type.specifier;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import io.github.douira.glsl_transformer.ast.data.TypeUtil;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import oculus.org.antlr.v4.runtime.Token;

public class BuiltinFixedTypeSpecifier
extends TypeSpecifier {
    public BuiltinType type;

    public BuiltinFixedTypeSpecifier(BuiltinType type) {
        this.type = type;
    }

    public BuiltinFixedTypeSpecifier(BuiltinType type, ArraySpecifier arraySpecifier) {
        super(arraySpecifier);
        this.type = type;
    }

    @Override
    public TypeSpecifier.SpecifierType getSpecifierType() {
        return TypeSpecifier.SpecifierType.BULTIN_FIXED;
    }

    @Override
    public <R> R typeSpecifierAccept(ASTVisitor<R> visitor) {
        return visitor.visitBuiltinFixedTypeSpecifier(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
    }

    @Override
    public BuiltinFixedTypeSpecifier clone() {
        return new BuiltinFixedTypeSpecifier(this.type, BuiltinFixedTypeSpecifier.clone(this.arraySpecifier));
    }

    @Override
    public BuiltinFixedTypeSpecifier cloneInto(Root root) {
        return (BuiltinFixedTypeSpecifier)super.cloneInto(root);
    }

    public static enum BuiltinType implements TokenTyped
    {
        VOID(219, TypeKind.VOID),
        ATOMIC_UINT(44, TypeKind.ATOMIC_UINT),
        SAMPLER2D(154, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER3D(155, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLERCUBE(199, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER2DSHADOW(158, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLERCUBESHADOW(197, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER2DARRAY(161, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER2DARRAYSHADOW(163, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLERCUBEARRAY(205, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLERCUBEARRAYSHADOW(198, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        ISAMPLER2D(165, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        ISAMPLER3D(167, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        ISAMPLERCUBE(200, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        ISAMPLER2DARRAY(169, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        ISAMPLERCUBEARRAY(206, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        USAMPLER2D(171, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        USAMPLER3D(173, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        USAMPLERCUBE(201, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        USAMPLER2DARRAY(175, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        USAMPLERCUBEARRAY(207, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        SAMPLER1D(153, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER1DSHADOW(157, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER1DARRAY(160, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER1DARRAYSHADOW(162, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        ISAMPLER1D(164, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        ISAMPLER1DARRAY(168, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        USAMPLER1D(170, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        USAMPLER1DARRAY(174, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        SAMPLER2DRECT(156, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        SAMPLER2DRECTSHADOW(159, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        ISAMPLER2DRECT(166, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        USAMPLER2DRECT(172, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        SAMPLERBUFFER(202, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        ISAMPLERBUFFER(203, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        USAMPLERBUFFER(204, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        SAMPLER2DMS(176, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        ISAMPLER2DMS(177, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        USAMPLER2DMS(178, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        SAMPLER2DMSARRAY(179, TypeKind.SAMPLER, ValueFormat.FLOATING_POINT),
        ISAMPLER2DMSARRAY(180, TypeKind.SAMPLER, ValueFormat.SIGNED_INTEGER),
        USAMPLER2DMSARRAY(181, TypeKind.SAMPLER, ValueFormat.UNSIGNED_INTEGER),
        IMAGE2D(145, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE2D(151, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE2D(148, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGE3D(146, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE3D(152, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE3D(149, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGECUBE(208, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGECUBE(210, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGECUBE(209, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGEBUFFER(211, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGEBUFFER(212, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGEBUFFER(213, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGE1D(144, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE1D(150, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE1D(147, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGE1DARRAY(183, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE1DARRAY(188, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE1DARRAY(193, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGE2DRECT(182, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE2DRECT(187, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE2DRECT(192, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGE2DARRAY(184, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE2DARRAY(189, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE2DARRAY(194, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGECUBEARRAY(214, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGECUBEARRAY(215, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGECUBEARRAY(216, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGE2DMS(185, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE2DMS(190, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE2DMS(195, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        IMAGE2DMSARRAY(186, TypeKind.IMAGE, ValueFormat.FLOATING_POINT),
        IIMAGE2DMSARRAY(191, TypeKind.IMAGE, ValueFormat.SIGNED_INTEGER),
        UIMAGE2DMSARRAY(196, TypeKind.IMAGE, ValueFormat.UNSIGNED_INTEGER),
        ACCELERATION_STRUCTURE(43, TypeKind.ACCELERATION_STRUCTURE);

        public final int tokenType;
        public final TypeKind kind;
        public final ValueFormat valueFormat;

        private BuiltinType(int tokenType, TypeKind kind, ValueFormat valueFormat) {
            this.tokenType = tokenType;
            this.kind = kind;
            this.valueFormat = valueFormat;
        }

        private BuiltinType(int tokenType, TypeKind kind) {
            this(tokenType, kind, null);
        }

        @Override
        public int getTokenType() {
            return this.tokenType;
        }

        public static BuiltinType fromToken(Token token) {
            return (BuiltinType)TypeUtil.enumFromToken((TokenTyped[])BuiltinType.values(), (Token)token);
        }

        public static enum TypeKind {
            VOID,
            ATOMIC_UINT,
            SAMPLER,
            IMAGE,
            ACCELERATION_STRUCTURE;

        }

        public static enum ValueFormat {
            FLOATING_POINT,
            SIGNED_INTEGER,
            UNSIGNED_INTEGER;

        }
    }
}

