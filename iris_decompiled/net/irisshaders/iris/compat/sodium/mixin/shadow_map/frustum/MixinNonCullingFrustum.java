/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.viewport.Viewport
 *  me.jellysquid.mods.sodium.client.render.viewport.ViewportProvider
 *  me.jellysquid.mods.sodium.client.render.viewport.frustum.Frustum
 *  net.minecraft.client.Minecraft
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 */
package net.irisshaders.iris.compat.sodium.mixin.shadow_map.frustum;

import me.jellysquid.mods.sodium.client.render.viewport.Viewport;
import me.jellysquid.mods.sodium.client.render.viewport.ViewportProvider;
import me.jellysquid.mods.sodium.client.render.viewport.frustum.Frustum;
import net.irisshaders.iris.shadows.frustum.fallback.NonCullingFrustum;
import net.minecraft.client.Minecraft;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={NonCullingFrustum.class})
public class MixinNonCullingFrustum
implements Frustum,
ViewportProvider {
    private final Vector3d pos = new Vector3d();

    public Viewport sodium$createViewport() {
        return new Viewport((Frustum)this, this.pos.set(Minecraft.m_91087_().f_91063_.m_109153_().m_90583_().f_82479_, Minecraft.m_91087_().f_91063_.m_109153_().m_90583_().f_82480_, Minecraft.m_91087_().f_91063_.m_109153_().m_90583_().f_82481_));
    }

    public boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return true;
    }
}

