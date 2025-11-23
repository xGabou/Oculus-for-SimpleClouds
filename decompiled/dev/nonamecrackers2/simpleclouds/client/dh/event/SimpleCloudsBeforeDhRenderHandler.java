/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeApplyShaderRenderEvent
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package dev.nonamecrackers2.simpleclouds.client.dh.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeApplyShaderRenderEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import dev.nonamecrackers2.simpleclouds.client.dh.SimpleCloudsDhCompatHandler;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class SimpleCloudsBeforeDhRenderHandler
extends DhApiBeforeApplyShaderRenderEvent {
    public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> event) {
        SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
        CloudsRenderPipeline pipeline = renderer.getRenderPipeline();
        Minecraft mc = Minecraft.m_91087_();
        Vec3 camPos = mc.f_91063_.m_109153_().m_90583_();
        SimpleCloudsDhCompatHandler._markPassComplete(false);
        DhApiRenderParam params = (DhApiRenderParam)event.value;
        Matrix4f projMat = new Matrix4f().setTransposed(params.dhProjectionMatrix.getValuesAsArray());
        Matrix4f modelView = new Matrix4f().setTransposed(params.mcModelViewMatrix.getValuesAsArray());
        PoseStack stack = new PoseStack();
        stack.m_166856_();
        stack.m_85850_().m_252922_().set((Matrix4fc)modelView);
        SimpleCloudsDhCompatHandler._updateCachedDhState(projMat, modelView);
        int fbo = SimpleCloudsDhCompatHandler._getDhFramebufferId();
        if (SimpleCloudsRenderer.canRenderInDimension(mc.f_91073_)) {
            pipeline.beforeDistantHorizonsApplyShader(mc, renderer, stack, projMat, params.partialTicks, camPos.f_82479_, camPos.f_82480_, camPos.f_82481_, renderer.getCullFrustum(), fbo);
        }
    }
}

