/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.AtomTransition;
import oculus.org.antlr.v4.runtime.atn.RangeTransition;
import oculus.org.antlr.v4.runtime.atn.Transition;

public abstract class CodePointTransitions {
    public static Transition createWithCodePoint(ATNState target, int codePoint) {
        return CodePointTransitions.createWithCodePointRange(target, codePoint, codePoint);
    }

    public static Transition createWithCodePointRange(ATNState target, int codePointFrom, int codePointTo) {
        return codePointFrom == codePointTo ? new AtomTransition(target, codePointFrom) : new RangeTransition(target, codePointFrom, codePointTo);
    }
}

