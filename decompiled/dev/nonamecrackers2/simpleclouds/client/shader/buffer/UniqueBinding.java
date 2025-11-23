/*
 * Decompiled with CFR 0.152.
 */
package dev.nonamecrackers2.simpleclouds.client.shader.buffer;

import dev.nonamecrackers2.simpleclouds.client.shader.buffer.WithBinding;

public class UniqueBinding
implements WithBinding {
    private int binding = -1;

    public UniqueBinding(int binding) {
        this.binding = binding;
    }

    @Override
    public int getBinding() {
        return this.binding;
    }

    @Override
    public void close() {
        this.binding = -1;
    }
}

