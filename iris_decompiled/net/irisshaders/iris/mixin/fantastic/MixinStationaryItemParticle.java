/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.BlockMarker
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.client.ChunkRenderTypeSet
 *  net.minecraftforge.client.model.data.ModelData
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.fantastic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BlockMarker;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockMarker.class})
public class MixinStationaryItemParticle {
    @Unique
    private boolean isOpaque;

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void iris$resolveTranslucency(ClientLevel clientLevel, double d, double e, double f, BlockState blockState, CallbackInfo ci) {
        BakedModel model = Minecraft.m_91087_().m_91289_().m_110910_(blockState);
        ChunkRenderTypeSet types = model.getRenderTypes(blockState, clientLevel.f_46441_, ModelData.EMPTY);
        if (types.contains(RenderType.m_110451_()) || types.contains(RenderType.m_110463_()) || types.contains(RenderType.m_110457_())) {
            this.isOpaque = true;
        }
    }

    @Inject(method={"getRenderType"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$overrideParticleRenderType(CallbackInfoReturnable<ParticleRenderType> cir) {
        if (this.isOpaque) {
            cir.setReturnValue((Object)ParticleRenderType.f_107429_);
        }
    }
}

