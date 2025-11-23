/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.Arrays;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerIndexedCustomAction;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public class LexerActionExecutor {
    private final LexerAction[] lexerActions;
    private final int hashCode;

    public LexerActionExecutor(LexerAction[] lexerActions) {
        this.lexerActions = lexerActions;
        int hash = MurmurHash.initialize();
        for (LexerAction lexerAction : lexerActions) {
            hash = MurmurHash.update(hash, lexerAction);
        }
        this.hashCode = MurmurHash.finish(hash, lexerActions.length);
    }

    public static LexerActionExecutor append(LexerActionExecutor lexerActionExecutor, LexerAction lexerAction) {
        if (lexerActionExecutor == null) {
            return new LexerActionExecutor(new LexerAction[]{lexerAction});
        }
        LexerAction[] lexerActions = Arrays.copyOf(lexerActionExecutor.lexerActions, lexerActionExecutor.lexerActions.length + 1);
        lexerActions[lexerActions.length - 1] = lexerAction;
        return new LexerActionExecutor(lexerActions);
    }

    public LexerActionExecutor fixOffsetBeforeMatch(int offset) {
        LexerAction[] updatedLexerActions = null;
        for (int i = 0; i < this.lexerActions.length; ++i) {
            if (!this.lexerActions[i].isPositionDependent() || this.lexerActions[i] instanceof LexerIndexedCustomAction) continue;
            if (updatedLexerActions == null) {
                updatedLexerActions = (LexerAction[])this.lexerActions.clone();
            }
            updatedLexerActions[i] = new LexerIndexedCustomAction(offset, this.lexerActions[i]);
        }
        if (updatedLexerActions == null) {
            return this;
        }
        return new LexerActionExecutor(updatedLexerActions);
    }

    public LexerAction[] getLexerActions() {
        return this.lexerActions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(Lexer lexer, CharStream input, int startIndex) {
        boolean requiresSeek = false;
        int stopIndex = input.index();
        try {
            for (LexerAction lexerAction : this.lexerActions) {
                if (lexerAction instanceof LexerIndexedCustomAction) {
                    int offset = ((LexerIndexedCustomAction)lexerAction).getOffset();
                    input.seek(startIndex + offset);
                    lexerAction = ((LexerIndexedCustomAction)lexerAction).getAction();
                    requiresSeek = startIndex + offset != stopIndex;
                } else if (lexerAction.isPositionDependent()) {
                    input.seek(stopIndex);
                    requiresSeek = false;
                }
                lexerAction.execute(lexer);
            }
        }
        finally {
            if (requiresSeek) {
                input.seek(stopIndex);
            }
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerActionExecutor)) {
            return false;
        }
        LexerActionExecutor other = (LexerActionExecutor)obj;
        return this.hashCode == other.hashCode && Arrays.equals(this.lexerActions, other.lexerActions);
    }
}

