/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.joml.Vector4i
 */
package net.irisshaders.iris.gl.uniform;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.FloatUniform;
import net.irisshaders.iris.gl.uniform.IntUniform;
import net.irisshaders.iris.gl.uniform.LocationalUniformHolder;
import net.irisshaders.iris.gl.uniform.MatrixUniform;
import net.irisshaders.iris.gl.uniform.Uniform;
import net.irisshaders.iris.gl.uniform.UniformType;
import net.irisshaders.iris.gl.uniform.Vector2IntegerJomlUniform;
import net.irisshaders.iris.gl.uniform.Vector2Uniform;
import net.irisshaders.iris.gl.uniform.Vector3Uniform;
import net.irisshaders.iris.gl.uniform.Vector4ArrayUniform;
import net.irisshaders.iris.gl.uniform.Vector4IntegerJomlUniform;
import net.irisshaders.iris.gl.uniform.Vector4Uniform;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;

public interface DynamicLocationalUniformHolder
extends LocationalUniformHolder,
DynamicUniformHolder {
    public DynamicLocationalUniformHolder addDynamicUniform(Uniform var1, ValueUpdateNotifier var2);

    @Override
    default public DynamicLocationalUniformHolder uniform1f(String name, FloatSupplier value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.FLOAT).ifPresent(id -> this.addDynamicUniform(new FloatUniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicLocationalUniformHolder uniform1f(String name, IntSupplier value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.FLOAT).ifPresent(id -> this.addDynamicUniform(new FloatUniform(id, () -> value.getAsInt(), notifier), notifier));
        return this;
    }

    @Override
    default public DynamicLocationalUniformHolder uniform1f(String name, DoubleSupplier value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.FLOAT).ifPresent(id -> this.addDynamicUniform(new FloatUniform(id, () -> (float)value.getAsDouble(), notifier), notifier));
        return this;
    }

    @Override
    default public DynamicLocationalUniformHolder uniform1i(String name, IntSupplier value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.INT).ifPresent(id -> this.addDynamicUniform(new IntUniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicLocationalUniformHolder uniform2f(String name, Supplier<Vector2f> value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.VEC2).ifPresent(id -> this.addDynamicUniform(new Vector2Uniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicLocationalUniformHolder uniform2i(String name, Supplier<Vector2i> value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.VEC2I).ifPresent(id -> this.addDynamicUniform(new Vector2IntegerJomlUniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicUniformHolder uniform3f(String name, Supplier<Vector3f> value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.VEC3).ifPresent(id -> this.addDynamicUniform(new Vector3Uniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicUniformHolder uniform4f(String name, Supplier<Vector4f> value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.VEC4).ifPresent(id -> this.addDynamicUniform(new Vector4Uniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicUniformHolder uniform4fArray(String name, Supplier<float[]> value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.VEC4).ifPresent(id -> this.addDynamicUniform(new Vector4ArrayUniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicUniformHolder uniform4i(String name, Supplier<Vector4i> value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.VEC4I).ifPresent(id -> this.addDynamicUniform(new Vector4IntegerJomlUniform(id, value, notifier), notifier));
        return this;
    }

    @Override
    default public DynamicUniformHolder uniformMatrix(String name, Supplier<Matrix4f> value, ValueUpdateNotifier notifier) {
        this.location(name, UniformType.MAT4).ifPresent(id -> this.addDynamicUniform(new MatrixUniform(id, value, notifier), notifier));
        return this;
    }
}

