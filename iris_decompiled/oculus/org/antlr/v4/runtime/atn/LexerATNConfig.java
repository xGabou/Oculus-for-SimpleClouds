/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNConfig;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.DecisionState;
import oculus.org.antlr.v4.runtime.atn.LexerActionExecutor;
import oculus.org.antlr.v4.runtime.atn.PredictionContext;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;
import oculus.org.antlr.v4.runtime.misc.ObjectEqualityComparator;

public class LexerATNConfig
extends ATNConfig {
    private final LexerActionExecutor lexerActionExecutor;
    private final boolean passedThroughNonGreedyDecision;

    public LexerATNConfig(ATNState state, int alt, PredictionContext context) {
        super(state, alt, context, (SemanticContext)SemanticContext.Empty.Instance);
        this.passedThroughNonGreedyDecision = false;
        this.lexerActionExecutor = null;
    }

    public LexerATNConfig(ATNState state, int alt, PredictionContext context, LexerActionExecutor lexerActionExecutor) {
        super(state, alt, context, (SemanticContext)SemanticContext.Empty.Instance);
        this.lexerActionExecutor = lexerActionExecutor;
        this.passedThroughNonGreedyDecision = false;
    }

    public LexerATNConfig(LexerATNConfig c, ATNState state) {
        super(c, state, c.context, c.semanticContext);
        this.lexerActionExecutor = c.lexerActionExecutor;
        this.passedThroughNonGreedyDecision = LexerATNConfig.checkNonGreedyDecision(c, state);
    }

    public LexerATNConfig(LexerATNConfig c, ATNState state, LexerActionExecutor lexerActionExecutor) {
        super(c, state, c.context, c.semanticContext);
        this.lexerActionExecutor = lexerActionExecutor;
        this.passedThroughNonGreedyDecision = LexerATNConfig.checkNonGreedyDecision(c, state);
    }

    public LexerATNConfig(LexerATNConfig c, ATNState state, PredictionContext context) {
        super(c, state, context, c.semanticContext);
        this.lexerActionExecutor = c.lexerActionExecutor;
        this.passedThroughNonGreedyDecision = LexerATNConfig.checkNonGreedyDecision(c, state);
    }

    public final LexerActionExecutor getLexerActionExecutor() {
        return this.lexerActionExecutor;
    }

    public final boolean hasPassedThroughNonGreedyDecision() {
        return this.passedThroughNonGreedyDecision;
    }

    @Override
    public int hashCode() {
        int hashCode = MurmurHash.initialize(7);
        hashCode = MurmurHash.update(hashCode, this.state.stateNumber);
        hashCode = MurmurHash.update(hashCode, this.alt);
        hashCode = MurmurHash.update(hashCode, this.context);
        hashCode = MurmurHash.update(hashCode, this.semanticContext);
        hashCode = MurmurHash.update(hashCode, this.passedThroughNonGreedyDecision ? 1 : 0);
        hashCode = MurmurHash.update(hashCode, this.lexerActionExecutor);
        hashCode = MurmurHash.finish(hashCode, 6);
        return hashCode;
    }

    @Override
    public boolean equals(ATNConfig other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LexerATNConfig)) {
            return false;
        }
        LexerATNConfig lexerOther = (LexerATNConfig)other;
        if (this.passedThroughNonGreedyDecision != lexerOther.passedThroughNonGreedyDecision) {
            return false;
        }
        if (!ObjectEqualityComparator.INSTANCE.equals(this.lexerActionExecutor, lexerOther.lexerActionExecutor)) {
            return false;
        }
        return super.equals(other);
    }

    private static boolean checkNonGreedyDecision(LexerATNConfig source, ATNState target) {
        return source.passedThroughNonGreedyDecision || target instanceof DecisionState && ((DecisionState)target).nonGreedy;
    }
}

