/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerActionType;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerIndexedCustomAction
implements LexerAction {
    private final int offset;
    private final LexerAction action;

    public LexerIndexedCustomAction(int offset, LexerAction action) {
        this.offset = offset;
        this.action = action;
    }

    public int getOffset() {
        return this.offset;
    }

    public LexerAction getAction() {
        return this.action;
    }

    @Override
    public LexerActionType getActionType() {
        return this.action.getActionType();
    }

    @Override
    public boolean isPositionDependent() {
        return true;
    }

    @Override
    public void execute(Lexer lexer) {
        this.action.execute(lexer);
    }

    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.offset);
        hash = MurmurHash.update(hash, this.action);
        return MurmurHash.finish(hash, 2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerIndexedCustomAction)) {
            return false;
        }
        LexerIndexedCustomAction other = (LexerIndexedCustomAction)obj;
        return this.offset == other.offset && this.action.equals(other.action);
    }
}

