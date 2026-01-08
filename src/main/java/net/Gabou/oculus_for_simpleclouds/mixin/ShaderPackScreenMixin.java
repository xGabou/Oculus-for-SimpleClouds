package net.Gabou.oculus_for_simpleclouds.mixin;

import net.irisshaders.iris.gui.element.ShaderPackSelectionList;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(value = ShaderPackScreen.class, remap = false)
public abstract class ShaderPackScreenMixin {

    @Shadow
    private ShaderPackSelectionList shaderPackList;

    @Unique
    private boolean ocs$skipUnsupportedShaderPrompt;

    @Inject(method = "applyChanges", at = @At("HEAD"), cancellable = true)
    private void ocs$warnUnsupportedShader(CallbackInfo ci) {
        if (this.ocs$skipUnsupportedShaderPrompt) {
            this.ocs$skipUnsupportedShaderPrompt = false;
            return;
        }

        ShaderPackSelectionList.BaseEntry selected = this.shaderPackList.getSelected();
        if (!(selected instanceof ShaderPackSelectionList.ShaderPackEntry entry)) {
            return;
        }

        String packName = entry.getPackName();
        if (packName == null || packName.isBlank() || this.ocs$isAtmosphericPack(packName)) {
            return;
        }

        ShaderPackScreen self = (ShaderPackScreen) (Object) this;
        Minecraft client = Minecraft.getInstance();
        client.setScreen(new ConfirmScreen(
                accepted -> {
                    client.setScreen(self);
                    if (accepted) {
                        this.ocs$skipUnsupportedShaderPrompt = true;
                        self.applyChanges();
                    }
                },
                Component.literal("Unsupported shader selected"),
                Component.literal("You have selected a shader that is not supported. Only Atmospheric Shaders is compatible. Any other shader will behave exactly as if shaders were enabled without Oculus for Simple Clouds. By choosing Yes, you accept that visual issues will occur and agree not to report them since you are intentionally using an unsupported shader."),
                Component.literal("Yes"),
                Component.literal("No")
        ));
    }

    @Unique
    private boolean ocs$isAtmosphericPack(String packName) {
        return packName.toLowerCase(Locale.ROOT).contains("atmosphericshaders");
    }
}
