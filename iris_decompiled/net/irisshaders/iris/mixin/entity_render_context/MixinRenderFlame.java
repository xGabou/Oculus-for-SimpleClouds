/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.mixin.entity_render_context;

import java.util.function.Function;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pathways.LightningHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets={"mekanism/client/render/entity/RenderFlame"}, remap=false)
public class MixinRenderFlame {
    private static Object MEKANISM_FLAME;

    @Redirect(method={"render(Lmekanism/common/entity/EntityFlame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="FIELD", target="Lmekanism/client/render/MekanismRenderType;FLAME:Ljava/util/function/Function;"))
    private Function<ResourceLocation, RenderType> doNotSwitchShaders() {
        if (Iris.isPackInUseQuick()) {
            return LightningHandler.MEKANISM_FLAME;
        }
        return (Function)MEKANISM_FLAME;
    }

    static {
        try {
            MEKANISM_FLAME = Class.forName("mekanism.client.render.MekanismRenderType").getField("FLAME").get(null);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            Iris.logger.fatal("Failed to get Mekanism flame!");
        }
    }
}

