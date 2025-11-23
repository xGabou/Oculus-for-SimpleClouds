/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.mixin.fantastic;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;
import net.irisshaders.iris.fantastic.ParticleRenderingPhase;
import net.irisshaders.iris.fantastic.PhasedParticleEngine;
import net.irisshaders.iris.pipeline.programs.ShaderAccess;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ParticleEngine.class})
public class MixinParticleEngine
implements PhasedParticleEngine {
    private static final Set<ParticleRenderType> OPAQUE_PARTICLE_RENDER_TYPES = ImmutableSet.of((Object)ParticleRenderType.f_107430_, (Object)ParticleRenderType.f_107432_, (Object)ParticleRenderType.f_107433_, (Object)ParticleRenderType.f_107434_);
    @Shadow
    @Final
    private Map<ParticleRenderType, Queue<Particle>> f_107289_;
    @Unique
    private ParticleRenderingPhase phase = ParticleRenderingPhase.EVERYTHING;

    @Redirect(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), remap=false)
    private void iris$changeParticleShader(Supplier<ShaderInstance> pSupplier0) {
        RenderSystem.setShader(this.phase == ParticleRenderingPhase.TRANSLUCENT ? ShaderAccess::getParticleTranslucentShader : pSupplier0);
    }

    @Redirect(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V"}, at=@At(value="INVOKE", target="Ljava/util/Map;keySet()Ljava/util/Set;"), remap=false)
    private Set<ParticleRenderType> iris$selectParticlesToRender(Map<ParticleRenderType, Queue<Particle>> instance) {
        Set<ParticleRenderType> keySet = instance.keySet();
        if (this.phase == ParticleRenderingPhase.TRANSLUCENT) {
            return Sets.filter(keySet, type -> !OPAQUE_PARTICLE_RENDER_TYPES.contains(type));
        }
        if (this.phase == ParticleRenderingPhase.OPAQUE) {
            return Sets.filter(keySet, type -> !type.equals(ParticleRenderType.f_107431_));
        }
        return keySet;
    }

    @Override
    public void setParticleRenderingPhase(ParticleRenderingPhase phase) {
        this.phase = phase;
    }
}

