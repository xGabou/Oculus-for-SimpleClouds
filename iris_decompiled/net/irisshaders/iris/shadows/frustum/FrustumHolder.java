/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 */
package net.irisshaders.iris.shadows.frustum;

import net.minecraft.client.renderer.culling.Frustum;

public class FrustumHolder {
    private Frustum frustum;
    private String distanceInfo = "(unavailable)";
    private String cullingInfo = "(unavailable)";

    public FrustumHolder setInfo(Frustum frustum, String distanceInfo, String cullingInfo) {
        this.frustum = frustum;
        this.distanceInfo = distanceInfo;
        this.cullingInfo = cullingInfo;
        return this;
    }

    public Frustum getFrustum() {
        return this.frustum;
    }

    public String getDistanceInfo() {
        return this.distanceInfo;
    }

    public String getCullingInfo() {
        return this.cullingInfo;
    }
}

