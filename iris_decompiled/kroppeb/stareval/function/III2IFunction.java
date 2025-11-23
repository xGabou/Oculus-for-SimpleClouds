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
public interface III2IFunction
extends TypedFunction {
    public int eval(int var1, int var2, int var3);

    @Override
    default public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
        params[0].evaluateTo(context, functionReturn);
        int a = functionReturn.intReturn;
        params[1].evaluateTo(context, functionReturn);
        int b = functionReturn.intReturn;
        params[2].evaluateTo(context, functionReturn);
        int c = functionReturn.intReturn;
        functionReturn.intReturn = this.eval(a, b, c);
    }

    @Override
    default public Type getReturnType() {
        return Type.Int;
    }

    @Override
    default public TypedFunction.Parameter[] getParameters() {
        return new TypedFunction.Parameter[]{Type.IntParameter, Type.IntParameter, Type.IntParameter};
    }
}

