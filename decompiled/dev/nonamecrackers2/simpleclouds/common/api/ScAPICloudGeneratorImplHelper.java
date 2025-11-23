/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.ScAPICloudGenerator
 *  dev.nonamecrackers2.simpleclouds.api.common.world.ScAPISpawnRegion
 */
package dev.nonamecrackers2.simpleclouds.common.api;

import dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.ScAPICloudGenerator;
import dev.nonamecrackers2.simpleclouds.api.common.world.ScAPISpawnRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface ScAPICloudGeneratorImplHelper
extends ScAPICloudGenerator {
    default public void api_setClouds(Collection<? extends ScAPICloudRegion> clouds) {
        this.setClouds((List)clouds);
    }

    default public List<? extends ScAPICloudRegion> api_getCloudsInRegion(ScAPISpawnRegion region) {
        return this.getCloudsInRegion((SpawnRegion)region);
    }

    default public List<? extends ScAPISpawnRegion> api_getRegionsThatOccupyCloud(ScAPICloudRegion cloud) {
        return this.getRegionsThatOccupyCloud((CloudRegion)cloud);
    }

    default public boolean api_removeClouds(Predicate<? extends ScAPICloudRegion> predicate) {
        return this.removeClouds(predicate);
    }

    default public int api_removeCloudsCount(Predicate<? extends ScAPICloudRegion> predicate) {
        return this.removeCloudsCount(predicate);
    }

    default public boolean addCloudToTop(ScAPICloudRegion region) {
        return this.addCloud((CloudRegion)region, CloudGenerator.Order.TOP);
    }

    default public boolean addCloudToBottom(ScAPICloudRegion region) {
        return this.addCloud((CloudRegion)region, CloudGenerator.Order.BOTTOM);
    }

    default public boolean addCloudUsingWeight(ScAPICloudRegion region) {
        return this.addCloud((CloudRegion)region, CloudGenerator.Order.USE_WEIGHT);
    }

    public void setClouds(Collection<CloudRegion> var1);

    public List<CloudRegion> getCloudsInRegion(SpawnRegion var1);

    public List<SpawnRegion> getRegionsThatOccupyCloud(CloudRegion var1);

    public boolean removeClouds(Predicate<CloudRegion> var1);

    public int removeCloudsCount(Predicate<CloudRegion> var1);

    public boolean addCloud(CloudRegion var1, CloudGenerator.Order var2);
}

