/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.client.renderer.culling.Frustum
 */
package dev.nonamecrackers2.simpleclouds.client.mesh.generator;

import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.LevelOfDetailConfig;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.renderer.culling.Frustum;

public final class SingleRegionCloudMeshGenerator
extends CloudMeshGenerator {
    private CloudInfo type;
    private boolean needsNoiseRefreshing;

    protected SingleRegionCloudMeshGenerator(boolean shadedClouds, LevelOfDetailConfig lodConfig, Supplier<Integer> meshGenIntervalCalculator, boolean useTransparency, boolean fixedMeshDataSectionSize, CloudInfo type) {
        super(CloudMeshGenerator.MAIN_CUBE_MESH_GENERATOR, 1, false, shadedClouds, lodConfig, meshGenIntervalCalculator, useTransparency, fixedMeshDataSectionSize);
        this.setCloudType(type);
        this.setFadeDistances(0.5f, 1.0f);
    }

    @Override
    protected CloudMeshGenerator.ChunkGenSettings determineChunkGenSettings(float minX, float minZ, float maxX, float maxZ) {
        int endHeight;
        NoiseSettings config = this.type.noiseConfig();
        int startHeight = config.getStartHeight();
        if (startHeight == (endHeight = config.getEndHeight())) {
            return SingleRegionCloudMeshGenerator.skip();
        }
        return SingleRegionCloudMeshGenerator.heights(startHeight, endHeight);
    }

    public CloudInfo getCloudType() {
        return this.type;
    }

    public void setCloudType(CloudInfo type) {
        this.type = type;
        this.needsNoiseRefreshing = true;
    }

    @Override
    protected void setupShader() {
        super.setupShader();
        this.shader.createAndBindSSBO("LayerGroupings", 35044).allocateBuffer(24);
        this.shader.createAndBindSSBO("NoiseLayers", 35044).allocateBuffer(AbstractNoiseSettings.Param.values().length * 4 * 4);
        this.uploadNoiseData();
        this.needsNoiseRefreshing = false;
    }

    private void uploadNoiseData() {
        if (this.shader == null || !this.shader.isValid()) {
            return;
        }
        this.shader.getShaderStorageBuffer("NoiseLayers").writeData(b -> {
            float[] packed = this.type.noiseConfig().packForShader();
            for (int i = 0; i < packed.length && i < AbstractNoiseSettings.Param.values().length * 4; ++i) {
                b.putFloat(i * 4, packed[i]);
            }
        }, AbstractNoiseSettings.Param.values().length * 4 * 4, false);
        this.shader.getShaderStorageBuffer("LayerGroupings").writeData(b -> {
            this.type.packToBuffer((ByteBuffer)b, 0);
            b.rewind();
        }, 24, false);
    }

    @Override
    protected int prepareMeshGen(double originX, double originY, double originZ, float meshGenOffsetX, float meshGenOffsetZ, @Nullable Frustum frustum, int interval, float partialTick) {
        if (this.needsNoiseRefreshing) {
            this.uploadNoiseData();
            this.needsNoiseRefreshing = false;
        }
        return super.prepareMeshGen(originX, originY, originZ, meshGenOffsetX, meshGenOffsetZ, frustum, interval, partialTick);
    }

    @Override
    public void fillReport(CrashReportCategory category) {
        category.m_128159_("Cloud Type", (Object)this.type);
        super.fillReport(category);
    }
}

