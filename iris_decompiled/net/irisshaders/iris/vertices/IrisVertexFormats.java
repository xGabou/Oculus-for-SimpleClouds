/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  com.mojang.blaze3d.vertex.VertexFormatElement$Type
 *  com.mojang.blaze3d.vertex.VertexFormatElement$Usage
 */
package net.irisshaders.iris.vertices;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.irisshaders.iris.Iris;

public class IrisVertexFormats {
    public static final VertexFormatElement ENTITY_ELEMENT = new VertexFormatElement(11, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.GENERIC, 2);
    public static final VertexFormatElement ENTITY_ID_ELEMENT = new VertexFormatElement(11, VertexFormatElement.Type.USHORT, VertexFormatElement.Usage.UV, 3);
    public static final VertexFormatElement MID_TEXTURE_ELEMENT = new VertexFormatElement(12, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 2);
    public static final VertexFormatElement TANGENT_ELEMENT = new VertexFormatElement(13, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.GENERIC, 4);
    public static final VertexFormatElement MID_BLOCK_ELEMENT = new VertexFormatElement(14, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.GENERIC, 3);
    public static final VertexFormatElement PADDING_SHORT = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.PADDING, 1);
    public static final VertexFormat TERRAIN;
    public static final VertexFormat ENTITY;
    public static final VertexFormat GLYPH;
    public static final VertexFormat CLOUDS;

    private static void debug(VertexFormat format) {
        Iris.logger.info("Vertex format: " + format + " with byte size " + format.m_86020_());
        int byteIndex = 0;
        for (VertexFormatElement element : format.m_86023_()) {
            Iris.logger.info(element + " @ " + byteIndex + " is " + element.m_86041_() + " " + element.m_86048_());
            byteIndex += element.m_86050_();
        }
    }

    static {
        ImmutableMap.Builder terrainElements = ImmutableMap.builder();
        ImmutableMap.Builder entityElements = ImmutableMap.builder();
        ImmutableMap.Builder glyphElements = ImmutableMap.builder();
        ImmutableMap.Builder cloudsElements = ImmutableMap.builder();
        terrainElements.put((Object)"Position", (Object)DefaultVertexFormat.f_85804_);
        terrainElements.put((Object)"Color", (Object)DefaultVertexFormat.f_85805_);
        terrainElements.put((Object)"UV0", (Object)DefaultVertexFormat.f_85806_);
        terrainElements.put((Object)"UV2", (Object)DefaultVertexFormat.f_85808_);
        terrainElements.put((Object)"Normal", (Object)DefaultVertexFormat.f_85809_);
        terrainElements.put((Object)"Padding", (Object)DefaultVertexFormat.f_85810_);
        terrainElements.put((Object)"mc_Entity", (Object)ENTITY_ELEMENT);
        terrainElements.put((Object)"mc_midTexCoord", (Object)MID_TEXTURE_ELEMENT);
        terrainElements.put((Object)"at_tangent", (Object)TANGENT_ELEMENT);
        terrainElements.put((Object)"at_midBlock", (Object)MID_BLOCK_ELEMENT);
        terrainElements.put((Object)"Padding2", (Object)DefaultVertexFormat.f_85810_);
        entityElements.put((Object)"Position", (Object)DefaultVertexFormat.f_85804_);
        entityElements.put((Object)"Color", (Object)DefaultVertexFormat.f_85805_);
        entityElements.put((Object)"UV0", (Object)DefaultVertexFormat.f_85806_);
        entityElements.put((Object)"UV1", (Object)DefaultVertexFormat.f_85807_);
        entityElements.put((Object)"UV2", (Object)DefaultVertexFormat.f_85808_);
        entityElements.put((Object)"Normal", (Object)DefaultVertexFormat.f_85809_);
        entityElements.put((Object)"Padding", (Object)DefaultVertexFormat.f_85810_);
        entityElements.put((Object)"iris_Entity", (Object)ENTITY_ID_ELEMENT);
        entityElements.put((Object)"mc_midTexCoord", (Object)MID_TEXTURE_ELEMENT);
        entityElements.put((Object)"at_tangent", (Object)TANGENT_ELEMENT);
        entityElements.put((Object)"Padding2", (Object)PADDING_SHORT);
        glyphElements.put((Object)"Position", (Object)DefaultVertexFormat.f_85804_);
        glyphElements.put((Object)"Color", (Object)DefaultVertexFormat.f_85805_);
        glyphElements.put((Object)"UV0", (Object)DefaultVertexFormat.f_85806_);
        glyphElements.put((Object)"UV2", (Object)DefaultVertexFormat.f_85808_);
        glyphElements.put((Object)"Normal", (Object)DefaultVertexFormat.f_85809_);
        glyphElements.put((Object)"Padding", (Object)DefaultVertexFormat.f_85810_);
        glyphElements.put((Object)"iris_Entity", (Object)ENTITY_ID_ELEMENT);
        glyphElements.put((Object)"mc_midTexCoord", (Object)MID_TEXTURE_ELEMENT);
        glyphElements.put((Object)"at_tangent", (Object)TANGENT_ELEMENT);
        glyphElements.put((Object)"Padding2", (Object)PADDING_SHORT);
        cloudsElements.put((Object)"Position", (Object)DefaultVertexFormat.f_85804_);
        cloudsElements.put((Object)"Color", (Object)DefaultVertexFormat.f_85805_);
        cloudsElements.put((Object)"Normal", (Object)DefaultVertexFormat.f_85809_);
        cloudsElements.put((Object)"Padding", (Object)DefaultVertexFormat.f_85810_);
        TERRAIN = new VertexFormat(terrainElements.build());
        ENTITY = new VertexFormat(entityElements.build());
        GLYPH = new VertexFormat(glyphElements.build());
        CLOUDS = new VertexFormat(cloudsElements.build());
    }
}

