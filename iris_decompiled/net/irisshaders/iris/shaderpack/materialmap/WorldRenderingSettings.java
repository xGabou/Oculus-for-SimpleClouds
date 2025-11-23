/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntFunction
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.client.ChunkRenderTypeSet
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.materialmap;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import org.jetbrains.annotations.Nullable;

public class WorldRenderingSettings {
    public static final WorldRenderingSettings INSTANCE = new WorldRenderingSettings();
    private boolean reloadRequired = false;
    private Object2IntMap<BlockState> blockStateIds = null;
    private Map<Holder.Reference<Block>, ChunkRenderTypeSet> blockTypeIds = null;
    private Object2IntFunction<NamespacedId> entityIds;
    private Object2IntFunction<NamespacedId> itemIds;
    private float ambientOcclusionLevel = 1.0f;
    private boolean disableDirectionalShading = false;
    private boolean hasVillagerConversionId = false;
    private boolean useSeparateAo = false;
    private boolean useExtendedVertexFormat = false;
    private boolean separateEntityDraws = false;
    private boolean voxelizeLightBlocks = false;

    public boolean isReloadRequired() {
        return this.reloadRequired;
    }

    public void clearReloadRequired() {
        this.reloadRequired = false;
    }

    @Nullable
    public Object2IntMap<BlockState> getBlockStateIds() {
        return this.blockStateIds;
    }

    public void setBlockStateIds(Object2IntMap<BlockState> blockStateIds) {
        if (this.blockStateIds != null && this.blockStateIds.equals(blockStateIds)) {
            return;
        }
        this.reloadRequired = true;
        this.blockStateIds = blockStateIds;
    }

    @Nullable
    public Map<Holder.Reference<Block>, ChunkRenderTypeSet> getBlockTypeIds() {
        return this.blockTypeIds;
    }

    public void setBlockTypeIds(Map<Holder.Reference<Block>, ChunkRenderTypeSet> blockTypeIds) {
        if (this.blockTypeIds != null && this.blockTypeIds.equals(blockTypeIds)) {
            return;
        }
        this.reloadRequired = true;
        this.blockTypeIds = blockTypeIds;
    }

    @Nullable
    public Object2IntFunction<NamespacedId> getEntityIds() {
        return this.entityIds;
    }

    public void setEntityIds(Object2IntFunction<NamespacedId> entityIds) {
        this.entityIds = entityIds;
        this.hasVillagerConversionId = entityIds.containsKey((Object)new NamespacedId("minecraft", "zombie_villager_converting"));
    }

    @Nullable
    public Object2IntFunction<NamespacedId> getItemIds() {
        return this.itemIds;
    }

    public void setItemIds(Object2IntFunction<NamespacedId> itemIds) {
        this.itemIds = itemIds;
    }

    public float getAmbientOcclusionLevel() {
        return this.ambientOcclusionLevel;
    }

    public void setAmbientOcclusionLevel(float ambientOcclusionLevel) {
        if (ambientOcclusionLevel == this.ambientOcclusionLevel) {
            return;
        }
        this.reloadRequired = true;
        this.ambientOcclusionLevel = ambientOcclusionLevel;
    }

    public boolean shouldDisableDirectionalShading() {
        return this.disableDirectionalShading;
    }

    public void setDisableDirectionalShading(boolean disableDirectionalShading) {
        if (disableDirectionalShading == this.disableDirectionalShading) {
            return;
        }
        this.reloadRequired = true;
        this.disableDirectionalShading = disableDirectionalShading;
    }

    public boolean shouldUseSeparateAo() {
        return this.useSeparateAo;
    }

    public void setUseSeparateAo(boolean useSeparateAo) {
        if (useSeparateAo == this.useSeparateAo) {
            return;
        }
        this.reloadRequired = true;
        this.useSeparateAo = useSeparateAo;
    }

    public boolean shouldUseExtendedVertexFormat() {
        return this.useExtendedVertexFormat;
    }

    public void setUseExtendedVertexFormat(boolean useExtendedVertexFormat) {
        if (useExtendedVertexFormat == this.useExtendedVertexFormat) {
            return;
        }
        this.reloadRequired = true;
        this.useExtendedVertexFormat = useExtendedVertexFormat;
    }

    public boolean shouldVoxelizeLightBlocks() {
        return this.voxelizeLightBlocks;
    }

    public void setVoxelizeLightBlocks(boolean voxelizeLightBlocks) {
        if (voxelizeLightBlocks == this.voxelizeLightBlocks) {
            return;
        }
        this.reloadRequired = true;
        this.voxelizeLightBlocks = voxelizeLightBlocks;
    }

    public boolean shouldSeparateEntityDraws() {
        return this.separateEntityDraws;
    }

    public void setSeparateEntityDraws(boolean separateEntityDraws) {
        this.separateEntityDraws = separateEntityDraws;
    }

    public boolean hasVillagerConversionId() {
        return this.hasVillagerConversionId;
    }
}

