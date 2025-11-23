/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.batchedentityrendering.impl;

public interface FlushableMultiBufferSource {
    public void flushNonTranslucentContent();

    public void flushTranslucentContent();
}

