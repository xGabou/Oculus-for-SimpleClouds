/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.apache.commons.collections4.trie;

import java.util.Map;
import oculus.org.apache.commons.collections4.trie.AbstractPatriciaTrie;
import oculus.org.apache.commons.collections4.trie.analyzer.StringKeyAnalyzer;

public class PatriciaTrie<E>
extends AbstractPatriciaTrie<String, E> {
    private static final long serialVersionUID = 4446367780901817838L;

    public PatriciaTrie() {
        super(new StringKeyAnalyzer());
    }

    public PatriciaTrie(Map<? extends String, ? extends E> m) {
        super(new StringKeyAnalyzer(), m);
    }
}

