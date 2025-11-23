/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;

public abstract class DecisionState
extends ATNState {
    public int decision = -1;
    public boolean nonGreedy;
}

