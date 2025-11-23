/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.Lexer;
import oculus.org.antlr.v4.runtime.atn.LexerAction;
import oculus.org.antlr.v4.runtime.atn.LexerActionType;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerChannelAction
implements LexerAction {
    private final int channel;

    public LexerChannelAction(int channel) {
        this.channel = channel;
    }

    public int getChannel() {
        return this.channel;
    }

    @Override
    public LexerActionType getActionType() {
        return LexerActionType.CHANNEL;
    }

    @Override
    public boolean isPositionDependent() {
        return false;
    }

    @Override
    public void execute(Lexer lexer) {
        lexer.setChannel(this.channel);
    }

    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        hash = MurmurHash.update(hash, this.channel);
        return MurmurHash.finish(hash, 2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerChannelAction)) {
            return false;
        }
        return this.channel == ((LexerChannelAction)obj).channel;
    }

    public String toString() {
        return String.format("channel(%d)", this.channel);
    }
}

