/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.world.phys.AABB
 *  org.joml.Matrix4f
 */
package net.irisshaders.iris.shadows.frustum.fallback;

import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class BoxCullingFrustum
extends Frustum {
    private final BoxCuller boxCuller;
    private double x;
    private double y;
    private double z;
    private int worldMinYDH;
    private int worldMaxYDH;

    public BoxCullingFrustum(BoxCuller boxCuller) {
        super(new Matrix4f(), new Matrix4f());
        this.boxCuller = boxCuller;
    }

    public void m_113002_(double cameraX, double cameraY, double cameraZ) {
        this.x = cameraX;
        this.y = cameraY;
        this.z = cameraZ;
        this.boxCuller.setPosition(cameraX, cameraY, cameraZ);
    }

    public boolean canDetermineInvisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return false;
    }

    public boolean m_113029_(AABB box) {
        return !this.boxCuller.isCulled(box);
    }
}

