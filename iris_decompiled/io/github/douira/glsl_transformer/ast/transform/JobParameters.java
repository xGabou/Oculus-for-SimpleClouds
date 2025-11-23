/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.transform;

public interface JobParameters {
    public static final JobParameters EMPTY = new JobParameters(){

        @Override
        public boolean equals(Object other) {
            return other == this;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    };

    public boolean equals(Object var1);

    public int hashCode();
}

