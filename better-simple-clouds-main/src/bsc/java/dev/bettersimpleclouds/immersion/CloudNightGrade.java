package dev.bettersimpleclouds.immersion;

import com.mojang.blaze3d.shaders.AbstractUniform;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;

/**
 * Feeds the "night legibility" grade into the cloud shaders so Simple Clouds' clouds stop looking like flat dark grey
 * blobs at night.
 *
 * <p>At night Simple Clouds multiplies the cloud colour down to a dark grey ({@code ClientLevel#getCloudColor}), which
 * crushes the per-cube brightness that gives a cloud its shape into the bottom of the range where nothing reads. The
 * cloud shaders ({@code clouds.fsh} / {@code clouds_transparency.fsh}, both Better Simple Clouds copies) undo that in
 * the fragment stage - gamma to open the crushed range, an exposure lift, and a cool moonlit cast - blended in by a
 * {@code MicNight} factor so it's a no-op by day and fades on smoothly at dusk. This sets the two uniforms those
 * shaders read: {@code MicNight} (how "night" it is) and {@code MicNightStrength} (the configured strength, {@code 0}
 * when the feature is off).</p>
 *
 * <p>Both values are global for the frame (time-of-day + config), not per-pass, so feeding them at the head of the
 * opaque and transparent cloud draws covers every use of these shaders. Inert at {@code MicNightStrength = 0} ->
 * the shaders behave exactly like stock.</p>
 */
public final class CloudNightGrade {

    private CloudNightGrade() {}

    /**
     * How "night" it is, {@code [0,1]}: {@code 0} through the day and the sunset moment (so sunset colours are left
     * alone), ramping smoothly to {@code 1} once the sky is actually dark, and back down before sunrise. Keyed off the
     * sun's height (the same celestial angle that drives the vanilla day/night light curve), so it tracks real
     * darkness rather than a raw clock.
     */
    public static float nightness(final ClientLevel level, final float partialTick) {
        // getTimeOfDay drives the sun/moon position: cos(angle) is +1 at noon, 0 around sunrise/sunset, -1 at midnight.
        final float sunHeight = Mth.cos(level.getTimeOfDay(partialTick) * ((float) Math.PI * 2.0F));
        // Ramp in over sunHeight [0 (sunset) .. -0.5 (dark)], then smoothstep for a gentle dusk transition.
        final float t = Mth.clamp(-sunHeight / 0.5F, 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    /**
     * Sets {@code MicNight} and {@code MicNightStrength} on the given cloud shader for this frame. Safe to call
     * unconditionally; pushes the no-op defaults ({@code 0}) when the level is missing or the feature is off, so the
     * shader is left behaving exactly like stock Simple Clouds.
     */
    public static void feed(final SingleSSBOShaderInstance shader, final ClientLevel level, final float partialTick) {
        final AbstractUniform night = shader.safeGetUniform("MicNight");
        final AbstractUniform strength = shader.safeGetUniform("MicNightStrength");
        if (level == null || !BetterSimpleCloudsConfig.cloudNightBoost()) {
            night.set(0.0F);
            strength.set(0.0F);
            return;
        }
        night.set(nightness(level, partialTick));
        strength.set(BetterSimpleCloudsConfig.cloudNightBoostStrength());
    }
}
