/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import oculus.org.antlr.v4.runtime.atn.ATNConfig;
import oculus.org.antlr.v4.runtime.atn.ATNConfigSet;
import oculus.org.antlr.v4.runtime.atn.ATNState;
import oculus.org.antlr.v4.runtime.atn.RuleStopState;
import oculus.org.antlr.v4.runtime.atn.SemanticContext;
import oculus.org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import oculus.org.antlr.v4.runtime.misc.FlexibleHashMap;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;

public enum PredictionMode {
    SLL,
    LL,
    LL_EXACT_AMBIG_DETECTION;


    public static boolean hasSLLConflictTerminatingPrediction(PredictionMode mode, ATNConfigSet configs) {
        Collection<BitSet> altsets;
        if (PredictionMode.allConfigsInRuleStopStates(configs)) {
            return true;
        }
        if (mode == SLL && configs.hasSemanticContext) {
            ATNConfigSet dup = new ATNConfigSet();
            for (ATNConfig c : configs) {
                c = new ATNConfig(c, SemanticContext.Empty.Instance);
                dup.add(c);
            }
            configs = dup;
        }
        boolean heuristic = PredictionMode.hasConflictingAltSet(altsets = PredictionMode.getConflictingAltSubsets(configs)) && !PredictionMode.hasStateAssociatedWithOneAlt(configs);
        return heuristic;
    }

    public static boolean hasConfigInRuleStopState(ATNConfigSet configs) {
        for (ATNConfig c : configs) {
            if (!(c.state instanceof RuleStopState)) continue;
            return true;
        }
        return false;
    }

    public static boolean allConfigsInRuleStopStates(ATNConfigSet configs) {
        for (ATNConfig config : configs) {
            if (config.state instanceof RuleStopState) continue;
            return false;
        }
        return true;
    }

    public static int resolvesToJustOneViableAlt(Collection<BitSet> altsets) {
        return PredictionMode.getSingleViableAlt(altsets);
    }

    public static boolean allSubsetsConflict(Collection<BitSet> altsets) {
        return !PredictionMode.hasNonConflictingAltSet(altsets);
    }

    public static boolean hasNonConflictingAltSet(Collection<BitSet> altsets) {
        for (BitSet alts : altsets) {
            if (alts.cardinality() != 1) continue;
            return true;
        }
        return false;
    }

    public static boolean hasConflictingAltSet(Collection<BitSet> altsets) {
        for (BitSet alts : altsets) {
            if (alts.cardinality() <= 1) continue;
            return true;
        }
        return false;
    }

    public static boolean allSubsetsEqual(Collection<BitSet> altsets) {
        Iterator<BitSet> it = altsets.iterator();
        BitSet first = it.next();
        while (it.hasNext()) {
            BitSet next = it.next();
            if (next.equals(first)) continue;
            return false;
        }
        return true;
    }

    public static int getUniqueAlt(Collection<BitSet> altsets) {
        BitSet all = PredictionMode.getAlts(altsets);
        if (all.cardinality() == 1) {
            return all.nextSetBit(0);
        }
        return 0;
    }

    public static BitSet getAlts(Collection<BitSet> altsets) {
        BitSet all = new BitSet();
        for (BitSet alts : altsets) {
            all.or(alts);
        }
        return all;
    }

    public static BitSet getAlts(ATNConfigSet configs) {
        BitSet alts = new BitSet();
        for (ATNConfig config : configs) {
            alts.set(config.alt);
        }
        return alts;
    }

    public static Collection<BitSet> getConflictingAltSubsets(ATNConfigSet configs) {
        AltAndContextMap configToAlts = new AltAndContextMap();
        for (ATNConfig c : configs) {
            BitSet alts = (BitSet)configToAlts.get(c);
            if (alts == null) {
                alts = new BitSet();
                configToAlts.put(c, alts);
            }
            alts.set(c.alt);
        }
        return configToAlts.values();
    }

    public static Map<ATNState, BitSet> getStateToAltMap(ATNConfigSet configs) {
        HashMap<ATNState, BitSet> m = new HashMap<ATNState, BitSet>();
        for (ATNConfig c : configs) {
            BitSet alts = (BitSet)m.get(c.state);
            if (alts == null) {
                alts = new BitSet();
                m.put(c.state, alts);
            }
            alts.set(c.alt);
        }
        return m;
    }

    public static boolean hasStateAssociatedWithOneAlt(ATNConfigSet configs) {
        Map<ATNState, BitSet> x = PredictionMode.getStateToAltMap(configs);
        for (BitSet alts : x.values()) {
            if (alts.cardinality() != 1) continue;
            return true;
        }
        return false;
    }

    public static int getSingleViableAlt(Collection<BitSet> altsets) {
        BitSet viableAlts = new BitSet();
        for (BitSet alts : altsets) {
            int minAlt = alts.nextSetBit(0);
            viableAlts.set(minAlt);
            if (viableAlts.cardinality() <= 1) continue;
            return 0;
        }
        return viableAlts.nextSetBit(0);
    }

    private static final class AltAndContextConfigEqualityComparator
    extends AbstractEqualityComparator<ATNConfig> {
        public static final AltAndContextConfigEqualityComparator INSTANCE = new AltAndContextConfigEqualityComparator();

        private AltAndContextConfigEqualityComparator() {
        }

        @Override
        public int hashCode(ATNConfig o) {
            int hashCode = MurmurHash.initialize(7);
            hashCode = MurmurHash.update(hashCode, o.state.stateNumber);
            hashCode = MurmurHash.update(hashCode, o.context);
            hashCode = MurmurHash.finish(hashCode, 2);
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
            return a.state.stateNumber == b.state.stateNumber && a.context.equals(b.context);
        }
    }

    static class AltAndContextMap
    extends FlexibleHashMap<ATNConfig, BitSet> {
        public AltAndContextMap() {
            super(AltAndContextConfigEqualityComparator.INSTANCE);
        }
    }
}

