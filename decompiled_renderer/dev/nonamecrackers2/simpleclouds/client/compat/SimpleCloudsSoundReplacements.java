/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.Sound$Type
 *  net.minecraft.client.resources.sounds.SoundEventRegistration
 *  net.minecraft.client.sounds.Weighted
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.sounds.SoundEvents
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.client.compat;

import com.google.common.collect.ImmutableMap;
import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleCloudsSoundReplacements {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/SimpleCloudsSoundReplacements");
    private static final Map<ResourceLocation, Replacements> REPLACEMENT_SOUNDS = ImmutableMap.of((Object)SoundEvents.f_12541_.m_11660_(), (Object)((Object)new Replacements(IntStream.range(1, 9).mapToObj(i -> "ambient/weather/rain" + i).toList(), SimpleCloudsCompatHelper::useCustomRainSounds)), (Object)SoundEvents.f_12542_.m_11660_(), (Object)((Object)new Replacements(IntStream.range(1, 5).mapToObj(i -> "ambient/weather/rain" + i).toList(), SimpleCloudsCompatHelper::useCustomRainSounds)));

    public static Weighted<Sound> applyReplacement(Weighted<Sound> currentSound, ResourceLocation soundLoc, SoundEventRegistration soundReg, Map<ResourceLocation, Resource> soundCache) {
        if (currentSound instanceof Sound) {
            Sound sound = (Sound)currentSound;
            if (!REPLACEMENT_SOUNDS.containsKey(soundLoc)) {
                return currentSound;
            }
            Replacements replacements = REPLACEMENT_SOUNDS.get(soundLoc);
            if (!replacements.condition().get().booleanValue() || !replacements.replacements().contains(sound.m_119787_().m_135815_())) {
                return currentSound;
            }
            String newLoc = "simpleclouds:" + sound.m_119787_().m_135815_();
            Sound newSound = new Sound(newLoc, sound.m_235146_(), sound.m_235147_(), sound.m_7789_(), Sound.Type.FILE, false, sound.m_119797_(), sound.m_119798_());
            if (!soundCache.containsKey(newSound.m_119790_())) {
                LOGGER.error("Could not find replacement sound '{}'", (Object)newLoc);
                return currentSound;
            }
            return newSound;
        }
        return currentSound;
    }

    private record Replacements(List<String> replacements, Supplier<Boolean> condition) {
    }
}

