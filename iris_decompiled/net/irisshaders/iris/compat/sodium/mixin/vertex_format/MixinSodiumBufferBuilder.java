/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  me.jellysquid.mods.sodium.client.render.vertex.buffer.ExtendedBufferBuilder
 *  me.jellysquid.mods.sodium.client.render.vertex.buffer.SodiumBufferBuilder
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.NormalAttribute
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import me.jellysquid.mods.sodium.client.render.vertex.buffer.ExtendedBufferBuilder;
import me.jellysquid.mods.sodium.client.render.vertex.buffer.SodiumBufferBuilder;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.NormalAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.IrisCommonVertexAttributes;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.SodiumBufferBuilderPolygonView;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.irisshaders.iris.vertices.ExtendedDataHelper;
import net.irisshaders.iris.vertices.IrisExtendedBufferBuilder;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SodiumBufferBuilder.class})
public abstract class MixinSodiumBufferBuilder
implements BlockSensitiveBufferBuilder {
    @Unique
    private static final int ATTRIBUTE_TANGENT_BIT = 1 << IrisCommonVertexAttributes.TANGENT.ordinal();
    @Unique
    private static final int ATTRIBUTE_MID_TEX_COORD_BIT = 1 << IrisCommonVertexAttributes.MID_TEX_COORD.ordinal();
    @Unique
    private static final int ATTRIBUTE_BLOCK_ID_BIT = 1 << IrisCommonVertexAttributes.BLOCK_ID.ordinal();
    @Unique
    private static final int ATTRIBUTE_ENTITY_ID_BIT = 1 << IrisCommonVertexAttributes.ENTITY_ID.ordinal();
    @Unique
    private static final int ATTRIBUTE_MID_BLOCK_BIT = 1 << IrisCommonVertexAttributes.MID_BLOCK.ordinal();
    @Shadow
    @Final
    private static int ATTRIBUTE_NOT_PRESENT;
    @Shadow
    @Final
    private static int ATTRIBUTE_NORMAL_BIT;
    @Unique
    private final SodiumBufferBuilderPolygonView polygon = new SodiumBufferBuilderPolygonView();
    @Unique
    private final Vector3f normal = new Vector3f();
    @Shadow
    @Final
    private ExtendedBufferBuilder builder;
    @Shadow
    private int attributeOffsetPosition;
    @Shadow
    private int attributeOffsetTexture;
    @Shadow
    private int attributeOffsetNormal;
    @Shadow
    private int requiredAttributes;
    @Shadow
    private int writtenAttributes;
    @Unique
    private int attributeOffsetTangent;
    @Unique
    private int attributeOffsetMidTexCoord;
    @Unique
    private int attributeOffsetBlockId;
    @Unique
    private int attributeOffsetEntityId;
    @Unique
    private int attributeOffsetMidBlock;

    @Shadow
    public abstract BufferBuilder getOriginalBufferBuilder();

    @Shadow
    abstract void putNormalAttribute(int var1);

    @Unique
    private void putBlockIdAttribute(short s0, short s1) {
        if (this.attributeOffsetBlockId == ATTRIBUTE_NOT_PRESENT) {
            return;
        }
        long offset = MemoryUtil.memAddress((ByteBuffer)this.builder.sodium$getBuffer(), (int)(this.builder.sodium$getElementOffset() + this.attributeOffsetBlockId));
        MemoryUtil.memPutShort((long)offset, (short)s0);
        MemoryUtil.memPutShort((long)(offset + 2L), (short)s1);
        this.writtenAttributes |= ATTRIBUTE_BLOCK_ID_BIT;
    }

    @Unique
    private void putEntityIdAttribute(short s0, short s1, short s2) {
        if (this.attributeOffsetEntityId == ATTRIBUTE_NOT_PRESENT) {
            return;
        }
        long offset = MemoryUtil.memAddress((ByteBuffer)this.builder.sodium$getBuffer(), (int)(this.builder.sodium$getElementOffset() + this.attributeOffsetEntityId));
        MemoryUtil.memPutShort((long)offset, (short)s0);
        MemoryUtil.memPutShort((long)(offset + 2L), (short)s1);
        MemoryUtil.memPutShort((long)(offset + 4L), (short)s2);
        this.writtenAttributes |= ATTRIBUTE_ENTITY_ID_BIT;
    }

    @Unique
    private void putMidBlockAttribute(int midBlock) {
        if (this.attributeOffsetMidBlock == ATTRIBUTE_NOT_PRESENT) {
            return;
        }
        long offset = MemoryUtil.memAddress((ByteBuffer)this.builder.sodium$getBuffer(), (int)(this.builder.sodium$getElementOffset() + this.attributeOffsetMidBlock));
        MemoryUtil.memPutInt((long)offset, (int)midBlock);
        this.writtenAttributes |= ATTRIBUTE_MID_BLOCK_BIT;
    }

    @Override
    public void beginBlock(short block, short renderType, int localPosX, int localPosY, int localPosZ) {
        ((BlockSensitiveBufferBuilder)this.getOriginalBufferBuilder()).beginBlock(block, renderType, localPosX, localPosY, localPosZ);
    }

    @Override
    public void endBlock() {
        ((BlockSensitiveBufferBuilder)this.getOriginalBufferBuilder()).endBlock();
    }

    @Inject(method={"resetAttributeBindings"}, at={@At(value="RETURN")}, remap=false)
    private void onResetAttributeBindings(CallbackInfo ci) {
        this.attributeOffsetTangent = ATTRIBUTE_NOT_PRESENT;
        this.attributeOffsetMidTexCoord = ATTRIBUTE_NOT_PRESENT;
        this.attributeOffsetBlockId = ATTRIBUTE_NOT_PRESENT;
        this.attributeOffsetEntityId = ATTRIBUTE_NOT_PRESENT;
        this.attributeOffsetMidBlock = ATTRIBUTE_NOT_PRESENT;
    }

    @Inject(method={"updateAttributeBindings"}, at={@At(value="RETURN")}, remap=false)
    private void onUpdateAttributeBindings(VertexFormatDescription desc, CallbackInfo ci) {
        if (desc.containsElement(IrisCommonVertexAttributes.TANGENT)) {
            this.requiredAttributes |= ATTRIBUTE_TANGENT_BIT;
            this.attributeOffsetTangent = desc.getElementOffset(IrisCommonVertexAttributes.TANGENT);
        }
        if (desc.containsElement(IrisCommonVertexAttributes.MID_TEX_COORD)) {
            this.requiredAttributes |= ATTRIBUTE_MID_TEX_COORD_BIT;
            this.attributeOffsetMidTexCoord = desc.getElementOffset(IrisCommonVertexAttributes.MID_TEX_COORD);
        }
        if (desc.containsElement(IrisCommonVertexAttributes.BLOCK_ID)) {
            this.requiredAttributes |= ATTRIBUTE_BLOCK_ID_BIT;
            this.attributeOffsetBlockId = desc.getElementOffset(IrisCommonVertexAttributes.BLOCK_ID);
        }
        if (desc.containsElement(IrisCommonVertexAttributes.ENTITY_ID)) {
            this.requiredAttributes |= ATTRIBUTE_ENTITY_ID_BIT;
            this.attributeOffsetEntityId = desc.getElementOffset(IrisCommonVertexAttributes.ENTITY_ID);
        }
        if (desc.containsElement(IrisCommonVertexAttributes.MID_BLOCK)) {
            this.requiredAttributes |= ATTRIBUTE_MID_BLOCK_BIT;
            this.attributeOffsetMidBlock = desc.getElementOffset(IrisCommonVertexAttributes.MID_BLOCK);
        }
    }

    @Inject(method={"endVertex"}, at={@At(value="HEAD")})
    private void onEndVertex(CallbackInfo ci) {
        IrisExtendedBufferBuilder ext = (IrisExtendedBufferBuilder)this.builder;
        if (!ext.iris$extending()) {
            return;
        }
        if (ext.iris$injectNormalAndUV1() && (this.writtenAttributes & ATTRIBUTE_NORMAL_BIT) == 0) {
            this.putNormalAttribute(0);
        }
        if (ext.iris$isTerrain()) {
            this.putBlockIdAttribute(ext.iris$currentBlock(), ext.iris$currentRenderType());
        } else {
            this.putEntityIdAttribute((short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity(), (short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity(), (short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem());
        }
        this.writtenAttributes |= ATTRIBUTE_MID_TEX_COORD_BIT;
        this.writtenAttributes |= ATTRIBUTE_TANGENT_BIT;
        if (ext.iris$isTerrain()) {
            long offset = MemoryUtil.memAddress((ByteBuffer)this.builder.sodium$getBuffer(), (int)(this.builder.sodium$getElementOffset() + this.attributeOffsetPosition));
            float x = PositionAttribute.getX((long)offset);
            float y = PositionAttribute.getY((long)offset);
            float z = PositionAttribute.getZ((long)offset);
            this.putMidBlockAttribute(ExtendedDataHelper.computeMidBlock(x, y, z, ext.iris$currentLocalPosX(), ext.iris$currentLocalPosY(), ext.iris$currentLocalPosZ()));
        }
        ext.iris$incrementVertexCount();
        VertexFormat.Mode mode = ext.iris$mode();
        int vertexCount = ext.iris$vertexCount();
        if (mode == VertexFormat.Mode.QUADS && vertexCount == 4 || mode == VertexFormat.Mode.TRIANGLES && vertexCount == 3) {
            this.fillExtendedData(vertexCount);
        }
    }

    @Unique
    private void fillExtendedData(int vertexAmount) {
        int vertex;
        IrisExtendedBufferBuilder ext = (IrisExtendedBufferBuilder)this.builder;
        ext.iris$resetVertexCount();
        int stride = ext.iris$format().m_86020_();
        long ptr = MemoryUtil.memAddress((ByteBuffer)this.builder.sodium$getBuffer(), (int)this.builder.sodium$getElementOffset());
        this.polygon.setup(ptr, this.attributeOffsetPosition, this.attributeOffsetTexture, stride, vertexAmount);
        float midU = 0.0f;
        float midV = 0.0f;
        for (vertex = 0; vertex < vertexAmount; ++vertex) {
            midU += this.polygon.u(vertex);
            midV += this.polygon.v(vertex);
        }
        midU /= (float)vertexAmount;
        midV /= (float)vertexAmount;
        if (vertexAmount == 3) {
            for (vertex = 0; vertex < vertexAmount; ++vertex) {
                int packedNormal = NormalAttribute.get((long)(ptr + (long)this.attributeOffsetNormal - (long)stride * (long)vertex));
                int tangent = NormalHelper.computeTangentSmooth(NormI8.unpackX(packedNormal), NormI8.unpackY(packedNormal), NormI8.unpackZ(packedNormal), this.polygon);
                MemoryUtil.memPutFloat((long)(ptr + (long)this.attributeOffsetMidTexCoord - (long)stride * (long)vertex), (float)midU);
                MemoryUtil.memPutFloat((long)(ptr + (long)this.attributeOffsetMidTexCoord + 4L - (long)stride * (long)vertex), (float)midV);
                MemoryUtil.memPutInt((long)(ptr + (long)this.attributeOffsetTangent - (long)stride * (long)vertex), (int)tangent);
            }
        } else {
            NormalHelper.computeFaceNormal(this.normal, this.polygon);
            int packedNormal = NormI8.pack(this.normal.x, this.normal.y, this.normal.z, 0.0f);
            int tangent = NormalHelper.computeTangent(this.normal.x, this.normal.y, this.normal.z, this.polygon);
            for (int vertex2 = 0; vertex2 < vertexAmount; ++vertex2) {
                MemoryUtil.memPutFloat((long)(ptr + (long)this.attributeOffsetMidTexCoord - (long)stride * (long)vertex2), (float)midU);
                MemoryUtil.memPutFloat((long)(ptr + (long)this.attributeOffsetMidTexCoord + 4L - (long)stride * (long)vertex2), (float)midV);
                MemoryUtil.memPutInt((long)(ptr + (long)this.attributeOffsetNormal - (long)stride * (long)vertex2), (int)packedNormal);
                MemoryUtil.memPutInt((long)(ptr + (long)this.attributeOffsetTangent - (long)stride * (long)vertex2), (int)tangent);
            }
        }
    }
}

