/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultedVertexConsumer
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package net.irisshaders.iris.mixin.vertices.block_rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={BufferBuilder.class}, priority=999)
public abstract class MixinBufferBuilder_SeparateAo
extends DefaultedVertexConsumer {
    private float[] brightnesses;
    private int brightnessIndex;

    public void m_85995_(PoseStack.Pose matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, int[] lights, int overlay, boolean useQuadColorData) {
        if (WorldRenderingSettings.INSTANCE.shouldUseSeparateAo()) {
            this.brightnesses = brightnesses;
            this.brightnessIndex = 0;
            brightnesses = new float[brightnesses.length];
            Arrays.fill(brightnesses, 1.0f);
        }
        super.m_85995_(matrixEntry, quad, brightnesses, red, green, blue, lights, overlay, useQuadColorData);
    }

    @ModifyVariable(method={"vertex"}, at=@At(value="HEAD"), index=7, argsOnly=true)
    public float vertex(float alpha) {
        if (this.brightnesses != null && WorldRenderingSettings.INSTANCE.shouldUseSeparateAo()) {
            if (this.brightnessIndex < this.brightnesses.length) {
                alpha = this.brightnesses[this.brightnessIndex++];
            } else {
                this.brightnesses = null;
            }
        }
        return alpha;
    }
}

