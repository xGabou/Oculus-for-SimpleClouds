/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.texture.pbr;

import net.irisshaders.iris.texture.pbr.PBRAtlasHolder;
import net.irisshaders.iris.texture.pbr.TextureAtlasExtension;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TextureAtlas.class})
public abstract class MixinTextureAtlas
extends AbstractTexture
implements TextureAtlasExtension {
    @Shadow
    @Final
    private ResourceLocation f_118265_;
    @Unique
    private PBRAtlasHolder pbrHolder;

    @Inject(method={"cycleAnimationFrames()V"}, at={@At(value="TAIL")})
    private void iris$onTailCycleAnimationFrames(CallbackInfo ci) {
        if (this.pbrHolder != null) {
            this.pbrHolder.cycleAnimationFrames();
        }
    }

    @Override
    public PBRAtlasHolder getPBRHolder() {
        return this.pbrHolder;
    }

    @Override
    public PBRAtlasHolder getOrCreatePBRHolder() {
        if (this.pbrHolder == null) {
            this.pbrHolder = new PBRAtlasHolder();
        }
        return this.pbrHolder;
    }
}

