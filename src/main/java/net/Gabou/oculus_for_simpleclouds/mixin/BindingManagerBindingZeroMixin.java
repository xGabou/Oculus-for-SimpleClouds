package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.BindingManager;
import it.unimi.dsi.fastutil.ints.IntList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BindingManager.class, remap = false)
public abstract class BindingManagerBindingZeroMixin {

    @Shadow
    private static IntList ALL_SHADER_STORAGE_BINDINGS;

    /**
     * Simple Clouds reserves SSBO bindings from the top down, but the stock loop
     * stops at 1 and leaves binding 0 unused. That turns a 16-slot pool into 15
     * usable slots and triggers reload crashes once the pool fills up.
     */
    @Overwrite
    public static int getAvailableShaderStorageBinding() {
        RenderSystem.assertOnRenderThread();
        int maxSSBOBindings = GL11.glGetInteger(37085);
        for (int i = maxSSBOBindings - 1; i >= 0; --i) {
            if (ALL_SHADER_STORAGE_BINDINGS.contains(i)) {
                continue;
            }
            return i;
        }
        throw new NullPointerException("No available buffer binding. Total available buffer bindings: " + maxSSBOBindings + ", used: " + ALL_SHADER_STORAGE_BINDINGS.size());
    }
}
