/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node;

import io.github.douira.glsl_transformer.ast.data.ProxyArrayList;
import io.github.douira.glsl_transformer.ast.node.VersionStatement;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ListASTNode;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.FunctionDefinition;
import io.github.douira.glsl_transformer.ast.node.statement.CompoundStatement;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.print.OutputOptions;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.ast.transform.TransformationException;
import io.github.douira.glsl_transformer.ast.traversal.ASTListener;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class TranslationUnit
extends ListASTNode<ExternalDeclaration> {
    protected VersionStatement versionStatement;
    public final OutputOptions outputOptions;

    public TranslationUnit(VersionStatement versionStatement, Stream<ExternalDeclaration> children, OutputOptions outputOptions) {
        super(children);
        this.versionStatement = this.setup(versionStatement, this::setVersionStatement);
        this.outputOptions = outputOptions;
    }

    public TranslationUnit(VersionStatement versionStatement, Stream<ExternalDeclaration> children) {
        super(children);
        this.versionStatement = this.setup(versionStatement, this::setVersionStatement);
        this.outputOptions = new OutputOptions();
    }

    public TranslationUnit(Stream<ExternalDeclaration> children) {
        super(children);
        this.outputOptions = new OutputOptions();
    }

    public VersionStatement getVersionStatement() {
        return this.versionStatement;
    }

    public void setVersionStatement(VersionStatement versionStatement) {
        this.updateParents(this.versionStatement, versionStatement, this::setVersionStatement);
        this.versionStatement = versionStatement;
    }

    public void injectNode(ASTInjectionPoint injectionPoint, ExternalDeclaration node) {
        ((ProxyArrayList)this.getChildren()).add(injectionPoint.getInjectionIndex(this), node);
    }

    public void injectNodes(ASTInjectionPoint injectionPoint, Collection<ExternalDeclaration> nodes) {
        ((ProxyArrayList)this.getChildren()).addAll(injectionPoint.getInjectionIndex(this), nodes);
    }

    public void injectNodes(ASTInjectionPoint injectionPoint, Stream<ExternalDeclaration> nodes) {
        nodes.reduce(injectionPoint.getInjectionIndex(this), (index, node) -> {
            ((ProxyArrayList)this.getChildren()).add((int)index, node);
            return index + 1;
        }, Integer::sum);
    }

    public void parseAndInjectNodes(ASTParser t, ASTInjectionPoint injectionPoint, Stream<String> externalDeclarations) {
        this.injectNodes(injectionPoint, externalDeclarations.map(str -> t.parseExternalDeclaration(this.getRoot(), (String)str)));
    }

    public void parseAndInjectNode(ASTParser t, ASTInjectionPoint injectionPoint, String externalDeclaration) {
        ((ProxyArrayList)this.getChildren()).add(injectionPoint.getInjectionIndex(this), t.parseExternalDeclaration(this.getRoot(), externalDeclaration));
    }

    public void parseAndInjectNodes(ASTParser t, ASTInjectionPoint injectionPoint, String ... externalDeclarations) {
        this.injectNodes(injectionPoint, t.parseExternalDeclarations(this.getRoot(), externalDeclarations));
    }

    public Optional<CompoundStatement> getOneFunctionDefinitionBodyOptional(String functionName) {
        return this.getRoot().identifierIndex.getStream(functionName).map(id -> id.getBranchAncestor(FunctionDefinition.class, FunctionDefinition::getFunctionPrototype)).filter(Objects::nonNull).findAny().map(FunctionDefinition::getBody);
    }

    public CompoundStatement getOneFunctionDefinitionBody(String functionName) {
        return this.getOneFunctionDefinitionBodyOptional(functionName).orElseThrow(TransformationException.supplier("No function definition found for '" + functionName + "'"));
    }

    public CompoundStatement getOneMainDefinitionBody() {
        return this.getOneFunctionDefinitionBody("main");
    }

    public void prependFunctionBody(String functionName, Statement statement) {
        this.getOneFunctionDefinitionBody(functionName).getStatements().add(0, statement);
    }

    public void prependFunctionBody(String functionName, Collection<Statement> statements) {
        this.getOneFunctionDefinitionBody(functionName).getStatements().addAll(0, statements);
    }

    public void appendFunctionBody(String functionName, Statement statement) {
        this.getOneFunctionDefinitionBody(functionName).getStatements().add(statement);
    }

    public void appendFunctionBody(String functionName, Collection<Statement> statements) {
        this.getOneFunctionDefinitionBody(functionName).getStatements().addAll(statements);
    }

    public void prependMainFunctionBody(Statement statement) {
        this.prependFunctionBody("main", statement);
    }

    public void prependMainFunctionBody(Collection<Statement> statements) {
        this.prependFunctionBody("main", statements);
    }

    public void appendMainFunctionBody(Statement statement) {
        this.appendFunctionBody("main", statement);
    }

    public void appendMainFunctionBody(Collection<Statement> statements) {
        this.appendFunctionBody("main", statements);
    }

    public void prependMainFunctionBody(ASTParser t, String ... statements) {
        this.prependMainFunctionBody(t.parseStatements(this.getRoot(), statements));
    }

    public void prependMainFunctionBody(ASTParser t, String statement) {
        this.prependMainFunctionBody(t.parseStatement(this.getRoot(), statement));
    }

    public void appendMainFunctionBody(ASTParser t, String ... statements) {
        this.appendMainFunctionBody(t.parseStatements(this.getRoot(), statements));
    }

    public void appendMainFunctionBody(ASTParser t, String statement) {
        this.appendMainFunctionBody(t.parseStatement(this.getRoot(), statement));
    }

    public void ensureVersionStatement() {
        if (this.versionStatement == null) {
            this.setVersionStatement(VersionStatement.getDefault());
        }
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitTranslationUnit(this);
    }

    @Override
    public void enterNode(ASTListener listener) {
        listener.enterTranslationUnit(this);
    }

    @Override
    public void exitNode(ASTListener listener) {
        listener.exitTranslationUnit(this);
    }

    @Override
    public TranslationUnit clone() {
        return new TranslationUnit(TranslationUnit.clone(this.versionStatement), this.getClonedChildren(), this.outputOptions.clone());
    }

    @Override
    public TranslationUnit cloneInto(Root root) {
        return (TranslationUnit)super.cloneInto(root);
    }
}

