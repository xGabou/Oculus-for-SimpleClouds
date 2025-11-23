/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.util.Arrays;
import oculus.org.antlr.v4.runtime.Vocabulary;

public class VocabularyImpl
implements Vocabulary {
    private static final String[] EMPTY_NAMES = new String[0];
    public static final VocabularyImpl EMPTY_VOCABULARY = new VocabularyImpl(EMPTY_NAMES, EMPTY_NAMES, EMPTY_NAMES);
    private final String[] literalNames;
    private final String[] symbolicNames;
    private final String[] displayNames;
    private final int maxTokenType;

    public VocabularyImpl(String[] literalNames, String[] symbolicNames) {
        this(literalNames, symbolicNames, null);
    }

    public VocabularyImpl(String[] literalNames, String[] symbolicNames, String[] displayNames) {
        this.literalNames = literalNames != null ? literalNames : EMPTY_NAMES;
        this.symbolicNames = symbolicNames != null ? symbolicNames : EMPTY_NAMES;
        this.displayNames = displayNames != null ? displayNames : EMPTY_NAMES;
        this.maxTokenType = Math.max(this.displayNames.length, Math.max(this.literalNames.length, this.symbolicNames.length)) - 1;
    }

    public static Vocabulary fromTokenNames(String[] tokenNames) {
        if (tokenNames == null || tokenNames.length == 0) {
            return EMPTY_VOCABULARY;
        }
        String[] literalNames = Arrays.copyOf(tokenNames, tokenNames.length);
        String[] symbolicNames = Arrays.copyOf(tokenNames, tokenNames.length);
        for (int i = 0; i < tokenNames.length; ++i) {
            String tokenName = tokenNames[i];
            if (tokenName == null) continue;
            if (!tokenName.isEmpty()) {
                char firstChar = tokenName.charAt(0);
                if (firstChar == '\'') {
                    symbolicNames[i] = null;
                    continue;
                }
                if (Character.isUpperCase(firstChar)) {
                    literalNames[i] = null;
                    continue;
                }
            }
            literalNames[i] = null;
            symbolicNames[i] = null;
        }
        return new VocabularyImpl(literalNames, symbolicNames, tokenNames);
    }

    @Override
    public int getMaxTokenType() {
        return this.maxTokenType;
    }

    @Override
    public String getLiteralName(int tokenType) {
        if (tokenType >= 0 && tokenType < this.literalNames.length) {
            return this.literalNames[tokenType];
        }
        return null;
    }

    @Override
    public String getSymbolicName(int tokenType) {
        if (tokenType >= 0 && tokenType < this.symbolicNames.length) {
            return this.symbolicNames[tokenType];
        }
        if (tokenType == -1) {
            return "EOF";
        }
        return null;
    }

    @Override
    public String getDisplayName(int tokenType) {
        String displayName;
        if (tokenType >= 0 && tokenType < this.displayNames.length && (displayName = this.displayNames[tokenType]) != null) {
            return displayName;
        }
        String literalName = this.getLiteralName(tokenType);
        if (literalName != null) {
            return literalName;
        }
        String symbolicName = this.getSymbolicName(tokenType);
        if (symbolicName != null) {
            return symbolicName;
        }
        return Integer.toString(tokenType);
    }

    public String[] getLiteralNames() {
        return this.literalNames;
    }

    public String[] getSymbolicNames() {
        return this.symbolicNames;
    }

    public String[] getDisplayNames() {
        return this.displayNames;
    }
}

