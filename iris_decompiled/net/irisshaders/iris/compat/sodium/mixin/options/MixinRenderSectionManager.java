/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gui.SodiumGameOptions$PerformanceSettings
 *  me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.options;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import net.irisshaders.iris.Iris;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderSectionManager.class})
public class MixinRenderSectionManager {
    @Redirect(method={"getSearchDistance"}, remap=false, at=@At(value="FIELD", target="Lme/jellysquid/mods/sodium/client/gui/SodiumGameOptions$PerformanceSettings;useFogOcclusion:Z", remap=false))
    private boolean iris$disableFogOcclusion(SodiumGameOptions.PerformanceSettings settings) {
        if (Iris.getCurrentPack().isPresent()) {
            return false;
        }
        return settings.useFogOcclusion;
    }
}

