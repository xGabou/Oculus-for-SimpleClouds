/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttribute
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat$Builder
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import java.util.EnumMap;
import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttribute;
import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat;
import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={GlVertexFormat.Builder.class})
public class MixinGlVertexFormatBuilder<T extends Enum<T>> {
    private static final GlVertexAttribute EMPTY = new GlVertexAttribute(GlVertexAttributeFormat.FLOAT, 0, false, 0, 0, false);
    @Shadow
    @Final
    private int stride;
    @Shadow
    @Final
    private EnumMap<T, GlVertexAttribute> attributes;

    @Redirect(method={"build"}, at=@At(value="INVOKE", target="java/util/EnumMap.get (Ljava/lang/Object;)Ljava/lang/Object;"), remap=false)
    private Object iris$suppressMissingAttributes(EnumMap<?, ?> map, Object key) {
        Object value = map.get(key);
        if (value == null) {
            return EMPTY;
        }
        return value;
    }

    @Overwrite(remap=false)
    private GlVertexFormat.Builder<T> addElement(T type, GlVertexAttribute attribute) {
        if (this.attributes.put(type, attribute) != null) {
            throw new IllegalStateException("Generic attribute " + ((Enum)type).name() + " already defined in vertex format");
        }
        return (GlVertexFormat.Builder)this;
    }
}

