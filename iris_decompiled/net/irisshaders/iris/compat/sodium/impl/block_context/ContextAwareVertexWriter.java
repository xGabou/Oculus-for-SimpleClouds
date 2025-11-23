/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.compat.sodium.impl.block_context;

import net.irisshaders.iris.compat.sodium.impl.block_context.BlockContextHolder;

public interface ContextAwareVertexWriter {
    public void iris$setContextHolder(BlockContextHolder var1);

    public void flipUpcomingQuadNormal();
}

