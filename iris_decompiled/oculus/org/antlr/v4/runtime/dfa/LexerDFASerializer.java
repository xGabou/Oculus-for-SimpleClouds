/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.dfa;

import oculus.org.antlr.v4.runtime.VocabularyImpl;
import oculus.org.antlr.v4.runtime.dfa.DFA;
import oculus.org.antlr.v4.runtime.dfa.DFASerializer;

public class LexerDFASerializer
extends DFASerializer {
    public LexerDFASerializer(DFA dfa) {
        super(dfa, VocabularyImpl.EMPTY_VOCABULARY);
    }

    @Override
    protected String getEdgeLabel(int i) {
        return new StringBuilder("'").appendCodePoint(i).append("'").toString();
    }
}

