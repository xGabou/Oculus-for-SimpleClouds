/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.model.light.flat.FlatLightPipeline
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.directional_shading;

import me.jellysquid.mods.sodium.client.model.light.flat.FlatLightPipeline;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FlatLightPipeline.class})
public class MixinFlatLightPipeline {
    @Redirect(method={"calculate"}, at=@At(value="INVOKE", target="net/minecraft/world/level/BlockAndTintGetter.getShade (Lnet/minecraft/core/Direction;Z)F"))
    private float iris$getBrightness(BlockAndTintGetter level, Direction direction, boolean shaded) {
        if (WorldRenderingSettings.INSTANCE.shouldDisableDirectionalShading()) {
            return 1.0f;
        }
        return level.m_7717_(direction, shaded);
    }
}

