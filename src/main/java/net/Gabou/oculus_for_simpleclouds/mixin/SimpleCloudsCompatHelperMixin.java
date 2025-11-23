package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import dev.nonamecrackers2.simpleclouds.client.mesh.RendererInitializeResult;
import net.minecraft.network.chat.Component;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(value = SimpleCloudsCompatHelper.class, remap = false)
public abstract class SimpleCloudsCompatHelperMixin {

    /**
     * Completely replaces SimpleClouds' compat check.
     * Logic:
     *  - If Oculus is loaded AND the addon is NOT loaded → return error.
     *  - Otherwise → return an empty result (no errors).
     * @author Gabou
     * @reason Oculus is now compatible
     */
    @Overwrite
    public static @Nullable RendererInitializeResult findCompatErrors() {

        boolean oculusLoaded = CompatHelper.isOculusLoaded();

        RendererInitializeResult.Builder builder = RendererInitializeResult.builder();

        // The ONLY case where we throw an error:
        if (!oculusLoaded && SimpleCloudsMod.dhLoaded()) {
            builder.addError(
                    null,
                    "Simple Clouds Notice",
                    Component.literal("Oculus detected but Oculus-For-SimpleClouds is required.")
            );
        }

        // Return final result (empty or containing the error)
        return builder.build();
    }
}
