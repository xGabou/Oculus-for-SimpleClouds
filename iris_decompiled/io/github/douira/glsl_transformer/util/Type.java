/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.util;

import io.github.douira.glsl_transformer.ast.data.TokenTyped;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import oculus.org.antlr.v4.runtime.Token;

public enum Type implements TokenTyped
{
    BOOL(69, 68, NumberType.BOOLEAN, "bool", "bool", 1, new int[0]),
    BVEC2(70, NumberType.BOOLEAN, "bvec2", "bvec2", 1, 2),
    BVEC3(71, NumberType.BOOLEAN, "bvec3", "bvec3", 1, 3),
    BVEC4(72, NumberType.BOOLEAN, "bvec4", "bvec4", 1, 4),
    INT8(73, NumberType.SIGNED_INTEGER, null, "int8_t", 8, new int[0]),
    I8VEC2(74, NumberType.SIGNED_INTEGER, null, "i8vec2", 8, 2),
    I8VEC3(75, NumberType.SIGNED_INTEGER, null, "i8vec3", 8, 3),
    I8VEC4(76, NumberType.SIGNED_INTEGER, null, "i8vec4", 8, 4),
    UINT8(77, NumberType.UNSIGNED_INTEGER, null, "uint8_t", 8, new int[0]),
    U8VEC2(78, NumberType.UNSIGNED_INTEGER, null, "ui8vec2", 8, 2),
    U8VEC3(79, NumberType.UNSIGNED_INTEGER, null, "ui8vec3", 8, 3),
    U8VEC4(80, NumberType.UNSIGNED_INTEGER, null, "ui8vec4", 8, 4),
    INT16(81, 60, NumberType.SIGNED_INTEGER, null, "int16_t", 16, new int[0]),
    I16VEC2(82, NumberType.SIGNED_INTEGER, null, "i16vec2", 16, 2),
    I16VEC3(83, NumberType.SIGNED_INTEGER, null, "i16vec3", 16, 3),
    I16VEC4(84, NumberType.SIGNED_INTEGER, null, "i16vec4", 16, 4),
    UINT16(85, 59, NumberType.UNSIGNED_INTEGER, null, "uint16_t", 16, new int[0]),
    U16VEC2(86, NumberType.UNSIGNED_INTEGER, null, "ui16vec2", 16, 2),
    U16VEC3(87, NumberType.UNSIGNED_INTEGER, null, "ui16vec3", 16, 3),
    U16VEC4(88, NumberType.UNSIGNED_INTEGER, null, "ui16vec4", 16, 4),
    INT32(89, 62, NumberType.SIGNED_INTEGER, "int", "int32_t", 32, new int[0]),
    I32VEC2(90, NumberType.SIGNED_INTEGER, "ivec2", "i32vec2", 32, 2),
    I32VEC3(91, NumberType.SIGNED_INTEGER, "ivec3", "i32vec3", 32, 3),
    I32VEC4(92, NumberType.SIGNED_INTEGER, "ivec4", "i32vec4", 32, 4),
    UINT32(93, 61, NumberType.UNSIGNED_INTEGER, "uint", "uint32_t", 32, new int[0]),
    U32VEC2(94, NumberType.UNSIGNED_INTEGER, "uvec2", "u32vec2", 32, 2),
    U32VEC3(95, NumberType.UNSIGNED_INTEGER, "uvec3", "u32vec3", 32, 3),
    U32VEC4(96, NumberType.UNSIGNED_INTEGER, "uvec4", "u32vec4", 32, 4),
    INT64(97, 64, NumberType.SIGNED_INTEGER, null, "int64_t", 64, new int[0]),
    I64VEC2(98, NumberType.SIGNED_INTEGER, null, "i64vec2", 64, 2),
    I64VEC3(99, NumberType.SIGNED_INTEGER, null, "i64vec3", 64, 3),
    I64VEC4(100, NumberType.SIGNED_INTEGER, null, "i64vec4", 64, 4),
    UINT64(101, 63, NumberType.UNSIGNED_INTEGER, null, "uint64_t", 64, new int[0]),
    U64VEC2(102, NumberType.UNSIGNED_INTEGER, null, "ui64vec2", 64, 2),
    U64VEC3(103, NumberType.UNSIGNED_INTEGER, null, "ui64vec3", 64, 3),
    U64VEC4(104, NumberType.UNSIGNED_INTEGER, null, "ui64vec4", 64, 4),
    FLOAT16(105, 65, NumberType.FLOATING_POINT, null, "float16_t", 16, new int[0]),
    F16VEC2(106, NumberType.FLOATING_POINT, null, "f16vec2", 16, 2),
    F16VEC3(107, NumberType.FLOATING_POINT, null, "f16vec3", 16, 3),
    F16VEC4(108, NumberType.FLOATING_POINT, null, "f16vec4", 16, 4),
    F16MAT2X2(109, NumberType.FLOATING_POINT, "f16mat2", "f16mat2x2", 16, 2, 2),
    F16MAT2X3(110, NumberType.FLOATING_POINT, null, "f16mat2x3", 16, 2, 3),
    F16MAT2X4(111, NumberType.FLOATING_POINT, null, "f16mat2x4", 16, 2, 4),
    F16MAT3X2(112, NumberType.FLOATING_POINT, null, "f16mat3x2", 16, 3, 2),
    F16MAT3X3(113, NumberType.FLOATING_POINT, "f16mat3", "f16mat3x3", 16, 3, 3),
    F16MAT3X4(114, NumberType.FLOATING_POINT, null, "f16mat3x4", 16, 3, 4),
    F16MAT4X2(115, NumberType.FLOATING_POINT, null, "f16mat4x2", 16, 4, 2),
    F16MAT4X3(116, NumberType.FLOATING_POINT, null, "f16mat4x3", 16, 4, 3),
    F16MAT4X4(117, NumberType.FLOATING_POINT, "f16mat4", "f16mat4x4", 16, 4, 4),
    FLOAT32(118, 66, NumberType.FLOATING_POINT, "float", "float32_t", 32, new int[0]),
    F32VEC2(119, NumberType.FLOATING_POINT, "vec2", "f32vec2", 32, 2),
    F32VEC3(120, NumberType.FLOATING_POINT, "vec3", "f32vec3", 32, 3),
    F32VEC4(121, NumberType.FLOATING_POINT, "vec4", "f32vec4", 32, 4),
    F32MAT2X2(122, NumberType.FLOATING_POINT, "mat2", "f32mat2x2", 32, 2, 2),
    F32MAT2X3(123, NumberType.FLOATING_POINT, "mat2x3", "f32mat2x3", 32, 2, 3),
    F32MAT2X4(124, NumberType.FLOATING_POINT, "mat2x4", "f32mat2x4", 32, 2, 4),
    F32MAT3X2(125, NumberType.FLOATING_POINT, "mat3x2", "f32mat3x2", 32, 3, 2),
    F32MAT3X3(126, NumberType.FLOATING_POINT, "mat3", "f32mat3x3", 32, 3, 3),
    F32MAT3X4(127, NumberType.FLOATING_POINT, "mat3x4", "f32mat3x4", 32, 3, 4),
    F32MAT4X2(128, NumberType.FLOATING_POINT, "mat4x2", "f32mat4x2", 32, 4, 2),
    F32MAT4X3(129, NumberType.FLOATING_POINT, "mat4x3", "f32mat4x3", 32, 4, 3),
    F32MAT4X4(130, NumberType.FLOATING_POINT, "mat4", "f32mat4x4", 32, 4, 4),
    FLOAT64(131, 67, NumberType.FLOATING_POINT, "double", "float64_t", 64, new int[0]),
    F64VEC2(132, NumberType.FLOATING_POINT, "dvec2", "f64vec2", 64, 2),
    F64VEC3(133, NumberType.FLOATING_POINT, "dvec3", "f64vec3", 64, 3),
    F64VEC4(134, NumberType.FLOATING_POINT, "dvec4", "f64vec4", 64, 4),
    F64MAT2X2(135, NumberType.FLOATING_POINT, "dmat2", "f64mat2x2", 64, 2, 2),
    F64MAT2X3(136, NumberType.FLOATING_POINT, "dmat2x3", "f64mat2x3", 64, 2, 3),
    F64MAT2X4(137, NumberType.FLOATING_POINT, "dmat2x4", "f64mat2x4", 64, 2, 4),
    F64MAT3X2(138, NumberType.FLOATING_POINT, "dmat3x2", "f64mat3x2", 64, 3, 2),
    F64MAT3X3(139, NumberType.FLOATING_POINT, "dmat3", "f64mat3x3", 64, 3, 3),
    F64MAT3X4(140, NumberType.FLOATING_POINT, "dmat3x4", "f64mat3x4", 64, 3, 4),
    F64MAT4X2(141, NumberType.FLOATING_POINT, "dmat4x2", "f64mat4x2", 64, 4, 2),
    F64MAT4X3(142, NumberType.FLOATING_POINT, "dmat4x3", "f64mat4x3", 64, 4, 3),
    F64MAT4X4(143, NumberType.FLOATING_POINT, "dmat4", "f64mat4x4", 64, 4, 4);

