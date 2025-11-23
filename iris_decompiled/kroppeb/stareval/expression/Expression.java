/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.expression;

import java.util.Collection;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;

public interface Expression {
    public void evaluateTo(FunctionContext var1, FunctionReturn var2);

    default public Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
        return this;
    }

    public void listVariables(Collection<? super VariableExpression> var1);
}

