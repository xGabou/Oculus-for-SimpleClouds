package net.Gabou.oculus_for_simpleclouds.interiorfog;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class InteriorCloudState {
    private static float lastStrength;

    public static float getLastStrength() {
        return lastStrength;
    }

    public static boolean shouldSuppressOpaqueMesh(float partialTick) {
        return false;
    }

    public static boolean shouldSuppressTransparentMesh(float partialTick) {
        return false;
    }

    public static float currentStrength(float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (!InteriorCloudConfig.ENABLED.get() || mc.level == null || mc.gameRenderer == null) {
            return cache(0.0F);
        }

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 pos = camera.getPosition();
        return cache(calculateStrength(mc.level, pos.x, pos.y, pos.z));
    }

    private static float calculateStrength(ClientLevel level, double camX, double camY, double camZ) {
        if (!SimpleCloudsRenderer.canRenderInDimension(level)) {
            return 0.0F;
        }

        CloudManager<?> manager = CloudManager.get(level);
        Pair<CloudType, Float> result = manager.getCloudTypeAtWorldPos((float) camX, (float) camZ);
        CloudType type = result.getLeft();
        if (type == null || SimpleCloudsConstants.EMPTY.id().equals(type.id()) || type.stormStart() <= 0.0F) {
            return 0.0F;
        }

        float horizontal = 1.0F - result.getRight();
        float minHorizontalDensity = InteriorCloudConfig.MIN_HORIZONTAL_DENSITY.get().floatValue();
        horizontal = Mth.clamp((horizontal - minHorizontalDensity) / (1.0F - minHorizontalDensity), 0.0F, 1.0F);
        horizontal = (float) Math.pow(horizontal, InteriorCloudConfig.HORIZONTAL_DENSITY_POWER.get());

        float cloudBottom = manager.getCloudHeight();
        float cloudTop = cloudBottom + type.stormStart() * SimpleCloudsConstants.CLOUD_SCALE;
        float fade = InteriorCloudConfig.VERTICAL_FADE_BLOCKS.get().floatValue();
        float y = (float) camY;
        float bottomFade = smoothstep(cloudBottom - fade, cloudBottom + fade, y);
        float topFade = 1.0F - smoothstep(cloudTop - fade, cloudTop + fade, y);
        float vertical = Mth.clamp(bottomFade * topFade, 0.0F, 1.0F);

        return Mth.clamp(horizontal * vertical, 0.0F, 1.0F);
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = Mth.clamp((value - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    private static float cache(float strength) {
        lastStrength = strength;
        return strength;
    }

    private InteriorCloudState() {
    }
}
