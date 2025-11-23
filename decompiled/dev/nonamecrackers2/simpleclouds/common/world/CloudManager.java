/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType
 *  dev.nonamecrackers2.simpleclouds.api.common.event.ModifyCloudSpeedEvent
 *  dev.nonamecrackers2.simpleclouds.api.common.world.ScAPICloudManager
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 *  org.apache.commons.lang3.tuple.Pair
 *  org.joml.Vector2f
 */
package dev.nonamecrackers2.simpleclouds.common.world;

import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType;
import dev.nonamecrackers2.simpleclouds.api.common.event.ModifyCloudSpeedEvent;
import dev.nonamecrackers2.simpleclouds.api.common.world.ScAPICloudManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudGetter;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManagerHolder;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2f;

public abstract class CloudManager<T extends Level>
implements CloudGetter,
ScAPICloudManager {
    public static final int CLOUD_HEIGHT_MAX = 2048;
    public static final int CLOUD_HEIGHT_MIN = 0;
    public static final int UPDATE_INTERVAL = 200;
    public static final float RANDOM_SPREAD = 10000.0f;
    public static final float SCROLL_OFFSET = 100.0f;
    protected final T level;
    protected final CloudTypeSource cloudSource;
    protected final CloudGenerator cloudGenerator;
    private long seed;
    @Nullable
    protected RandomSource random;
    protected float scrollAngle;
    protected float scrollXO;
    protected float scrollYO;
    protected float scrollZO;
    protected float scrollX;
    protected float scrollY;
    protected float scrollZ;
    protected float speed = 1.0f;
    protected int cloudHeight = 128;
    protected int tickCount;
    protected int nextLightningStrike = 60;
    protected boolean useVanillaWeather;

    public static <T extends Level> CloudManager<T> get(T level) {
        return Objects.requireNonNull(((CloudManagerHolder)level).getCloudManager(), "Cloud manager is not available, this shouldn't happen!");
    }

    public CloudManager(T level, CloudTypeSource source, Supplier<CloudSpawningConfig> configGetter, BiFunction<CloudGetter, Supplier<CloudSpawningConfig>, CloudGenerator> generatorFunc) {
        this.level = level;
        this.cloudSource = source;
        this.cloudGenerator = generatorFunc.apply(this, configGetter);
        this.useVanillaWeather = this.determineUseVanillaWeather();
    }

    public CloudGenerator getCloudGenerator() {
        return this.cloudGenerator;
    }

    @Override
    public List<CloudRegion> getClouds() {
        return this.cloudGenerator.getClouds();
    }

    @Override
    public CloudType getCloudTypeForId(ResourceLocation id) {
        return this.cloudSource.getCloudTypeForId(id);
    }

    @Override
    public CloudType[] getIndexedCloudTypes() {
        return this.cloudSource.getIndexedCloudTypes();
    }

    public boolean isCloudGeneratorActive() {
        return this.getCloudMode() != CloudMode.SINGLE;
    }

    public void onPlayerJoin(Player player) {
        if (this.isCloudGeneratorActive() && !SimpleCloudsAPI.getApi().getHooks().isExternalWeatherControlEnabled()) {
            this.cloudGenerator.doInitialGen(player.m_146903_(), player.m_146907_(), (Level)this.level, false);
        }
    }

    @Override
    public Pair<CloudType, Float> getCloudTypeAtPosition(float x, float z) {
        CloudType type;
        if (this.getCloudMode() != CloudMode.SINGLE) {
            Pair<CloudRegion, Float> result = CloudRegion.calculateAt(this.getClouds(), x, z);
            CloudType type2 = null;
            if (result.getLeft() != null) {
                type2 = this.getCloudTypeForId(((CloudRegion)result.getLeft()).getCloudTypeId());
            }
            if (type2 == null) {
                type2 = SimpleCloudsConstants.EMPTY;
            }
            return Pair.of((Object)type2, (Object)Float.valueOf(1.0f - ((Float)result.getRight()).floatValue()));
        }
        String rawId = this.getSingleModeCloudTypeRawId();
        ResourceLocation id = ResourceLocation.m_135820_((String)rawId);
        if (id != null && (type = this.getCloudTypeForId(id)) != null) {
            return Pair.of((Object)type, (Object)Float.valueOf(0.0f));
        }
        return Pair.of((Object)SimpleCloudsConstants.EMPTY, (Object)Float.valueOf(0.0f));
    }

    public Pair<Boolean, Biome.Precipitation> getPrecipitationAt(BlockPos pos) {
        if (!this.level.m_45527_(pos) || this.level.m_5452_(Heightmap.Types.MOTION_BLOCKING, pos).m_123342_() > pos.m_123342_()) {
            return Pair.of((Object)false, (Object)Biome.Precipitation.NONE);
        }
        Biome.Precipitation precipitation = ((Biome)this.level.m_204166_(pos).m_203334_()).m_264600_(pos);
        Pair<CloudType, Float> info = this.getCloudTypeAtWorldPos((float)pos.m_123341_() + 0.5f, (float)pos.m_123343_() + 0.5f);
        CloudType type = (CloudType)info.getLeft();
        if ((float)pos.m_123342_() + 0.5f > type.stormStart() * 8.0f + 128.0f) {
            return Pair.of((Object)false, (Object)Biome.Precipitation.NONE);
        }
        if (((CloudType)info.getLeft()).weatherType().includesRain() && ((Float)info.getRight()).floatValue() < 0.69f) {
            return Pair.of((Object)true, (Object)precipitation);
        }
        return Pair.of((Object)false, (Object)Biome.Precipitation.NONE);
    }

    public boolean isRainingAt(BlockPos pos) {
        Pair<Boolean, Biome.Precipitation> val = this.getPrecipitationAt(pos);
        return (Boolean)val.getLeft() != false && val.getRight() != Biome.Precipitation.RAIN;
    }

    public boolean isSnowingAt(BlockPos pos) {
        Pair<Boolean, Biome.Precipitation> val = this.getPrecipitationAt(pos);
        return (Boolean)val.getLeft() != false && val.getRight() == Biome.Precipitation.SNOW;
    }

    public boolean hasPrecipitationAt(BlockPos pos) {
        Pair<Boolean, Biome.Precipitation> val = this.getPrecipitationAt(pos);
        return (Boolean)val.getLeft() != false && val.getRight() != Biome.Precipitation.NONE;
    }

    public float getRainLevel(float x, float y, float z) {
        Pair<CloudType, Float> info = this.getCloudTypeAtWorldPos(x, z);
        CloudType type = (CloudType)info.getLeft();
        if (!type.weatherType().includesRain()) {
            return 0.0f;
        }
        float fade = ((Float)info.getRight()).floatValue();
        float verticalFade = 1.0f - Mth.m_14036_((float)((y - (type.stormStart() * 8.0f + (float)this.getCloudHeight())) / 32.0f), (float)0.0f, (float)1.0f);
        return Math.min(1.0f, Math.max(0.0f, 0.7f - fade) / 0.1f) * verticalFade;
    }

    public void init(long seed) {
        RandomSource random;
        this.random = random = this.setSeed(seed);
        this.speed = 1.0f;
        this.cloudGenerator.initialize(random, (Level)this.level);
    }

    public int getCloudHeight() {
        return this.cloudHeight;
    }

    public void setCloudHeight(int height) {
        this.cloudHeight = height;
    }

    public void tick() {
        MinecraftServer server = this.level.m_7654_();
        if (server instanceof DedicatedServer && server.m_7416_() == 0) {
            return;
        }
        ++this.tickCount;
        this.scrollXO = this.scrollX;
        this.scrollYO = this.scrollY;
        this.scrollZO = this.scrollZ;
        float speed = this.getCloudSpeed();
        speed = this.modifyCloudSpeed(speed);
        if (this.isCloudGeneratorActive()) {
            this.cloudGenerator.tick((Level)this.level, speed);
        }
        this.scrollAngle += (speed *= 1.0E-4f);
        this.scrollX = (float)Math.cos(this.scrollAngle) * 100.0f;
        this.scrollY = 0.0f;
        this.scrollZ = (float)Math.sin(this.scrollAngle) * 100.0f;
        boolean flag = this.determineUseVanillaWeather();
        if (flag != this.useVanillaWeather) {
            this.useVanillaWeather = flag;
            this.resetVanillaWeather();
        }
        if (!this.useVanillaWeather) {
            this.tickLightning();
        }
    }

    protected void resetVanillaWeather() {
    }

    protected void tickLightning() {
        if (this.nextLightningStrike <= 0 || --this.nextLightningStrike > 0) {
            return;
        }
        this.attemptToSpawnLightning();
        int minInterval = (Integer)SimpleCloudsConfig.COMMON.lightningSpawnIntervalMin.get();
        int maxInterval = Math.max(minInterval, (Integer)SimpleCloudsConfig.COMMON.lightningSpawnIntervalMax.get());
        this.nextLightningStrike = Mth.m_216287_((RandomSource)this.random, (int)minInterval, (int)maxInterval);
    }

    protected boolean determineUseVanillaWeather() {
        return CloudManager.useVanillaWeather(this.level, this);
    }

    public final boolean shouldUseVanillaWeather() {
        return this.useVanillaWeather;
    }

    protected abstract void attemptToSpawnLightning();

    protected abstract void spawnLightning(CloudType var1, float var2, int var3, int var4, boolean var5);

    public abstract CloudMode getCloudMode();

    public abstract String getSingleModeCloudTypeRawId();

    public void spawnLightning(int x, int z, boolean soundOnly) {
        Pair<CloudType, Float> info = this.getCloudTypeAtWorldPos((float)x + 0.5f, (float)z + 0.5f);
        this.spawnLightning((CloudType)info.getLeft(), ((Float)info.getRight()).floatValue(), x, z, soundOnly);
    }

    public Vector2f calculateWindDirection() {
        float dirX = Mth.m_14089_((float)this.scrollAngle);
        float dirZ = Mth.m_14031_((float)this.scrollAngle);
        return new Vector2f(dirX, dirZ);
    }

    public int getTickCount() {
        return this.tickCount;
    }

    public long getSeed() {
        return this.seed;
    }

    public RandomSource setSeed(long seed) {
        this.seed = seed;
        return RandomSource.m_216335_((long)seed);
    }

    protected float modifyCloudSpeed(float speed) {
        ModifyCloudSpeedEvent event = new ModifyCloudSpeedEvent(this.level, (ScAPICloudManager)this, speed);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getCurrentSpeed();
    }

    public float getCloudSpeed() {
        return this.speed;
    }

    public void setCloudSpeed(float speed) {
        this.speed = Math.max(0.0f, speed);
    }

    public float getScrollAngle() {
        return this.scrollAngle;
    }

    public void setScrollAngle(float angle) {
        this.scrollAngle = angle;
    }

    public float getScrollX() {
        return this.scrollX;
    }

    public float getScrollY() {
        return this.scrollY;
    }

    public float getScrollZ() {
        return this.scrollZ;
    }

    public float getScrollX(float partialTicks) {
        return Mth.m_14179_((float)partialTicks, (float)this.scrollXO, (float)this.scrollX);
    }

    public float getScrollY(float partialTicks) {
        return Mth.m_14179_((float)partialTicks, (float)this.scrollYO, (float)this.scrollY);
    }

    public float getScrollZ(float partialTicks) {
        return Mth.m_14179_((float)partialTicks, (float)this.scrollZO, (float)this.scrollZ);
    }

    public static boolean isValidLightning(CloudType type, float fade, RandomSource random) {
        return type.weatherType().includesThunder() && fade < 0.8f;
    }

    public static boolean useVanillaWeather(Level level, CloudTypeSource source) {
        if (!SimpleCloudsConfig.SERVER_SPEC.isLoaded()) {
            return false;
        }
        boolean flag = ((List)SimpleCloudsConfig.SERVER.dimensionWhitelist.get()).stream().anyMatch(val -> level.m_46472_().m_135782_().toString().equals(val));
        if ((Boolean)SimpleCloudsConfig.SERVER.whitelistAsBlacklist.get() != false ? flag : !flag) {
            return true;
        }
        CloudMode mode = (CloudMode)SimpleCloudsConfig.SERVER.cloudMode.get();
        switch (mode) {
            case AMBIENT: {
                return true;
            }
            case SINGLE: {
                CloudType type;
                String rawId = (String)SimpleCloudsConfig.SERVER.singleModeCloudType.get();
                ResourceLocation id = ResourceLocation.m_135820_((String)rawId);
                if (id == null || (type = source.getCloudTypeForId(id)) == null || type.weatherType() != WeatherType.NONE) break;
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[level=" + this.level.m_46472_().m_135782_() + "]";
    }
}

