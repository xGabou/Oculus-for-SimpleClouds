/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.world.phys.AABB
 *  org.joml.Matrix4f
 */
package net.irisshaders.iris.shadows.frustum.fallback;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class NonCullingFrustum
extends Frustum {
    public NonCullingFrustum() {
        super(new Matrix4f(), new Matrix4f());
    }

    public boolean canDetermineInvisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return false;
    }

    public boolean m_113029_(AABB box) {
        return true;
    }
}

