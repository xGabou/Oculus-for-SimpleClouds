/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 *  org.joml.Vector4f
 *  org.joml.Vector4i
 */
package net.irisshaders.iris.parsing;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;

public abstract class VectorType
extends Type.ObjectType {
    public static final JOMLVector<Vector2f> VEC2 = new JOMLVector<Vector2f>("vec2", Vector2f::new);
    public static final JOMLVector<Vector3f> VEC3 = new JOMLVector<Vector3f>("vec3", Vector3f::new);
    public static final JOMLVector<Vector4f> VEC4 = new JOMLVector<Vector4f>("vec4", Vector4f::new);
    public static final JOMLVector<Vector2i> I_VEC2 = new JOMLVector<Vector2i>("ivec2", Vector2i::new);
    public static final JOMLVector<Vector3i> I_VEC3 = new JOMLVector<Vector3i>("ivec3", Vector3i::new);
    public static final JOMLVector<Vector4i> I_VEC4 = new JOMLVector<Vector4i>("ivec4", Vector4i::new);
    public static final VectorType B_VEC2 = new ArrayVector(Type.Boolean, 2);
    public static final VectorType B_VEC3 = new ArrayVector(Type.Boolean, 3);
    public static final VectorType B_VEC4 = new ArrayVector(Type.Boolean, 4);
    public static final ArrayVector[] AllArrayVectorTypes = (ArrayVector[])Stream.of(Type.Int, Type.Boolean).flatMap(type -> IntStream.rangeClosed(2, 4).mapToObj(i -> new ArrayVector((Type)type, i))).toArray(ArrayVector[]::new);
    public static final VectorType[] AllVectorTypes = (VectorType[])Arrays.stream(Type.AllPrimitives).flatMap(type -> IntStream.rangeClosed(2, 4).mapToObj(i -> VectorType.of(type, i))).toArray(VectorType[]::new);

    public static VectorType of(Type.Primitive primitive, int size) {
        if (primitive.equals(Type.Float)) {
            return switch (size) {
                case 2 -> VEC2;
                case 3 -> VEC3;
                case 4 -> VEC4;
                default -> throw new IllegalArgumentException("not a valid vector");
            };
        }
        return new ArrayVector(primitive, size);
    }

    public static class JOMLVector<T>
    extends VectorType {
        private final String name;
        private final Supplier<T> supplier;

        public JOMLVector(String name, Supplier<T> supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public T create() {
            return this.supplier.get();
        }
    }

    public static class ArrayVector
    extends VectorType {
        private final Type inner;
        private final int size;

        public ArrayVector(Type inner, int size) {
            this.inner = inner;
            this.size = size;
        }

        public Object createObject() {
            return this.inner.createArray(this.size);
        }

        public void setValue(Object vector, int index, FunctionReturn functionReturn) {
            this.inner.setValueFromReturn(vector, index, functionReturn);
        }

        public void getValue(Object vector, int index, FunctionReturn functionReturn) {
            this.inner.getValueFromArray(vector, index, functionReturn);
        }

        public <T1, T2> void map(T1 item1, T2 item2, FunctionReturn functionReturn, IntObjectObjectObjectConsumer<T1, T2, FunctionReturn> mapper) {
            Object array = this.createObject();
            for (int i = 0; i < this.size; ++i) {
                mapper.accept(i, item1, item2, functionReturn);
                this.setValue(array, i, functionReturn);
            }
            functionReturn.objectReturn = array;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ArrayVector)) {
                return false;
            }
            ArrayVector that = (ArrayVector)o;
            return this.size == that.size && this.inner.equals(that.inner);
        }

        public int hashCode() {
            return Objects.hash(this.inner, this.size);
        }

        @Override
        public String toString() {
            String base = this.inner.equals(Type.Float) ? "" : this.inner.toString().substring(0, 1);
            return "__" + base + "vec" + this.size;
        }

        public static interface IntObjectObjectObjectConsumer<TB, TC, TD> {
            public void accept(int var1, TB var2, TC var3, TD var4);
        }
    }
}

