/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.function;

import java.util.Arrays;
import kroppeb.stareval.function.Type;
import kroppeb.stareval.function.TypedFunction;

public abstract class AbstractTypedFunction
implements TypedFunction {
    private final Type returnType;
    private final TypedFunction.Parameter[] parameters;
    private final int priority;
    private final boolean isPure;

    public AbstractTypedFunction(Type returnType, TypedFunction.Parameter[] parameters, int priority, boolean isPure) {
        this.returnType = returnType;
        this.parameters = parameters;
        this.priority = priority;
        this.isPure = isPure;
    }

    public AbstractTypedFunction(Type returnType, Type[] parameterType) {
        this.returnType = returnType;
        this.parameters = (TypedFunction.Parameter[])Arrays.stream(parameterType).map(TypedFunction.Parameter::new).toArray(TypedFunction.Parameter[]::new);
        this.priority = 0;
        this.isPure = true;
    }

    @Override
    public Type getReturnType() {
        return this.returnType;
    }

    @Override
    public TypedFunction.Parameter[] getParameters() {
        return this.parameters;
    }

    @Override
    public boolean isPure() {
        return this.isPure;
    }

    @Override
    public int priority() {
        return this.priority;
    }
}

