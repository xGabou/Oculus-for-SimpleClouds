/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.expression;

import java.util.Collection;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.Type;

public abstract class ConstantExpression
implements Expression {
    private final Type type;

    protected ConstantExpression(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public void listVariables(Collection<? super VariableExpression> variables) {
    }
}

