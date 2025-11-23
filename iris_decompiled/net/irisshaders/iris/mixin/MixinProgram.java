/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.preprocessor.GlslPreprocessor
 *  com.mojang.blaze3d.shaders.Program
 *  com.mojang.blaze3d.shaders.Program$Type
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={Program.class})
public class MixinProgram {
    @Redirect(method={"compileShaderInternal"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;process(Ljava/lang/String;)Ljava/util/List;"))
    private static List<String> iris$allowSkippingMojImportDirectives(GlslPreprocessor includeHandler, String shaderSource) {
        if (!shaderSource.contains("moj_import")) {
            return Collections.singletonList(shaderSource);
        }
        return includeHandler.m_166461_(shaderSource);
    }

    @Inject(method={"compileShaderInternal"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;glGetShaderInfoLog(II)Ljava/lang/String;")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private static void iris$causeException(Program.Type arg, String string, InputStream inputStream, String string2, GlslPreprocessor arg2, CallbackInfoReturnable<Integer> cir, String string3, int i) {
        cir.cancel();
        throw new ShaderCompileException(string + arg.m_85569_(), GlStateManager.glGetShaderInfoLog((int)i, (int)32768));
    }
}

