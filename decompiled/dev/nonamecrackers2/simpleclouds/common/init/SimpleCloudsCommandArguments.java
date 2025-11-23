/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfos
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.nonamecrackers2.simpleclouds.common.init;

import dev.nonamecrackers2.simpleclouds.common.command.argument.CloudTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class SimpleCloudsCommandArguments {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.COMMAND_ARGUMENT_TYPES, (String)"simpleclouds");
    public static final RegistryObject<CloudTypeArgument.Info> CLOUD_TYPE = ARGUMENT_TYPES.register("cloud_type", () -> (CloudTypeArgument.Info)ArgumentTypeInfos.registerByClass(CloudTypeArgument.class, (ArgumentTypeInfo)new CloudTypeArgument.Info()));

    public static void register(IEventBus modBus) {
        ARGUMENT_TYPES.register(modBus);
    }
}

