/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.pipeline.transform.transformer;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.declaration.DeclarationMember;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.LiteralExpression;
import io.github.douira.glsl_transformer.ast.node.expression.ReferenceExpression;
import io.github.douira.glsl_transformer.ast.node.expression.binary.ArrayAccessExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.FunctionCallExpression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinFixedTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinNumericTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.match.AutoHintedMatcher;
import io.github.douira.glsl_transformer.ast.query.match.Matcher;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.ast.transform.Template;
import io.github.douira.glsl_transformer.parser.ParseShape;
import io.github.douira.glsl_transformer.util.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;

public class CommonTransformer {
    public static final AutoHintedMatcher<Expression> glTextureMatrix0 = new AutoHintedMatcher<Expression>("gl_TextureMatrix[0]", ParseShape.EXPRESSION);
    public static final AutoHintedMatcher<Expression> glTextureMatrix1 = new AutoHintedMatcher<Expression>("gl_TextureMatrix[1]", ParseShape.EXPRESSION);
    public static final AutoHintedMatcher<Expression> glTextureMatrix2 = new AutoHintedMatcher<Expression>("gl_TextureMatrix[2]", ParseShape.EXPRESSION);
    public static final Matcher<ExternalDeclaration> sampler = new Matcher<ExternalDeclaration>("uniform Type name;", ParseShape.EXTERNAL_DECLARATION){
        {
            this.markClassedPredicateWildcard("type", ((Identifier)((ExternalDeclaration)this.pattern).getRoot().identifierIndex.getUnique("Type")).getAncestor(TypeSpecifier.class), BuiltinFixedTypeSpecifier.class, specifier -> specifier.type.kind == BuiltinFixedTypeSpecifier.BuiltinType.TypeKind.SAMPLER);
            this.markClassWildcard("name*", ((Identifier)((ExternalDeclaration)this.pattern).getRoot().identifierIndex.getUnique("name")).getAncestor(DeclarationMember.class));
        }
    };
    private static final AutoHintedMatcher<Expression> glFragDataI = new AutoHintedMatcher<Expression>("gl_FragData[index]", ParseShape.EXPRESSION){
        {
            this.markClassedPredicateWildcard("index", ((Identifier)((Expression)this.pattern).getRoot().identifierIndex.getUnique("index")).getAncestor(ReferenceExpression.class), LiteralExpression.class, literalExpression -> literalExpression.isInteger() && literalExpression.getInteger() >= 0L);
        }
    };
    private static final Template<ExternalDeclaration> fragDataDeclaration = Template.withExternalDeclaration("layout (location = __index) out vec4 __name;");
    private static final List<Expression> replaceExpressions = new ArrayList<Expression>();
    private static final List<Long> replaceIndexes = new ArrayList<Long>();
    private static final Template<ExternalDeclaration> inputDeclarationTemplate = Template.withExternalDeclaration("uniform int __name;");
    private static final Template<ExternalDeclaration> inputDeclarationTemplateLayout = Template.withExternalDeclaration("layout (location = __index) uniform int __name;");

    static void renameFunctionCall(Root root, String oldName, String newName) {
        root.process(root.identifierIndex.getStream(oldName).filter(id -> id.getParent() instanceof FunctionCallExpression), id -> id.setName(newName));
    }

    static void renameAndWrapShadow(ASTParser t, Root root, String oldName, String innerName) {
        root.process(root.identifierIndex.getStream(oldName).filter(id -> id.getParent() instanceof FunctionCallExpression), id -> {
            FunctionCallExpression functionCall = (FunctionCallExpression)id.getParent();
            functionCall.getFunctionName().setName(innerName);
            FunctionCallExpression wrapper = (FunctionCallExpression)t.parseExpression(root, "vec4()");
            functionCall.replaceBy(wrapper);
            wrapper.getParameters().add(functionCall);
        });
    }

