/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.query.index;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.query.index.StringKeyedIndex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class IdentifierIndex<S extends Set<Identifier>, I extends Map<String, S>>
extends StringKeyedIndex<Identifier, Identifier, S, I> {
    public IdentifierIndex(I index, Supplier<S> setFactory) {
        super(index, setFactory);
    }

    @Override
    protected Identifier getNode(Identifier entry) {
        return entry;
    }

    @Override
    public void add(Identifier node) {
        String key = node.getName();
        Set set = (Set)this.index.get(key);
        if (set == null) {
            set = (Set)this.setFactory.get();
            this.index.put(key, set);
        }
        set.add(node);
    }

    @Override
    public void remove(Identifier node) {
        String key = node.getName();
        Set set = (Set)this.index.get(key);
        if (set == null) {
            return;
        }
        set.remove(node);
        if (set.isEmpty()) {
            this.index.remove(key);
        }
    }

    public boolean rename(String oldName, String newName) {
        if (oldName.equals(newName)) {
            return false;
        }
        Identifier.validateContents(newName);
        Set set = (Set)this.index.get(oldName);
        if (set == null) {
            return false;
        }
        this.index.remove(oldName);
        Set existing = (Set)this.index.get(newName);
        if (existing == null) {
            this.index.put(newName, set);
        } else {
            existing.addAll(set);
        }
        for (Identifier id : set) {
            id._setNameInternal(newName);
        }
        return true;
    }

    public static IdentifierIndex<HashSet<Identifier>, HashMap<String, HashSet<Identifier>>> withOnlyExact() {
        return new IdentifierIndex<HashSet<Identifier>, HashMap<String, HashSet<Identifier>>>(new HashMap(), HashSet::new);
    }

    public static IdentifierIndex<LinkedHashSet<Identifier>, HashMap<String, LinkedHashSet<Identifier>>> withOnlyExactOrdered() {
        return new IdentifierIndex<LinkedHashSet<Identifier>, HashMap<String, LinkedHashSet<Identifier>>>(new HashMap(), LinkedHashSet::new);
    }

    public static <R extends Set<Identifier>> IdentifierIndex<R, HashMap<String, R>> withOnlyExact(Supplier<R> setFactory) {
        return new IdentifierIndex(new HashMap(), setFactory);
    }
}

