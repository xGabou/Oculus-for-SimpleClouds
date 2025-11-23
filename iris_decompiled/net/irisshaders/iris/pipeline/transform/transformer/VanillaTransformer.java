/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.pipeline.transform.transformer;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import net.irisshaders.iris.gl.blending.AlphaTests;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.pipeline.transform.parameter.VanillaParameters;
import net.irisshaders.iris.pipeline.transform.transformer.CommonTransformer;
import net.irisshaders.iris.pipeline.transform.transformer.EntityPatcher;

public class VanillaTransformer {
    public static void transform(ASTParser t, TranslationUnit tree, Root root, VanillaParameters parameters) {
        if (parameters.inputs.hasOverlay()) {
            EntityPatcher.patchOverlayColor(t, tree, root, parameters);
            EntityPatcher.patchEntityId(t, tree, root, parameters);
        } else if (parameters.inputs.isText()) {
            EntityPatcher.patchEntityId(t, tree, root, parameters);
        }
        CommonTransformer.transform(t, tree, root, parameters, false);
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            root.rename("gl_MultiTexCoord2", "gl_MultiTexCoord1");
            if (parameters.inputs.hasTex()) {
                root.replaceReferenceExpressions(t, "gl_MultiTexCoord0", "vec4(iris_UV0, 0.0, 1.0)");
                tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in vec2 iris_UV0;");
            } else {
                root.replaceReferenceExpressions(t, "gl_MultiTexCoord0", "vec4(0.5, 0.5, 0.0, 1.0)");
            }
            if (parameters.inputs.hasLight()) {
                root.replaceReferenceExpressions(t, "gl_MultiTexCoord1", "vec4(iris_UV2, 0.0, 1.0)");
                tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in ivec2 iris_UV2;");
            } else {
                root.replaceReferenceExpressions(t, "gl_MultiTexCoord1", "vec4(240.0, 240.0, 0.0, 1.0)");
            }
            CommonTransformer.patchMultiTexCoord3(t, tree, root, parameters);
            CommonTransformer.replaceGlMultiTexCoordBounded(t, root, 4, 7);
        }
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform vec4 iris_ColorModulator;");
        if (parameters.inputs.hasColor() && parameters.type == PatchShaderType.VERTEX) {
            if (parameters.alpha == AlphaTests.VERTEX_ALPHA) {
                root.replaceReferenceExpressions(t, "gl_Color", "vec4((iris_Color * iris_ColorModulator).rgb, iris_ColorModulator.a)");
            } else {
                root.replaceReferenceExpressions(t, "gl_Color", "(iris_Color * iris_ColorModulator)");
            }
            if (parameters.type.glShaderType == ShaderType.VERTEX) {
                tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in vec4 iris_Color;");
            }
        } else if (parameters.inputs.isGlint()) {
            tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform float iris_GlintAlpha;");
            root.replaceReferenceExpressions(t, "gl_Color", "vec4(iris_ColorModulator.rgb, iris_ColorModulator.a * iris_GlintAlpha)");
        } else {
            root.rename("gl_Color", "iris_ColorModulator");
        }
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            if (parameters.inputs.hasNormal()) {
                if (!parameters.inputs.isNewLines()) {
                    root.rename("gl_Normal", "iris_Normal");
                } else {
                    root.replaceReferenceExpressions(t, "gl_Normal", "vec3(0.0, 0.0, 1.0)");
                }
                tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in vec3 iris_Normal;");
            } else {
                root.replaceReferenceExpressions(t, "gl_Normal", "vec3(0.0, 0.0, 1.0)");
            }
        }
        tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_LightmapTextureMatrix;", "uniform mat4 iris_TextureMat;", "uniform mat4 iris_ModelViewMat;");
        root.replaceExpressionMatches(t, CommonTransformer.glTextureMatrix0, "iris_TextureMat");
        root.replaceExpressionMatches(t, CommonTransformer.glTextureMatrix1, "iris_LightmapTextureMatrix");
        root.replaceReferenceExpressions(t, "gl_NormalMatrix", "iris_NormalMat");
        root.replaceReferenceExpressions(t, "gl_ModelViewMatrixInverse", "iris_ModelViewMatInverse");
        root.replaceReferenceExpressions(t, "gl_ProjectionMatrixInverse", "iris_ProjMatInverse");
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat3 iris_NormalMat;");
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ProjMatInverse;");
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ModelViewMatInverse;");
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "in vec3 iris_Position;");
            if (root.identifierIndex.has("ftransform")) {
                tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_FUNCTIONS, "vec4 ftransform() { return gl_ModelViewProjectionMatrix * gl_Vertex; }");
            }
            if (parameters.inputs.isNewLines()) {
                root.replaceReferenceExpressions(t, "gl_Vertex", "vec4(iris_Position + iris_vertex_offset, 1.0)");
                root.rename("main", "irisMain");
                tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "vec3 iris_vertex_offset = vec3(0.0);");
                tree.parseAndInjectNodes(t, ASTInjectionPoint.END, "uniform vec2 iris_ScreenSize;", "uniform float iris_LineWidth;", "void iris_widen_lines(vec4 linePosStart, vec4 linePosEnd) {vec3 ndc1 = linePosStart.xyz / linePosStart.w;vec3 ndc2 = linePosEnd.xyz / linePosEnd.w;vec2 lineScreenDirection = normalize((ndc2.xy - ndc1.xy) * iris_ScreenSize);vec2 lineOffset = vec2(-lineScreenDirection.y, lineScreenDirection.x) * iris_LineWidth / iris_ScreenSize;if (lineOffset.x < 0.0) {    lineOffset *= -1.0;}if (gl_VertexID % 2 == 0) {    gl_Position = vec4((ndc1 + vec3(lineOffset, 0.0)) * linePosStart.w, linePosStart.w);} else {    gl_Position = vec4((ndc1 - vec3(lineOffset, 0.0)) * linePosStart.w, linePosStart.w);}}", "void main() {iris_vertex_offset = iris_Normal;irisMain();vec4 linePosEnd = gl_Position;gl_Position = vec4(0.0);iris_vertex_offset = vec3(0.0);irisMain();vec4 linePosStart = gl_Position;iris_widen_lines(linePosStart, linePosEnd);}");
            } else {
                root.replaceReferenceExpressions(t, "gl_Vertex", "vec4(iris_Position, 1.0)");
            }
        }
        root.replaceReferenceExpressions(t, "gl_ModelViewProjectionMatrix", "(gl_ProjectionMatrix * gl_ModelViewMatrix)");
        if (parameters.hasChunkOffset) {
            boolean doInjection = root.replaceReferenceExpressionsReport(t, "gl_ModelViewMatrix", "(iris_ModelViewMat * _iris_internal_translate(iris_ChunkOffset))");
            if (doInjection) {
                tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_FUNCTIONS, "uniform vec3 iris_ChunkOffset;", "mat4 _iris_internal_translate(vec3 offset) {return mat4(1.0, 0.0, 0.0, 0.0,0.0, 1.0, 0.0, 0.0,0.0, 0.0, 1.0, 0.0,offset.x, offset.y, offset.z, 1.0); }");
            }
        } else if (parameters.inputs.isNewLines()) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "const float iris_VIEW_SHRINK = 1.0 - (1.0 / 256.0);", "const mat4 iris_VIEW_SCALE = mat4(iris_VIEW_SHRINK, 0.0, 0.0, 0.0,0.0, iris_VIEW_SHRINK, 0.0, 0.0,0.0, 0.0, iris_VIEW_SHRINK, 0.0,0.0, 0.0, 0.0, 1.0);");
            root.replaceReferenceExpressions(t, "gl_ModelViewMatrix", "(iris_VIEW_SCALE * iris_ModelViewMat)");
        } else {
            root.rename("gl_ModelViewMatrix", "iris_ModelViewMat");
        }
        root.rename("gl_ProjectionMatrix", "iris_ProjMat");
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ProjMat;");
    }
}

