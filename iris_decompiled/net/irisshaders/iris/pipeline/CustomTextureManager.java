/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.ResourceLocationException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.commons.io.FilenameUtils
 */
package net.irisshaders.iris.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.texture.GlTexture;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.gl.texture.TextureWrapper;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.mixin.LightTextureAccessor;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.shaderpack.texture.CustomTextureData;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.targets.backed.NativeImageBackedCustomTexture;
import net.irisshaders.iris.targets.backed.NativeImageBackedNoiseTexture;
import net.irisshaders.iris.texture.format.TextureFormat;
import net.irisshaders.iris.texture.format.TextureFormatLoader;
import net.irisshaders.iris.texture.pbr.PBRTextureHolder;
import net.irisshaders.iris.texture.pbr.PBRTextureManager;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

public class CustomTextureManager {
    private final EnumMap<TextureStage, Object2ObjectMap<String, TextureAccess>> customTextureIdMap = new EnumMap(TextureStage.class);
    private final Object2ObjectMap<String, TextureAccess> irisCustomTextures = new Object2ObjectOpenHashMap();
    private final TextureAccess noise;
    private final List<AbstractTexture> ownedTextures = new ArrayList<AbstractTexture>();
    private final List<GlTexture> ownedRawTextures = new ArrayList<GlTexture>();

    public CustomTextureManager(PackDirectives packDirectives, EnumMap<TextureStage, Object2ObjectMap<String, CustomTextureData>> customTextureDataMap, Object2ObjectMap<String, CustomTextureData> irisCustomTextureDataMap, Optional<CustomTextureData> customNoiseTextureData) {
        customTextureDataMap.forEach((textureStage, customTextureStageDataMap) -> {
            Object2ObjectOpenHashMap customTextureIds = new Object2ObjectOpenHashMap();
            customTextureStageDataMap.forEach((arg_0, arg_1) -> this.lambda$new$0((Object2ObjectMap)customTextureIds, textureStage, arg_0, arg_1));
            this.customTextureIdMap.put((TextureStage)((Object)textureStage), (Object2ObjectMap<String, TextureAccess>)customTextureIds);
        });
        irisCustomTextureDataMap.forEach((name, texture) -> {
            try {
                this.irisCustomTextures.put(name, (Object)this.createCustomTexture((CustomTextureData)texture));
            }
            catch (IOException e) {
                Iris.logger.error("Unable to parse the image data for the custom texture on sampler " + name, e);
            }
        });
        this.noise = customNoiseTextureData.flatMap(textureData -> {
            try {
                return Optional.of(this.createCustomTexture((CustomTextureData)textureData));
            }
            catch (IOException | ResourceLocationException e) {
                Iris.logger.error("Unable to parse the image data for the custom noise texture", e);
                return Optional.empty();
            }
        }).orElseGet(() -> {
            int noiseTextureResolution = packDirectives.getNoiseTextureResolution();
            NativeImageBackedNoiseTexture texture = new NativeImageBackedNoiseTexture(noiseTextureResolution);
            this.ownedTextures.add((AbstractTexture)texture);
            return texture;
        });
    }

