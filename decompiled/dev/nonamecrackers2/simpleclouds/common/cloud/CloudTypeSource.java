/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 */
package dev.nonamecrackers2.simpleclouds.common.cloud;

import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface CloudTypeSource {
    @Nullable
    public CloudType getCloudTypeForId(ResourceLocation var1);

    public CloudType[] getIndexedCloudTypes();

    default public boolean doesCloudTypeExist(ResourceLocation id) {
        return this.getCloudTypeForId(id) != null;
    }

    default public Optional<CloudType> getCloudTypeFromRawId(String id) {
        ResourceLocation loc = ResourceLocation.m_135820_((String)id);
        if (id != null) {
            return Optional.ofNullable(this.getCloudTypeForId(loc));
        }
        return Optional.empty();
    }
}

