/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonObject
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraft.util.GsonHelper
 *  org.slf4j.Logger
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.Reader;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.blending.DepthColorStorage;
import net.irisshaders.iris.pipeline.ShaderRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.irisshaders.iris.pipeline.programs.FallbackShader;
import net.irisshaders.iris.pipeline.programs.ShaderInstanceInterface;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ShaderInstance.class})
public abstract class MixinShaderInstance
implements ShaderInstanceInterface {
    @Unique
    private static final ImmutableSet<String> ATTRIBUTE_LIST = ImmutableSet.of((Object)"Position", (Object)"Color", (Object)"Normal", (Object)"UV0", (Object)"UV1", (Object)"UV2", (Object[])new String[0]);

    private static boolean shouldOverrideShaders() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof ShaderRenderingPipeline) {
            return ((ShaderRenderingPipeline)pipeline).shouldOverrideShaders();
        }
        return false;
    }

    @Shadow
    public abstract int m_108943_();

    @Redirect(method={"updateLocations"}, at=@At(value="INVOKE", target="Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap=false))
    private void iris$redirectLogSpam(Logger logger, String message, Object arg1, Object arg2) {
        if (this instanceof ExtendedShader || this instanceof FallbackShader) {
            return;
        }
        logger.warn(message, arg1, arg2);
    }

    @Redirect(method={"<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/shaders/Uniform;glBindAttribLocation(IILjava/lang/CharSequence;)V"))
    public void iris$redirectBindAttributeLocation(int i, int j, CharSequence charSequence) {
        if (this instanceof ExtendedShader && ATTRIBUTE_LIST.contains((Object)charSequence)) {
            Uniform.m_166710_((int)i, (int)j, (CharSequence)("iris_" + charSequence));
        } else {
            Uniform.m_166710_((int)i, (int)j, (CharSequence)charSequence);
        }
    }

    @Inject(method={"apply"}, at={@At(value="TAIL")})
    private void iris$lockDepthColorState(CallbackInfo ci) {
        if (this instanceof ExtendedShader || this instanceof FallbackShader || !MixinShaderInstance.shouldOverrideShaders()) {
            return;
        }
        DepthColorStorage.disableDepthColor();
    }

    @Inject(method={"clear"}, at={@At(value="HEAD")})
    private void iris$unlockDepthColorState(CallbackInfo ci) {
        if (this instanceof ExtendedShader || this instanceof FallbackShader || !MixinShaderInstance.shouldOverrideShaders()) {
            return;
        }
        DepthColorStorage.unlockDepthColor();
    }

    @Redirect(method={"<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/GsonHelper;parse(Ljava/io/Reader;)Lcom/google/gson/JsonObject;"))
    public JsonObject iris$setupGeometryShader(Reader reader, ResourceProvider resourceProvider, ResourceLocation name, VertexFormat vertexFormat) {
        this.iris$createExtraShaders(resourceProvider, name);
        return GsonHelper.m_13859_((Reader)reader);
    }

    @Override
    public void iris$createExtraShaders(ResourceProvider provider, ResourceLocation name) {
    }
}

