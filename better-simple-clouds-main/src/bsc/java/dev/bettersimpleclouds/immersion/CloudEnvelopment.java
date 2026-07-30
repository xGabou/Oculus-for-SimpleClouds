package dev.bettersimpleclouds.immersion;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;

/**
 * Computes how thoroughly the camera is wrapped inside a Simple Clouds cloud &mdash; a single smooth
 * {@code envelopment} value in {@code [0,1]} that drives the whole in-cloud look.
 *
 * <p>Simple Clouds meshes its clouds on the GPU, so there is no per-point density to query on the CPU. Instead we
 * combine the two signals the mod already maintains every frame, both cheap and stable:</p>
 * <ul>
 *   <li><b>Horizontal</b> &mdash; {@link WorldEffects#getCloudTypeAtCamera()} /
 *       {@link WorldEffects#getFadeRegionAtCamera()} tell us whether the camera column sits under a cloud region
 *       and how far from that region's soft edge (0 at the edge, 1 deep inside).</li>
 *   <li><b>Vertical</b> &mdash; a soft band around {@link CloudManager#getCloudHeight()}: clouds occupy roughly
 *       {@code [base, base + thickness]} (cloud cells are 8 blocks tall). We ramp in/out over a soft margin so
 *       entering and leaving the layer fades rather than snaps.</li>
 * </ul>
 *
 * <p>The product of the two is then eased toward over time ({@link #tick()}), so flying through wispy gaps or
 * across a region edge never pops the effect on or off.</p>
 */
public final class CloudEnvelopment {

    /** Cloud cells are 8 blocks tall in world space (see {@code SimpleCloudsRenderer#translateClouds}). */
    private static final float CELL_BLOCKS = 8.0F;

    private float smoothed;
    private float smoothedPrev;

    // --- last-computed values, for the debug overlay only ---
    private float dbgHorizontal;
    private float dbgVertical;
    private float dbgTarget;
    private float dbgCamY;
    private int dbgCloudBase;
    private float dbgBottom;
    private float dbgTop;
    private float storminess;
    private String dbgType = "-";

    /** Per-tick easing toward the instantaneous target, so crossings fade instead of snapping. */
    public void tick() {
        this.smoothedPrev = this.smoothed;
        final float target = instantaneous();
        // ~0.18/tick rise & fall: full fade in well under a second, no visible pop.
        this.smoothed += (target - this.smoothed) * 0.18F;
        if (this.smoothed < 1.0E-4F)
            this.smoothed = 0.0F;
    }

    /** One-line diagnostic of what the detector currently sees (for the debug overlay). */
    public String debugLine(final float partialTick) {
        return String.format(
            "In-Cloud: env=%.2f  storm=%.2f  (h=%.2f v=%.2f)  camY=%.0f  cloudLayer=[%.0f..%.0f]  type=%s",
            envelopment(partialTick), this.storminess, this.dbgHorizontal, this.dbgVertical, this.dbgCamY,
            this.dbgBottom, this.dbgTop, this.dbgType);
    }

    public void reset() {
        this.smoothed = 0.0F;
        this.smoothedPrev = 0.0F;
    }

    /** Interpolated envelopment for this frame, in {@code [0,1]}. */
    public float envelopment(final float partialTick) {
        return Mth.clamp(Mth.lerp(partialTick, this.smoothedPrev, this.smoothed), 0.0F, 1.0F);
    }

    /** True once there is anything to draw this frame. */
    public boolean active(final float partialTick) {
        return envelopment(partialTick) > 1.0E-3F;
    }

    /** Storminess of the cloud currently at the camera, {@code [0,1]} (0 for calm/normal clouds). */
    public float storminess() {
        return this.storminess;
    }

    /**
     * Instantaneous (un-smoothed) envelopment measured at an <em>arbitrary</em> world position, querying the
     * cloud state directly rather than through {@link WorldEffects}'s cached at-camera fields. Same formula as
     * {@link #instantaneous()}; touches no instance or debug state, so callers (e.g. compatibility patches
     * measuring an Immersive Portals portal camera) can probe freely without disturbing the live detector.
     */
    public static float instantaneousAt(final double x, final double y, final double z) {
        final SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getOptionalInstance().orElse(null);
        if (renderer == null)
            return 0.0F;
        final ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return 0.0F;

        final CloudManager<ClientLevel> manager = CloudManager.get(level);
        final var result = manager.getCloudTypeAtWorldPos((float) x, (float) z);
        final CloudType type = result.getLeft();
        if (type == null)
            return 0.0F;

        // Identical to instantaneous(): presence = 1 - fade, sharpened; then the soft vertical band over the
        // cloud type's real noise height range.
        final float horizontal = Mth.clamp((1.0F - result.getRight()) * 1.15F, 0.0F, 1.0F);
        if (horizontal <= 0.0F)
            return 0.0F;
        final int cloudBase = manager.getCloudHeight();
        final float bottom = cloudBase + type.noiseConfig().getStartHeight() * CELL_BLOCKS;
        final float top = cloudBase + type.noiseConfig().getEndHeight() * CELL_BLOCKS;
        final float vertical = verticalBand((float) y, bottom, top);
        if (vertical <= 0.0F)
            return 0.0F;
        return horizontal * vertical;
    }

