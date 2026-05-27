package net.Gabou.oculus_for_simpleclouds.mixin.dh;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.event.impl.DetermineCloudRenderPipelineEvent;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhEventBridge;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhPipeline;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareNoDhPipeline;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SC_PipelineTracer {
    private static String lastPipeline = "";
    private static long lastRenderBeforeLevelLogMs;

    @Inject(method = "getRenderPipeline", at = @At("RETURN"))
    private void ofscGetPipeline(CallbackInfoReturnable<Object> cir) {
        String pipeline = String.valueOf(cir.getReturnValue());
        if (!pipeline.equals(lastPipeline)) {
            System.out.println("[OFSC TRACE] getRenderPipeline returns " + pipeline);
            lastPipeline = pipeline;
        }
    }
    @Redirect(
            method = "renderBeforeLevel",
            at = @At(
                    value = "NEW",
                    target = "dev/nonamecrackers2/simpleclouds/client/event/impl/DetermineCloudRenderPipelineEvent"
            )
    )
    private DetermineCloudRenderPipelineEvent ofsc_replaceEvent(CloudsRenderPipeline ignored) {
        CloudsRenderPipeline pipeline;
        if (CompatHelper.areShadersRunning()) {
            pipeline = SimpleCloudsMod.dhLoaded() ? ShaderAwareDhPipeline.INSTANCE : ShaderAwareNoDhPipeline.INSTANCE;
        } else {
            pipeline = CloudsRenderPipeline.DEFAULT;
        }
        return new DetermineCloudRenderPipelineEvent(pipeline);
    }

    @Inject(method = "renderBeforeLevel", at = @At("TAIL"))
    private void ofsc_logRenderBeforeLevel(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (SimpleCloudsMod.dhLoaded()) {
            ShaderAwareDhEventBridge.register();
        }

        long now = System.currentTimeMillis();
        if (now - lastRenderBeforeLevelLogMs < 1000L) {
            return;
        }
        lastRenderBeforeLevelLogMs = now;

        SimpleCloudsRenderer renderer = (SimpleCloudsRenderer) (Object) this;
        CloudMeshGenerator generator = renderer.getMeshGenerator();
        int opaqueVerts = generator == null ? -1 : generator.getOpaqueBufferBytesUsed() / 24;
        int transparentVerts = generator == null ? -1 : generator.getTransparentBufferBytesUsed() / 24;
        int regions = -1;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            try {
                regions = CloudManager.get(mc.level).getCloudGenerator().getTotalCloudRegions();
            } catch (RuntimeException ignored) {
                regions = -2;
            }
        }

        System.out.println("[OFSC DEBUG] renderBeforeLevel: canRender="
                + SimpleCloudsRenderer.canRenderInDimension(mc.level)
                + " shaders=" + CompatHelper.areShadersRunning()
                + " dhLoaded=" + SimpleCloudsMod.dhLoaded()
                + " generateMesh=" + SimpleCloudsConfig.CLIENT.generateMesh.get()
                + " renderClouds=" + SimpleCloudsConfig.CLIENT.renderClouds.get()
                + " regions=" + regions
                + " meshStatus=" + (generator == null ? "null" : generator.getMeshGenStatus())
                + " opaqueVerts=" + opaqueVerts
                + " transparentVerts=" + transparentVerts);
    }





}
