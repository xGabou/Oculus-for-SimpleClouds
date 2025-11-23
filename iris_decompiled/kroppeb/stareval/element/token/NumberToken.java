/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.element.token;

import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.element.token.Token;

public class NumberToken
extends Token
implements ExpressionElement {
    private final String number;

    public NumberToken(String number) {
        this.number = number;
    }

    public String getNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return "Number{" + this.number + "}";
    }
}

