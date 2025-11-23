/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.function;

import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import kroppeb.stareval.function.TypedFunction;

@FunctionalInterface
public interface FF2FFunction
extends TypedFunction {
    public float eval(float var1, float var2);

    @Override
    default public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
        params[0].evaluateTo(context, functionReturn);
        float a = functionReturn.floatReturn;
        params[1].evaluateTo(context, functionReturn);
        float b = functionReturn.floatReturn;
        functionReturn.floatReturn = this.eval(a, b);
    }

    @Override
    default public Type getReturnType() {
        return Type.Float;
    }

    @Override
    default public TypedFunction.Parameter[] getParameters() {
        return new TypedFunction.Parameter[]{Type.FloatParameter, Type.FloatParameter};
    }
}

