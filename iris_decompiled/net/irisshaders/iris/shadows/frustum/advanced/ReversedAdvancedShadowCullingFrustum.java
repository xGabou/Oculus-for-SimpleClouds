/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package net.irisshaders.iris.shadows.frustum.advanced;

import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.irisshaders.iris.shadows.frustum.advanced.AdvancedShadowCullingFrustum;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ReversedAdvancedShadowCullingFrustum
extends AdvancedShadowCullingFrustum {
    private final BoxCuller distanceCuller;

    public ReversedAdvancedShadowCullingFrustum(Matrix4f playerView, Matrix4f playerProjection, Vector3f shadowLightVectorFromOrigin, BoxCuller voxelCuller, BoxCuller distanceCuller) {
        super(playerView, playerProjection, shadowLightVectorFromOrigin, voxelCuller);
        this.distanceCuller = distanceCuller;
    }

    @Override
    public void m_113002_(double cameraX, double cameraY, double cameraZ) {
        if (this.distanceCuller != null) {
            this.distanceCuller.setPosition(cameraX, cameraY, cameraZ);
        }
        super.m_113002_(cameraX, cameraY, cameraZ);
    }

    @Override
    public boolean m_113029_(AABB aabb) {
        if (this.distanceCuller != null && this.distanceCuller.isCulled(aabb)) {
            return false;
        }
        if (this.boxCuller != null && !this.boxCuller.isCulled(aabb)) {
            return true;
        }
        return this.isVisible(aabb.f_82288_, aabb.f_82289_, aabb.f_82290_, aabb.f_82291_, aabb.f_82292_, aabb.f_82293_) != 0;
    }

    @Override
    public int fastAabbTest(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        if (this.distanceCuller != null && this.distanceCuller.isCulled(minX, minY, minZ, maxX, maxY, maxZ)) {
            return 0;
        }
        if (this.boxCuller != null && !this.boxCuller.isCulled(minX, minY, minZ, maxX, maxY, maxZ)) {
            return 2;
        }
        return this.isVisible(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        if (this.distanceCuller != null && this.distanceCuller.isCulledSodium(minX, minY, minZ, maxX, maxY, maxZ)) {
            return false;
        }
        if (this.boxCuller != null && !this.boxCuller.isCulledSodium(minX, minY, minZ, maxX, maxY, maxZ)) {
            return true;
        }
        return this.checkCornerVisibility(minX, minY, minZ, maxX, maxY, maxZ) > 0;
    }
}

