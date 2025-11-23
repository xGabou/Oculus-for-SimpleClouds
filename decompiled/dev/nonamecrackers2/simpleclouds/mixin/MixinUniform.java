/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Uniform
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import com.mojang.blaze3d.shaders.Uniform;
import java.nio.FloatBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={Uniform.class})
public abstract class MixinUniform {
    @Redirect(method={"set([F)V"}, at=@At(value="INVOKE", target="Ljava/nio/FloatBuffer;put([F)Ljava/nio/FloatBuffer;"))
    private FloatBuffer simpleclouds$fixMatrixBug(FloatBuffer buffer, float[] values) {
        return buffer.put(values, 0, buffer.limit());
    }
}

