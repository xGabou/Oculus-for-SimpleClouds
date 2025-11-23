/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector2i
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector3i
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.joml.Vector4i
 */
package net.irisshaders.iris.parsing;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.function.AbstractTypedFunction;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionResolver;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import kroppeb.stareval.function.TypedFunction;
import net.irisshaders.iris.parsing.BooleanVectorizedFunction;
import net.irisshaders.iris.parsing.MatrixType;
import net.irisshaders.iris.parsing.SmoothFloat;
import net.irisshaders.iris.parsing.VectorConstructor;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.parsing.VectorizedFunction;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.joml.Vector4i;

public class IrisFunctions {
    public static final FunctionResolver functions;
    static final FunctionResolver.Builder builder;

    static <T extends TypedFunction> void addVectorized(String name, T function) {
        if (!(function.getReturnType() instanceof Type.Primitive)) {
            throw new IllegalArgumentException(name + " is not vectorizable");
        }
        IrisFunctions.add(name, new VectorizedFunction(function, 2));
        IrisFunctions.add(name, new VectorizedFunction(function, 3));
        IrisFunctions.add(name, new VectorizedFunction(function, 4));
    }

    static <T extends TypedFunction> void addVectorizable(String name, T function) {
        IrisFunctions.add(name, function);
        IrisFunctions.addVectorized(name, function);
    }

    static <T extends TypedFunction> void addBooleanVectorizable(String name, T function) {
        assert (function.getReturnType().equals(Type.Boolean));
        IrisFunctions.add(name, function);
        if (!(function.getReturnType() instanceof Type.Primitive)) {
            throw new IllegalArgumentException(name + " is not vectorizable");
        }
        IrisFunctions.add(name, new BooleanVectorizedFunction(function, 2));
        IrisFunctions.add(name, new BooleanVectorizedFunction(function, 3));
        IrisFunctions.add(name, new BooleanVectorizedFunction(function, 4));
    }

