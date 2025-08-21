package net.Gabou.oculus_for_simpleclouds;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;

/**
 * Registers a weather provider for Iris when Oculus is installed.
 */
public final class SimpleCloudsIrisWeatherCompat {
    private SimpleCloudsIrisWeatherCompat() {
    }

    /**
     * Initializes the Iris weather compatibility hook.
     */
    public static void init() {
        if (!CompatHelper.isOculusLoaded()) {
            return;
        }

        IrisWeatherApi.setProvider(new IrisWeatherApi.Provider() {
            @Override
            public float getRainStrength(ClientLevel level, float tickDelta) {
                Minecraft mc = Minecraft.getInstance();
                Vec3 pos = mc.gameRenderer.getMainCamera().getPosition();
                CloudManager<?> manager = CloudManager.get(level);
                if (!manager.shouldUseVanillaWeather()) {
                    return manager.getRainLevel((float) pos.x, (float) pos.y, (float) pos.z);
                }
                return level.getRainLevel(tickDelta);
            }

            @Override
            public float getThunderStrength(ClientLevel level, float tickDelta) {
                return SimpleCloudsRenderer.getOptionalInstance()
                        .map(SimpleCloudsRenderer::getWorldEffectsManager)
                        .map(effects -> effects.getStorminessSmoothed(tickDelta))
                        .orElse(level.getThunderLevel(tickDelta));
            }
        });
    }
}