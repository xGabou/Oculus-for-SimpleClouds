/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.TextureUtil
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.SpriteContents$FrameInfo
 *  net.minecraft.client.renderer.texture.SpriteContents$Ticker
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite$Ticker
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.pbr;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.mixin.texture.SpriteContentsAnimatedTextureAccessor;
import net.irisshaders.iris.mixin.texture.SpriteContentsFrameInfoAccessor;
import net.irisshaders.iris.mixin.texture.SpriteContentsTickerAccessor;
import net.irisshaders.iris.texture.SpriteContentsExtension;
import net.irisshaders.iris.texture.pbr.PBRAtlasHolder;
import net.irisshaders.iris.texture.pbr.PBRDumpable;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.irisshaders.iris.texture.pbr.TextureAtlasExtension;
import net.irisshaders.iris.texture.pbr.loader.AtlasPBRLoader;
import net.irisshaders.iris.texture.util.TextureManipulationUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class PBRAtlasTexture
extends AbstractTexture
implements PBRDumpable {
    protected final TextureAtlas atlasTexture;
    protected final PBRType type;
    protected final ResourceLocation id;
    protected final Map<ResourceLocation, AtlasPBRLoader.PBRTextureAtlasSprite> texturesByName = new HashMap<ResourceLocation, AtlasPBRLoader.PBRTextureAtlasSprite>();
    protected final List<TextureAtlasSprite.Ticker> animatedTextures = new ArrayList<TextureAtlasSprite.Ticker>();
    protected int width;
    protected int height;
    protected int mipLevel;

    public PBRAtlasTexture(TextureAtlas atlasTexture, PBRType type) {
        this.atlasTexture = atlasTexture;
        this.type = type;
        this.id = new ResourceLocation(atlasTexture.m_118330_().m_135827_(), atlasTexture.m_118330_().m_135815_().replace(".png", "") + type.getSuffix() + ".png");
    }

    public static void syncAnimation(SpriteContents.Ticker source, SpriteContents.Ticker target) {
        int time;
        SpriteContentsTickerAccessor sourceAccessor = (SpriteContentsTickerAccessor)source;
        List<SpriteContents.FrameInfo> sourceFrames = ((SpriteContentsAnimatedTextureAccessor)sourceAccessor.getAnimationInfo()).getFrames();
        int ticks = 0;
        for (int f = 0; f < sourceAccessor.getFrame(); ++f) {
            ticks += ((SpriteContentsFrameInfoAccessor)sourceFrames.get(f)).getTime();
        }
        SpriteContentsTickerAccessor targetAccessor = (SpriteContentsTickerAccessor)target;
        List<SpriteContents.FrameInfo> targetFrames = ((SpriteContentsAnimatedTextureAccessor)targetAccessor.getAnimationInfo()).getFrames();
        int cycleTime = 0;
        int frameCount = targetFrames.size();
        for (SpriteContents.FrameInfo frame : targetFrames) {
            cycleTime += ((SpriteContentsFrameInfoAccessor)frame).getTime();
        }
        ticks %= cycleTime;
        int targetFrame = 0;
        while (ticks >= (time = ((SpriteContentsFrameInfoAccessor)targetFrames.get(targetFrame)).getTime())) {
            ++targetFrame;
            ticks -= time;
        }
        targetAccessor.setFrame(targetFrame);
        targetAccessor.setSubFrame(ticks + sourceAccessor.getSubFrame());
    }

    protected static void dumpSpriteNames(Path dir, String fileName, Map<ResourceLocation, AtlasPBRLoader.PBRTextureAtlasSprite> sprites) {
        Path path = dir.resolve(fileName + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            for (Map.Entry entry : sprites.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                AtlasPBRLoader.PBRTextureAtlasSprite sprite = (AtlasPBRLoader.PBRTextureAtlasSprite)((Object)entry.getValue());
                writer.write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", entry.getKey(), sprite.m_174743_(), sprite.m_174744_(), sprite.m_245424_().m_246492_(), sprite.m_245424_().m_245330_()));
            }
        }
        catch (IOException e) {
            Iris.logger.warn("Failed to write file {}", path, e);
        }
    }

    public PBRType getType() {
        return this.type;
    }

    public ResourceLocation getAtlasId() {
        return this.id;
    }

    public void addSprite(AtlasPBRLoader.PBRTextureAtlasSprite sprite) {
        this.texturesByName.put(sprite.m_245424_().m_246162_(), sprite);
    }

    @Nullable
    public AtlasPBRLoader.PBRTextureAtlasSprite getSprite(ResourceLocation id) {
        return this.texturesByName.get(id);
    }

    public void clear() {
        this.animatedTextures.forEach(TextureAtlasSprite.Ticker::close);
        this.texturesByName.clear();
        this.animatedTextures.clear();
    }

    public void upload(int atlasWidth, int atlasHeight, int mipLevel) {
        int glId = this.m_117963_();
        TextureUtil.prepareImage((int)glId, (int)mipLevel, (int)atlasWidth, (int)atlasHeight);
        TextureManipulationUtil.fillWithColor(glId, mipLevel, this.type.getDefaultValue());
        this.width = atlasWidth;
        this.height = atlasHeight;
        this.mipLevel = mipLevel;
        for (AtlasPBRLoader.PBRTextureAtlasSprite sprite : this.texturesByName.values()) {
            try {
                this.uploadSprite(sprite);
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.m_127521_((Throwable)throwable, (String)"Stitching texture atlas");
                CrashReportCategory crashReportCategory = crashReport.m_127514_("Texture being stitched together");
                crashReportCategory.m_128159_("Atlas path", (Object)this.id);
                crashReportCategory.m_128159_("Sprite", (Object)sprite);
                throw new ReportedException(crashReport);
            }
        }
        PBRAtlasHolder pbrHolder = ((TextureAtlasExtension)this.atlasTexture).getOrCreatePBRHolder();
        switch (this.type) {
            case NORMAL: {
                pbrHolder.setNormalAtlas(this);
                break;
            }
            case SPECULAR: {
                pbrHolder.setSpecularAtlas(this);
            }
        }
    }

    public boolean tryUpload(int atlasWidth, int atlasHeight, int mipLevel) {
        try {
            this.upload(atlasWidth, atlasHeight, mipLevel);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    protected void uploadSprite(AtlasPBRLoader.PBRTextureAtlasSprite sprite) {
        TextureAtlasSprite.Ticker spriteTicker = sprite.m_247406_();
        if (spriteTicker != null) {
            this.animatedTextures.add(spriteTicker);
            SpriteContents.Ticker sourceTicker = ((SpriteContentsExtension)sprite.getBaseSprite().m_245424_()).getCreatedTicker();
            SpriteContents.Ticker targetTicker = ((SpriteContentsExtension)sprite.m_245424_()).getCreatedTicker();
            if (sourceTicker != null && targetTicker != null) {
                PBRAtlasTexture.syncAnimation(sourceTicker, targetTicker);
                SpriteContentsTickerAccessor tickerAccessor = (SpriteContentsTickerAccessor)targetTicker;
                SpriteContentsAnimatedTextureAccessor infoAccessor = (SpriteContentsAnimatedTextureAccessor)tickerAccessor.getAnimationInfo();
                infoAccessor.invokeUploadFrame(sprite.m_174743_(), sprite.m_174744_(), ((SpriteContentsFrameInfoAccessor)infoAccessor.getFrames().get(tickerAccessor.getFrame())).getIndex());
                return;
            }
        }
        sprite.m_118416_();
    }

    public void cycleAnimationFrames() {
        this.m_117966_();
        for (TextureAtlasSprite.Ticker ticker : this.animatedTextures) {
            ticker.m_245385_();
        }
    }

    public void close() {
        PBRAtlasHolder pbrHolder = ((TextureAtlasExtension)this.atlasTexture).getPBRHolder();
        if (pbrHolder != null) {
            switch (this.type) {
                case NORMAL: {
                    pbrHolder.setNormalAtlas(null);
                    break;
                }
                case SPECULAR: {
                    pbrHolder.setSpecularAtlas(null);
                }
            }
        }
        this.clear();
    }

    public void m_6704_(ResourceManager manager) {
    }

    public void m_276079_(ResourceLocation id, Path path) {
        String fileName = id.m_179910_();
        TextureUtil.writeAsPNG((Path)path, (String)fileName, (int)this.m_117963_(), (int)this.mipLevel, (int)this.width, (int)this.height);
        PBRAtlasTexture.dumpSpriteNames(path, fileName, this.texturesByName);
    }

    @Override
    public ResourceLocation getDefaultDumpLocation() {
        return this.id;
    }
}

