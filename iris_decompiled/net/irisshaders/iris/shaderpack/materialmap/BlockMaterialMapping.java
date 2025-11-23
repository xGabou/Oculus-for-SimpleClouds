/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraftforge.client.ChunkRenderTypeSet
 *  net.minecraftforge.registries.ForgeRegistries
 */
package net.irisshaders.iris.shaderpack.materialmap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.BlockRenderType;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockMaterialMapping {
    public static Object2IntMap<BlockState> createBlockStateIdMap(Int2ObjectMap<List<BlockEntry>> blockPropertiesMap) {
        Object2IntOpenHashMap blockStateIds = new Object2IntOpenHashMap();
        blockPropertiesMap.forEach((arg_0, arg_1) -> BlockMaterialMapping.lambda$createBlockStateIdMap$0((Object2IntMap)blockStateIds, arg_0, arg_1));
        return blockStateIds;
    }

    public static Map<Holder.Reference<Block>, ChunkRenderTypeSet> createBlockTypeMap(Map<NamespacedId, BlockRenderType> blockPropertiesMap) {
        Object2ObjectOpenHashMap blockTypeIds = new Object2ObjectOpenHashMap();
        blockPropertiesMap.forEach((arg_0, arg_1) -> BlockMaterialMapping.lambda$createBlockTypeMap$2((Map)blockTypeIds, arg_0, arg_1));
        return blockTypeIds;
    }

    private static RenderType convertBlockToRenderType(BlockRenderType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            default -> throw new IncompatibleClassChangeError();
            case BlockRenderType.SOLID -> RenderType.m_110451_();
            case BlockRenderType.CUTOUT -> RenderType.m_110463_();
            case BlockRenderType.CUTOUT_MIPPED -> RenderType.m_110457_();
            case BlockRenderType.TRANSLUCENT -> RenderType.m_110466_();
        };
    }

    private static void addBlockStates(BlockEntry entry, Object2IntMap<BlockState> idMap, int intId) {
        ResourceLocation resourceLocation;
        NamespacedId id = entry.id();
        try {
            resourceLocation = new ResourceLocation(id.getNamespace(), id.getName());
        }
        catch (Exception exception) {
            throw new IllegalStateException("Failed to get entry for " + intId, exception);
        }
        Optional delegateOpt = ForgeRegistries.BLOCKS.getDelegate(resourceLocation);
        if (delegateOpt.isEmpty() || !((Holder.Reference)delegateOpt.get()).m_203633_()) {
            return;
        }
        Block block = (Block)((Holder.Reference)delegateOpt.get()).get();
        Map<String, String> propertyPredicates = entry.propertyPredicates();
        if (propertyPredicates.isEmpty()) {
            for (BlockState state : block.m_49965_().m_61056_()) {
                idMap.putIfAbsent((Object)state, intId);
            }
            return;
        }
        HashMap properties = new HashMap();
        StateDefinition stateManager = block.m_49965_();
        propertyPredicates.forEach((key, value) -> {
            Property property = stateManager.m_61081_(key);
            if (property == null) {
                Iris.logger.warn("Error while parsing the block ID map entry for \"block." + intId + "\":");
                Iris.logger.warn("- The block " + resourceLocation + " has no property with the name " + key + ", ignoring!");
                return;
            }
            properties.put((Property<?>)property, (String)value);
        });
        for (BlockState state : stateManager.m_61056_()) {
            if (!BlockMaterialMapping.checkState(state, properties)) continue;
            idMap.putIfAbsent((Object)state, intId);
        }
    }

    private static boolean checkState(BlockState state, Map<Property<?>, String> expectedValues) {
        for (Map.Entry<Property<?>, String> condition : expectedValues.entrySet()) {
            String actualValue;
            Property<?> property = condition.getKey();
            String expectedValue = condition.getValue();
            if (expectedValue.equals(actualValue = property.m_6940_(state.m_61143_(property)))) continue;
            return false;
        }
        return true;
    }

    private static /* synthetic */ void lambda$createBlockTypeMap$2(Map blockTypeIds, NamespacedId id, BlockRenderType blockType) {
        ResourceLocation resourceLocation = new ResourceLocation(id.getNamespace(), id.getName());
        ForgeRegistries.BLOCKS.getDelegate(resourceLocation).ifPresent(block -> blockTypeIds.put(block, ChunkRenderTypeSet.of((RenderType[])new RenderType[]{BlockMaterialMapping.convertBlockToRenderType(blockType)})));
    }

    private static /* synthetic */ void lambda$createBlockStateIdMap$0(Object2IntMap blockStateIds, Integer intId, List entries) {
        for (BlockEntry entry : entries) {
            BlockMaterialMapping.addBlockStates(entry, (Object2IntMap<BlockState>)blockStateIds, intId);
        }
    }
}

