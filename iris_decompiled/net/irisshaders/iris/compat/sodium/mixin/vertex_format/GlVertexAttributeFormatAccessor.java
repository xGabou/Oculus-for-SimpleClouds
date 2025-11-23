/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={GlVertexAttributeFormat.class})
public interface GlVertexAttributeFormatAccessor {
    @Invoker(value="<init>")
    public static GlVertexAttributeFormat createGlVertexAttributeFormat(int glId, int size) {
        throw new AssertionError((Object)"accessor failure");
    }
}

