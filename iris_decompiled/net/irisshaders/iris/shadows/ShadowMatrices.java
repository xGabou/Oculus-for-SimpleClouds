/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.irisshaders.iris.shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ShadowMatrices {
    private static final float NEAR = 0.05f;
    private static final float FAR = 256.0f;

    public static Matrix4f createOrthoMatrix(float halfPlaneLength, float nearPlane, float farPlane) {
        return new Matrix4f(1.0f / halfPlaneLength, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f / halfPlaneLength, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f / (nearPlane - farPlane), 0.0f, 0.0f, 0.0f, -(farPlane + nearPlane) / (farPlane - nearPlane), 1.0f);
    }

    public static Matrix4f createPerspectiveMatrix(float fov) {
        float yScale = (float)(1.0 / Math.tan(Math.toRadians(fov) * 0.5));
        return new Matrix4f(yScale, 0.0f, 0.0f, 0.0f, 0.0f, yScale, 0.0f, 0.0f, 0.0f, 0.0f, -1.0003906f, -1.0f, 0.0f, 0.0f, -0.10001954f, 1.0f);
    }

    public static void createBaselineModelViewMatrix(PoseStack target, float shadowAngle, float sunPathRotation) {
        float skyAngle = shadowAngle < 0.25f ? shadowAngle + 0.75f : shadowAngle - 0.25f;
        target.m_85850_().m_252943_().identity();
        target.m_85850_().m_252922_().identity();
        target.m_85850_().m_252922_().translate(0.0f, 0.0f, -100.0f);
        target.m_252781_(Axis.f_252529_.m_252977_(90.0f));
        target.m_252781_(Axis.f_252403_.m_252977_(skyAngle * -360.0f));
        target.m_252781_(Axis.f_252529_.m_252977_(sunPathRotation));
    }

    public static void snapModelViewToGrid(PoseStack target, float shadowIntervalSize, double cameraX, double cameraY, double cameraZ) {
        if (Math.abs(shadowIntervalSize) == 0.0f) {
            return;
        }
        float offsetX = (float)cameraX % shadowIntervalSize;
        float offsetY = (float)cameraY % shadowIntervalSize;
        float offsetZ = (float)cameraZ % shadowIntervalSize;
        float halfIntervalSize = shadowIntervalSize / 2.0f;
        target.m_85850_().m_252922_().translate(offsetX -= halfIntervalSize, offsetY -= halfIntervalSize, offsetZ -= halfIntervalSize);
    }

    public static void createModelViewMatrix(PoseStack target, float shadowAngle, float shadowIntervalSize, float sunPathRotation, double cameraX, double cameraY, double cameraZ) {
        ShadowMatrices.createBaselineModelViewMatrix(target, shadowAngle, sunPathRotation);
        ShadowMatrices.snapModelViewToGrid(target, shadowIntervalSize, cameraX, cameraY, cameraZ);
    }

    private static final class Tests {
        private Tests() {
        }

        public static void main(String[] args) {
            Matrix4f expected = new Matrix4f(0.03125f, 0.0f, 0.0f, 0.0f, 0.0f, 0.03125f, 0.0f, 0.0f, 0.0f, 0.0f, -0.007814026f, 0.0f, 0.0f, 0.0f, -1.0003906f, 1.0f);
            Tests.test("ortho projection hpl=32", expected, ShadowMatrices.createOrthoMatrix(32.0f, 0.05f, 256.0f));
            Matrix4f expected110 = new Matrix4f(0.009090909f, 0.0f, 0.0f, 0.0f, 0.0f, 0.009090909f, 0.0f, 0.0f, 0.0f, 0.0f, -0.007814026f, 0.0f, 0.0f, 0.0f, -1.0003906f, 1.0f);
            Tests.test("ortho projection hpl=110", expected110, ShadowMatrices.createOrthoMatrix(110.0f, 0.05f, 256.0f));
            Matrix4f expected90Proj = new Matrix4f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0003906f, -1.0f, 0.0f, 0.0f, -0.10001954f, 0.0f);
            Tests.test("perspective projection fov=90", expected90Proj, ShadowMatrices.createPerspectiveMatrix(90.0f));
            Matrix4f expectedModelViewAtDawn = new Matrix4f(0.2154504f, 5.8204815E-8f, 0.9765147f, 0.0f, -0.97651476f, 1.2841845E-8f, 0.21545039f, 0.0f, 0.0f, -0.99999994f, 5.9604645E-8f, 0.0f, 0.3800215f, 1.0264281f, -100.44631f, 1.0f);
            PoseStack modelView = new PoseStack();
            ShadowMatrices.createModelViewMatrix(modelView, 0.03451777f, 2.0f, 0.0f, 0.646045982837677, 82.53274536132812, -514.0264282226562);
            Tests.test("model view at dawn", expectedModelViewAtDawn, modelView.m_85850_().m_252922_());
        }

        private static void test(String name, Matrix4f expected, Matrix4f created) {
            if (expected.equals((Matrix4fc)created, 5.0E-4f)) {
                System.err.println("test " + name + " failed: ");
                System.err.println("    expected: ");
                System.err.print(expected);
                System.err.println("    created: ");
                System.err.print(created.toString());
            } else {
                System.out.println("test " + name + " passed");
            }
        }
    }
}

