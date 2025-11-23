/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.vertices;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.ImmediateState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VertexFormat.class})
public class MixinVertexFormat {
    @Inject(method={"setupBufferState"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$onSetupBufferState(CallbackInfo ci) {
        if (WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat() && ImmediateState.renderWithExtendedVertexFormat) {
            if (this == DefaultVertexFormat.f_85811_) {
                IrisVertexFormats.TERRAIN.m_166912_();
                ci.cancel();
            } else if (this == DefaultVertexFormat.f_85820_) {
                IrisVertexFormats.GLYPH.m_166912_();
                ci.cancel();
            } else if (this == DefaultVertexFormat.f_85812_) {
                IrisVertexFormats.ENTITY.m_166912_();
                ci.cancel();
            }
        }
    }

    @Inject(method={"clearBufferState"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$onClearBufferState(CallbackInfo ci) {
        if (WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat() && ImmediateState.renderWithExtendedVertexFormat) {
            if (this == DefaultVertexFormat.f_85811_) {
                IrisVertexFormats.TERRAIN.m_86024_();
                ci.cancel();
            } else if (this == DefaultVertexFormat.f_85820_) {
                IrisVertexFormats.GLYPH.m_86024_();
                ci.cancel();
            } else if (this == DefaultVertexFormat.f_85812_) {
                IrisVertexFormats.ENTITY.m_86024_();
                ci.cancel();
            }
        }
    }
}

