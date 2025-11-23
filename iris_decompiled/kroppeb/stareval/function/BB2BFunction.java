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
public interface BB2BFunction
extends TypedFunction {
    public boolean eval(boolean var1, boolean var2);

    @Override
    default public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
        params[0].evaluateTo(context, functionReturn);
        boolean a = functionReturn.booleanReturn;
        params[1].evaluateTo(context, functionReturn);
        boolean b = functionReturn.booleanReturn;
        functionReturn.booleanReturn = this.eval(a, b);
    }

    @Override
    default public Type getReturnType() {
        return Type.Boolean;
    }

    @Override
    default public TypedFunction.Parameter[] getParameters() {
        return new TypedFunction.Parameter[]{Type.BooleanParameter, Type.BooleanParameter};
    }
}

