package net.Gabou.oculus_for_simpleclouds;

import net.minecraft.client.multiplayer.ClientLevel;
import java.util.Objects;

/**
 * Allows other mods to override the rain and thunder strengths used by Iris shader uniforms.
 *
 * <p>Mods providing localized weather effects should implement {@link Provider} and register it via
 * {@link #setProvider(Provider)} during initialization. For example:</p>
 *
 * <pre>{@code
 * IrisWeatherApi.setProvider(new IrisWeatherApi.Provider() {
 *     public float getRainStrength(ClientLevel level, float tickDelta) {
 *         return SimpleCloudsWeather.getRain(level, tickDelta);
 *     }
 *
 *     public float getThunderStrength(ClientLevel level, float tickDelta) {
 *         return SimpleCloudsWeather.getThunder(level, tickDelta);
 *     }
 * });
 * }</pre>
 */
public final class IrisWeatherApi {
    /**
     * Supplies rain and thunder strengths used by Iris.
     */
    @FunctionalInterface
    public interface Provider {
        /**
         * @return rain strength in the range {@code [0,1]}
         */
        float getRainStrength(ClientLevel level, float tickDelta);

        /**
         * @return thunder strength in the range {@code [0,1]}
         */
        default float getThunderStrength(ClientLevel level, float tickDelta) {
            return level.getThunderLevel(tickDelta);
        }
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

    /**
     * Registers the weather provider to query for rain and thunder strengths.
     *
     * @param provider implementation that supplies localized weather values
     */
    public static void setProvider(Provider provider) {
        IrisWeatherApi.provider = Objects.requireNonNull(provider);
    }

    public static float getRainStrength(ClientLevel level, float tickDelta) {
        return provider.getRainStrength(level, tickDelta);
    }

    public static float getThunderStrength(ClientLevel level, float tickDelta) {
        return provider.getThunderStrength(level, tickDelta);
    }
}