/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  net.minecraft.client.renderer.texture.AbstractTexture
 */
package net.irisshaders.iris.samplers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.image.GlImage;
import net.irisshaders.iris.gl.sampler.GlSampler;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.irisshaders.iris.targets.RenderTarget;
import net.irisshaders.iris.targets.RenderTargets;
import net.minecraft.client.renderer.texture.AbstractTexture;

public class IrisSamplers {
    public static final int ALBEDO_TEXTURE_UNIT = 0;
    public static final int OVERLAY_TEXTURE_UNIT = 1;
    public static final int LIGHTMAP_TEXTURE_UNIT = 2;
    public static final ImmutableSet<Integer> WORLD_RESERVED_TEXTURE_UNITS = ImmutableSet.of((Object)0, (Object)1, (Object)2);
    public static final ImmutableSet<Integer> COMPOSITE_RESERVED_TEXTURE_UNITS = ImmutableSet.of((Object)1, (Object)2);
    private static GlSampler SHADOW_SAMPLER_NEAREST;
    private static GlSampler SHADOW_SAMPLER_LINEAR;
    private static GlSampler LINEAR_MIPMAP;
    private static GlSampler NEAREST_MIPMAP;

    private IrisSamplers() {
    }

    public static void initRenderer() {
        SHADOW_SAMPLER_NEAREST = new GlSampler(false, false, true, true);
        SHADOW_SAMPLER_LINEAR = new GlSampler(true, false, true, true);
        LINEAR_MIPMAP = new GlSampler(true, true, false, false);
        NEAREST_MIPMAP = new GlSampler(false, true, false, false);
    }

    public static void addRenderTargetSamplers(SamplerHolder samplers, Supplier<ImmutableSet<Integer>> flipped, RenderTargets renderTargets, boolean isFullscreenPass, WorldRenderingPipeline pipeline) {
        int startIndex;
        for (int i = startIndex = isFullscreenPass ? 0 : 4; i < renderTargets.getRenderTargetCount(); ++i) {
            int index = i;
            IntSupplier texture = () -> {
                ImmutableSet flippedBuffers = (ImmutableSet)flipped.get();
                RenderTarget target = renderTargets.getOrCreate(index);
                if (flippedBuffers.contains((Object)index)) {
                    return target.getAltTexture();
                }
                return target.getMainTexture();
            };
            String name = "colortex" + i;
            if (i < PackRenderTargetDirectives.LEGACY_RENDER_TARGETS.size()) {
                String legacyName = (String)PackRenderTargetDirectives.LEGACY_RENDER_TARGETS.get(i);
                if (samplers.hasSampler(legacyName) || samplers.hasSampler(name)) {
                    renderTargets.createIfUnsure(index);
                }
                if (i == 0 && isFullscreenPass) {
                    samplers.addDefaultSampler(TextureType.TEXTURE_2D, texture, null, null, name, legacyName);
                    continue;
                }
                samplers.addDynamicSampler(TextureType.TEXTURE_2D, texture, null, name, legacyName);
                continue;
            }
            if (samplers.hasSampler(name)) {
                renderTargets.createIfUnsure(index);
            }
            samplers.addDynamicSampler(texture, name);
        }
        samplers.addDynamicSampler(TextureType.TEXTURE_2D, () -> pipeline.getDHCompat().getDepthTex(), null, "dhDepthTex", "dhDepthTex0");
        samplers.addDynamicSampler(TextureType.TEXTURE_2D, () -> pipeline.getDHCompat().getDepthTexNoTranslucent(), null, "dhDepthTex1");
    }

    public static void addNoiseSampler(SamplerHolder samplers, TextureAccess sampler) {
        samplers.addDynamicSampler(sampler.getTextureId(), "noisetex");
    }

