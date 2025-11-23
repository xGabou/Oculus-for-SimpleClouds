/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  org.lwjgl.system.MemoryUtil
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.nio.ByteBuffer;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={BufferBuilder.class})
public class MixinBufferBuilder
implements MemoryTrackingBuffer {
    @Shadow
    private ByteBuffer f_85648_;

    @Override
    public int getAllocatedSize() {
        return this.f_85648_.capacity();
    }

    @Override
    public int getUsedSize() {
        return this.f_85648_.position();
    }

    @Override
    public void freeAndDeleteBuffer() {
        if (this.f_85648_ == null) {
            return;
        }
        MemoryUtil.getAllocator((boolean)false).free(MemoryUtil.memAddress((ByteBuffer)this.f_85648_));
        this.f_85648_ = null;
    }
}

