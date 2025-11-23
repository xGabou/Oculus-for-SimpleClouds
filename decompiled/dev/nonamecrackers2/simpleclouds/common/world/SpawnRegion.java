/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  dev.nonamecrackers2.simpleclouds.api.common.world.ScAPISpawnRegion
 *  net.minecraft.util.RandomSource
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 */
package dev.nonamecrackers2.simpleclouds.common.world;

import com.google.common.collect.Lists;
import dev.nonamecrackers2.simpleclouds.api.common.world.ScAPISpawnRegion;
import java.util.ArrayList;
import java.util.function.BiPredicate;
import net.minecraft.util.RandomSource;
import org.joml.Vector2f;
import org.joml.Vector2i;

public record SpawnRegion(int x, int z, int radius) implements ScAPISpawnRegion
{
    public boolean includesPoint(int x, int z) {
        return x >= this.getMinX() && x <= this.getMaxX() && z >= this.getMinZ() && z <= this.getMaxZ();
    }

    public boolean intersectsCircle(float x, float z, float radius) {
        float dx = Math.abs(x - (float)this.x);
        float dz = Math.abs(z - (float)this.z);
        if (dx > (float)this.radius + radius || dz > (float)this.radius + radius) {
            return false;
        }
        if (dx <= (float)this.radius || dz < (float)this.radius) {
            return true;
        }
        float cornerDist = Vector2f.distanceSquared((float)dx, (float)dz, (float)this.radius, (float)this.radius);
        return cornerDist <= radius * radius;
    }

    public int getMinX() {
        return this.x - this.radius;
    }

    public int getMaxX() {
        return this.x + this.radius;
    }

    public int getMinZ() {
        return this.z - this.radius;
    }

    public int getMaxZ() {
        return this.z + this.radius;
    }

    public static void randomPointForEachRegion(Iterable<SpawnRegion> regions, RandomSource random, int maxAttemptsPerRegion, BiPredicate<SpawnRegion, Vector2i> consumer) {
        ArrayList prevPositions = Lists.newArrayList();
        block0: for (SpawnRegion region : regions) {
            if (prevPositions.stream().anyMatch(pos -> region.includesPoint(pos.x, pos.y))) continue;
            for (int i = 0; i < maxAttemptsPerRegion; ++i) {
                Vector2i pos2 = SpawnRegion.getRandomPointInRegion(region, random);
                if (!consumer.test(region, pos2)) continue;
                prevPositions.add(pos2);
                continue block0;
            }
        }
    }

    public static Vector2i getRandomPointInRegion(SpawnRegion region, RandomSource random) {
        int x = random.m_188503_(region.radius() * 2) - region.radius() + region.x();
        int z = random.m_188503_(region.radius() * 2) - region.radius() + region.z();
        return new Vector2i(x, z);
    }

    public static boolean doesCircleIntersect(Iterable<SpawnRegion> regions, float x, float z, float radius) {
        for (SpawnRegion region : regions) {
            if (!region.intersectsCircle(x, z, radius)) continue;
            return true;
        }
        return false;
    }
}