    public static boolean hasShadowSamplers(SamplerHolder samplers) {
        ImmutableList.Builder shadowSamplers = ImmutableList.builder().add((Object[])new String[]{"shadowtex0", "shadowtex0DH", "shadowtex0HW", "shadowtex1", "shadowtex1HW", "shadowtex1DH", "shadow", "watershadow", "shadowcolor"});
        for (int i = 0; i < 8; ++i) {
            shadowSamplers.add((Object)("shadowcolor" + i));
            shadowSamplers.add((Object)("shadowcolorimg" + i));
        }
        for (String samplerName : shadowSamplers.build()) {
            if (!samplers.hasSampler(samplerName)) continue;
            return true;
        }
        return false;
    }

    public static boolean addShadowSamplers(SamplerHolder samplers, ShadowRenderTargets shadowRenderTargets, ImmutableSet<Integer> flipped, boolean separateHardwareSamplers) {
        boolean usesShadows;
        boolean waterShadowEnabled = samplers.hasSampler("watershadow");
        if (waterShadowEnabled) {
            usesShadows = true;
            samplers.addDynamicSampler(TextureType.TEXTURE_2D, shadowRenderTargets.getDepthTexture()::getTextureId, separateHardwareSamplers ? null : (shadowRenderTargets.isHardwareFiltered(0) ? (shadowRenderTargets.isLinearFiltered(0) ? SHADOW_SAMPLER_LINEAR : SHADOW_SAMPLER_NEAREST) : null), "shadowtex0", "watershadow");
            samplers.addDynamicSampler(TextureType.TEXTURE_2D, shadowRenderTargets.getDepthTextureNoTranslucents()::getTextureId, separateHardwareSamplers ? null : (shadowRenderTargets.isHardwareFiltered(1) ? (shadowRenderTargets.isLinearFiltered(1) ? SHADOW_SAMPLER_LINEAR : SHADOW_SAMPLER_NEAREST) : null), "shadowtex1", "shadow");
        } else {
            usesShadows = samplers.addDynamicSampler(TextureType.TEXTURE_2D, shadowRenderTargets.getDepthTexture()::getTextureId, separateHardwareSamplers ? null : (shadowRenderTargets.isHardwareFiltered(0) ? (shadowRenderTargets.isLinearFiltered(0) ? SHADOW_SAMPLER_LINEAR : SHADOW_SAMPLER_NEAREST) : null), "shadowtex0", "shadow");
            usesShadows |= samplers.addDynamicSampler(TextureType.TEXTURE_2D, shadowRenderTargets.getDepthTextureNoTranslucents()::getTextureId, separateHardwareSamplers ? null : (shadowRenderTargets.isHardwareFiltered(1) ? (shadowRenderTargets.isLinearFiltered(1) ? SHADOW_SAMPLER_LINEAR : SHADOW_SAMPLER_NEAREST) : null), "shadowtex1");
        }
        if (flipped == null) {
            if (samplers.addDynamicSampler(() -> shadowRenderTargets.getColorTextureId(0), "shadowcolor")) {
                shadowRenderTargets.createIfEmpty(0);
            }
            for (int i = 0; i < shadowRenderTargets.getRenderTargetCount(); ++i) {
                int finalI = i;
                if (!samplers.addDynamicSampler(() -> shadowRenderTargets.getColorTextureId(finalI), "shadowcolor" + i)) continue;
                shadowRenderTargets.createIfEmpty(finalI);
            }
        } else {
            if (samplers.addDynamicSampler(() -> flipped.contains((Object)0) ? shadowRenderTargets.get(0).getAltTexture() : shadowRenderTargets.get(0).getMainTexture(), "shadowcolor")) {
                shadowRenderTargets.createIfEmpty(0);
            }
            for (int i = 0; i < shadowRenderTargets.getRenderTargetCount(); ++i) {
                int finalI = i;
                if (!samplers.addDynamicSampler(() -> flipped.contains((Object)finalI) ? shadowRenderTargets.get(finalI).getAltTexture() : shadowRenderTargets.get(finalI).getMainTexture(), "shadowcolor" + i)) continue;
                shadowRenderTargets.createIfEmpty(finalI);
            }
        }
        if (shadowRenderTargets.isHardwareFiltered(0) && separateHardwareSamplers) {
            samplers.addDynamicSampler(TextureType.TEXTURE_2D, shadowRenderTargets.getDepthTexture()::getTextureId, shadowRenderTargets.isLinearFiltered(0) ? SHADOW_SAMPLER_LINEAR : SHADOW_SAMPLER_NEAREST, "shadowtex0HW");
        }
        if (shadowRenderTargets.isHardwareFiltered(1) && separateHardwareSamplers) {
            samplers.addDynamicSampler(TextureType.TEXTURE_2D, shadowRenderTargets.getDepthTextureNoTranslucents()::getTextureId, shadowRenderTargets.isLinearFiltered(1) ? SHADOW_SAMPLER_LINEAR : SHADOW_SAMPLER_NEAREST, "shadowtex1HW");
        }
        return usesShadows;
    }

