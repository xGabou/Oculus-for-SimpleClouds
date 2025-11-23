/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.expression;

import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;

public class BasicVariableExpression
implements VariableExpression {
    private final String name;
    private final Type type;

    public BasicVariableExpression(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void evaluateTo(FunctionContext c, FunctionReturn r) {
        c.getVariable(this.name).evaluateTo(c, r);
    }

    @Override
    public Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
        if (context.hasVariable(this.name)) {
            context.getVariable(this.name).evaluateTo(context, functionReturn);
            return this.type.createConstant(functionReturn);
        }
        return this;
    }
}

