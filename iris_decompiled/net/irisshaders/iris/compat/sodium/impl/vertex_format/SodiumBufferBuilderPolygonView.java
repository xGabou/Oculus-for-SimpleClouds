/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.TextureAttribute
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format;

import net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.TextureAttribute;
import net.irisshaders.iris.vertices.views.QuadView;

public class SodiumBufferBuilderPolygonView
implements QuadView {
    private long ptr;
    private int attributeOffsetPosition;
    private int attributeOffsetTexture;
    private int stride;
    private int vertexAmount;

    public void setup(long ptr, int attributeOffsetPosition, int attributeOffsetTexture, int stride, int vertexAmount) {
        this.ptr = ptr;
        this.attributeOffsetPosition = attributeOffsetPosition;
        this.attributeOffsetTexture = attributeOffsetTexture;
        this.stride = stride;
        this.vertexAmount = vertexAmount;
    }

    @Override
    public float x(int index) {
        return PositionAttribute.getX((long)(this.ptr + (long)this.attributeOffsetPosition - (long)this.stride * (long)(this.vertexAmount - index - 1)));
    }

    @Override
    public float y(int index) {
        return PositionAttribute.getY((long)(this.ptr + (long)this.attributeOffsetPosition - (long)this.stride * (long)(this.vertexAmount - index - 1)));
    }

    @Override
    public float z(int index) {
        return PositionAttribute.getZ((long)(this.ptr + (long)this.attributeOffsetPosition - (long)this.stride * (long)(this.vertexAmount - index - 1)));
    }

    @Override
    public float u(int index) {
        return TextureAttribute.getU((long)(this.ptr + (long)this.attributeOffsetTexture - (long)this.stride * (long)(this.vertexAmount - index - 1)));
    }

    @Override
    public float v(int index) {
        return TextureAttribute.getV((long)(this.ptr + (long)this.attributeOffsetTexture - (long)this.stride * (long)(this.vertexAmount - index - 1)));
    }
}

