/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  javax.annotation.Nullable
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL31
 *  org.lwjgl.opengl.GL41
 *  org.lwjgl.opengl.GL42
 */
package dev.nonamecrackers2.simpleclouds.client.mesh.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.mesh.LevelOfDetailOptions;
import dev.nonamecrackers2.simpleclouds.client.mesh.RendererInitializeResult;
import dev.nonamecrackers2.simpleclouds.client.mesh.chunk.MeshChunk;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.MultiRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.SingleRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.instancing.InstanceableMesh;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.LevelOfDetailConfig;
import dev.nonamecrackers2.simpleclouds.client.mesh.lod.PreparedChunk;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.BindingManager;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.ShaderStorageBufferObject;
import dev.nonamecrackers2.simpleclouds.client.shader.compute.ComputeShader;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.mixin.MixinFrustumAccessor;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;

public abstract class CloudMeshGenerator {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/CloudMeshGenerator");
    public static final ResourceLocation MAIN_CUBE_MESH_GENERATOR = SimpleCloudsMod.id("cube_mesh");
    public static final int MAX_NOISE_LAYERS = 4;
    public static final int VERTICAL_CHUNK_SPAN = 8;
    public static final int LOCAL_SIZE = 8;
    public static final int WORK_SIZE = 4;
    public static final int TICKS_UNTIL_FADE_RESET = 120;
    public static final int BYTES_PER_SIDE_INFO = 24;
    public static final int MAX_SIDE_INFO_BUFFER_SIZE = 0x3000000;
    public static final String SIDE_INFO_BUFFER_NAME = "SideInfoBuffer";
    public static final String TOTAL_SIDES_NAME = "TotalSides";
    public static final String SIDES_PER_CHUNK_NAME = "SidesPerChunk";
    public static final int BYTES_PER_CUBE_INFO = 24;
    public static final int MAX_TRANSPARENT_CUBE_INFO_BUFFER_SIZE = 0x3000000;
    public static final String TRANSPARENT_CUBE_INFO_BUFFER_NAME = "TransparentCubeInfoBuffer";
    public static final String TRANSPARENT_TOTAL_CUBES_NAME = "TotalTransparentCubes";
    public static final String TRANSPARENT_CUBES_PER_CHUNK_NAME = "TransparentCubesPerChunk";
    public static final String NOISE_LAYERS_NAME = "NoiseLayers";
    public static final String LAYER_GROUPINGS_NAME = "LayerGroupings";
    protected final ResourceLocation meshShaderLoc;
    protected final int shaderType;
    protected final boolean fadeNearOrigin;
    protected final boolean shadedClouds;
    protected final boolean useTransparency;
    protected final LevelOfDetailConfig lodConfig;
    protected final boolean useFixedMeshDataSectionSize;
    @Nullable
    protected List<MeshChunk> chunks;
    protected final List<ChunkGenTask> completedGenTasks = Lists.newArrayList();
    protected final Queue<ChunkGenTask> chunkGenTasks = Queues.newArrayDeque();
    protected final Supplier<Integer> meshGenIntervalCalculator;
    protected int meshGenInterval = 1;
    protected int tasksPerTick;
    @Nullable
    protected ComputeShader shader;
    @Nullable
    protected InstanceableMesh sideMesh;
    @Nullable
    protected InstanceableMesh cubeMesh;
    protected Pair<MeshGenStatus, MeshGenStatus> meshGenStatus = Pair.of((Object)((Object)MeshGenStatus.NOT_INITIALIZED), (Object)((Object)MeshGenStatus.NOT_INITIALIZED));
    protected float scrollX;
    protected float scrollY;
    protected float scrollZ;
    protected boolean testFacesFacingAway;
    private float fadeStart;
    private float fadeEnd;
    private float cullDistance;
    private int transparencyDistance;
    private int opaqueBufferSize;
    private int opaqueBufferBytesUsed;
    private int transparentBufferSize;
    private int transparentBufferBytesUsed;
    private int opaqueBytesPerChunk;
    private int transparentBytesPerChunk;

    public CloudMeshGenerator(ResourceLocation meshShaderLoc, int shaderType, boolean fadeNearOrigin, boolean shadedClouds, LevelOfDetailConfig lodConfig, Supplier<Integer> meshGenIntervalCalculator, boolean useTransparency, boolean fixedMeshDataSectionSize) {
        this.meshShaderLoc = meshShaderLoc;
        this.shaderType = shaderType;
        this.fadeNearOrigin = fadeNearOrigin;
        this.shadedClouds = shadedClouds;
        this.useFixedMeshDataSectionSize = fixedMeshDataSectionSize;
        this.lodConfig = lodConfig;
        this.meshGenIntervalCalculator = meshGenIntervalCalculator;
        this.useTransparency = useTransparency;
        float maxRadius = this.getCloudAreaMaxRadius();
        this.fadeStart = 0.9f * maxRadius;
        this.fadeEnd = maxRadius;
        this.transparencyDistance = (int)maxRadius / 2;
    }

