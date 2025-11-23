/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.dimension.DimensionType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.mixin;

import java.util.OptionalLong;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={DimensionType.class})
public interface DimensionTypeAccessor {
    @Accessor
    public OptionalLong getFixedTime();

    @Accessor
    public float getAmbientLight();
}

