/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  com.mojang.blaze3d.shaders.Shader
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ProgramManager.class})
public class MixinProgramManager {
    @Inject(method={"releaseProgram"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;assertOnRenderThread()V")})
    private static void iris$releaseGeometry(Shader shader, CallbackInfo ci) {
        if (shader instanceof ExtendedShader && ((ExtendedShader)shader).getGeometry() != null) {
            ((ExtendedShader)shader).getGeometry().m_85543_();
        }
        if (shader instanceof ExtendedShader && ((ExtendedShader)shader).getTessControl() != null) {
            ((ExtendedShader)shader).getTessControl().m_85543_();
        }
        if (shader instanceof ExtendedShader && ((ExtendedShader)shader).getTessEval() != null) {
            ((ExtendedShader)shader).getTessEval().m_85543_();
        }
    }
}

