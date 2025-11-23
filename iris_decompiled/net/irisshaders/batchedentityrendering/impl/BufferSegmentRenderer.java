/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.irisshaders.batchedentityrendering.impl.BufferSegment;

public class BufferSegmentRenderer {
    public void draw(BufferSegment segment) {
        if (segment.renderedBuffer() != null) {
            segment.type().m_110185_();
            this.drawInner(segment);
            segment.type().m_110188_();
        }
    }

    public void drawInner(BufferSegment segment) {
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)segment.renderedBuffer());
    }
}

