/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.token_filter;

import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import io.github.douira.glsl_transformer.token_filter.MultiFilter;
import java.util.function.Supplier;
import oculus.org.antlr.v4.runtime.Token;

public abstract class TokenFilter<J extends JobParameters> {
    private Supplier<J> jobParametersSupplier;

    public abstract boolean isTokenAllowed(Token var1);

    public void resetState() {
    }

    public void setJobParametersSupplier(Supplier<J> jobParametersSupplier) {
        this.jobParametersSupplier = jobParametersSupplier;
    }

    protected J getJobParameters() {
        return (J)((JobParameters)this.jobParametersSupplier.get());
    }

    public static <J extends JobParameters> TokenFilter<J> join(TokenFilter<J> a, TokenFilter<J> b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (MultiFilter.class.isInstance(b)) {
            if (MultiFilter.class.isInstance(a)) {
                MultiFilter bMulti = (MultiFilter)b;
                MultiFilter aMulti = (MultiFilter)a;
                Object multi = aMulti.clone();
                ((MultiFilter)multi).addAll(bMulti);
                return multi;
            }
            return TokenFilter.join(b, a);
        }
        if (MultiFilter.class.isInstance(a)) {
            MultiFilter aMulti = (MultiFilter)a;
            Object multi = aMulti.clone();
            ((MultiFilter)multi).add(b);
            return multi;
        }
        MultiFilter<J> multi = new MultiFilter<J>();
        multi.add(a);
        multi.add(b);
        return multi;
    }
}

