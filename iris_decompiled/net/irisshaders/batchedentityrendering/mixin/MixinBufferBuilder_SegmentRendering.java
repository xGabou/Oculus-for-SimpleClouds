/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics
 *  org.lwjgl.system.MemoryUtil
 *  org.spongepowered.asm.mixin.Dynamic
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics;
import net.irisshaders.batchedentityrendering.impl.BufferBuilderExt;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BufferBuilder.class}, priority=1010)
public class MixinBufferBuilder_SegmentRendering
implements BufferBuilderExt {
    @Shadow
    private ByteBuffer f_85648_;
    @Shadow
    private VertexFormat f_85658_;
    @Shadow
    private int f_85654_;
    @Shadow
    private int f_85652_;
    @Unique
    private boolean dupeNextVertex;

    @Shadow
    private void m_85665_() {
        throw new AssertionError((Object)"not shadowed");
    }

    @Override
    public void splitStrip() {
        if (this.f_85654_ == 0) {
            return;
        }
        this.duplicateLastVertex();
        this.dupeNextVertex = true;
    }

    private void duplicateLastVertex() {
        int i = this.f_85658_.m_86020_();
        MemoryIntrinsics.copyMemory((long)MemoryUtil.memAddress((ByteBuffer)this.f_85648_, (int)(this.f_85652_ - i)), (long)MemoryUtil.memAddress((ByteBuffer)this.f_85648_, (int)this.f_85652_), (int)i);
        this.f_85652_ += i;
        ++this.f_85654_;
        this.m_85665_();
    }

    @Inject(method={"end"}, at={@At(value="RETURN")})
    private void batchedentityrendering$onEnd(CallbackInfoReturnable<BufferBuilder.RenderedBuffer> cir) {
        this.dupeNextVertex = false;
    }

    @Inject(method={"endVertex"}, at={@At(value="RETURN")})
    private void batchedentityrendering$onNext(CallbackInfo ci) {
        if (this.dupeNextVertex) {
            this.dupeNextVertex = false;
            this.duplicateLastVertex();
        }
    }

    @Inject(method={"sodium$moveToNextVertex"}, at={@At(value="RETURN")}, require=0)
    @Dynamic
    private void batchedentityrendering$onNextSodium(CallbackInfo ci) {
        if (this.dupeNextVertex) {
            this.dupeNextVertex = false;
            this.duplicateLastVertex();
        }
    }
}

