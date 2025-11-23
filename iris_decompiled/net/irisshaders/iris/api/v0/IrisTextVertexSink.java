/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 */
package net.irisshaders.iris.api.v0;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;

public interface IrisTextVertexSink {
    public VertexFormat getUnderlyingVertexFormat();

    public ByteBuffer getUnderlyingByteBuffer();

    public void quad(float var1, float var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9, float var10, int var11);
}

