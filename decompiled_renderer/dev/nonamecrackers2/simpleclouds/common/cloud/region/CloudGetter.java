/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.nonamecrackers2.simpleclouds.common.cloud.region;

import com.google.common.collect.ImmutableList;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

public interface CloudGetter
extends CloudTypeSource {
    public static final CloudGetter EMPTY = new CloudGetter(){

        @Override
        public CloudType getCloudTypeForId(ResourceLocation id) {
            if (id.toString().equals("simpleclouds:empty")) {
                return SimpleCloudsConstants.EMPTY;
            }
            return null;
        }

        @Override
        public CloudType[] getIndexedCloudTypes() {
            return new CloudType[]{SimpleCloudsConstants.EMPTY};
        }

        @Override
        public List<CloudRegion> getClouds() {
            return ImmutableList.of();
        }

        @Override
        public Pair<CloudType, Float> getCloudTypeAtPosition(float x, float z) {
            return Pair.of((Object)SimpleCloudsConstants.EMPTY, (Object)Float.valueOf(0.0f));
        }
    };

    public List<CloudRegion> getClouds();

    public Pair<CloudType, Float> getCloudTypeAtPosition(float var1, float var2);

    default public Pair<CloudType, Float> getCloudTypeAtWorldPos(float x, float z) {
        return this.getCloudTypeAtPosition(x / 8.0f, z / 8.0f);
    }
}

