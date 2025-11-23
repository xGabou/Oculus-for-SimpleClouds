/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.atlas.SpriteSource$Output
 *  net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister
 *  net.minecraft.resources.FileToIdConverter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.mixin.texture.pbr;

import net.irisshaders.iris.texture.pbr.PBRType;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={DirectoryLister.class})
public class MixinDirectoryLister {
    @Shadow
    @Final
    private String f_260442_;
    @Shadow
    @Final
    private String f_260464_;

    @Overwrite
    public void m_260891_(ResourceManager resourceManager, SpriteSource.Output output) {
        FileToIdConverter fileToIdConverter = new FileToIdConverter("textures/" + this.f_260442_, ".png");
        fileToIdConverter.m_247457_(resourceManager).forEach((location, resource) -> {
            ResourceLocation baseLocation;
            String basePath = PBRType.removeSuffix(location.m_135815_());
            if (basePath != null && resourceManager.m_213713_(baseLocation = location.m_247449_(basePath)).isPresent()) {
                return;
            }
            ResourceLocation resourceLocation = fileToIdConverter.m_245273_(location).m_246208_(this.f_260464_);
            output.m_261028_(resourceLocation, resource);
        });
    }
}

