/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.world.phys.AABB
 *  org.joml.Math
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.shadows.frustum.advanced;

import net.irisshaders.iris.shadows.frustum.BoxCuller;
import net.irisshaders.iris.shadows.frustum.advanced.BaseClippingPlanes;
import net.irisshaders.iris.shadows.frustum.advanced.NeighboringPlaneSet;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class AdvancedShadowCullingFrustum
extends Frustum {
    private static final int MAX_CLIPPING_PLANES = 13;
    protected final BoxCuller boxCuller;
    private final Vector4f[] planes = new Vector4f[13];
    private final Vector3f shadowLightVectorFromOrigin;
    public double x;
    public double y;
    public double z;
    private int worldMinYDH;
    private int worldMaxYDH;
    private int planeCount = 0;

    public AdvancedShadowCullingFrustum(Matrix4f playerView, Matrix4f playerProjection, Vector3f shadowLightVectorFromOrigin, BoxCuller boxCuller) {
        super(new Matrix4f(), new Matrix4f());
        this.shadowLightVectorFromOrigin = shadowLightVectorFromOrigin;
        BaseClippingPlanes baseClippingPlanes = new BaseClippingPlanes(playerView, playerProjection);
        boolean[] isBack = this.addBackPlanes(baseClippingPlanes);
        this.addEdgePlanes(baseClippingPlanes, isBack);
        this.boxCuller = boxCuller;
    }

    private void addPlane(Vector4f plane) {
        this.planes[this.planeCount] = plane;
        ++this.planeCount;
    }

    private boolean[] addBackPlanes(BaseClippingPlanes baseClippingPlanes) {
        Vector4f[] planes = baseClippingPlanes.getPlanes();
        boolean[] isBack = new boolean[planes.length];
        for (int planeIndex = 0; planeIndex < planes.length; ++planeIndex) {
            Vector4f plane = planes[planeIndex];
            Vector3f planeNormal = this.truncate(plane);
            float dot = planeNormal.dot((Vector3fc)this.shadowLightVectorFromOrigin);
            boolean back = (double)dot > 0.0;
            boolean edge = (double)dot == 0.0;
            isBack[planeIndex] = back;
            if (!back && !edge) continue;
            this.addPlane(plane);
        }
        return isBack;
    }

    private void addEdgePlanes(BaseClippingPlanes baseClippingPlanes, boolean[] isBack) {
        Vector4f[] planes = baseClippingPlanes.getPlanes();
        for (int planeIndex = 0; planeIndex < planes.length; ++planeIndex) {
            if (!isBack[planeIndex]) continue;
            Vector4f plane = planes[planeIndex];
            NeighboringPlaneSet neighbors = NeighboringPlaneSet.forPlane(planeIndex);
            if (!isBack[neighbors.plane0()]) {
                this.addEdgePlane(plane, planes[neighbors.plane0()]);
            }
            if (!isBack[neighbors.plane1()]) {
                this.addEdgePlane(plane, planes[neighbors.plane1()]);
            }
            if (!isBack[neighbors.plane2()]) {
                this.addEdgePlane(plane, planes[neighbors.plane2()]);
            }
            if (isBack[neighbors.plane3()]) continue;
            this.addEdgePlane(plane, planes[neighbors.plane3()]);
        }
    }

    private Vector3f truncate(Vector4f base) {
        return new Vector3f(base.x(), base.y(), base.z());
    }

    private Vector4f extend(Vector3f base, float w) {
        return new Vector4f(base.x(), base.y(), base.z(), w);
    }

    private float lengthSquared(Vector3f v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();
        return x * x + y * y + z * z;
    }

    private Vector3f cross(Vector3f first, Vector3f second) {
        Vector3f result = new Vector3f(first.x(), first.y(), first.z());
        result.cross((Vector3fc)second);
        return result;
    }

    private void addEdgePlane(Vector4f backPlane4, Vector4f frontPlane4) {
        Vector3f backPlaneNormal = this.truncate(backPlane4);
        Vector3f frontPlaneNormal = this.truncate(frontPlane4);
        Vector3f intersection = this.cross(backPlaneNormal, frontPlaneNormal);
        Vector3f edgePlaneNormal = this.cross(intersection, this.shadowLightVectorFromOrigin);
        Vector3f ixb = this.cross(intersection, backPlaneNormal);
        Vector3f fxi = this.cross(frontPlaneNormal, intersection);
        ixb.mul(-frontPlane4.w());
        fxi.mul(-backPlane4.w());
        ixb.add((Vector3fc)fxi);
        Vector3f point = ixb;
        point.mul(1.0f / this.lengthSquared(intersection));
        float d = edgePlaneNormal.dot((Vector3fc)point);
        float w = -d;
        Vector4f plane = this.extend(edgePlaneNormal, w);
        this.addPlane(plane);
    }

    public void m_113002_(double cameraX, double cameraY, double cameraZ) {
        if (this.boxCuller != null) {
            this.boxCuller.setPosition(cameraX, cameraY, cameraZ);
        }
        this.x = cameraX;
        this.y = cameraY;
        this.z = cameraZ;
    }

    public boolean m_113029_(AABB aabb) {
        if (this.boxCuller != null && this.boxCuller.isCulled(aabb)) {
            return false;
        }
        return this.isVisible(aabb.f_82288_, aabb.f_82289_, aabb.f_82290_, aabb.f_82291_, aabb.f_82292_, aabb.f_82293_) != 0;
    }

    public int fastAabbTest(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        if (this.boxCuller != null && this.boxCuller.isCulled(minX, minY, minZ, maxX, maxY, maxZ)) {
            return 0;
        }
        return this.isVisible(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean canDetermineInvisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return false;
    }

    protected int isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        float f = (float)(minX - this.x);
        float g = (float)(minY - this.y);
        float h = (float)(minZ - this.z);
        float i = (float)(maxX - this.x);
        float j = (float)(maxY - this.y);
        float k = (float)(maxZ - this.z);
        return this.checkCornerVisibility(f, g, h, i, j, k);
    }

    protected int checkCornerVisibility(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        for (int i = 0; i < this.planeCount; ++i) {
            Vector4f plane = this.planes[i];
            float outsideBoundX = plane.x() < 0.0f ? minX : maxX;
            float outsideBoundY = plane.y() < 0.0f ? minY : maxY;
            float outsideBoundZ = plane.z() < 0.0f ? minZ : maxZ;
            if (!(Math.fma((float)plane.x(), (float)outsideBoundX, (float)Math.fma((float)plane.y(), (float)outsideBoundY, (float)(plane.z() * outsideBoundZ))) < -plane.w())) continue;
            return 0;
        }
        return 2;
    }

    public boolean checkCornerVisibilityBool(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        for (int i = 0; i < this.planeCount; ++i) {
            float f = this.planes[i].x * (this.planes[i].x < 0.0f ? minX : maxX) + this.planes[i].y * (this.planes[i].y < 0.0f ? minY : maxY);
            float f2 = this.planes[i].z;
            float f3 = this.planes[i].z < 0.0f ? minZ : maxZ;
            if (!(f + f2 * f3 < -this.planes[i].w)) continue;
            return false;
        }
        return true;
    }
}

