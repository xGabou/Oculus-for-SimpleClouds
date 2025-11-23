/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerActionType;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public class LexerTypeAction
implements LexerAction {
    private final int type;

    public LexerTypeAction(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public LexerActionType getActionType() {
        return LexerActionType.TYPE;
    }

    @Override
    public boolean isPositionDependent() {
        return false;
    }

    @Override
    public void execute(Lexer lexer) {
        lexer.setType(this.type);
    }

    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        hash = MurmurHash.update(hash, this.type);
        return MurmurHash.finish(hash, 2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerTypeAction)) {
            return false;
        }
        return this.type == ((LexerTypeAction)obj).type;
    }

    public String toString() {
        return String.format("type(%d)", this.type);
    }
}

