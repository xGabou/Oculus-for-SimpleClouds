/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.gl.uniform;

import java.util.OptionalInt;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.BooleanUniform;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.FloatUniform;
import net.irisshaders.iris.gl.uniform.IntUniform;
import net.irisshaders.iris.gl.uniform.MatrixFromFloatArrayUniform;
import net.irisshaders.iris.gl.uniform.MatrixUniform;
import net.irisshaders.iris.gl.uniform.Uniform;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformType;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.gl.uniform.Vector2IntegerJomlUniform;
import net.irisshaders.iris.gl.uniform.Vector2Uniform;
import net.irisshaders.iris.gl.uniform.Vector3IntegerUniform;
import net.irisshaders.iris.gl.uniform.Vector3Uniform;
import net.irisshaders.iris.gl.uniform.Vector4ArrayUniform;
import net.irisshaders.iris.gl.uniform.Vector4Uniform;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public interface LocationalUniformHolder
extends UniformHolder {
    public LocationalUniformHolder addUniform(UniformUpdateFrequency var1, Uniform var2);

    public OptionalInt location(String var1, UniformType var2);

    @Override
    default public LocationalUniformHolder uniform1f(UniformUpdateFrequency updateFrequency, String name, FloatSupplier value) {
        this.location(name, UniformType.FLOAT).ifPresent(id -> this.addUniform(updateFrequency, new FloatUniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform1f(UniformUpdateFrequency updateFrequency, String name, IntSupplier value) {
        this.location(name, UniformType.FLOAT).ifPresent(id -> this.addUniform(updateFrequency, new FloatUniform(id, () -> value.getAsInt())));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform1f(UniformUpdateFrequency updateFrequency, String name, DoubleSupplier value) {
        this.location(name, UniformType.FLOAT).ifPresent(id -> this.addUniform(updateFrequency, new FloatUniform(id, () -> (float)value.getAsDouble())));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform1i(UniformUpdateFrequency updateFrequency, String name, IntSupplier value) {
        this.location(name, UniformType.INT).ifPresent(id -> this.addUniform(updateFrequency, new IntUniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform1b(UniformUpdateFrequency updateFrequency, String name, BooleanSupplier value) {
        this.location(name, UniformType.INT).ifPresent(id -> this.addUniform(updateFrequency, new BooleanUniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform2f(UniformUpdateFrequency updateFrequency, String name, Supplier<Vector2f> value) {
        this.location(name, UniformType.VEC2).ifPresent(id -> this.addUniform(updateFrequency, new Vector2Uniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform2i(UniformUpdateFrequency updateFrequency, String name, Supplier<Vector2i> value) {
        this.location(name, UniformType.VEC2I).ifPresent(id -> this.addUniform(updateFrequency, new Vector2IntegerJomlUniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform3f(UniformUpdateFrequency updateFrequency, String name, Supplier<Vector3f> value) {
        this.location(name, UniformType.VEC3).ifPresent(id -> this.addUniform(updateFrequency, new Vector3Uniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform3i(UniformUpdateFrequency updateFrequency, String name, Supplier<Vector3i> value) {
        this.location(name, UniformType.VEC3I).ifPresent(id -> this.addUniform(updateFrequency, new Vector3IntegerUniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniformTruncated3f(UniformUpdateFrequency updateFrequency, String name, Supplier<Vector4f> value) {
        this.location(name, UniformType.VEC3).ifPresent(id -> this.addUniform(updateFrequency, Vector3Uniform.truncated(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform3d(UniformUpdateFrequency updateFrequency, String name, Supplier<Vector3d> value) {
        this.location(name, UniformType.VEC3).ifPresent(id -> this.addUniform(updateFrequency, Vector3Uniform.converted(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform4f(UniformUpdateFrequency updateFrequency, String name, Supplier<Vector4f> value) {
        this.location(name, UniformType.VEC4).ifPresent(id -> this.addUniform(updateFrequency, new Vector4Uniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniform4fArray(UniformUpdateFrequency updateFrequency, String name, Supplier<float[]> value) {
        this.location(name, UniformType.VEC4).ifPresent(id -> this.addUniform(updateFrequency, new Vector4ArrayUniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniformMatrix(UniformUpdateFrequency updateFrequency, String name, Supplier<Matrix4f> value) {
        this.location(name, UniformType.MAT4).ifPresent(id -> this.addUniform(updateFrequency, new MatrixUniform(id, value)));
        return this;
    }

    @Override
    default public LocationalUniformHolder uniformMatrixFromArray(UniformUpdateFrequency updateFrequency, String name, Supplier<float[]> value) {
        this.location(name, UniformType.MAT4).ifPresent(id -> this.addUniform(updateFrequency, new MatrixFromFloatArrayUniform(id, value)));
        return this;
    }
}

