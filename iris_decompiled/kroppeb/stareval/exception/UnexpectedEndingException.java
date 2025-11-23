/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.exception;

import kroppeb.stareval.exception.ParseException;

public class UnexpectedEndingException
extends ParseException {
    public UnexpectedEndingException() {
        this("Expected to read more text, but the string has ended");
    }

    public UnexpectedEndingException(String message) {
        super(message);
    }
}

