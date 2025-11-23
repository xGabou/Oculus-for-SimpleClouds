/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.indium.mixin;

import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets={"link/infra/indium/renderer/render/AbstractBlockRenderContext"}, remap=false)
@Pseudo
public class MixinAbstractBlockRenderContext {
    private static int iris$multiplyRGB(int color, float shade) {
        int alpha = color >> 24 & 0xFF;
        int red = (int)((float)(color >> 16 & 0xFF) * shade);
        int green = (int)((float)(color >> 8 & 0xFF) * shade);
        int blue = (int)((float)(color & 0xFF) * shade);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    @Redirect(method={"shadeQuad", "shadeFlatQuad"}, at=@At(value="INVOKE", target="Llink/infra/indium/renderer/helper/ColorHelper;multiplyRGB(IF)I"), require=0)
    private int iris$separateAoColorMultiply(int color, float ao) {
        if (WorldRenderingSettings.INSTANCE.shouldUseSeparateAo()) {
            color &= 0xFFFFFF;
            return color |= (int)(ao * 255.0f) << 24;
        }
        return MixinAbstractBlockRenderContext.iris$multiplyRGB(color, ao);
    }
}

