/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.token_filter;

import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.token_filter.TokenFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import oculus.org.antlr.v4.runtime.Token;

public class MultiFilter<J extends JobParameters>
extends TokenFilter<J> {
    private Collection<TokenFilter<J>> subfilters;
    private boolean conjunction = true;
    private boolean shortCircuit = false;

    public MultiFilter(Collection<TokenFilter<J>> subfilters, boolean conjunction, boolean shortCircuit) {
        this(subfilters);
        this.conjunction = conjunction;
        this.shortCircuit = shortCircuit;
    }

    public MultiFilter(int initialCapacity, boolean conjunction, boolean shortCircuit) {
        this(new ArrayList<TokenFilter<J>>(initialCapacity), conjunction, shortCircuit);
    }

    public MultiFilter(boolean conjunction, boolean shortCircuit) {
        this.conjunction = conjunction;
        this.shortCircuit = shortCircuit;
    }

    public MultiFilter(int initialCapacity) {
        this(new ArrayList(initialCapacity));
    }

    public MultiFilter() {
        this(new ArrayList());
    }

    public MultiFilter(Collection<? extends TokenFilter<J>> subfilters) {
        this.subfilters = new ArrayList<TokenFilter<J>>(subfilters);
    }

    public void setConjunction(boolean conjunction) {
        this.conjunction = conjunction;
    }

    public void setShortCircuit(boolean shortCircuit) {
        this.shortCircuit = shortCircuit;
    }

    public boolean add(TokenFilter<J> filter) {
        return this.subfilters.add(filter);
    }

    public boolean addAll(Collection<? extends TokenFilter<J>> newSubfilters) {
        return this.subfilters.addAll(newSubfilters);
    }

    public boolean addAll(MultiFilter<J> other) {
        return this.addAll(other.subfilters);
    }

    public MultiFilter<J> clone() {
        return new MultiFilter<J>(this.subfilters, this.conjunction, this.shortCircuit);
    }

    @Override
    public void resetState() {
        super.resetState();
        for (TokenFilter<J> filter : this.subfilters) {
            filter.resetState();
        }
    }

    @Override
    public void setJobParametersSupplier(Supplier<J> jobParametersSupplier) {
        super.setJobParametersSupplier(jobParametersSupplier);
        for (TokenFilter<J> filter : this.subfilters) {
            filter.setJobParametersSupplier(jobParametersSupplier);
        }
    }

    @Override
    public boolean isTokenAllowed(Token token) {
        boolean result = this.conjunction;
        for (TokenFilter<J> filter : this.subfilters) {
            boolean verdict = filter.isTokenAllowed(token);
            boolean bl = this.conjunction ? result && verdict : (result = result || verdict);
            if (!this.shortCircuit || result == this.conjunction) continue;
            return result;
        }
        return result;
    }
}

