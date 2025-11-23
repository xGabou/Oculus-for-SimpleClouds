/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.Sheets
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.batchedentityrendering.mixin;

import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Sheets.class})
public class MixinSheets {
    @Shadow
    @Final
    private static RenderType f_266092_;

    @Inject(method={"<clinit>"}, at={@At(value="TAIL")})
    private static void setSheet(CallbackInfo ci) {
        ((BlendingStateHolder)f_266092_).setTransparencyType(TransparencyType.OPAQUE_DECAL);
        ((BlendingStateHolder)RenderType.m_269058_()).setTransparencyType(TransparencyType.OPAQUE);
        ((BlendingStateHolder)RenderType.m_269508_()).setTransparencyType(TransparencyType.OPAQUE);
    }
}

