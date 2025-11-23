/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package net.irisshaders.iris.mixin.vertices.block_rendering;

import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={ClientLevel.class})
public class MixinClientLevel {
    @ModifyVariable(method={"getShade(Lnet/minecraft/core/Direction;Z)F"}, at=@At(value="HEAD"), argsOnly=true)
    private boolean iris$maybeDisableDirectionalShading(boolean shaded) {
        if (WorldRenderingSettings.INSTANCE.shouldDisableDirectionalShading()) {
            return false;
        }
        return shaded;
    }

    @ModifyVariable(method={"getShade(FFFZ)F"}, at=@At(value="HEAD"), argsOnly=true, remap=false)
    private boolean iris$maybeDisableDirectionalShadingForge(boolean shaded) {
        if (WorldRenderingSettings.INSTANCE.shouldDisableDirectionalShading()) {
            return false;
        }
        return shaded;
    }
}

