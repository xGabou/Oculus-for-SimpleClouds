/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.LevelRenderer
 *  org.spongepowered.asm.mixin.Mixin
 */
package net.irisshaders.iris.mixin.shadows;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={LevelRenderer.class}, priority=1010)
public abstract class MixinPreventRebuildNearInShadowPass {
}

