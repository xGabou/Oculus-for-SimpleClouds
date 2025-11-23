/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.helpers;

public record Tri<X, Y, Z>(X first, Y second, Z third) {
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tri)) {
            return false;
        }
        Tri tri = (Tri)((Object)obj);
        return tri.first == this.first && tri.second == this.second && tri.third == this.third;
    }

    public String toString() {
        return "First: " + this.first.toString() + " Second: " + this.second.toString() + " Third: " + this.third.toString();
    }
}

