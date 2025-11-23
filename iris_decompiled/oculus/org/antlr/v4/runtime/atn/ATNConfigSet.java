/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import oculus.org.antlr.v4.runtime.atn.ATNConfig;
import oculus.org.antlr.v4.runtime.atn.ATNSimulator;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.PredictionContext;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;
import oculus.org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import oculus.org.antlr.v4.runtime.misc.Array2DHashSet;
import oculus.org.antlr.v4.runtime.misc.DoubleKeyMap;

public class ATNConfigSet
implements Set<ATNConfig> {
    protected boolean readonly = false;
    public AbstractConfigHashSet configLookup;
    public final ArrayList<ATNConfig> configs = new ArrayList(7);
    public int uniqueAlt;
    protected BitSet conflictingAlts;
    public boolean hasSemanticContext;
    public boolean dipsIntoOuterContext;
    public final boolean fullCtx;
    private int cachedHashCode = -1;

    public ATNConfigSet(boolean fullCtx) {
        this.configLookup = new ConfigHashSet();
        this.fullCtx = fullCtx;
    }

    public ATNConfigSet() {
        this(true);
    }

    public ATNConfigSet(ATNConfigSet old) {
        this(old.fullCtx);
        this.addAll(old);
        this.uniqueAlt = old.uniqueAlt;
        this.conflictingAlts = old.conflictingAlts;
        this.hasSemanticContext = old.hasSemanticContext;
        this.dipsIntoOuterContext = old.dipsIntoOuterContext;
    }

    @Override
    public boolean add(ATNConfig config) {
        return this.add(config, null);
    }

    public boolean add(ATNConfig config, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
        ATNConfig existing;
        if (this.readonly) {
            throw new IllegalStateException("This set is readonly");
        }
        if (config.semanticContext != SemanticContext.Empty.Instance) {
            this.hasSemanticContext = true;
        }
        if (config.getOuterContextDepth() > 0) {
            this.dipsIntoOuterContext = true;
        }
        if ((existing = this.configLookup.getOrAdd(config)) == config) {
            this.cachedHashCode = -1;
            this.configs.add(config);
            return true;
        }
        boolean rootIsWildcard = !this.fullCtx;
        PredictionContext merged = PredictionContext.merge(existing.context, config.context, rootIsWildcard, mergeCache);
        existing.reachesIntoOuterContext = Math.max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);
        if (config.isPrecedenceFilterSuppressed()) {
            existing.setPrecedenceFilterSuppressed(true);
        }
        existing.context = merged;
        return true;
    }

    public List<ATNConfig> elements() {
        return this.configs;
    }

    public Set<ATNState> getStates() {
        HashSet<ATNState> states = new HashSet<ATNState>();
        for (ATNConfig c : this.configs) {
            states.add(c.state);
        }
        return states;
    }

    public BitSet getAlts() {
        BitSet alts = new BitSet();
        for (ATNConfig config : this.configs) {
            alts.set(config.alt);
        }
        return alts;
    }

    public List<SemanticContext> getPredicates() {
        ArrayList<SemanticContext> preds = new ArrayList<SemanticContext>();
        for (ATNConfig c : this.configs) {
            if (c.semanticContext == SemanticContext.Empty.Instance) continue;
            preds.add(c.semanticContext);
        }
        return preds;
    }

    public ATNConfig get(int i) {
        return this.configs.get(i);
    }

    public void optimizeConfigs(ATNSimulator interpreter) {
        if (this.readonly) {
            throw new IllegalStateException("This set is readonly");
        }
        if (this.configLookup.isEmpty()) {
            return;
        }
        for (ATNConfig config : this.configs) {
            config.context = interpreter.getCachedContext(config.context);
        }
    }

    @Override
    public boolean addAll(Collection<? extends ATNConfig> coll) {
        for (ATNConfig aTNConfig : coll) {
            this.add(aTNConfig);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ATNConfigSet)) {
            return false;
        }
        ATNConfigSet other = (ATNConfigSet)o;
        boolean same = this.configs != null && this.configs.equals(other.configs) && this.fullCtx == other.fullCtx && this.uniqueAlt == other.uniqueAlt && this.conflictingAlts == other.conflictingAlts && this.hasSemanticContext == other.hasSemanticContext && this.dipsIntoOuterContext == other.dipsIntoOuterContext;
        return same;
    }

    @Override
    public int hashCode() {
        if (this.isReadonly()) {
            if (this.cachedHashCode == -1) {
                this.cachedHashCode = this.configs.hashCode();
            }
            return this.cachedHashCode;
        }
        return this.configs.hashCode();
    }

    @Override
    public int size() {
        return this.configs.size();
    }

    @Override
    public boolean isEmpty() {
        return this.configs.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (this.configLookup == null) {
            throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
        }
        return this.configLookup.contains(o);
    }

    public boolean containsFast(ATNConfig obj) {
        if (this.configLookup == null) {
            throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
        }
        return this.configLookup.containsFast(obj);
    }

    @Override
    public Iterator<ATNConfig> iterator() {
        return this.configs.iterator();
    }

    @Override
    public void clear() {
        if (this.readonly) {
            throw new IllegalStateException("This set is readonly");
        }
        this.configs.clear();
        this.cachedHashCode = -1;
        this.configLookup.clear();
    }

    public boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        this.configLookup = null;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.elements().toString());
        if (this.hasSemanticContext) {
            buf.append(",hasSemanticContext=").append(this.hasSemanticContext);
        }
        if (this.uniqueAlt != 0) {
            buf.append(",uniqueAlt=").append(this.uniqueAlt);
        }
        if (this.conflictingAlts != null) {
            buf.append(",conflictingAlts=").append(this.conflictingAlts);
        }
        if (this.dipsIntoOuterContext) {
            buf.append(",dipsIntoOuterContext");
        }
        return buf.toString();
    }

    public ATNConfig[] toArray() {
        return (ATNConfig[])this.configLookup.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.configLookup.toArray((U[])a);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public static abstract class AbstractConfigHashSet
    extends Array2DHashSet<ATNConfig> {
        public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator) {
            this(comparator, 16, 2);
        }

        public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator, int initialCapacity, int initialBucketCapacity) {
            super(comparator, initialCapacity, initialBucketCapacity);
        }

        @Override
        protected final ATNConfig asElementType(Object o) {
            if (!(o instanceof ATNConfig)) {
                return null;
            }
            return (ATNConfig)o;
        }

        protected final ATNConfig[][] createBuckets(int capacity) {
            return new ATNConfig[capacity][];
        }

        protected final ATNConfig[] createBucket(int capacity) {
            return new ATNConfig[capacity];
        }
    }

    public static final class ConfigEqualityComparator
    extends AbstractEqualityComparator<ATNConfig> {
        public static final ConfigEqualityComparator INSTANCE = new ConfigEqualityComparator();

        private ConfigEqualityComparator() {
        }

        @Override
        public int hashCode(ATNConfig o) {
            int hashCode = 7;
            hashCode = 31 * hashCode + o.state.stateNumber;
            hashCode = 31 * hashCode + o.alt;
            hashCode = 31 * hashCode + o.semanticContext.hashCode();
            return hashCode;
        }

        @Override
        public boolean equals(ATNConfig a, ATNConfig b) {
            if (a == b) {
                return true;
            }
            if (a == null || b == null) {
                return false;
            }
            return a.state.stateNumber == b.state.stateNumber && a.alt == b.alt && a.semanticContext.equals(b.semanticContext);
        }
    }

    public static class ConfigHashSet
    extends AbstractConfigHashSet {
        public ConfigHashSet() {
            super(ConfigEqualityComparator.INSTANCE);
        }
    }
}

