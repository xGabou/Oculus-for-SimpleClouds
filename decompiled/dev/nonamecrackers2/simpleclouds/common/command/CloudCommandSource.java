/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.SpawnInfo
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.StaticSpawnInfo
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.arguments.ResourceLocationArgument
 *  net.minecraft.commands.arguments.coordinates.Vec2Argument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec2
 */
package dev.nonamecrackers2.simpleclouds.common.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.SpawnInfo;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.StaticSpawnInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudSpawningConfig;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.ServerCloudManager;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import dev.nonamecrackers2.simpleclouds.common.world.SyncType;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

public interface CloudCommandSource<S extends Level, T extends CloudManager<S>> {
    public static final CloudCommandSource<ServerLevel, ServerCloudManager> SERVER = new CloudCommandSource<ServerLevel, ServerCloudManager>(){

        @Override
        public Player getPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            return ((CommandSourceStack)context.getSource()).m_81375_();
        }

        @Override
        public ServerCloudManager getCloudManager(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            return (ServerCloudManager)CloudManager.get(((CommandSourceStack)context.getSource()).m_81372_());
        }

        @Override
        public void onValueUpdated(ServerCloudManager cloudManager, SyncType sync) {
            cloudManager.queueSync(sync);
        }
    };
    public static final Predicate<CloudRegion> ALL = r -> true;
    public static final Function<CloudSpawningConfig.Info, SpawnInfo> EXTREME_CLOUD_INFO = info -> new StaticSpawnInfo(info.cloudType(), info.speed().m_142734_(), info.radius().m_142737_(), info.existTicks().m_142737_(), info.growTicks().m_142737_(), info.stretchFactor().m_142735_(), info.movesToPlayer(), info.orderWeight());
    public static final Function<CloudSpawningConfig.Info, SpawnInfo> TEMPERATE_CLOUD_INFO = info -> new StaticSpawnInfo(info.cloudType(), info.speed().m_142735_(), info.radius().m_142739_(), info.existTicks().m_142739_(), info.growTicks().m_142739_(), info.stretchFactor().m_142734_(), info.movesToPlayer(), info.orderWeight());

    public static Predicate<CloudRegion> storms(CloudTypeSource source) {
        return r -> {
            CloudType type = source.getCloudTypeForId(r.getCloudTypeId());
            if (type != null) {
                return type.weatherType().causesDarkening();
            }
            return false;
        };
    }

    public T getCloudManager(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

    public Player getPlayer(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

    public void onValueUpdated(T var1, SyncType var2);

    default public int getScrollAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        Object manager = this.getCloudManager(context);
        source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.scroll.get", (Object[])new Object[]{Float.valueOf(manager.getScrollX()), Float.valueOf(manager.getScrollY()), Float.valueOf(manager.getScrollZ())}), false);
        return 0;
    }

    default public int getSpeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        Object manager = this.getCloudManager(context);
        source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.speed.get", (Object[])new Object[]{Float.valueOf(manager.getCloudSpeed())}), false);
        return 0;
    }

    default public int setSpeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        T manager = this.getCloudManager(context);
        float speed = FloatArgumentType.getFloat(context, (String)"amount");
        ((CloudManager)manager).setCloudSpeed(speed);
        this.onValueUpdated(manager, SyncType.MOVEMENT);
        return 0;
    }

    default public int getSeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        Object manager = this.getCloudManager(context);
        source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.seed.get", (Object[])new Object[]{ComponentUtils.m_258024_((String)String.valueOf(manager.getSeed()))}), true);
        return 0;
    }

    default public int getCloudHeight(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int height = ((CloudManager)this.getCloudManager(context)).getCloudHeight();
        source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.height.get", (Object[])new Object[]{height}), false);
        return height;
    }

    default public int setCloudHeight(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int height = IntegerArgumentType.getInteger(context, (String)"height");
        T manager = this.getCloudManager(context);
        ((CloudManager)manager).setCloudHeight(height);
        this.onValueUpdated(manager, SyncType.MOVEMENT);
        source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.height.set", (Object[])new Object[]{height}), true);
        return height;
    }

    default public int spawnCloud(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        float accelerationFactor;
        float maxSpeed;
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        T manager = this.getCloudManager(context);
        CloudGenerator generator = ((CloudManager)manager).getCloudGenerator();
        ResourceLocation id = ResourceLocationArgument.m_107011_(context, (String)"type");
        Vec2 pos = Vec2Argument.m_120825_(context, (String)"position");
        float radius = FloatArgumentType.getFloat(context, (String)"radius") / 8.0f;
        float stretchFactor = FloatArgumentType.getFloat(context, (String)"stretchFactor");
        float rotation = (float)Math.PI / 180 * (FloatArgumentType.getFloat(context, (String)"rotation") % 360.0f);
        int lifeTime = IntegerArgumentType.getInteger(context, (String)"lifeTime");
        int growTime = IntegerArgumentType.getInteger(context, (String)"growTime");
        Vec2 direction = Vec2Argument.m_120825_(context, (String)"direction");
        if (generator.addCloud(new CloudRegion(id, direction, maxSpeed = FloatArgumentType.getFloat(context, (String)"maxSpeed"), accelerationFactor = FloatArgumentType.getFloat(context, (String)"accelerationFactor"), pos.f_82470_ / 8.0f, pos.f_82471_ / 8.0f, radius, rotation, stretchFactor, lifeTime, growTime, Integer.MAX_VALUE), CloudGenerator.Order.TOP)) {
            source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.clouds.spawn", (Object[])new Object[]{id, Float.valueOf(pos.f_82470_), Float.valueOf(pos.f_82471_)}), true);
            return 1;
        }
        source.m_81352_((Component)Component.m_237115_((String)"command.simpleclouds.clouds.spawn.fail"));
        return 0;
    }

    default public int spawnRandomCloud(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        T manager = this.getCloudManager(context);
        CloudGenerator generator = ((CloudManager)manager).getCloudGenerator();
        CloudRegion region = generator.spawnCloud(generator.getSpawnConfig().get(), source.getUnsidedLevel()).orElse(null);
        if (region != null) {
            source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.clouds.spawn", (Object[])new Object[]{region.getCloudTypeId(), Float.valueOf(region.getWorldX()), Float.valueOf(region.getWorldZ())}), true);
            return 1;
        }
        source.m_81352_((Component)Component.m_237115_((String)"command.simpleclouds.clouds.spawn.fail"));
        return 0;
    }

    default public int spawnModifiedCloud(CommandContext<CommandSourceStack> context, Function<CloudSpawningConfig.Info, SpawnInfo> func) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        T manager = this.getCloudManager(context);
        CloudGenerator generator = ((CloudManager)manager).getCloudGenerator();
        ResourceLocation id = ResourceLocationArgument.m_107011_(context, (String)"type");
        CloudSpawningConfig config = generator.getSpawnConfig().get();
        CloudSpawningConfig.Info info = config.getWeightInfo(id);
        if (info == null) {
            source.m_81352_((Component)Component.m_237110_((String)"commands.simpleclouds.cloudType.notFound", (Object[])new Object[]{id.toString()}));
            return 0;
        }
        RandomSource random = RandomSource.m_216327_();
        CloudRegion region = generator.spawnCloud(() -> (SpawnInfo)func.apply(info), config.getSpawnInterval().m_214085_(random), config.getMaxRegions(), source.getUnsidedLevel()).orElse(null);
        if (region != null) {
            source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.clouds.spawn", (Object[])new Object[]{region.getCloudTypeId(), Float.valueOf(region.getWorldX()), Float.valueOf(region.getWorldZ())}), true);
            return 2;
        }
        source.m_81352_((Component)Component.m_237115_((String)"command.simpleclouds.clouds.spawn.fail"));
        return 1;
    }

    default public int getCloudTypeAt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        T manager = this.getCloudManager(context);
        CloudGenerator generator = ((CloudManager)manager).getCloudGenerator();
        Vec2 pos = Vec2Argument.m_120825_(context, (String)"position");
        CloudRegion region = generator.getCloudAtWorldPosition(pos.f_82470_, pos.f_82471_);
        if (region == null) {
            source.m_288197_(() -> Component.m_237115_((String)"command.simpleclouds.clouds.get.empty"), false);
            return 0;
        }
        CloudType type = ((CloudManager)manager).getCloudTypeForId(region.getCloudTypeId());
        if (type == null) {
            source.m_81352_((Component)Component.m_237110_((String)"commands.simpleclouds.cloudType.notFound", (Object[])new Object[]{region.getCloudTypeId().toString()}));
            return 0;
        }
        source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.clouds.get", (Object[])new Object[]{type.id().toString(), Float.valueOf(region.getWorldX()), Float.valueOf(region.getWorldZ()), type.weatherType().m_7912_()}), false);
        return type.weatherType().ordinal() + 1;
    }

    default public int getCloudTypeCount(CommandContext<CommandSourceStack> context, boolean inRegion, boolean withRadius) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        T manager = this.getCloudManager(context);
        CloudGenerator generator = ((CloudManager)manager).getCloudGenerator();
        ArrayList regions = Lists.newArrayList();
        if (inRegion) {
            Vec2 pos = Vec2Argument.m_120825_(context, (String)"position");
            int radius = SimpleCloudsConstants.SPAWN_RADIUS;
            if (withRadius) {
                radius = IntegerArgumentType.getInteger(context, (String)"radius");
            }
            SpawnRegion region = new SpawnRegion(Mth.m_14143_((float)pos.f_82471_) / 8, Mth.m_14143_((float)pos.f_82471_) / 8, radius);
            regions.addAll(generator.getCloudsInRegion(region));
        } else {
            regions.addAll(generator.getClouds());
        }
        int size = regions.size();
        String types = regions.stream().map(t -> t.getCloudTypeId().toString()).distinct().collect(Collectors.joining(", "));
        source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.clouds.count", (Object[])new Object[]{size, types}), false);
        return size;
    }

    default public int clearClouds(CommandContext<CommandSourceStack> context, Predicate<CloudRegion> region) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        T manager = this.getCloudManager(context);
        CloudGenerator generator = ((CloudManager)manager).getCloudGenerator();
        int amount = generator.removeCloudsCount(region);
        if (amount > 0) {
            source.m_288197_(() -> Component.m_237110_((String)"command.simpleclouds.clouds.clear", (Object[])new Object[]{amount}), true);
        } else {
            source.m_81352_((Component)Component.m_237115_((String)"command.simpleclouds.clouds.clear.fail"));
        }
        return amount;
    }

    default public int refreshClouds(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (SimpleCloudsAPI.getApi().getHooks().isExternalWeatherControlEnabled()) {
            return 0;
        }
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        T manager = this.getCloudManager(context);
        CloudGenerator generator = ((CloudManager)manager).getCloudGenerator();
        generator.removeAllClouds();
        for (SpawnRegion region : generator.getSpawnRegions()) {
            generator.doInitialGen(region.x(), region.z(), source.getUnsidedLevel(), true);
        }
        source.m_288197_(() -> Component.m_237115_((String)"command.simpleclouds.clouds.refresh"), true);
        return 1;
    }
}

