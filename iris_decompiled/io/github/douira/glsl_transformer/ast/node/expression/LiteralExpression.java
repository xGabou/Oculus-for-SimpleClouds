/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.expression;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.TerminalExpression;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import io.github.douira.glsl_transformer.util.Type;
import java.util.Objects;

public class LiteralExpression
extends TerminalExpression {
    private Type literalType;
    private boolean booleanValue;
    private long integerValue;
    private IntegerFormat integerFormat;
    private double floatingValue;

    private LiteralExpression(Type literalType, boolean booleanValue, long integerValue, IntegerFormat integerFormat, double floatingValue) {
        this.literalType = literalType;
        this.booleanValue = booleanValue;
        this.integerValue = integerValue;
        this.integerFormat = integerFormat;
        this.floatingValue = floatingValue;
    }

    public LiteralExpression(boolean booleanValue) {
        this.setBoolean(booleanValue);
    }

    public LiteralExpression(Type literalType, long integerValue) {
        this.setInteger(literalType, integerValue);
    }

    public LiteralExpression(Type literalType, long integerValue, IntegerFormat integerFormat) {
        this.setInteger(literalType, integerValue, integerFormat);
    }

    public LiteralExpression(Type literalType, double floatingValue) {
        this.setFloating(literalType, floatingValue);
    }

    private void validateLiteralType(Type type) {
        if (type == null) {
            throw new NullPointerException("Literal type cannot be null!");
        }
        if (!type.isScalar()) {
            throw new IllegalArgumentException("Literal type must be a scalar!");
        }
    }

    public Number getNumber() {
        int bitDepth = this.literalType.getBitDepth();
        switch (this.getNumberType()) {
            case BOOLEAN: {
                return this.booleanValue ? 1 : 0;
            }
            case SIGNED_INTEGER: 
            case UNSIGNED_INTEGER: {
                switch (bitDepth) {
                    case 8: {
                        return (byte)this.integerValue;
                    }
                    case 16: {
                        return (short)this.integerValue;
                    }
                    case 32: {
                        return (int)this.integerValue;
                    }
                    case 64: {
                        return this.integerValue;
                    }
                }
                throw new IllegalArgumentException("Unsupported bit depth: " + bitDepth);
            }
            case FLOATING_POINT: {
                if (bitDepth == 64) {
                    return this.floatingValue;
                }
                return Float.valueOf((float)this.floatingValue);
            }
        }
        throw new IllegalArgumentException("Unsupported number type: " + this.getNumberType());
    }

    public Type getType() {
        return this.literalType;
    }

    public Type.NumberType getNumberType() {
        return this.literalType.getNumberType();
    }

    public boolean getBoolean() {
        return this.booleanValue;
    }

    public void setBoolean(boolean booleanValue) {
        this.booleanValue = booleanValue;
        this.integerFormat = null;
        this.integerValue = 0L;
        this.floatingValue = 0.0;
        this.literalType = Type.BOOL;
    }

    public void changeBoolean(boolean booleanValue) {
        if (!this.isBoolean()) {
            throw new IllegalStateException("Literal type must be a boolean!");
        }
        this.booleanValue = booleanValue;
    }

    public long getInteger() {
        return this.integerValue;
    }

    public void setInteger(Type integerType, long integerValue, IntegerFormat integerFormat) {
        Objects.requireNonNull(integerFormat, "Integer format cannot be null!");
        this.validateLiteralType(integerType);
        Type.NumberType numberType = integerType.getNumberType();
        if (numberType != Type.NumberType.SIGNED_INTEGER && numberType != Type.NumberType.UNSIGNED_INTEGER) {
            throw new IllegalArgumentException("Literal type must be an integer!");
        }
        this.integerValue = integerValue;
        this.booleanValue = false;
        this.integerFormat = integerFormat;
        this.floatingValue = 0.0;
        this.literalType = integerType;
    }

    public void setInteger(Type integerType, long integerValue) {
        this.setInteger(integerType, integerValue, IntegerFormat.DECIMAL);
    }

    public void setInteger(int integerValue) {
        this.setInteger(Type.INT32, integerValue);
    }

    public void changeInteger(long integerValue) {
        if (!this.isInteger()) {
            throw new IllegalStateException("Literal type must be an integer!");
        }
        this.integerValue = integerValue;
    }

    public IntegerFormat getIntegerFormat() {
        return this.integerFormat;
    }

    public int getIntegerRadix() {
        return this.integerFormat.radix;
    }

    public void setIntegerFormat(IntegerFormat integerFormat) {
        if (!this.isInteger()) {
            throw new IllegalStateException("Literal type must be an integer!");
        }
        this.integerFormat = integerFormat;
    }

    public double getFloating() {
        return this.floatingValue;
    }

    public void setFloating(Type floatingType, double floatingValue) {
        this.validateLiteralType(floatingType);
        if (floatingType.getNumberType() != Type.NumberType.FLOATING_POINT) {
            throw new IllegalArgumentException("Literal type must be a floating point!");
        }
        this.floatingValue = floatingValue;
        this.booleanValue = false;
        this.integerValue = 0L;
        this.integerFormat = null;
        this.literalType = floatingType;
    }

    public void setFloating(float floatingValue) {
        this.setFloating(Type.FLOAT32, floatingValue);
    }

    public void changeFloating(double floatingValue) {
        if (!this.isFloatingPoint()) {
            throw new IllegalStateException("Literal type must be a floating point!");
        }
        this.floatingValue = floatingValue;
    }

    public boolean isBoolean() {
        return this.getNumberType() == Type.NumberType.BOOLEAN;
    }

    public boolean isInteger() {
        return this.getNumberType() == Type.NumberType.SIGNED_INTEGER || this.getNumberType() == Type.NumberType.UNSIGNED_INTEGER;
    }

    public boolean isFloatingPoint() {
        return this.getNumberType() == Type.NumberType.FLOATING_POINT;
    }

    public boolean isPositive() {
        switch (this.getNumberType()) {
            case BOOLEAN: {
                return this.booleanValue;
            }
            case SIGNED_INTEGER: 
            case UNSIGNED_INTEGER: {
                return this.integerValue > 0L;
            }
            case FLOATING_POINT: {
                return this.floatingValue > 0.0;
            }
        }
        throw new IllegalArgumentException("Unsupported number type: " + this.getNumberType());
    }

    public boolean isNonZero() {
        switch (this.getNumberType()) {
            case BOOLEAN: {
                return true;
            }
            case SIGNED_INTEGER: 
            case UNSIGNED_INTEGER: {
                return this.integerValue != 0L;
            }
            case FLOATING_POINT: {
                return this.floatingValue != 0.0;
            }
        }
        throw new IllegalArgumentException("Unsupported number type: " + this.getNumberType());
    }

    public static LiteralExpression getDefaultValue(Type.NumberType numberType) {
        switch (numberType) {
            case BOOLEAN: {
                return new LiteralExpression(false);
            }
            case SIGNED_INTEGER: 
            case UNSIGNED_INTEGER: {
                return new LiteralExpression(Type.INT32, 0L);
            }
            case FLOATING_POINT: {
                return new LiteralExpression(Type.FLOAT32, 0.0);
            }
        }
        throw new IllegalArgumentException("Unsupported literal type: " + numberType);
    }

    public static LiteralExpression getDefaultValue(Type type) {
        return LiteralExpression.getDefaultValue(type.getNumberType());
    }

    @Override
    public Expression.ExpressionType getExpressionType() {
        return Expression.ExpressionType.LITERAL;
    }

    @Override
    public <R> R expressionAccept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        super.enterNode(listener);
        listener.enterLiteralExpression(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        super.exitNode(listener);
        listener.exitLiteralExpression(this);
    }

    @Override
    public LiteralExpression clone() {
        return new LiteralExpression(this.literalType, this.booleanValue, this.integerValue, this.integerFormat, this.floatingValue);
    }

    @Override
    public LiteralExpression cloneInto(Root root) {
        return (LiteralExpression)super.cloneInto(root);
    }

    public static enum IntegerFormat {
        DECIMAL(10),
        HEXADECIMAL(16),
        OCTAL(8);

        public final int radix;

        private IntegerFormat(int radix) {
            this.radix = radix;
        }
    }
}

