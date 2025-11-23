/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.SpriteContents$Ticker
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.jetbrains.annotations.Nullable;

public interface SpriteContentsExtension {
    @Nullable
    public SpriteContents.Ticker getCreatedTicker();
}

