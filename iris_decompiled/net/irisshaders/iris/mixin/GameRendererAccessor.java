/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.renderer.GameRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={GameRenderer.class})
public interface GameRendererAccessor {
    @Accessor
    public boolean getRenderHand();

    @Accessor
    public boolean getPanoramicMode();

    @Invoker
    public void invokeBobView(PoseStack var1, float var2);

    @Invoker
    public void invokeBobHurt(PoseStack var1, float var2);

    @Invoker
    public double invokeGetFov(Camera var1, float var2, boolean var3);
}

