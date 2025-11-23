/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={Frustum.class})
public interface MixinFrustumAccessor {
    @Invoker(value="cubeInFrustum")
    public boolean simpleclouds$cubeInFrustum(double var1, double var3, double var5, double var7, double var9, double var11);
}

