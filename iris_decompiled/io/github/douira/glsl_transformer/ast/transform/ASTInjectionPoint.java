/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.FunctionDefinition;
import io.github.douira.glsl_transformer.ast.node.external_declaration.LayoutDefaults;
import io.github.douira.glsl_transformer.util.ExcludeFromJacocoGeneratedReport;
import java.util.ArrayList;
import java.util.function.Predicate;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum ASTInjectionPoint {
    BEFORE_ALL{

        @Override
        public int getInjectionIndex(TranslationUnit translationUnit) {
            return 0;
        }
    }
    ,
    BEFORE_DECLARATIONS{

        @Override
        public int getInjectionIndex(TranslationUnit translationUnit) {
            return ASTInjectionPoint.findLastIndexWith(translationUnit, node -> node instanceof FunctionDefinition || node instanceof DeclarationExternalDeclaration || node instanceof LayoutDefaults);
        }
    }
    ,
    BEFORE_FUNCTIONS{

        @Override
        public int getInjectionIndex(TranslationUnit translationUnit) {
            return ASTInjectionPoint.findLastIndexWith(translationUnit, node -> node instanceof FunctionDefinition);
        }
    }
    ,
    END{

        @Override
        public int getInjectionIndex(TranslationUnit translationUnit) {
            return ((ArrayList)translationUnit.getChildren()).size();
        }
    };


    public abstract int getInjectionIndex(TranslationUnit var1);

    private static int findLastIndexWith(TranslationUnit translationUnit, Predicate<ExternalDeclaration> stopPredicate) {
        int i;
        int size = ((ArrayList)translationUnit.getChildren()).size();
        for (i = 0; i < size; ++i) {
            if (!stopPredicate.test((ExternalDeclaration)((ArrayList)translationUnit.getChildren()).get(i))) continue;
            return i;
        }
        return i;
    }

    @ExcludeFromJacocoGeneratedReport
    protected boolean checkChildRelevant(Class<?> childClass) {
        throw new AssertionError((Object)"A non-special injection point doesn't have a child relevance implementation!");
    }
}

