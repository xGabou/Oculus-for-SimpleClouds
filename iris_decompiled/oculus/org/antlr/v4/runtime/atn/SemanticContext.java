/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import oculus.org.antlr.v4.runtime.Recognizer;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.misc.MurmurHash;
import oculus.org.antlr.v4.runtime.misc.Utils;

public abstract class SemanticContext {
    public abstract boolean eval(Recognizer<?, ?> var1, RuleContext var2);

    public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
        return this;
    }

    public static SemanticContext and(SemanticContext a, SemanticContext b) {
        if (a == null || a == Empty.Instance) {
            return b;
        }
        if (b == null || b == Empty.Instance) {
            return a;
        }
        AND result = new AND(a, b);
        if (result.opnds.length == 1) {
            return result.opnds[0];
        }
        return result;
    }

    public static SemanticContext or(SemanticContext a, SemanticContext b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (a == Empty.Instance || b == Empty.Instance) {
            return Empty.Instance;
        }
        OR result = new OR(a, b);
        if (result.opnds.length == 1) {
            return result.opnds[0];
        }
        return result;
    }

    private static List<PrecedencePredicate> filterPrecedencePredicates(Collection<? extends SemanticContext> collection) {
        ArrayList<PrecedencePredicate> result = null;
        Iterator<? extends SemanticContext> iterator = collection.iterator();
        while (iterator.hasNext()) {
            SemanticContext context = iterator.next();
            if (!(context instanceof PrecedencePredicate)) continue;
            if (result == null) {
                result = new ArrayList<PrecedencePredicate>();
            }
            result.add((PrecedencePredicate)context);
            iterator.remove();
        }
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public static class OR
    extends Operator {
        public final SemanticContext[] opnds;

        public OR(SemanticContext a, SemanticContext b) {
            HashSet<SemanticContext> operands = new HashSet<SemanticContext>();
            if (a instanceof OR) {
                operands.addAll(Arrays.asList(((OR)a).opnds));
            } else {
                operands.add(a);
            }
            if (b instanceof OR) {
                operands.addAll(Arrays.asList(((OR)b).opnds));
            } else {
                operands.add(b);
            }
            List precedencePredicates = SemanticContext.filterPrecedencePredicates(operands);
            if (!precedencePredicates.isEmpty()) {
                PrecedencePredicate reduced = (PrecedencePredicate)Collections.max(precedencePredicates);
                operands.add(reduced);
            }
            this.opnds = operands.toArray(new SemanticContext[0]);
        }

        @Override
        public Collection<SemanticContext> getOperands() {
            return Arrays.asList(this.opnds);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OR)) {
                return false;
            }
            OR other = (OR)obj;
            return Arrays.equals(this.opnds, other.opnds);
        }

        public int hashCode() {
            return MurmurHash.hashCode(this.opnds, OR.class.hashCode());
        }

        @Override
        public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            for (SemanticContext opnd : this.opnds) {
                if (!opnd.eval(parser, parserCallStack)) continue;
                return true;
            }
            return false;
        }

        @Override
        public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            boolean differs = false;
            ArrayList<SemanticContext> operands = new ArrayList<SemanticContext>();
            for (SemanticContext context : this.opnds) {
                SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
                differs |= evaluated != context;
                if (evaluated == Empty.Instance) {
                    return Empty.Instance;
                }
                if (evaluated == null) continue;
                operands.add(evaluated);
            }
            if (!differs) {
                return this;
            }
            if (operands.isEmpty()) {
                return null;
            }
            SemanticContext result = (SemanticContext)operands.get(0);
            for (int i = 1; i < operands.size(); ++i) {
                result = SemanticContext.or(result, (SemanticContext)operands.get(i));
            }
            return result;
        }

        public String toString() {
            return Utils.join(Arrays.asList(this.opnds).iterator(), "||");
        }
    }

    public static class AND
    extends Operator {
        public final SemanticContext[] opnds;

        public AND(SemanticContext a, SemanticContext b) {
            HashSet<SemanticContext> operands = new HashSet<SemanticContext>();
            if (a instanceof AND) {
                operands.addAll(Arrays.asList(((AND)a).opnds));
            } else {
                operands.add(a);
            }
            if (b instanceof AND) {
                operands.addAll(Arrays.asList(((AND)b).opnds));
            } else {
                operands.add(b);
            }
            List precedencePredicates = SemanticContext.filterPrecedencePredicates(operands);
            if (!precedencePredicates.isEmpty()) {
                PrecedencePredicate reduced = (PrecedencePredicate)Collections.min(precedencePredicates);
                operands.add(reduced);
            }
            this.opnds = operands.toArray(new SemanticContext[0]);
        }

        @Override
        public Collection<SemanticContext> getOperands() {
            return Arrays.asList(this.opnds);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AND)) {
                return false;
            }
            AND other = (AND)obj;
            return Arrays.equals(this.opnds, other.opnds);
        }

        public int hashCode() {
            return MurmurHash.hashCode(this.opnds, AND.class.hashCode());
        }

        @Override
        public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            for (SemanticContext opnd : this.opnds) {
                if (opnd.eval(parser, parserCallStack)) continue;
                return false;
            }
            return true;
        }

        @Override
        public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            boolean differs = false;
            ArrayList<SemanticContext> operands = new ArrayList<SemanticContext>();
            for (SemanticContext context : this.opnds) {
                SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
                differs |= evaluated != context;
                if (evaluated == null) {
                    return null;
                }
                if (evaluated == Empty.Instance) continue;
                operands.add(evaluated);
            }
            if (!differs) {
                return this;
            }
            if (operands.isEmpty()) {
                return Empty.Instance;
            }
            SemanticContext result = (SemanticContext)operands.get(0);
            for (int i = 1; i < operands.size(); ++i) {
                result = SemanticContext.and(result, (SemanticContext)operands.get(i));
            }
            return result;
        }

        public String toString() {
            return Utils.join(Arrays.asList(this.opnds).iterator(), "&&");
        }
    }

    public static abstract class Operator
    extends SemanticContext {
        public abstract Collection<SemanticContext> getOperands();
    }

    public static class PrecedencePredicate
    extends SemanticContext
    implements Comparable<PrecedencePredicate> {
        public final int precedence;

        protected PrecedencePredicate() {
            this.precedence = 0;
        }

        public PrecedencePredicate(int precedence) {
            this.precedence = precedence;
        }

        @Override
        public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            return parser.precpred(parserCallStack, this.precedence);
        }

        @Override
        public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            if (parser.precpred(parserCallStack, this.precedence)) {
                return Empty.Instance;
            }
            return null;
        }

        @Override
        public int compareTo(PrecedencePredicate o) {
            return this.precedence - o.precedence;
        }

        public int hashCode() {
            int hashCode = 1;
            hashCode = 31 * hashCode + this.precedence;
            return hashCode;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PrecedencePredicate)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            PrecedencePredicate other = (PrecedencePredicate)obj;
            return this.precedence == other.precedence;
        }

        public String toString() {
            return "{" + this.precedence + ">=prec}?";
        }
    }

    public static class Predicate
    extends SemanticContext {
        public final int ruleIndex;
        public final int predIndex;
        public final boolean isCtxDependent;

        protected Predicate() {
            this.ruleIndex = -1;
            this.predIndex = -1;
            this.isCtxDependent = false;
        }

        public Predicate(int ruleIndex, int predIndex, boolean isCtxDependent) {
            this.ruleIndex = ruleIndex;
            this.predIndex = predIndex;
            this.isCtxDependent = isCtxDependent;
        }

        @Override
        public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            RuleContext localctx = this.isCtxDependent ? parserCallStack : null;
            return parser.sempred(localctx, this.ruleIndex, this.predIndex);
        }

        public int hashCode() {
            int hashCode = MurmurHash.initialize();
            hashCode = MurmurHash.update(hashCode, this.ruleIndex);
            hashCode = MurmurHash.update(hashCode, this.predIndex);
            hashCode = MurmurHash.update(hashCode, this.isCtxDependent ? 1 : 0);
            hashCode = MurmurHash.finish(hashCode, 3);
            return hashCode;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Predicate)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            Predicate p = (Predicate)obj;
            return this.ruleIndex == p.ruleIndex && this.predIndex == p.predIndex && this.isCtxDependent == p.isCtxDependent;
        }

        public String toString() {
            return "{" + this.ruleIndex + ":" + this.predIndex + "}?";
        }
    }

    public static class Empty
    extends SemanticContext {
        public static final Empty Instance = new Empty();

        @Override
        public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
            return false;
        }
    }
}

