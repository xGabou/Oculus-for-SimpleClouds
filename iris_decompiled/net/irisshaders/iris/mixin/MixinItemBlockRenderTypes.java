/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.client.ChunkRenderTypeSet
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import java.util.Map;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemBlockRenderTypes.class})
public class MixinItemBlockRenderTypes {
    @Inject(method={"getRenderLayers"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void iris$setCustomRenderType(BlockState arg, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        ChunkRenderTypeSet type;
        Map<Holder.Reference<Block>, ChunkRenderTypeSet> idMap = WorldRenderingSettings.INSTANCE.getBlockTypeIds();
        if (idMap != null && (type = idMap.get(ForgeRegistries.BLOCKS.getDelegateOrThrow((Object)arg.m_60734_()))) != null) {
            cir.setReturnValue((Object)type);
        }
    }
}

