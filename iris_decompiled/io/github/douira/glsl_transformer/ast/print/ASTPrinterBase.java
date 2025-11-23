/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.print;

import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.print.TokenProcessor;
import io.github.douira.glsl_transformer.ast.print.TokenRole;
import io.github.douira.glsl_transformer.ast.print.token.IndentMarker;
import io.github.douira.glsl_transformer.ast.print.token.LineDirectiveMarker;
import io.github.douira.glsl_transformer.ast.print.token.LiteralToken;
import io.github.douira.glsl_transformer.ast.print.token.ParserToken;
import io.github.douira.glsl_transformer.ast.print.token.PrintToken;
import io.github.douira.glsl_transformer.ast.print.token.ReplaceToken;
import io.github.douira.glsl_transformer.ast.transform.SourceLocation;
import io.github.douira.glsl_transformer.ast.traversal.ASTListenerVisitor;
import io.github.douira.glsl_transformer.token_filter.TokenChannel;
import java.util.List;

public abstract class ASTPrinterBase
extends ASTListenerVisitor<Void> {
    private PrintToken lastToken;
    private ASTNode currentNode;
    private TokenProcessor tokenProcessor;

    protected ASTPrinterBase(TokenProcessor tokenProcessor) {
        this.tokenProcessor = tokenProcessor;
    }

    protected String generateString() {
        return this.tokenProcessor.generateString();
    }

    protected void appendToken(PrintToken token) {
        this.tokenProcessor.appendToken(token);
    }

    public void replaceToken(PrintToken replacement) {
        this.lastToken = replacement;
    }

    protected void emitToken(PrintToken token) {
        token.setSource(this.currentNode);
        if (token instanceof ReplaceToken) {
            ReplaceToken replaceToken = (ReplaceToken)token;
            if (this.lastToken == null) {
                return;
            }
            replaceToken.replace(this.lastToken, this);
            return;
        }
        if (this.lastToken != null) {
            this.appendToken(this.lastToken);
        }
        this.lastToken = token;
    }

    protected void finalizePrinting() {
        if (this.lastToken != null) {
            this.appendToken(this.lastToken);
            this.lastToken = null;
        }
    }

    protected void emitTokens(PrintToken ... tokens) {
        for (PrintToken t : tokens) {
            this.emitToken(t);
        }
    }

    protected void emitLiteral(TokenRole role, String literal) {
        this.emitToken(new LiteralToken(role, literal));
    }

    protected void emitLiteral(String literal) {
        this.emitToken(new LiteralToken(literal));
    }

    protected void emitLiteralSafe(String literal) {
        if (literal != null) {
            this.emitLiteral(literal);
        }
    }

    protected void emitLiterals(TokenRole role, String ... literals) {
        for (String l : literals) {
            this.emitLiteral(role, l);
        }
    }

    protected void emitLiterals(String ... literals) {
        this.emitLiterals(TokenRole.DEFAULT, literals);
    }

    protected void emitType(TokenRole role, int type) {
        this.emitToken(new ParserToken(role, type));
    }

    protected void emitType(int type) {
        this.emitType(TokenRole.DEFAULT, type);
    }

    protected void emitType(TokenRole role, int ... types) {
        for (int t : types) {
            this.emitType(role, t);
        }
    }

    protected void emitType(int ... types) {
        this.emitType(TokenRole.DEFAULT, types);
    }

    protected void emitWhitespace(TokenRole role, String whitespace) {
        this.emitToken(new LiteralToken(TokenChannel.WHITESPACE, role, whitespace));
    }

    protected void emitExactWhitespace(String whitespace) {
        this.emitWhitespace(TokenRole.EXACT, whitespace);
    }

    private void emitSpace(TokenRole role) {
        this.emitWhitespace(role, " ");
    }

    protected void emitExactSpace() {
        this.emitSpace(TokenRole.EXACT);
    }

    protected void emitExtendableSpace() {
        this.emitSpace(TokenRole.EXTENDABLE_SPACE);
    }

    protected void emitBreakableSpace() {
        this.emitSpace(TokenRole.BREAKABLE_SPACE);
    }

    private void emitNewline(TokenRole role) {
        this.emitWhitespace(role, "\n");
    }

    protected void emitExactNewline() {
        this.emitNewline(TokenRole.EXACT);
    }

    protected void emitCommonNewline() {
        this.emitNewline(TokenRole.COMMON_FORMATTING);
    }

    protected void emitStatementEnd() {
        this.emitType(243);
        this.emitCommonNewline();
    }

    protected void emitLineDirective(SourceLocation location) {
        if (location == null) {
            return;
        }
        if (!location.hasLine()) {
            throw new IllegalArgumentException("Location must have line to be printed!");
        }
        this.emitToken(new LineDirectiveMarker(location));
    }

    protected void indent() {
        this.emitToken(IndentMarker.indent());
    }

    protected void unindent() {
        this.emitToken(IndentMarker.unindent());
    }

    protected void compactCommonNewline() {
        this.compactCommonNewline(ASTNode.class);
    }

    protected void compactCommonNewline(Class<? extends ASTNode> sourceClass) {
        this.emitToken(ReplaceToken.fromMatchAndNodeCondition(new LiteralToken(TokenRole.COMMON_FORMATTING, " "), "\n", node -> sourceClass.isAssignableFrom(node.getClass())));
    }

    protected void visitWithSeparator(List<? extends ASTNode> nodes, Runnable emitter) {
        int size = nodes.size();
        for (int i = 0; i < size; ++i) {
            ASTNode node = nodes.get(i);
            if (node == null) continue;
            this.visit(node);
            if (i >= size - 1) continue;
            emitter.run();
        }
    }

    protected void visitCommaSpaced(List<? extends ASTNode> nodes) {
        this.visitWithSeparator(nodes, () -> {
            this.emitType(246);
            this.emitBreakableSpace();
        });
    }

    protected ASTNode getCurrentNode() {
        return this.currentNode;
    }

    protected void setCurrentNode(ASTNode currentNode) {
        this.currentNode = currentNode;
    }

    protected boolean visitSafe(ASTNode node) {
        if (node != null) {
            this.visit(node);
            return true;
        }
        return false;
    }

    @Override
    public void enterContext(ASTNode node) {
        this.setCurrentNode(node);
    }

    @Override
    public Void visit(ASTNode node) {
        super.visit(node);
        return null;
    }

    @Override
    public Void initialResult() {
        return null;
    }

    @Override
    public Void superNodeTypeResult() {
        return null;
    }

    @Override
    public Void visitData(Object data) {
        return null;
    }

    @Override
    public Void defaultResult() {
        throw new IllegalStateException("The default value should never be used and all nodes should be printed properly!");
    }
}

