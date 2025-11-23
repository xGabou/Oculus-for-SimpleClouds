/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.pbr;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

public class PBRSpriteHolder {
    protected TextureAtlasSprite normalSprite;
    protected TextureAtlasSprite specularSprite;

    @Nullable
    public TextureAtlasSprite getNormalSprite() {
        return this.normalSprite;
    }

    public void setNormalSprite(TextureAtlasSprite sprite) {
        this.normalSprite = sprite;
    }

    @Nullable
    public TextureAtlasSprite getSpecularSprite() {
        return this.specularSprite;
    }

    public void setSpecularSprite(TextureAtlasSprite sprite) {
        this.specularSprite = sprite;
    }

    public void close() {
        if (this.normalSprite != null) {
            this.normalSprite.m_245424_().close();
        }
        if (this.specularSprite != null) {
            this.specularSprite.m_245424_().close();
        }
    }
}