    private final int tokenType;
    private final int literalTokenType;
    private final NumberType numberType;
    private final int[] dimensions;
    private final int bitDepth;
    private final String compactName;
    private final String explicitName;
    private EnumSet<Type> implicitCastTypes;
    private final int[] SCALAR_DIMENSIONS = new int[]{1};
    private static final Type[] tokenTypesToValues;
    private static final Map<Integer, Type> literalTokenTypesToValues;
    private static final int minIndex;

    private Type(int tokenType, NumberType numberType, String compactName, String explicitName, int bitDepth, int ... dimensions) {
        this(tokenType, 0, numberType, compactName, explicitName, bitDepth, dimensions);
    }

    private Type(int tokenType, int literalTokenType, NumberType numberType, String compactName, String explicitName, int bitDepth, int ... dimensions) {
        if (bitDepth > numberType.getMaxBitDepth()) {
            throw new IllegalArgumentException("Bit depth provided is larger than maximum bit depth for type " + numberType);
        }
        if (dimensions.length < 1) {
            dimensions = this.SCALAR_DIMENSIONS;
        } else {
            int[] maxDimensions = numberType.getMaxDimensions();
            if (dimensions.length > maxDimensions.length) {
                throw new IllegalArgumentException("Dimensions provided is longer than maximum dimensions for type " + numberType);
            }
            for (int i = 0; i < dimensions.length; ++i) {
                int dimSize = dimensions[i];
                int maxDimSize = maxDimensions[i];
                if (dimSize <= maxDimSize) continue;
                throw new IllegalArgumentException("Dimensions provided exceeds maximum dimensions for type " + numberType);
            }
        }
        this.tokenType = tokenType;
        this.literalTokenType = literalTokenType;
        this.numberType = numberType;
        this.dimensions = dimensions;
        this.bitDepth = bitDepth;
        this.compactName = compactName;
        this.explicitName = explicitName;
    }

