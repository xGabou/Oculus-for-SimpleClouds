/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.StarLoopEntryState;

public final class StarLoopbackState
extends ATNState {
    public final StarLoopEntryState getLoopEntryState() {
        return (StarLoopEntryState)this.transition((int)0).target;
    }

    @Override
    public int getStateType() {
        return 9;
    }
}

