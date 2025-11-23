/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.viewport.Viewport
 *  me.jellysquid.mods.sodium.client.render.viewport.ViewportProvider
 *  me.jellysquid.mods.sodium.client.render.viewport.frustum.Frustum
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package net.irisshaders.iris.compat.sodium.mixin.shadow_map.frustum;

import me.jellysquid.mods.sodium.client.render.viewport.Viewport;
import me.jellysquid.mods.sodium.client.render.viewport.ViewportProvider;
import me.jellysquid.mods.sodium.client.render.viewport.frustum.Frustum;
import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.irisshaders.iris.shadows.frustum.advanced.AdvancedShadowCullingFrustum;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={AdvancedShadowCullingFrustum.class})
public abstract class MixinAdvancedShadowCullingFrustum
implements ViewportProvider,
Frustum {
    @Unique
    private final Vector3d position = new Vector3d();
    @Shadow
    public double x;
    @Shadow
    public double y;
    @Shadow
    public double z;
    @Shadow
    @Final
    protected BoxCuller boxCuller;

    @Shadow(remap=false)
    protected abstract int checkCornerVisibility(float var1, float var2, float var3, float var4, float var5, float var6);

    public boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return (this.boxCuller == null || !this.boxCuller.isCulledSodium(minX, minY, minZ, maxX, maxY, maxZ)) && this.checkCornerVisibility(minX, minY, minZ, maxX, maxY, maxZ) > 0;
    }

    public Viewport sodium$createViewport() {
        return new Viewport((Frustum)this, this.position.set(this.x, this.y, this.z));
    }
}

