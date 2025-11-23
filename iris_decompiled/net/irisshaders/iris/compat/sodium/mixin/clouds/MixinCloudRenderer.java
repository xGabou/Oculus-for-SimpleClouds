/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexBuffer$Usage
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  me.jellysquid.mods.sodium.client.render.immediate.CloudRenderer
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription
 *  net.caffeinemc.mods.sodium.api.vertex.format.common.ColorVertex
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.FogRenderer$FogData
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.compat.sodium.mixin.clouds;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.jellysquid.mods.sodium.client.render.immediate.CloudRenderer;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ColorVertex;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.entity_xhfp.CloudVertex;
import net.irisshaders.iris.pipeline.ShaderRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={CloudRenderer.class})
public abstract class MixinCloudRenderer {
    @Shadow
    private ShaderInstance shader;
    @Shadow
    @Final
    private FogRenderer.FogData fogData;
    @Unique
    private VertexBuffer vertexBufferWithNormals;
    @Unique
    private int prevCenterCellXIris;
    @Unique
    private int prevCenterCellYIris;
    @Unique
    private int cachedRenderDistanceIris;

    @Inject(method={"writeVertex"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void writeIrisVertex(long buffer, float x, float y, float z, int color, CallbackInfoReturnable<Long> cir) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            CloudVertex.write(buffer, x, y, z, color);
            cir.setReturnValue((Object)(buffer + 20L));
        }
    }

    @Shadow
    protected abstract void rebuildGeometry(BufferBuilder var1, int var2, int var3, int var4);

    @Shadow
    protected abstract void applyFogModifiers(ClientLevel var1, FogRenderer.FogData var2, LocalPlayer var3, int var4, float var5);

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void buildIrisVertexBuffer(ClientLevel world, LocalPlayer player, PoseStack matrices, Matrix4f projectionMatrix, float ticks, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            ci.cancel();
            this.renderIris(world, player, matrices, projectionMatrix, ticks, tickDelta, cameraX, cameraY, cameraZ);
        }
    }

    public void renderIris(@Nullable ClientLevel world, LocalPlayer player, PoseStack matrices, Matrix4f projectionMatrix, float ticks, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        boolean insideClouds;
        if (world == null) {
            return;
        }
        float cloudHeight = world.m_104583_().m_108871_();
        if (Float.isNaN(cloudHeight)) {
            return;
        }
        Vec3 color = world.m_104808_(tickDelta);
        double cloudTime = (ticks + tickDelta) * 0.03f;
        double cloudCenterX = cameraX + cloudTime;
        double cloudCenterZ = cameraZ + 0.33;
        int renderDistance = Minecraft.m_91087_().f_91066_.m_193772_();
        int cloudDistance = Math.max(32, renderDistance * 2 + 9);
        int centerCellX = (int)Math.floor(cloudCenterX / 12.0);
        int centerCellZ = (int)Math.floor(cloudCenterZ / 12.0);
        if (this.vertexBufferWithNormals == null || this.prevCenterCellXIris != centerCellX || this.prevCenterCellYIris != centerCellZ || this.cachedRenderDistanceIris != renderDistance) {
            BufferBuilder bufferBuilder = Tesselator.m_85913_().m_85915_();
            bufferBuilder.m_166779_(VertexFormat.Mode.QUADS, IrisVertexFormats.CLOUDS);
            this.rebuildGeometry(bufferBuilder, cloudDistance + 4, centerCellX, centerCellZ);
            if (this.vertexBufferWithNormals == null) {
                this.vertexBufferWithNormals = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
            }
            this.vertexBufferWithNormals.m_85921_();
            this.vertexBufferWithNormals.m_231221_(bufferBuilder.m_231175_());
            VertexBuffer.m_85931_();
            this.prevCenterCellXIris = centerCellX;
            this.prevCenterCellYIris = centerCellZ;
            this.cachedRenderDistanceIris = renderDistance;
        }
        float previousEnd = RenderSystem.getShaderFogEnd();
        float previousStart = RenderSystem.getShaderFogStart();
        this.fogData.f_234201_ = cloudDistance * 8;
        this.fogData.f_234200_ = cloudDistance * 8 - 16;
        this.applyFogModifiers(world, this.fogData, player, cloudDistance * 8, tickDelta);
        RenderSystem.setShaderFogEnd((float)this.fogData.f_234201_);
        RenderSystem.setShaderFogStart((float)this.fogData.f_234200_);
        float translateX = (float)(cloudCenterX - (double)(centerCellX * 12));
        float translateZ = (float)(cloudCenterZ - (double)(centerCellZ * 12));
        RenderSystem.enableDepthTest();
        this.vertexBufferWithNormals.m_85921_();
        boolean bl = insideClouds = cameraY < (double)(cloudHeight + 4.5f) && cameraY > (double)(cloudHeight - 0.5f);
        if (insideClouds) {
            RenderSystem.disableCull();
        } else {
            RenderSystem.enableCull();
        }
        RenderSystem.setShaderColor((float)((float)color.f_82479_), (float)((float)color.f_82480_), (float)((float)color.f_82481_), (float)0.8f);
        matrices.m_85836_();
        Matrix4f modelViewMatrix = matrices.m_85850_().m_252922_();
        modelViewMatrix.translate(-translateX, cloudHeight - (float)cameraY + 0.33f, -translateZ);
        RenderSystem.disableBlend();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        this.vertexBufferWithNormals.m_253207_(modelViewMatrix, projectionMatrix, this.getClouds());
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc((int)514);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        this.vertexBufferWithNormals.m_253207_(modelViewMatrix, projectionMatrix, this.getClouds());
        matrices.m_85849_();
        VertexBuffer.m_85931_();
        RenderSystem.disableBlend();
        RenderSystem.depthFunc((int)515);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableCull();
        RenderSystem.setShaderFogEnd((float)previousEnd);
        RenderSystem.setShaderFogStart((float)previousStart);
    }

    @ModifyArg(method={"rebuildGeometry"}, at=@At(value="INVOKE", target="Lorg/lwjgl/system/MemoryStack;nmalloc(I)J"), remap=false)
    private int allocateNewSize(int size) {
        return IrisApi.getInstance().isShaderPackInUse() ? 480 : size;
    }

    @ModifyArg(method={"rebuildGeometry"}, at=@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/api/vertex/buffer/VertexBufferWriter;push(Lorg/lwjgl/system/MemoryStack;JILnet/caffeinemc/mods/sodium/api/vertex/format/VertexFormatDescription;)V"), index=3, remap=false)
    private VertexFormatDescription modifyArgIris(VertexFormatDescription vertexFormatDescription) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            return CloudVertex.FORMAT;
        }
        return ColorVertex.FORMAT;
    }

    private ShaderInstance getClouds() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof ShaderRenderingPipeline) {
            return ((ShaderRenderingPipeline)pipeline).getShaderMap().getShader(ShaderKey.CLOUDS_SODIUM);
        }
        return this.shader;
    }
}

