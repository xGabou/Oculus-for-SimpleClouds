/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  org.joml.Vector3f
 */
package net.irisshaders.iris.vertices;

import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class NormI8 {
    private static final int X_COMPONENT_OFFSET = 0;
    private static final int Y_COMPONENT_OFFSET = 8;
    private static final int Z_COMPONENT_OFFSET = 16;
    private static final int W_COMPONENT_OFFSET = 24;
    private static final float COMPONENT_RANGE = 127.0f;
    private static final float NORM = 0.007874016f;

    public static int pack(Vector3f normal) {
        return NormI8.pack(normal.x(), normal.y(), normal.z(), 0.0f);
    }

    public static int pack(float x, float y, float z, float w) {
        return (int)(x * 127.0f) & 0xFF | ((int)(y * 127.0f) & 0xFF) << 8 | ((int)(z * 127.0f) & 0xFF) << 16 | ((int)(w * 127.0f) & 0xFF) << 24;
    }

    public static int packColor(float x, float y, float z, float w) {
        return (int)(x * 127.0f) & 0xFF | ((int)(y * 127.0f) & 0xFF) << 8 | ((int)(z * 127.0f) & 0xFF) << 16 | ((int)w & 0xFF) << 24;
    }

    private static int encode(float comp) {
        return (int)(Mth.m_14036_((float)comp, (float)-1.0f, (float)1.0f) * 127.0f) & 0xFF;
    }

    public static float unpackX(int norm) {
        return (float)((byte)(norm >> 0 & 0xFF)) * 0.007874016f;
    }

    public static float unpackY(int norm) {
        return (float)((byte)(norm >> 8 & 0xFF)) * 0.007874016f;
    }

    public static float unpackZ(int norm) {
        return (float)((byte)(norm >> 16 & 0xFF)) * 0.007874016f;
    }

    public static float unpackW(int norm) {
        return (float)((byte)(norm >> 24 & 0xFF)) * 0.007874016f;
    }
}

