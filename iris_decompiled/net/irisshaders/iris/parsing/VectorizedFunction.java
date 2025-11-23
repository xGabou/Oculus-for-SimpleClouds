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

public class VectorizedFunction
implements TypedFunction {
    final TypedFunction inner;
    final int size;
    final VectorType.ArrayVector returnType;
    final TypedFunction.Parameter[] parameters;
    private final ElementAccessExpression[] vectorAccessors;
    private int index;
    private final VectorType.ArrayVector.IntObjectObjectObjectConsumer<VectorizedFunction, FunctionContext, FunctionReturn> mapper = (i, self, ctx, fr) -> {
        self.index = i;
        self.inner.evaluateTo(self.vectorAccessors, (FunctionContext)ctx, (FunctionReturn)fr);
    };

    public VectorizedFunction(TypedFunction inner, int size) {
        this.inner = inner;
        this.size = size;
        this.returnType = new VectorType.ArrayVector(inner.getReturnType(), size);
        TypedFunction.Parameter[] innerTypes = inner.getParameters();
        this.parameters = new TypedFunction.Parameter[innerTypes.length];
        this.vectorAccessors = new ElementAccessExpression[innerTypes.length];
        for (int i2 = 0; i2 < innerTypes.length; ++i2) {
            this.parameters[i2] = new TypedFunction.Parameter(new VectorType.ArrayVector(innerTypes[i2].type(), size));
            this.vectorAccessors[i2] = new ElementAccessExpression(innerTypes[i2].type());
        }
    }

    @Override
    public Type getReturnType() {
        return this.returnType;
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
        this.returnType.map(this, context, functionReturn, this.mapper);
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
            this.parameterType.getValueFromArray(this.vector, VectorizedFunction.this.index, functionReturn);
        }

        @Override
        public void listVariables(Collection<? super VariableExpression> variables) {
            throw new IllegalStateException();
        }
    }
}

