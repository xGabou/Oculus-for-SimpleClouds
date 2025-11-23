/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.function;

import java.util.Arrays;
import java.util.stream.Collectors;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;

public interface TypedFunction {
    public static String format(TypedFunction function, String name) {
        return String.format("%s %s(%s) (priority: %d, pure:%s)", function.getReturnType().toString(), name, Arrays.stream(function.getParameters()).map(param -> param.constant() ? "const " + param.type() : param.type().toString()).collect(Collectors.joining(", ")), function.priority(), function.isPure() ? "yes" : "no");
    }

    public Type getReturnType();

    public Parameter[] getParameters();

    public void evaluateTo(Expression[] var1, FunctionContext var2, FunctionReturn var3);

    default public boolean isPure() {
        return true;
    }

    default public int priority() {
        return 0;
    }

    public static class Parameter {
        private final Type type;
        private final boolean isConstant;

        public Parameter(Type type, boolean isConstant) {
            this.type = type;
            this.isConstant = isConstant;
        }

        public Parameter(Type type) {
            this(type, false);
        }

        public Type type() {
            return this.type;
        }

        public boolean constant() {
            return this.isConstant;
        }
    }
}

