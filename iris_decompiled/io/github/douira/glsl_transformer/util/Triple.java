/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.util;

public class Triple<V> {
    public final V a;
    public final V b;
    public final V c;

    public Triple(V a, V b, V c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.a == null ? 0 : this.a.hashCode());
        result = 31 * result + (this.b == null ? 0 : this.b.hashCode());
        result = 31 * result + (this.c == null ? 0 : this.c.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Triple other = (Triple)obj;
        if (this.a == null ? other.a != null : !this.a.equals(other.a)) {
            return false;
        }
        if (this.b == null ? other.b != null : !this.b.equals(other.b)) {
            return false;
        }
        return !(this.c == null ? other.c != null : !this.c.equals(other.c));
    }
}

