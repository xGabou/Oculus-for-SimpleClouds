package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SimpleCloudsCompatHelper.class)
public class SimpleCloudsCompatHelperMixin {
    @Redirect(
            method = "findCompatErrors",
            at = @At(value = "INVOKE", target = "Ldev/nonamecrackers2/simpleclouds/SimpleCloudsMod;dhLoaded()Z")
    )
    private static boolean oculus_for_simpleclouds$skipOculusDhErrorWhenAddonLoaded() {
        if (ModList.get().isLoaded("simplecloudsoculus")) {
            return false;
        }
        return SimpleCloudsMod.dhLoaded();
    }
}
