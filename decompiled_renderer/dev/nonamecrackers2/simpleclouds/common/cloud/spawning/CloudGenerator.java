/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.CreateRegionFunction
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.SpawnInfo
 *  dev.nonamecrackers2.simpleclouds.api.common.event.CloudRegionNaturallySpawnEvent
 *  dev.nonamecrackers2.simpleclouds.api.common.event.CloudRegionRemovedEvent
 *  dev.nonamecrackers2.simpleclouds.api.common.event.CloudRegionRemovedEvent$Reason
 *  javax.annotation.Nullable
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec2
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 */
package dev.nonamecrackers2.simpleclouds.common.cloud.spawning;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.CreateRegionFunction;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.SpawnInfo;
import dev.nonamecrackers2.simpleclouds.api.common.event.CloudRegionNaturallySpawnEvent;
import dev.nonamecrackers2.simpleclouds.api.common.event.CloudRegionRemovedEvent;
import dev.nonamecrackers2.simpleclouds.common.api.ScAPICloudGeneratorImplHelper;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector2i;

public abstract class CloudGenerator
implements ScAPICloudGeneratorImplHelper {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/CloudGenerator");
    private List<SpawnRegion> spawnRegions = Lists.newArrayList();
    private final List<CloudRegion> clouds = Lists.newArrayList();
    protected RandomSource random = RandomSource.m_216327_();
    protected final Supplier<CloudSpawningConfig> spawnConfig;
    protected int ticksTillNextGen;
    protected final CloudTypeSource cloudGetter;

    public CloudGenerator(CloudTypeSource cloudGetter, Supplier<CloudSpawningConfig> spawnConfig) {
        this.cloudGetter = cloudGetter;
        this.spawnConfig = spawnConfig;
    }

    public int getTicksTillNextGen() {
        return this.ticksTillNextGen;
    }

    public Supplier<CloudSpawningConfig> getSpawnConfig() {
        return this.spawnConfig;
    }

    public final List<CloudRegion> getClouds() {
        return ImmutableList.copyOf(this.clouds);
    }

    public final List<SpawnRegion> getSpawnRegions() {
        return ImmutableList.copyOf(this.spawnRegions);
    }

    @Override
    public List<CloudRegion> getCloudsInRegion(SpawnRegion region) {
        ArrayList clouds = Lists.newArrayList();
        for (CloudRegion cloud : this.clouds) {
            if (!cloud.intersects(region)) continue;
            clouds.add(cloud);
        }
        return clouds;
    }

    @Nullable
    public CloudRegion getCloudAtWorldPosition(float worldX, float worldZ) {
        return this.getCloudAtPosition(worldX / 8.0f, worldZ / 8.0f);
    }

    @Nullable
    public CloudRegion getCloudAtPosition(float x, float z) {
        return (CloudRegion)CloudRegion.calculateAt(this.getClouds(), x, z).getLeft();
    }

    @Override
    public List<SpawnRegion> getRegionsThatOccupyCloud(CloudRegion cloud) {
        ArrayList regions = Lists.newArrayList();
        for (SpawnRegion region : this.spawnRegions) {
            if (!cloud.intersects(region)) continue;
            regions.add(region);
        }
        return regions;
    }

    public final int getTotalCloudRegions() {
        return this.clouds.size();
    }

    @Override
    public void setClouds(Collection<CloudRegion> clouds) {
        this.removeAllClouds();
        clouds.forEach(r -> this.clouds.add((CloudRegion)r));
    }

    public boolean removeAllClouds() {
        return this.removeClouds(r -> true);
    }

    @Override
    public boolean removeClouds(Predicate<CloudRegion> predicate) {
        return this.removeCloudsCount(predicate) > 0;
    }

    @Override
    public int removeCloudsCount(Predicate<CloudRegion> predicate) {
        int count = 0;
        Iterator<CloudRegion> iterator = this.clouds.iterator();
        while (iterator.hasNext()) {
            CloudRegion region = iterator.next();
            if (!predicate.test(region)) continue;
            iterator.remove();
            MinecraftForge.EVENT_BUS.post((Event)new CloudRegionRemovedEvent(null, (ScAPICloudRegion)region, CloudRegionRemovedEvent.Reason.MANUALLY));
            ++count;
        }
        return count;
    }

    @Override
    public boolean addCloud(CloudRegion region, Order order) {
        if (!this.cloudGetter.doesCloudTypeExist(region.getCloudTypeId())) {
            LOGGER.warn("Attempted to spawn a cloud formation: unknown id '{}'", (Object)region.getCloudTypeId());
            return false;
        }
        if (this.clouds.contains(region)) {
            return false;
        }
        for (SpawnRegion spawnRegion : this.getRegionsThatOccupyCloud(region)) {
            int totalCount = 0;
            for (CloudRegion cloud : this.clouds) {
                if (!cloud.intersects(spawnRegion)) continue;
                ++totalCount;
            }
            if (totalCount < 8) continue;
            return false;
        }
        order.appender.accept(this.clouds, region);
        return true;
    }

    public void initialize(RandomSource random, Level level) {
        this.random = RandomSource.m_216327_();
        this.spawnRegions = this.determineValidSpawnRegions(this.random, level);
        this.removeAllClouds();
        CloudSpawningConfig config = this.spawnConfig.get();
        this.ticksTillNextGen = config.getSpawnInterval().m_214085_(this.random);
    }

    public void tick(@Nullable Level level, float speed) {
        CloudSpawningConfig config;
        int maxSpawnInterval;
        this.spawnRegions = this.determineValidSpawnRegions(this.random, level);
        Iterator<CloudRegion> iterator = this.clouds.iterator();
        while (iterator.hasNext()) {
            CloudRegion region = iterator.next();
            boolean isVisible = SpawnRegion.doesCircleIntersect(this.spawnRegions, region.getWorldX(), region.getWorldZ(), region.getWorldRadius() / region.getStretch() + 1600.0f);
            if (isVisible != region.wasPriorVisible()) {
                this.onRegionVisibilityChange(region, isVisible);
            }
            region.tick(this.random, level, isVisible, speed);
            if (!this.cloudGetter.doesCloudTypeExist(region.getCloudTypeId())) {
                LOGGER.warn("Cloud type with id {} no longer exists, removing cloud region", (Object)region.getCloudTypeId());
                iterator.remove();
                MinecraftForge.EVENT_BUS.post((Event)new CloudRegionRemovedEvent(level, (ScAPICloudRegion)region, CloudRegionRemovedEvent.Reason.CLOUD_TYPE_NO_LONGER_EXISTS));
            }
            if (!region.isDead()) continue;
            iterator.remove();
            CloudRegionRemovedEvent.Reason reason = CloudRegionRemovedEvent.Reason.NATURALLY;
            if (!region.wasPriorVisible()) {
                reason = CloudRegionRemovedEvent.Reason.NO_LONGER_VISIBLE;
            }
            MinecraftForge.EVENT_BUS.post((Event)new CloudRegionRemovedEvent(level, (ScAPICloudRegion)region, reason));
        }
        if (this.ticksTillNextGen > 0) {
            this.ticksTillNextGen -= Math.max(1, Mth.m_14167_((float)speed));
        }
        if (this.ticksTillNextGen > (maxSpawnInterval = (config = this.spawnConfig.get()).getSpawnInterval().m_142737_())) {
            this.ticksTillNextGen = maxSpawnInterval;
        }
        if (!SimpleCloudsAPI.getApi().getHooks().isExternalWeatherControlEnabled() && !config.isEmpty() && this.shouldGenerateCloud(config, this.random, level)) {
            this.spawnCloud(config, level);
        }
    }

    protected boolean shouldGenerateCloud(CloudSpawningConfig config, RandomSource random, Level level) {
        return this.ticksTillNextGen <= 0;
    }

    public Optional<CloudRegion> spawnCloud(CloudSpawningConfig config, Level level) {
        return this.spawnCloud(() -> config.getRandom(this.random).orElse(null), config.getSpawnInterval().m_214085_(this.random), config.getMaxRegions(), level);
    }

    public Optional<CloudRegion> spawnCloud(Supplier<SpawnInfo> infoGetter, int nextSpawnInterval, int maxRegions, Level level) {
        return this.spawnCloud(infoGetter, nextSpawnInterval, maxRegions, level, this::createRegion);
    }

    public Optional<CloudRegion> spawnCloud(Supplier<SpawnInfo> infoGetter, int nextSpawnInterval, int maxRegions, Level level, CreateRegionFunction regionFunc) {
        this.ticksTillNextGen = nextSpawnInterval;
        MutableObject spawnedCloud = new MutableObject();
        SpawnRegion.randomPointForEachRegion(this.spawnRegions, this.random, 10, (r, p) -> {
            if (this.getCloudsInRegion((SpawnRegion)((Object)r)).size() >= maxRegions) {
                return true;
            }
            float x = (float)p.x + 0.5f;
            float z = (float)p.y + 0.5f;
            SpawnInfo info = (SpawnInfo)infoGetter.get();
            if (info == null) {
                return false;
            }
            CloudType type = this.cloudGetter.getCloudTypeForId(info.cloudType());
            if (type == null) {
                LOGGER.warn("Spawn config has unknown cloud type with id '{}'", (Object)info.cloudType());
                return false;
            }
            return regionFunc.create((SpawnInfo)infoGetter.get(), (float)r.x() + 0.5f, (float)r.z() + 0.5f, x, z, this.random, true).map(apiRegion -> {
                CloudRegion region = (CloudRegion)apiRegion;
                if (this.addCloud(region, Order.USE_WEIGHT)) {
                    spawnedCloud.setValue((Object)region);
                    MinecraftForge.EVENT_BUS.post((Event)new CloudRegionNaturallySpawnEvent(level, apiRegion));
                    return true;
                }
                return false;
            }).orElse(false);
        });
        return Optional.ofNullable((CloudRegion)spawnedCloud.getValue());
    }

    public Optional<CloudRegion> createRegion(SpawnInfo info, float playerX, float playerZ, float x, float z, RandomSource random, boolean growTime) {
        for (CloudRegion region : this.getClouds()) {
            float dist = Vector2f.distance((float)x, (float)z, (float)region.getWorldX(), (float)region.getWorldZ()) - region.getWorldRadius();
            if (!(dist <= 500.0f)) continue;
            return Optional.empty();
        }
        float deltaAdj = info.movesToPlayer() ? 0.1f : 1.0f;
        float deltaX = (playerX - x) * (1.0f + random.m_188501_() * deltaAdj);
        float deltaZ = (playerZ - z) * (1.0f + random.m_188501_() * deltaAdj);
        float rotation = (float)Math.atan2(deltaX, deltaZ) + (float)Math.PI;
        Vec2 direction = random.m_188503_(5) == 0 ? new Vec2(random.m_188501_() * 2.0f - 1.0f, random.m_188501_() * 2.0f - 1.0f).m_165902_() : new Vec2(deltaX, deltaZ).m_165902_();
        float radius = info.determineRadius(random);
        float maxSpeed = info.determineSpeed(random);
        float accelerationFactor = 0.01f;
        int existTicks = info.determineExistTicks(random);
        int growTicks = growTime ? info.determineGrowTicks(random) : 0;
        float stretchFactor = info.determineStretchFactor(random);
        return Optional.of(new CloudRegion(info.cloudType(), direction, maxSpeed, accelerationFactor, x / 8.0f, z / 8.0f, radius / 8.0f, rotation, stretchFactor, existTicks, growTicks, info.orderWeight()));
    }

    public void doInitialGen(int x, int z, Level level, boolean ignoreOtherRegions) {
        SpawnRegion region = new SpawnRegion(x, z, SimpleCloudsConstants.SPAWN_RADIUS);
        CloudSpawningConfig config = this.spawnConfig.get();
        if (this.getCloudsInRegion(region).size() > config.getMaxInitialRegions()) {
            return;
        }
        block0: for (int i = 0; i < config.getMaxInitialRegions(); ++i) {
            for (int j = 0; j < 10; ++j) {
                CloudRegion cloudFormation;
                Vector2i pos = SpawnRegion.getRandomPointInRegion(region, this.random);
                if (this.getCloudsInRegion(region).size() >= config.getMaxInitialRegions() || !ignoreOtherRegions && this.spawnRegions.stream().anyMatch(r -> r.includesPoint(pos.x, pos.y)) || (cloudFormation = (CloudRegion)this.createRegion(config.getRandom(this.random).orElse(null), (float)x + 0.5f, (float)z + 0.5f, (float)pos.x + 0.5f, (float)pos.y + 0.5f, this.random, false).orElse(null)) == null) continue;
                this.addCloud(cloudFormation, Order.USE_WEIGHT);
                continue block0;
            }
        }
    }

    protected void onRegionVisibilityChange(CloudRegion region, boolean nowVisible) {
    }

    protected abstract List<SpawnRegion> determineValidSpawnRegions(RandomSource var1, @Nullable Level var2);

    public static enum Order {
        TOP((l, r) -> l.add(r)),
        BOTTOM((l, r) -> l.add(0, r)),
        USE_WEIGHT((l, r) -> {
            int prevWeight = 0;
            for (int i = 0; i < l.size(); ++i) {
                CloudRegion region = (CloudRegion)l.get(i);
                if (r.getOrderWeight() < prevWeight || r.getOrderWeight() > region.getOrderWeight()) continue;
                l.add(i, r);
                prevWeight = region.getOrderWeight();
                return;
            }
            l.add(r);
        });

        private final BiConsumer<List<CloudRegion>, CloudRegion> appender;

        private Order(BiConsumer<List<CloudRegion>, CloudRegion> appender) {
            this.appender = appender;
        }
    }
}