    public boolean fadeNearOriginEnabled() {
        return this.fadeNearOrigin;
    }

    public boolean shadedCloudsEnabled() {
        return this.shadedClouds;
    }

    public boolean transparencyEnabled() {
        return this.useTransparency;
    }

    public boolean usesFixedMeshDataSectionSize() {
        return this.useFixedMeshDataSectionSize;
    }

    public LevelOfDetailConfig getLodConfig() {
        return this.lodConfig;
    }

    public CloudMeshGenerator setTestFacesFacingAway(boolean flag) {
        this.testFacesFacingAway = flag;
        return this;
    }

    public CloudMeshGenerator setFadeDistances(float fadeStart, float fadeEnd) {
        float fs = fadeStart;
        float fe = fadeEnd;
        if (fs > fe) {
            fs = fadeEnd;
            fe = fadeStart;
        }
        this.fadeStart = fs * (float)this.getCloudAreaMaxRadius();
        this.fadeEnd = fe * (float)this.getCloudAreaMaxRadius();
        return this;
    }

    public CloudMeshGenerator setTransparencyRenderDistance(float percentage) {
        this.transparencyDistance = Mth.m_14143_((float)(percentage * (float)this.getCloudAreaMaxRadius()));
        return this;
    }

    public float getFadeStart() {
        return this.fadeStart;
    }

    public float getFadeEnd() {
        return this.fadeEnd;
    }

    public int getCloudAreaMaxRadius() {
        return this.lodConfig.getEffectiveChunkSpan() * 4 * 8 / 2;
    }

    public void setCullDistance(float dist) {
        if (dist <= 0.0f) {
            throw new IllegalArgumentException("Cull distance must be greater than zero");
        }
        this.cullDistance = dist;
    }

    public void disableCullDistance() {
        this.cullDistance = 0.0f;
    }

    public void setScroll(float x, float y, float z) {
        this.scrollX = x;
        this.scrollY = y;
        this.scrollZ = z;
    }

    public Pair<MeshGenStatus, MeshGenStatus> getMeshGenStatus() {
        return this.meshGenStatus;
    }

    @Nullable
    public InstanceableMesh getSideMesh() {
        return this.sideMesh;
    }

    @Nullable
    public InstanceableMesh getCubeMesh() {
        return this.cubeMesh;
    }

    public int getOpaqueBufferSize() {
        return this.opaqueBufferSize;
    }

    public int getOpaqueBufferBytesUsed() {
        return this.opaqueBufferBytesUsed;
    }

    public int getTransparentBufferSize() {
        return this.transparentBufferSize;
    }

    public int getTransparentBufferBytesUsed() {
        return this.transparentBufferBytesUsed;
    }

    public int getOpaqueBytesPerChunk() {
        return this.opaqueBytesPerChunk;
    }

    public int getTransparentBytesPerChunk() {
        return this.transparentBytesPerChunk;
    }

    public int getTotalMeshChunks() {
        if (this.chunks == null) {
            return 0;
        }
        return this.chunks.size();
    }

    public int getMeshGenInterval() {
        return this.meshGenInterval;
    }

    public void close() {
        RenderSystem.assertOnRenderThreadOrInit();
        this.opaqueBufferBytesUsed = 0;
        this.opaqueBufferSize = 0;
        this.opaqueBytesPerChunk = 0;
        this.transparentBufferBytesUsed = 0;
        this.transparentBufferSize = 0;
        this.transparentBytesPerChunk = 0;
        GL42.glMemoryBarrier((int)-1);
        this.chunkGenTasks.clear();
        this.completedGenTasks.clear();
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        if (this.chunks != null) {
            for (MeshChunk chunk : this.chunks) {
                chunk.destroy();
            }
            this.chunks = null;
        }
        if (this.sideMesh != null) {
            this.sideMesh.destroy();
            this.sideMesh = null;
        }
        if (this.cubeMesh != null) {
            this.cubeMesh.destroy();
            this.cubeMesh = null;
        }
    }

    public boolean canRender() {
        return this.chunks != null;
    }

