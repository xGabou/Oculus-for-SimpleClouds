/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.uniforms;

import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformType;

public class ExternallyManagedUniforms {
    private ExternallyManagedUniforms() {
    }

    public static void addExternallyManagedUniforms(UniformHolder uniformHolder) {
        ExternallyManagedUniforms.addMat4(uniformHolder, "iris_ModelViewMatrix");
        ExternallyManagedUniforms.addMat4(uniformHolder, "u_ModelViewProjectionMatrix");
        ExternallyManagedUniforms.addMat3(uniformHolder, "iris_NormalMatrix");
        ExternallyManagedUniforms.addFloat(uniformHolder, "darknessFactor");
        ExternallyManagedUniforms.addFloat(uniformHolder, "darknessLightFactor");
    }

    public static void addExternallyManagedUniforms116(UniformHolder uniformHolder) {
        ExternallyManagedUniforms.addExternallyManagedUniforms(uniformHolder);
        uniformHolder.externallyManagedUniform("u_ModelScale", UniformType.VEC3);
        uniformHolder.externallyManagedUniform("u_TextureScale", UniformType.VEC2);
    }

    public static void addExternallyManagedUniforms117(UniformHolder uniformHolder) {
        ExternallyManagedUniforms.addExternallyManagedUniforms(uniformHolder);
        ExternallyManagedUniforms.addFloat(uniformHolder, "iris_FogStart");
        ExternallyManagedUniforms.addFloat(uniformHolder, "iris_FogEnd");
        ExternallyManagedUniforms.addVec4(uniformHolder, "iris_FogColor");
        ExternallyManagedUniforms.addMat4(uniformHolder, "iris_ProjectionMatrix");
        ExternallyManagedUniforms.addMat4(uniformHolder, "iris_ModelViewMatrix");
        ExternallyManagedUniforms.addMat3(uniformHolder, "iris_NormalMatrix");
        ExternallyManagedUniforms.addFloat(uniformHolder, "iris_TextureScale");
        ExternallyManagedUniforms.addFloat(uniformHolder, "iris_GlintAlpha");
        ExternallyManagedUniforms.addFloat(uniformHolder, "iris_ModelScale");
        ExternallyManagedUniforms.addFloat(uniformHolder, "iris_ModelOffset");
        ExternallyManagedUniforms.addVec3(uniformHolder, "iris_CameraTranslation");
        ExternallyManagedUniforms.addVec3(uniformHolder, "u_RegionOffset");
        uniformHolder.externallyManagedUniform("iris_TextureMat", UniformType.MAT4);
        uniformHolder.externallyManagedUniform("iris_ModelViewMat", UniformType.MAT4);
        uniformHolder.externallyManagedUniform("iris_ProjMat", UniformType.MAT4);
        uniformHolder.externallyManagedUniform("iris_ChunkOffset", UniformType.VEC3);
        uniformHolder.externallyManagedUniform("iris_ColorModulator", UniformType.VEC4);
        uniformHolder.externallyManagedUniform("iris_NormalMat", UniformType.MAT3);
        uniformHolder.externallyManagedUniform("iris_FogStart", UniformType.FLOAT);
        uniformHolder.externallyManagedUniform("iris_FogEnd", UniformType.FLOAT);
        uniformHolder.externallyManagedUniform("iris_FogDensity", UniformType.FLOAT);
        uniformHolder.externallyManagedUniform("iris_LineWidth", UniformType.FLOAT);
        uniformHolder.externallyManagedUniform("iris_ScreenSize", UniformType.VEC2);
        uniformHolder.externallyManagedUniform("iris_FogColor", UniformType.VEC4);
    }

    private static void addMat3(UniformHolder uniformHolder, String name) {
        uniformHolder.externallyManagedUniform(name, UniformType.MAT3);
    }

    private static void addMat4(UniformHolder uniformHolder, String name) {
        uniformHolder.externallyManagedUniform(name, UniformType.MAT4);
    }

    private static void addVec3(UniformHolder uniformHolder, String name) {
        uniformHolder.externallyManagedUniform(name, UniformType.VEC3);
    }

    private static void addVec4(UniformHolder uniformHolder, String name) {
        uniformHolder.externallyManagedUniform(name, UniformType.VEC4);
    }

    private static void addFloat(UniformHolder uniformHolder, String name) {
        uniformHolder.externallyManagedUniform(name, UniformType.FLOAT);
    }
}

