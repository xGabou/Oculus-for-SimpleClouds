package net.Gabou.oculus_for_simpleclouds;

import dev.nonamecrackers2.simpleclouds.api.SimpleCloudsAPI;
import dev.nonamecrackers2.simpleclouds.api.common.world.ScAPICloudManager;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;

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

    public record CloudLayerTextureState(
            int textureId,
            int textureUnit,
            int textureSize,
            float worldSpan,
            float originX,
            float originZ,
            float scrollX,
            float scrollZ,
            float cloudHeight
    ) {
    }

    private static final class CloudLayerTexture {
        private static final Logger LOGGER = LogManager.getLogger("oculus_for_simpleclouds/CloudLayerTexture");
        private static final int MASK_RESOLUTION = 1024;
        private static final float SHADOW_WORLD_SPAN = 2048.0F;
        private static final long UPDATE_INTERVAL_MS = 50L;
        private static final int PREFERRED_TEXTURE_UNIT = 12;
        private static final float MIN_SCROLL_DELTA = (SHADOW_WORLD_SPAN / MASK_RESOLUTION) * 0.25F;

        private static int textureId = -1;
        private static int framebufferId = -1;
        private static int shaderProgram = -1;
        private static int quadVao = -1;
        private static int textureUnit = -1;
        private static long lastUploadMs;
        private static float lastScrollX = Float.NaN;
        private static float lastScrollZ = Float.NaN;
        private static float lastOriginX = Float.NaN;
        private static float lastOriginZ = Float.NaN;
        private static float lastTickDelta = Float.NaN;
        private static boolean valid;
        private static boolean warnedTextureUnitRange;

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
            if (!ensureResources()) {
                valid = false;
                return Optional.empty();
            }
            CloudManager<ClientLevel> manager = managerOpt.get();
            float tickDelta = CapturedRenderingState.INSTANCE.getTickDelta();
            Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
            float currentScrollX = manager.getScrollX(tickDelta);
            float currentScrollZ = manager.getScrollZ(tickDelta);
            float originX = alignOrigin((float) cameraPos.x);
            float originZ = alignOrigin((float) cameraPos.z);
            long now = System.currentTimeMillis();
            if (shouldUpdate(now, tickDelta, currentScrollX, currentScrollZ, originX, originZ)) {
                if (!renderMask(originX, originZ, currentScrollX, currentScrollZ)) {
                    valid = false;
                    return Optional.empty();
                }
                lastUploadMs = now;
                lastScrollX = currentScrollX;
                lastScrollZ = currentScrollZ;
                lastOriginX = originX;
                lastOriginZ = originZ;
                lastTickDelta = tickDelta;
                valid = true;
            }
            if (!valid) {
                return Optional.empty();
            }
            int unit = resolveTextureUnit();
            bindTextureUnit(unit);
            return Optional.of(new CloudLayerTextureState(
                    textureId,
                    unit,
                    MASK_RESOLUTION,
                    SHADOW_WORLD_SPAN,
                    lastOriginX,
                    lastOriginZ,
                    lastScrollX,
                    lastScrollZ,
                    manager.getCloudHeight()
            ));
        }

        private static boolean shouldUpdate(long now, float tickDelta, float scrollX, float scrollZ, float originX, float originZ) {
            if (!valid || Float.isNaN(lastScrollX) || Float.isNaN(lastScrollZ) || Float.isNaN(lastOriginX) || Float.isNaN(lastOriginZ)) {
                return true;
            }
            boolean originChanged = originX != lastOriginX || originZ != lastOriginZ;
            float dx = scrollX - lastScrollX;
            float dz = scrollZ - lastScrollZ;
            boolean scrollMoved = dx * dx + dz * dz >= MIN_SCROLL_DELTA * MIN_SCROLL_DELTA;
            boolean tickDeltaMoved = Float.isNaN(lastTickDelta) || Math.abs(tickDelta - lastTickDelta) >= 0.01F;
            boolean intervalElapsed = now - lastUploadMs >= UPDATE_INTERVAL_MS;
            return originChanged || (intervalElapsed && (scrollMoved || tickDeltaMoved));
        }

        private static float alignOrigin(float cameraCoordinate) {
            float texelSize = SHADOW_WORLD_SPAN / (float) MASK_RESOLUTION;
            float unaligned = cameraCoordinate - SHADOW_WORLD_SPAN * 0.5F;
            return (float) Math.floor(unaligned / texelSize) * texelSize;
        }

        private static boolean ensureResources() {
            int previousFramebuffer = GL11C.glGetInteger(GL30C.GL_FRAMEBUFFER_BINDING);
            int previousTexture = GL11C.glGetInteger(GL11C.GL_TEXTURE_BINDING_2D);
            try {
                if (textureId == -1) {
                    textureId = GL11C.glGenTextures();
                    GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureId);
                    GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
                    GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
                    GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
                    GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
                    GL11C.glTexImage2D(
                            GL11C.GL_TEXTURE_2D,
                            0,
                            GL30C.GL_R32F,
                            MASK_RESOLUTION,
                            MASK_RESOLUTION,
                            0,
                            GL11C.GL_RED,
                            GL11C.GL_FLOAT,
                            (java.nio.ByteBuffer) null
                    );
                }
                if (framebufferId == -1) {
                    framebufferId = GL30C.glGenFramebuffers();
                    GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, framebufferId);
                    GL30C.glFramebufferTexture2D(
                            GL30C.GL_FRAMEBUFFER,
                            GL30C.GL_COLOR_ATTACHMENT0,
                            GL11C.GL_TEXTURE_2D,
                            textureId,
                            0
                    );
                    int status = GL30C.glCheckFramebufferStatus(GL30C.GL_FRAMEBUFFER);
                    if (status != GL30C.GL_FRAMEBUFFER_COMPLETE) {
                        LOGGER.error("Cloud shadow mask FBO is incomplete (status={})", status);
                        return false;
                    }
                }
                if (shaderProgram == -1) {
                    shaderProgram = createShaderProgram();
                    if (shaderProgram == -1) {
                        return false;
                    }
                }
                if (quadVao == -1) {
                    quadVao = GL30C.glGenVertexArrays();
                }
                return true;
            } finally {
                GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, previousFramebuffer);
                GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, previousTexture);
            }
        }

        private static boolean renderMask(float originX, float originZ, float scrollX, float scrollZ) {
            int previousFramebuffer = GL11C.glGetInteger(GL30C.GL_FRAMEBUFFER_BINDING);
            int previousProgram = GL11C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
            int previousVao = GL11C.glGetInteger(GL30C.GL_VERTEX_ARRAY_BINDING);
            int previousActiveTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
            int previousTexture = GL11C.glGetInteger(GL11C.GL_TEXTURE_BINDING_2D);
            int[] viewport = new int[4];
            GL11C.glGetIntegerv(GL11C.GL_VIEWPORT, viewport);
            boolean blendEnabled = GL11C.glIsEnabled(GL11C.GL_BLEND);
            boolean depthTestEnabled = GL11C.glIsEnabled(GL11C.GL_DEPTH_TEST);
            boolean cullEnabled = GL11C.glIsEnabled(GL11C.GL_CULL_FACE);
            boolean scissorEnabled = GL11C.glIsEnabled(GL11C.GL_SCISSOR_TEST);

            try {
                GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, framebufferId);
                GL11C.glViewport(0, 0, MASK_RESOLUTION, MASK_RESOLUTION);
                GL11C.glDisable(GL11C.GL_BLEND);
                GL11C.glDisable(GL11C.GL_DEPTH_TEST);
                GL11C.glDisable(GL11C.GL_CULL_FACE);
                GL11C.glDisable(GL11C.GL_SCISSOR_TEST);
                GL20C.glUseProgram(shaderProgram);
                setUniform2f(shaderProgram, "uOriginWorldXZ", originX, originZ);
                setUniform1f(shaderProgram, "uWorldSpan", SHADOW_WORLD_SPAN);
                setUniform2f(shaderProgram, "uScrollXZ", scrollX, scrollZ);
                GL30C.glBindVertexArray(quadVao);
                GL11C.glDrawArrays(GL11C.GL_TRIANGLES, 0, 3);
                return true;
            } catch (RuntimeException ex) {
                LOGGER.error("Failed rendering cloud shadow mask texture", ex);
                return false;
            } finally {
                GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, previousFramebuffer);
                GL20C.glUseProgram(previousProgram);
                GL30C.glBindVertexArray(previousVao);
                GL13C.glActiveTexture(previousActiveTexture);
                GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, previousTexture);
                GL11C.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
                restoreState(GL11C.GL_BLEND, blendEnabled);
                restoreState(GL11C.GL_DEPTH_TEST, depthTestEnabled);
                restoreState(GL11C.GL_CULL_FACE, cullEnabled);
                restoreState(GL11C.GL_SCISSOR_TEST, scissorEnabled);
            }
        }

        private static int createShaderProgram() {
            int vertexShader = compileShader(GL20C.GL_VERTEX_SHADER, """
                    #version 330 core
                    const vec2 positions[3] = vec2[](
                        vec2(-1.0, -1.0),
                        vec2( 3.0, -1.0),
                        vec2(-1.0,  3.0)
                    );
                    out vec2 vUv;
                    void main() {
                        vec2 position = positions[gl_VertexID];
                        vUv = position * 0.5 + 0.5;
                        gl_Position = vec4(position, 0.0, 1.0);
                    }
                    """);
            if (vertexShader == -1) {
                return -1;
            }
            int fragmentShader = compileShader(GL20C.GL_FRAGMENT_SHADER, """
                    #version 330 core
                    in vec2 vUv;
                    layout(location = 0) out float outMask;

                    uniform vec2 uOriginWorldXZ;
                    uniform float uWorldSpan;
                    uniform vec2 uScrollXZ;

                    float hash21(vec2 p) {
                        p = fract(p * vec2(123.34, 345.45));
                        p += dot(p, p + 34.345);
                        return fract(p.x * p.y);
                    }

                    float noise(vec2 p) {
                        vec2 i = floor(p);
                        vec2 f = fract(p);
                        float a = hash21(i);
                        float b = hash21(i + vec2(1.0, 0.0));
                        float c = hash21(i + vec2(0.0, 1.0));
                        float d = hash21(i + vec2(1.0, 1.0));
                        vec2 u = f * f * (3.0 - 2.0 * f);
                        return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
                    }

                    float fbm(vec2 p) {
                        float value = 0.0;
                        float amp = 0.5;
                        float frequency = 1.0;
                        for (int i = 0; i < 4; i++) {
                            value += amp * noise(p * frequency);
                            frequency *= 2.0;
                            amp *= 0.5;
                        }
                        return value;
                    }

                    void main() {
                        vec2 world = uOriginWorldXZ + vUv * uWorldSpan + uScrollXZ;
                        vec2 samplePos = world * 0.00125;
                        float base = fbm(samplePos);
                        float detail = fbm(samplePos * 2.75 + vec2(19.7, -11.3));
                        float coverage = clamp(base * 0.78 + detail * 0.22, 0.0, 1.0);
                        outMask = smoothstep(0.35, 0.75, coverage);
                    }
                    """);
            if (fragmentShader == -1) {
                GL20C.glDeleteShader(vertexShader);
                return -1;
            }
            int program = GL20C.glCreateProgram();
            GL20C.glAttachShader(program, vertexShader);
            GL20C.glAttachShader(program, fragmentShader);
            GL20C.glLinkProgram(program);
            GL20C.glDeleteShader(vertexShader);
            GL20C.glDeleteShader(fragmentShader);
            if (GL20C.glGetProgrami(program, GL20C.GL_LINK_STATUS) == GL11C.GL_FALSE) {
                LOGGER.error("Failed to link cloud shadow mask shader: {}", GL20C.glGetProgramInfoLog(program));
                GL20C.glDeleteProgram(program);
                return -1;
            }
            return program;
        }

        private static int compileShader(int shaderType, String source) {
            int shader = GL20C.glCreateShader(shaderType);
            GL20C.glShaderSource(shader, source);
            GL20C.glCompileShader(shader);
            if (GL20C.glGetShaderi(shader, GL20C.GL_COMPILE_STATUS) == GL11C.GL_FALSE) {
                LOGGER.error("Failed to compile cloud shadow mask shader stage {}: {}", shaderType, GL20C.glGetShaderInfoLog(shader));
                GL20C.glDeleteShader(shader);
                return -1;
            }
            return shader;
        }

        private static void setUniform1f(int program, String name, float value) {
            int location = GL20C.glGetUniformLocation(program, name);
            if (location >= 0) {
                GL20C.glUniform1f(location, value);
            }
        }

        private static void setUniform2f(int program, String name, float x, float y) {
            int location = GL20C.glGetUniformLocation(program, name);
            if (location >= 0) {
                GL20C.glUniform2f(location, x, y);
            }
        }

        private static void restoreState(int capability, boolean enabled) {
            if (enabled) {
                GL11C.glEnable(capability);
            } else {
                GL11C.glDisable(capability);
            }
        }

        private static void bindTextureUnit(int textureUnit) {
            int previousActiveTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
            GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + textureUnit);
            GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureId);
            GL13C.glActiveTexture(previousActiveTexture);
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
            if (PREFERRED_TEXTURE_UNIT < 0 || PREFERRED_TEXTURE_UNIT >= maxUnits) {
                if (!warnedTextureUnitRange) {
                    LOGGER.warn("Preferred texture unit {} is out of range for {} texture units, falling back to unit 0", PREFERRED_TEXTURE_UNIT, maxUnits);
                    warnedTextureUnitRange = true;
                }
                textureUnit = 0;
            } else {
                textureUnit = PREFERRED_TEXTURE_UNIT;
            }
            return textureUnit;
        }
    }
}
