/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.nonamecrackers2.simpleclouds.client.world;

import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.cloud.region.ClientCloudGenerator;
import dev.nonamecrackers2.simpleclouds.client.cloud.spawning.ClientSideCloudSpawningManager;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

public class ClientCloudManager
extends CloudManager<ClientLevel> {
    private boolean receivedSync;

    public ClientCloudManager(ClientLevel level) {
        super(level, ClientSideCloudTypeManager.getInstance(), ClientSideCloudSpawningManager.getClientInstance()::getConfig, ClientCloudGenerator::new);
    }

    @Override
    public ClientCloudGenerator getCloudGenerator() {
        return (ClientCloudGenerator)super.getCloudGenerator();
    }

    @Override
    public CloudMode getCloudMode() {
        if (this.receivedSync && SimpleCloudsConfig.SERVER_SPEC.isLoaded()) {
            return (CloudMode)SimpleCloudsConfig.SERVER.cloudMode.get();
        }
        return (CloudMode)SimpleCloudsConfig.CLIENT.cloudMode.get();
    }

    @Override
    public String getSingleModeCloudTypeRawId() {
        if (this.receivedSync && SimpleCloudsConfig.SERVER_SPEC.isLoaded()) {
            return (String)SimpleCloudsConfig.SERVER.singleModeCloudType.get();
        }
        return (String)SimpleCloudsConfig.CLIENT.singleModeCloudType.get();
    }

    @Override
    protected void resetVanillaWeather() {
        ((ClientLevel)this.level).m_46734_(0.0f);
        ((ClientLevel)this.level).m_46707_(0.0f);
    }

    @Override
    protected void tickLightning() {
        if (this.receivedSync) {
            return;
        }
        super.tickLightning();
    }

    @Override
    protected void attemptToSpawnLightning() {
        Minecraft mc = Minecraft.m_91087_();
        Camera camera = mc.f_91063_.m_109153_();
        int camX = camera.m_90588_().m_123341_();
        int camZ = camera.m_90588_().m_123343_();
        for (int i = 0; i < 12; ++i) {
            int x = this.random.m_188503_(20000) - 10000 + camX;
            int z = this.random.m_188503_(20000) - 10000 + camZ;
            Pair<CloudType, Float> info = this.getCloudTypeAtWorldPos((float)x + 0.5f, (float)z + 0.5f);
            float fade = ((Float)info.getRight()).floatValue();
            CloudType type = (CloudType)info.getLeft();
            if (!ClientCloudManager.isValidLightning(type, fade, this.random)) continue;
            this.spawnLightning(type, fade, x, z, this.random.m_188503_(3) == 0);
            break;
        }
    }

    @Override
    protected void spawnLightning(CloudType type, float fade, int x, int z, boolean soundOnly) {
        int y = (int)(type.stormStart() * 8.0f + (float)this.getCloudHeight());
        float spreadnessFactor = this.random.m_188501_();
        float length = spreadnessFactor * 300.0f + 200.0f;
        float minPitch = 20.0f + spreadnessFactor * 40.0f;
        float maxPitch = 80.0f + spreadnessFactor * 10.0f;
        SimpleCloudsRenderer.getInstance().getWorldEffectsManager().spawnLightning(new BlockPos(x, y, z), soundOnly, this.random.m_188502_(), 4, 2, length, 20.0f, minPitch, maxPitch);
    }

    @Override
    protected boolean determineUseVanillaWeather() {
        return !this.receivedSync || super.determineUseVanillaWeather();
    }

    @Override
    public float getCloudSpeed() {
        return this.receivedSync ? super.getCloudSpeed() : ((Double)SimpleCloudsConfig.CLIENT.speedModifier.get()).floatValue();
    }

    @Override
    public int getCloudHeight() {
        return this.receivedSync ? super.getCloudHeight() : ((Integer)SimpleCloudsConfig.CLIENT.cloudHeight.get()).intValue();
    }

    public void setReceivedSync() {
        this.receivedSync = true;
    }

    public boolean hasReceivedSync() {
        return this.receivedSync;
    }

    public static boolean isAvailableServerSide() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ != null) {
            return ((ClientCloudManager)CloudManager.get(mc.f_91073_)).hasReceivedSync();
        }
        return false;
    }
}

