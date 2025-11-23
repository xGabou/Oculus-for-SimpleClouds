/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.Holder
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.level.storage.WritableLevelData
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManagerHolder;
import java.awt.Color;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientLevel.class})
public abstract class MixinClientLevel
extends Level
implements CloudManagerHolder<ClientLevel> {
    @Unique
    private ClientCloudManager cloudManager;

    protected MixinClientLevel(WritableLevelData data, ResourceKey<Level> dimension, RegistryAccess registry, Holder<DimensionType> dimensionType, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long seed, int maxChainedNeighbourUpdates) {
        super(data, dimension, registry, dimensionType, profiler, isClientSide, isDebug, seed, maxChainedNeighbourUpdates);
        throw new UnsupportedOperationException();
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    public void simpleclouds$createCloudManager_init(CallbackInfo ci) {
        this.cloudManager = new ClientCloudManager((ClientLevel)this);
        this.cloudManager.init((Boolean)SimpleCloudsConfig.CLIENT.useSpecificSeed.get() != false ? ((Long)SimpleCloudsConfig.CLIENT.cloudSeed.get()).longValue() : RandomSource.m_216327_().m_188505_());
    }

    @Inject(method={"getSkyDarken"}, at={@At(value="RETURN")}, cancellable=true)
    public void simpleclouds$modifySkyDarken_getSkyDarken(float partialTick, CallbackInfoReturnable<Float> ci) {
        ci.setReturnValue((Object)Float.valueOf(((Float)ci.getReturnValue()).floatValue() * SimpleCloudsRenderer.getInstance().getWorldEffectsManager().getDarkenFactor(partialTick)));
    }

    public ClientCloudManager getCloudManager() {
        return this.cloudManager;
    }

    @Inject(method={"getSkyColor"}, at={@At(value="RETURN")}, cancellable=true)
    public void simpleclouds$modifySkyColor_getSkyColor(Vec3 col, float partialTick, CallbackInfoReturnable<Vec3> ci) {
        Vec3 defaultCol = (Vec3)ci.getReturnValue();
        Color finalCol = SimpleCloudsRenderer.getInstance().getWorldEffectsManager().calculateSkyColor((float)defaultCol.f_82479_, (float)defaultCol.f_82480_, (float)defaultCol.f_82481_, partialTick);
        ci.setReturnValue((Object)new Vec3((double)((float)finalCol.getRed() / 255.0f), (double)((float)finalCol.getGreen() / 255.0f), (double)((float)finalCol.getBlue() / 255.0f)));
    }
}

