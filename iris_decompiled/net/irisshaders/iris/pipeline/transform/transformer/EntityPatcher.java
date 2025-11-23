/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.pipeline.transform.transformer;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.match.AutoHintedMatcher;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.parser.ParseShape;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.pipeline.transform.parameter.GeometryInfoParameters;
import net.irisshaders.iris.pipeline.transform.parameter.VanillaParameters;

public class EntityPatcher {
    private static final AutoHintedMatcher<ExternalDeclaration> uniformVec4EntityColor = new AutoHintedMatcher<ExternalDeclaration>("uniform vec4 entityColor;", ParseShape.EXTERNAL_DECLARATION);
    private static final AutoHintedMatcher<ExternalDeclaration> uniformIntEntityId = new AutoHintedMatcher<ExternalDeclaration>("uniform int entityId;", ParseShape.EXTERNAL_DECLARATION);
    private static final AutoHintedMatcher<ExternalDeclaration> uniformIntBlockEntityId = new AutoHintedMatcher<ExternalDeclaration>("uniform int blockEntityId;", ParseShape.EXTERNAL_DECLARATION);
    private static final AutoHintedMatcher<ExternalDeclaration> uniformIntCurrentRenderedItemId = new AutoHintedMatcher<ExternalDeclaration>("uniform int currentRenderedItemId;", ParseShape.EXTERNAL_DECLARATION);

    public static void patchOverlayColor(ASTParser t, TranslationUnit tree, Root root, GeometryInfoParameters parameters) {
        root.processMatches(t, uniformVec4EntityColor, ASTNode::detachAndDelete);
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform sampler2D iris_overlay;", "out vec4 entityColor;", "out vec4 iris_vertexColor;", "in ivec2 iris_UV1;");
            tree.prependMainFunctionBody(t, "vec4 overlayColor = texelFetch(iris_overlay, iris_UV1, 0);", "entityColor = vec4(overlayColor.rgb, 1.0 - overlayColor.a);", "iris_vertexColor = iris_Color;", "entityColor.rgb *= float(entityColor.a != 0.0);");
        } else if (parameters.type.glShaderType == ShaderType.TESSELATION_CONTROL) {
            root.replaceReferenceExpressions(t, "entityColor", "entityColor[gl_InvocationID]");
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "patch out vec4 entityColorTCS;", "in vec4 entityColor[];", "out vec4 iris_vertexColorTCS[];", "in vec4 iris_vertexColor[];");
            tree.prependMainFunctionBody(t, "entityColorTCS = entityColor[gl_InvocationID];", "iris_vertexColorTCS[gl_InvocationID] = iris_vertexColor[gl_InvocationID];");
        } else if (parameters.type.glShaderType == ShaderType.TESSELATION_EVAL) {
            root.replaceReferenceExpressions(t, "entityColor", "entityColorTCS");
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "out vec4 entityColorTES;", "patch in vec4 entityColorTCS;", "out vec4 iris_vertexColorTES;", "in vec4 iris_vertexColorTCS[];");
            tree.prependMainFunctionBody(t, "entityColorTES = entityColorTCS;", "iris_vertexColorTES = iris_vertexColorTCS[0];");
        } else if (parameters.type.glShaderType == ShaderType.GEOMETRY) {
            root.replaceReferenceExpressions(t, "entityColor", "entityColor[0]");
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "out vec4 entityColorGS;", "in vec4 entityColor[];", "out vec4 iris_vertexColorGS;", "in vec4 iris_vertexColor[];");
            tree.prependMainFunctionBody(t, "entityColorGS = entityColor[0];", "iris_vertexColorGS = iris_vertexColor[0];");
            if (parameters.hasTesselation) {
                root.rename("iris_vertexColor", "iris_vertexColorTES");
                root.rename("entityColor", "entityColorTES");
            }
        } else if (parameters.type.glShaderType == ShaderType.FRAGMENT) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in vec4 entityColor;", "in vec4 iris_vertexColor;");
            tree.prependMainFunctionBody(t, "float iris_vertexColorAlpha = iris_vertexColor.a;");
            if (parameters.hasGeometry) {
                root.rename("entityColor", "entityColorGS");
                root.rename("iris_vertexColor", "iris_vertexColorGS");
            } else if (parameters.hasTesselation) {
                root.rename("entityColor", "entityColorTES");
                root.rename("iris_vertexColor", "iris_vertexColorTES");
            }
        }
    }

    public static void patchEntityId(ASTParser t, TranslationUnit tree, Root root, VanillaParameters parameters) {
        root.processMatches(t, uniformIntEntityId, ASTNode::detachAndDelete);
        root.processMatches(t, uniformIntBlockEntityId, ASTNode::detachAndDelete);
        root.processMatches(t, uniformIntCurrentRenderedItemId, ASTNode::detachAndDelete);
        if (parameters.type.glShaderType == ShaderType.GEOMETRY) {
            root.replaceReferenceExpressions(t, "entityId", "iris_entityInfo[0].x");
            root.replaceReferenceExpressions(t, "blockEntityId", "iris_entityInfo[0].y");
            root.replaceReferenceExpressions(t, "currentRenderedItemId", "iris_entityInfo[0].z");
        } else {
            root.replaceReferenceExpressions(t, "entityId", "iris_entityInfo.x");
            root.replaceReferenceExpressions(t, "blockEntityId", "iris_entityInfo.y");
            root.replaceReferenceExpressions(t, "currentRenderedItemId", "iris_entityInfo.z");
        }
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "flat out ivec3 iris_entityInfo;", "in ivec3 iris_Entity;");
            tree.prependMainFunctionBody(t, "iris_entityInfo = iris_Entity;");
        } else if (parameters.type.glShaderType == ShaderType.TESSELATION_CONTROL) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "flat out ivec3 iris_entityInfoTCS[];", "flat in ivec3 iris_entityInfo[];");
            root.replaceReferenceExpressions(t, "iris_entityInfo", "iris_EntityInfo[gl_InvocationID]");
            tree.prependMainFunctionBody(t, "iris_entityInfoTCS[gl_InvocationID] = iris_entityInfo[gl_InvocationID];");
        } else if (parameters.type.glShaderType == ShaderType.TESSELATION_EVAL) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "flat out ivec3 iris_entityInfoTES;", "flat in ivec3 iris_entityInfoTCS[];");
            tree.prependMainFunctionBody(t, "iris_entityInfoTES = iris_entityInfoTCS[0];");
            root.replaceReferenceExpressions(t, "iris_entityInfo", "iris_EntityInfoTCS[0]");
        } else if (parameters.type.glShaderType == ShaderType.GEOMETRY) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "flat out ivec3 iris_entityInfoGS;", "flat in ivec3 iris_entityInfo" + (parameters.hasTesselation ? "TES" : "") + "[];");
            tree.prependMainFunctionBody(t, "iris_entityInfoGS = iris_entityInfo" + (parameters.hasTesselation ? "TES" : "") + "[0];");
        } else if (parameters.type.glShaderType == ShaderType.FRAGMENT) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "flat in ivec3 iris_entityInfo;");
            if (parameters.hasGeometry) {
                root.rename("iris_entityInfo", "iris_EntityInfoGS");
            } else if (parameters.hasTesselation) {
                root.rename("iris_entityInfo", "iris_entityInfoTES");
            }
        }
    }
}

