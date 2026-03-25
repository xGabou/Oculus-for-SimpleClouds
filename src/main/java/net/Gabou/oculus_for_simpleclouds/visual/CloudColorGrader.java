package net.Gabou.oculus_for_simpleclouds.visual;

import net.minecraft.client.Minecraft;

public final class CloudColorGrader {
    private static final float HORIZON_HEIGHT_RANGE = 0.35F;
    private static final float COLORIZE_AMOUNT_MAX = 0.52F;
    private static final float WARM_AMOUNT_MAX = 0.48F;
    private static final float CONTRAST_BOOST_MAX = 0.16F;
    private static final float SATURATION_BOOST_MAX = 0.18F;
    private static final float BRIGHTNESS_GAIN_MAX = 0.12F;

    private static final float WARM_R = 1.08F;
    private static final float WARM_G = 0.96F;
    private static final float WARM_B = 0.86F;

    private CloudColorGrader() {
    }

    public static float[] grade(Minecraft mc, float partialTick, float r, float g, float b) {
        if (mc == null || mc.level == null) {
            return new float[]{r, g, b};
        }

        float timeOfDay = mc.level.getTimeOfDay(partialTick);
        float sunAngle = timeOfDay * (float)(Math.PI * 2.0);
        float sunHeight = (float) Math.sin(sunAngle);

        float horizon = 1.0F - Math.min(1.0F, Math.abs(sunHeight) / HORIZON_HEIGHT_RANGE);
        horizon = smoothstep(0.0F, 1.0F, horizon);

        float[] sunriseColor = mc.level.effects().getSunriseColor(timeOfDay, partialTick);
        float sunriseStrength = sunriseColor != null && sunriseColor.length >= 4 ? clamp01(sunriseColor[3]) : 0.0F;

        float tintR = WARM_R;
        float tintG = WARM_G;
        float tintB = WARM_B;
        if (sunriseColor != null && sunriseColor.length >= 3) {
            tintR = lerp(tintR, clamp01(0.82F + sunriseColor[0] * 0.38F), sunriseStrength);
            tintG = lerp(tintG, clamp01(0.78F + sunriseColor[1] * 0.24F), sunriseStrength);
            tintB = lerp(tintB, clamp01(0.72F + sunriseColor[2] * 0.18F), sunriseStrength);
        }

        float baseLuma = luminance(r, g, b);
        float colorizeAmount = horizon * lerp(0.20F, COLORIZE_AMOUNT_MAX, sunriseStrength);
        float warmAmount = horizon * lerp(0.18F, WARM_AMOUNT_MAX, sunriseStrength);
        float contrast = 1.0F + horizon * lerp(0.06F, CONTRAST_BOOST_MAX, sunriseStrength);
        float saturation = 1.0F + horizon * lerp(0.05F, SATURATION_BOOST_MAX, sunriseStrength);
        float brightnessGain = 1.0F + horizon * lerp(0.02F, BRIGHTNESS_GAIN_MAX, sunriseStrength);

        float rr = lerp(r, baseLuma * tintR, colorizeAmount);
        float gg = lerp(g, baseLuma * tintG, colorizeAmount);
        float bb = lerp(b, baseLuma * tintB, colorizeAmount);

        rr = lerp(rr, rr * tintR, warmAmount);
        gg = lerp(gg, gg * tintG, warmAmount);
        bb = lerp(bb, bb * tintB, warmAmount);

        rr = (rr - 0.5F) * contrast + 0.5F;
        gg = (gg - 0.5F) * contrast + 0.5F;
        bb = (bb - 0.5F) * contrast + 0.5F;

        rr *= brightnessGain;
        gg *= brightnessGain;
        bb *= brightnessGain;

        float gradedLuma = luminance(rr, gg, bb);
        rr = gradedLuma + (rr - gradedLuma) * saturation;
        gg = gradedLuma + (gg - gradedLuma) * saturation;
        bb = gradedLuma + (bb - gradedLuma) * saturation;

        // RenderDoc validation:
        // 1) Capture a frame at sunrise.
        // 2) Find the draw using the Simple Clouds program.
        // 3) Compare before/after cloud color multiplier or final cloud color attachment.
        // 4) Expect warmer tones near sunrise/sunset and near-original color at midday.
        return new float[]{clamp01(rr), clamp01(gg), clamp01(bb)};
    }

    private static float smoothstep(float edge0, float edge1, float x) {
        if (edge0 == edge1) {
            return x < edge0 ? 0.0F : 1.0F;
        }
        float t = clamp01((x - edge0) / (edge1 - edge0));
        return t * t * (3.0F - 2.0F * t);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float luminance(float r, float g, float b) {
        return r * 0.2126F + g * 0.7152F + b * 0.0722F;
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
