/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import oculus.org.antlr.v4.runtime.atn.ATNConfigSet;
import oculus.org.antlr.v4.runtime.misc.ObjectEqualityComparator;

public class OrderedATNConfigSet
extends ATNConfigSet {
    public OrderedATNConfigSet() {
        this.configLookup = new LexerConfigHashSet();
    }

    public static class LexerConfigHashSet
    extends ATNConfigSet.AbstractConfigHashSet {
        public LexerConfigHashSet() {
            super(ObjectEqualityComparator.INSTANCE);
        }
    }
}

