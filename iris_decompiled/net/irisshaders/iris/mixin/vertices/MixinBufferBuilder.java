/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferVertexConsumer
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.DefaultedVertexConsumer
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.vertices;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.irisshaders.iris.vertices.BufferBuilderPolygonView;
import net.irisshaders.iris.vertices.ExtendedDataHelper;
import net.irisshaders.iris.vertices.ExtendingBufferBuilder;
import net.irisshaders.iris.vertices.IrisExtendedBufferBuilder;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BufferBuilder.class})
public abstract class MixinBufferBuilder
extends DefaultedVertexConsumer
implements BufferVertexConsumer,
BlockSensitiveBufferBuilder,
ExtendingBufferBuilder,
IrisExtendedBufferBuilder {
    @Unique
    private final BufferBuilderPolygonView polygon = new BufferBuilderPolygonView();
    @Unique
    private final Vector3f normal = new Vector3f();
    @Unique
    private boolean iris$shouldNotExtend;
    @Unique
    private boolean extending;
    @Unique
    private boolean iris$isTerrain;
    @Unique
    private boolean injectNormalAndUV1;
    @Unique
    private int vertexCount;
    @Unique
    private short currentBlock = (short)-1;
    @Unique
    private short currentRenderType = (short)-1;
    @Unique
    private int currentLocalPosX;
    @Unique
    private int currentLocalPosY;
    @Unique
    private int currentLocalPosZ;
    @Shadow
    private ByteBuffer f_85648_;
    @Shadow
    private VertexFormat.Mode f_85657_;
    @Shadow
    private VertexFormat f_85658_;
    @Shadow
    private int f_85652_;
    @Shadow
    @Nullable
    private VertexFormatElement f_85655_;

    @Shadow
    public abstract void m_166779_(VertexFormat.Mode var1, VertexFormat var2);

    @Shadow
    public abstract void m_5586_(int var1, short var2);

    @Shadow
    public abstract void m_5751_();

    @Override
    public void iris$beginWithoutExtending(VertexFormat.Mode drawMode, VertexFormat vertexFormat) {
        this.iris$shouldNotExtend = true;
        this.m_166779_(drawMode, vertexFormat);
        this.iris$shouldNotExtend = false;
    }

    @NotNull
    public VertexConsumer m_7120_(int pBufferVertexConsumer0, int pInt1) {
        return super.m_7120_(pBufferVertexConsumer0, pInt1);
    }

    @ModifyVariable(method={"begin"}, at=@At(value="HEAD"), argsOnly=true)
    private VertexFormat iris$extendFormat(VertexFormat format) {
        this.extending = false;
        this.iris$isTerrain = false;
        this.injectNormalAndUV1 = false;
        if (this.iris$shouldNotExtend || !WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return format;
        }
        if (format == DefaultVertexFormat.f_85811_) {
            this.extending = true;
            this.iris$isTerrain = true;
            this.injectNormalAndUV1 = false;
            return IrisVertexFormats.TERRAIN;
        }
        if (format == DefaultVertexFormat.f_85812_) {
            this.extending = true;
            this.iris$isTerrain = false;
            this.injectNormalAndUV1 = false;
            return IrisVertexFormats.ENTITY;
        }
        if (format == DefaultVertexFormat.f_85820_) {
            this.extending = true;
            this.iris$isTerrain = false;
            this.injectNormalAndUV1 = true;
            return IrisVertexFormats.GLYPH;
        }
        return format;
    }

    @Inject(method={"reset()V"}, at={@At(value="HEAD")})
    private void iris$onReset(CallbackInfo ci) {
        this.vertexCount = 0;
    }

    @Inject(method={"endVertex"}, at={@At(value="HEAD")})
    private void iris$beforeNext(CallbackInfo ci) {
        if (!this.extending) {
            return;
        }
        if (this.injectNormalAndUV1 && this.f_85655_ == DefaultVertexFormat.f_85809_) {
            this.putInt(0, 0);
            this.m_5751_();
        }
        if (this.iris$isTerrain) {
            this.m_5586_(0, this.currentBlock);
            this.m_5586_(2, this.currentRenderType);
        } else {
            this.m_5586_(0, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
            this.m_5586_(2, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
            this.m_5586_(4, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem());
        }
        this.m_5751_();
        this.m_5832_(0, 0.0f);
        this.m_5832_(4, 0.0f);
        this.m_5751_();
        this.putInt(0, 0);
        this.m_5751_();
        if (this.iris$isTerrain) {
            int posIndex = this.f_85652_ - 48;
            float x = this.f_85648_.getFloat(posIndex);
            float y = this.f_85648_.getFloat(posIndex + 4);
            float z = this.f_85648_.getFloat(posIndex + 8);
            this.putInt(0, ExtendedDataHelper.computeMidBlock(x, y, z, this.currentLocalPosX, this.currentLocalPosY, this.currentLocalPosZ));
            this.m_5751_();
        }
        ++this.vertexCount;
        if (this.f_85657_ == VertexFormat.Mode.QUADS && this.vertexCount == 4 || this.f_85657_ == VertexFormat.Mode.TRIANGLES && this.vertexCount == 3) {
            this.fillExtendedData(this.vertexCount);
        }
    }

    @Unique
    private void fillExtendedData(int vertexAmount) {
        int tangentOffset;
        int normalOffset;
        int midVOffset;
        int midUOffset;
        this.vertexCount = 0;
        int stride = this.f_85658_.m_86020_();
        this.polygon.setup(this.f_85648_, this.f_85652_, stride, vertexAmount);
        float midU = 0.0f;
        float midV = 0.0f;
        for (int vertex = 0; vertex < vertexAmount; ++vertex) {
            midU += this.polygon.u(vertex);
            midV += this.polygon.v(vertex);
        }
        midU /= (float)vertexAmount;
        midV /= (float)vertexAmount;
        if (this.iris$isTerrain) {
            midUOffset = 16;
            midVOffset = 12;
            normalOffset = 24;
            tangentOffset = 8;
        } else {
            midUOffset = 14;
            midVOffset = 10;
            normalOffset = 24;
            tangentOffset = 6;
        }
        if (vertexAmount == 3) {
            for (int vertex = 0; vertex < vertexAmount; ++vertex) {
                int packedNormal = this.f_85648_.getInt(this.f_85652_ - normalOffset - stride * vertex);
                int tangent = NormalHelper.computeTangentSmooth(NormI8.unpackX(packedNormal), NormI8.unpackY(packedNormal), NormI8.unpackZ(packedNormal), this.polygon);
                this.f_85648_.putFloat(this.f_85652_ - midUOffset - stride * vertex, midU);
                this.f_85648_.putFloat(this.f_85652_ - midVOffset - stride * vertex, midV);
                this.f_85648_.putInt(this.f_85652_ - tangentOffset - stride * vertex, tangent);
            }
        } else {
            NormalHelper.computeFaceNormal(this.normal, this.polygon);
            int packedNormal = NormI8.pack(this.normal.x, this.normal.y, this.normal.z, 0.0f);
            int tangent = NormalHelper.computeTangent(this.normal.x, this.normal.y, this.normal.z, this.polygon);
            for (int vertex = 0; vertex < vertexAmount; ++vertex) {
                this.f_85648_.putFloat(this.f_85652_ - midUOffset - stride * vertex, midU);
                this.f_85648_.putFloat(this.f_85652_ - midVOffset - stride * vertex, midV);
                this.f_85648_.putInt(this.f_85652_ - normalOffset - stride * vertex, packedNormal);
                this.f_85648_.putInt(this.f_85652_ - tangentOffset - stride * vertex, tangent);
            }
        }
    }

    @Unique
    private void putInt(int i, int value) {
        this.f_85648_.putInt(this.f_85652_ + i, value);
    }

    @Override
    public void beginBlock(short block, short renderType, int localPosX, int localPosY, int localPosZ) {
        this.currentBlock = block;
        this.currentRenderType = renderType;
        this.currentLocalPosX = localPosX;
        this.currentLocalPosY = localPosY;
        this.currentLocalPosZ = localPosZ;
    }

    @Override
    public void endBlock() {
        this.currentBlock = (short)-1;
        this.currentRenderType = (short)-1;
        this.currentLocalPosX = 0;
        this.currentLocalPosY = 0;
        this.currentLocalPosZ = 0;
    }

    @Override
    public VertexFormat iris$format() {
        return this.f_85658_;
    }

    @Override
    public VertexFormat.Mode iris$mode() {
        return this.f_85657_;
    }

    @Override
    public boolean iris$extending() {
        return this.extending;
    }

    @Override
    public boolean iris$isTerrain() {
        return this.iris$isTerrain;
    }

    @Override
    public boolean iris$injectNormalAndUV1() {
        return this.injectNormalAndUV1;
    }

    @Override
    public int iris$vertexCount() {
        return this.vertexCount;
    }

    @Override
    public void iris$incrementVertexCount() {
        ++this.vertexCount;
    }

    @Override
    public void iris$resetVertexCount() {
        this.vertexCount = 0;
    }

    @Override
    public short iris$currentBlock() {
        return this.currentBlock;
    }

    @Override
    public short iris$currentRenderType() {
        return this.currentRenderType;
    }

    @Override
    public int iris$currentLocalPosX() {
        return this.currentLocalPosX;
    }

    @Override
    public int iris$currentLocalPosY() {
        return this.currentLocalPosY;
    }

    @Override
    public int iris$currentLocalPosZ() {
        return this.currentLocalPosZ;
    }
}

