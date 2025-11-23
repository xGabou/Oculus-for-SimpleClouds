/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.chars.Char2ObjectMap
 *  it.unimi.dsi.fastutil.chars.Char2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
 */
package kroppeb.stareval.parser;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import kroppeb.stareval.parser.BinaryOp;
import kroppeb.stareval.parser.OpResolver;
import kroppeb.stareval.parser.UnaryOp;

public final class ParserOptions {
    private final Char2ObjectMap<? extends OpResolver<? extends UnaryOp>> unaryOpResolvers;
    private final Char2ObjectMap<? extends OpResolver<? extends BinaryOp>> binaryOpResolvers;
    private final TokenRules tokenRules;

    private ParserOptions(Char2ObjectMap<? extends OpResolver<? extends UnaryOp>> unaryOpResolvers, Char2ObjectMap<? extends OpResolver<? extends BinaryOp>> binaryOpResolvers, TokenRules tokenRules) {
        this.unaryOpResolvers = unaryOpResolvers;
        this.binaryOpResolvers = binaryOpResolvers;
        this.tokenRules = tokenRules;
    }

    TokenRules getTokenRules() {
        return this.tokenRules;
    }

    OpResolver<? extends UnaryOp> getUnaryOpResolver(char c) {
        return (OpResolver)this.unaryOpResolvers.get(c);
    }

    OpResolver<? extends BinaryOp> getBinaryOpResolver(char c) {
        return (OpResolver)this.binaryOpResolvers.get(c);
    }

    public static interface TokenRules {
        public static final TokenRules DEFAULT = new TokenRules(){};

        public static boolean isNumber(char c) {
            return c >= '0' && c <= '9';
        }

        public static boolean isLowerCaseLetter(char c) {
            return c >= 'a' && c <= 'z';
        }

        public static boolean isUpperCaseLetter(char c) {
            return c >= 'A' && c <= 'Z';
        }

        public static boolean isLetter(char c) {
            return TokenRules.isLowerCaseLetter(c) || TokenRules.isUpperCaseLetter(c);
        }

        default public boolean isIdStart(char c) {
            return TokenRules.isLetter(c) || c == '_';
        }

        default public boolean isIdPart(char c) {
            return this.isIdStart(c) || TokenRules.isNumber(c);
        }

        default public boolean isNumberStart(char c) {
            return TokenRules.isNumber(c) || c == '.';
        }

        default public boolean isNumberPart(char c) {
            return this.isNumberStart(c) || TokenRules.isLetter(c);
        }

        default public boolean isAccessStart(char c) {
            return this.isIdStart(c) || TokenRules.isNumber(c);
        }

        default public boolean isAccessPart(char c) {
            return this.isAccessStart(c);
        }
    }

    public static class Builder {
        private final Char2ObjectMap<OpResolver.Builder<UnaryOp>> unaryOpResolvers = new Char2ObjectOpenHashMap();
        private final Char2ObjectMap<OpResolver.Builder<BinaryOp>> binaryOpResolvers = new Char2ObjectOpenHashMap();
        private TokenRules tokenRules = TokenRules.DEFAULT;

        private static <T> Char2ObjectMap<? extends OpResolver<? extends T>> buildOpResolvers(Char2ObjectMap<OpResolver.Builder<T>> ops) {
            Char2ObjectOpenHashMap result = new Char2ObjectOpenHashMap();
            ops.char2ObjectEntrySet().forEach(arg_0 -> Builder.lambda$buildOpResolvers$0((Char2ObjectMap)result, arg_0));
            return result;
        }

        public void addUnaryOp(String s, UnaryOp op) {
            char first = s.charAt(0);
            String trailing = s.substring(1);
            ((OpResolver.Builder)this.unaryOpResolvers.computeIfAbsent(first, c -> new OpResolver.Builder())).multiChar(trailing, op);
        }

        public void addBinaryOp(String s, BinaryOp op) {
            char first = s.charAt(0);
            String trailing = s.substring(1);
            ((OpResolver.Builder)this.binaryOpResolvers.computeIfAbsent(first, c -> new OpResolver.Builder())).multiChar(trailing, op);
        }

        public void setTokenRules(TokenRules tokenRules) {
            this.tokenRules = tokenRules;
        }

        public ParserOptions build() {
            return new ParserOptions(Builder.buildOpResolvers(this.unaryOpResolvers), Builder.buildOpResolvers(this.binaryOpResolvers), this.tokenRules);
        }

        private static /* synthetic */ void lambda$buildOpResolvers$0(Char2ObjectMap result, Char2ObjectMap.Entry entry) {
            result.put(entry.getCharKey(), ((OpResolver.Builder)entry.getValue()).build());
        }
    }
}

