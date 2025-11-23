/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.atn.ATNConfigSet;
import oculus.org.antlr.v4.runtime.atn.DecisionEventInfo;

public class ErrorInfo
extends DecisionEventInfo {
    public ErrorInfo(int decision, ATNConfigSet configs, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
        super(decision, configs, input, startIndex, stopIndex, fullCtx);
    }
}

