/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.iris.layer;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;

public interface WrappingMultiBufferSource {
    public void pushWrappingFunction(Function<RenderType, RenderType> var1);

    public void popWrappingFunction();

    public void assertWrapStackEmpty();
}

