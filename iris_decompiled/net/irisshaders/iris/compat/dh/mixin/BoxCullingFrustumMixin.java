/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum
 *  com.seibel.distanthorizons.api.objects.math.DhApiMat4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.compat.dh.mixin;

import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.irisshaders.iris.shadows.frustum.fallback.BoxCullingFrustum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={BoxCullingFrustum.class}, remap=false)
public class BoxCullingFrustumMixin
implements IDhApiShadowCullingFrustum {
    @Shadow
    @Final
    protected BoxCuller boxCuller;
    @Shadow
    private int worldMinYDH;
    @Shadow
    private int worldMaxYDH;

    public void update(int worldMinBlockY, int worldMaxBlockY, DhApiMat4f worldViewProjection) {
        this.worldMinYDH = worldMinBlockY;
        this.worldMaxYDH = worldMaxBlockY;
    }

    public boolean intersects(int lodBlockPosMinX, int lodBlockPosMinZ, int lodBlockWidth, int lodDetailLevel) {
        return !this.boxCuller.isCulled(lodBlockPosMinX, this.worldMinYDH, lodBlockPosMinZ, lodBlockPosMinX + lodBlockWidth, this.worldMaxYDH, lodBlockPosMinZ + lodBlockWidth);
    }
}

