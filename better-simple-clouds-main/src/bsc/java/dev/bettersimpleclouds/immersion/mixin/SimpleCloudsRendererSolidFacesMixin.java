package dev.bettersimpleclouds.immersion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;
import dev.bettersimpleclouds.immersion.InteriorFillState;

import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;

/**
 * Keeps every exposed cloud face, so a Simple Clouds cloud stays solid when you are inside it (or looking at its far
 * or top side) instead of being see-through.
 *
 * <p>Simple Clouds meshes its clouds on the GPU and, by default, skips any cube face whose normal points away from
 * the camera (the compute shader's {@code TestFacesFacingAway} uniform, fed from the {@code testSidesThatAreOccluded}
 * client option, which defaults to off). That view-dependent culling drops exactly the faces you see from
 * <em>inside</em> a cloud: the surrounding surface faces all point outward, away from the camera, so none are
 * generated and you look straight through. The opaque cloud pass already draws with back-face culling disabled
 * ({@code RenderSystem.disableCull()}), so once those faces exist they show from both sides - no extra work needed
 * there.</p>
 *
 * <p>{@code SimpleCloudsRenderer#prepareMeshGenerator} re-applies that flag from the config every frame, immediately
 * before the mesh is regenerated, so setting it from elsewhere (e.g. once per client tick) is clobbered before it can
 * take effect. We inject at the tail of that method - after Simple Clouds has set the flag, before the mesh is
 * regenerated in the same frame - and force it on whenever the in-cloud patch and its solid-faces option are enabled,
 * leaving Simple Clouds' own value untouched when they're not.</p>
 *
 * <p>Gated on {@code simpleclouds} by {@link dev.bettersimpleclouds.immersion.SimpleCloudsImmersionMixinPlugin
 * the patch's mixin plugin}; client-only, since {@link SimpleCloudsRenderer} only exists on the client.</p>
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class SimpleCloudsRendererSolidFacesMixin {

    @Shadow
    public abstract CloudMeshGenerator getMeshGenerator();

    @Inject(method = "prepareMeshGenerator", at = @At("TAIL"))
    private void bsc$forceSolidCloudFaces(final float partialTicks, final CallbackInfo ci) {
        if (!BetterSimpleCloudsConfig.inCloudEnabled() || !BetterSimpleCloudsConfig.inCloudSolidFaces())
            return;
        // Solid (away-facing) faces only matter while you're inside a cloud (from outside, the camera-facing side is
        // already opaque). Only force them when actually enveloped, so we don't double cloud geometry the rest of the
        // time. Leaving the flag untouched lets Simple Clouds' own default culling stand when we're not inside.
        if (InteriorFillState.envelopment <= 0.02F)
            return;
        final CloudMeshGenerator gen = this.getMeshGenerator();
        if (gen != null)
            gen.setTestFacesFacingAway(true);
    }
}