    public final RendererInitializeResult init(ResourceManager manager) {
        RendererInitializeResult.Builder builder = RendererInitializeResult.builder();
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            return builder.errorUnknown(new IllegalStateException("Init not called on render thread"), "Mesh Generator; Head").build();
        }
        this.opaqueBufferBytesUsed = 0;
        this.opaqueBufferSize = 0;
        this.opaqueBytesPerChunk = 0;
        this.transparentBufferBytesUsed = 0;
        this.transparentBufferSize = 0;
        this.transparentBytesPerChunk = 0;
        GL42.glMemoryBarrier((int)-1);
        this.chunkGenTasks.clear();
        this.completedGenTasks.clear();
        LOGGER.debug("Beginning mesh generator initialization");
        if (this.shader != null) {
            LOGGER.debug("Freeing mesh compute shader");
            this.shader.close();
            this.shader = null;
        }
        if (this.chunks != null) {
            for (MeshChunk chunk : this.chunks) {
                chunk.destroy();
            }
            this.chunks = null;
        }
        try {
            LOGGER.debug("Creating mesh compute shader...");
            this.shader = this.createShader(manager);
            this.setupShader();
        }
        catch (IOException e) {
            builder.errorCouldNotLoadMeshScript(e, "Mesh Generator; Compute Shader");
        }
        catch (Exception e) {
            builder.errorRecommendations(e, "Mesh Generator; Compute Shader");
        }
        try {
            this.initExtra(manager);
        }
        catch (Exception e) {
            builder.errorUnknown(e, "Init Extra");
        }
        List<PreparedChunk> preparedChunks = this.getLodConfig().getPreparedChunks();
        ImmutableList.Builder meshChunks = ImmutableList.builder();
        int totalPreparedChunks = preparedChunks.size();
        this.opaqueBytesPerChunk = Mth.m_14167_((float)(this.opaqueBufferSize / totalPreparedChunks));
        this.transparentBytesPerChunk = Mth.m_14167_((float)(this.transparentBufferSize / totalPreparedChunks));
        if (!this.useFixedMeshDataSectionSize) {
            this.opaqueBytesPerChunk *= 4;
            this.transparentBytesPerChunk *= 4;
        }
        int maxOpaqueElements = Mth.m_14143_((float)((float)this.opaqueBytesPerChunk / 24.0f));
        int maxTransparentElements = Mth.m_14143_((float)((float)this.transparentBytesPerChunk / 24.0f));
        int opaqueElementOffset = 0;
        int transparentElementOffset = 0;
        for (PreparedChunk chunk : preparedChunks) {
            meshChunks.add((Object)new MeshChunk(chunk, maxOpaqueElements, opaqueElementOffset, 24, maxTransparentElements, transparentElementOffset, 24, this.useTransparency));
            opaqueElementOffset += maxOpaqueElements;
            transparentElementOffset += maxTransparentElements;
        }
        this.chunks = meshChunks.build();
        LOGGER.debug("Opaque buffer size: {} bytes, transparent buffer size: {} bytes", (Object)this.opaqueBufferSize, (Object)this.transparentBufferSize);
        if (this.sideMesh != null) {
            this.sideMesh.destroy();
        }
        this.sideMesh = InstanceableMesh.defaultSide();
        if (this.cubeMesh != null) {
            this.cubeMesh.destroy();
        }
        this.cubeMesh = InstanceableMesh.defaultCube();
        BindingManager.printDebug();
        LOGGER.debug("Finished initializing mesh generator");
        return builder.build();
    }

    protected ComputeShader createShader(ResourceManager manager) throws IOException {
        ImmutableMap parameters = ImmutableMap.of((Object)"TYPE", (Object)String.valueOf(this.shaderType), (Object)"FADE_NEAR_ORIGIN", (Object)(this.fadeNearOrigin ? "1" : "0"), (Object)"STYLE", (Object)(this.shadedClouds ? "1" : "0"), (Object)"TRANSPARENCY", (Object)(this.useTransparency ? "1" : "0"), (Object)"FIXED_SECTION_SIZE", (Object)(this.useFixedMeshDataSectionSize ? "1" : "0"));
        return ComputeShader.loadShader(this.meshShaderLoc, (ResourceProvider)manager, 8, 8, 8, (ImmutableMap<String, String>)parameters);
    }

    protected void setupShader() {
        this.opaqueBufferSize = this.createBuffers(TOTAL_SIDES_NAME, SIDES_PER_CHUNK_NAME, SIDE_INFO_BUFFER_NAME, 0x3000000 * (this.useFixedMeshDataSectionSize ? 4 : 1));
        if (this.useTransparency) {
            this.transparentBufferSize = this.createBuffers(TRANSPARENT_TOTAL_CUBES_NAME, TRANSPARENT_CUBES_PER_CHUNK_NAME, TRANSPARENT_CUBE_INFO_BUFFER_NAME, 0x3000000 * (this.useFixedMeshDataSectionSize ? 4 : 1));
        }
        this.shader.forUniform("TotalLodLevels", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)this.lodConfig.getLods().length));
        this.uploadFadeData();
    }

    private void uploadFadeData() {
        if (this.shader == null || !this.shader.isValid()) {
            return;
        }
        this.shader.forUniform("TransparencyDistance", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)this.transparencyDistance));
        this.shader.forUniform("FadeStart", (id, loc) -> GL41.glProgramUniform1f((int)id, (int)loc, (float)this.fadeStart));
        this.shader.forUniform("FadeEnd", (id, loc) -> GL41.glProgramUniform1f((int)id, (int)loc, (float)this.fadeEnd));
    }

    private int createBuffers(String totalCounterName, String countPerChunkName, String elementInfoBufferName, int maxSize) {
        if (!this.useFixedMeshDataSectionSize) {
            ShaderStorageBufferObject totalCountBuffer = this.shader.createAndBindSSBO(totalCounterName, 35050);
            totalCountBuffer.allocateBuffer(4);
            totalCountBuffer.writeData(b -> b.putInt(0, 0), 4, false);
        }
        int bufferSize = this.shader.createAndBindSSBO(elementInfoBufferName, 35050).allocateBuffer(maxSize);
        int totalChunks = this.getLodConfig().getPreparedChunks().size();
        int countPerChunkBufferSize = totalChunks * 4;
        ShaderStorageBufferObject countPerChunkBuffer = this.shader.createAndBindSSBO(countPerChunkName, 35050);
        countPerChunkBuffer.allocateBuffer(countPerChunkBufferSize);
        countPerChunkBuffer.writeData(b -> {
            for (int i = 0; i < totalChunks; ++i) {
                b.putInt(0);
            }
            b.rewind();
        }, countPerChunkBufferSize, false);
        return bufferSize;
    }

    protected void initExtra(ResourceManager manager) throws IOException {
    }

    public void generateMesh() {
        RenderSystem.assertOnRenderThread();
        if (this.shader == null || !this.shader.isValid()) {
            return;
        }
        this.prepareMeshGen(0.0, 0.0, 0.0, 0.0f, 0.0f, null, 1, 1.0f);
        if (!this.chunkGenTasks.isEmpty()) {
            this.doMeshGenning(this.chunkGenTasks.size());
        }
        this.meshGenStatus = this.finalizeMeshGen();
        this.completedGenTasks.clear();
    }

    public void worldTick() {
        if (this.chunks != null) {
            this.chunks.forEach(MeshChunk::tick);
        }
    }

    public void genTick(double originX, double originY, double originZ, @Nullable Frustum frustum, float partialTick) {
        RenderSystem.assertOnRenderThread();
        if (this.shader == null || !this.shader.isValid()) {
            return;
        }
        float chunkSize = 32.0f;
        float meshGenOffsetX = (float)Mth.m_14107_((double)(originX / (double)chunkSize)) * chunkSize;
        float meshGenOffsetZ = (float)Mth.m_14107_((double)(originZ / (double)chunkSize)) * chunkSize;
        if (this.chunkGenTasks.isEmpty()) {
            this.meshGenStatus = this.finalizeMeshGen();
            this.completedGenTasks.clear();
            this.meshGenInterval = this.meshGenIntervalCalculator.get();
            if (this.meshGenInterval <= 0) {
                throw new RuntimeException("Mesh gen interval is <= 0");
            }
            this.tasksPerTick = this.prepareMeshGen(originX, originY, originZ, meshGenOffsetX, meshGenOffsetZ, frustum, this.meshGenInterval, partialTick);
        } else {
            this.onOffGen();
        }
        if (!this.chunkGenTasks.isEmpty()) {
            this.doMeshGenning(this.tasksPerTick);
        }
    }

    private static MeshGenStatus fixedIterateAndCopyToChunkBuffer(int copyBufferId, int copyBufferSizeBytes, Collection<MeshChunk> chunks, Function<MeshChunk, Integer> byteOffsetPerChunk, Function<MeshChunk, Integer> chunkBufferId, Function<MeshChunk, Integer> bytesToCopyPerChunk, Function<MeshChunk, Integer> bufferSizeBytesPerChunk) {
        MeshGenStatus result = MeshGenStatus.NORMAL;
        GlStateManager._glBindBuffer((int)36662, (int)copyBufferId);
        for (MeshChunk chunk : chunks) {
            int byteOffset;
            int bytesToCopy = bytesToCopyPerChunk.apply(chunk);
            if (bytesToCopy <= 0) continue;
            int maxSize = bufferSizeBytesPerChunk.apply(chunk);
            if (bytesToCopy > maxSize) {
                bytesToCopy = maxSize;
                result = MeshGenStatus.CHUNK_OVERFLOW;
            }
            if ((byteOffset = byteOffsetPerChunk.apply(chunk).intValue()) + bytesToCopy > copyBufferSizeBytes && (bytesToCopy = copyBufferSizeBytes - byteOffset) <= 0) continue;
            GlStateManager._glBindBuffer((int)36663, (int)chunkBufferId.apply(chunk));
            GL31.glCopyBufferSubData((int)36662, (int)36663, (long)byteOffset, (long)0L, (long)bytesToCopy);
        }
        return result;
    }

    private static MeshGenStatus packedIterateAndCopyToChunkBuffer(int copyBufferId, int copyBufferSizeBytes, Collection<MeshChunk> chunks, Function<MeshChunk, Integer> chunkBufferId, Function<MeshChunk, Integer> bytesToCopyPerChunk, Function<MeshChunk, Integer> bufferSizeBytesPerChunk) {
        MeshGenStatus result = MeshGenStatus.NORMAL;
        GlStateManager._glBindBuffer((int)36662, (int)copyBufferId);
        int currentBytes = 0;
        for (MeshChunk chunk : chunks) {
            int totalBytes = bytesToCopyPerChunk.apply(chunk);
            if (totalBytes <= 0) continue;
            int lastBytesOffset = totalBytes;
            int maxSize = bufferSizeBytesPerChunk.apply(chunk);
            if (lastBytesOffset > maxSize) {
                lastBytesOffset = maxSize;
                result = MeshGenStatus.CHUNK_OVERFLOW;
            }
            boolean stop = false;
            if (currentBytes + lastBytesOffset > copyBufferSizeBytes) {
                lastBytesOffset = copyBufferSizeBytes - currentBytes;
                if (lastBytesOffset <= 0) {
                    return MeshGenStatus.MESH_POOL_OVERFLOW;
                }
                stop = true;
            }
            GlStateManager._glBindBuffer((int)36663, (int)chunkBufferId.apply(chunk));
            GL31.glCopyBufferSubData((int)36662, (int)36663, (long)currentBytes, (long)0L, (long)lastBytesOffset);
            currentBytes += totalBytes;
            if (!stop) continue;
            return MeshGenStatus.MESH_POOL_OVERFLOW;
        }
        return result;
    }

    protected Pair<MeshGenStatus, MeshGenStatus> finalizeMeshGen() {
        if (this.shader == null || !this.shader.isValid() || this.chunks == null) {
            return Pair.of((Object)((Object)MeshGenStatus.NOT_INITIALIZED), (Object)((Object)MeshGenStatus.NOT_INITIALIZED));
        }
        if (this.completedGenTasks.isEmpty()) {
            return Pair.of((Object)((Object)MeshGenStatus.NO_TASKS), (Object)((Object)MeshGenStatus.NO_TASKS));
        }
        RenderSystem.assertOnRenderThread();
        GL42.glMemoryBarrier((int)8192);
        MeshGenStatus opaqueResult = MeshGenStatus.NORMAL;
        MeshGenStatus transparentResult = MeshGenStatus.NORMAL;
        opaqueResult = this.copyMeshData(TOTAL_SIDES_NAME, SIDES_PER_CHUNK_NAME, SIDE_INFO_BUFFER_NAME, MeshChunk::getOpaqueBuffers, 24, this.opaqueBufferSize);
        if (this.useTransparency) {
            transparentResult = this.copyMeshData(TRANSPARENT_TOTAL_CUBES_NAME, TRANSPARENT_CUBES_PER_CHUNK_NAME, TRANSPARENT_CUBE_INFO_BUFFER_NAME, c -> c.getTransparentBuffers().get(), 24, this.transparentBufferSize);
        }
        this.opaqueBufferBytesUsed = 0;
        this.transparentBufferBytesUsed = 0;
        for (MeshChunk chunk : this.chunks) {
            this.opaqueBufferBytesUsed += chunk.getOpaqueBuffers().getElementCount() * 24;
            chunk.getTransparentBuffers().ifPresent(bufferSet -> this.transparentBufferBytesUsed += bufferSet.getElementCount() * 24);
        }
        return Pair.of((Object)((Object)opaqueResult), (Object)((Object)transparentResult));
    }

    private MeshGenStatus copyMeshData(String totalCountBufferName, String countPerChunkBufferName, String elementBufferName, Function<MeshChunk, MeshChunk.BufferSet> bufferSetFunction, int bytesPerElement, int elementBufferSize) {
        MeshGenStatus status = MeshGenStatus.NORMAL;
        if (!this.useFixedMeshDataSectionSize) {
            this.shader.getShaderStorageBuffer(totalCountBufferName).writeData(b -> b.putInt(0, 0), 4, true);
        }
        this.shader.getShaderStorageBuffer(countPerChunkBufferName).readWriteData(buffer -> {
            for (ChunkGenTask gennedChunk : this.completedGenTasks) {
                MeshChunk.BufferSet bufferSet = (MeshChunk.BufferSet)bufferSetFunction.apply(gennedChunk.chunk());
                int index = gennedChunk.index() * 4;
                int count = buffer.getInt(index);
                bufferSet.setTotalElementCount(count);
                buffer.putInt(index, 0);
            }
        }, this.chunks.size() * 4);
        List completedChunks = this.completedGenTasks.stream().map(ChunkGenTask::chunk).toList();
        int elementBufferId = this.shader.getShaderStorageBuffer(elementBufferName).getId();
        status = this.useFixedMeshDataSectionSize ? CloudMeshGenerator.fixedIterateAndCopyToChunkBuffer(elementBufferId, elementBufferSize, completedChunks, bufferSetFunction.andThen(b -> b.getElementOffset() * bytesPerElement), bufferSetFunction.andThen(MeshChunk.BufferSet::getBufferId), bufferSetFunction.andThen(c -> c.getElementCount() * bytesPerElement), bufferSetFunction.andThen(MeshChunk.BufferSet::getBufferSize)) : CloudMeshGenerator.packedIterateAndCopyToChunkBuffer(elementBufferId, elementBufferSize, completedChunks, bufferSetFunction.andThen(MeshChunk.BufferSet::getBufferId), bufferSetFunction.andThen(c -> c.getElementCount() * bytesPerElement), bufferSetFunction.andThen(MeshChunk.BufferSet::getBufferSize));
        GlStateManager._glBindBuffer((int)36662, (int)0);
        GlStateManager._glBindBuffer((int)36663, (int)0);
        return status;
    }

    protected int prepareMeshGen(double originX, double originY, double originZ, float meshGenOffsetX, float meshGenOffsetZ, @Nullable Frustum frustum, int genInterval, float partialTick) {
        this.shader.forUniform("Scroll", (id, loc) -> GL41.glProgramUniform3f((int)id, (int)loc, (float)this.scrollX, (float)this.scrollY, (float)this.scrollZ));
        this.shader.forUniform("Wiggle", (id, loc) -> GL41.glProgramUniform1f((int)id, (int)loc, (float)((this.scrollX + this.scrollY + this.scrollZ) / 5.0f)));
        this.shader.forUniform("Origin", (id, loc) -> GL41.glProgramUniform3f((int)id, (int)loc, (float)((float)originX), (float)((float)originY), (float)((float)originZ)));
        this.shader.forUniform("TestFacesFacingAway", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)(this.testFacesFacingAway ? 1 : 0)));
        this.uploadFadeData();
        int chunkCount = 0;
        for (int i = 0; i < this.chunks.size(); ++i) {
            if (!this.queueChunkMeshGenTaskOrClear(this.chunks.get(i), i, meshGenOffsetX, meshGenOffsetZ, frustum)) continue;
            ++chunkCount;
        }
        return Mth.m_14167_((float)((float)chunkCount / (float)genInterval));
    }

    protected void onOffGen() {
        if (!this.useFixedMeshDataSectionSize) {
            this.shader.getShaderStorageBuffer(TOTAL_SIDES_NAME).readWriteData(b -> {}, 4);
        }
        this.shader.getShaderStorageBuffer(SIDES_PER_CHUNK_NAME).readWriteData(buffer -> {}, this.chunks.size() * 4);
        if (this.useTransparency) {
            if (!this.useFixedMeshDataSectionSize) {
                this.shader.getShaderStorageBuffer(TRANSPARENT_TOTAL_CUBES_NAME).readWriteData(b -> {}, 4);
            }
            this.shader.getShaderStorageBuffer(TRANSPARENT_CUBES_PER_CHUNK_NAME).readWriteData(buffer -> {}, this.chunks.size() * 4);
        }
    }

    protected boolean queueChunkMeshGenTaskOrClear(MeshChunk chunk, int chunkIndex, float meshGenOffsetX, float meshGenOffsetZ, @Nullable Frustum frustum) {
        PreparedChunk chunkInfo = chunk.getChunkInfo();
        AABB bounds = chunkInfo.bounds();
        float minX = (float)bounds.f_82288_ + meshGenOffsetX;
        float minZ = (float)bounds.f_82290_ + meshGenOffsetZ;
        float maxX = (float)bounds.f_82291_ + meshGenOffsetX;
        float maxZ = (float)bounds.f_82293_ + meshGenOffsetZ;
        if (frustum == null || ((MixinFrustumAccessor)frustum).simpleclouds$cubeInFrustum(minX, bounds.f_82289_, minZ, maxX, bounds.f_82292_, maxZ)) {
            double nearestCornerX = Math.max(Math.max(bounds.f_82288_, -bounds.f_82291_), 0.0);
            double nearestCornerZ = Math.max(Math.max(bounds.f_82290_, -bounds.f_82293_), 0.0);
            double dist = Math.sqrt(nearestCornerX * nearestCornerX + nearestCornerZ * nearestCornerZ);
            if (this.cullDistance <= 0.0f || dist < (double)this.cullDistance) {
                ChunkGenSettings settings = this.determineChunkGenSettings(minX, minZ, maxX, maxZ);
                if (settings.skipChunk()) {
                    chunk.clearChunk();
                    return false;
                }
                this.chunkGenTasks.add(new ChunkGenTask(chunk, minX, (float)bounds.f_82289_, minZ, maxX, (float)bounds.f_82292_, maxZ, chunkIndex, minX, 0.0f, minZ, settings.minimumHeight(), settings.maximumHeight()));
                return true;
            }
        }
        return false;
    }

    protected abstract ChunkGenSettings determineChunkGenSettings(float var1, float var2, float var3, float var4);

    protected void doMeshGenning(int tasksPerTick) {
        ChunkGenTask task;
        for (int i = 0; i < tasksPerTick && (task = this.chunkGenTasks.poll()) != null; ++i) {
            this.generateChunk(task);
            this.updateMeshChunkAfterGeneration(task.chunk(), task);
            this.completedGenTasks.add(task);
        }
    }

    protected void updateMeshChunkAfterGeneration(MeshChunk chunk, ChunkGenTask task) {
        chunk.setBounds(task.minX(), task.minY(), task.minZ(), task.maxX(), task.maxY(), task.maxZ());
        chunk.setHeights(task.startY(), task.endY());
        chunk.resetLastGenTime();
    }

    protected void generateChunk(ChunkGenTask task) {
        PreparedChunk chunkInfo = task.chunk().getChunkInfo();
        int lodScale = chunkInfo.lodScale();
        int lowestY = task.startY();
        int height = Mth.m_14167_((float)((float)(task.endY() - lowestY) / (float)lodScale));
        int localHeightInvocations = Mth.m_14167_((float)((float)height / 8.0f));
        if (localHeightInvocations > 0) {
            this.shader.forUniform("ChunkIndex", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)task.index()));
            this.shader.forUniform("LodLevel", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)chunkInfo.lodLevel()));
            this.shader.forUniform("RenderOffset", (id, loc) -> GL41.glProgramUniform3f((int)id, (int)loc, (float)task.x(), (float)(task.y() + (float)lowestY), (float)task.z()));
            this.shader.forUniform("Scale", (id, loc) -> GL41.glProgramUniform1f((int)id, (int)loc, (float)lodScale));
            this.shader.forUniform("DoNotOccludeSide", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)chunkInfo.noOcclusionDirectionIndex()));
            if (this.useFixedMeshDataSectionSize) {
                this.shader.forUniform("OpaqueMeshDataOffset", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)task.chunk().getOpaqueBuffers().getElementOffset()));
                task.chunk().getTransparentBuffers().ifPresent(bufferSet -> this.shader.forUniform("TransparentMeshDataOffset", (id, loc) -> GL41.glProgramUniform1i((int)id, (int)loc, (int)bufferSet.getElementOffset())));
            }
            this.shader.dispatch(4, localHeightInvocations, 4, false);
            if (!this.useFixedMeshDataSectionSize) {
                GL42.glMemoryBarrier((int)8192);
            }
        }
    }

    public void forRenderableMeshChunks(@Nullable Frustum frustum, Function<MeshChunk, MeshChunk.BufferSet> bufferSetFunction, BiConsumer<MeshChunk, MeshChunk.BufferSet> function) {
        this.forRenderableMeshChunks(frustum, bufferSetFunction, function, false);
    }

    public void forRenderableMeshChunks(@Nullable Frustum frustum, Function<MeshChunk, MeshChunk.BufferSet> bufferSetFunction, BiConsumer<MeshChunk, MeshChunk.BufferSet> function, boolean updateFade) {
        for (MeshChunk chunk : this.chunks) {
            MeshChunk.BufferSet bufferSet = bufferSetFunction.apply(chunk);
            if (bufferSet.getElementCount() <= 0) continue;
            if (updateFade && chunk.getTicksSinceLastGen() > 120) {
                chunk.resetAlpha();
                chunk.setFadeEnabled(false);
            }
            boolean render = true;
            if (frustum != null) {
                render = ((MixinFrustumAccessor)frustum).simpleclouds$cubeInFrustum(chunk.getBoundsMinX(), chunk.getBoundsMinY(), chunk.getBoundsMinZ(), chunk.getBoundsMaxX(), chunk.getBoundsMaxY(), chunk.getBoundsMaxZ());
            }
            if (!render) continue;
            PreparedChunk chunkInfo = chunk.getChunkInfo();
            AABB bounds = chunkInfo.bounds();
            double nearestCornerX = Math.max(Math.max(bounds.f_82288_, -bounds.f_82291_), 0.0);
            double nearestCornerZ = Math.max(Math.max(bounds.f_82290_, -bounds.f_82293_), 0.0);
            double dist = Math.sqrt(nearestCornerX * nearestCornerX + nearestCornerZ * nearestCornerZ);
            if (!(this.cullDistance <= 0.0f) && !((double)this.cullDistance > dist)) continue;
            if (updateFade) {
                chunk.setFadeEnabled(true);
            }
            function.accept(chunk, bufferSet);
        }
    }

    public void fillReport(CrashReportCategory category) {
        category.m_128159_("Shader Type", (Object)this.shaderType);
        category.m_128159_("Shaded Clouds", (Object)this.shadedClouds);
        category.m_128159_("Transparency Enabled", (Object)this.useTransparency);
        category.m_128159_("Fade Near Origin", (Object)this.fadeNearOrigin);
        category.m_128159_("Compute Shader", (Object)this.shader);
        category.m_128159_("Level Of Details", (Object)(1 + this.lodConfig.getLods().length));
        category.m_128159_("Generation Frame Interval", (Object)this.meshGenInterval);
        category.m_128159_("Total Prepared Chunks", (Object)this.lodConfig.getPreparedChunks().size());
        category.m_128159_("Tasks Per Frame", (Object)this.tasksPerTick);
        category.m_128159_("Scroll", (Object)String.format("X: %s, Y: %s, Z: %s", Float.valueOf(this.scrollX), Float.valueOf(this.scrollY), Float.valueOf(this.scrollZ)));
        category.m_128159_("Total Mesh Chunks", this.chunks != null ? Integer.valueOf(this.chunks.size()) : "null");
        category.m_128159_("Mesh Gen Status", this.meshGenStatus);
        category.m_128159_("Test Occluded Faces", (Object)this.testFacesFacingAway);
    }

    public String toString() {
        return String.format("%s[shader_name=%s]", this.getClass().getSimpleName(), this.meshShaderLoc);
    }

    protected static ChunkGenSettings skip() {
        return new ChunkGenSettings(true, 0, 0);
    }

    protected static ChunkGenSettings heights(int min, int max) {
        return new ChunkGenSettings(false, min, max);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static enum MeshGenStatus {
        NOT_INITIALIZED("Not initialized", true),
        NO_TASKS("No tasks", false),
        NORMAL("Normal", false),
        MESH_POOL_OVERFLOW("Mesh pool overflow", true),
        CHUNK_OVERFLOW("Chunk overflow", true);

        private String name;
        private boolean isErroneous;

        private MeshGenStatus(String name, boolean isErroneous) {
            this.name = name;
            this.isErroneous = isErroneous;
        }

        public String getName() {
            return this.name;
        }

        public boolean isErroneous() {
            return this.isErroneous;
        }
    }

    protected record ChunkGenSettings(boolean skipChunk, int minimumHeight, int maximumHeight) {
    }

    protected record ChunkGenTask(MeshChunk chunk, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int index, float x, float y, float z, int startY, int endY) {
    }

    public static class Builder {
        private boolean fadeNearOrigin;
        private boolean shadedClouds = true;
        private LevelOfDetailConfig lodConfig = LevelOfDetailOptions.HIGH.getConfig();
        private Supplier<Integer> meshGenIntervalCalculator = () -> 5;
        private boolean useTransparency = true;
        private boolean fixedMeshDataSectionSize;
        private float fadeStart = 0.5f;
        private float fadeEnd = 1.0f;
        private boolean testFacesFacingAway = false;

        private Builder() {
        }

        public Builder fadeNearOrigin(boolean flag) {
            this.fadeNearOrigin = flag;
            return this;
        }

        public Builder shadedClouds(boolean flag) {
            this.shadedClouds = flag;
            return this;
        }

        public Builder meshGenInterval(int interval) {
            if (interval <= 0) {
                throw new IllegalArgumentException("Mesh gen interval must be greater than 0");
            }
            this.meshGenIntervalCalculator = () -> interval;
            return this;
        }

        public Builder meshGenInterval(Supplier<Integer> calculator) {
            this.meshGenIntervalCalculator = calculator;
            return this;
        }

        public Builder lodConfig(LevelOfDetailConfig config) {
            this.lodConfig = config;
            return this;
        }

        public Builder useTransparency(boolean flag) {
            this.useTransparency = flag;
            return this;
        }

        public Builder fixedMeshDataSectionSize(boolean flag) {
            this.fixedMeshDataSectionSize = flag;
            return this;
        }

        public Builder fadeStart(float fadeStart) {
            this.fadeStart = fadeStart;
            return this;
        }

        public Builder fadeEnd(float fadeEnd) {
            this.fadeEnd = fadeEnd;
            return this;
        }

        public Builder testFacesFacingAway(boolean flag) {
            this.testFacesFacingAway = flag;
            return this;
        }

        private <T extends CloudMeshGenerator> T applyExtraSettings(T generator) {
            generator.setFadeDistances(this.fadeStart, this.fadeEnd);
            generator.setTestFacesFacingAway(this.testFacesFacingAway);
            return generator;
        }

        public MultiRegionCloudMeshGenerator createMultiRegion() {
            return this.applyExtraSettings(new MultiRegionCloudMeshGenerator(this.fadeNearOrigin, this.shadedClouds, this.lodConfig, this.meshGenIntervalCalculator, this.useTransparency, this.fixedMeshDataSectionSize));
        }

        public SingleRegionCloudMeshGenerator createSingleRegion(CloudInfo type) {
            return this.applyExtraSettings(new SingleRegionCloudMeshGenerator(this.shadedClouds, this.lodConfig, this.meshGenIntervalCalculator, this.useTransparency, this.fixedMeshDataSectionSize, type));
        }
    }
}

