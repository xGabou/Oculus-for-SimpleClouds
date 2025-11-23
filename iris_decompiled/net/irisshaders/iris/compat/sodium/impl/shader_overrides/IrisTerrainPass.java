/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses
 *  me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass
 */
package net.irisshaders.iris.compat.sodium.impl.shader_overrides;

import me.jellysquid.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;

public enum IrisTerrainPass {
    SHADOW("shadow"),
    SHADOW_CUTOUT("shadow"),
    GBUFFER_SOLID("gbuffers_terrain"),
    GBUFFER_CUTOUT("gbuffers_terrain_cutout"),
    GBUFFER_TRANSLUCENT("gbuffers_water");

    private final String name;

    private IrisTerrainPass(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isShadow() {
        return this == SHADOW || this == SHADOW_CUTOUT;
    }

    public TerrainRenderPass toTerrainPass() {
        switch (this) {
            case SHADOW: 
            case GBUFFER_SOLID: {
                return DefaultTerrainRenderPasses.SOLID;
            }
            case SHADOW_CUTOUT: 
            case GBUFFER_CUTOUT: {
                return DefaultTerrainRenderPasses.CUTOUT;
            }
            case GBUFFER_TRANSLUCENT: {
                return DefaultTerrainRenderPasses.TRANSLUCENT;
            }
        }
        return null;
    }
}

