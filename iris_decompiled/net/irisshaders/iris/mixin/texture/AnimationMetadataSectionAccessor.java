/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.metadata.animation.AnimationMetadataSection
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.mixin.texture;

import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={AnimationMetadataSection.class})
public interface AnimationMetadataSectionAccessor {
    @Accessor(value="frameWidth")
    public int getFrameWidth();

    @Mutable
    @Accessor(value="frameWidth")
    public void setFrameWidth(int var1);

    @Accessor(value="frameHeight")
    public int getFrameHeight();

    @Mutable
    @Accessor(value="frameHeight")
    public void setFrameHeight(int var1);
}

