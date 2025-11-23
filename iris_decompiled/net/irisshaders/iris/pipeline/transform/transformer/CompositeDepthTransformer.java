/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.pipeline.transform.transformer;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.declaration.DeclarationMember;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.match.HintedMatcher;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.parser.ParseShape;

class CompositeDepthTransformer {
    private static final HintedMatcher<ExternalDeclaration> uniformFloatCenterDepthSmooth = new HintedMatcher<ExternalDeclaration>("uniform float name;", ParseShape.EXTERNAL_DECLARATION, "centerDepthSmooth"){
        {
            this.markClassWildcard("name*", ((Identifier)((ExternalDeclaration)this.pattern).getRoot().identifierIndex.getUnique("name")).getAncestor(DeclarationMember.class));
        }
    };

    CompositeDepthTransformer() {
    }

    public static void transform(ASTParser t, TranslationUnit tree, Root root) {
        if (root.processMatches(t, uniformFloatCenterDepthSmooth, match -> {
            TypeAndInitDeclaration declaration = (TypeAndInitDeclaration)((DeclarationExternalDeclaration)match).getDeclaration();
            ASTNode memberToDelete = null;
            for (DeclarationMember member : declaration.getMembers()) {
                if (!member.getName().getName().equals("centerDepthSmooth")) continue;
                memberToDelete = member;
                break;
            }
            if (memberToDelete != null) {
                if (declaration.getMembers().size() == 1) {
                    match.detachAndDelete();
                } else {
                    memberToDelete.detachAndDelete();
                }
            }
        })) {
            tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform sampler2D iris_centerDepthSmooth;");
            root.replaceReferenceExpressions(t, "centerDepthSmooth", "texture(iris_centerDepthSmooth, vec2(0.5)).r");
        }
    }
}

