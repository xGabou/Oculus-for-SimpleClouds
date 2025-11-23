/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.shadows.frustum.advanced;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class BaseClippingPlanes {
    private final Vector4f[] planes = new Vector4f[6];

    public BaseClippingPlanes(Matrix4f playerView, Matrix4f playerProjection) {
        this.init(playerView, playerProjection);
    }

    private static Vector4f transform(Matrix4f transform, float x, float y, float z) {
        Vector4f vector4f = new Vector4f(x, y, z, 1.0f);
        vector4f.mul((Matrix4fc)transform);
        vector4f.normalize();
        return vector4f;
    }

    private void init(Matrix4f view, Matrix4f projection) {
        Matrix4f transform = new Matrix4f((Matrix4fc)projection);
        transform.mul((Matrix4fc)view);
        transform.transpose();
        this.planes[0] = BaseClippingPlanes.transform(transform, -1.0f, 0.0f, 0.0f);
        this.planes[1] = BaseClippingPlanes.transform(transform, 1.0f, 0.0f, 0.0f);
        this.planes[2] = BaseClippingPlanes.transform(transform, 0.0f, -1.0f, 0.0f);
        this.planes[3] = BaseClippingPlanes.transform(transform, 0.0f, 1.0f, 0.0f);
        this.planes[4] = BaseClippingPlanes.transform(transform, 0.0f, 0.0f, -1.0f);
        this.planes[5] = BaseClippingPlanes.transform(transform, 0.0f, 0.0f, 1.0f);
    }

    public Vector4f[] getPlanes() {
        return this.planes;
    }
}

