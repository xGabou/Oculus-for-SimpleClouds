/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 */
package net.irisshaders.iris.gl.program;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.image.ImageBinding;
import net.irisshaders.iris.gl.image.ImageHolder;
import net.irisshaders.iris.gl.image.ImageLimits;
import net.irisshaders.iris.gl.program.GlUniform1iCall;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;

public class ProgramImages {
    private final ImmutableList<ImageBinding> imageBindings;
    private List<GlUniform1iCall> initializer;

    private ProgramImages(ImmutableList<ImageBinding> imageBindings, List<GlUniform1iCall> initializer) {
        this.imageBindings = imageBindings;
        this.initializer = initializer;
    }

    public static Builder builder(int program) {
        return new Builder(program);
    }

    public void update() {
        if (this.initializer != null) {
            for (GlUniform1iCall call : this.initializer) {
                RenderSystem.glUniform1i((int)call.location(), (int)call.value());
            }
            this.initializer = null;
        }
        for (ImageBinding imageBinding : this.imageBindings) {
            imageBinding.update();
        }
    }

    public int getActiveImages() {
        return this.imageBindings.size();
    }

    public static final class Builder
    implements ImageHolder {
        private final int program;
        private final ImmutableList.Builder<ImageBinding> images;
        private final List<GlUniform1iCall> calls;
        private final int maxImageUnits;
        private int nextImageUnit;

        private Builder(int program) {
            this.program = program;
            this.images = ImmutableList.builder();
            this.calls = new ArrayList<GlUniform1iCall>();
            this.nextImageUnit = 0;
            this.maxImageUnits = ImageLimits.get().getMaxImageUnits();
        }

        @Override
        public boolean hasImage(String name) {
            return GlStateManager._glGetUniformLocation((int)this.program, (CharSequence)name) != -1;
        }

        @Override
        public void addTextureImage(IntSupplier textureID, InternalTextureFormat internalFormat, String name) {
            int location = GlStateManager._glGetUniformLocation((int)this.program, (CharSequence)name);
            if (location == -1) {
                return;
            }
            if (this.nextImageUnit >= this.maxImageUnits) {
                if (this.maxImageUnits == 0) {
                    throw new IllegalStateException("Image units are not supported on this platform, but a shader program attempted to reference " + name + ".");
                }
                throw new IllegalStateException("No more available texture units while activating image " + name + ". Only " + this.maxImageUnits + " image units are available.");
            }
            if (internalFormat == InternalTextureFormat.RGBA) {
                internalFormat = InternalTextureFormat.RGBA8;
            }
            this.images.add((Object)new ImageBinding(this.nextImageUnit, internalFormat.getGlFormat(), textureID));
            this.calls.add(new GlUniform1iCall(location, this.nextImageUnit));
            ++this.nextImageUnit;
        }

        public ProgramImages build() {
            return new ProgramImages((ImmutableList<ImageBinding>)this.images.build(), this.calls);
        }
    }
}

