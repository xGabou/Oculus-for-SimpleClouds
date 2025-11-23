/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.vertices;

public final class ExtendedDataHelper {
    public static final short BLOCK_RENDER_TYPE = -1;
    public static final short FLUID_RENDER_TYPE = 1;

    public static int packMidBlock(float x, float y, float z) {
        return (int)(x * 64.0f) & 0xFF | ((int)(y * 64.0f) & 0xFF) << 8 | ((int)(z * 64.0f) & 0xFF) << 16;
    }

    public static int computeMidBlock(float x, float y, float z, int localPosX, int localPosY, int localPosZ) {
        return ExtendedDataHelper.packMidBlock((float)localPosX + 0.5f - x, (float)localPosY + 0.5f - y, (float)localPosZ + 0.5f - z);
    }
}

