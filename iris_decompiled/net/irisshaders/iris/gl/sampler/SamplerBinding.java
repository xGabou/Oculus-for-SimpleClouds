/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.sampler;

import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.sampler.GlSampler;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.texture.TextureType;

public class SamplerBinding {
    private final int textureUnit;
    private final IntSupplier texture;
    private final ValueUpdateNotifier notifier;
    private final TextureType textureType;
    private final int sampler;

    public SamplerBinding(TextureType type, int textureUnit, IntSupplier texture, GlSampler sampler, ValueUpdateNotifier notifier) {
        this.textureType = type;
        this.textureUnit = textureUnit;
        this.texture = texture;
        this.sampler = sampler == null ? 0 : sampler.getId();
        this.notifier = notifier;
    }

    public void update() {
        this.updateSampler();
        if (this.notifier != null) {
            this.notifier.setListener(this::updateSampler);
        }
    }

    private void updateSampler() {
        IrisRenderSystem.bindSamplerToUnit(this.textureUnit, this.sampler);
        IrisRenderSystem.bindTextureToUnit(this.textureType.getGlType(), this.textureUnit, this.texture.getAsInt());
    }
}

