/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.parsing;

import java.util.Collection;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import kroppeb.stareval.function.TypedFunction;
import net.irisshaders.iris.parsing.VectorType;

public class BooleanVectorizedFunction
implements TypedFunction {
    final TypedFunction inner;
    final int size;
    final TypedFunction.Parameter[] parameters;
    private final ElementAccessExpression[] vectorAccessors;
    private int index;

    public BooleanVectorizedFunction(TypedFunction inner, int size) {
        this.inner = inner;
        this.size = size;
        TypedFunction.Parameter[] innerTypes = inner.getParameters();
        this.parameters = new TypedFunction.Parameter[innerTypes.length];
        this.vectorAccessors = new ElementAccessExpression[innerTypes.length];
        for (int i = 0; i < innerTypes.length; ++i) {
            this.parameters[i] = new TypedFunction.Parameter(VectorType.of((Type.Primitive)innerTypes[i].type(), size));
            this.vectorAccessors[i] = new ElementAccessExpression(innerTypes[i].type());
        }
    }

    @Override
    public Type getReturnType() {
        return Type.Boolean;
    }

    @Override
    public TypedFunction.Parameter[] getParameters() {
        return this.parameters;
    }

    @Override
    public void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
        for (int p = 0; p < params.length; ++p) {
            Expression param = params[p];
            param.evaluateTo(context, functionReturn);
            this.vectorAccessors[p].vector = functionReturn.objectReturn;
        }
        int i = 0;
        while (i < this.size) {
            this.index = i++;
            this.inner.evaluateTo(this.vectorAccessors, context, functionReturn);
            if (functionReturn.booleanReturn) continue;
            return;
        }
    }

    class ElementAccessExpression
    implements Expression {
        final Type parameterType;
        Object vector;

        ElementAccessExpression(Type parameterType) {
            this.parameterType = parameterType;
        }

        @Override
        public void evaluateTo(FunctionContext context, FunctionReturn functionReturn) {
            this.parameterType.getValueFromArray(this.vector, BooleanVectorizedFunction.this.index, functionReturn);
        }

        @Override
        public void listVariables(Collection<? super VariableExpression> variables) {
            throw new IllegalStateException();
        }
    }
}

