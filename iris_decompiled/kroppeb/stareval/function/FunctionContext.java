/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.function;

import kroppeb.stareval.expression.Expression;

public interface FunctionContext {
    public Expression getVariable(String var1);

    public boolean hasVariable(String var1);
}