    private TextureAccess createCustomTexture(CustomTextureData textureData) throws IOException, ResourceLocationException {
        if (textureData instanceof CustomTextureData.PngData) {
            NativeImageBackedCustomTexture texture = new NativeImageBackedCustomTexture((CustomTextureData.PngData)textureData);
            this.ownedTextures.add((AbstractTexture)texture);
            return texture;
        }
        if (textureData instanceof CustomTextureData.LightmapMarker) {
            return new TextureWrapper(() -> ((DynamicTexture)((LightTextureAccessor)Minecraft.m_91087_().f_91063_.m_109154_()).getLightTexture()).m_117963_(), TextureType.TEXTURE_2D);
        }
        if (textureData instanceof CustomTextureData.RawData1D) {
            CustomTextureData.RawData1D rawData1D = (CustomTextureData.RawData1D)textureData;
            GlTexture texture = new GlTexture(TextureType.TEXTURE_1D, rawData1D.getSizeX(), 0, 0, rawData1D.getInternalFormat().getGlFormat(), rawData1D.getPixelFormat().getGlFormat(), rawData1D.getPixelType().getGlFormat(), rawData1D.getContent(), rawData1D.getFilteringData());
            this.ownedRawTextures.add(texture);
            return texture;
        }
        if (textureData instanceof CustomTextureData.RawDataRect) {
            CustomTextureData.RawDataRect rawDataRect = (CustomTextureData.RawDataRect)textureData;
            GlTexture texture = new GlTexture(TextureType.TEXTURE_RECTANGLE, rawDataRect.getSizeX(), rawDataRect.getSizeY(), 0, rawDataRect.getInternalFormat().getGlFormat(), rawDataRect.getPixelFormat().getGlFormat(), rawDataRect.getPixelType().getGlFormat(), rawDataRect.getContent(), rawDataRect.getFilteringData());
            this.ownedRawTextures.add(texture);
            return texture;
        }
        if (textureData instanceof CustomTextureData.RawData2D) {
            CustomTextureData.RawData2D rawData2D = (CustomTextureData.RawData2D)textureData;
            GlTexture texture = new GlTexture(TextureType.TEXTURE_2D, rawData2D.getSizeX(), rawData2D.getSizeY(), 0, rawData2D.getInternalFormat().getGlFormat(), rawData2D.getPixelFormat().getGlFormat(), rawData2D.getPixelType().getGlFormat(), rawData2D.getContent(), rawData2D.getFilteringData());
            this.ownedRawTextures.add(texture);
            return texture;
        }
        if (textureData instanceof CustomTextureData.RawData3D) {
            CustomTextureData.RawData3D rawData3D = (CustomTextureData.RawData3D)textureData;
            GlTexture texture = new GlTexture(TextureType.TEXTURE_3D, rawData3D.getSizeX(), rawData3D.getSizeY(), rawData3D.getSizeZ(), rawData3D.getInternalFormat().getGlFormat(), rawData3D.getPixelFormat().getGlFormat(), rawData3D.getPixelType().getGlFormat(), rawData3D.getContent(), rawData3D.getFilteringData());
            this.ownedRawTextures.add(texture);
            return texture;
        }
        if (textureData instanceof CustomTextureData.ResourceData) {
            CustomTextureData.ResourceData resourceData = (CustomTextureData.ResourceData)textureData;
            String namespace = resourceData.getNamespace();
            Object location = resourceData.getLocation();
            int extensionIndex = FilenameUtils.indexOfExtension((String)location);
            Object withoutExtension = extensionIndex != -1 ? ((String)location).substring(0, extensionIndex) : location;
            PBRType pbrType = PBRType.fromFileLocation((String)withoutExtension);
            TextureManager textureManager = Minecraft.m_91087_().m_91097_();
            if (pbrType == null) {
                ResourceLocation textureLocation = new ResourceLocation(namespace, (String)location);
                return new TextureWrapper(() -> {
                    AbstractTexture texture = textureManager.m_118506_(textureLocation);
                    return texture != null ? texture.m_117963_() : MissingTextureAtlasSprite.m_118080_().m_117963_();
                }, TextureType.TEXTURE_2D);
            }
            location = ((String)location).substring(0, extensionIndex - pbrType.getSuffix().length()) + ((String)location).substring(extensionIndex);
            ResourceLocation textureLocation = new ResourceLocation(namespace, (String)location);
            return new TextureWrapper(() -> {
                AbstractTexture texture = textureManager.m_118506_(textureLocation);
                if (texture != null) {
                    int id = texture.m_117963_();
                    PBRTextureHolder pbrHolder = PBRTextureManager.INSTANCE.getOrLoadHolder(id);
                    AbstractTexture pbrTexture = switch (pbrType) {
                        case PBRType.NORMAL -> pbrHolder.normalTexture();
                        case PBRType.SPECULAR -> pbrHolder.specularTexture();
                        default -> throw new IllegalArgumentException("Unknown PBRType '" + pbrType + "'");
                    };
                    TextureFormat textureFormat = TextureFormatLoader.getFormat();
                    if (textureFormat != null) {
                        int previousBinding = GlStateManagerAccessor.getTEXTURES()[GlStateManagerAccessor.getActiveTexture()].f_84801_;
                        GlStateManager._bindTexture((int)pbrTexture.m_117963_());
                        textureFormat.setupTextureParameters(pbrType, pbrTexture);
                        GlStateManager._bindTexture((int)previousBinding);
                    }
                    return pbrTexture.m_117963_();
                }
                return MissingTextureAtlasSprite.m_118080_().m_117963_();
            }, TextureType.TEXTURE_2D);
        }
        throw new IllegalArgumentException("Don't know texture type!");
    }

    public EnumMap<TextureStage, Object2ObjectMap<String, TextureAccess>> getCustomTextureIdMap() {
        return this.customTextureIdMap;
    }

    public Object2ObjectMap<String, TextureAccess> getCustomTextureIdMap(TextureStage stage) {
        return this.customTextureIdMap.getOrDefault((Object)stage, (Object2ObjectMap<String, TextureAccess>)Object2ObjectMaps.emptyMap());
    }

    public Object2ObjectMap<String, TextureAccess> getIrisCustomTextures() {
        return this.irisCustomTextures;
    }

    public TextureAccess getNoiseTexture() {
        return this.noise;
    }

    public void destroy() {
        this.ownedTextures.forEach(AbstractTexture::close);
        this.ownedRawTextures.forEach(GlResource::destroy);
    }

    private /* synthetic */ void lambda$new$0(Object2ObjectMap customTextureIds, TextureStage textureStage, String samplerName, CustomTextureData textureData) {
        try {
            customTextureIds.put((Object)samplerName, (Object)this.createCustomTexture(textureData));
        }
        catch (IOException | ResourceLocationException e) {
            Iris.logger.error("Unable to parse the image data for the custom texture on stage " + textureStage + ", sampler " + samplerName, e);
        }
    }
}

