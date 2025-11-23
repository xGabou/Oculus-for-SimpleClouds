/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.texture.pbr;

import net.irisshaders.iris.texture.pbr.PBRSpriteHolder;
import net.irisshaders.iris.texture.pbr.SpriteContentsExtension;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SpriteContents.class})
public class MixinSpriteContents
implements SpriteContentsExtension {
    @Unique
    private PBRSpriteHolder pbrHolder;

    @Inject(method={"close()V"}, at={@At(value="TAIL")}, remap=false)
    private void iris$onTailClose(CallbackInfo ci) {
        if (this.pbrHolder != null) {
            this.pbrHolder.close();
        }
    }

    @Override
    public PBRSpriteHolder getPBRHolder() {
        return this.pbrHolder;
    }

    @Override
    public PBRSpriteHolder getOrCreatePBRHolder() {
        if (this.pbrHolder == null) {
            this.pbrHolder = new PBRSpriteHolder();
        }
        return this.pbrHolder;
    }
}

