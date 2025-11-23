/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.element.token;

import kroppeb.stareval.element.token.Token;
import kroppeb.stareval.parser.BinaryOp;

public class BinaryOperatorToken
extends Token {
    public final BinaryOp op;

    public BinaryOperatorToken(BinaryOp op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return "BinaryOp{" + this.op + "}";
    }
}

