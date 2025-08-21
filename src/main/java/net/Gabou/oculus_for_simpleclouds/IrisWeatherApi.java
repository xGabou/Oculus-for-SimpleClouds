package net.Gabou.oculus_for_simpleclouds;

import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Allows other mods to override the rain and thunder strengths used by Iris shader uniforms.
 */
public final class IrisWeatherApi {
    public interface Provider {
        float getRainStrength(ClientLevel level, float tickDelta);
        float getThunderStrength(ClientLevel level, float tickDelta);
    }

    private static Provider provider = new Provider() {
        @Override
        public float getRainStrength(ClientLevel level, float tickDelta) {
            return level.getRainLevel(tickDelta);
        }

        @Override
        public float getThunderStrength(ClientLevel level, float tickDelta) {
            return level.getThunderLevel(tickDelta);
        }
    };

    private IrisWeatherApi() {
    }

    public static void setProvider(Provider provider) {
        IrisWeatherApi.provider = provider;
    }

    public static float getRainStrength(ClientLevel level, float tickDelta) {
        return provider.getRainStrength(level, tickDelta);
    }

    public static float getThunderStrength(ClientLevel level, float tickDelta) {
        return provider.getThunderStrength(level, tickDelta);
    }
}