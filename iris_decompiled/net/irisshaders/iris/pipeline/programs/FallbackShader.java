/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.pipeline.programs;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.util.List;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

public class FallbackShader
extends ShaderInstance {
    private final IrisRenderingPipeline parent;
    private final BlendModeOverride blendModeOverride;
    private final GlFramebuffer writingToBeforeTranslucent;
    private final GlFramebuffer writingToAfterTranslucent;
    @Nullable
    private final Uniform FOG_DENSITY;
    @Nullable
    private final Uniform FOG_IS_EXP2;
    private final int gtexture;
    private final int overlay;
    private final int lightmap;

    public FallbackShader(ResourceProvider resourceFactory, String string, VertexFormat vertexFormat, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent, BlendModeOverride blendModeOverride, float alphaValue, IrisRenderingPipeline parent) throws IOException {
        super(resourceFactory, string, vertexFormat);
        this.parent = parent;
        this.blendModeOverride = blendModeOverride;
        this.writingToBeforeTranslucent = writingToBeforeTranslucent;
        this.writingToAfterTranslucent = writingToAfterTranslucent;
        this.FOG_DENSITY = this.m_173348_("FogDensity");
        this.FOG_IS_EXP2 = this.m_173348_("FogIsExp2");
        this.gtexture = GlStateManager._glGetUniformLocation((int)this.m_108943_(), (CharSequence)"gtexture");
        this.overlay = GlStateManager._glGetUniformLocation((int)this.m_108943_(), (CharSequence)"overlay");
        this.lightmap = GlStateManager._glGetUniformLocation((int)this.m_108943_(), (CharSequence)"lightmap");
        Uniform ALPHA_TEST_VALUE = this.m_173348_("AlphaTestValue");
        if (ALPHA_TEST_VALUE != null) {
            ALPHA_TEST_VALUE.m_5985_(alphaValue);
        }
    }

    public void m_173362_() {
        super.m_173362_();
        if (this.blendModeOverride != null) {
            BlendModeOverride.restore();
        }
        Minecraft.m_91087_().m_91385_().m_83947_(false);
    }

    public void m_173363_() {
        if (this.FOG_DENSITY != null && this.FOG_IS_EXP2 != null) {
            float fogDensity = CapturedRenderingState.INSTANCE.getFogDensity();
            if ((double)fogDensity >= 0.0) {
                this.FOG_DENSITY.m_5985_(fogDensity);
                this.FOG_IS_EXP2.m_142617_(1);
            } else {
                this.FOG_DENSITY.m_5985_(0.0f);
                this.FOG_IS_EXP2.m_142617_(0);
            }
        }
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 0, RenderSystem.getShaderTexture((int)0));
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 1, RenderSystem.getShaderTexture((int)1));
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, RenderSystem.getShaderTexture((int)2));
        ProgramManager.m_85578_((int)this.m_108943_());
        List uniformList = this.f_173331_;
        for (Uniform uniform : uniformList) {
            this.uploadIfNotNull(uniform);
        }
        GlStateManager._glUniform1i((int)this.gtexture, (int)0);
        GlStateManager._glUniform1i((int)this.overlay, (int)1);
        GlStateManager._glUniform1i((int)this.lightmap, (int)2);
        if (this.blendModeOverride != null) {
            this.blendModeOverride.apply();
        }
        if (this.parent.isBeforeTranslucent) {
            this.writingToBeforeTranslucent.bind();
        } else {
            this.writingToAfterTranslucent.bind();
        }
    }

    private void uploadIfNotNull(Uniform uniform) {
        if (uniform != null) {
            uniform.m_85633_();
        }
    }
}

