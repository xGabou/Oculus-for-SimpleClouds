package dev.bettersimpleclouds.immersion.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.ShaderInstance;

import dev.bettersimpleclouds.immersion.CloudFogRange;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;

/**
 * Gives every cloud shader the cloud-relative fog range &mdash; see {@link CloudFogRange} for why the terrain range
 * flattens the cloudscape, especially at night.
 *
 * <p>{@code prepareShader} is the one place Simple Clouds pushes the fog uniforms, and every cloud shader goes through
 * it: the opaque pass, the transparency pass, the shadow map and the rest, on both cloud pipelines. Injecting at its
 * tail &mdash; after the stock {@code FOG_START} / {@code FOG_END} have been written &mdash; means the override lands
 * on all of them from a single hook, and cannot be bypassed by the renderer choosing a different pass.</p>
 *
 * <p>The obvious alternative, hooking the opaque render call, is what this replaces: that method only runs under one of
 * the two pipelines, so the fix silently did nothing whenever the other was active.</p>
 *
 * <p>{@code require = 0} keeps the mod loading if a future Simple Clouds reshapes the method; clouds then simply keep
 * the terrain fog range they use today.</p>
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class CloudShaderFogRangeMixin {

    @Inject(method = "prepareShader", at = @At("TAIL"), require = 0)
    private static void bsc$cloudFogRange(final ShaderInstance shader, final Matrix4f modelView,
            final Matrix4f projMat, final float fogStart, final float fogEnd, final CallbackInfo ci) {
        CloudFogRange.feed(shader, fogEnd);
    }
}
