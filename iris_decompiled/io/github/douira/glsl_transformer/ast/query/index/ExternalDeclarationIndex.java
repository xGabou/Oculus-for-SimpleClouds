/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.declaration.Declaration;
import io.github.douira.glsl_transformer.ast.node.declaration.DeclarationMember;
import io.github.douira.glsl_transformer.ast.node.declaration.FunctionDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.InterfaceBlockDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.VariableDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.CustomDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExtensionDirective;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.FunctionDefinition;
import io.github.douira.glsl_transformer.ast.node.external_declaration.IncludeDirective;
import io.github.douira.glsl_transformer.ast.node.type.specifier.FunctionPrototype;
import io.github.douira.glsl_transformer.ast.query.index.StringKeyedIndex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ExternalDeclarationIndex<S extends Set<DeclarationEntry>, I extends Map<String, S>>
extends StringKeyedIndex<DeclarationEntry, ExternalDeclaration, S, I> {
    public ExternalDeclarationIndex(I index, Supplier<S> setFactory) {
        super(index, setFactory);
    }

    @Override
    protected ExternalDeclaration getNode(DeclarationEntry entry) {
        return entry.declaration;
    }

    private void iterateKeyMembers(ExternalDeclaration node, BiConsumer<ASTNode, String> memberConsumer) {
        if (node instanceof DeclarationExternalDeclaration) {
            DeclarationExternalDeclaration declarationExternalDeclaration = (DeclarationExternalDeclaration)node;
            Declaration declaration = declarationExternalDeclaration.getDeclaration();
            if (declaration instanceof TypeAndInitDeclaration) {
                TypeAndInitDeclaration typeAndInitDeclaration = (TypeAndInitDeclaration)declaration;
                for (DeclarationMember member : typeAndInitDeclaration.getMembers()) {
                    memberConsumer.accept(member, member.getName().getName());
                }
            } else if (declaration instanceof FunctionDeclaration) {
                FunctionDeclaration functionDeclaration = (FunctionDeclaration)declaration;
                memberConsumer.accept(node, functionDeclaration.getFunctionPrototype().getName().getName());
            } else if (declaration instanceof InterfaceBlockDeclaration) {
                InterfaceBlockDeclaration interfaceBlockDeclaration = (InterfaceBlockDeclaration)declaration;
                memberConsumer.accept(node, interfaceBlockDeclaration.getBlockName().getName());
            } else if (declaration instanceof VariableDeclaration) {
                VariableDeclaration variableDeclaration = (VariableDeclaration)declaration;
                for (Identifier name : variableDeclaration.getNames()) {
                    memberConsumer.accept(name, name.getName());
                }
            }
        } else if (node instanceof FunctionDefinition) {
            FunctionDefinition functionDefinition = (FunctionDefinition)node;
            memberConsumer.accept(node, functionDefinition.getFunctionPrototype().getName().getName());
        } else if (node instanceof ExtensionDirective) {
            ExtensionDirective extensionDirective = (ExtensionDirective)node;
            memberConsumer.accept(node, extensionDirective.getName());
        } else if (node instanceof CustomDirective) {
            CustomDirective customDirective = (CustomDirective)node;
            memberConsumer.accept(node, customDirective.getContent());
        } else if (node instanceof IncludeDirective) {
            IncludeDirective includeDirective = (IncludeDirective)node;
            memberConsumer.accept(node, includeDirective.getContent());
        }
    }

    private void addEntry(ExternalDeclaration node, ASTNode keyMember, String key) {
        Set set = (Set)this.index.get(key);
        if (set == null) {
            set = (Set)this.setFactory.get();
            this.index.put(key, set);
        }
        set.add(new DeclarationEntry(node, keyMember));
    }

    private void removeEntry(ExternalDeclaration node, ASTNode keyMember, String key) {
        Set set = (Set)this.index.get(key);
        if (set == null) {
            return;
        }
        set.remove((Object)new DeclarationEntry(node, keyMember));
        if (set.isEmpty()) {
            this.index.remove(key);
        }
    }

    @Override
    public void add(ExternalDeclaration node) {
        this.iterateKeyMembers(node, (keyMember, key) -> this.addEntry(node, (ASTNode)keyMember, (String)key));
    }

    @Override
    public void remove(ExternalDeclaration node) {
        this.iterateKeyMembers(node, (keyMember, key) -> this.removeEntry(node, (ASTNode)keyMember, (String)key));
    }

    private void iterateSubtreeEntries(ASTNode node, ASTNode nodeParent, BiConsumer<ASTNode, String> memberConsumer) {
        if (node == null) {
            return;
        }
        if (node instanceof TypeAndInitDeclaration) {
            TypeAndInitDeclaration typeAndInitDeclaration = (TypeAndInitDeclaration)node;
            for (DeclarationMember member : typeAndInitDeclaration.getMembers()) {
                memberConsumer.accept(member, member.getName().getName());
            }
            return;
        }
        if (node instanceof VariableDeclaration) {
            VariableDeclaration variableDeclaration = (VariableDeclaration)node;
            for (Identifier name : variableDeclaration.getNames()) {
                memberConsumer.accept(name, name.getName());
            }
            return;
        }
        String singleKey = null;
        ASTNode keyMember = node;
        if (node instanceof Identifier) {
            Identifier identifier = (Identifier)node;
            singleKey = identifier.getName();
            keyMember = node;
            node = node.getParent();
        }
        if (node instanceof DeclarationMember) {
            keyMember = node;
            node = node.getParent();
        }
        if (node instanceof FunctionPrototype) {
            node = node.getParent();
        }
        if (node instanceof FunctionDefinition) {
            memberConsumer.accept(node, singleKey);
            return;
        }
        if (node instanceof VariableDeclaration || node instanceof TypeAndInitDeclaration) {
            node = node.getParent();
        } else if (node instanceof FunctionDeclaration || node instanceof InterfaceBlockDeclaration) {
            keyMember = node = node.getParent();
        }
        if (node instanceof DeclarationExternalDeclaration) {
            memberConsumer.accept(keyMember, singleKey);
            return;
        }
    }

    public void notifySubtreeAdd(ASTNode subtreeRoot) {
        if (subtreeRoot instanceof ExternalDeclaration) {
            ExternalDeclaration externalDeclaration = (ExternalDeclaration)subtreeRoot;
            this.add(externalDeclaration);
        }
        this.iterateSubtreeEntries(subtreeRoot, subtreeRoot.getParent(), (keyMember, key) -> this.addEntry(keyMember.getAncestor(ExternalDeclaration.class), (ASTNode)keyMember, (String)key));
    }

    public void notifySubtreeRemove(ASTNode subtreeRoot) {
        if (subtreeRoot instanceof ExternalDeclaration) {
            ExternalDeclaration externalDeclaration = (ExternalDeclaration)subtreeRoot;
            this.remove(externalDeclaration);
        }
        this.iterateSubtreeEntries(subtreeRoot, subtreeRoot.getLastParent(), (keyMember, key) -> this.removeEntry(keyMember.getAncestor(ExternalDeclaration.class), (ASTNode)keyMember, (String)key));
    }

    public static ExternalDeclarationIndex<HashSet<DeclarationEntry>, HashMap<String, HashSet<DeclarationEntry>>> withOnlyExact() {
        return new ExternalDeclarationIndex<HashSet<DeclarationEntry>, HashMap<String, HashSet<DeclarationEntry>>>(new HashMap(), HashSet::new);
    }

    public static ExternalDeclarationIndex<LinkedHashSet<DeclarationEntry>, HashMap<String, LinkedHashSet<DeclarationEntry>>> withOnlyExactOrdered() {
        return new ExternalDeclarationIndex<LinkedHashSet<DeclarationEntry>, HashMap<String, LinkedHashSet<DeclarationEntry>>>(new HashMap(), LinkedHashSet::new);
    }

    public static <R extends Set<DeclarationEntry>> ExternalDeclarationIndex<R, HashMap<String, R>> withOnlyExact(Supplier<R> setFactory) {
        return new ExternalDeclarationIndex(new HashMap(), setFactory);
    }

    public record DeclarationEntry(ExternalDeclaration declaration, ASTNode keyMember) {
    }
}

