/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.BitSet;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.atn.ATNConfigSet;
import oculus.org.antlr.v4.runtime.atn.DecisionEventInfo;

public class AmbiguityInfo
extends DecisionEventInfo {
    public BitSet ambigAlts;

    public AmbiguityInfo(int decision, ATNConfigSet configs, BitSet ambigAlts, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
        super(decision, configs, input, startIndex, stopIndex, fullCtx);
        this.ambigAlts = ambigAlts;
    }
}

