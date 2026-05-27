package net.Gabou.oculus_for_simpleclouds.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.renderer.WorldEffects;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldEffects.class, remap = false)
public class SimpleCloudsWorldEffectsWeatherMixin {
    @Shadow
    private CloudType typeAtCamera;
    @Shadow
    private float fadeAtCamera;
    @Shadow
    private float storminessAtCamera;

    @Inject(method = "renderPost", at = @At("TAIL"))
    private void oculus_for_simpleclouds$keepCloudWeatherWhenVanillaWeatherFlagFlips(PoseStack stack, float partialTick, double camX, double camY, double camZ, float scale, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            return;
        }

        CloudManager<ClientLevel> manager = CloudManager.get(level);
        Pair<CloudType, Float> result = manager.getCloudTypeAtWorldPos((float) camX, (float) camZ);
        CloudType type = result.getLeft();
        float edgeFade = result.getRight();
        this.typeAtCamera = type;
        this.fadeAtCamera = edgeFade;

        float verticalFade = 1.0F - Mth.clamp(((float) camY - (type.stormStart() * SimpleCloudsConstants.CLOUD_SCALE + manager.getCloudHeight())) / SimpleCloudsConstants.RAIN_VERTICAL_FADE, 0.0F, 1.0F);
        float horizontalFade = Mth.clamp((1.0F - edgeFade) * 3.0F, 0.0F, 1.0F);
        float forcedStorminess = type.weatherType().causesDarkening() ? type.storminess() * horizontalFade * verticalFade : 0.0F;
        if (forcedStorminess > this.storminessAtCamera) {
            this.storminessAtCamera = forcedStorminess;
        }

        float rainLevel = manager.getRainLevel((float) camX, (float) camY, (float) camZ);
        if (rainLevel > level.getRainLevel(partialTick)) {
            level.setRainLevel(rainLevel);
        }
        if (forcedStorminess > level.getThunderLevel(partialTick)) {
            level.setThunderLevel(forcedStorminess);
        }
    }
}
