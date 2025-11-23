/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.token_filter;

import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.token_filter.TokenChannel;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import java.util.EnumSet;
import oculus.org.antlr.v4.runtime.Token;

public class ChannelFilter<J extends JobParameters>
extends TokenFilter<J> {
    private final EnumSet<TokenChannel> disallowedChannels;

    public ChannelFilter(EnumSet<TokenChannel> disallowedChannels) {
        this.disallowedChannels = disallowedChannels;
    }

    public ChannelFilter(TokenChannel disallowedChannel) {
        this(EnumSet.of(disallowedChannel));
    }

    @Override
    public boolean isTokenAllowed(Token token) {
        return !this.disallowedChannels.contains((Object)TokenChannel.getTokenChannel(token));
    }
}

