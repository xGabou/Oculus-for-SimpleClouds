package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = Level.class, remap = false)
public abstract class LevelMixin {

    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    private void pa$overrideRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        Level level = (Level) (Object) this;
        if(!(level instanceof ClientLevel clientLevel)) {
            return;
        }
        CloudManager<ClientLevel> manager = CloudManager.get(clientLevel);
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) {
            return;
        }
        BlockPos pos = player.blockPosition();
        cir.setReturnValue(manager.getRainLevel(pos.getX(), pos.getY(), pos.getZ()));
    }
}
