//package net.Gabou.oculus_for_simpleclouds.dh;
//
//import dev.nonamecrackers2.simpleclouds.client.renderer.pipeline.CloudsRenderPipeline;
//
///**
// * Shader-aware pipeline used when Distant Horizons is not loaded.
// * Skips all DH-specific depth merging while keeping shader integration.
// */
//public class ShaderAwareNoDhPipeline extends ShaderAwarePipelineBase {
//    public static final ShaderAwareNoDhPipeline INSTANCE = new ShaderAwareNoDhPipeline();
//
//    private ShaderAwareNoDhPipeline() {
//        super(CloudsRenderPipeline.SHADER_SUPPORT);
//    }
//
//    @Override
//    protected boolean allowDhDepthMerge() {
//        return false;
//    }
//
//    @Override
//    protected boolean shouldRebindDhFbo() {
//        return false;
//    }
//
//    @Override
//    public String toString() {
//        return "ShaderAwareNoDhPipeline";
//    }
//}
