package net.Gabou.oculus_for_simpleclouds;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector4f;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class SimpleCloudsUniforms {
    private static long lastDebugTime = 0L;
    private static final long DEBUG_INTERVAL_MS = 1000; // print once per second

    private SimpleCloudsUniforms() {
    }
    private static void debugPrint(String msg) {
        long now = System.currentTimeMillis();
        if (now - lastDebugTime >= DEBUG_INTERVAL_MS) {
            System.out.println("[OCS DEBUG] " + msg);
            lastDebugTime = now;
        }
    }

    /**
     * Encodes the weather state surrounding the camera into a vec4:
     * <pre>
     * x = WeatherType ordinal (NONE=3, RAIN=2, THUNDER=1, THUNDERSTORM=0)
     * y = Fade towards the center of the current cloud region [0,1]
     * z = Instant storminess at the camera (accounts for altitude and fade)
     * w = Smoothed storminess used by Simple Clouds' post effects
     * </pre>
     */
    public static Vector4f sampleCloudState() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.gameRenderer == null || mc.gameRenderer.getMainCamera() == null) {
            return new Vector4f();
        }

        Optional<CloudManager<ClientLevel>> manager = tryGetManager(level);
        if (manager.isEmpty()) {
            return new Vector4f();
        }
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        Pair<CloudType, Float> info = manager.get().getCloudTypeAtWorldPos((float) cameraPos.x, (float) cameraPos.z);
        CloudType type = info.getLeft();
        float edgeFade =  info.getRight();

        OptionalWorldEffects effects = OptionalWorldEffects.grab();
        float fade = 1.0F - effects.fade().orElse(edgeFade);
        float baseStorminess = effects.instantStorminess()
                .orElseGet(() -> computeStorminess(manager.get(), type, cameraPos, fade));
        float tickDelta = CapturedRenderingState.INSTANCE.getTickDelta();
        float smoothed = effects.smoothedStorminess(tickDelta).orElse(baseStorminess);

        Vector4f out = new Vector4f(
                type.weatherType().ordinal(),
                fade,
                baseStorminess,
                smoothed
        );

        debugPrint("CloudState = " + out);
        return out;

    }

    /**
     * Encodes the structural properties of the cloud the player is currently inside of:
     * <pre>
     * x = Storminess scalar for the cloud type
     * y = Storm start height in blocks above the cloud layer
     * z = Horizontal storm fade distance
     * w = Transparency fade applied by Simple Clouds when near the region edge
     * </pre>
     */
    public static Vector4f sampleCloudType() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.gameRenderer == null || mc.gameRenderer.getMainCamera() == null) {
            return new Vector4f();
        }

        Optional<CloudManager<ClientLevel>> manager = tryGetManager(level);
        if (manager.isEmpty()) {
            return new Vector4f();
        }
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        Pair<CloudType, Float> info = manager.get().getCloudTypeAtWorldPos((float) cameraPos.x, (float) cameraPos.z);
        CloudType type = info.getLeft();


        Vector4f out = new Vector4f(
                type.storminess(),
                type.stormStart(),
                type.stormFadeDistance(),
                type.transparencyFade()
        );

        debugPrint("CloudType = " + out);
        return out;

    }

    private static float computeStorminess(CloudManager<ClientLevel> manager, CloudType type, Vec3 cameraPos, float fade) {
        if (manager.shouldUseVanillaWeather() || !type.weatherType().causesDarkening()) {
            return 0.0F;
        }
        float cameraY = (float) cameraPos.y;
        float cloudCeiling = type.stormStart() * SimpleCloudsConstants.CLOUD_SCALE + manager.getCloudHeight();
        float verticalFade = 1.0F - Mth.clamp((cameraY - cloudCeiling) / SimpleCloudsConstants.RAIN_VERTICAL_FADE, 0.0F, 1.0F);
        float horizontalFade = Mth.clamp((1.0F - fade) * 3.0F, 0.0F, 1.0F);
        return type.storminess() * horizontalFade * verticalFade;
    }

    private record OptionalWorldEffects(Optional<WorldEffects> delegate) {
        static OptionalWorldEffects grab() {
            return new OptionalWorldEffects(SimpleCloudsRenderer.getOptionalInstance()
                    .map(SimpleCloudsRenderer::getWorldEffectsManager));
        }

        Optional<Float> fade() {
            return delegate.map(WorldEffects::getFadeRegionAtCamera);
        }

        Optional<Float> instantStorminess() {
            return delegate.map(WorldEffects::getStorminessAtCamera);
        }

        Optional<Float> smoothedStorminess(float tickDelta) {
            return delegate.map(worldEffects -> worldEffects.getStorminessSmoothed(tickDelta));
        }
    }

    private static Optional<CloudManager<ClientLevel>> tryGetManager(ClientLevel level) {
        try {
            return Optional.ofNullable(CloudManager.get(level));
        } catch (NullPointerException | IllegalStateException ex) {
            return Optional.empty();
        }
    }
}
