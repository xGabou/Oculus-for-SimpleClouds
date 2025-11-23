/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerActionType;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerPushModeAction
implements LexerAction {
    private final int mode;

    public LexerPushModeAction(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }

    @Override
    public LexerActionType getActionType() {
        return LexerActionType.PUSH_MODE;
    }

    @Override
    public boolean isPositionDependent() {
        return false;
    }

    @Override
    public void execute(Lexer lexer) {
        lexer.pushMode(this.mode);
    }

    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        hash = MurmurHash.update(hash, this.mode);
        return MurmurHash.finish(hash, 2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerPushModeAction)) {
            return false;
        }
        return this.mode == ((LexerPushModeAction)obj).mode;
    }

    public String toString() {
        return String.format("pushMode(%d)", this.mode);
    }
}

