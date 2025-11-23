/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat
 *  me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder
 *  me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType
 */
package net.irisshaders.iris.compat.sodium.impl.vertex_format.terrain_xhfp;

import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat;
import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkMeshAttribute;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.IrisChunkMeshAttributes;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.IrisGlVertexAttributeFormat;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.terrain_xhfp.XHFPTerrainVertex;

public class XHFPModelVertexType
implements ChunkVertexType {
    public static final int STRIDE = 40;
    public static final GlVertexFormat<ChunkMeshAttribute> VERTEX_FORMAT = GlVertexFormat.builder(ChunkMeshAttribute.class, (int)40).addElement((Enum)ChunkMeshAttribute.POSITION_MATERIAL_MESH, 0, GlVertexAttributeFormat.UNSIGNED_SHORT, 4, false, true).addElement((Enum)ChunkMeshAttribute.COLOR_SHADE, 8, GlVertexAttributeFormat.UNSIGNED_BYTE, 4, true, false).addElement((Enum)ChunkMeshAttribute.BLOCK_TEXTURE, 12, GlVertexAttributeFormat.UNSIGNED_SHORT, 2, false, false).addElement((Enum)ChunkMeshAttribute.LIGHT_TEXTURE, 16, GlVertexAttributeFormat.UNSIGNED_SHORT, 2, false, true).addElement((Enum)IrisChunkMeshAttributes.MID_TEX_COORD, 20, GlVertexAttributeFormat.UNSIGNED_SHORT, 2, false, false).addElement((Enum)IrisChunkMeshAttributes.TANGENT, 24, IrisGlVertexAttributeFormat.BYTE, 4, true, false).addElement((Enum)IrisChunkMeshAttributes.NORMAL, 28, IrisGlVertexAttributeFormat.BYTE, 3, true, false).addElement((Enum)IrisChunkMeshAttributes.BLOCK_ID, 32, IrisGlVertexAttributeFormat.SHORT, 2, false, false).addElement((Enum)IrisChunkMeshAttributes.MID_BLOCK, 36, IrisGlVertexAttributeFormat.BYTE, 4, false, false).build();
    private static final int POSITION_MAX_VALUE = 65536;
    private static final int TEXTURE_MAX_VALUE = 32768;
    private static final float MODEL_ORIGIN = 8.0f;
    private static final float MODEL_RANGE = 32.0f;
    private static final float MODEL_SCALE = 4.8828125E-4f;
    private static final float MODEL_SCALE_INV = 2048.0f;
    private static final float TEXTURE_SCALE = 3.0517578E-5f;

    public static int encodeTexture(float u, float v) {
        return (Math.round(u * 32768.0f) & 0xFFFF) << 0 | (Math.round(v * 32768.0f) & 0xFFFF) << 16;
    }

    static float decodeBlockTexture(short raw) {
        return (float)(raw & 0xFFFF) * 3.0517578E-5f;
    }

    static short encodePosition(float v) {
        return (short)((8.0f + v) * 2048.0f);
    }

    static float decodePosition(short raw) {
        return (float)(raw & 0xFFFF) * 4.8828125E-4f - 8.0f;
    }

    public float getTextureScale() {
        return 3.0517578E-5f;
    }

    public float getPositionScale() {
        return 4.8828125E-4f;
    }

    public float getPositionOffset() {
        return -8.0f;
    }

    public GlVertexFormat<ChunkMeshAttribute> getVertexFormat() {
        return VERTEX_FORMAT;
    }

    public ChunkVertexEncoder getEncoder() {
        return new XHFPTerrainVertex();
    }
}

