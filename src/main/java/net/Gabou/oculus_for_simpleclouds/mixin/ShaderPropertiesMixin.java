package net.Gabou.oculus_for_simpleclouds.mixin;

import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderProperties.class)
public class ShaderPropertiesMixin {
    @Inject(
            method = "<init>(Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/option/ShaderPackOptions;Ljava/lang/Iterable;)V",
            at = @At("TAIL")
    )
    private void injectCustomUniforms(String contents,
                                      ShaderPackOptions shaderPackOptions,
                                      Iterable environmentDefines,
                                      CallbackInfo ci) {

        ShaderProperties self = (ShaderProperties) (Object) this;

        CustomUniforms.Builder builder = self.getCustomUniforms();

        // Register ALL your SimpleClouds uniforms here
        builder.addVariable("vec4", "sc_State", "vec4(0, 0, 0, 0)", true);
        builder.addVariable("vec4", "sc_Type", "vec4(0, 0, 0, 0)", true);
        builder.addVariable("float", "sc_CloudShadowFactor", "0.0", true);
        // Sampler uniforms are bound directly in ExtendedShaderMixin.
    }

}
