/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  com.mojang.blaze3d.vertex.VertexFormatElement$Usage
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.vertices;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={VertexFormatElement.class})
public class MixinVertexFormatElement {
    @Inject(method={"supportsUsage"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$fixGenericAttributes(int index, VertexFormatElement.Usage type, CallbackInfoReturnable<Boolean> cir) {
        if (type == VertexFormatElement.Usage.GENERIC || type == VertexFormatElement.Usage.PADDING) {
            cir.setReturnValue((Object)true);
        }
    }
}

