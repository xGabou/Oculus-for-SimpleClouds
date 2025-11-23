/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  net.minecraft.client.renderer.PostChain
 *  net.minecraft.client.renderer.PostPass
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={PostChain.class})
public interface MixinPostChain {
    @Accessor(value="passes")
    public List<PostPass> simpleclouds$getPostPasses();

    @Accessor(value="customRenderTargets")
    public Map<String, RenderTarget> simpleclouds$getCustomRenderTargets();

    @Accessor(value="screenTarget")
    public RenderTarget simpleclouds$getScreenTarget();
}

