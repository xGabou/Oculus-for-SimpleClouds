package net.Gabou.oculus_for_simpleclouds.mixin;

import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShaderProperties.class, remap = false)
public class ShaderPropertiesMixin {
    @Inject(
            method = "<init>(Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/option/ShaderPackOptions;Ljava/lang/Iterable;)V",
            at = @At("TAIL")
    )
    private void injectCustomUniforms(String contents,
                                      ShaderPackOptions shaderPackOptions,
                                      Iterable environmentDefines,
                                      CallbackInfo ci) {

        // SimpleClouds uniforms are declared by the shader bridge and updated
        // by CommonUniformsMixin. Registering the same names here as custom
        // uniforms makes Oculus report duplicate uniforms in every program.
    }

}
