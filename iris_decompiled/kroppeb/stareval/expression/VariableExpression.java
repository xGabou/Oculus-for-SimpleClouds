/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.expression;

import java.util.Collection;
import kroppeb.stareval.expression.Expression;

public interface VariableExpression
extends Expression {
    @Override
    default public void listVariables(Collection<? super VariableExpression> variables) {
        variables.add(this);
    }
}

