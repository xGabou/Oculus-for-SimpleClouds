/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl.ordering;

import java.util.List;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.minecraft.client.renderer.RenderType;

public interface RenderOrderManager {
    public void begin(RenderType var1);

    public void startGroup();

    public boolean maybeStartGroup();

    public void endGroup();

    public void reset();

    public void resetType(TransparencyType var1);

    public List<RenderType> getRenderOrder();
}