    /** Instantaneous (un-smoothed) target from the current camera position and cloud state; records debug state. */
    private float instantaneous() {
        this.dbgHorizontal = 0.0F;
        this.dbgVertical = 0.0F;
        this.dbgTarget = 0.0F;
        this.storminess = 0.0F;
        this.dbgType = "-";

        final SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getOptionalInstance().orElse(null);
        if (renderer == null)
            return 0.0F;
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        if (level == null || mc.gameRenderer == null)
            return 0.0F;

        final WorldEffects effects = renderer.getWorldEffectsManager();
        if (effects == null)
            return 0.0F;

        final double camY = mc.gameRenderer.getMainCamera().getPosition().y;
        final int cloudBase = CloudManager.get(level).getCloudHeight();
        this.dbgCamY = (float) camY;
        this.dbgCloudBase = cloudBase;

        final CloudType type = effects.getCloudTypeAtCamera();
        this.dbgType = type == null ? "null" : type.id().getPath();
        if (type == null)
            return 0.0F;
        // The cloud type's storm parameter (0 = calm small clouds, ->1 = big storm clouds) drives how busy the
        // in-cloud motes get. Blend in the live storminess at the camera so an active storm is messier still.
        this.storminess = Mth.clamp(Math.max(type.storminess(), effects.getStorminessAtCamera()), 0.0F, 1.0F);

        // Simple Clouds' fade is 0 deep inside a cloud region and 1 at/beyond its soft edge (and 1 in clear sky,
        // where the type is EMPTY), so presence = 1 - fade. Sharpen slightly so edges stay soft but the interior
        // saturates. This also self-zeroes clear sky, so no explicit EMPTY check is needed.
        final float horizontal = Mth.clamp((1.0F - effects.getFadeRegionAtCamera()) * 1.15F, 0.0F, 1.0F);
        this.dbgHorizontal = horizontal;

        // Vertical: the cloud layer's REAL world-Y extent. Simple Clouds renders cloud cells (8 blocks each) from
        // the cloud base up, but each cloud type's noise only fills cells between its start/end height - so the
        // actual cloud sits at cloudBase + [startHeight, endHeight] * 8, which can be hundreds of blocks above the
        // base. Use those real heights instead of guessing a thickness.
        final int startH = type.noiseConfig().getStartHeight();
        final int endH = type.noiseConfig().getEndHeight();
        final float bottom = cloudBase + startH * CELL_BLOCKS;
        final float top = cloudBase + endH * CELL_BLOCKS;
        this.dbgBottom = bottom;
        this.dbgTop = top;

        final float vertical = verticalBand((float) camY, bottom, top);
        this.dbgVertical = vertical;

        if (horizontal <= 0.0F || vertical <= 0.0F)
            return 0.0F;

        final float target = horizontal * vertical;
        this.dbgTarget = target;
        return target;
    }

    /**
     * Soft membership of {@code camY} in the cloud layer {@code [bottom, top]}, with a configurable soft-edge
     * margin so you sink into and rise out of the cloud gradually instead of snapping.
     */
    private static float verticalBand(final float camY, final float bottom, final float top) {
        if (top <= bottom)
            return 0.0F; // empty noise band (e.g. EMPTY cloud type)
        final float margin = (float) BetterSimpleCloudsConfig.inCloudVerticalPaddingBlocks();
        if (camY <= bottom - margin || camY >= top + margin)
            return 0.0F;

        // 1 inside [bottom, top]; fade out over `margin` blocks past each face.
        final float rise = smoothstep(bottom - margin, bottom, camY);
        final float fall = 1.0F - smoothstep(top, top + margin, camY);
        return Mth.clamp(Math.min(rise, fall), 0.0F, 1.0F);
    }

    private static float smoothstep(final float edge0, final float edge1, final float x) {
        final float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    /** The cloud type currently at the camera, or {@code null} if none. */
    @Nullable
    public static CloudType cloudTypeAtCamera() {
        final SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getOptionalInstance().orElse(null);
        if (renderer == null)
            return null;
        final WorldEffects effects = renderer.getWorldEffectsManager();
        return effects == null ? null : effects.getCloudTypeAtCamera();
    }
}
