/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.storage.DimensionDataStorage
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.common.event.TickChunks;
import dev.nonamecrackers2.simpleclouds.common.world.CloudData;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManagerHolder;
import dev.nonamecrackers2.simpleclouds.common.world.ServerCloudManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ServerLevel.class})
public abstract class MixinServerLevel
implements CloudManagerHolder<ServerLevel> {
    @Unique
    private ServerCloudManager cloudManager;
    @Shadow
    @Final
    private MinecraftServer f_8548_;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    public void simpleclouds$createCloudManager_init(CallbackInfo ci) {
        this.cloudManager = new ServerCloudManager((ServerLevel)this);
        this.cloudManager.init(RandomSource.m_216335_((long)this.f_8548_.m_129910_().m_246337_().m_245499_()).m_188505_());
        this.m_8895_().m_164861_(tag -> CloudData.load(this.cloudManager, tag), () -> new CloudData(this.cloudManager), "clouddata");
    }

    @Inject(method={"advanceWeatherCycle"}, at={@At(value="HEAD")}, cancellable=true)
    public void simpleclouds$disableWeatherCycle_advanceWeatherCycle(CallbackInfo ci) {
        if (!this.cloudManager.shouldUseVanillaWeather()) {
            this.m_184097_();
            ci.cancel();
        }
    }

    @Inject(method={"tickChunk"}, at={@At(value="RETURN")})
    public void simpleclouds$localizedWeatherHandlePrecipitation(LevelChunk chunk, int tickSpeed, CallbackInfo ci) {
        CloudManager<Level> manager = CloudManager.get((Level)this);
        if (!manager.shouldUseVanillaWeather()) {
            TickChunks.rainAndSnowVanillaCompatibility((ServerLevel)this, (ChunkAccess)chunk);
        }
    }

    @Shadow
    protected abstract void m_184097_();

    public ServerCloudManager getCloudManager() {
        return this.cloudManager;
    }

    @Shadow
    public abstract DimensionDataStorage m_8895_();
}

