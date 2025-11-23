/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils
 *  net.caffeinemc.mods.sodium.api.util.ColorABGR
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  net.caffeinemc.mods.sodium.api.vertex.format.common.GlyphVertex
 *  net.minecraft.client.gui.font.glyphs.BakedGlyph
 *  org.joml.Math
 *  org.joml.Matrix4f
 *  org.lwjgl.system.MemoryStack
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.font;

import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.GlyphVertex;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.GlyphVertexExt;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BakedGlyph.class})
public class MixinGlyphRenderer {
    @Shadow
    @Final
    private float f_95205_;
    @Shadow
    @Final
    private float f_95206_;
    @Shadow
    @Final
    private float f_95207_;
    @Shadow
    @Final
    private float f_95208_;
    @Shadow
    @Final
    private float f_95201_;
    @Shadow
    @Final
    private float f_95203_;
    @Shadow
    @Final
    private float f_95204_;
    @Shadow
    @Final
    private float f_95202_;

    private static void write(boolean ext, long buffer, Matrix4f matrix, float x, float y, float z, int color, float u, float v, int light) {
        float x2 = Math.fma((float)matrix.m00(), (float)x, (float)Math.fma((float)matrix.m10(), (float)y, (float)Math.fma((float)matrix.m20(), (float)z, (float)matrix.m30())));
        float y2 = Math.fma((float)matrix.m01(), (float)x, (float)Math.fma((float)matrix.m11(), (float)y, (float)Math.fma((float)matrix.m21(), (float)z, (float)matrix.m31())));
        float z2 = Math.fma((float)matrix.m02(), (float)x, (float)Math.fma((float)matrix.m12(), (float)y, (float)Math.fma((float)matrix.m22(), (float)z, (float)matrix.m32())));
        if (ext) {
            GlyphVertexExt.write(buffer, x2, y2, z2, color, u, v, light);
        } else {
            GlyphVertex.put((long)buffer, (float)x2, (float)y2, (float)z2, (int)color, (float)u, (float)v, (int)light);
        }
    }

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void render(boolean italic, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light, CallbackInfo ci) {
        VertexBufferWriter writer = VertexConsumerUtils.convertOrLog((VertexConsumer)vertexConsumer);
        if (writer == null) {
            return;
        }
        ci.cancel();
        float x1 = x + this.f_95205_;
        float x2 = x + this.f_95206_;
        float y1 = this.f_95207_ - 3.0f;
        float y2 = this.f_95208_ - 3.0f;
        float h1 = y + y1;
        float h2 = y + y2;
        float w1 = italic ? 1.0f - 0.25f * y1 : 0.0f;
        float w2 = italic ? 1.0f - 0.25f * y2 : 0.0f;
        int color = ColorABGR.pack((float)red, (float)green, (float)blue, (float)alpha);
        boolean ext = this.extend();
        int stride = ext ? GlyphVertexExt.STRIDE : 28;
        try (MemoryStack stack = MemoryStack.stackPush();){
            long buffer;
            long ptr = buffer = stack.nmalloc(4 * stride);
            MixinGlyphRenderer.write(ext, ptr, matrix, x1 + w1, h1, 0.0f, color, this.f_95201_, this.f_95203_, light);
            MixinGlyphRenderer.write(ext, ptr += (long)stride, matrix, x1 + w2, h2, 0.0f, color, this.f_95201_, this.f_95204_, light);
            MixinGlyphRenderer.write(ext, ptr += (long)stride, matrix, x2 + w2, h2, 0.0f, color, this.f_95202_, this.f_95204_, light);
            MixinGlyphRenderer.write(ext, ptr += (long)stride, matrix, x2 + w1, h1, 0.0f, color, this.f_95202_, this.f_95203_, light);
            ptr += (long)stride;
            writer.push(stack, buffer, 4, ext ? GlyphVertexExt.FORMAT : GlyphVertex.FORMAT);
        }
    }

    private boolean extend() {
        return IrisApi.getInstance().isShaderPackInUse() && ImmediateState.renderWithExtendedVertexFormat;
    }
}

