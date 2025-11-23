/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.node.abstract_node;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.SourceLocation;
import io.github.douira.glsl_transformer.ast.transform.Template;
import io.github.douira.glsl_transformer.ast.traversal.ASTVisitor;
import io.github.douira.glsl_transformer.ast.traversal.ASTVoidVisitor;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ASTNode {
    private ASTNode parent;
    private ASTNode lastParent;
    private Consumer<ASTNode> selfReplacer;
    private Root root = Root.getActiveBuildRoot();
    protected Template<?> template = null;
    protected SourceLocation sourceLocation = null;
    private boolean registered = false;

    public abstract <R> R accept(ASTVisitor<R> var1);

    public SourceLocation getSourceLocation() {
        return this.sourceLocation;
    }

    void setSourceLocation(SourceLocation sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public ASTNode getParent() {
        return this.parent;
    }

    public ASTNode getLastParent() {
        return this.lastParent;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public Consumer<ASTNode> getParentSetter() {
        return this.selfReplacer;
    }

    public ASTNode getNthParent(int n) {
        ASTNode node = this;
        for (int i = 0; i < n; ++i) {
            if (node == null) {
                return null;
            }
            node = node.getParent();
        }
        return node;
    }

    public boolean hasAncestor(int limit, int skip, Predicate<ASTNode> predicate) {
        ASTNode node = this;
        for (int i = 0; i <= limit; ++i) {
            if (node == null) {
                return false;
            }
            if (i >= skip && predicate.test(node)) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    public boolean hasAncestor(Predicate<ASTNode> predicate) {
        return this.hasAncestor(Integer.MAX_VALUE, 0, predicate);
    }

    public boolean hasAncestor(Class<? extends ASTNode> ancestorType) {
        return this.hasAncestor(ancestorType::isInstance);
    }

    public boolean hasAncestor(ASTNode node) {
        return this.hasAncestor(node::equals);
    }

    public ASTNode getAncestor(int limit, int skip, Predicate<ASTNode> predicate) {
        ASTNode node = this;
        for (int i = 0; i <= limit; ++i) {
            if (node == null) {
                return null;
            }
            if (i >= skip && predicate.test(node)) {
                return node;
            }
            node = node.getParent();
        }
        return null;
    }

    public ASTNode getAncestor(Predicate<ASTNode> predicate) {
        return this.getAncestor(Integer.MAX_VALUE, 0, predicate);
    }

    public <N extends ASTNode> N getAncestor(Class<N> ancestorType) {
        return (N)((ASTNode)ancestorType.cast(this.getAncestor(ancestorType::isInstance)));
    }

    public ASTNode getBranchAncestor(int limit, int skip, BiPredicate<ASTNode, ASTNode> predicate) {
        ASTNode node = this;
        ASTNode last = null;
        for (int i = 0; i <= limit; ++i) {
            if (node == null) {
                return null;
            }
            if (i >= skip && predicate.test(node, last)) {
                return node;
            }
            last = node;
            node = node.getParent();
        }
        return null;
    }

    public <N extends ASTNode> N getBranchAncestor(int limit, int skip, Class<N> branchClass, Function<N, ? extends ASTNode> branchGetter) {
        return (N)this.getBranchAncestor(limit, skip, (node, last) -> {
            if (!branchClass.isInstance(node)) {
                return false;
            }
            return branchGetter.apply((ASTNode)branchClass.cast(node)) == last;
        });
    }

    public <N extends ASTNode> N getBranchAncestor(Class<N> branchClass, Function<N, ? extends ASTNode> branchGetter) {
        return this.getBranchAncestor(Integer.MAX_VALUE, 0, branchClass, branchGetter);
    }

    public <N extends ASTNode, R extends ASTNode> R getBranchAncestorContinue(Class<N> branchClass, Function<N, ? extends ASTNode> branchGetter, Class<R> continueClass) {
        N result = this.getBranchAncestor(branchClass, branchGetter);
        return result == null ? null : (R)((ASTNode)result).getAncestor(continueClass);
    }

    public Stream<ASTNode> getAncestors() {
        return Stream.iterate((Object)this, ASTNode::hasParent, ASTNode::getParent);
    }

    public Root getRoot() {
        return this.root;
    }

    private void setRoot(Root root, boolean isSubtreeRoot) {
        if (this.root == root) {
            return;
        }
        if (this.registered) {
            this.unregister(isSubtreeRoot);
        }
        this.root = root;
        this.register(isSubtreeRoot);
    }

    private void unregister(boolean isSubtreeRoot) {
        this.root.unregisterNode(this, isSubtreeRoot);
        this.registered = false;
    }

    private void register(boolean isSubtreeRoot) {
        this.root.registerNode(this, isSubtreeRoot);
        this.registered = true;
    }

    public boolean setParent(ASTNode parent, Consumer<? extends ASTNode> setter) {
        Objects.requireNonNull(parent);
        this.selfReplacer = setter;
        if (this.parent == parent) {
            return false;
        }
        if (this.root == parent.root) {
            this.lastParent = this.parent;
            this.parent = parent;
            if (!this.registered) {
                this.register(parent.registered);
            }
        } else {
            this.lastParent = this.parent;
            this.parent = parent;
            this.changeRootRecursive(parent.root);
        }
        return true;
    }

    private void changeRootRecursive(Root root) {
        new ChangeRootVisitor(root).visit(this);
    }

    public boolean replaceBy(ASTNode replacement) {
        if (this.selfReplacer != null) {
            this.selfReplacer.accept(replacement);
            return true;
        }
        return false;
    }

    public boolean replaceByAndDelete(ASTNode replacement) {
        if (this.replaceBy(replacement)) {
            this.unregisterSubtree();
            return true;
        }
        return false;
    }

    public boolean detach() {
        return this.replaceBy(null);
    }

    public boolean detachAndDelete() {
        return this.replaceByAndDelete(null);
    }

    public void detachParent() {
        this.lastParent = this.parent;
        this.parent = null;
        this.selfReplacer = null;
    }

    public void unregisterSubtree() {
        this.detachParent();
        new UnregisterVisitor().visit(this);
    }

    public static boolean swap(ASTNode a, ASTNode b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        ASTNode bParent = b.getParent();
        ASTNode aParent = a.getParent();
        Objects.requireNonNull(aParent);
        Objects.requireNonNull(bParent);
        if (aParent == b || bParent == a) {
            return false;
        }
        Consumer<ASTNode> bReplacer = b.selfReplacer;
        a.replaceBy(b);
        bReplacer.accept(a);
        return true;
    }

    public <N extends ASTNode> N setup(N node, Consumer<? extends N> setter) {
        if (node != null) {
            node.setParent(this, setter);
        }
        return node;
    }

    public <N extends ASTNode> void updateParents(N currentNode, N newNode, Consumer<? extends N> setter) {
        if (currentNode == newNode && newNode.getParent() == this) {
            return;
        }
        if (currentNode != null) {
            currentNode.detachParent();
        }
        if (newNode != null) {
            newNode.setParent(this, setter);
        }
    }

    public void markTemplate(Template<?> template) {
        this.template = template;
    }

    protected abstract ASTNode clone();

    public ASTNode cloneInto(Root root) {
        return root.indexNodes(this::clone);
    }

    public static <N extends ASTNode> N clone(N node) {
        N replacement;
        if (node == null) {
            return null;
        }
        ASTNode clone = node.template == null ? node.clone() : ((replacement = node.template.getReplacement(node)) == null ? node.clone() : replacement);
        clone.setSourceLocation(node.getSourceLocation());
        return (N)clone;
    }

    public static <N extends ASTNode> Stream<N> clone(ChildNodeList<N> nodes) {
        return nodes == null ? null : nodes.getClonedStream();
    }

    private class ChangeRootVisitor
    extends ASTVoidVisitor {
        private Root rootToSet;

        ChangeRootVisitor(Root rootToSet) {
            this.rootToSet = rootToSet;
        }

        @Override
        public void visitVoid(ASTNode node) {
            node.setRoot(this.rootToSet, node == ASTNode.this);
        }
    }

    private class UnregisterVisitor
    extends ASTVoidVisitor {
        private UnregisterVisitor() {
        }

        @Override
        public void visitVoid(ASTNode node) {
            node.unregister(node == ASTNode.this);
        }
    }
}

