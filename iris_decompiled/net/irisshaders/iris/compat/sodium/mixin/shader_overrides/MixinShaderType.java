/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gl.shader.ShaderType
 *  org.apache.commons.lang3.ArrayUtils
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.compat.sodium.mixin.shader_overrides;

import me.jellysquid.mods.sodium.client.gl.shader.ShaderType;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisShaderTypes;
import net.irisshaders.iris.compat.sodium.mixin.shader_overrides.ShaderTypeAccessor;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ShaderType.class})
public class MixinShaderType {
    @Shadow(remap=false)
    @Final
    @Mutable
    private static ShaderType[] $VALUES;

    static {
        int baseOrdinal = $VALUES.length;
        IrisShaderTypes.GEOMETRY = ShaderTypeAccessor.createShaderType("GEOMETRY", baseOrdinal, 36313);
        IrisShaderTypes.TESS_CONTROL = ShaderTypeAccessor.createShaderType("TESS_CONTROL", baseOrdinal + 1, 36488);
        IrisShaderTypes.TESS_EVAL = ShaderTypeAccessor.createShaderType("TESS_EVAL", baseOrdinal + 2, 36487);
        $VALUES = (ShaderType[])ArrayUtils.addAll((Object[])$VALUES, (Object[])new ShaderType[]{IrisShaderTypes.GEOMETRY, IrisShaderTypes.TESS_CONTROL, IrisShaderTypes.TESS_EVAL});
    }
}

