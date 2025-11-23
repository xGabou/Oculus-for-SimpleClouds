/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.ScAPIInternal
 *  dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI
 *  dev.nonamecrackers2.simpleclouds.api.common.ScAPIHooks
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion
 *  dev.nonamecrackers2.simpleclouds.api.common.world.ScAPICloudManager
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec2
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 */
package dev.nonamecrackers2.simpleclouds.common.api;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.api.ScAPIInternal;
import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.api.common.ScAPIHooks;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion;
import dev.nonamecrackers2.simpleclouds.api.common.world.ScAPICloudManager;
import dev.nonamecrackers2.simpleclouds.common.api.SimpleCloudsHooks;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.apache.maven.artifact.versioning.ArtifactVersion;

public class SimpleCloudsAPIImpl
implements SimpleCloudsAPI {
    public static final SimpleCloudsAPIImpl INSTANCE = new SimpleCloudsAPIImpl();
    private final SimpleCloudsHooks hooks = new SimpleCloudsHooks();

    public static void bootstrap() {
    }

    public ArtifactVersion getSimpleCloudsVersion() {
        return SimpleCloudsMod.getModVersion();
    }

    public ScAPICloudManager getCloudManager(Level level) {
        return CloudManager.get(level);
    }

    public ScAPIHooks getHooks() {
        return this.hooks;
    }

    public ScAPICloudRegion createCloudRegion(ResourceLocation cloudTypeId, Vec2 movementDirection, float maxSpeed, float accelerationFactor, float posX, float posZ, float radius, float rotation, float stretchFactor, int existsForTicks, int growTicks, int orderWeight) {
        return new CloudRegion(cloudTypeId, movementDirection, maxSpeed, accelerationFactor, posX, posZ, radius, rotation, stretchFactor, existsForTicks, growTicks, orderWeight);
    }

    static {
        ScAPIInternal._setApi((SimpleCloudsAPI)INSTANCE);
    }
}

