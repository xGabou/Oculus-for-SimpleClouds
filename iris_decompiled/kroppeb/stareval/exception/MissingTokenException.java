/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.exception;

import kroppeb.stareval.exception.ParseException;

public class MissingTokenException
extends ParseException {
    public MissingTokenException(String message, int index) {
        super(message + " at index " + index);
    }
}

