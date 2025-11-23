/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.RootSupplier;
import io.github.douira.glsl_transformer.parser.ParseShape;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Template<N extends ASTNode> {
    private final Map<ASTNode, Supplier<ASTNode>> replacements = new HashMap<ASTNode, Supplier<ASTNode>>();
    private int localReplacementsMarked = 0;
    private List<ASTNode> localReplacements = Collections.emptyList();
    protected final N source;

    public Template(N source) {
        this.source = source;
    }

    public N getSource() {
        return this.source;
    }

    public Root getSourceRoot() {
        return ((ASTNode)this.source).getRoot();
    }

    public <R> R getReplacement(R original) {
        Supplier<ASTNode> replacementSupplier = this.replacements.get(original);
        return (R)(replacementSupplier == null ? null : replacementSupplier.get());
    }

    public N getInstanceFor(Root root) {
        return (N)((ASTNode)this.source).cloneInto(root);
    }

    public void supplyLocalReplacements(List<ASTNode> replacements) {
        Objects.requireNonNull(replacements);
        if (replacements.size() < this.localReplacementsMarked) {
            throw new IllegalStateException("The local replacements must have enough items for all marked nodes in the template.");
        }
        this.localReplacements = replacements;
    }

    public void supplyLocalReplacements(ASTNode replacement) {
        Objects.requireNonNull(replacement);
        this.supplyLocalReplacements(Collections.singletonList(replacement));
    }

    public void supplyLocalReplacements(ASTNode ... replacements) {
        Objects.requireNonNull(replacements);
        this.supplyLocalReplacements(Arrays.asList(replacements));
    }

    public N getInstanceFor(Root root, List<ASTNode> localReplacements) {
        this.supplyLocalReplacements(localReplacements);
        return this.getInstanceFor(root);
    }

    public N getInstanceFor(Root root, ASTNode localReplacement) {
        this.supplyLocalReplacements(localReplacement);
        return this.getInstanceFor(root);
    }

    public N getInstanceFor(Root root, ASTNode ... localReplacements) {
        this.supplyLocalReplacements(localReplacements);
        return this.getInstanceFor(root);
    }

    public void markLocalReplacement(ASTNode original) {
        int index = this.localReplacementsMarked++;
        this.markReplacement(original, () -> this.localReplacements.get(index));
    }

    public void markLocalReplacement(String tag, Class<? extends ASTNode> type) {
        this.markLocalReplacement(((Identifier)((ASTNode)this.source).getRoot().identifierIndex.getOne(tag)).getAncestor(type));
    }

    public void markIdentifierReplacement(String tag) {
        this.markLocalReplacement((ASTNode)((ASTNode)this.source).getRoot().identifierIndex.getOne(tag));
    }

    public void markIdentifierReplacement(String tag, Supplier<ASTNode> replacement) {
        this.markReplacement((ASTNode)((ASTNode)this.source).getRoot().identifierIndex.getOne(tag), replacement);
    }

    public void markReplacement(ASTNode original, Supplier<ASTNode> replacement) {
        Objects.requireNonNull(original);
        Objects.requireNonNull(replacement);
        this.replacements.put(original, replacement);
        original.markTemplate(this);
    }

    public <NN extends ASTNode> void markReplacement(String tag, Class<NN> type, Supplier<NN> replacement) {
        this.markReplacement((ASTNode)((Identifier)((ASTNode)this.source).getRoot().identifierIndex.getOne(tag)).getAncestor(type), replacement);
    }

    public static <N extends ASTNode> Template<N> ofCloned(N source) {
        return new Template<ASTNode>(source.cloneInto(RootSupplier.supplyDefault()));
    }

    public static Template<ExternalDeclaration> withExternalDeclaration(String input) {
        return new Template<ExternalDeclaration>(ParseShape.EXTERNAL_DECLARATION._parseNodeSeparateInternal(input));
    }

    public static Template<Statement> withStatement(String input) {
        return new Template<Statement>(ParseShape.STATEMENT._parseNodeSeparateInternal(input));
    }

    public static Template<Expression> withExpression(String input) {
        return new Template<Expression>(ParseShape.EXPRESSION._parseNodeSeparateInternal(input));
    }
}

