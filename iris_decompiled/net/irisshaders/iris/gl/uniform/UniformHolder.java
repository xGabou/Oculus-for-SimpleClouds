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

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.UniformType;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public interface UniformHolder {
    public UniformHolder uniform1f(UniformUpdateFrequency var1, String var2, FloatSupplier var3);

    public UniformHolder uniform1f(UniformUpdateFrequency var1, String var2, IntSupplier var3);

    public UniformHolder uniform1f(UniformUpdateFrequency var1, String var2, DoubleSupplier var3);

    public UniformHolder uniform1i(UniformUpdateFrequency var1, String var2, IntSupplier var3);

    public UniformHolder uniform1b(UniformUpdateFrequency var1, String var2, BooleanSupplier var3);

    public UniformHolder uniform2f(UniformUpdateFrequency var1, String var2, Supplier<Vector2f> var3);

    public UniformHolder uniform2i(UniformUpdateFrequency var1, String var2, Supplier<Vector2i> var3);

    public UniformHolder uniform3f(UniformUpdateFrequency var1, String var2, Supplier<Vector3f> var3);

    public UniformHolder uniform3i(UniformUpdateFrequency var1, String var2, Supplier<Vector3i> var3);

    public UniformHolder uniformTruncated3f(UniformUpdateFrequency var1, String var2, Supplier<Vector4f> var3);

    public UniformHolder uniform3d(UniformUpdateFrequency var1, String var2, Supplier<Vector3d> var3);

    public UniformHolder uniform4f(UniformUpdateFrequency var1, String var2, Supplier<Vector4f> var3);

    public UniformHolder uniform4fArray(UniformUpdateFrequency var1, String var2, Supplier<float[]> var3);

    public UniformHolder uniformMatrix(UniformUpdateFrequency var1, String var2, Supplier<Matrix4f> var3);

    public UniformHolder uniformMatrixFromArray(UniformUpdateFrequency var1, String var2, Supplier<float[]> var3);

    public UniformHolder externallyManagedUniform(String var1, UniformType var2);
}

