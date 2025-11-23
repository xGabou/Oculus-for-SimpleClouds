/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  me.jellysquid.mods.sodium.client.render.vertex.VertexFormatDescriptionImpl
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import com.mojang.blaze3d.vertex.VertexFormat;
import me.jellysquid.mods.sodium.client.render.vertex.VertexFormatDescriptionImpl;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={VertexFormatDescriptionImpl.class})
public class MixinVertexFormatDescriptionImpl {
    @Inject(remap=false, method={"checkSimple"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$forceSimple(VertexFormat format, CallbackInfoReturnable<Boolean> cir) {
        if (format == IrisVertexFormats.TERRAIN || format == IrisVertexFormats.ENTITY || format == IrisVertexFormats.GLYPH) {
            cir.setReturnValue((Object)true);
        }
    }
}

