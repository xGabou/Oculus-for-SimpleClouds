/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 */
package net.irisshaders.iris.uniforms;

import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.helpers.JomlConversions;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.client.Minecraft;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class CameraUniforms {
    private static final Minecraft client = Minecraft.m_91087_();

    private CameraUniforms() {
    }

    public static void addCameraUniforms(UniformHolder uniforms, FrameUpdateNotifier notifier) {
        CameraPositionTracker tracker = new CameraPositionTracker(notifier);
        uniforms.uniform1f(UniformUpdateFrequency.ONCE, "near", () -> 0.05).uniform1f(UniformUpdateFrequency.PER_FRAME, "far", CameraUniforms::getRenderDistanceInBlocks).uniform3d(UniformUpdateFrequency.PER_FRAME, "cameraPosition", tracker::getCurrentCameraPosition).uniform1f(UniformUpdateFrequency.PER_FRAME, "eyeAltitude", tracker::getCurrentCameraPositionY).uniform3d(UniformUpdateFrequency.PER_FRAME, "previousCameraPosition", tracker::getPreviousCameraPosition).uniform3i(UniformUpdateFrequency.PER_FRAME, "cameraPositionInt", () -> CameraUniforms.getCameraPositionInt(CameraUniforms.getUnshiftedCameraPosition())).uniform3f(UniformUpdateFrequency.PER_FRAME, "cameraPositionFract", () -> CameraUniforms.getCameraPositionFract(CameraUniforms.getUnshiftedCameraPosition())).uniform3i(UniformUpdateFrequency.PER_FRAME, "previousCameraPositionInt", () -> CameraUniforms.getCameraPositionInt(tracker.getPreviousCameraPositionUnshifted())).uniform3f(UniformUpdateFrequency.PER_FRAME, "previousCameraPositionFract", () -> CameraUniforms.getCameraPositionFract(tracker.getPreviousCameraPositionUnshifted()));
    }

    private static int getRenderDistanceInBlocks() {
        return CameraUniforms.client.f_91066_.m_193772_() * 16;
    }

    public static Vector3d getUnshiftedCameraPosition() {
        return JomlConversions.fromVec3(CameraUniforms.client.f_91063_.m_109153_().m_90583_());
    }

    public static Vector3f getCameraPositionFract(Vector3d originalPos) {
        return new Vector3f((float)(originalPos.x - Math.floor(originalPos.x)), (float)(originalPos.y - Math.floor(originalPos.y)), (float)(originalPos.z - Math.floor(originalPos.z)));
    }

    public static Vector3i getCameraPositionInt(Vector3d originalPos) {
        return new Vector3i((int)Math.floor(originalPos.x), (int)Math.floor(originalPos.y), (int)Math.floor(originalPos.z));
    }

    static class CameraPositionTracker {
        private static final double WALK_RANGE = 30000.0;
        private static final double TP_RANGE = 1000.0;
        private final Vector3d shift = new Vector3d();
        private Vector3d previousCameraPosition = new Vector3d();
        private Vector3d currentCameraPosition = new Vector3d();
        private Vector3d previousCameraPositionUnshifted = new Vector3d();
        private Vector3d currentCameraPositionUnshifted = new Vector3d();

        CameraPositionTracker(FrameUpdateNotifier notifier) {
            notifier.addListener(this::update);
        }

        private static double getShift(double value, double prevValue) {
            if (Math.abs(value) > 30000.0 || Math.abs(value - prevValue) > 1000.0) {
                return -(value - value % 30000.0);
            }
            return 0.0;
        }

        private void update() {
            this.previousCameraPosition = this.currentCameraPosition;
            this.previousCameraPositionUnshifted = this.currentCameraPositionUnshifted;
            this.currentCameraPosition = CameraUniforms.getUnshiftedCameraPosition().add((Vector3dc)this.shift);
            this.currentCameraPositionUnshifted = CameraUniforms.getUnshiftedCameraPosition();
            this.updateShift();
        }

        private void updateShift() {
            double dX = CameraPositionTracker.getShift(this.currentCameraPosition.x, this.previousCameraPosition.x);
            double dZ = CameraPositionTracker.getShift(this.currentCameraPosition.z, this.previousCameraPosition.z);
            if (dX != 0.0 || dZ != 0.0) {
                this.applyShift(dX, dZ);
            }
        }

        private void applyShift(double dX, double dZ) {
            this.shift.x += dX;
            this.currentCameraPosition.x += dX;
            this.previousCameraPosition.x += dX;
            this.shift.z += dZ;
            this.currentCameraPosition.z += dZ;
            this.previousCameraPosition.z += dZ;
        }

        public Vector3d getCurrentCameraPosition() {
            return this.currentCameraPosition;
        }

        public Vector3d getPreviousCameraPosition() {
            return this.previousCameraPosition;
        }

        public Vector3d getPreviousCameraPositionUnshifted() {
            return this.previousCameraPositionUnshifted;
        }

        public double getCurrentCameraPositionY() {
            return this.currentCameraPosition.y;
        }
    }
}

