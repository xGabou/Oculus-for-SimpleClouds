/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.mixin;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={LightTexture.class})
public interface LightTextureAccessor {
    @Accessor(value="lightTexture")
    public DynamicTexture getLightTexture();
}

