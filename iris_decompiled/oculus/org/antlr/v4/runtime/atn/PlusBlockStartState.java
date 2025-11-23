/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.BlockStartState;
import oculus.org.antlr.v4.runtime.atn.PlusLoopbackState;

public final class PlusBlockStartState
extends BlockStartState {
    public PlusLoopbackState loopBackState;

    @Override
    public int getStateType() {
        return 4;
    }
}

