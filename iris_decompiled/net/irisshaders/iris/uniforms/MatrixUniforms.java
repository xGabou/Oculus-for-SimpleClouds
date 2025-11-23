/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.irisshaders.iris.uniforms;

import java.util.function.Supplier;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.shadows.ShadowMatrices;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class MatrixUniforms {
    private MatrixUniforms() {
    }

    public static void addMatrixUniforms(UniformHolder uniforms, PackDirectives directives) {
        MatrixUniforms.addMatrix(uniforms, "ModelView", CapturedRenderingState.INSTANCE::getGbufferModelView);
        MatrixUniforms.addMatrix(uniforms, "Projection", CapturedRenderingState.INSTANCE::getGbufferProjection);
        MatrixUniforms.addDHMatrix(uniforms, "Projection", DHCompat::getProjection);
        MatrixUniforms.addShadowMatrix(uniforms, "ModelView", () -> new Matrix4f((Matrix4fc)ShadowRenderer.createShadowModelView(directives.getSunPathRotation(), directives.getShadowDirectives().getIntervalSize()).m_85850_().m_252922_()));
        MatrixUniforms.addShadowMatrix(uniforms, "Projection", () -> ShadowMatrices.createOrthoMatrix(directives.getShadowDirectives().getDistance(), directives.getShadowDirectives().getNearPlane() < 0.0f ? (float)(-DHCompat.getRenderDistance()) : directives.getShadowDirectives().getNearPlane(), directives.getShadowDirectives().getFarPlane() < 0.0f ? (float)DHCompat.getRenderDistance() : directives.getShadowDirectives().getFarPlane()));
    }

    private static void addMatrix(UniformHolder uniforms, String name, Supplier<Matrix4f> supplier) {
        uniforms.uniformMatrix(UniformUpdateFrequency.PER_FRAME, "gbuffer" + name, supplier).uniformMatrix(UniformUpdateFrequency.PER_FRAME, "gbuffer" + name + "Inverse", new Inverted(supplier)).uniformMatrix(UniformUpdateFrequency.PER_FRAME, "gbufferPrevious" + name, new Previous(supplier));
    }

    private static void addDHMatrix(UniformHolder uniforms, String name, Supplier<Matrix4f> supplier) {
        uniforms.uniformMatrix(UniformUpdateFrequency.PER_FRAME, "dh" + name, supplier).uniformMatrix(UniformUpdateFrequency.PER_FRAME, "dh" + name + "Inverse", new Inverted(supplier)).uniformMatrix(UniformUpdateFrequency.PER_FRAME, "dhPrevious" + name, new Previous(supplier));
    }

    private static void addShadowMatrix(UniformHolder uniforms, String name, Supplier<Matrix4f> supplier) {
        uniforms.uniformMatrix(UniformUpdateFrequency.PER_FRAME, "shadow" + name, supplier).uniformMatrix(UniformUpdateFrequency.PER_FRAME, "shadow" + name + "Inverse", new Inverted(supplier));
    }

    private static class Inverted
    implements Supplier<Matrix4f> {
        private final Supplier<Matrix4f> parent;

        Inverted(Supplier<Matrix4f> parent) {
            this.parent = parent;
        }

        @Override
        public Matrix4f get() {
            Matrix4f copy = new Matrix4f((Matrix4fc)this.parent.get());
            copy.invert();
            return copy;
        }
    }

    private static class Previous
    implements Supplier<Matrix4f> {
        private final Supplier<Matrix4f> parent;
        private Matrix4f previous;

        Previous(Supplier<Matrix4f> parent) {
            this.parent = parent;
            this.previous = new Matrix4f();
        }

        @Override
        public Matrix4f get() {
            Matrix4f copy = new Matrix4f((Matrix4fc)this.parent.get());
            Matrix4f previous = new Matrix4f((Matrix4fc)this.previous);
            this.previous = copy;
            return previous;
        }
    }
}

