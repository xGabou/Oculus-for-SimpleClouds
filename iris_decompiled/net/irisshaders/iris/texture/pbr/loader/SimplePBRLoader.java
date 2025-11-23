/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.SimpleTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.pbr.loader;

import java.io.IOException;
import net.irisshaders.iris.mixin.texture.SimpleTextureAccessor;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.irisshaders.iris.texture.pbr.loader.PBRTextureLoader;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class SimplePBRLoader
implements PBRTextureLoader<SimpleTexture> {
    @Override
    public void load(SimpleTexture texture, ResourceManager resourceManager, PBRTextureLoader.PBRTextureConsumer pbrTextureConsumer) {
        ResourceLocation location = ((SimpleTextureAccessor)texture).getLocation();
        AbstractTexture normalTexture = this.createPBRTexture(location, resourceManager, PBRType.NORMAL);
        AbstractTexture specularTexture = this.createPBRTexture(location, resourceManager, PBRType.SPECULAR);
        if (normalTexture != null) {
            pbrTextureConsumer.acceptNormalTexture(normalTexture);
        }
        if (specularTexture != null) {
            pbrTextureConsumer.acceptSpecularTexture(specularTexture);
        }
    }

    @Nullable
    protected AbstractTexture createPBRTexture(ResourceLocation imageLocation, ResourceManager resourceManager, PBRType pbrType) {
        ResourceLocation pbrImageLocation = imageLocation.m_247266_(pbrType::appendSuffix);
        SimpleTexture pbrTexture = new SimpleTexture(pbrImageLocation);
        try {
            pbrTexture.m_6704_(resourceManager);
        }
        catch (IOException e) {
            return null;
        }
        return pbrTexture;
    }
}

