/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 */
package net.irisshaders.iris.vertices;

import com.mojang.blaze3d.vertex.VertexFormat;

public interface IrisExtendedBufferBuilder {
    public VertexFormat iris$format();

    public VertexFormat.Mode iris$mode();

    public boolean iris$extending();

    public boolean iris$isTerrain();

    public boolean iris$injectNormalAndUV1();

    public int iris$vertexCount();

    public void iris$incrementVertexCount();

    public void iris$resetVertexCount();

    public short iris$currentBlock();

    public short iris$currentRenderType();

    public int iris$currentLocalPosX();

    public int iris$currentLocalPosY();

    public int iris$currentLocalPosZ();
}