    public static boolean hasPBRSamplers(SamplerHolder samplers) {
        return samplers.hasSampler("normals") || samplers.hasSampler("specular");
    }

    public static void addLevelSamplers(SamplerHolder samplers, WorldRenderingPipeline pipeline, AbstractTexture whitePixel, boolean hasTexture, boolean hasLightmap, boolean hasOverlay) {
        if (hasTexture) {
            samplers.addExternalSampler(0, "tex", "texture", "gtexture");
        } else {
            samplers.addDynamicSampler(() -> ((AbstractTexture)whitePixel).m_117963_(), "tex", "texture", "gtexture", "gcolor", "colortex0");
        }
        if (hasLightmap) {
            samplers.addExternalSampler(2, "lightmap");
        } else {
            samplers.addDynamicSampler(() -> ((AbstractTexture)whitePixel).m_117963_(), "lightmap");
        }
        if (hasOverlay) {
            samplers.addExternalSampler(1, "iris_overlay");
        } else {
            samplers.addDynamicSampler(() -> ((AbstractTexture)whitePixel).m_117963_(), "iris_overlay");
        }
        samplers.addDynamicSampler(pipeline::getCurrentNormalTexture, StateUpdateNotifiers.normalTextureChangeNotifier, "normals");
        samplers.addDynamicSampler(pipeline::getCurrentSpecularTexture, StateUpdateNotifiers.specularTextureChangeNotifier, "specular");
    }

    public static void addWorldDepthSamplers(SamplerHolder samplers, RenderTargets renderTargets) {
        samplers.addDynamicSampler(renderTargets::getDepthTexture, "depthtex0");
        samplers.addDynamicSampler(renderTargets.getDepthTextureNoTranslucents()::getTextureId, "depthtex1");
    }

    public static void addCompositeSamplers(SamplerHolder samplers, RenderTargets renderTargets) {
        samplers.addDynamicSampler(renderTargets::getDepthTexture, "gdepthtex", "depthtex0");
        samplers.addDynamicSampler(renderTargets.getDepthTextureNoTranslucents()::getTextureId, "depthtex1");
        samplers.addDynamicSampler(renderTargets.getDepthTextureNoHand()::getTextureId, "depthtex2");
    }

    public static void addCustomTextures(SamplerHolder samplers, Object2ObjectMap<String, TextureAccess> irisCustomTextures) {
        irisCustomTextures.forEach((name, texture) -> samplers.addDynamicSampler(texture.getType(), texture.getTextureId(), null, (String)name));
    }

    public static void addCustomImages(SamplerHolder images, Set<GlImage> customImages) {
        customImages.forEach(image -> {
            if (image.getSamplerName() != null) {
                images.addDynamicSampler(image.getTarget(), image::getId, null, image.getSamplerName());
            }
        });
    }
}

