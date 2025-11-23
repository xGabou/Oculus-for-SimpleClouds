/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterRenderEvent
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package dev.nonamecrackers2.simpleclouds.client.dh.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterRenderEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import dev.nonamecrackers2.simpleclouds.client.dh.SimpleCloudsDhCompatHandler;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class SimpleCloudsAfterDhRenderHandler
extends DhApiAfterRenderEvent {
    public void afterRender(DhApiEventParam<Void> event) {
        if (SimpleCloudsDhCompatHandler._isPassComplete()) {
            return;
        }
        SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
        CloudsRenderPipeline pipeline = renderer.getRenderPipeline();
        Minecraft mc = Minecraft.m_91087_();
        Vec3 camPos = mc.f_91063_.m_109153_().m_90583_();
        Matrix4f projMat = SimpleCloudsDhCompatHandler._getDhProjMat();
        Matrix4f modelView = SimpleCloudsDhCompatHandler._getDhModelViewMat();
        PoseStack stack = new PoseStack();
        stack.m_166856_();
        stack.m_85850_().m_252922_().set((Matrix4fc)modelView);
        float partialTick = mc.getPartialTick();
        int fbo = SimpleCloudsDhCompatHandler._getDhFramebufferId();
        if (SimpleCloudsRenderer.canRenderInDimension(mc.f_91073_)) {
            pipeline.afterDistantHorizonsRender(mc, renderer, stack, projMat, partialTick, camPos.f_82479_, camPos.f_82480_, camPos.f_82481_, renderer.getCullFrustum(), fbo);
        }
        SimpleCloudsDhCompatHandler._updateDhFramebufferId(0);
        SimpleCloudsDhCompatHandler._updateCachedDhState(null, null);
        SimpleCloudsDhCompatHandler._markPassComplete(true);
    }
}

