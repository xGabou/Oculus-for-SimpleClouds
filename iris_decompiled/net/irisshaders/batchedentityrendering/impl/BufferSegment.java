/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;

public record BufferSegment(BufferBuilder.RenderedBuffer renderedBuffer, RenderType type) {
}

