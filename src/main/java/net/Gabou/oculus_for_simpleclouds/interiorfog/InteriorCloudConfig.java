package net.Gabou.oculus_for_simpleclouds.interiorfog;

import net.minecraftforge.common.ForgeConfigSpec;

public class InteriorCloudConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue SUPPRESS_MESH;
    public static final ForgeConfigSpec.BooleanValue SUPPRESS_TRANSPARENT_MESH;
    public static final ForgeConfigSpec.BooleanValue MODIFY_FOG;
    public static final ForgeConfigSpec.BooleanValue MODIFY_FOG_COLOR;
    public static final ForgeConfigSpec.DoubleValue MESH_SUPPRESSION_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue FOG_START;
    public static final ForgeConfigSpec.DoubleValue FOG_END;
    public static final ForgeConfigSpec.DoubleValue FOG_COLOR_BLEND;
    public static final ForgeConfigSpec.DoubleValue VERTICAL_FADE_BLOCKS;
    public static final ForgeConfigSpec.DoubleValue MIN_HORIZONTAL_DENSITY;
    public static final ForgeConfigSpec.DoubleValue HORIZONTAL_DENSITY_POWER;
    public static final ForgeConfigSpec.DoubleValue COLOR_RED;
    public static final ForgeConfigSpec.DoubleValue COLOR_GREEN;
    public static final ForgeConfigSpec.DoubleValue COLOR_BLUE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("interior_clouds");

        ENABLED = builder.comment("Master toggle for the interior cloud effect.")
                .define("enabled", true);
        SUPPRESS_MESH = builder.comment("Hide the normal SimpleClouds mesh when the camera is inside the cloud volume.")
                .define("suppressMesh", true);
        SUPPRESS_TRANSPARENT_MESH = builder.comment("Also hide the transparent cloud pass while inside the cloud volume.")
                .define("suppressTransparentMesh", true);
        MODIFY_FOG = builder.comment("Make vanilla terrain fog denser while the camera is inside the cloud volume.")
                .define("modifyFog", true);
        MODIFY_FOG_COLOR = builder.comment("Tint fog toward a soft cloud color while inside the cloud volume.")
                .define("modifyFogColor", true);

        MESH_SUPPRESSION_THRESHOLD = builder.comment("Interior strength needed before the mesh is suppressed. Lower hides sooner near cloud edges.")
                .defineInRange("meshSuppressionThreshold", 0.18D, 0.0D, 1.0D);
        FOG_START = builder.comment("Target fog start in blocks at full interior strength.")
                .defineInRange("fogStart", -4.0D, -64.0D, 256.0D);
        FOG_END = builder.comment("Target fog end in blocks at full interior strength.")
                .defineInRange("fogEnd", 28.0D, 1.0D, 1024.0D);
        FOG_COLOR_BLEND = builder.comment("How strongly the interior effect tints fog color.")
                .defineInRange("fogColorBlend", 0.75D, 0.0D, 1.0D);
        VERTICAL_FADE_BLOCKS = builder.comment("Soft fade distance at the top and bottom of the cloud volume, in blocks.")
                .defineInRange("verticalFadeBlocks", 28.0D, 1.0D, 256.0D);
        MIN_HORIZONTAL_DENSITY = builder.comment("Ignore very weak cloud region edge density before applying interior fog.")
                .defineInRange("minHorizontalDensity", 0.08D, 0.0D, 0.95D);
        HORIZONTAL_DENSITY_POWER = builder.comment("Higher values keep the effect weaker near horizontal cloud edges.")
                .defineInRange("horizontalDensityPower", 1.4D, 0.25D, 8.0D);

        COLOR_RED = builder.comment("Target interior fog red channel.")
                .defineInRange("colorRed", 0.78D, 0.0D, 1.0D);
        COLOR_GREEN = builder.comment("Target interior fog green channel.")
                .defineInRange("colorGreen", 0.82D, 0.0D, 1.0D);
        COLOR_BLUE = builder.comment("Target interior fog blue channel.")
                .defineInRange("colorBlue", 0.86D, 0.0D, 1.0D);

        builder.pop();
        SPEC = builder.build();
    }

    private InteriorCloudConfig() {
    }
}
