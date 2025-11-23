/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.metadata.animation.AnimationMetadataSection
 *  net.minecraft.client.resources.metadata.animation.FrameSize
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceMetadata
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.pbr.loader;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.mixin.texture.AnimationMetadataSectionAccessor;
import net.irisshaders.iris.mixin.texture.TextureAtlasAccessor;
import net.irisshaders.iris.texture.format.TextureFormat;
import net.irisshaders.iris.texture.format.TextureFormatLoader;
import net.irisshaders.iris.texture.mipmap.ChannelMipmapGenerator;
import net.irisshaders.iris.texture.mipmap.CustomMipmapGenerator;
import net.irisshaders.iris.texture.mipmap.LinearBlendFunction;
import net.irisshaders.iris.texture.pbr.PBRAtlasTexture;
import net.irisshaders.iris.texture.pbr.PBRSpriteHolder;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.irisshaders.iris.texture.pbr.SpriteContentsExtension;
import net.irisshaders.iris.texture.pbr.loader.PBRTextureLoader;
import net.irisshaders.iris.texture.util.ImageManipulationUtil;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class AtlasPBRLoader
implements PBRTextureLoader<TextureAtlas> {
    public static final ChannelMipmapGenerator LINEAR_MIPMAP_GENERATOR = new ChannelMipmapGenerator(LinearBlendFunction.INSTANCE, LinearBlendFunction.INSTANCE, LinearBlendFunction.INSTANCE, LinearBlendFunction.INSTANCE);

    @Override
    public void load(TextureAtlas atlas, ResourceManager resourceManager, PBRTextureLoader.PBRTextureConsumer pbrTextureConsumer) {
        TextureAtlasAccessor atlasAccessor = (TextureAtlasAccessor)atlas;
        int atlasWidth = atlasAccessor.callGetWidth();
        int atlasHeight = atlasAccessor.callGetHeight();
        int mipLevel = atlasAccessor.getMipLevel();
        PBRAtlasTexture normalAtlas = null;
        PBRAtlasTexture specularAtlas = null;
        for (TextureAtlasSprite sprite : ((TextureAtlasAccessor)atlas).getTexturesByName().values()) {
            PBRSpriteHolder pbrSpriteHolder;
            PBRTextureAtlasSprite normalSprite = this.createPBRSprite(sprite, resourceManager, atlas, atlasWidth, atlasHeight, mipLevel, PBRType.NORMAL);
            PBRTextureAtlasSprite specularSprite = this.createPBRSprite(sprite, resourceManager, atlas, atlasWidth, atlasHeight, mipLevel, PBRType.SPECULAR);
            if (normalSprite != null) {
                if (normalAtlas == null) {
                    normalAtlas = new PBRAtlasTexture(atlas, PBRType.NORMAL);
                }
                normalAtlas.addSprite(normalSprite);
                pbrSpriteHolder = ((SpriteContentsExtension)sprite.m_245424_()).getOrCreatePBRHolder();
                pbrSpriteHolder.setNormalSprite(normalSprite);
            }
            if (specularSprite == null) continue;
            if (specularAtlas == null) {
                specularAtlas = new PBRAtlasTexture(atlas, PBRType.SPECULAR);
            }
            specularAtlas.addSprite(specularSprite);
            pbrSpriteHolder = ((SpriteContentsExtension)sprite.m_245424_()).getOrCreatePBRHolder();
            pbrSpriteHolder.setSpecularSprite(specularSprite);
        }
        if (normalAtlas != null && normalAtlas.tryUpload(atlasWidth, atlasHeight, mipLevel)) {
            pbrTextureConsumer.acceptNormalTexture(normalAtlas);
        }
        if (specularAtlas != null && specularAtlas.tryUpload(atlasWidth, atlasHeight, mipLevel)) {
            pbrTextureConsumer.acceptSpecularTexture(specularAtlas);
        }
    }

    @Nullable
    protected PBRTextureAtlasSprite createPBRSprite(TextureAtlasSprite sprite, ResourceManager resourceManager, TextureAtlas atlas, int atlasWidth, int atlasHeight, int mipLevel, PBRType pbrType) {
        NativeImage nativeImage;
        ResourceMetadata animationMetadata;
        ResourceLocation spriteName = sprite.m_245424_().m_246162_();
        ResourceLocation pbrImageLocation = this.getPBRImageLocation(spriteName, pbrType);
        Optional optionalResource = resourceManager.m_213713_(pbrImageLocation);
        if (optionalResource.isEmpty()) {
            return null;
        }
        Resource resource = (Resource)optionalResource.get();
        try {
            animationMetadata = resource.m_215509_();
        }
        catch (Exception e) {
            Iris.logger.error("Unable to parse metadata from {}", pbrImageLocation, e);
            return null;
        }
        try (InputStream stream = resource.m_215507_();){
            nativeImage = NativeImage.m_85058_((InputStream)stream);
        }
        catch (IOException e) {
            Iris.logger.error("Using missing texture, unable to load {}", pbrImageLocation, e);
            return null;
        }
        int imageWidth = nativeImage.m_84982_();
        int imageHeight = nativeImage.m_85084_();
        AnimationMetadataSection metadataSection = animationMetadata.m_214059_((MetadataSectionSerializer)AnimationMetadataSection.f_119011_).orElse(AnimationMetadataSection.f_119012_);
        FrameSize frameSize = metadataSection.m_245821_(imageWidth, imageHeight);
        int frameWidth = frameSize.f_244129_();
        int frameHeight = frameSize.f_244503_();
        if (!Mth.m_264612_((int)imageWidth, (int)frameWidth) || !Mth.m_264612_((int)imageHeight, (int)frameHeight)) {
            Iris.logger.error("Image {} size {},{} is not multiple of frame size {},{}", pbrImageLocation, imageWidth, imageHeight, frameWidth, frameHeight);
            nativeImage.close();
            return null;
        }
        int targetFrameWidth = sprite.m_245424_().m_246492_();
        int targetFrameHeight = sprite.m_245424_().m_245330_();
        if (frameWidth != targetFrameWidth || frameHeight != targetFrameHeight) {
            try {
                int targetImageWidth = imageWidth / frameWidth * targetFrameWidth;
                int targetImageHeight = imageHeight / frameHeight * targetFrameHeight;
                NativeImage scaledImage = targetImageWidth % imageWidth == 0 && targetImageHeight % imageHeight == 0 ? ImageManipulationUtil.scaleNearestNeighbor(nativeImage, targetImageWidth, targetImageHeight) : ImageManipulationUtil.scaleBilinear(nativeImage, targetImageWidth, targetImageHeight);
                nativeImage.close();
                nativeImage = scaledImage;
                frameWidth = targetFrameWidth;
                frameHeight = targetFrameHeight;
                if (metadataSection != AnimationMetadataSection.f_119012_) {
                    AnimationMetadataSectionAccessor animationAccessor = (AnimationMetadataSectionAccessor)metadataSection;
                    int internalFrameWidth = animationAccessor.getFrameWidth();
                    int internalFrameHeight = animationAccessor.getFrameHeight();
                    if (internalFrameWidth != -1) {
                        animationAccessor.setFrameWidth(frameWidth);
                    }
                    if (internalFrameHeight != -1) {
                        animationAccessor.setFrameHeight(frameHeight);
                    }
                }
            }
            catch (Exception e) {
                Iris.logger.error("Something bad happened trying to load PBR texture " + spriteName.m_135815_() + pbrType.getSuffix() + "!", e);
                throw e;
            }
        }
        ResourceLocation pbrSpriteName = new ResourceLocation(spriteName.m_135827_(), spriteName.m_135815_() + pbrType.getSuffix());
        PBRSpriteContents pbrSpriteContents = new PBRSpriteContents(pbrSpriteName, new FrameSize(frameWidth, frameHeight), nativeImage, metadataSection, pbrType);
        pbrSpriteContents.m_246368_(mipLevel);
        return new PBRTextureAtlasSprite(pbrSpriteName, pbrSpriteContents, atlasWidth, atlasHeight, sprite.m_174743_(), sprite.m_174744_(), sprite);
    }

    protected ResourceLocation getPBRImageLocation(ResourceLocation spriteName, PBRType pbrType) {
        String path = pbrType.appendSuffix(spriteName.m_135815_());
        if (path.startsWith("optifine/cit/")) {
            return new ResourceLocation(spriteName.m_135827_(), path + ".png");
        }
        return new ResourceLocation(spriteName.m_135827_(), "textures/" + path + ".png");
    }

    public static class PBRTextureAtlasSprite
    extends TextureAtlasSprite {
        protected final TextureAtlasSprite baseSprite;

        protected PBRTextureAtlasSprite(ResourceLocation location, PBRSpriteContents contents, int atlasWidth, int atlasHeight, int x, int y, TextureAtlasSprite baseSprite) {
            super(location, (SpriteContents)contents, atlasWidth, atlasHeight, x, y);
            this.baseSprite = baseSprite;
        }

        public TextureAtlasSprite getBaseSprite() {
            return this.baseSprite;
        }
    }

    protected static class PBRSpriteContents
    extends SpriteContents
    implements CustomMipmapGenerator.Provider {
        protected final PBRType pbrType;

        public PBRSpriteContents(ResourceLocation name, FrameSize size, NativeImage image, AnimationMetadataSection metadata, PBRType pbrType) {
            super(name, size, image, metadata);
            this.pbrType = pbrType;
        }

        @Override
        public CustomMipmapGenerator getMipmapGenerator() {
            CustomMipmapGenerator generator;
            TextureFormat format = TextureFormatLoader.getFormat();
            if (format != null && (generator = format.getMipmapGenerator(this.pbrType)) != null) {
                return generator;
            }
            return LINEAR_MIPMAP_GENERATOR;
        }
    }
}

