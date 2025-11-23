/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix2f
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package net.irisshaders.iris.parsing;

import java.util.function.Supplier;
import kroppeb.stareval.function.Type;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MatrixType<T>
extends Type.ObjectType {
    public static MatrixType<Matrix2f> MAT2 = new MatrixType<Matrix2f>("mat2", Matrix2f::new);
    public static MatrixType<Matrix3f> MAT3 = new MatrixType<Matrix3f>("mat3", Matrix3f::new);
    public static MatrixType<Matrix4f> MAT4 = new MatrixType<Matrix4f>("mat4", Matrix4f::new);
    final String name;
    private final Supplier<T> supplier;

    public MatrixType(String name, Supplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