    @Override
    public int getTokenType() {
        return this.tokenType;
    }

    public NumberType getNumberType() {
        return this.numberType;
    }

    public int[] getDimensions() {
        return this.dimensions;
    }

    public int getDimension() {
        return this.dimensions.length;
    }

    public boolean isScalar() {
        return this.dimensions == this.SCALAR_DIMENSIONS;
    }

    public boolean isVector() {
        return this.dimensions.length == 1;
    }

    public boolean isMatrix() {
        return this.dimensions.length == 2;
    }

    public int getBitDepth() {
        return this.bitDepth;
    }

    public String getCompactName() {
        return this.compactName;
    }

    public String getMostCompactName() {
        return this.compactName != null ? this.compactName : this.explicitName;
    }

    public String getExplicitName() {
        return this.explicitName;
    }

    public EnumSet<Type> getImplicitCasts() {
        return this.implicitCastTypes;
    }

    public static Type fromToken(Token token) {
        return Type.ofTokenType(token.getType());
    }

    public static Type ofTokenType(int tokenType) {
        return tokenTypesToValues[tokenType - minIndex];
    }

    public static Type ofLiteralTokenType(int literalTokenType) {
        Type type = literalTokenTypesToValues.get(literalTokenType);
        if (type == null) {
            throw new IllegalArgumentException("Token type has no literal type: " + literalTokenType);
        }
        return type;
    }

