package dev.bettersimpleclouds.immersion;

import com.mojang.blaze3d.shaders.AbstractUniform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;

/**
 * Moonlight scattering <b>through</b> the clouds &mdash; the silver lining.
 *
 * <p>Cloud droplets scatter light overwhelmingly <em>forwards</em>, which is why a cloud drifting in front of the moon
 * lights up along its edge instead of simply blocking it. Simple Clouds draws its clouds as lit cubes with no notion
 * of the moon at all, so a cloud crossing a full moon just goes flat grey. This feeds the cloud shaders a moon
 * direction and an intensity; the fragment stage adds a Henyey-Greenstein forward-scattering lobe around it, so
 * clouds near the moon glow and clouds away from it do not.</p>
 *
 * <p><b>Deliberately self-contained.</b> True Night computes an almost identical moon model, and this could have
 * called into it &mdash; but Better Simple Clouds must not grow a dependency on another mod for a core visual, and
 * True Night is client-side and optional. The two agree because they use the same physics, not because one calls the
 * other. The moon illumination table below is the same standard lunar photometric law; if you change one, change
 * both.</p>
 *
 * <p>Inert at {@code MicMoonGlow = 0} &mdash; by day, in rain, at new moon, or with the option off, the shaders
 * behave exactly like stock.</p>
 */
public final class CloudMoonGlow {

    private CloudMoonGlow() {}

    /**
     * Relative lunar illuminance by phase index, from the standard photometric law
     * {@code m(a) = -12.73 + 0.026|a| + 4e-9 a^4}. Index is {@code getMoonPhase()}: <b>0 = full, 4 = new</b>.
     *
     * <p>Not vanilla's {@code MOON_BRIGHTNESS_PER_PHASE} ({@code 1, .75, .5, .25, 0, ...}): that is the illuminated
     * <em>fraction of the disc</em>, which is linear in phase and wrong by ~10x at the quarters as a measure of light
     * actually delivered. The Moon is not a Lambertian sphere &mdash; a full moon is ~4.4x a quarter moon, not 2x.</p>
     */
    private static final float[] MOON_ILLUM = {
        1.0F, 0.2265F, 0.0910F, 0.0263F, 0.00028F, 0.0263F, 0.0910F, 0.2265F
    };

    /**
     * Phase curve compression. Raw physics leaves five of the eight phases within a third of a stop of nothing, and
     * Minecraft's 8-day cycle makes each step a ~3.7-real-day jump, so the glow would look identical on most nights.
     * {@code 0.8} keeps a full:new ratio of ~600:1 while still separating the middle phases visibly.
     */
    private static final float PHASE_GAMMA = 0.80F;

    /**
     * How much light the glow may add to a cloud at its very peak, in colour units, at {@code 100%} strength.
     *
     * <p><b>This is the knob that stops the effect blowing out.</b> The scattering lobe is normalised to peak at
     * exactly {@code 1.0} looking straight at the moon, so without this the configured strength would be added to
     * the cloud's colour outright &mdash; {@code +0.75} of near-white on a full moon, which does not read as a silver
     * lining, it reads as a hole burned in the cloud deck. A night cloud sits around {@code 0.1}-{@code 0.3}, so
     * {@code 0.20} at the default strength roughly doubles the rim directly in front of the moon and fades within a
     * few degrees, which is what the real effect looks like.</p>
     *
     * <p>It also absorbs a subtlety: the glow is added in <em>both</em> cloud passes, and the translucent fringe
     * wraps the opaque body, so a fragment near the moon can receive it twice (the second time scaled by the
     * fringe's alpha). Budgeting the peak here rather than in the shader keeps that headroom in one documented
     * place instead of splitting it across two files.</p>
     */
    private static final float MAX_PEAK_ADD = 0.20F;

    /** Warm horizon colour: moonlight through a lot of air has had its blue scattered out of it. */
    private static final float LOW_R = 1.00F, LOW_G = 0.66F, LOW_B = 0.42F;
    /** Cool overhead colour, matching the scotopic cast of a high moon. */
    private static final float HIGH_R = 0.82F, HIGH_G = 0.88F, HIGH_B = 1.00F;

