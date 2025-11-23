/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.expression;

import java.util.Collection;
import kroppeb.stareval.expression.ConstantExpression;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.TypedFunction;

public class CallExpression
implements Expression {
    private final TypedFunction function;
    private final Expression[] arguments;

    public CallExpression(TypedFunction function, Expression[] arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public void evaluateTo(FunctionContext context, FunctionReturn functionReturn) {
        this.function.evaluateTo(this.arguments, context, functionReturn);
    }

    @Override
    public void listVariables(Collection<? super VariableExpression> variables) {
        for (Expression argument : this.arguments) {
            argument.listVariables(variables);
        }
    }

    private boolean isConstant() {
        for (Expression i : this.arguments) {
            if (i instanceof ConstantExpression) continue;
            return false;
        }
        return true;
    }

    @Override
    public Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
        if (this.function.isPure() && this.isConstant()) {
            this.evaluateTo(context, functionReturn);
            return this.function.getReturnType().createConstant(functionReturn);
        }
        Expression[] partialEvaluatedParams = new Expression[this.arguments.length];
        boolean allFullySimplified = true;
        boolean noneSimplified = true;
        for (int i = 0; i < this.arguments.length; ++i) {
            Expression simplified = this.arguments[i].partialEval(context, functionReturn);
            if (simplified instanceof ConstantExpression) {
                noneSimplified = false;
            } else {
                allFullySimplified = false;
                if (simplified != this.arguments[i]) {
                    noneSimplified = false;
                }
            }
            partialEvaluatedParams[i] = simplified;
        }
        if (this.function.isPure() && allFullySimplified) {
            this.function.evaluateTo(partialEvaluatedParams, context, functionReturn);
            return this.function.getReturnType().createConstant(functionReturn);
        }
        if (noneSimplified) {
            return this;
        }
        return new CallExpression(this.function, partialEvaluatedParams);
    }
}

