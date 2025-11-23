/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.Objects;
import oculus.org.antlr.v4.runtime.Recognizer;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.PredictionContext;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public class ATNConfig {
    private static final int SUPPRESS_PRECEDENCE_FILTER = 0x40000000;
    public final ATNState state;
    public final int alt;
    public PredictionContext context;
    public int reachesIntoOuterContext;
    public final SemanticContext semanticContext;

    public ATNConfig(ATNConfig old) {
        this.state = old.state;
        this.alt = old.alt;
        this.context = old.context;
        this.semanticContext = old.semanticContext;
        this.reachesIntoOuterContext = old.reachesIntoOuterContext;
    }

    public ATNConfig(ATNState state, int alt, PredictionContext context) {
        this(state, alt, context, (SemanticContext)SemanticContext.Empty.Instance);
    }

    public ATNConfig(ATNState state, int alt, PredictionContext context, SemanticContext semanticContext) {
        this.state = state;
        this.alt = alt;
        this.context = context;
        this.semanticContext = semanticContext;
    }

    public ATNConfig(ATNConfig c, ATNState state) {
        this(c, state, c.context, c.semanticContext);
    }

    public ATNConfig(ATNConfig c, ATNState state, SemanticContext semanticContext) {
        this(c, state, c.context, semanticContext);
    }

    public ATNConfig(ATNConfig c, SemanticContext semanticContext) {
        this(c, c.state, c.context, semanticContext);
    }

    public ATNConfig(ATNConfig c, ATNState state, PredictionContext context) {
        this(c, state, context, c.semanticContext);
    }

    public ATNConfig(ATNConfig c, ATNState state, PredictionContext context, SemanticContext semanticContext) {
        this.state = state;
        this.alt = c.alt;
        this.context = context;
        this.semanticContext = semanticContext;
        this.reachesIntoOuterContext = c.reachesIntoOuterContext;
    }

    public final int getOuterContextDepth() {
        return this.reachesIntoOuterContext & 0xBFFFFFFF;
    }

    public final boolean isPrecedenceFilterSuppressed() {
        return (this.reachesIntoOuterContext & 0x40000000) != 0;
    }

    public final void setPrecedenceFilterSuppressed(boolean value) {
        this.reachesIntoOuterContext = value ? (this.reachesIntoOuterContext |= 0x40000000) : (this.reachesIntoOuterContext &= 0xBFFFFFFF);
    }

    public boolean equals(Object o) {
        if (!(o instanceof ATNConfig)) {
            return false;
        }
        return this.equals((ATNConfig)o);
    }

    public boolean equals(ATNConfig other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return this.state.stateNumber == other.state.stateNumber && this.alt == other.alt && Objects.equals(this.context, other.context) && this.semanticContext.equals(other.semanticContext) && this.isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed();
    }

    public int hashCode() {
        int hashCode = MurmurHash.initialize(7);
        hashCode = MurmurHash.update(hashCode, this.state.stateNumber);
        hashCode = MurmurHash.update(hashCode, this.alt);
        hashCode = MurmurHash.update(hashCode, this.context);
        hashCode = MurmurHash.update(hashCode, this.semanticContext);
        hashCode = MurmurHash.finish(hashCode, 4);
        return hashCode;
    }

    public String toString() {
        return this.toString(null, true);
    }

    public String toString(Recognizer<?, ?> recog, boolean showAlt) {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        buf.append(this.state);
        if (showAlt) {
            buf.append(",");
            buf.append(this.alt);
        }
        if (this.context != null) {
            buf.append(",[");
            buf.append(this.context.toString());
            buf.append("]");
        }
        if (this.semanticContext != null && this.semanticContext != SemanticContext.Empty.Instance) {
            buf.append(",");
            buf.append(this.semanticContext);
        }
        if (this.getOuterContextDepth() > 0) {
            buf.append(",up=").append(this.getOuterContextDepth());
        }
        buf.append(')');
        return buf.toString();
    }
}

