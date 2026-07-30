package dev.bettersimpleclouds.immersion;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

/**
 * Bridges the in-cloud {@link CloudEnvelopment} signal (computed on the client tick) to the cloud mesh compute shader
 * (fed as uniforms during mesh generation by {@code CloudMeshGeneratorFillMixin}).
 *
 * <p>Simple Clouds builds its cloud mesh on the GPU, so the only place that knows where cloud actually exists is the
 * compute shader. The {@linkplain dev.bettersimpleclouds.immersion.mixin.CloudMeshGeneratorFillMixin
 * interior-fill mixin} reads these values when it sets the {@code MicFill*} uniforms, telling that shader to fill the
 * cloud interior near the camera (translucent haze ramping to an opaque view-distance shell). Values are plain
 * volatiles - written on the client thread, read on the render thread; a frame of staleness is irrelevant since the
 * envelopment eases slowly.</p>
 */
public final class InteriorFillState {

    /** Simple Clouds cells are 8 blocks across; the shader works in cell units. */
    private static final float CELL = 8.0F;

    /** Raw {@code [0,1]} envelopment (how deep inside the cloud), independent of whether the fill is enabled. */
    public static volatile float envelopment;
    /** {@code [0,1]} how deep inside the cloud the camera is, or 0 when the interior fill is disabled. */
    public static volatile float strength;
    /** View distance in cell units (config blocks / 8): translucent haze within, opaque shell just beyond. */
    public static volatile float viewDistCells;
    /** Constant per-cell opacity of the translucent interior haze. */
    public static volatile float hazeOpacity;
    /** {@code 1} to draw the opaque view-distance shell (hard cut-off), {@code 0} for haze only. */
    public static volatile float shell;

    /** Default radius (cell units) within which away-facing cloud faces are kept when Simple Clouds'
     * test-occluded-faces flag is on; past it the shader falls back to camera-facing culling (far away, solid
     * faces just make giant low-LOD cubes). 16 cells = 128 blocks. */
    public static final float DEFAULT_SOLID_FACE_RADIUS_CELLS = 16.0F;

    /** Radius (cell units) for the away-facing-face gate ({@code MicSolidNearCells}). Compatibility patches may
     * raise it - Immersive Portals views the clouds from cameras far outside it, where the gate's fallback culling
     * shows as see-through clouds - and should restore {@link #DEFAULT_SOLID_FACE_RADIUS_CELLS} afterwards. */
    public static volatile float solidFaceRadiusCells = DEFAULT_SOLID_FACE_RADIUS_CELLS;

    private InteriorFillState() {}

    /** Refresh from the current envelopment and config (called each client tick while in a level). */
    public static void update(final float env) {
        envelopment = env;
        strength = BetterSimpleCloudsConfig.inCloudInteriorFillEnabled() ? env : 0.0F;
        viewDistCells = BetterSimpleCloudsConfig.inCloudViewDistanceBlocks() / CELL;
        hazeOpacity = BetterSimpleCloudsConfig.inCloudHazeOpacity();
        shell = BetterSimpleCloudsConfig.inCloudSolidViewShell() ? 1.0F : 0.0F;
    }

    /** Turn the fill off (no level, or the whole effect disabled). */
    public static void disable() {
        envelopment = 0.0F;
        strength = 0.0F;
    }
}
