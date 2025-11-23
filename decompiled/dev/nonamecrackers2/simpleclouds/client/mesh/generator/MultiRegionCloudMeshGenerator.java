/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.RenderSystem
 *  javax.annotation.Nullable
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.joml.Matrix2f
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL12
 *  org.lwjgl.opengl.GL41
 *  org.lwjgl.opengl.GL42
 */
package dev.nonamecrackers2.simpleclouds.client.mesh.generator;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.LevelOfDetail;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.LevelOfDetailConfig;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.PreparedChunk;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.BindingManager;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.ShaderStorageBufferObject;
import dev.nonamecrackers2.simpleclouds.client.shader.compute.ComputeShader;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudGetter;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;

public final class MultiRegionCloudMeshGenerator
extends CloudMeshGenerator {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/MultiRegionCloudMeshGenerator");
    private static final ResourceLocation REGION_GENERATOR_LOC = SimpleCloudsMod.id("cloud_regions");
    private static final String LOD_SCALES_NAME = "LodScales";
    private static final String CLOUD_REGIONS_NAME = "CloudRegions";
    public static final int MAX_CLOUD_TYPES = 64;
    public static final int MAX_CLOUD_FORMATIONS = 10;
    private static final int BYTES_PER_REGION = 32;
    private int requiredRegionTexSize;
    private CloudGetter cloudGetter = CloudGetter.EMPTY;
    private CloudInfo[] cachedTypes = new CloudInfo[0];
    @Nullable
    private ComputeShader regionTextureGenerator;
    private int cloudRegionTextureId = -1;
    private int cloudRegionImageBinding = -1;
    private boolean updateCloudTypes;
    private int currentCloudFormationCount;

    protected MultiRegionCloudMeshGenerator(boolean fadeNearOrigin, boolean shadedClouds, LevelOfDetailConfig lodConfig, Supplier<Integer> meshGenIntervalCalculator, boolean useTransparency, boolean fixedMeshDataSectionSize) {
        super(CloudMeshGenerator.MAIN_CUBE_MESH_GENERATOR, 0, fadeNearOrigin, shadedClouds, lodConfig, meshGenIntervalCalculator, useTransparency, fixedMeshDataSectionSize);
    }

    public void setCloudGetter(CloudGetter getter) {
        this.cloudGetter = Objects.requireNonNull(getter, "Cloud getter cannot be null");
        this.updateCloudTypes();
    }

    public int getCloudRegionTextureId() {
        return this.cloudRegionTextureId;
    }

    public void updateCloudTypes() {
        this.updateCloudTypes = true;
    }

    public int getTotalCloudTypes() {
        return this.cachedTypes.length;
    }

    public int getCloudFormationCount() {
        return this.currentCloudFormationCount;
    }

    @Override
    protected void setupShader() {
        super.setupShader();
        this.cachedTypes = new CloudInfo[0];
        this.updateCloudTypes = false;
        this.shader.createAndBindSSBO("NoiseLayers", 35044).allocateBuffer(AbstractNoiseSettings.Param.values().length * 4 * 4 * 64);
        this.shader.createAndBindSSBO("LayerGroupings", 35044).allocateBuffer(1536);
        this.uploadCloudTypeData();
    }

    @Override
    protected void initExtra(ResourceManager manager) throws IOException {
        this.currentCloudFormationCount = 0;
        this.requiredRegionTexSize = 0;
        if (this.regionTextureGenerator != null) {
            this.regionTextureGenerator.close();
        }
        ImmutableMap params = ImmutableMap.of((Object)"EDGE_FADE_FACTOR", (Object)String.valueOf(0.005f));
        this.regionTextureGenerator = ComputeShader.loadShader(REGION_GENERATOR_LOC, (ResourceProvider)manager, 16, 16, this.lodConfig.getLods().length + 1, (ImmutableMap<String, String>)params);
        ShaderStorageBufferObject lodScales = this.regionTextureGenerator.createAndBindSSBO(LOD_SCALES_NAME, 35045);
        int lodScalesSize = this.lodConfig.getLods().length * 4 + 4;
        lodScales.allocateBuffer(lodScalesSize);
        lodScales.writeData(b -> {
            b.putFloat(1.0f);
            for (LevelOfDetail l : this.lodConfig.getLods()) {
                b.putFloat(l.chunkScale());
            }
            b.rewind();
        }, lodScalesSize, false);
        this.regionTextureGenerator.createAndBindSSBO(CLOUD_REGIONS_NAME, 35045).allocateBuffer(320);
        int prevSpan = this.lodConfig.getPrimaryChunkSpan();
        int prevScale = 1;
        int largestSpan = prevSpan;
        for (LevelOfDetail config : this.lodConfig.getLods()) {
            int scale = config.chunkScale();
            int div = scale / prevScale;
            prevScale = scale;
            if ((prevSpan = prevSpan / div + config.spread() * 2) <= largestSpan) continue;
            largestSpan = prevSpan;
        }
        this.requiredRegionTexSize = largestSpan * 32;
        if (this.cloudRegionTextureId != -1) {
            TextureUtil.releaseTextureId((int)this.cloudRegionTextureId);
            this.cloudRegionTextureId = -1;
        }
        this.cloudRegionTextureId = TextureUtil.generateTextureId();
        GL11.glBindTexture((int)35866, (int)this.cloudRegionTextureId);
        GL11.glTexParameteri((int)35866, (int)10242, (int)33071);
        GL11.glTexParameteri((int)35866, (int)10243, (int)33071);
        GL11.glTexParameteri((int)35866, (int)10241, (int)9729);
        GL11.glTexParameteri((int)35866, (int)10240, (int)9729);
        GL12.glTexImage3D((int)35866, (int)0, (int)33328, (int)this.requiredRegionTexSize, (int)this.requiredRegionTexSize, (int)(this.lodConfig.getLods().length + 1), (int)0, (int)33319, (int)5126, (IntBuffer)null);
        GL11.glBindTexture((int)35866, (int)0);
        if (this.cloudRegionImageBinding != -1) {
            BindingManager.freeImageUnit(this.cloudRegionImageBinding);
        }
        this.cloudRegionImageBinding = BindingManager.getAvailableImageUnit();
        BindingManager.useImageUnit(this.cloudRegionImageBinding);
        GL42.glBindImageTexture((int)this.cloudRegionImageBinding, (int)this.cloudRegionTextureId, (int)0, (boolean)true, (int)0, (int)35001, (int)33328);
        this.regionTextureGenerator.setImageUnit("regionTexture", this.cloudRegionImageBinding);
        this.runRegionGenerator(0.0f, 0.0f, 1.0f);
        this.shader.setSampler2DArray("RegionsSampler", this.cloudRegionTextureId, 0);
        this.shader.forUniform("RegionsTexSize", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)this.requiredRegionTexSize));
        LOGGER.debug("Created cloud region texture generator with size {}x{}x{}", (Object)this.requiredRegionTexSize, (Object)this.requiredRegionTexSize, (Object)(this.lodConfig.getLods().length + 1));
    }

    @Override
    protected CloudMeshGenerator.ChunkGenSettings determineChunkGenSettings(float minX, float minZ, float maxX, float maxZ) {
        float[][] positions = new float[][]{{minX, minZ}, {minX, maxZ}, {maxX, minZ}, {maxX, maxZ}};
        int smallestStartHeight = 0;
        int largestEndHeight = 0;
        boolean empty = true;
        for (int i = 0; i < positions.length; ++i) {
            float[] pos = positions[i];
            Pair<CloudType, Float> typeAt = this.cloudGetter.getCloudTypeAtPosition(pos[0], pos[1]);
            if (((Float)typeAt.getRight()).floatValue() < 1.0f) {
                empty = false;
            }
            NoiseSettings config = ((CloudType)typeAt.getLeft()).noiseConfig();
            int startHeight = config.getStartHeight();
            int endHeight = config.getEndHeight();
            if (i == 0 || smallestStartHeight > startHeight) {
                smallestStartHeight = startHeight;
            }
            if (i != 0 && largestEndHeight >= endHeight) continue;
            largestEndHeight = endHeight;
        }
        if (empty || smallestStartHeight == largestEndHeight) {
            return MultiRegionCloudMeshGenerator.skip();
        }
        return MultiRegionCloudMeshGenerator.heights(smallestStartHeight, largestEndHeight);
    }

    @Override
    protected void generateChunk(CloudMeshGenerator.ChunkGenTask task) {
        this.shader.forUniform("RegionSampleOffset", (id, loc) -> {
            PreparedChunk chunk = task.chunk().getChunkInfo();
            GL41.glProgramUniform2f((int)id, (int)loc, (float)((float)chunk.x() * 32.0f + (float)this.requiredRegionTexSize / 2.0f), (float)((float)chunk.z() * 32.0f + (float)this.requiredRegionTexSize / 2.0f));
        });
        this.shader.setSampler2DArray("RegionsSampler", this.cloudRegionTextureId, 0);
        super.generateChunk(task);
    }

    private void runRegionGenerator(float meshOffsetX, float meshOffsetZ, float partialTick) {
        if (this.regionTextureGenerator == null || !this.regionTextureGenerator.isValid()) {
            return;
        }
        this.uploadCloudRegionData(partialTick);
        this.regionTextureGenerator.forUniform("Offset", (id, loc) -> GL41.glProgramUniform2f((int)id, (int)loc, (float)meshOffsetX, (float)meshOffsetZ));
        this.regionTextureGenerator.dispatchAndWait(this.requiredRegionTexSize / 16, this.requiredRegionTexSize / 16, 1);
    }

    private void uploadCloudRegionData(float partialTick) {
        if (this.regionTextureGenerator == null || !this.regionTextureGenerator.isValid()) {
            return;
        }
        List regionData = this.cloudGetter.getClouds().stream().map(region -> {
            Matrix2f transform = region.createTransform(partialTick);
            float[] data = new float[]{region.getPosX(partialTick), region.getPosZ(partialTick), ArrayUtils.indexOf((Object[])this.cachedTypes, (Object)this.cloudGetter.getCloudTypeForId(region.getCloudTypeId())), region.getRadius(partialTick), transform.m00, transform.m01, transform.m10, transform.m11};
            return data;
        }).filter(data -> data[2] >= 0.0f).toList();
        int regionDataSize = regionData.size();
        int count = Math.min(10, regionDataSize);
        if (regionDataSize != this.currentCloudFormationCount) {
            if (regionDataSize > 10 && regionDataSize > this.currentCloudFormationCount) {
                LOGGER.warn("Cloud formations {}/{}. Maximum count has been exceeded; some cloud formations will be ignored. Please ensure cloud formation count does not exceed the maximum of {}.", (Object)regionData.size(), (Object)10, (Object)10);
            }
            this.currentCloudFormationCount = regionDataSize;
        }
        if (count > 0) {
            ShaderStorageBufferObject regionsBuffer = this.regionTextureGenerator.getShaderStorageBuffer(CLOUD_REGIONS_NAME);
            regionsBuffer.writeData(b -> {
                for (int i = 0; i < count; ++i) {
                    float[] data;
                    for (float f : data = (float[])regionData.get(i)) {
                        b.putFloat(f);
                    }
                }
                b.rewind();
            }, count * 32, false);
        }
        this.regionTextureGenerator.forUniform("TotalCloudRegions", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)count));
    }

    private void uploadCloudTypeData() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.shader != null && this.shader.isValid()) {
            CloudType[] toCopy = this.cloudGetter.getIndexedCloudTypes();
            if (toCopy.length > 64) {
                LOGGER.warn("Cloud type count exceeds the maximum. Not all cloud types will render.");
            }
            int copySize = Math.min(64, toCopy.length);
            this.cachedTypes = Arrays.copyOf(toCopy, copySize);
            LOGGER.debug("Uploading cloud type noise data...");
            this.shader.getShaderStorageBuffer("LayerGroupings").writeData(b -> {
                int previousLayerIndex = 0;
                for (int i = 0; i < this.cachedTypes.length; ++i) {
                    CloudInfo type = this.cachedTypes[i];
                    previousLayerIndex = type.packToBuffer((ByteBuffer)b, previousLayerIndex);
                }
                b.rewind();
            }, 24 * this.cachedTypes.length, false);
            this.shader.getShaderStorageBuffer("NoiseLayers").writeData(b -> {
                for (int i = 0; i < this.cachedTypes.length; ++i) {
                    NoiseSettings settings = this.cachedTypes[i].noiseConfig();
                    float[] packed = settings.packForShader();
                    for (int j = 0; j < packed.length && j < AbstractNoiseSettings.Param.values().length * 4; ++j) {
                        b.putFloat(packed[j]);
                    }
                }
                b.rewind();
            }, AbstractNoiseSettings.Param.values().length * 4 * 4 * this.cachedTypes.length, false);
        }
    }

    @Override
    protected int prepareMeshGen(double originX, double originY, double originZ, float meshGenOffsetX, float meshGenOffsetZ, @Nullable Frustum frustum, int interval, float partialTick) {
        if (this.updateCloudTypes) {
            this.uploadCloudTypeData();
            this.updateCloudTypes = false;
        }
        this.runRegionGenerator(meshGenOffsetX, meshGenOffsetZ, partialTick);
        return super.prepareMeshGen(originX, originY, originZ, meshGenOffsetX, meshGenOffsetZ, frustum, interval, partialTick);
    }

    @Override
    protected void onOffGen() {
        super.onOffGen();
        if (this.regionTextureGenerator != null) {
            this.regionTextureGenerator.getShaderStorageBuffer(CLOUD_REGIONS_NAME).readData(buf -> {}, 320);
        }
    }

    @Override
    public void close() {
        super.close();
        this.currentCloudFormationCount = 0;
        this.requiredRegionTexSize = 0;
        this.updateCloudTypes = false;
        this.cloudGetter = CloudGetter.EMPTY;
        this.cachedTypes = new CloudInfo[0];
        if (this.regionTextureGenerator != null) {
            this.regionTextureGenerator.close();
            this.regionTextureGenerator = null;
        }
        if (this.cloudRegionTextureId != -1) {
            TextureUtil.releaseTextureId((int)this.cloudRegionTextureId);
            this.cloudRegionTextureId = -1;
        }
        if (this.cloudRegionImageBinding != -1) {
            BindingManager.freeImageUnit(this.cloudRegionImageBinding);
            this.cloudRegionImageBinding = -1;
        }
    }

    @Override
    public void fillReport(CrashReportCategory category) {
        category.m_128159_("Cloud Types", (Object)("(" + this.cachedTypes.length + ") " + Joiner.on((String)", ").join((Object[])this.cachedTypes)));
        category.m_128159_("Cloud Regions", (Object)this.cloudGetter.getClouds().size());
        category.m_128159_("Cloud Formations", (Object)this.currentCloudFormationCount);
        super.fillReport(category);
    }
}

