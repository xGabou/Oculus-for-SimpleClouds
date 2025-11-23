/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.irisshaders.iris.samplers;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.image.GlImage;
import net.irisshaders.iris.gl.image.ImageHolder;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.irisshaders.iris.targets.RenderTarget;
import net.irisshaders.iris.targets.RenderTargets;

public class IrisImages {
    public static void addRenderTargetImages(ImageHolder images, Supplier<ImmutableSet<Integer>> flipped, RenderTargets renderTargets) {
        for (int i = 0; i < renderTargets.getRenderTargetCount(); ++i) {
            int index = i;
            String name = "colorimg" + i;
            if (!images.hasImage(name)) continue;
            renderTargets.createIfUnsure(index);
            IntSupplier textureID = () -> {
                ImmutableSet flippedBuffers = (ImmutableSet)flipped.get();
                RenderTarget target = renderTargets.getOrCreate(index);
                if (flippedBuffers.contains((Object)index)) {
                    return target.getAltTexture();
                }
                return target.getMainTexture();
            };
            InternalTextureFormat internalFormat = renderTargets.getOrCreate(i).getInternalFormat();
            images.addTextureImage(textureID, internalFormat, name);
        }
    }

    public static boolean hasShadowImages(ImageHolder images) {
        if (images == null) {
            return false;
        }
        return images.hasImage("shadowcolorimg0") || images.hasImage("shadowcolorimg1");
    }

    public static boolean hasRenderTargetImages(ImageHolder images, RenderTargets targets) {
        for (int i = 0; i < targets.getRenderTargetCount(); ++i) {
            if (images == null || !images.hasImage("colorimg" + i)) continue;
            return true;
        }
        return false;
    }

    public static void addShadowColorImages(ImageHolder images, ShadowRenderTargets shadowRenderTargets, ImmutableSet<Integer> flipped) {
        if (images == null) {
            return;
        }
        for (int i = 0; i < shadowRenderTargets.getNumColorTextures(); ++i) {
            int index = i;
            IntSupplier textureID = flipped == null ? () -> shadowRenderTargets.getColorTextureId(index) : () -> flipped.contains((Object)index) ? shadowRenderTargets.getOrCreate(index).getAltTexture() : shadowRenderTargets.getOrCreate(index).getMainTexture();
            InternalTextureFormat format = shadowRenderTargets.getColorTextureFormat(index);
            images.addTextureImage(textureID, format, "shadowcolorimg" + i);
        }
    }

    public static void addCustomImages(ImageHolder images, Set<GlImage> customImages) {
        customImages.forEach(image -> images.addTextureImage(image::getId, image.getInternalFormat(), image.getName()));
    }
}

