/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.math.MatrixHelper
 *  net.caffeinemc.mods.sodium.api.util.ColorABGR
 *  net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format.entity;

import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.EntityVertex;
import net.irisshaders.iris.vertices.ImmediateState;
import net.irisshaders.iris.vertices.NormI8;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={EntityRenderDispatcher.class}, priority=1010)
public class MixinEntityRenderDispatcher {
    @Unique
    private static final int SHADOW_COLOR = ColorABGR.pack((float)1.0f, (float)1.0f, (float)1.0f);

    private static void writeShadowVertexIris(long ptr, Matrix4f matPosition, float x, float y, float z, float u, float v, int color, float midU, float midV, int normal, int tangent) {
        float xt = MatrixHelper.transformPositionX((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        float yt = MatrixHelper.transformPositionY((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        float zt = MatrixHelper.transformPositionZ((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        EntityVertex.write(ptr, xt, yt, zt, color, u, v, midU, midV, OverlayTexture.f_118083_, 0xF000F0, normal, tangent);
    }

    private static void writeShadowVertex(long ptr, Matrix4f matPosition, float x, float y, float z, float u, float v, int color, int normal) {
        float xt = MatrixHelper.transformPositionX((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        float yt = MatrixHelper.transformPositionY((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        float zt = MatrixHelper.transformPositionZ((Matrix4f)matPosition, (float)x, (float)y, (float)z);
        ModelVertex.write((long)ptr, (float)xt, (float)yt, (float)zt, (int)color, (float)u, (float)v, (int)OverlayTexture.f_118083_, (int)0xF000F0, (int)normal);
    }

    private static boolean shouldBeExtended() {
        return IrisApi.getInstance().isShaderPackInUse() && ImmediateState.renderWithExtendedVertexFormat;
    }

    private static int getTangent(int normal, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2) {
        float normalX = NormI8.unpackX(normal);
        float normalY = NormI8.unpackY(normal);
        float normalZ = NormI8.unpackZ(normal);
        float edge1x = x1 - x0;
        float edge1y = y1 - y0;
        float edge1z = z1 - z0;
        float edge2x = x2 - x0;
        float edge2y = y2 - y0;
        float edge2z = z2 - z0;
        float deltaU1 = u1 - u0;
        float deltaV2 = v2 - v0;
        float deltaU2 = u2 - u0;
        float deltaV1 = v1 - v0;
        float fdenom = deltaU1 * deltaV2 - deltaU2 * deltaV1;
        float f = (double)fdenom == 0.0 ? 1.0f : 1.0f / fdenom;
        float tangentx = f * (deltaV2 * edge1x - deltaV1 * edge2x);
        float tangenty = f * (deltaV2 * edge1y - deltaV1 * edge2y);
        float tangentz = f * (deltaV2 * edge1z - deltaV1 * edge2z);
        float tcoeff = MixinEntityRenderDispatcher.rsqrt(tangentx * tangentx + tangenty * tangenty + tangentz * tangentz);
        tangentx *= tcoeff;
        tangenty *= tcoeff;
        float bitangentx = f * (-deltaU2 * edge1x + deltaU1 * edge2x);
        float bitangenty = f * (-deltaU2 * edge1y + deltaU1 * edge2y);
        float bitangentz = f * (-deltaU2 * edge1z + deltaU1 * edge2z);
        float bitcoeff = MixinEntityRenderDispatcher.rsqrt(bitangentx * bitangentx + bitangenty * bitangenty + bitangentz * bitangentz);
        float pbitangentx = tangenty * normalZ - (tangentz *= tcoeff) * normalY;
        float pbitangenty = tangentz * normalX - tangentx * normalZ;
        float pbitangentz = tangentx * normalY - tangenty * normalX;
        float dot = (bitangentx *= bitcoeff) * pbitangentx + (bitangenty *= bitcoeff) * pbitangenty + (bitangentz *= bitcoeff) * pbitangentz;
        float tangentW = dot < 0.0f ? -1.0f : 1.0f;
        return NormI8.pack(tangentx, tangenty, tangentz, tangentW);
    }

    private static float rsqrt(float value) {
        if (value == 0.0f) {
            return 1.0f;
        }
        return (float)(1.0 / Math.sqrt(value));
    }
}

