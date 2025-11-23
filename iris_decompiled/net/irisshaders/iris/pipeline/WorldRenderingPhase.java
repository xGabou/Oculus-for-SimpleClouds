/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.iris.pipeline;

import net.minecraft.client.renderer.RenderType;

public enum WorldRenderingPhase {
    NONE,
    SKY,
    SUNSET,
    CUSTOM_SKY,
    SUN,
    MOON,
    STARS,
    VOID,
    TERRAIN_SOLID,
    TERRAIN_CUTOUT_MIPPED,
    TERRAIN_CUTOUT,
    ENTITIES,
    BLOCK_ENTITIES,
    DESTROY,
    OUTLINE,
    DEBUG,
    HAND_SOLID,
    TERRAIN_TRANSLUCENT,
    TRIPWIRE,
    PARTICLES,
    CLOUDS,
    RAIN_SNOW,
    WORLD_BORDER,
    HAND_TRANSLUCENT;


    public static WorldRenderingPhase fromTerrainRenderType(RenderType renderType) {
        if (renderType == RenderType.m_110451_()) {
            return TERRAIN_SOLID;
        }
        if (renderType == RenderType.m_110463_()) {
            return TERRAIN_CUTOUT;
        }
        if (renderType == RenderType.m_110457_()) {
            return TERRAIN_CUTOUT_MIPPED;
        }
        if (renderType == RenderType.m_110466_()) {
            return TERRAIN_TRANSLUCENT;
        }
        if (renderType == RenderType.m_110503_()) {
            return TRIPWIRE;
        }
        throw new IllegalStateException("Illegal render type!");
    }
}

