/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.materialmap;

import java.util.Objects;

public class NamespacedId {
    private final String namespace;
    private final String name;

    public NamespacedId(String combined) {
        int colonIdx = combined.indexOf(58);
        if (colonIdx == -1) {
            this.namespace = "minecraft";
            this.name = combined;
        } else {
            this.namespace = combined.substring(0, colonIdx);
            this.name = combined.substring(colonIdx + 1);
        }
    }

    public NamespacedId(String namespace, String name) {
        this.namespace = Objects.requireNonNull(namespace);
        this.name = Objects.requireNonNull(name);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NamespacedId that = (NamespacedId)o;
        return this.namespace.equals(that.namespace) && this.name.equals(that.name);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.namespace == null ? 0 : this.namespace.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public String toString() {
        return "NamespacedId{namespace='" + this.namespace + "', name='" + this.name + "'}";
    }
}

