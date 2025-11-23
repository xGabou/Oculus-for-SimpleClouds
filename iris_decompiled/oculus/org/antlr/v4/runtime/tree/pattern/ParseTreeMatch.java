/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.tree.pattern;

import java.util.Collections;
import java.util.List;
import oculus.org.antlr.v4.runtime.misc.MultiMap;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.pattern.ParseTreePattern;

public class ParseTreeMatch {
    private final ParseTree tree;
    private final ParseTreePattern pattern;
    private final MultiMap<String, ParseTree> labels;
    private final ParseTree mismatchedNode;

    public ParseTreeMatch(ParseTree tree, ParseTreePattern pattern, MultiMap<String, ParseTree> labels, ParseTree mismatchedNode) {
        if (tree == null) {
            throw new IllegalArgumentException("tree cannot be null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern cannot be null");
        }
        if (labels == null) {
            throw new IllegalArgumentException("labels cannot be null");
        }
        this.tree = tree;
        this.pattern = pattern;
        this.labels = labels;
        this.mismatchedNode = mismatchedNode;
    }

    public ParseTree get(String label) {
        List parseTrees = (List)this.labels.get(label);
        if (parseTrees == null || parseTrees.size() == 0) {
            return null;
        }
        return (ParseTree)parseTrees.get(parseTrees.size() - 1);
    }

    public List<ParseTree> getAll(String label) {
        List nodes = (List)this.labels.get(label);
        if (nodes == null) {
            return Collections.emptyList();
        }
        return nodes;
    }

    public MultiMap<String, ParseTree> getLabels() {
        return this.labels;
    }

    public ParseTree getMismatchedNode() {
        return this.mismatchedNode;
    }

    public boolean succeeded() {
        return this.mismatchedNode == null;
    }

    public ParseTreePattern getPattern() {
        return this.pattern;
    }

    public ParseTree getTree() {
        return this.tree;
    }

    public String toString() {
        return String.format("Match %s; found %d labels", this.succeeded() ? "succeeded" : "failed", this.getLabels().size());
    }
}

