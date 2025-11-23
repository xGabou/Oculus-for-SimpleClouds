/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Coerce
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.pixelmon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets={"com/pixelmonmod/pixelmon/client/models/smd/NormalizedFace"})
public class MixinNormalizedFace {
    @Redirect(method={"addFaceForRender"}, at=@At(value="FIELD", opcode=180, target="Lcom/pixelmonmod/pixelmon/client/models/smd/DeformVertex;id2:I"))
    public int hideBufferBuilderId(@Coerce Object instance) {
        return -1;
    }
}

