/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format;

import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat;
import net.irisshaders.iris.compat.sodium.mixin.vertex_format.GlVertexAttributeFormatAccessor;

public class IrisGlVertexAttributeFormat {
    public static final GlVertexAttributeFormat BYTE = GlVertexAttributeFormatAccessor.createGlVertexAttributeFormat(5120, 1);
    public static final GlVertexAttributeFormat SHORT = GlVertexAttributeFormatAccessor.createGlVertexAttributeFormat(5122, 2);
}

