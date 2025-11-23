/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  me.jellysquid.mods.sodium.client.model.quad.ModelQuadView
 *  me.jellysquid.mods.sodium.client.render.immediate.model.BakedModelEncoder
 *  me.jellysquid.mods.sodium.client.util.ModelQuadUtil
 *  net.caffeinemc.mods.sodium.api.math.MatrixHelper
 *  net.caffeinemc.mods.sodium.api.util.ColorABGR
 *  net.caffeinemc.mods.sodium.api.util.ColorU8
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex
 *  net.minecraft.core.Direction
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.lwjgl.system.MemoryStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import me.jellysquid.mods.sodium.client.util.ModelQuadUtil;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.util.ColorU8;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ModelVertex;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.EntityVertex;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BakedModelEncoder.class})
public class MixinModelVertex {
    @Shadow
    private static int mergeNormalAndMult(int packed, int calc, Matrix3f matNormal) {
        throw new AssertionError();
    }

    @Inject(method={"writeQuadVertices(Lnet/caffeinemc/mods/sodium/api/vertex/buffer/VertexBufferWriter;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;III)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void redirect2(VertexBufferWriter writer, PoseStack.Pose matrices, ModelQuadView quad, int color, int light, int overlay, CallbackInfo ci) {
        if (MixinModelVertex.shouldBeExtended()) {
            ci.cancel();
            EntityVertex.writeQuadVertices(writer, matrices, quad, light, overlay, color);
        }
    }

    @Overwrite(remap=false)
    public static void writeQuadVertices(VertexBufferWriter writer, PoseStack.Pose matrices, ModelQuadView quad, float r, float g, float b, float[] brightnessTable, boolean colorize, int[] light, int overlay) {
        Matrix3f matNormal = matrices.m_252943_();
        Matrix4f matPosition = matrices.m_252922_();
        try (MemoryStack stack = MemoryStack.stackPush();){
            long buffer;
            long ptr = buffer = stack.nmalloc(144);
            int normal = MatrixHelper.transformNormal((Matrix3f)matNormal, (Direction)quad.getLightFace());
            for (int i = 0; i < 4; ++i) {
                float fB;
                float fG;
                float fR;
                int color;
                float x = quad.getX(i);
                float y = quad.getY(i);
                float z = quad.getZ(i);
                float xt = MatrixHelper.transformPositionX((Matrix4f)matPosition, (float)x, (float)y, (float)z);
                float yt = MatrixHelper.transformPositionY((Matrix4f)matPosition, (float)x, (float)y, (float)z);
                float zt = MatrixHelper.transformPositionZ((Matrix4f)matPosition, (float)x, (float)y, (float)z);
                float brightness = brightnessTable[i];
                if (colorize) {
                    color = quad.getColor(i);
                    float oR = ColorU8.byteToNormalizedFloat((int)ColorABGR.unpackRed((int)color));
                    float oG = ColorU8.byteToNormalizedFloat((int)ColorABGR.unpackGreen((int)color));
                    float oB = ColorU8.byteToNormalizedFloat((int)ColorABGR.unpackBlue((int)color));
                    fR = oR * brightness * r;
                    fG = oG * brightness * g;
                    fB = oB * brightness * b;
                } else {
                    fR = brightness * r;
                    fG = brightness * g;
                    fB = brightness * b;
                }
                color = ColorABGR.pack((float)fR, (float)fG, (float)fB, (float)1.0f);
                ModelVertex.write((long)ptr, (float)xt, (float)yt, (float)zt, (int)color, (float)quad.getTexU(i), (float)quad.getTexV(i), (int)overlay, (int)ModelQuadUtil.mergeBakedLight((int)quad.getLight(i), (int)light[i]), (int)MixinModelVertex.mergeNormalAndMult(quad.getForgeNormal(i), normal, matNormal));
                ptr += 36L;
            }
            writer.push(stack, buffer, 4, ModelVertex.FORMAT);
        }
    }

    private static boolean shouldBeExtended() {
        return IrisApi.getInstance().isShaderPackInUse() && ImmediateState.renderWithExtendedVertexFormat;
    }
}

