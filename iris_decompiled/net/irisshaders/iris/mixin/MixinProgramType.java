/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Program$Type
 *  org.apache.commons.lang3.ArrayUtils
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.shaders.Program;
import net.irisshaders.iris.gl.program.IrisProgramTypes;
import net.irisshaders.iris.mixin.ProgramTypeAccessor;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Program.Type.class})
public class MixinProgramType {
    @Shadow
    @Final
    @Mutable
    private static Program.Type[] $VALUES;

    static {
        int baseOrdinal = $VALUES.length;
        IrisProgramTypes.GEOMETRY = ProgramTypeAccessor.createProgramType("GEOMETRY", baseOrdinal, "geometry", ".gsh", 36313);
        IrisProgramTypes.TESS_CONTROL = ProgramTypeAccessor.createProgramType("TESS_CONTROL", baseOrdinal + 1, "tess_control", ".tcs", 36488);
        IrisProgramTypes.TESS_EVAL = ProgramTypeAccessor.createProgramType("TESS_EVAL", baseOrdinal + 2, "tess_eval", ".tes", 36487);
        $VALUES = (Program.Type[])ArrayUtils.addAll((Object[])$VALUES, (Object[])new Program.Type[]{IrisProgramTypes.GEOMETRY, IrisProgramTypes.TESS_CONTROL, IrisProgramTypes.TESS_EVAL});
    }
}

