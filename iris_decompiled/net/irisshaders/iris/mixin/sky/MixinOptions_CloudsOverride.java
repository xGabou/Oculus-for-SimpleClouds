/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CloudStatus
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.Options
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.sky;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.properties.CloudSetting;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Options.class}, priority=1010)
public class MixinOptions_CloudsOverride {
    @Shadow
    @Final
    private OptionInstance<Integer> f_92106_;

    @Inject(method={"getCloudsType"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$overrideCloudsType(CallbackInfoReturnable<CloudStatus> cir) {
        if ((Integer)this.f_92106_.m_231551_() < 4) {
            return;
        }
        Iris.getPipelineManager().getPipeline().ifPresent(p -> {
            CloudSetting setting = p.getCloudSetting();
            switch (setting) {
                case OFF: {
                    cir.setReturnValue((Object)CloudStatus.OFF);
                    return;
                }
                case FAST: {
                    cir.setReturnValue((Object)CloudStatus.FAST);
                    return;
                }
                case FANCY: {
                    cir.setReturnValue((Object)CloudStatus.FANCY);
                }
            }
        });
    }
}

