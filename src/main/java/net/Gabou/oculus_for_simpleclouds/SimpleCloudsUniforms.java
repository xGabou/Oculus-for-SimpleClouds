package net.Gabou.oculus_for_simpleclouds;

import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.api.common.world.ScAPICloudManager;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
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
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class SimpleCloudsUniforms {
    private static long lastDebugTime = 0L;
    private static final long DEBUG_INTERVAL_MS = 1000; // print once per second

    private SimpleCloudsUniforms() {
    }
    public static void debugPrint(String msg) {
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


        return out;

    }
    private static float smoothstep(float edge0, float edge1, float x) {
        x = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return x * x * (3.0f - 2.0f * x);
    }

    public static float sampleCloudShadow() {
        Vector4f state = sampleCloudState();
        float thick = state.y();
        float storm = state.z();

        // replicate GLSL logic
        float coverage = Math.max(0f, Math.min(1f, thick * 0.65f + storm * 0.45f));
        float shadow = smoothstep(0.25f, 0.85f, coverage);

        return shadow * 0.85f;
    }

    public static Optional<CloudLayerTextureState> prepareCloudLayerTexture() {
        return CloudLayerTexture.prepare();
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
            ScAPICloudManager apiManager = SimpleCloudsAPI.getApi().getCloudManager(level);
            if (apiManager instanceof CloudManager<?> manager) {
                @SuppressWarnings("unchecked")
                CloudManager<ClientLevel> typed = (CloudManager<ClientLevel>) manager;
                return Optional.ofNullable(typed);
            }
        } catch (NullPointerException | IllegalStateException ignored) {
        }
        try {
            return Optional.ofNullable(CloudManager.get(level));
        } catch (NullPointerException | IllegalStateException ex) {
            return Optional.empty();
        }
    }

    public record CloudLayerTextureState(int textureId, int textureUnit) {
    }

    private static final class CloudLayerTexture {
        private static final int GRID_RESOLUTION = 64;
        private static final float SAMPLE_SPAN = 2048.0F;
        private static final long UPDATE_INTERVAL_MS = 50L;
        private static final FloatBuffer BUFFER = BufferUtils.createFloatBuffer(GRID_RESOLUTION * GRID_RESOLUTION);

        private static int textureId = -1;
        private static int textureUnit = -1;
        private static long lastUploadMs;
        private static boolean valid;

        private static Optional<CloudLayerTextureState> prepare() {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.gameRenderer == null || mc.gameRenderer.getMainCamera() == null) {
                valid = false;
                return Optional.empty();
            }
            if (!isHighQualityModeActive()) {
                return Optional.empty();
            }
            Optional<CloudManager<ClientLevel>> managerOpt = tryGetManager(mc.level);
            if (managerOpt.isEmpty()) {
                valid = false;
                return Optional.empty();
            }
            ensureTexture();
            long now = System.currentTimeMillis();
            if (!valid || now - lastUploadMs >= UPDATE_INTERVAL_MS) {
                if (!upload(managerOpt.get(), mc.gameRenderer.getMainCamera().getPosition())) {
                    valid = false;
                    return Optional.empty();
                }
                lastUploadMs = now;
                valid = true;
            }
            if (!valid) {
                return Optional.empty();
            }
            return Optional.of(new CloudLayerTextureState(textureId, resolveTextureUnit()));
        }

        private static void ensureTexture() {
            if (textureId != -1) {
                return;
            }
            textureId = GL11C.glGenTextures();
            GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureId);
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
            GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, 0);
        }

        private static boolean upload(CloudManager<ClientLevel> manager, Vec3 camera) {
            List<CloudRegion> regions = manager.getClouds();
            if (regions.isEmpty()) {
                return false;
            }
            FloatBuffer buffer = BUFFER;
            buffer.clear();
            float halfSpan = SAMPLE_SPAN * 0.5f;
            float startX = (float) camera.x - halfSpan;
            float startZ = (float) camera.z - halfSpan;
            float step = SAMPLE_SPAN / GRID_RESOLUTION;
            for (int z = 0; z < GRID_RESOLUTION; z++) {
                float sampleZ = startZ + z * step;
                for (int x = 0; x < GRID_RESOLUTION; x++) {
                    float sampleX = startX + x * step;
                    float coverage = sampleCoverage(regions, sampleX, sampleZ);
                    buffer.put(coverage);
                }
            }
            buffer.flip();
            GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureId);
            GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, 4);
            GL11C.glTexImage2D(
                    GL11C.GL_TEXTURE_2D,
                    0,
                    GL30C.GL_R32F,
                    GRID_RESOLUTION,
                    GRID_RESOLUTION,
                    0,
                    GL11C.GL_RED,
                    GL11C.GL_FLOAT,
                    buffer
            );
            GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, 0);
            return true;
        }

        private static float sampleCoverage(List<CloudRegion> regions, float worldX, float worldZ) {
            Pair<CloudRegion, Float> result = CloudRegion.calculateAt(regions, worldX, worldZ);
            if (result == null || result.getRight() == null) {
                return 0.0f;
            }
            return Mth.clamp(result.getRight(), 0.0f, 1.0f);
        }

        private static boolean isHighQualityModeActive() {
            return SimpleCloudsRenderer.getOptionalInstance()
                    .flatMap(SimpleCloudsRenderer::getShadowMap)
                    .isPresent();
        }

        private static int resolveTextureUnit() {
            if (textureUnit >= 0) {
                return textureUnit;
            }
            int maxUnits = GL11C.glGetInteger(GL20C.GL_MAX_TEXTURE_IMAGE_UNITS);
            textureUnit = Math.max(0, maxUnits - 1);
            return textureUnit;
        }
    }
}
