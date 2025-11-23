/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SimpleAnimatedParticle
 *  net.minecraft.client.particle.SpriteSet
 *  org.spongepowered.asm.mixin.Mixin
 */
package net.irisshaders.iris.mixin.fantastic;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets={"net.minecraft.client.particle.FireworkParticles$SparkParticle"})
public class MixinFireworkSparkParticle
extends SimpleAnimatedParticle {
    private MixinFireworkSparkParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteProvider, float upwardsAcceleration) {
        super(level, x, y, z, spriteProvider, upwardsAcceleration);
    }

    public ParticleRenderType m_7556_() {
        return ParticleRenderType.f_107430_;
    }
}

