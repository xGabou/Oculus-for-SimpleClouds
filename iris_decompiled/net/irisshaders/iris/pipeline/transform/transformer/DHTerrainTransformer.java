/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.pipeline.transform.transformer;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.util.Type;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;
import net.irisshaders.iris.pipeline.transform.transformer.CommonTransformer;

public class DHTerrainTransformer {
    public static void transform(ASTParser t, TranslationUnit tree, Root root, Parameters parameters) {
        CommonTransformer.transform(t, tree, root, parameters, false);
        root.replaceExpressionMatches(t, CommonTransformer.glTextureMatrix0, "mat4(1.0)");
        root.replaceExpressionMatches(t, CommonTransformer.glTextureMatrix1, "mat4(1.0)");
        root.rename("gl_ProjectionMatrix", "iris_ProjectionMatrix");
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            root.rename("gl_MultiTexCoord2", "gl_MultiTexCoord1");
            root.replaceReferenceExpressions(t, "gl_MultiTexCoord0", "vec4(0.0, 0.0, 0.0, 1.0)");
            root.replaceReferenceExpressions(t, "gl_MultiTexCoord1", "vec4(_vert_tex_light_coord, 0.0, 1.0)");
            CommonTransformer.replaceGlMultiTexCoordBounded(t, root, 4, 7);
        }
        root.rename("gl_Color", "_vert_color");
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            root.replaceReferenceExpressions(t, "gl_Normal", "_vert_normal");
        }
        root.replaceReferenceExpressions(t, "gl_NormalMatrix", "iris_NormalMatrix");
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat3 iris_NormalMatrix;");
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ModelViewMatrixInverse;");
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ProjectionMatrixInverse;");
        Iris.logger.warn("Type is " + parameters.type);
        root.rename("gl_ModelViewMatrix", "iris_ModelViewMatrix");
        root.rename("gl_ModelViewMatrixInverse", "iris_ModelViewMatrixInverse");
        root.rename("gl_ProjectionMatrixInverse", "iris_ProjectionMatrixInverse");
        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            if (root.identifierIndex.has("ftransform")) {
                tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_FUNCTIONS, "vec4 ftransform() { return gl_ModelViewProjectionMatrix * gl_Vertex; }");
            }
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ProjectionMatrix;", "uniform mat4 iris_ModelViewMatrix;", "vec4 getVertexPosition() { return vec4(modelOffset + _vert_position, 1.0); }");
            root.replaceReferenceExpressions(t, "gl_Vertex", "getVertexPosition()");
            DHTerrainTransformer.injectVertInit(t, tree, root, parameters);
        } else {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ModelViewMatrix;", "uniform mat4 iris_ProjectionMatrix;");
        }
        root.replaceReferenceExpressions(t, "gl_ModelViewProjectionMatrix", "(iris_ProjectionMatrix * iris_ModelViewMatrix)");
        CommonTransformer.applyIntelHd4000Workaround(root);
    }

    public static void injectVertInit(ASTParser t, TranslationUnit tree, Root root, Parameters parameters) {
        tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_FUNCTIONS, "vec3 _vert_position;", "vec2 _vert_tex_light_coord;", "int dhMaterialId;", "vec4 _vert_color;", "vec3 _vert_normal;", "uniform float mircoOffset;", "uniform vec3 modelOffset;", "const vec3 irisNormals[6] = vec3[](vec3(0,-1,0),vec3(0,1,0),vec3(0,0,-1),vec3(0,0,1),vec3(-1,0,0),vec3(1,0,0));", "void _vert_init() {    uint meta = vPosition.a;\nuint mirco = (meta & 0xFF00u) >> 8u; // mirco offset which is a xyz 2bit value\n    // 0b00 = no offset\n    // 0b01 = positive offset\n    // 0b11 = negative offset\n    // format is: 0b00zzyyxx\n    float mx = (mirco & 1u)!=0u ? mircoOffset : 0.0;\n    mx = (mirco & 2u)!=0u ? -mx : mx;\n    float my = (mirco & 4u)!=0u ? mircoOffset : 0.0;\n    my = (mirco & 8u)!=0u ? -my : my;\n    float mz = (mirco & 16u)!=0u ? mircoOffset : 0.0;\n    mz = (mirco & 32u)!=0u ? -mz : mz;\n        uint lights = meta & 0xFFu;\n_vert_position = (vPosition.xyz + vec3(mx, 0, mz));_vert_normal = irisNormals[irisExtra.y];dhMaterialId = int(irisExtra.x);_vert_tex_light_coord = vec2((float(lights/16u)+0.5) / 16.0, (mod(float(lights), 16.0)+0.5) / 16.0);_vert_color = iris_color; }");
        CommonTransformer.addIfNotExists(root, t, tree, "iris_color", Type.F32VEC4, StorageQualifier.StorageType.IN);
        CommonTransformer.addIfNotExists(root, t, tree, "vPosition", Type.U32VEC4, StorageQualifier.StorageType.IN);
        CommonTransformer.addIfNotExists(root, t, tree, "irisExtra", Type.U32VEC4, StorageQualifier.StorageType.IN);
        tree.prependMainFunctionBody(t, "_vert_init();");
    }
}

