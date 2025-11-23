/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum
 *  com.seibel.distanthorizons.api.objects.math.DhApiMat4f
 *  org.spongepowered.asm.mixin.Mixin
 */
package net.irisshaders.iris.compat.dh.mixin;

import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import net.irisshaders.iris.shadows.frustum.fallback.NonCullingFrustum;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={NonCullingFrustum.class}, remap=false)
public class NonCullingFrustumMixin
implements IDhApiShadowCullingFrustum {
    public void update(int worldMinBlockY, int worldMaxBlockY, DhApiMat4f worldViewProjection) {
    }

    public boolean intersects(int lodBlockPosMinX, int lodBlockPosMinZ, int lodBlockWidth, int lodDetailLevel) {
        return true;
    }
}

