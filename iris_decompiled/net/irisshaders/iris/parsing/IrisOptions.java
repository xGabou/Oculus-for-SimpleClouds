/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.parsing;

import kroppeb.stareval.parser.BinaryOp;
import kroppeb.stareval.parser.ParserOptions;
import kroppeb.stareval.parser.UnaryOp;

public class IrisOptions {
    public static final ParserOptions options;
    static final BinaryOp Multiply;
    static final BinaryOp Divide;
    static final BinaryOp Remainder;
    static final BinaryOp Add;
    static final BinaryOp Subtract;
    static final BinaryOp Equals;
    static final BinaryOp NotEquals;
    static final BinaryOp LessThan;
    static final BinaryOp MoreThan;
    static final BinaryOp LessThanOrEquals;
    static final BinaryOp MoreThanOrEquals;
    static final BinaryOp And;
    static final BinaryOp Or;
    static final UnaryOp Not;
    static final UnaryOp Negate;

    static {
        Multiply = new BinaryOp("multiply", 0);
        Divide = new BinaryOp("divide", 0);
        Remainder = new BinaryOp("remainder", 0);
        Add = new BinaryOp("add", 1);
        Subtract = new BinaryOp("subtract", 1);
        Equals = new BinaryOp("equals", 2);
        NotEquals = new BinaryOp("notEquals", 2);
        LessThan = new BinaryOp("lessThan", 2);
        MoreThan = new BinaryOp("moreThan", 2);
        LessThanOrEquals = new BinaryOp("lessThanOrEquals", 2);
        MoreThanOrEquals = new BinaryOp("moreThanOrEquals", 2);
        And = new BinaryOp("and", 3);
        Or = new BinaryOp("or", 3);
        Not = new UnaryOp("not");
        Negate = new UnaryOp("negate");
        ParserOptions.Builder builder = new ParserOptions.Builder();
        builder.addBinaryOp("*", Multiply);
        builder.addBinaryOp("/", Divide);
        builder.addBinaryOp("%", Remainder);
        builder.addBinaryOp("+", Add);
        builder.addBinaryOp("-", Subtract);
        builder.addBinaryOp("==", Equals);
        builder.addBinaryOp("!=", NotEquals);
        builder.addBinaryOp("<", LessThan);
        builder.addBinaryOp(">", MoreThan);
        builder.addBinaryOp("<=", LessThanOrEquals);
        builder.addBinaryOp(">=", MoreThanOrEquals);
        builder.addBinaryOp("\u2260", NotEquals);
        builder.addBinaryOp("\u2264", LessThanOrEquals);
        builder.addBinaryOp("\u2265", MoreThanOrEquals);
        builder.addBinaryOp("&&", And);
        builder.addBinaryOp("||", Or);
        builder.addUnaryOp("!", Not);
        builder.addUnaryOp("-", Negate);
        options = builder.build();
    }
}

