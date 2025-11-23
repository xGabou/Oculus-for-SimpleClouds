/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.sampler;

import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;

public class GlSampler
extends GlResource {
    public GlSampler(boolean linear, boolean mipmapped, boolean shadow, boolean hardwareShadow) {
        super(IrisRenderSystem.genSampler());
        IrisRenderSystem.samplerParameteri(this.getId(), 10241, linear ? 9729 : 9728);
        IrisRenderSystem.samplerParameteri(this.getId(), 10240, linear ? 9729 : 9728);
        IrisRenderSystem.samplerParameteri(this.getId(), 10242, 33071);
        IrisRenderSystem.samplerParameteri(this.getId(), 10243, 33071);
        if (mipmapped) {
            IrisRenderSystem.samplerParameteri(this.getId(), 10241, linear ? 9987 : 9984);
        }
        if (hardwareShadow) {
            IrisRenderSystem.samplerParameteri(this.getId(), 34892, 34894);
        }
    }

    @Override
    protected void destroyInternal() {
        IrisRenderSystem.destroySampler(this.getGlId());
    }

    public int getId() {
        return this.getGlId();
    }
}

