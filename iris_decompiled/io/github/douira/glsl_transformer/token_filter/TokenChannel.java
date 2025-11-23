/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.token_filter;

import io.github.douira.glsl_transformer.GLSLLexer;
import oculus.org.antlr.v4.runtime.Token;

public enum TokenChannel {
    DEFAULT,
    HIDDEN,
    WHITESPACE,
    COMMENTS,
    PREPROCESSOR;

    private static TokenChannel[] channels;

    public static TokenChannel getTokenChannel(Token token) {
        return channels[token.getChannel()];
    }

    static {
        channels = TokenChannel.values();
        if (channels[0] != DEFAULT) {
            throw new AssertionError((Object)"The default channel position should match ANTLR's convention!");
        }
        if (channels[1] != HIDDEN) {
            throw new AssertionError((Object)"The hidden channel position should match ANTLR's convention!");
        }
        for (int i = 2; i < channels.length; ++i) {
            String generatedName;
            String enumName = channels[i].name();
            if (!enumName.equals(generatedName = GLSLLexer.channelNames[i])) {
                throw new AssertionError((Object)("The channel with name " + enumName + " at position " + i + " has to match the corresponding generated channel name " + generatedName + "!"));
            }
        }
    }
}

