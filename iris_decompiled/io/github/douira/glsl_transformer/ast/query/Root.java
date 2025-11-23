/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.ReferenceExpression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.query.index.ExternalDeclarationIndex;
import io.github.douira.glsl_transformer.ast.query.index.IdentifierIndex;
import io.github.douira.glsl_transformer.ast.query.index.NodeIndex;
import io.github.douira.glsl_transformer.ast.query.index.PrefixExternalDeclarationIndex;
import io.github.douira.glsl_transformer.ast.query.index.PrefixIdentifierIndex;
import io.github.douira.glsl_transformer.ast.query.match.HintedMatcher;
import io.github.douira.glsl_transformer.ast.query.match.Matcher;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.util.Passthrough;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Root {
    public final NodeIndex<?> nodeIndex;
    public final IdentifierIndex<?, ?> identifierIndex;
    public final ExternalDeclarationIndex<?, ?> externalDeclarationIndex;
    private static Deque<Root> activeBuildRoots = new ArrayDeque<Root>();
    private List<? extends ASTNode> nodeList;
    private boolean activity;

    public Root(NodeIndex<?> nodeIndex, IdentifierIndex<?, ?> identifierIndex, ExternalDeclarationIndex<?, ?> externalDeclarationIndex) {
        this.nodeIndex = nodeIndex;
        this.identifierIndex = identifierIndex;
        this.externalDeclarationIndex = externalDeclarationIndex;
    }

    public PrefixIdentifierIndex<?, ?> getPrefixIdentifierIndex() {
        IdentifierIndex<?, ?> identifierIndex = this.identifierIndex;
        if (identifierIndex instanceof PrefixIdentifierIndex) {
            PrefixIdentifierIndex index = (PrefixIdentifierIndex)identifierIndex;
            return index;
        }
        throw new IllegalStateException("The identifier index is not a prefix index");
    }

    public PrefixExternalDeclarationIndex<?, ?> getPrefixExternalDeclarationIndex() {
        ExternalDeclarationIndex<?, ?> externalDeclarationIndex = this.externalDeclarationIndex;
        if (externalDeclarationIndex instanceof PrefixExternalDeclarationIndex) {
            PrefixExternalDeclarationIndex index = (PrefixExternalDeclarationIndex)externalDeclarationIndex;
            return index;
        }
        throw new IllegalStateException("The external declaration index is not a prefix index");
    }

    public void registerNode(ASTNode node, boolean isSubtreeRoot) {
        if (this.nodeIndex != null) {
            this.nodeIndex.add(node);
        }
        if (this.identifierIndex != null && node instanceof Identifier) {
            Identifier identifier = (Identifier)node;
            this.identifierIndex.add(identifier);
        }
        if (this.externalDeclarationIndex != null) {
            if (node instanceof ExternalDeclaration) {
                ExternalDeclaration externalDeclaration = (ExternalDeclaration)node;
                this.externalDeclarationIndex.add(externalDeclaration);
            } else if (isSubtreeRoot && !(node instanceof TranslationUnit)) {
                this.externalDeclarationIndex.notifySubtreeAdd(node);
            }
        }
    }

    public void unregisterNode(ASTNode node, boolean isSubtreeRoot) {
        if (this.nodeIndex != null) {
            this.nodeIndex.remove(node);
        }
        if (this.identifierIndex != null && node instanceof Identifier) {
            Identifier identifier = (Identifier)node;
            this.identifierIndex.remove(identifier);
        }
        if (this.externalDeclarationIndex != null) {
            if (node instanceof ExternalDeclaration) {
                ExternalDeclaration externalDeclaration = (ExternalDeclaration)node;
                this.externalDeclarationIndex.remove(externalDeclaration);
            } else if (isSubtreeRoot && !(node instanceof TranslationUnit)) {
                this.externalDeclarationIndex.notifySubtreeRemove(node);
            }
        }
    }

    public void unregisterIdentifierRename(Identifier identifier) {
        if (this.identifierIndex != null) {
            this.identifierIndex.remove(identifier);
        }
        this.unregisterFastRename(identifier);
    }

    public void unregisterFastRename(ASTNode identifier) {
        if (this.externalDeclarationIndex != null) {
            this.externalDeclarationIndex.notifySubtreeRemove(identifier);
        }
    }

    public void registerIdentifierRename(Identifier identifier) {
        if (this.identifierIndex != null) {
            this.identifierIndex.add(identifier);
        }
        this.registerFastRename(identifier);
    }

    public void registerFastRename(ASTNode identifier) {
        if (this.externalDeclarationIndex != null) {
            this.externalDeclarationIndex.notifySubtreeAdd(identifier);
        }
    }

    private void ensureEmptyNodeList() {
        if (this.nodeList == null) {
            this.nodeList = new ArrayList<ASTNode>();
        } else {
            this.nodeList.clear();
        }
    }

    public static Root getActiveBuildRoot() {
        return activeBuildRoots.peekFirst();
    }

    protected final <R> R withActiveBuildRoot(Function<Root, R> rootConsumer) {
        activeBuildRoots.push(this);
        try {
            R r = rootConsumer.apply(this);
            return r;
        }
        finally {
            activeBuildRoots.pop();
        }
    }

    public <N extends ASTNode> N indexNodes(Supplier<N> builder) {
        return (N)this.withActiveBuildRoot(root -> {
            ASTNode result = (ASTNode)builder.get();
            root.registerNode(result, true);
            return result;
        });
    }

    public void indexBuildSession(Runnable session) {
        this.withActiveBuildRoot(root -> {
            session.run();
            return null;
        });
    }

    public void indexBuildSession(Consumer<Root> session) {
        this.withActiveBuildRoot(root -> {
            session.accept((Root)root);
            return null;
        });
    }

    public <N extends ASTNode> void indexSeparateTrees(Consumer<Passthrough<N>> registererConsumer) {
        this.withActiveBuildRoot(root -> {
            registererConsumer.accept(node -> {
                root.registerNode((ASTNode)node, true);
                return node;
            });
            return null;
        });
    }

    public boolean rename(String oldName, String newName) {
        return this.identifierIndex.rename(oldName, newName);
    }

    public <N extends ASTNode> boolean process(Stream<? extends N> targets, Consumer<? super N> replacer) {
        this.ensureEmptyNodeList();
        if (targets == null) {
            return false;
        }
        List<? extends ASTNode> typedList = this.nodeList;
        targets.forEach(typedList::add);
        boolean activity = false;
        for (ASTNode aSTNode : typedList) {
            if (aSTNode == null) continue;
            replacer.accept(aSTNode);
            activity = true;
        }
        return activity;
    }

    public boolean process(String name, Consumer<Identifier> replacer) {
        return this.process(this.identifierIndex.getStream(name), replacer);
    }

    public void replaceReferenceExpressions(ASTParser t, String name, String expression) {
        this.replaceReferenceExpressions(t, this.identifierIndex.getStream(name), expression);
    }

    public boolean replaceReferenceExpressionsReport(ASTParser t, String name, String expression) {
        return this.replaceReferenceExpressionsReport(t, this.identifierIndex.getStream(name), expression);
    }

    public void replaceReferenceExpressions(ASTParser t, Stream<Identifier> targets, String expression) {
        this.process(targets, (? super N identifier) -> {
            ASTNode parent = identifier.getParent();
            if (!(parent instanceof ReferenceExpression)) {
                return;
            }
            parent.replaceByAndDelete(t.parseExpression(identifier.getRoot(), expression));
        });
    }

    public boolean replaceReferenceExpressionsReport(ASTParser t, Stream<Identifier> targets, String expression) {
        this.activity = false;
        this.process(targets, (? super N identifier) -> {
            ASTNode parent = identifier.getParent();
            if (!(parent instanceof ReferenceExpression)) {
                return;
            }
            parent.replaceByAndDelete(t.parseExpression(identifier.getRoot(), expression));
            this.activity = true;
        });
        return this.activity;
    }

    public boolean replaceExpressions(ASTParser t, Stream<? extends Expression> targets, String expression) {
        return this.process(targets, (? super N node) -> node.replaceByAndDelete(t.parseExpression(node.getRoot(), expression)));
    }

    public static boolean replaceExpressionsConcurrent(ASTParser t, List<? extends Expression> targets, String expression) {
        for (Expression expression2 : targets) {
            expression2.replaceByAndDelete(t.parseExpression(expression2.getRoot(), expression));
        }
        return !targets.isEmpty();
    }

    public <N extends ASTNode> boolean processMatches(ASTParser t, Stream<? extends ASTNode> matchTargetChildren, Matcher<N> matcher, Consumer<? super N> replacer) {
        Class matchClass = matcher.getPatternClass();
        return this.process(matchTargetChildren.map(node -> node.getAncestor(matchClass)).distinct().filter(matcher::matches), replacer);
    }

    public <N extends ASTNode> boolean processMatches(ASTParser t, HintedMatcher<N> hintedMatcher, Consumer<? super N> replacer) {
        return this.processMatches(t, this.identifierIndex.getStream(hintedMatcher.getHint()), hintedMatcher, replacer);
    }

    public <N extends Expression> boolean replaceExpressionMatches(ASTParser t, Stream<? extends ASTNode> matchTargetChildren, Matcher<N> matcher, String expression) {
        Class matchClass = matcher.getPatternClass();
        return this.replaceExpressions(t, matchTargetChildren.map(node -> (Expression)node.getAncestor(matchClass)).distinct().filter(matcher::matches), expression);
    }

    public <N extends Expression> boolean replaceExpressionMatches(ASTParser t, HintedMatcher<N> hintedMatcher, String expression) {
        return this.replaceExpressionMatches(t, this.identifierIndex.getStream(hintedMatcher.getHint()), hintedMatcher, expression);
    }
}

