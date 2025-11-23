/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 */
package dev.nonamecrackers2.simpleclouds.client.mesh.lod;

import net.minecraft.world.phys.AABB;

public record PreparedChunk(int lodLevel, int lodScale, int x, int y, int z, int noOcclusionDirectionIndex, AABB bounds) {
    public static PreparedChunk create(int lodLevel, int lodScale, int x, int y, int z, int noOcclusionDirectionIndex) {
        float chunkSize = 32.0f * (float)lodScale;
        float maxY = 256.0f;
        float offsetX = (float)x * chunkSize;
        float offsetZ = (float)z * chunkSize;
        AABB bounds = new AABB((double)offsetX, -320.0, (double)offsetZ, (double)(offsetX + chunkSize), (double)maxY, (double)(offsetZ + chunkSize));
        return new PreparedChunk(lodLevel, lodScale, x, y, z, noOcclusionDirectionIndex, bounds);
    }
}

