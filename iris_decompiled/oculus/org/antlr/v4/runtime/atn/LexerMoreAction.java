/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerActionType;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerMoreAction
implements LexerAction {
    public static final LexerMoreAction INSTANCE = new LexerMoreAction();

    private LexerMoreAction() {
    }

    @Override
    public LexerActionType getActionType() {
        return LexerActionType.MORE;
    }

    @Override
    public boolean isPositionDependent() {
        return false;
    }

    @Override
    public void execute(Lexer lexer) {
        lexer.more();
    }

    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        return MurmurHash.finish(hash, 1);
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public String toString() {
        return "more";
    }
}

