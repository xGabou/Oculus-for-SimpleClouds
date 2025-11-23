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

public class DHGenericTransformer {
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
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ProjectionMatrix;", "uniform mat4 iris_ModelViewMatrix;", "vec4 getVertexPosition() { return vec4(_vert_position, 1.0); }");
            root.replaceReferenceExpressions(t, "gl_Vertex", "getVertexPosition()");
            DHGenericTransformer.injectVertInit(t, tree, root, parameters);
        } else {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS, "uniform mat4 iris_ModelViewMatrix;", "uniform mat4 iris_ProjectionMatrix;");
        }
        root.replaceReferenceExpressions(t, "gl_ModelViewProjectionMatrix", "(iris_ProjectionMatrix * iris_ModelViewMatrix)");
        CommonTransformer.applyIntelHd4000Workaround(root);
    }

    public static void injectVertInit(ASTParser t, TranslationUnit tree, Root root, Parameters parameters) {
        tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_FUNCTIONS, "vec3 _vert_position;", "vec2 _vert_tex_light_coord;", "int dhMaterialId;", "vec4 _vert_color;", "vec3 _vert_normal;", "uniform ivec3 uOffsetChunk;", "uniform vec3 uOffsetSubChunk;", "uniform ivec3 uCameraPosChunk;", "uniform vec3 uCameraPosSubChunk;", "uniform int uSkyLight;", "uniform int uBlockLight;", "const vec3 irisNormals[6] = vec3[](vec3(0,0,-1),vec3(0,0,1),vec3(-1,0,0),vec3(1,0,0),vec3(0,-1,0),vec3(0,1,0));", "void _vert_init() {\n\tvec3 trans = (aTranslateChunk + uOffsetChunk - uCameraPosChunk) * 16.0f;\n\ttrans += (aTranslateSubChunk + uOffsetSubChunk - uCameraPosSubChunk);\n\tmat4 transform = mat4(\n         aScale.x, 0.0,      0.0,      0.0,\n         0.0,      aScale.y, 0.0,      0.0,\n         0.0,      0.0,      aScale.z, 0.0,\n         trans.x,  trans.y,  trans.z,  1.0\n     );\n     _vert_position = (transform * vec4(vPosition, 1.0)).xyz;\n\t_vert_normal = irisNormals[int(floor(float(gl_VertexID) / 4))];\n\t\t\t\tfloat blockLight = (float(uBlockLight)+0.5) / 16.0;\n\t\t\t\tfloat skyLight = (float(uSkyLight)+0.5) / 16.0;\n     _vert_tex_light_coord = vec2(blockLight, skyLight);\n     dhMaterialId = aMaterial;\n     _vert_color = iris_color;\n     }\n");
        CommonTransformer.addIfNotExists(root, t, tree, "iris_color", Type.F32VEC4, StorageQualifier.StorageType.IN, 1);
        CommonTransformer.addIfNotExists(root, t, tree, "aScale", Type.F32VEC3, StorageQualifier.StorageType.IN, 2);
        CommonTransformer.addIfNotExists(root, t, tree, "aTranslateChunk", Type.I32VEC3, StorageQualifier.StorageType.IN, 3);
        CommonTransformer.addIfNotExists(root, t, tree, "aTranslateSubChunk", Type.F32VEC3, StorageQualifier.StorageType.IN, 4);
        CommonTransformer.addIfNotExists(root, t, tree, "aMaterial", Type.INT32, StorageQualifier.StorageType.IN, 5);
        CommonTransformer.addIfNotExists(root, t, tree, "vPosition", Type.F32VEC3, StorageQualifier.StorageType.IN, 0);
        tree.prependMainFunctionBody(t, "_vert_init();");
    }
}

