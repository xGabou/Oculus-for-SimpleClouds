/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.client.renderer.settings;

import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.mesh.LevelOfDetailOptions;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CloudsRendererSettings {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/CloudsRendererSettings");
    public static final CloudsRendererSettings DEFAULT = new CloudsRendererSettings(){

        @Override
        public boolean useTransparency() {
            return (Boolean)SimpleCloudsConfig.CLIENT.transparency.get();
        }

        @Override
        public boolean shadedClouds() {
            return (Boolean)SimpleCloudsConfig.CLIENT.shadedClouds.get();
        }

        @Override
        public boolean useFixedMeshDataSectionSize() {
            return (Boolean)SimpleCloudsConfig.CLIENT.concurrentComputeDispatches.get();
        }

        @Override
        public LevelOfDetailOptions getLodConfig() {
            return (LevelOfDetailOptions)((Object)SimpleCloudsConfig.CLIENT.levelOfDetail.get());
        }

        @Override
        public CloudMode getCloudMode() {
            ClientLevel level = Minecraft.m_91087_().f_91073_;
            if (level != null) {
                return CloudManager.get(level).getCloudMode();
            }
            return CloudMode.DEFAULT;
        }

        @Override
        @Nullable
        public CloudType getSingleModeCloudType() {
            ClientLevel level = Minecraft.m_91087_().f_91073_;
            String rawId = level != null ? CloudManager.get(level).getSingleModeCloudTypeRawId() : "simpleclouds:itty_bitty";
            return ClientSideCloudTypeManager.getInstance().getCloudTypeFromRawId(rawId).orElse(null);
        }
    };
    @Nullable
    private CloudMode currentCloudMode;
    @Nullable
    private LevelOfDetailOptions currentLod;

    public abstract CloudMode getCloudMode();

    public abstract boolean shadedClouds();

    public abstract boolean useTransparency();

    public abstract boolean useFixedMeshDataSectionSize();

    public abstract LevelOfDetailOptions getLodConfig();

    @Nullable
    public abstract CloudType getSingleModeCloudType();

    public boolean needsReinitialization(@Nullable CloudMeshGenerator generator) {
        return this.checkAndOrBeginInitialization(generator, false);
    }

    public boolean checkAndOrBeginInitialization(@Nullable CloudMeshGenerator generator) {
        return this.checkAndOrBeginInitialization(generator, true);
    }

    protected boolean checkAndOrBeginInitialization(@Nullable CloudMeshGenerator generator, boolean initializesAfterwards) {
        boolean flag = false;
        CloudMode mode = Objects.requireNonNull(this.getCloudMode(), "Must supply a cloud mode");
        boolean shadedClouds = this.shadedClouds();
        boolean transparency = this.useTransparency();
        LevelOfDetailOptions lod = Objects.requireNonNull(this.getLodConfig(), "Must supply a LOD");
        boolean fixedMeshDataSectionSize = this.useFixedMeshDataSectionSize();
        if (generator != null) {
            if (this.currentCloudMode != mode) {
                flag = true;
            } else if (generator.shadedCloudsEnabled() != shadedClouds) {
                flag = true;
            } else if (generator.transparencyEnabled() != transparency) {
                flag = true;
            } else if (this.currentLod != lod) {
                flag = true;
            } else if (generator.usesFixedMeshDataSectionSize() != fixedMeshDataSectionSize) {
                flag = true;
            }
        } else {
            flag = true;
        }
        if (flag && initializesAfterwards) {
            this.currentCloudMode = mode;
            this.currentLod = lod;
            LOGGER.debug("Beginning mesh generator initialization for cloud mode {}, shaded clouds {}, transparency {}, and LOD {}", (Object)mode, (Object)shadedClouds, (Object)transparency, (Object)lod);
        }
        return flag;
    }

    @Nullable
    public CloudMode getCurrentCloudMode() {
        return this.currentCloudMode;
    }

    @Nullable
    public LevelOfDetailOptions getCurrentLod() {
        return this.currentLod;
    }
}