    public static void patchMultiTexCoord3(ASTParser t, TranslationUnit tree, Root root, Parameters parameters) {
        if (parameters.type.glShaderType == ShaderType.VERTEX && root.identifierIndex.has("gl_MultiTexCoord3") && !root.identifierIndex.has("mc_midTexCoord")) {
            root.rename("gl_MultiTexCoord3", "mc_midTexCoord");
            tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "attribute vec4 mc_midTexCoord;");
        }
    }

    public static void upgradeStorageQualifiers(ASTParser t, TranslationUnit tree, Root root, Parameters parameters) {
        for (StorageQualifier storageQualifier : root.nodeIndex.get(StorageQualifier.class)) {
            if (storageQualifier.storageType == StorageQualifier.StorageType.ATTRIBUTE) {
                storageQualifier.storageType = StorageQualifier.StorageType.IN;
                continue;
            }
            if (storageQualifier.storageType != StorageQualifier.StorageType.VARYING) continue;
            storageQualifier.storageType = parameters.type.glShaderType == ShaderType.VERTEX ? StorageQualifier.StorageType.OUT : StorageQualifier.StorageType.IN;
        }
    }

    public static void transform(ASTParser t, TranslationUnit tree, Root root, Parameters parameters, boolean core) {
        root.rename("gl_FogFragCoord", "iris_FogFragCoord");
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "out float iris_FogFragCoord;");
            tree.prependMainFunctionBody(t, "iris_FogFragCoord = 0.0f;");
        } else if (parameters.type.glShaderType == ShaderType.FRAGMENT) {
            tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in float iris_FogFragCoord;");
        }
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "vec4 iris_FrontColor;");
            root.rename("gl_FrontColor", "iris_FrontColor");
        }
        if (parameters.type.glShaderType == ShaderType.FRAGMENT) {
            if (root.identifierIndex.has("gl_FragColor")) {
                Iris.logger.warn("[Patcher] gl_FragColor is not supported yet, please use gl_FragData! Assuming that the shaderpack author intended to use gl_FragData[0]...");
                root.replaceReferenceExpressions(t, "gl_FragColor", "gl_FragData[0]");
            }
            if (root.identifierIndex.has("gl_TexCoord")) {
                root.rename("gl_TexCoord", "irs_texCoords");
                tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in vec4 irs_texCoords[3];");
            }
            if (root.identifierIndex.has("gl_Color")) {
                root.rename("gl_Color", "irs_Color");
                tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in vec4 irs_Color;");
            }
            replaceExpressions.clear();
            replaceIndexes.clear();
            HashSet<Long> replaceIndexesSet = new HashSet<Long>();
            for (Identifier id2 : root.identifierIndex.get("gl_FragData")) {
                ArrayAccessExpression accessExpression = id2.getAncestor(ArrayAccessExpression.class);
                if (accessExpression == null || !glFragDataI.matchesExtract(accessExpression)) continue;
                replaceExpressions.add(accessExpression);
                long index = glFragDataI.getNodeMatch("index", LiteralExpression.class).getInteger();
                replaceIndexes.add(index);
                replaceIndexesSet.add(index);
            }
            for (int i = 0; i < replaceExpressions.size(); ++i) {
                replaceExpressions.get(i).replaceByAndDelete(new ReferenceExpression(new Identifier("iris_FragData" + replaceIndexes.get(i))));
            }
            Iterator i = replaceIndexesSet.iterator();
            while (i.hasNext()) {
                long index = (Long)i.next();
                tree.injectNode(ASTInjectionPoint.BEFORE_DECLARATIONS, fragDataDeclaration.getInstanceFor(root, new LiteralExpression(Type.INT32, index), new Identifier("iris_FragData" + index)));
            }
            replaceExpressions.clear();
            replaceIndexes.clear();
            if (parameters.getAlphaTest() != AlphaTest.ALWAYS && !core && replaceIndexesSet.contains(0L)) {
                tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform float iris_currentAlphaTest;");
                tree.appendMainFunctionBody(t, parameters.getAlphaTest().toExpression("iris_FragData0.a", "iris_currentAlphaTest", "\t"));
            }
        }
        if (parameters.type.glShaderType == ShaderType.VERTEX || parameters.type.glShaderType == ShaderType.FRAGMENT) {
            CommonTransformer.upgradeStorageQualifiers(t, tree, root, parameters);
        }
        RenameTargetResult gcolorResult = CommonTransformer.getGtextureRenameTargets("gcolor", root);
        RenameTargetResult textureResult = CommonTransformer.getGtextureRenameTargets("texture", root);
        DeclarationMember samplerDeclarationMember = null;
        Stream<Identifier> targets = Stream.empty();
        if (gcolorResult != null) {
            samplerDeclarationMember = gcolorResult.samplerDeclarationMember;
            targets = Stream.concat(targets, gcolorResult.targets);
        }
        if (textureResult != null) {
            if (samplerDeclarationMember == null) {
                samplerDeclarationMember = textureResult.samplerDeclarationMember;
            } else {
                DeclarationMember secondDeclarationMember = textureResult.samplerDeclarationMember;
                if (((TypeAndInitDeclaration)secondDeclarationMember.getParent()).getMembers().size() == 1) {
                    textureResult.samplerDeclaration.detachAndDelete();
                } else {
                    secondDeclarationMember.detachAndDelete();
                }
            }
            targets = Stream.concat(targets, textureResult.targets);
        }
        if (samplerDeclarationMember != null) {
            samplerDeclarationMember.getName().setName("gtexture");
        }
        root.process(targets.filter(id -> !(id.getParent() instanceof FunctionCallExpression)), id -> id.setName("gtexture"));
        root.rename("gl_Fog", "iris_Fog");
        tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform float iris_FogDensity;", "uniform float iris_FogStart;", "uniform float iris_FogEnd;", "uniform vec4 iris_FogColor;", "struct iris_FogParameters {vec4 color;float density;float start;float end;float scale;};", "iris_FogParameters iris_Fog = iris_FogParameters(iris_FogColor, iris_FogDensity, iris_FogStart, iris_FogEnd, 1.0 / (iris_FogEnd - iris_FogStart));");
        CommonTransformer.renameFunctionCall(root, "texture2D", "texture");
        CommonTransformer.renameFunctionCall(root, "texture3D", "texture");
        CommonTransformer.renameFunctionCall(root, "texture2DLod", "textureLod");
        CommonTransformer.renameFunctionCall(root, "texture3DLod", "textureLod");
        CommonTransformer.renameFunctionCall(root, "texture2DGrad", "textureGrad");
        CommonTransformer.renameFunctionCall(root, "texture2DGradARB", "textureGrad");
        CommonTransformer.renameFunctionCall(root, "texture3DGrad", "textureGrad");
        CommonTransformer.renameFunctionCall(root, "texelFetch2D", "texelFetch");
        CommonTransformer.renameFunctionCall(root, "texelFetch3D", "texelFetch");
        CommonTransformer.renameFunctionCall(root, "textureSize2D", "textureSize");
        CommonTransformer.renameAndWrapShadow(t, root, "shadow2D", "texture");
        CommonTransformer.renameAndWrapShadow(t, root, "shadow2DLod", "textureLod");
    }

    private static RenameTargetResult getGtextureRenameTargets(String name, Root root) {
        ArrayList<Identifier> gtextureTargets = new ArrayList<Identifier>();
        DeclarationExternalDeclaration samplerDeclaration = null;
        DeclarationMember samplerDeclarationMember = null;
        for (Identifier id : root.identifierIndex.get(name)) {
            DeclarationExternalDeclaration externalDeclaration;
            gtextureTargets.add(id);
            if (samplerDeclaration != null || (externalDeclaration = (DeclarationExternalDeclaration)id.getAncestor(3, 0, DeclarationExternalDeclaration.class::isInstance)) == null) continue;
            if (sampler.matchesExtract(externalDeclaration)) {
                boolean foundNameMatch = false;
                for (DeclarationMember member : sampler.getNodeMatch("name*", DeclarationMember.class).getAncestor(TypeAndInitDeclaration.class).getMembers()) {
                    if (!member.getName().getName().equals(name)) continue;
                    foundNameMatch = true;
                }
                if (!foundNameMatch) {
                    return null;
                }
                samplerDeclaration = externalDeclaration;
                samplerDeclarationMember = id.getAncestor(DeclarationMember.class);
                gtextureTargets.remove(gtextureTargets.size() - 1);
                continue;
            }
            return null;
        }
        if (samplerDeclaration == null) {
            return null;
        }
        return new RenameTargetResult(samplerDeclaration, samplerDeclarationMember, gtextureTargets.stream());
    }

    public static void applyIntelHd4000Workaround(Root root) {
        root.rename("ftransform", "iris_ftransform");
    }

    public static void replaceGlMultiTexCoordBounded(ASTParser t, Root root, int minimum, int maximum) {
        root.replaceReferenceExpressions(t, root.getPrefixIdentifierIndex().prefixQueryFlat("gl_MultiTexCoord").filter(id -> {
            int index = Integer.parseInt(id.getName().substring("gl_MultiTexCoord".length()));
            return index >= minimum && index <= maximum;
        }), "vec4(0.0, 0.0, 0.0, 1.0)");
    }

    public static void addIfNotExists(Root root, ASTParser t, TranslationUnit tree, String name, Type type, StorageQualifier.StorageType storageType) {
        if (root.externalDeclarationIndex.getStream(name).noneMatch(entry -> entry.declaration() instanceof DeclarationExternalDeclaration)) {
            tree.injectNode(ASTInjectionPoint.BEFORE_DECLARATIONS, inputDeclarationTemplate.getInstanceFor(root, new StorageQualifier(storageType), new BuiltinNumericTypeSpecifier(type), new Identifier(name)));
        }
    }

    public static void addIfNotExists(Root root, ASTParser t, TranslationUnit tree, String name, Type type, StorageQualifier.StorageType storageType, int location) {
        if (root.externalDeclarationIndex.getStream(name).noneMatch(entry -> entry.declaration() instanceof DeclarationExternalDeclaration)) {
            tree.injectNode(ASTInjectionPoint.BEFORE_DECLARATIONS, inputDeclarationTemplateLayout.getInstanceFor(root, new LiteralExpression(Type.INT32, location), new StorageQualifier(storageType), new BuiltinNumericTypeSpecifier(type), new Identifier(name)));
        }
    }

    static {
        fragDataDeclaration.markLocalReplacement("__index", ReferenceExpression.class);
        fragDataDeclaration.markIdentifierReplacement("__name");
        inputDeclarationTemplate.markLocalReplacement((ASTNode)((Object)CommonTransformer.inputDeclarationTemplate.getSourceRoot().nodeIndex.getOne(StorageQualifier.class)));
        inputDeclarationTemplate.markLocalReplacement((ASTNode)((Object)CommonTransformer.inputDeclarationTemplate.getSourceRoot().nodeIndex.getOne(BuiltinNumericTypeSpecifier.class)));
        inputDeclarationTemplate.markIdentifierReplacement("__name");
        inputDeclarationTemplateLayout.markLocalReplacement("__index", ReferenceExpression.class);
        inputDeclarationTemplateLayout.markLocalReplacement((ASTNode)((Object)CommonTransformer.inputDeclarationTemplateLayout.getSourceRoot().nodeIndex.getOne(StorageQualifier.class)));
        inputDeclarationTemplateLayout.markLocalReplacement((ASTNode)((Object)CommonTransformer.inputDeclarationTemplateLayout.getSourceRoot().nodeIndex.getOne(BuiltinNumericTypeSpecifier.class)));
        inputDeclarationTemplateLayout.markIdentifierReplacement("__name");
    }

    private record RenameTargetResult(DeclarationExternalDeclaration samplerDeclaration, DeclarationMember samplerDeclarationMember, Stream<Identifier> targets) {
    }
}

