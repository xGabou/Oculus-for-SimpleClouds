/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.LevelRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package net.irisshaders.iris.mixin.shadows;

import net.irisshaders.iris.shadows.CullingDataCache;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer
implements CullingDataCache {
    @Override
    public void saveState() {
        this.swap();
    }

    @Override
    public void restoreState() {
        this.swap();
    }

    @Unique
    private void swap() {
    }
}

