/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  me.jellysquid.mods.sodium.client.gl.GlObject
 *  me.jellysquid.mods.sodium.client.gl.shader.GlProgram
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform
 *  me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.shader_overrides;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntFunction;
import me.jellysquid.mods.sodium.client.gl.GlObject;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniform;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.ShaderBindingContextExt;
import net.irisshaders.iris.gl.IrisRenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={GlProgram.class}, remap=false)
public class MixinGlProgram
extends GlObject
implements ShaderBindingContextExt {
    @Override
    public <U extends GlUniform<?>> U bindUniformIfPresent(String name, IntFunction<U> factory) {
        int index = GlStateManager._glGetUniformLocation((int)this.handle(), (CharSequence)name);
        if (index < 0) {
            return null;
        }
        return (U)((GlUniform)factory.apply(index));
    }

    @Redirect(method={"bind"}, at=@At(value="INVOKE", target="Lorg/lwjgl/opengl/GL20C;glUseProgram(I)V"))
    private void iris$useGlStateManager(int i) {
        GlStateManager._glUseProgram((int)i);
    }

    @Redirect(method={"unbind"}, at=@At(value="INVOKE", target="Lorg/lwjgl/opengl/GL20C;glUseProgram(I)V"))
    private void iris$useGlStateManager2(int i) {
        GlStateManager._glUseProgram((int)i);
    }

    @Override
    public GlUniformBlock bindUniformBlockIfPresent(String name, int bindingPoint) {
        int index = IrisRenderSystem.getUniformBlockIndex(this.handle(), name);
        if (index < 0) {
            return null;
        }
        IrisRenderSystem.uniformBlockBinding(this.handle(), index, bindingPoint);
        return new GlUniformBlock(bindingPoint);
    }
}

