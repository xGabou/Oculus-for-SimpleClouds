/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.impl.locale.XCldrStub$ImmutableMap
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.TimeArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  nonamecrackers2.crackerslib.client.gui.Popup
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.core.util.ObjectArrayIterator
 */
package dev.nonamecrackers2.simpleclouds.client.command.profiling;

import com.ibm.icu.impl.locale.XCldrStub;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.cloud.spawning.ClientSideCloudSpawningManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.profiling.ProfilingCloudGenerator;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import nonamecrackers2.crackerslib.client.gui.Popup;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.core.util.ObjectArrayIterator;

public class ProfilingCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)Commands.m_82127_((String)"simpleclouds").then(((LiteralArgumentBuilder)Commands.m_82127_((String)"profiling").requires(stack -> true)).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"generator").then(Commands.m_82129_((String)"time", (ArgumentType)TimeArgument.m_264474_((int)1)).executes(ctx -> ProfilingCommands.runGeneratorProfiler((CommandContext<CommandSourceStack>)ctx, IntegerArgumentType.getInteger((CommandContext)ctx, (String)"time"))))).executes(ctx -> ProfilingCommands.runGeneratorProfiler((CommandContext<CommandSourceStack>)ctx, 1728000)))));
    }

    private static int runGeneratorProfiler(CommandContext<CommandSourceStack> ctx, int iterations) throws CommandSyntaxException {
        Popup.createYesNoPopup(null, () -> {
            Popup primary = Popup.createInfoPopup(null, (int)300, (Component)Component.m_237113_((String)"Running profiler..."));
            Minecraft mc = Minecraft.m_91087_();
            ClientSideCloudTypeManager manager = ClientSideCloudTypeManager.getInstance();
            CloudType[] cloudTypes = manager.getIndexedCloudTypes();
            final CloudType[] cachedTypes = Arrays.copyOf(cloudTypes, cloudTypes.length);
            final Map cachedCloudMap = XCldrStub.ImmutableMap.copyOf(manager.getCloudTypes());
            CloudTypeSource wrapper = new CloudTypeSource(){

                @Override
                public CloudType[] getIndexedCloudTypes() {
                    return cachedTypes;
                }

                @Override
                public CloudType getCloudTypeForId(ResourceLocation id) {
                    return (CloudType)cachedCloudMap.get(id);
                }
            };
            ProfilingCloudGenerator.profile(ClientSideCloudSpawningManager.getClientInstance().getConfig(), wrapper, iterations).exceptionallyAsync(e -> {
                ProfilingCloudGenerator.LOGGER.error("Failed to run profiler", e);
                primary.m_7379_();
                Popup.createInfoPopup(null, (int)200, (Component)Component.m_237113_((String)("Profiler failed. Please see log for details.\n\n" + e.getMessage())));
                return null;
            }, (Executor)mc).thenAcceptAsync(results -> {
                if (results != null) {
                    primary.m_7379_();
                    try {
                        ProfilingCommands.acceptResults(results);
                    }
                    catch (Exception e) {
                        Popup.createInfoPopup(null, (int)200, (Component)Component.m_237113_((String)("An unknown error occured. See log for more details.\n\n" + e.getMessage())));
                        ProfilingCloudGenerator.LOGGER.error("Error when handling results", (Throwable)e);
                    }
                }
            }, (Executor)mc);
        }, (int)200, (Component)Component.m_237113_((String)"You are about to run the cloud generator profiler. This may take a moment. Do you wish to continue?"));
        return 0;
    }

    private static void acceptResults(ProfilingCloudGenerator.Results results) {
        MutableComponent mainMessage = Component.m_237113_((String)"Profiler completed. Below is a list of cloud types that spawned. Select a cloud type to see its individual stats.");
        int tickCountElapsed = results.getTotalTicksElapsed();
        mainMessage.m_130946_("\n\n");
        mainMessage.m_7220_((Component)Component.m_237113_((String)("Total time elapsed: " + ProfilingCommands.humanReadableTicks(tickCountElapsed) + " (" + tickCountElapsed + " ticks)")));
        mainMessage.m_130946_("\n");
        mainMessage.m_7220_((Component)Component.m_237113_((String)("Total clouds spawned: " + results.getTotalCloudTypesGenerated())));
        int averageSpawnTime = Math.round(results.getAverageSpawnTime());
        mainMessage.m_130946_("\n");
        mainMessage.m_7220_((Component)Component.m_237113_((String)("Average spawn time: " + ProfilingCommands.humanReadableTicks(averageSpawnTime) + " (" + averageSpawnTime + " ticks)")));
        int averageRainSpawnTime = Math.round(results.getAverageRainSpawnTime());
        mainMessage.m_130946_("\n");
        mainMessage.m_7220_((Component)Component.m_237113_((String)("Average rain spawn time: " + ProfilingCommands.humanReadableTicks(averageRainSpawnTime) + " (" + averageRainSpawnTime + " ticks)")));
        int averageThunderstormSpawnTime = Math.round(results.getAverageThunderstormSpawnTime());
        mainMessage.m_130946_("\n");
        mainMessage.m_7220_((Component)Component.m_237113_((String)("Average thunderstorm spawn time: " + ProfilingCommands.humanReadableTicks(averageThunderstormSpawnTime) + " (" + averageThunderstormSpawnTime + " ticks)")));
        mainMessage.m_130946_("\n");
        mainMessage.m_7220_(ProfilingCommands.createMinMaxInfo("Clouds existing at once", results.getCurrentCloudCountStats()));
        MutableObject main = new MutableObject();
        Map<ResourceLocation, ProfilingCloudGenerator.CloudStats> individualStats = results.getIndividualStats();
        Consumer<ResourceLocation> valueAcceptor = id -> Popup.createInfoPopup((Screen)((Screen)main.getValue()), (int)300, (Component)ProfilingCommands.createIndividualResults(id, (ProfilingCloudGenerator.CloudStats)individualStats.get(id))).alignLeft();
        main.setValue((Object)Popup.createOptionListPopup(null, builder -> {
            for (ResourceLocation id : individualStats.keySet()) {
                builder.addObject((Component)Component.m_237113_((String)id.toString()), (Object)id);
            }
        }, valueAcceptor, (int)300, (int)100, (Component)mainMessage).alignLeft());
    }

    private static Component createIndividualResults(ResourceLocation id, ProfilingCloudGenerator.CloudStats stats) {
        MutableComponent message = Component.m_237113_((String)id.toString());
        message.m_130946_("\n\nTotal spawned: " + stats.getTotalSpawned());
        int averageSpawnTicks = Math.round(stats.getAverageTicksToSpawn());
        message.m_130946_("\n\nAverage ticks to spawn: " + ProfilingCommands.humanReadableTicks(averageSpawnTicks) + " (" + averageSpawnTicks + " ticks)");
        message.m_130946_("\n");
        message.m_7220_(ProfilingCommands.createMinMaxTimeInfo("Time over player", stats.getTimeOverPlayer()));
        message.m_130946_("\n");
        message.m_7220_(ProfilingCommands.createMinMaxInfo("Speed", stats.getSpeedStats()));
        message.m_130946_("\n");
        message.m_7220_(ProfilingCommands.createMinMaxInfo("Radius", stats.getRadiusStats()));
        message.m_130946_("\n");
        message.m_7220_(ProfilingCommands.createMinMaxInfo("Stretch factor", stats.getStretchFactorStats()));
        message.m_130946_("\n");
        message.m_7220_(ProfilingCommands.createMinMaxTimeInfo("Exist time", stats.getExistTicks()));
        message.m_130946_("\n");
        message.m_7220_(ProfilingCommands.createMinMaxTimeInfo("Grow time", stats.getGrowTicks()));
        return message.m_130940_(ChatFormatting.YELLOW);
    }

    private static Component createMinMaxTimeInfo(String title, ProfilingCloudGenerator.MinMax minMax) {
        String str = String.format("%s; min: %s, max: %s, avg: %s", title, ProfilingCommands.humanReadableTicks(minMax.getMin()), ProfilingCommands.humanReadableTicks(minMax.getMax()), ProfilingCommands.humanReadableTicks(minMax.getAvg()));
        return Component.m_237113_((String)str);
    }

    private static Component createMinMaxInfo(String title, ProfilingCloudGenerator.MinMax minMax) {
        String str = String.format("%s; min: %.2f, max: %.2f, avg: %.3f", title, Float.valueOf(minMax.getMin()), Float.valueOf(minMax.getMax()), Float.valueOf(minMax.getAvg()));
        return Component.m_237113_((String)str);
    }

    private static String humanReadableTicks(float ticks) {
        char prevUnit;
        ObjectArrayIterator units = new ObjectArrayIterator((Object[])new Pair[]{Pair.of((Object)Character.valueOf('s'), (Object)Float.valueOf(20.0f)), Pair.of((Object)Character.valueOf('m'), (Object)Float.valueOf(60.0f)), Pair.of((Object)Character.valueOf('h'), (Object)Float.valueOf(60.0f)), Pair.of((Object)Character.valueOf('d'), (Object)Float.valueOf(24.0f))});
        Pair current = Pair.of((Object)Character.valueOf('t'), (Object)Float.valueOf(1.0f));
        do {
            prevUnit = ((Character)current.getLeft()).charValue();
        } while (units.hasNext() && ((ticks /= ((Float)current.getRight()).floatValue()) <= -((Float)(current = (Pair)units.next()).getRight()).floatValue() || ticks >= ((Float)current.getRight()).floatValue()));
        return String.format("%.1f%c", Float.valueOf(ticks), Character.valueOf(prevUnit));
    }
}

