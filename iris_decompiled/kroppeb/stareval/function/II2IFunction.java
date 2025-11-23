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
public interface II2IFunction
extends TypedFunction {
    public int eval(int var1, int var2);

    @Override
    default public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
        params[0].evaluateTo(context, functionReturn);
        int a = functionReturn.intReturn;
        params[1].evaluateTo(context, functionReturn);
        int b = functionReturn.intReturn;
        functionReturn.intReturn = this.eval(a, b);
    }

    @Override
    default public Type getReturnType() {
        return Type.Int;
    }

    @Override
    default public TypedFunction.Parameter[] getParameters() {
        return new TypedFunction.Parameter[]{Type.IntParameter, Type.IntParameter};
    }
}