    static {
        int localMinIndex = Integer.MAX_VALUE;
        int localMaxIndex = Integer.MIN_VALUE;
        for (Type entry : Type.values()) {
            int tokenType = entry.getTokenType();
            if (tokenType < localMinIndex) {
                localMinIndex = tokenType;
            }
            if (tokenType <= localMaxIndex) continue;
            localMaxIndex = tokenType;
        }
        minIndex = localMinIndex;
        Type[] localTokensTypesToValues = new Type[localMaxIndex - localMinIndex + 1];
        literalTokenTypesToValues = new HashMap<Integer, Type>();
        for (Type entry : Type.values()) {
            int index = entry.tokenType - minIndex;
            if (localTokensTypesToValues[index] != null) {
                throw new AssertionError((Object)"A type was registered multiple times for the same token. Fix the Tensor class' initialization!");
            }
            localTokensTypesToValues[index] = entry;
            if (entry.literalTokenType == 0) continue;
            literalTokenTypesToValues.put(entry.literalTokenType, entry);
        }
        tokenTypesToValues = localTokensTypesToValues;
        for (Type entry : Type.values()) {
            EnumSet<Type> registeredTypes = entry.numberType.registeredTypes;
            if (registeredTypes != null) {
                registeredTypes.add(entry);
                continue;
            }
            entry.numberType.registeredTypes = EnumSet.of(entry);
        }
        for (Type t1 : Type.values()) {
            EnumSet<Type> implicitCastTypes = EnumSet.noneOf(Type.class);
            t1.implicitCastTypes = implicitCastTypes;
            for (Type t2 : Type.values()) {
                boolean canCast;
                block26: {
                    boolean bl;
                    block25: {
                        block24: {
                            if (t1.equals(t2)) break block24;
                            if (!Arrays.equals(t1.dimensions, t2.dimensions)) break block25;
                            block0 : switch (t1.numberType) {
                                default: {
                                    throw new IncompatibleClassChangeError();
                                }
                                case BOOLEAN: {
                                    break block25;
                                }
                                case SIGNED_INTEGER: {
                                    switch (t2.numberType) {
                                        case UNSIGNED_INTEGER: 
                                        case SIGNED_INTEGER: 
                                        case FLOATING_POINT: {
                                            if (t2.bitDepth >= t1.bitDepth) {
                                                break block0;
                                            }
                                            break block25;
                                        }
                                    }
                                    break block25;
                                }
                                case UNSIGNED_INTEGER: {
                                    switch (t2.numberType) {
                                        case UNSIGNED_INTEGER: 
                                        case SIGNED_INTEGER: {
                                            if (t2.bitDepth > t1.bitDepth) {
                                                break block0;
                                            }
                                            break block25;
                                        }
                                        case FLOATING_POINT: {
                                            if (t2.bitDepth >= t1.bitDepth) {
                                                break block0;
                                            }
                                            break block25;
                                        }
                                    }
                                    break block25;
                                }
                                case FLOATING_POINT: {
                                    if (!t2.numberType.equals((Object)NumberType.FLOATING_POINT) || t2.bitDepth < t1.bitDepth) break block25;
                                }
                            }
                        }
                        bl = true;
                        break block26;
                    }
                    bl = canCast = false;
                }
                if (!canCast) continue;
                implicitCastTypes.add(t2);
            }
        }
    }

    public static enum NumberType {
        BOOLEAN(1, 4),
        UNSIGNED_INTEGER(64, 4),
        SIGNED_INTEGER(64, 4),
        FLOATING_POINT(64, 4, 4);

        private final int maxBitDepth;
        private final int[] maxDimensions;
        private EnumSet<Type> registeredTypes;

        private NumberType(int maxBitDepth, int ... maxDimensions) {
            this.maxBitDepth = maxBitDepth;
            this.maxDimensions = maxDimensions;
        }

        public int getMaxBitDepth() {
            return this.maxBitDepth;
        }

        public int[] getMaxDimensions() {
            return this.maxDimensions;
        }

        public EnumSet<Type> getRegisteredTypes() {
            return this.registeredTypes;
        }
    }
}

