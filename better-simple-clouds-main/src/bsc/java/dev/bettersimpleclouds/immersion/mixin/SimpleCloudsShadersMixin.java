package dev.bettersimpleclouds.immersion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.resources.ResourceLocation;

import dev.nonamecrackers2.simpleclouds.client.shader.SimpleCloudsShaders;

/**
 * Swaps the opaque cloud core shader for Better Simple Clouds's copy ({@code makeitcompatible:clouds}) so our fragment
 * shader runs instead of Simple Clouds' stock one. Our copy reuses Simple Clouds' own <em>vertex</em> shader
 * ({@code simpleclouds:clouds}, referenced from our shader JSON), so the SSBO geometry expansion is unchanged - only
 * the fragment stage differs, adding a tasteful "sit in the shader-lit scene" pass (sky tint / exposure) that is inert
 * at its defaults. The SSBO binding name passed to the constructor ({@code SideInfoBuffer}) is untouched.
 *
 * <p>Both the opaque {@code clouds} shader and the {@code clouds_transparency} shader are redirected to our copies;
 * the shadow-map shader keeps Simple Clouds' original. Our transparency copy only adds a tunable OIT weight
 * ({@code MicWeightFlatten}) to fix the mottled see-through cloud edges. {@code remap = false} (third-party target);
 * gated on {@code simpleclouds} by the patch's mixin plugin.</p>
 */
@Mixin(value = SimpleCloudsShaders.class, remap = false)
public abstract class SimpleCloudsShadersMixin {

    @ModifyArg(
        method = "registerShaders",
        at = @At(
            value = "INVOKE",
            target = "Ldev/nonamecrackers2/simpleclouds/client/shader/SingleSSBOShaderInstance;<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;Ljava/lang/String;)V"),
        index = 1)
    private static ResourceLocation bsc$useOwnCloudsShader(final ResourceLocation original) {
        if ("simpleclouds".equals(original.getNamespace())) {
            if ("clouds".equals(original.getPath()))
                return ResourceLocation.fromNamespaceAndPath("bettersimpleclouds", "clouds");
            if ("clouds_transparency".equals(original.getPath()))
                return ResourceLocation.fromNamespaceAndPath("bettersimpleclouds", "clouds_transparency");
        }
        return original;
    }
}
