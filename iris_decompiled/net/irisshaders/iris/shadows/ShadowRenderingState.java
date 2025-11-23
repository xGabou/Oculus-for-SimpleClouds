/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 */
package net.irisshaders.iris.shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowRenderingState {
    private static BlockEntityRenderFunction function = ShadowRenderer::renderBlockEntities;

    public static boolean areShadowsCurrentlyBeingRendered() {
        return ShadowRenderer.ACTIVE;
    }

    public static void setBlockEntityRenderFunction(BlockEntityRenderFunction function) {
        ShadowRenderingState.function = function;
    }

    public static int renderBlockEntities(ShadowRenderer shadowRenderer, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, Camera camera, double cameraX, double cameraY, double cameraZ, float tickDelta, boolean hasEntityFrustum, boolean lightsOnly) {
        return function.renderBlockEntities(shadowRenderer, bufferSource, modelView, camera, cameraX, cameraY, cameraZ, tickDelta, hasEntityFrustum, lightsOnly);
    }

    public static int getRenderDistance() {
        return ShadowRenderer.renderDistance;
    }

    public static interface BlockEntityRenderFunction {
        public int renderBlockEntities(ShadowRenderer var1, MultiBufferSource.BufferSource var2, PoseStack var3, Camera var4, double var5, double var7, double var9, float var11, boolean var12, boolean var13);
    }
}

