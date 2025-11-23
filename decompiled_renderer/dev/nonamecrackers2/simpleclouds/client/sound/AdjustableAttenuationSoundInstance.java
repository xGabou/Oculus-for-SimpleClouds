/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.client.sounds.WeighedSoundEvents
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 */
package dev.nonamecrackers2.simpleclouds.client.sound;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class AdjustableAttenuationSoundInstance
extends SimpleSoundInstance {
    private final int attenuationDistance;

    public AdjustableAttenuationSoundInstance(SoundEvent sound, SoundSource source, float volume, float pitch, RandomSource random, double x, double y, double z, int attenuationDistance) {
        super(sound, source, volume, pitch, random, x, y, z);
        this.attenuationDistance = attenuationDistance;
    }

    public WeighedSoundEvents m_6775_(SoundManager manager) {
        WeighedSoundEvents events = super.m_6775_(manager);
        this.f_119570_ = AdjustableAttenuationSoundInstance.wrap(this.f_119570_, this.attenuationDistance);
        return events;
    }

    public SoundInstance.Attenuation m_7438_() {
        return SoundInstance.Attenuation.LINEAR;
    }

    private static Sound wrap(Sound sound, int attenuationDistance) {
        return new Sound(sound.m_119787_().toString(), sound.m_235146_(), sound.m_235147_(), sound.m_7789_(), sound.m_119795_(), sound.m_119796_(), sound.m_119797_(), attenuationDistance);
    }
}

