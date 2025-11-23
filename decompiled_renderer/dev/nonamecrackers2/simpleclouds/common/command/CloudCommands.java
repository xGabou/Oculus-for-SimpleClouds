/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.TimeArgument
 *  net.minecraft.commands.arguments.coordinates.Vec2Argument
 */
package dev.nonamecrackers2.simpleclouds.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.command.CloudCommandSource;
import dev.nonamecrackers2.simpleclouds.common.command.argument.CloudTypeArgument;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;

public class CloudCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, String baseName, Predicate<CommandSourceStack> requirement, CloudCommandSource<?, ?> source, CloudTypeSource cloudTypeSource) {
        LiteralArgumentBuilder root = Commands.m_82127_((String)"simpleclouds");
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)baseName).requires(requirement)).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"clear").then(Commands.m_82127_((String)"all").executes(ctx -> source.clearClouds((CommandContext<CommandSourceStack>)ctx, CloudCommandSource.ALL)))).then(Commands.m_82127_((String)"storms").executes(ctx -> source.clearClouds((CommandContext<CommandSourceStack>)ctx, CloudCommandSource.storms(cloudTypeSource))))));
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)baseName).requires(requirement)).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"spawn").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.m_82129_((String)"type", (ArgumentType)CloudTypeArgument.type(cloudTypeSource)).then(Commands.m_82129_((String)"position", (ArgumentType)Vec2Argument.m_120822_()).then(Commands.m_82129_((String)"radius", (ArgumentType)FloatArgumentType.floatArg((float)0.0f)).then(Commands.m_82129_((String)"stretchFactor", (ArgumentType)FloatArgumentType.floatArg((float)0.01f)).then(Commands.m_82129_((String)"rotation", (ArgumentType)FloatArgumentType.floatArg()).then(Commands.m_82129_((String)"lifeTime", (ArgumentType)TimeArgument.m_264474_((int)0)).then(Commands.m_82129_((String)"growTime", (ArgumentType)TimeArgument.m_264474_((int)0)).then(Commands.m_82129_((String)"direction", (ArgumentType)Vec2Argument.m_174954_((boolean)false)).then(Commands.m_82129_((String)"maxSpeed", (ArgumentType)FloatArgumentType.floatArg((float)0.0f)).then(Commands.m_82129_((String)"accelerationFactor", (ArgumentType)FloatArgumentType.floatArg((float)0.0f)).executes(source::spawnCloud))))))))))).then(Commands.m_82127_((String)"extreme").executes(ctx -> source.spawnModifiedCloud((CommandContext<CommandSourceStack>)ctx, CloudCommandSource.EXTREME_CLOUD_INFO)))).then(Commands.m_82127_((String)"temperate").executes(ctx -> source.spawnModifiedCloud((CommandContext<CommandSourceStack>)ctx, CloudCommandSource.TEMPERATE_CLOUD_INFO)))).then(Commands.m_82127_((String)"random").executes(ctx -> source.spawnModifiedCloud((CommandContext<CommandSourceStack>)ctx, i -> i))))).then(Commands.m_82127_((String)"random").executes(source::spawnRandomCloud))));
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)baseName).requires(requirement)).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"get").then(Commands.m_82127_((String)"at").then(Commands.m_82129_((String)"position", (ArgumentType)Vec2Argument.m_120822_()).executes(source::getCloudTypeAt)))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"count").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"position", (ArgumentType)Vec2Argument.m_120822_()).then(Commands.m_82129_((String)"radius", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(ctx -> source.getCloudTypeCount((CommandContext<CommandSourceStack>)ctx, true, true)))).executes(ctx -> source.getCloudTypeCount((CommandContext<CommandSourceStack>)ctx, true, false)))).executes(ctx -> source.getCloudTypeCount((CommandContext<CommandSourceStack>)ctx, false, false)))));
        if (!SimpleCloudsAPI.getApi().getHooks().isExternalWeatherControlEnabled()) {
            root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)baseName).requires(requirement)).then(Commands.m_82127_((String)"refresh").executes(source::refreshClouds)));
        }
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)baseName).requires(requirement)).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"speed").then(Commands.m_82127_((String)"get").executes(source::getSpeed))).then(Commands.m_82127_((String)"set").then(Commands.m_82129_((String)"amount", (ArgumentType)FloatArgumentType.floatArg((float)0.0f)).executes(source::setSpeed)))));
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)baseName).requires(requirement)).then(Commands.m_82127_((String)"seed").then(Commands.m_82127_((String)"get").executes(source::getSeed))));
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)baseName).requires(requirement)).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"height").then(Commands.m_82127_((String)"get").executes(source::getCloudHeight))).then(Commands.m_82127_((String)"set").then(Commands.m_82129_((String)"height", (ArgumentType)IntegerArgumentType.integer((int)0, (int)2048)).executes(source::setCloudHeight)))));
        dispatcher.register(root);
    }
}