    /**
     * Feeds the moon-glow uniforms for this frame. Safe to call unconditionally: pushes {@code MicMoonGlow = 0} (a
     * no-op in the shader) whenever the level is missing, the feature is off, or there is simply no moonlight.
     */
    public static void feed(final SingleSSBOShaderInstance shader, final ClientLevel level, final float partialTick) {
        final AbstractUniform glow = shader.safeGetUniform("MicMoonGlow");
        final AbstractUniform dir = shader.safeGetUniform("MicMoonDir");
        final AbstractUniform colour = shader.safeGetUniform("MicMoonColor");
        final AbstractUniform sharp = shader.safeGetUniform("MicMoonSharpness");
        final AbstractUniform screen = shader.safeGetUniform("MicScreenSize");

        final float strength = BetterSimpleCloudsConfig.cloudMoonGlowStrength();
        if (level == null || strength <= 0.0F) {
            glow.set(0.0F);
            return;
        }

        // The celestial frame puts the sun at +Y and the moon at -Y, then rotates by the time of day. Carrying that
        // through gives the moon's WORLD direction directly, with no matrix needed:
        //   theta = timeOfDay * 2pi;  moon = (sin theta, -cos theta, 0)
        // Sanity: at noon (theta = 0) that is straight down; at midnight (theta = pi) straight up. Note -cos(theta)
        // is exactly the moon's altitude, i.e. the negative of the sun's - the moon is always opposite the sun.
        final float theta = level.getTimeOfDay(partialTick) * ((float) Math.PI * 2.0F);
        final float moonHeight = -Mth.cos(theta);

        final float master = strength * MAX_PEAK_ADD
            * illumination(level.getMoonPhase())
            * extinction(moonHeight)                                  // dims as it nears the horizon
            * smoothstep(-0.04F, 0.10F, moonHeight)                   // gone once it has set
            * (1.0F - level.getRainLevel(partialTick))                // no moon to scatter through an overcast
            * CloudNightGrade.nightness(level, partialTick);          // and none of it by day

        if (master <= 0.001F) {
            glow.set(0.0F);
            return;
        }

        // Passed in WORLD space and transformed by the shader's own ModelViewMat, rather than being converted here.
        // That guarantees the glow lines up with the geometry even if Simple Clouds changes what it puts in that
        // matrix - there is no second copy of the transform to drift out of sync.
        dir.set(Mth.sin(theta), moonHeight, 0.0F);

        final float high = smoothstep(0.0F, 0.35F, moonHeight);
        colour.set(Mth.lerp(high, LOW_R, HIGH_R), Mth.lerp(high, LOW_G, HIGH_G), Mth.lerp(high, LOW_B, HIGH_B));

        sharp.set(BetterSimpleCloudsConfig.cloudMoonGlowSharpness());
        glow.set(master);

        final var window = Minecraft.getInstance().getWindow();
        screen.set((float) window.getWidth(), (float) window.getHeight());
    }

    /** Phase illumination after the configured compression. */
    private static float illumination(final int phase) {
        return (float) Math.pow(MOON_ILLUM[Mth.clamp(phase, 0, 7)], PHASE_GAMMA);
    }

    /**
     * Atmospheric extinction versus altitude (Kasten-Young airmass, normalised to 1 at the zenith). No Lambert
     * cosine: this is light scattering toward the eye, not light landing on the ground.
     */
    private static float extinction(final float moonHeight) {
        final float sinH = Mth.clamp(moonHeight, 0.0F, 1.0F);
        if (sinH <= 0.0F)
            return 0.0F;
        final double hDeg = Math.toDegrees(Math.asin(sinH));
        final double airmass = 1.0 / (sinH + 0.50572 * Math.pow(hDeg + 6.07995, -1.6364));
        final double zenith = 1.0 / (1.0 + 0.50572 * Math.pow(90.0 + 6.07995, -1.6364));
        return (float) Math.exp(-0.25 * (airmass - zenith));
    }

    private static float smoothstep(final float edge0, final float edge1, final float x) {
        final float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }
}
