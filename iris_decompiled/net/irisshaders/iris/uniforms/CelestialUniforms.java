/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.uniforms;

import com.mojang.math.Axis;
import java.util.Objects;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector4f;

public final class CelestialUniforms {
    private final float sunPathRotation;

    public CelestialUniforms(float sunPathRotation) {
        this.sunPathRotation = sunPathRotation;
    }

    public static float getSunAngle() {
        float skyAngle = CelestialUniforms.getSkyAngle();
        if (skyAngle < 0.75f) {
            return skyAngle + 0.25f;
        }
        return skyAngle - 0.75f;
    }

    private static float getShadowAngle() {
        float shadowAngle = CelestialUniforms.getSunAngle();
        if (!CelestialUniforms.isDay()) {
            shadowAngle -= 0.5f;
        }
        return shadowAngle;
    }

    private static Vector4f getUpPosition() {
        Vector4f upVector = new Vector4f(0.0f, 100.0f, 0.0f, 0.0f);
        Matrix4f preCelestial = new Matrix4f((Matrix4fc)CapturedRenderingState.INSTANCE.getGbufferModelView());
        preCelestial.rotate((Quaternionfc)Axis.f_252436_.m_252977_(-90.0f));
        upVector = preCelestial.transform(upVector);
        return upVector;
    }

    public static boolean isDay() {
        return (double)CelestialUniforms.getSunAngle() <= 0.5;
    }

    private static ClientLevel getWorld() {
        return Objects.requireNonNull(Minecraft.m_91087_().f_91073_);
    }

    private static float getSkyAngle() {
        return CelestialUniforms.getWorld().m_46942_(CapturedRenderingState.INSTANCE.getTickDelta());
    }

    public void addCelestialUniforms(UniformHolder uniforms) {
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "sunAngle", CelestialUniforms::getSunAngle).uniformTruncated3f(UniformUpdateFrequency.PER_FRAME, "sunPosition", this::getSunPosition).uniformTruncated3f(UniformUpdateFrequency.PER_FRAME, "moonPosition", this::getMoonPosition).uniform1f(UniformUpdateFrequency.PER_FRAME, "shadowAngle", CelestialUniforms::getShadowAngle).uniformTruncated3f(UniformUpdateFrequency.PER_FRAME, "shadowLightPosition", this::getShadowLightPosition).uniformTruncated3f(UniformUpdateFrequency.PER_FRAME, "upPosition", CelestialUniforms::getUpPosition);
    }

    private Vector4f getSunPosition() {
        return this.getCelestialPosition(100.0f);
    }

    private Vector4f getMoonPosition() {
        return this.getCelestialPosition(-100.0f);
    }

    public Vector4f getShadowLightPosition() {
        return CelestialUniforms.isDay() ? this.getSunPosition() : this.getMoonPosition();
    }

    public Vector4f getShadowLightPositionInWorldSpace() {
        return CelestialUniforms.isDay() ? this.getCelestialPositionInWorldSpace(100.0f) : this.getCelestialPositionInWorldSpace(-100.0f);
    }

    private Vector4f getCelestialPositionInWorldSpace(float y) {
        Vector4f position = new Vector4f(0.0f, y, 0.0f, 0.0f);
        Matrix4f celestial = new Matrix4f();
        celestial.identity();
        celestial.rotate((Quaternionfc)Axis.f_252436_.m_252977_(-90.0f));
        celestial.rotate((Quaternionfc)Axis.f_252403_.m_252977_(this.sunPathRotation));
        celestial.rotate((Quaternionfc)Axis.f_252529_.m_252977_(CelestialUniforms.getSkyAngle() * 360.0f));
        celestial.transform(position);
        return position;
    }

    private Vector4f getCelestialPosition(float y) {
        Vector4f position = new Vector4f(0.0f, y, 0.0f, 0.0f);
        Matrix4f celestial = new Matrix4f((Matrix4fc)CapturedRenderingState.INSTANCE.getGbufferModelView());
        celestial.rotate((Quaternionfc)Axis.f_252436_.m_252977_(-90.0f));
        celestial.rotate((Quaternionfc)Axis.f_252403_.m_252977_(this.sunPathRotation));
        celestial.rotate((Quaternionfc)Axis.f_252529_.m_252977_(CelestialUniforms.getSkyAngle() * 360.0f));
        position = celestial.transform(position);
        return position;
    }
}

