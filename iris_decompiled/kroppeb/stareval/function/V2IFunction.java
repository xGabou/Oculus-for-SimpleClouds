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
public interface V2IFunction
extends TypedFunction {
    public int eval();

    @Override
    default public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
        functionReturn.intReturn = this.eval();
    }

    @Override
    default public Type getReturnType() {
        return Type.Int;
    }

    @Override
    default public TypedFunction.Parameter[] getParameters() {
        return new TypedFunction.Parameter[0];
    }
}

