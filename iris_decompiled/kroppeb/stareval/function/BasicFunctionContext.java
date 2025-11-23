/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package kroppeb.stareval.function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.function.FunctionContext;

public class BasicFunctionContext
implements FunctionContext {
    private final Map<String, Expression> variables = new Object2ObjectOpenHashMap();

    public void setVariable(String name, Expression value) {
        this.variables.put(name, value);
    }

    public void setIntVariable(String name, int value) {
        this.setVariable(name, (c, r) -> {
            r.intReturn = value;
        });
    }

    public void setFloatVariable(String name, float value) {
        this.setVariable(name, (c, r) -> {
            r.floatReturn = value;
        });
    }

    @Override
    public Expression getVariable(String name) {
        Expression expression = this.variables.get(name);
        if (expression == null) {
            throw new RuntimeException("Variable hasn't been set: " + name);
        }
        return expression;
    }

    @Override
    public boolean hasVariable(String name) {
        return this.variables.containsKey(name);
    }
}

