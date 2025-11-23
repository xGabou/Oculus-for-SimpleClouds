/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SystemReport
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import java.util.function.Supplier;
import net.irisshaders.iris.Iris;
import net.minecraft.SystemReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SystemReport.class})
public abstract class MixinSystemReport {
    @Shadow
    public abstract void m_143522_(String var1, Supplier<String> var2);

    @Inject(at={@At(value="RETURN")}, method={"<init>"})
    private void fillSystemDetails(CallbackInfo ci) {
        if (Iris.getCurrentPackName() == null) {
            return;
        }
        this.m_143522_("Loaded Shaderpack", () -> {
            StringBuilder sb = new StringBuilder(Iris.getCurrentPackName() + (Iris.isFallback() ? " (fallback)" : ""));
            Iris.getCurrentPack().ifPresent(pack -> {
                sb.append("\n\t\t");
                sb.append(pack.getProfileInfo());
            });
            return sb.toString();
        });
    }
}

