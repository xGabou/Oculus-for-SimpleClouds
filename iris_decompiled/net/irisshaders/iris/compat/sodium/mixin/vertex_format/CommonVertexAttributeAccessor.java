/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.CommonVertexAttribute
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.caffeinemc.mods.sodium.api.vertex.attributes.CommonVertexAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={CommonVertexAttribute.class})
public interface CommonVertexAttributeAccessor {
    @Invoker(value="<init>")
    public static CommonVertexAttribute createCommonVertexElement(String name, int ordinal, VertexFormatElement element) {
        throw new AssertionError();
    }
}

