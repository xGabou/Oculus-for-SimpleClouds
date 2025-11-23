/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class RenderTypeUtil {
    public static boolean isTriangleStripDrawMode(RenderType renderType) {
        return renderType.m_173186_() == VertexFormat.Mode.TRIANGLE_STRIP;
    }
}

