package dev.bettersimpleclouds.immersion.mixin;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.lwjgl.opengl.GL41;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;
import dev.bettersimpleclouds.immersion.InteriorFillState;

import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.shader.compute.ComputeShader;

/**
 * Drives Simple Clouds' cloud-mesh compute shader to fill a big cloud's hollow interior with semi-transparent cubes
 * near the camera (the in-cloud immersion patch's interior fill).
 *
 * <p>Two hooks on {@link CloudMeshGenerator}:</p>
 * <ul>
 *   <li><b>{@code createShader}</b> &mdash; when the fill is enabled, redirect the compute-shader load from
 *       {@code simpleclouds:cube_mesh} to our copy {@code makeitcompatible:cube_mesh}, which is the same shader plus a
 *       guarded block that emits a transparent cube for each solid cell within range of the camera. (Our copy still
 *       {@code #moj_import}s Simple Clouds' own includes, which resolve across namespaces.) Toggling the fill on/off
 *       swaps shaders, so it takes a resource reload (F3+T); range/opacity tune live through the uniforms below.</li>
 *   <li><b>{@code prepareMeshGen}</b> &mdash; each generation, push the live fill parameters as the {@code MicFill*}
 *       uniforms. We always upload them so disabling drives strength to 0 immediately; on Simple Clouds' stock shader
 *       (fill never enabled) the uniforms are simply absent and the upload is a one-time-warned no-op.</li>
 * </ul>
 *
 * <p>Because only solid cells ({@code noise > 0}) are filled, the cubes never appear outside the cloud, and Simple
 * Clouds' existing transparent pass blends them with proper depth layering. Gated on {@code simpleclouds}; this is the
 * GPU-side path, so it works wherever Simple Clouds' own transparent clouds do.</p>
 */
@Mixin(value = CloudMeshGenerator.class, remap = false)
public abstract class CloudMeshGeneratorFillMixin {

    @Shadow
    protected ComputeShader shader;

    @Redirect(
        method = "createShader",
        at = @At(
            value = "INVOKE",
            target = "Ldev/nonamecrackers2/simpleclouds/client/shader/compute/ComputeShader;loadShader(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/ResourceProvider;IIILcom/google/common/collect/ImmutableMap;)Ldev/nonamecrackers2/simpleclouds/client/shader/compute/ComputeShader;"))
    private ComputeShader bsc$swapMeshShader(final ResourceLocation loc, final ResourceProvider provider,
                                             final int localX, final int localY, final int localZ,
                                             final ImmutableMap<String, String> parameters) throws IOException {
        // Always use our copy for the main cube mesh: it is identical to Simple Clouds' shader unless our uniforms are
        // active (fill off + solid-faces off -> stock behaviour), and routing through it lets both the interior fill
        // and the near-only solid-faces gating (the huge-distant-cube fix) work without a resource reload.
        ResourceLocation target = loc;
        if ("simpleclouds".equals(loc.getNamespace()) && "cube_mesh".equals(loc.getPath())) {
            target = ResourceLocation.fromNamespaceAndPath("bettersimpleclouds", "cube_mesh");
        }
        return ComputeShader.loadShader(target, provider, localX, localY, localZ, parameters);
    }

    @Inject(method = "prepareMeshGen", at = @At("TAIL"))
    private void bsc$uploadFillUniforms(final double originX, final double originY, final double originZ,
                                        final float meshGenOffsetX, final float meshGenOffsetZ, final Frustum frustum,
                                        final int genInterval, final float partialTick,
                                        final CallbackInfoReturnable<Integer> cir) {
        final ComputeShader s = this.shader;
        if (s == null || !s.isValid())
            return;
        s.forUniform("MicFillStrength", (id, loc) -> GL41.glProgramUniform1f(id, loc, InteriorFillState.strength));
        s.forUniform("MicViewDist", (id, loc) -> GL41.glProgramUniform1f(id, loc, InteriorFillState.viewDistCells));
        s.forUniform("MicHazeOpacity", (id, loc) -> GL41.glProgramUniform1f(id, loc, InteriorFillState.hazeOpacity));
        s.forUniform("MicShell", (id, loc) -> GL41.glProgramUniform1f(id, loc, InteriorFillState.shell));
        // Radius of the away-facing-face gate; compatibility patches widen it while other cameras (portals) are
        // looking at the clouds. See InteriorFillState.solidFaceRadiusCells and cube_mesh.comp.
        s.forUniform("MicSolidNearCells",
            (id, loc) -> GL41.glProgramUniform1f(id, loc, InteriorFillState.solidFaceRadiusCells));
        // Far-cloud LOD tuning (per-type softening + small-floater cluster cull). Read straight from config so it works
        // regardless of the in-cloud immersion master switch; 0 -> inert (stock). See cube_mesh.comp.
        s.forUniform("MicBigLodStrength",
            (id, loc) -> GL41.glProgramUniform1f(id, loc, BetterSimpleCloudsConfig.bigCloudLodStrength()));
        s.forUniform("MicSmallLodStrength",
            (id, loc) -> GL41.glProgramUniform1f(id, loc, BetterSimpleCloudsConfig.smallCloudLodStrength()));
        s.forUniform("MicSmallCloudCull",
            (id, loc) -> GL41.glProgramUniform1f(id, loc, BetterSimpleCloudsConfig.farSmallCloudCull()));
        // Max connected size (in cells) of a floating cloud cluster to remove (0 = off). See cube_mesh.comp.
        s.forUniform("MicMaxClusterCells",
            (id, loc) -> GL41.glProgramUniform1f(id, loc, BetterSimpleCloudsConfig.cloudMinClusterNeighbors()));
    }
}
