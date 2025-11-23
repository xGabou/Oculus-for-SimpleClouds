/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.targets;

public interface RenderTargetStateListener {
    public static final RenderTargetStateListener NOP = bound -> {};

    public void setIsMainBound(boolean var1);
}

