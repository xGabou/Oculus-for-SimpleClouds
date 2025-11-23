/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.match;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import io.github.douira.glsl_transformer.ast.traversal.ASTVoidVisitor;
import io.github.douira.glsl_transformer.parser.ParseShape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class Matcher<N extends ASTNode> {
    protected final N pattern;
    protected final String wildcardPrefix;
    private Map<String, Object> dataMatches;
    private Map<String, ASTNode> nodeMatches;
    private Map<ASTNode, NodeWildcard> nodeWildcards;
    private boolean collectMatches = false;
    protected List<Object> patternItems;
    protected int patternItemsSize;
    private int matchIndex;
    private boolean matches;
    private NodeWildcard activeListWildcard;
    private ASTVisitor<?> matchVisitor = new ASTVoidVisitor(){

        @Override
        public Void visit(ASTNode node) {
            Object patternItem;
            if (!Matcher.this.matches || Matcher.this.matchIndex >= Matcher.this.patternItemsSize && Matcher.this.activeListWildcard == null) {
                Matcher.this.matches = false;
                return null;
            }
            Object object = patternItem = Matcher.this.matchIndex >= Matcher.this.patternItemsSize ? null : Matcher.this.patternItems.get(Matcher.this.matchIndex++);
            if (Matcher.this.nodeWildcards != null) {
                NodeWildcard wildcard;
                if (patternItem instanceof NodeWildcard && (wildcard = (NodeWildcard)patternItem).test(node)) {
                    if (Matcher.this.collectMatches) {
                        Matcher.this.nodeMatches.put(wildcard.name, node);
                    }
                    Matcher.this.activeListWildcard = wildcard.name.endsWith("*") ? wildcard : null;
                    return null;
                }
                if (Matcher.this.activeListWildcard != null) {
                    if (Matcher.this.activeListWildcard.test(node)) {
                        if (Matcher.this.collectMatches) {
                            Matcher.this.nodeMatches.put(Matcher.this.activeListWildcard.name, node);
                        }
                        --Matcher.this.matchIndex;
                        return null;
                    }
                    Matcher.this.activeListWildcard = null;
                }
            }
            if (patternItem == null) {
                Matcher.this.matches = false;
                return null;
            }
            if (node.getClass() != patternItem.getClass()) {
                Matcher.this.matches = false;
                return null;
            }
            return node.accept(this);
        }

        @Override
        public void visitVoidData(Object data) {
            String str;
            if (!Matcher.this.matches || Matcher.this.matchIndex >= Matcher.this.patternItemsSize || data instanceof ASTNode) {
                Matcher.this.matches = false;
                return;
            }
            Object patternItem = Matcher.this.patternItems.get(Matcher.this.matchIndex++);
            if (Matcher.this.wildcardPrefix != null && patternItem instanceof String && (str = (String)patternItem).startsWith(Matcher.this.wildcardPrefix)) {
                if (Matcher.this.collectMatches) {
                    Matcher.this.dataMatches.put(str.substring(Matcher.this.wildcardPrefix.length()), data);
                }
                Matcher.this.activeListWildcard = null;
                return;
            }
            if (!Objects.equals(data, patternItem)) {
                Matcher.this.matches = false;
                return;
            }
        }
    };

    public Matcher(N pattern, String wildcardPrefix) {
        this.pattern = pattern;
        this.wildcardPrefix = wildcardPrefix;
    }

    public Matcher(N pattern) {
        this(pattern, null);
    }

    public Matcher(String input, ParseShape<?, N> parseShape, String wildcardPrefix) {
        this(parseShape._parseNodeSeparateInternal(input), wildcardPrefix);
    }

    public Matcher(String input, ParseShape<?, N> parseShape) {
        this(input, parseShape, null);
    }

    public void preparePatternItems() {
        if (this.patternItems != null) {
            return;
        }
        this.patternItems = new ArrayList<Object>();
        new ASTVoidVisitor(){

            @Override
            public Void visit(ASTNode node) {
                NodeWildcard wildcard;
                if (Matcher.this.nodeWildcards != null && (wildcard = Matcher.this.nodeWildcards.get(node)) != null) {
                    Matcher.this.patternItems.add(wildcard);
                    return null;
                }
                Matcher.this.patternItems.add(node);
                node.accept(this);
                return null;
            }

            @Override
            public void visitVoidData(Object data) {
                Matcher.this.patternItems.add(data);
            }
        }.startVisit((ASTNode)this.pattern);
        this.patternItemsSize = this.patternItems.size();
    }

    public boolean matches(N tree) {
        if (tree == null) {
            return false;
        }
        this.preparePatternItems();
        this.matchIndex = 0;
        this.matches = true;
        this.activeListWildcard = null;
        this.matchVisitor.startVisit((ASTNode)tree);
        return this.matches;
    }

    private void ensureMatchMaps() {
        if (this.dataMatches == null) {
            this.dataMatches = new HashMap<String, Object>();
        }
        if (this.nodeMatches == null) {
            this.nodeMatches = new HashMap<String, ASTNode>();
        }
    }

    public boolean matchesExtract(N tree) {
        this.ensureMatchMaps();
        this.dataMatches.clear();
        this.nodeMatches.clear();
        this.collectMatches = true;
        boolean succeeded = this.matches(tree);
        this.collectMatches = false;
        if (!succeeded) {
            this.dataMatches.clear();
            this.nodeMatches.clear();
        }
        return succeeded;
    }

    public boolean matchesExtract(N tree, Map<String, Object> dataMatches, Map<String, ASTNode> nodeMatches) {
        this.dataMatches = dataMatches;
        this.nodeMatches = nodeMatches;
        boolean succeeded = this.matchesExtract(tree);
        this.dataMatches = null;
        this.nodeMatches = null;
        return succeeded;
    }

    public Map<String, Object> getDataMatches() {
        return this.dataMatches;
    }

    public Map<String, ASTNode> getNodeMatches() {
        return this.nodeMatches;
    }

    public Object getDataMatch(String name) {
        return this.dataMatches.get(name);
    }

    public String getStringDataMatch(String name) {
        String str;
        Object result = this.dataMatches.get(name);
        return result instanceof String ? (str = (String)result) : null;
    }

    public ASTNode getNodeMatch(String name) {
        return this.nodeMatches.get(name);
    }

    public <NN extends ASTNode> NN getNodeMatch(String name, Class<NN> type) {
        ASTNode result = this.nodeMatches.get(name);
        return (NN)(type.isInstance(result) ? (ASTNode)type.cast(result) : null);
    }

    public Class<? extends N> getPatternClass() {
        return this.pattern.getClass();
    }

    private void ensureWildcardMap() {
        if (this.nodeWildcards == null) {
            this.nodeWildcards = new HashMap<ASTNode, NodeWildcard>();
        }
    }

    private void markWildcard(ASTNode node, NodeWildcard wildcard) {
        this.ensureWildcardMap();
        this.nodeWildcards.put(node, wildcard);
    }

    public void markAnyWildcard(String name, ASTNode patternNode) {
        this.markWildcard(patternNode, new AnyWildcard(name));
    }

    public void markPredicatedWildcard(String name, ASTNode patternNode, Predicate<ASTNode> matchPredicate) {
        this.markWildcard(patternNode, new PredicateWildcard(name, matchPredicate));
    }

    public void markClassWildcard(String name, ASTNode patternNode, Class<? extends ASTNode> type) {
        this.markWildcard(patternNode, new ClassWildcard(name, type));
    }

    public void markClassWildcard(String name, ASTNode patternNode) {
        this.ensureWildcardMap();
        this.nodeWildcards.put(patternNode, new ClassWildcard(name, patternNode.getClass()));
    }

    public <NN extends ASTNode> void markClassedPredicateWildcard(String name, ASTNode patternNode, Class<NN> type, Predicate<NN> predicate) {
        this.markWildcard(patternNode, new ClassedPredicateWildcard<NN>(name, type, predicate));
    }

    private static abstract class NodeWildcard
    implements Predicate<ASTNode> {
        final String name;

        NodeWildcard(String name) {
            this.name = name;
        }
    }

    private static class AnyWildcard
    extends NodeWildcard {
        AnyWildcard(String name) {
            super(name);
        }

        @Override
        public boolean test(ASTNode node) {
            return true;
        }
    }

    private static class PredicateWildcard
    extends NodeWildcard {
        final Predicate<ASTNode> predicate;

        PredicateWildcard(String name, Predicate<ASTNode> predicate) {
            super(name);
            this.predicate = predicate;
        }

        @Override
        public boolean test(ASTNode node) {
            return this.predicate.test(node);
        }
    }

    private static class ClassWildcard
    extends NodeWildcard {
        final Class<? extends ASTNode> type;

        ClassWildcard(String name, Class<? extends ASTNode> type) {
            super(name);
            this.type = type;
        }

        @Override
        public boolean test(ASTNode node) {
            return this.type.isInstance(node);
        }
    }

    private static class ClassedPredicateWildcard<N extends ASTNode>
    extends NodeWildcard {
        final Class<N> type;
        final Predicate<N> predicate;

        ClassedPredicateWildcard(String name, Class<N> type, Predicate<N> predicate) {
            super(name);
            this.type = type;
            this.predicate = predicate;
        }

        @Override
        public boolean test(ASTNode node) {
            return this.type.isInstance(node) && this.predicate.test((ASTNode)this.type.cast(node));
        }
    }
}