    static <T> void addUnaryOpJOML(String name, final VectorType.JOMLVector<T> type, final BiConsumer<T, T> function) {
        builder.add(name, new AbstractTypedFunction(type, new Type[]{type}){
            private final T vector;
            {
                super(returnType, parameterType);
                this.vector = type.create();
            }

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                Object a = functionReturn.objectReturn;
                function.accept(a, this.vector);
                functionReturn.objectReturn = this.vector;
            }
        });
    }

    static <T> void addBinaryOpJOML(String name, final VectorType.JOMLVector<T> type, final TriConsumer<T, T, T> function) {
        builder.add(name, new AbstractTypedFunction(type, new Type[]{type, type}){
            private final T vector;
            {
                super(returnType, parameterType);
                this.vector = type.create();
            }

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                Object a = functionReturn.objectReturn;
                params[1].evaluateTo(context, functionReturn);
                Object b = functionReturn.objectReturn;
                function.accept(a, b, this.vector);
                functionReturn.objectReturn = this.vector;
            }
        });
    }

    static <T> void addTernaryOpJOML(String name, final VectorType.JOMLVector<T> type, final QuadConsumer<T, T, T, T> function) {
        builder.add(name, new AbstractTypedFunction(type, new Type[]{type, type, type}){
            private final T vector;
            {
                super(returnType, parameterType);
                this.vector = type.create();
            }

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                Object a = functionReturn.objectReturn;
                params[1].evaluateTo(context, functionReturn);
                Object b = functionReturn.objectReturn;
                params[2].evaluateTo(context, functionReturn);
                Object c = functionReturn.objectReturn;
                function.accept(a, b, c, this.vector);
                functionReturn.objectReturn = this.vector;
            }
        });
    }

    static <T> void addBinaryToBooleanOpJOML(String name, VectorType.JOMLVector<T> type, final boolean inverted, final ObjectObject2BooleanFunction<T, T> function) {
        builder.add(name, new AbstractTypedFunction(type, new Type[]{type, type}){

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                Object a = functionReturn.objectReturn;
                params[1].evaluateTo(context, functionReturn);
                Object b = functionReturn.objectReturn;
                functionReturn.objectReturn = function.apply(a, b) != inverted;
            }
        });
    }

    static <T extends TypedFunction> void add(String name, T function) {
        builder.add(name, function);
    }

    static void addCast(String name, final Type from, final Type to, final Consumer<FunctionReturn> function) {
        IrisFunctions.add(name, new TypedFunction(){

            @Override
            public Type getReturnType() {
                return to;
            }

            @Override
            public TypedFunction.Parameter[] getParameters() {
                return new TypedFunction.Parameter[]{new TypedFunction.Parameter(from)};
            }

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                function.accept(functionReturn);
            }
        });
    }

    static void addImplicitCast(Type from, Type to, Consumer<FunctionReturn> function) {
        IrisFunctions.addCast("<cast>", from, to, function);
        IrisFunctions.addExplicitCast(from, to, function);
    }

    static void addExplicitCast(Type from, Type to, Consumer<FunctionReturn> function) {
        IrisFunctions.addCast("to" + to.getClass().getSimpleName(), from, to, function);
    }

    public static void main(String[] args) {
        functions.logAllFunctions();
    }

    private static /* synthetic */ float lambda$static$62(Random random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private static /* synthetic */ int lambda$static$61(Random random, int a, int b) {
        return random.nextInt(b - a) + a;
    }

    static {
        int length;
        int n;
        builder = new FunctionResolver.Builder();
        IrisFunctions.addVectorizable("negate", a -> -a);
        IrisFunctions.add("negate", a -> -a);
        IrisFunctions.addUnaryOpJOML("negate", VectorType.VEC2, Vector2f::negate);
        IrisFunctions.addUnaryOpJOML("negate", VectorType.VEC3, Vector3f::negate);
        IrisFunctions.addUnaryOpJOML("negate", VectorType.VEC4, Vector4f::negate);
        IrisFunctions.addVectorizable("add", Integer::sum);
        IrisFunctions.add("add", Float::sum);
        IrisFunctions.addBinaryOpJOML("add", VectorType.VEC2, Vector2f::add);
        IrisFunctions.addBinaryOpJOML("add", VectorType.VEC3, Vector3f::add);
        IrisFunctions.addBinaryOpJOML("add", VectorType.VEC4, Vector4f::add);
        IrisFunctions.addVectorizable("subtract", (a, b) -> a - b);
        IrisFunctions.add("subtract", (a, b) -> a - b);
        IrisFunctions.addBinaryOpJOML("subtract", VectorType.VEC2, Vector2f::sub);
        IrisFunctions.addBinaryOpJOML("subtract", VectorType.VEC3, Vector3f::sub);
        IrisFunctions.addBinaryOpJOML("subtract", VectorType.VEC4, Vector4f::sub);
        IrisFunctions.addVectorizable("multiply", (a, b) -> a * b);
        IrisFunctions.add("multiply", (a, b) -> a * b);
        IrisFunctions.addBinaryOpJOML("multiply", VectorType.VEC2, Vector2f::mul);
        IrisFunctions.addBinaryOpJOML("multiply", VectorType.VEC3, Vector3f::mul);
        IrisFunctions.addBinaryOpJOML("multiply", VectorType.VEC4, Vector4f::mul);
        IrisFunctions.add("divide", (a, b) -> a / b);
        IrisFunctions.addBinaryOpJOML("divide", VectorType.VEC2, Vector2f::div);
        IrisFunctions.addBinaryOpJOML("divide", VectorType.VEC3, Vector3f::div);
        IrisFunctions.addBinaryOpJOML("divide", VectorType.VEC4, Vector4f::div);
        IrisFunctions.addVectorizable("remainder", (a, b) -> a % b);
        IrisFunctions.add("remainder", (a, b) -> a % b);
        IrisFunctions.addBooleanVectorizable("equals", (a, b) -> a == b);
        IrisFunctions.add("equals", (a, b) -> a == b);
        IrisFunctions.addBinaryToBooleanOpJOML("equal", VectorType.VEC2, false, Vector2f::equals);
        IrisFunctions.addBinaryToBooleanOpJOML("equal", VectorType.VEC3, false, Vector3f::equals);
        IrisFunctions.addBinaryToBooleanOpJOML("equal", VectorType.VEC4, false, Vector4f::equals);
        IrisFunctions.addBooleanVectorizable("notEquals", (a, b) -> a != b);
        IrisFunctions.add("notEquals", (a, b) -> a != b);
        IrisFunctions.addBinaryToBooleanOpJOML("equal", VectorType.VEC2, true, Vector2f::equals);
        IrisFunctions.addBinaryToBooleanOpJOML("equal", VectorType.VEC3, true, Vector3f::equals);
        IrisFunctions.addBinaryToBooleanOpJOML("equal", VectorType.VEC4, true, Vector4f::equals);
        IrisFunctions.add("lessThanOrEquals", (a, b) -> a <= b);
        IrisFunctions.add("lessThanOrEquals", (a, b) -> a <= b);
        IrisFunctions.add("moreThanOrEquals", (a, b) -> a >= b);
        IrisFunctions.add("moreThanOrEquals", (a, b) -> a >= b);
        IrisFunctions.add("lessThan", (a, b) -> a < b);
        IrisFunctions.add("lessThan", (a, b) -> a < b);
        IrisFunctions.add("moreThan", (a, b) -> a > b);
        IrisFunctions.add("moreThan", (a, b) -> a > b);
        IrisFunctions.addVectorizable("equals", (a, b) -> a == b);
        IrisFunctions.addVectorizable("notEquals", (a, b) -> a != b);
        IrisFunctions.addVectorizable("and", (a, b) -> a && b);
        IrisFunctions.addVectorizable("or", (a, b) -> a || b);
        IrisFunctions.addVectorizable("not", a -> !a);
        IrisFunctions.add("torad", a -> (float)Math.toRadians(a));
        IrisFunctions.add("todeg", a -> (float)Math.toDegrees(a));
        IrisFunctions.add("radians", a -> (float)Math.toRadians(a));
        IrisFunctions.add("degrees", a -> (float)Math.toDegrees(a));
        IrisFunctions.add("sin", a -> (float)Math.sin(a));
        IrisFunctions.add("cos", a -> (float)Math.cos(a));
        IrisFunctions.add("tan", a -> (float)Math.tan(a));
        IrisFunctions.add("asin", a -> (float)Math.asin(a));
        IrisFunctions.add("acos", a -> (float)Math.acos(a));
        IrisFunctions.add("atan", a -> (float)Math.atan(a));
        IrisFunctions.add("atan", (y, x) -> (float)Math.atan2(y, x));
        IrisFunctions.add("atan2", (y, x) -> (float)Math.atan2(y, x));
        IrisFunctions.add("pow", (a, b) -> (float)Math.pow(a, b));
        IrisFunctions.add("exp", a -> (float)Math.exp(a));
        IrisFunctions.add("log", a -> (float)Math.log(a));
        IrisFunctions.add("exp2", a -> (float)Math.pow(2.0, a));
        IrisFunctions.add("log2", a -> (float)(Math.log(a) / Math.log(2.0)));
        IrisFunctions.add("sqrt", a -> (float)Math.sqrt(a));
        IrisFunctions.add("log10", a -> (float)Math.log10(a));
        IrisFunctions.add("log", (base, value) -> (float)(Math.log(value) / Math.log(base)));
        IrisFunctions.add("exp10", a -> (float)Math.pow(10.0, a));
        IrisFunctions.addVectorizable("abs", Math::abs);
        IrisFunctions.add("abs", Math::abs);
        IrisFunctions.addUnaryOpJOML("abs", VectorType.VEC2, Vector2f::absolute);
        IrisFunctions.addUnaryOpJOML("abs", VectorType.VEC3, Vector3f::absolute);
        IrisFunctions.addUnaryOpJOML("abs", VectorType.VEC4, Vector4f::absolute);
        IrisFunctions.add("sign", Math::signum);
        IrisFunctions.add("signum", Math::signum);
        IrisFunctions.add("floor", a -> (float)Math.floor(a));
        IrisFunctions.add("floor", a -> (int)Math.floor(a));
        IrisFunctions.addUnaryOpJOML("floor", VectorType.VEC2, Vector2f::floor);
        IrisFunctions.addUnaryOpJOML("floor", VectorType.VEC3, Vector3f::floor);
        IrisFunctions.addUnaryOpJOML("floor", VectorType.VEC4, Vector4f::floor);
        IrisFunctions.add("ceil", a -> (float)Math.ceil(a));
        IrisFunctions.add("ceil", a -> (int)Math.ceil(a));
        IrisFunctions.addUnaryOpJOML("ceil", VectorType.VEC2, Vector2f::ceil);
        IrisFunctions.addUnaryOpJOML("ceil", VectorType.VEC3, Vector3f::ceil);
        IrisFunctions.addUnaryOpJOML("ceil", VectorType.VEC4, Vector4f::ceil);
        IrisFunctions.add("frac", a -> (float)((double)a - Math.floor(a)));
        IrisFunctions.addVectorizable("min", Math::min);
        IrisFunctions.add("min", Math::min);
        IrisFunctions.addBinaryOpJOML("min", VectorType.VEC2, Vector2f::min);
        IrisFunctions.addBinaryOpJOML("min", VectorType.VEC3, Vector3f::min);
        IrisFunctions.addBinaryOpJOML("min", VectorType.VEC4, Vector4f::min);
        IrisFunctions.addVectorizable("max", Math::max);
        IrisFunctions.add("max", Math::max);
        IrisFunctions.addBinaryOpJOML("max", VectorType.VEC2, Vector2f::max);
        IrisFunctions.addBinaryOpJOML("max", VectorType.VEC3, Vector3f::max);
        IrisFunctions.addBinaryOpJOML("max", VectorType.VEC4, Vector4f::max);
        for (int length2 = 3; length2 <= 16; ++length2) {
            Object[] inputs = new Type[length2];
            Arrays.fill(inputs, Type.Float);
            IrisFunctions.add("min", new AbstractTypedFunction(Type.Float, (Type[])inputs){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    float min = functionReturn.floatReturn;
                    for (int i = 1; i < params.length; ++i) {
                        params[1].evaluateTo(context, functionReturn);
                        min = Math.min(min, functionReturn.floatReturn);
                    }
                    functionReturn.floatReturn = min;
                }
            });
            inputs = new Type[length2];
            Arrays.fill(inputs, Type.Float);
            IrisFunctions.add("max", new AbstractTypedFunction(Type.Float, (Type[])inputs){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    float max = functionReturn.floatReturn;
                    for (int i = 1; i < params.length; ++i) {
                        params[1].evaluateTo(context, functionReturn);
                        max = Math.max(max, functionReturn.floatReturn);
                    }
                    functionReturn.floatReturn = max;
                }
            });
            inputs = new Type[length2];
            Arrays.fill(inputs, Type.Int);
            IrisFunctions.addVectorizable("min", new AbstractTypedFunction(Type.Int, (Type[])inputs){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    int min = functionReturn.intReturn;
                    for (int i = 1; i < params.length; ++i) {
                        params[1].evaluateTo(context, functionReturn);
                        min = Math.min(min, functionReturn.intReturn);
                    }
                    functionReturn.intReturn = min;
                }
            });
            inputs = new Type[length2];
            Arrays.fill(inputs, Type.Int);
            IrisFunctions.addVectorizable("max", new AbstractTypedFunction(Type.Int, (Type[])inputs){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    int max = functionReturn.intReturn;
                    for (int i = 1; i < params.length; ++i) {
                        params[1].evaluateTo(context, functionReturn);
                        max = Math.max(max, functionReturn.intReturn);
                    }
                    functionReturn.intReturn = max;
                }
            });
        }
        IrisFunctions.addVectorizable("clamp", (val, min, max) -> Math.max(min, Math.min(max, val)));
        IrisFunctions.add("clamp", (val, min, max) -> Math.max(min, Math.min(max, val)));
        IrisFunctions.addTernaryOpJOML("clamp", VectorType.VEC2, (val, min, max, dest) -> {
            val.min((Vector2fc)max, dest);
            dest.max((Vector2fc)min);
        });
        IrisFunctions.addTernaryOpJOML("clamp", VectorType.VEC3, (val, min, max, dest) -> {
            val.min((Vector3fc)max, dest);
            dest.max((Vector3fc)min);
        });
        IrisFunctions.addTernaryOpJOML("clamp", VectorType.VEC4, (val, min, max, dest) -> {
            val.min((Vector4fc)max, dest);
            dest.max((Vector4fc)min);
        });
        IrisFunctions.add("mix", (x, y, a) -> x + (y - x) * a);
        IrisFunctions.addVectorizable("edge", (edge, x) -> x < edge ? 0 : 1);
        IrisFunctions.add("edge", (edge, x) -> x < edge ? 0.0f : 1.0f);
        IrisFunctions.addVectorizable("fmod", Math::floorMod);
        IrisFunctions.add("fmod", (a, b) -> (a % b + b) % b);
        Type[] random = new Random();
        IrisFunctions.addVectorizable("randomInt", ((Random)random)::nextInt);
        IrisFunctions.addVectorizable("randomInt", ((Random)random)::nextInt);
        IrisFunctions.addVectorizable("randomInt", (arg_0, arg_1) -> IrisFunctions.lambda$static$61((Random)random, arg_0, arg_1));
        IrisFunctions.add("random", ((Random)random)::nextFloat);
        IrisFunctions.add("random", (arg_0, arg_1) -> IrisFunctions.lambda$static$62((Random)random, arg_0, arg_1));
        for (Type type : Type.AllPrimitives) {
            IrisFunctions.add("if", new AbstractTypedFunction(type, new Type[]{Type.Boolean, type, type}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    params[functionReturn.booleanReturn ? 1 : 2].evaluateTo(context, functionReturn);
                }
            });
        }
        random = VectorType.AllVectorTypes;
        int inputs2 = random.length;
        for (n = 0; n < inputs2; ++n) {
            Type type;
            type = random[n];
            IrisFunctions.add("if", new AbstractTypedFunction(type, new Type[]{Type.Boolean, type, type}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    params[functionReturn.booleanReturn ? 1 : 2].evaluateTo(context, functionReturn);
                }
            });
        }
        for (length = 2; length <= 16; ++length) {
            for (Type type2 : Type.AllPrimitives) {
                Type[] params = new Type[length * 2 + 1];
                for (int i = 0; i < length * 2; i += 2) {
                    params[i] = Type.Boolean;
                    params[i + 1] = type2;
                }
                params[length * 2] = type2;
                final int finalLength = length * 2;
                IrisFunctions.add("if", new AbstractTypedFunction(type2, params){

                    @Override
                    public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                        for (int i = 0; i < finalLength; i += 2) {
                            params[i].evaluateTo(context, functionReturn);
                            if (functionReturn.booleanReturn) {
                                params[i + 1].evaluateTo(context, functionReturn);
                                return;
                            }
                            params[finalLength].evaluateTo(context, functionReturn);
                        }
                    }
                });
            }
            Type[] inputs2 = VectorType.AllVectorTypes;
            n = inputs2.length;
            for (int type = 0; type < n; ++type) {
                Type type2;
                type2 = inputs2[type];
                IrisFunctions.add("if", new AbstractTypedFunction(type2, new Type[]{Type.Boolean, type2, type2}){

                    @Override
                    public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                        params[0].evaluateTo(context, functionReturn);
                        params[functionReturn.booleanReturn ? 1 : 2].evaluateTo(context, functionReturn);
                    }
                });
            }
        }
        builder.addDynamicFunction("smooth", Type.Float, () -> new AbstractTypedFunction(Type.Float, new TypedFunction.Parameter[]{new TypedFunction.Parameter(Type.Float, false)}, 0, false){
            private final SmoothFloat smoothFloat = new SmoothFloat();

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                float target = functionReturn.floatReturn;
                functionReturn.floatReturn = this.smoothFloat.updateAndGet(target, 1.0f, 1.0f);
            }
        });
        builder.addDynamicFunction("smooth", Type.Float, () -> new AbstractTypedFunction(Type.Float, new TypedFunction.Parameter[]{new TypedFunction.Parameter(Type.Float, true), new TypedFunction.Parameter(Type.Float, false)}, 1, false){
            private final SmoothFloat smoothFloat = new SmoothFloat();

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[1].evaluateTo(context, functionReturn);
                float target = functionReturn.floatReturn;
                functionReturn.floatReturn = this.smoothFloat.updateAndGet(target, 1.0f, 1.0f);
            }
        });
        builder.addDynamicFunction("smooth", Type.Float, () -> new AbstractTypedFunction(Type.Float, new TypedFunction.Parameter[]{new TypedFunction.Parameter(Type.Float, false), new TypedFunction.Parameter(Type.Float, false)}, 0, false){
            private final SmoothFloat smoothFloat = new SmoothFloat();

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                float target = functionReturn.floatReturn;
                params[1].evaluateTo(context, functionReturn);
                float fadeTime = functionReturn.floatReturn;
                functionReturn.floatReturn = this.smoothFloat.updateAndGet(target, fadeTime, fadeTime);
            }
        });
        builder.addDynamicFunction("smooth", Type.Float, () -> new AbstractTypedFunction(Type.Float, new TypedFunction.Parameter[]{new TypedFunction.Parameter(Type.Float, true), new TypedFunction.Parameter(Type.Float, false), new TypedFunction.Parameter(Type.Float, false)}, 1, false){
            private final SmoothFloat smoothFloat = new SmoothFloat();

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[1].evaluateTo(context, functionReturn);
                float target = functionReturn.floatReturn;
                params[2].evaluateTo(context, functionReturn);
                float fadeTime = functionReturn.floatReturn;
                functionReturn.floatReturn = this.smoothFloat.updateAndGet(target, fadeTime, fadeTime);
            }
        });
        builder.addDynamicFunction("smooth", Type.Float, () -> new AbstractTypedFunction(Type.Float, new TypedFunction.Parameter[]{new TypedFunction.Parameter(Type.Float, false), new TypedFunction.Parameter(Type.Float, false), new TypedFunction.Parameter(Type.Float, false)}, 0, false){
            private final SmoothFloat smoothFloat = new SmoothFloat();

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                float target = functionReturn.floatReturn;
                params[1].evaluateTo(context, functionReturn);
                float fadeUpTime = functionReturn.floatReturn;
                params[2].evaluateTo(context, functionReturn);
                float fadeDownTime = functionReturn.floatReturn;
                functionReturn.floatReturn = this.smoothFloat.updateAndGet(target, fadeUpTime, fadeDownTime);
            }
        });
        builder.addDynamicFunction("smooth", Type.Float, () -> new AbstractTypedFunction(Type.Float, new TypedFunction.Parameter[]{new TypedFunction.Parameter(Type.Float, true), new TypedFunction.Parameter(Type.Float, false), new TypedFunction.Parameter(Type.Float, false), new TypedFunction.Parameter(Type.Float, false)}, 1, false){
            private final SmoothFloat smoothFloat = new SmoothFloat();

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[1].evaluateTo(context, functionReturn);
                float target = functionReturn.floatReturn;
                params[2].evaluateTo(context, functionReturn);
                float fadeUpTime = functionReturn.floatReturn;
                params[3].evaluateTo(context, functionReturn);
                float fadeDownTime = functionReturn.floatReturn;
                functionReturn.floatReturn = this.smoothFloat.updateAndGet(target, fadeUpTime, fadeDownTime);
            }
        });
        IrisFunctions.addImplicitCast(Type.Int, Type.Float, r -> {
            r.floatReturn = r.intReturn;
        });
        IrisFunctions.addExplicitCast(Type.Float, Type.Int, r -> {
            r.intReturn = (int)r.floatReturn;
        });
        IrisFunctions.add("between", (a, min, max) -> a >= min && a <= max);
        IrisFunctions.add("between", (a, min, max) -> a >= min && a <= max);
        IrisFunctions.add("equals", (a, b, epsilon) -> Math.abs(a - b) <= epsilon);
        length = 2;
        while (length <= 32) {
            Object[] params = new Type[length];
            Arrays.fill(params, Type.Float);
            final int finalLength = length++;
            IrisFunctions.add("in", new AbstractTypedFunction(Type.Boolean, (Type[])params){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    float value = functionReturn.floatReturn;
                    for (int i = 1; i < finalLength; ++i) {
                        params[i].evaluateTo(context, functionReturn);
                        if (functionReturn.floatReturn != value) continue;
                        functionReturn.booleanReturn = true;
                        return;
                    }
                    functionReturn.booleanReturn = false;
                }
            });
        }
        for (Type.Primitive type : new Type.Primitive[]{Type.Boolean, Type.Int}) {
            for (int size = 2; size <= 4; ++size) {
                VectorConstructor function = new VectorConstructor((Type)type, size);
                IrisFunctions.add(Character.toLowerCase(type.getClass().getSimpleName().charAt(0)) + "vec" + size, function);
            }
        }
        IrisFunctions.add("vec2", new AbstractTypedFunction(VectorType.VEC2, new Type[]{Type.Float, Type.Float}){

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                float x = functionReturn.floatReturn;
                params[1].evaluateTo(context, functionReturn);
                float y = functionReturn.floatReturn;
                functionReturn.objectReturn = new Vector2f(x, y);
            }
        });
        IrisFunctions.add("vec3", new AbstractTypedFunction(VectorType.VEC3, new Type[]{Type.Float, Type.Float, Type.Float}){

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                float x = functionReturn.floatReturn;
                params[1].evaluateTo(context, functionReturn);
                float y = functionReturn.floatReturn;
                params[2].evaluateTo(context, functionReturn);
                float z = functionReturn.floatReturn;
                functionReturn.objectReturn = new Vector3f(x, y, z);
            }
        });
        IrisFunctions.add("vec4", new AbstractTypedFunction(VectorType.VEC4, new Type[]{Type.Float, Type.Float, Type.Float, Type.Float}){

            @Override
            public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                params[0].evaluateTo(context, functionReturn);
                float x = functionReturn.floatReturn;
                params[1].evaluateTo(context, functionReturn);
                float y = functionReturn.floatReturn;
                params[2].evaluateTo(context, functionReturn);
                float z = functionReturn.floatReturn;
                params[3].evaluateTo(context, functionReturn);
                float w = functionReturn.floatReturn;
                functionReturn.objectReturn = new Vector4f(x, y, z, w);
            }
        });
        String[][] accessNames = new String[][]{{"0", "r", "x", "s"}, {"1", "g", "y", "t"}, {"2", "b", "z", "p"}, {"3", "a", "w", "q"}};
        for (String access : accessNames[0]) {
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC2}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector2f)functionReturn.objectReturn).x;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC2}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector2i)functionReturn.objectReturn).x;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC3}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector3f)functionReturn.objectReturn).x;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC3}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector3i)functionReturn.objectReturn).x;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector4f)functionReturn.objectReturn).x;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector4i)functionReturn.objectReturn).x;
                }
            });
        }
        for (String access : accessNames[1]) {
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC2}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector2f)functionReturn.objectReturn).y;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC2}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector2i)functionReturn.objectReturn).y;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC3}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector3f)functionReturn.objectReturn).y;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC3}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector3i)functionReturn.objectReturn).y;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector4f)functionReturn.objectReturn).y;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector4i)functionReturn.objectReturn).y;
                }
            });
        }
        for (String access : accessNames[2]) {
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC3}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector3f)functionReturn.objectReturn).z;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC3}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector3i)functionReturn.objectReturn).z;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector4f)functionReturn.objectReturn).z;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector4i)functionReturn.objectReturn).z;
                }
            });
        }
        for (String access : accessNames[3]) {
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Float, new Type[]{VectorType.VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.floatReturn = ((Vector4f)functionReturn.objectReturn).w;
                }
            });
            IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(Type.Int, new Type[]{VectorType.I_VEC4}){

                @Override
                public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                    params[0].evaluateTo(context, functionReturn);
                    functionReturn.intReturn = ((Vector4i)functionReturn.objectReturn).w;
                }
            });
        }
        for (int i = 0; i < 4; ++i) {
            for (String access : accessNames[i]) {
                final int finalI = i;
                IrisFunctions.add("<access$" + access + ">", new AbstractTypedFunction(VectorType.VEC4, new Type[]{MatrixType.MAT4}){

                    @Override
                    public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
                        params[0].evaluateTo(context, functionReturn);
                        functionReturn.objectReturn = ((Matrix4f)functionReturn.objectReturn).getColumn(finalI, new Vector4f());
                    }
                });
            }
        }
        functions = builder.build();
    }

    static interface TriConsumer<T, U, V> {
        public void accept(T var1, U var2, V var3);
    }

    static interface QuadConsumer<T, U, V, W> {
        public void accept(T var1, U var2, V var3, W var4);
    }

    static interface ObjectObject2BooleanFunction<T, U> {
        public boolean apply(T var1, U var2);
    }
}

