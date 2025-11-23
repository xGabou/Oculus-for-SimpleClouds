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
public interface FFF2BFunction
extends TypedFunction {
    public boolean eval(float var1, float var2, float var3);

    @Override
    default public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
        params[0].evaluateTo(context, functionReturn);
        float a = functionReturn.floatReturn;
        params[1].evaluateTo(context, functionReturn);
        float b = functionReturn.floatReturn;
        params[2].evaluateTo(context, functionReturn);
        float c = functionReturn.floatReturn;
        functionReturn.booleanReturn = this.eval(a, b, c);
    }

    @Override
    default public Type getReturnType() {
        return Type.Boolean;
    }

    @Override
    default public TypedFunction.Parameter[] getParameters() {
        return new TypedFunction.Parameter[]{Type.FloatParameter, Type.FloatParameter, Type.FloatParameter};
    }
}

