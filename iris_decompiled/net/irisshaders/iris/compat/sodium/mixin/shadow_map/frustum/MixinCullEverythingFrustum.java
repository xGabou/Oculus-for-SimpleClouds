/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.viewport.Viewport
 *  me.jellysquid.mods.sodium.client.render.viewport.ViewportProvider
 *  me.jellysquid.mods.sodium.client.render.viewport.frustum.Frustum
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 */
package net.irisshaders.iris.compat.sodium.mixin.shadow_map.frustum;

import me.jellysquid.mods.sodium.client.render.viewport.Viewport;
import me.jellysquid.mods.sodium.client.render.viewport.ViewportProvider;
import me.jellysquid.mods.sodium.client.render.viewport.frustum.Frustum;
import net.irisshaders.iris.shadows.frustum.CullEverythingFrustum;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={CullEverythingFrustum.class})
public class MixinCullEverythingFrustum
implements Frustum,
ViewportProvider {
    private static final Vector3d EMPTY = new Vector3d();

    public Viewport sodium$createViewport() {
        return new Viewport((Frustum)this, EMPTY);
    }

    public boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return false;
    }
}

