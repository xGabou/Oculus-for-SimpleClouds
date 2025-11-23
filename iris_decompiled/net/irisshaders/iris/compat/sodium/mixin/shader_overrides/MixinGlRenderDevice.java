/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.tessellation.GlPrimitiveType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.shader_overrides;

import me.jellysquid.mods.sodium.client.gl.tessellation.GlPrimitiveType;
import net.irisshaders.iris.vertices.ImmediateState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets={"me.jellysquid.mods.sodium.client.gl.device.GLRenderDevice$ImmediateDrawCommandList"}, remap=false)
public class MixinGlRenderDevice {
    @Redirect(method={"multiDrawElementsBaseVertex"}, at=@At(value="INVOKE", target="Lme/jellysquid/mods/sodium/client/gl/tessellation/GlPrimitiveType;getId()I"))
    private int replaceId(GlPrimitiveType instance) {
        if (ImmediateState.usingTessellation) {
            return 14;
        }
        return instance.getId();
    }
}

