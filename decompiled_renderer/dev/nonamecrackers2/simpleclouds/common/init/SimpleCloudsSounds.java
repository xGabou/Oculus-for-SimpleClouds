/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.nonamecrackers2.simpleclouds.common.init;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class SimpleCloudsSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.SOUND_EVENTS, (String)"simpleclouds");
    public static final RegistryObject<SoundEvent> DISTANT_THUNDER = SimpleCloudsSounds.createSoundEvent("distant_thunder");
    public static final RegistryObject<SoundEvent> CLOSE_THUNDER = SimpleCloudsSounds.createSoundEvent("close_thunder");

    private static RegistryObject<SoundEvent> createSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.m_262824_((ResourceLocation)SimpleCloudsMod.id(name)));
    }

    public static void register(IEventBus modBus) {
        SOUND_EVENTS.register(modBus);
    }
}

