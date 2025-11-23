/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Program$Type
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={Program.Type.class})
public interface ProgramTypeAccessor {
    @Invoker(value="<init>")
    public static Program.Type createProgramType(String name, int ordinal, String typeName, String extension, int glId) {
        throw new AssertionError();
    }
}

