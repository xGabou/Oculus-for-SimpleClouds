/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.atn.ATNConfigSet;
import oculus.org.antlr.v4.runtime.atn.DecisionEventInfo;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;

public class PredicateEvalInfo
extends DecisionEventInfo {
    public final SemanticContext semctx;
    public final int predictedAlt;
    public final boolean evalResult;

    public PredicateEvalInfo(int decision, TokenStream input, int startIndex, int stopIndex, SemanticContext semctx, boolean evalResult, int predictedAlt, boolean fullCtx) {
        super(decision, new ATNConfigSet(), input, startIndex, stopIndex, fullCtx);
        this.semctx = semctx;
        this.evalResult = evalResult;
        this.predictedAlt = predictedAlt;
    }
}

