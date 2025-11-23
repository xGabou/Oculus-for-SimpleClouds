/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.pipeline.TextureTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.EffectInstance
 *  net.minecraft.client.renderer.PostChain
 *  net.minecraft.client.renderer.PostPass
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 */
package dev.nonamecrackers2.simpleclouds.client.renderer;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.CloudRenderTarget;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.WeightedBlendingTarget;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.mixin.MixinPostChain;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;

public class CloudImageRenderer
implements AutoCloseable {
    private static final int DEFAULT_WIDTH = 2048;
    private static final int DEFAULT_HEIGHT = 2048;
    private static final float ZOOM_CONSTANT = 1.0f;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    private final File path;
    private final CloudMeshGenerator generator;
    @Nullable
    private PostChain finalComposite;
    @Nullable
    private RenderTarget finalTarget;
    @Nullable
    private RenderTarget cloudsTarget;
    @Nullable
    private WeightedBlendingTarget transparencyTarget;
    private int oldWindowHeight = -1;
    private int oldWindowWidth = -1;
    private float rotX;
    private float rotY;
    private float zoom;
    private float r;
    private float g;
    private float b;

    public CloudImageRenderer(Minecraft mc, File path, float rotX, float rotY, float zoom, CloudMeshGenerator generator) {
        this.mc = mc;
        this.path = path;
        this.rotX = rotX;
        this.rotY = rotY;
        this.zoom = zoom;
        this.generator = generator;
    }

    public static CloudImageRenderer basicIsometric(File path, CloudMeshGenerator generator) {
        float fadeDist = generator.getFadeStart() + 32.0f;
        float zoom = 2048.0f / (fadeDist * 2.0f);
        return new CloudImageRenderer(Minecraft.m_91087_(), path, 225.0f, 45.0f, zoom, generator);
    }

    @Nullable
    public RenderTarget getFrameBuffer() {
        return this.finalTarget;
    }

    public void setRotX(float rot) {
        this.rotX = rot;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void setBgCol(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void initialize() {
        block5: {
            RenderSystem.assertOnRenderThread();
            Window window = this.mc.m_91268_();
            this.oldWindowWidth = window.m_85441_();
            this.oldWindowHeight = window.m_85442_();
            window.m_166450_(2048);
            window.m_166452_(2048);
            window.m_85378_(window.m_85449_());
            this.cloudsTarget = new CloudRenderTarget(2048, 2048, Minecraft.f_91002_, false);
            this.cloudsTarget.m_83931_(this.r, this.g, this.b, 1.0f);
            this.cloudsTarget.m_83954_(Minecraft.f_91002_);
            if (this.generator.transparencyEnabled()) {
                this.transparencyTarget = new WeightedBlendingTarget(2048, 2048, Minecraft.f_91002_, false);
            }
            this.finalTarget = new TextureTarget(2048, 2048, true, Minecraft.f_91002_);
            this.finalTarget.m_83931_(0.0f, 0.0f, 0.0f, 1.0f);
            this.finalTarget.m_83954_(Minecraft.f_91002_);
            ResourceLocation finalComLoc = this.generator.transparencyEnabled() ? SimpleCloudsRenderer.FINAL_COMPOSITE_LOC : SimpleCloudsRenderer.FINAL_COMPOSITE_NO_TRANSPARENCY_LOC;
            try {
                this.finalComposite = new PostChain(this.mc.m_91097_(), this.mc.m_91098_(), this.finalTarget, finalComLoc);
                this.finalComposite.m_110025_(2048, 2048);
                for (PostPass pass : ((MixinPostChain)this.finalComposite).simpleclouds$getPostPasses()) {
                    EffectInstance effect = pass.m_110074_();
                    if (this.generator.transparencyEnabled()) {
                        effect.m_108954_("AccumTexture", () -> this.transparencyTarget.m_83975_());
                        effect.m_108954_("RevealageTexture", () -> this.transparencyTarget.getRevealageTextureId());
                    }
                    effect.m_108954_("CloudsTexture", () -> this.cloudsTarget.m_83975_());
                    effect.m_108954_("CloudsDepthTexture", () -> this.cloudsTarget.m_83980_());
                }
            }
            catch (JsonSyntaxException | IOException e) {
                LOGGER.error("Failed to create final composite post process chain", e);
                if (this.finalComposite == null) break block5;
                this.finalComposite.close();
                this.finalComposite = null;
            }
        }
    }

    private void assertValid() {
        Objects.requireNonNull(this.finalComposite, "Not properly initialized");
        Objects.requireNonNull(this.finalTarget, "Not properly initialized");
        Objects.requireNonNull(this.cloudsTarget, "Not properly initialized");
        Objects.requireNonNull(this.transparencyTarget, "Not properly initialized");
        if (this.oldWindowWidth < 0 || this.oldWindowHeight < 0) {
            throw new IllegalStateException("Not properly initialized");
        }
    }

    public void render() {
        RenderSystem.assertOnRenderThread();
        this.assertValid();
        float farPlane = this.generator.getFadeEnd() * 2.0f;
        Matrix4f projectionMat = new Matrix4f().setOrtho(0.0f, 2048.0f, 2048.0f, 0.0f, 0.0f, farPlane);
        Quaternionf rot = new Quaternionf().rotateX(this.rotX * ((float)Math.PI / 180)).rotateY((float)Math.PI + this.rotY * ((float)Math.PI / 180));
        PoseStack stack = new PoseStack();
        stack.m_166856_();
        stack.m_85837_(1024.0, 1024.0, 0.0);
        stack.m_252931_(new Matrix4f().scaling(this.zoom * 1.0f, this.zoom * 1.0f, -1.0f));
        stack.m_85837_(0.0, 0.0, (double)(farPlane / 2.0f));
        stack.m_252781_(rot);
        this.cloudsTarget.m_83954_(Minecraft.f_91002_);
        this.cloudsTarget.m_83947_(true);
        SimpleCloudsRenderer.renderCloudsOpaque(this.generator, stack, projectionMat, Float.MAX_VALUE, Float.MAX_VALUE, 1.0f, 1.0f, 1.0f, 1.0f, null, false);
        this.transparencyTarget.m_83954_(Minecraft.f_91002_);
        if (this.generator.transparencyEnabled()) {
            this.transparencyTarget.m_83947_(true);
            this.transparencyTarget.m_83945_(this.cloudsTarget);
            if (GlStateManager._getError() != 0) {
                throw new RuntimeException("Failed to copy depth buffers");
            }
            this.transparencyTarget.m_83947_(false);
            SimpleCloudsRenderer.renderCloudsTransparency(this.generator, stack, projectionMat, Float.MAX_VALUE, Float.MAX_VALUE, 1.0f, 1.0f, 1.0f, 1.0f, null, false);
        }
        this.finalTarget.m_83954_(Minecraft.f_91002_);
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        RenderSystem.depthMask((boolean)false);
        Matrix4f invertedProjMat = new Matrix4f((Matrix4fc)projectionMat).invert();
        Matrix4f invertedModelViewMat = new Matrix4f((Matrix4fc)stack.m_85850_().m_252922_()).invert();
        for (PostPass pass : ((MixinPostChain)this.finalComposite).simpleclouds$getPostPasses()) {
            EffectInstance effect = pass.m_110074_();
            effect.m_108960_("InverseWorldProjMat").m_5679_(invertedProjMat);
            effect.m_108960_("InverseModelViewMat").m_5679_(invertedModelViewMat);
            effect.m_108960_("FogStart").m_5985_(Float.MAX_VALUE);
            effect.m_108960_("FogEnd").m_5985_(Float.MAX_VALUE);
        }
        this.finalComposite.m_110023_(1.0f);
        RenderSystem.depthMask((boolean)true);
        this.mc.m_91385_().m_83947_(true);
    }

    public void exportToRenderedImage(Consumer<Component> messageAcceptor) {
        RenderSystem.assertOnRenderThread();
        this.assertValid();
        NativeImage image = new NativeImage(2048, 2048, false);
        RenderSystem.bindTexture((int)this.finalTarget.m_83975_());
        image.m_85045_(0, true);
        image.m_85122_();
        File loc = CloudImageRenderer.getFile(this.path);
        Util.m_183992_().execute(() -> {
            try {
                image.m_85056_(loc);
                MutableComponent component = Component.m_237113_((String)loc.getName()).m_130940_(ChatFormatting.UNDERLINE).m_130938_(s -> s.m_131142_(new ClickEvent(ClickEvent.Action.OPEN_FILE, loc.getAbsolutePath())));
                messageAcceptor.accept((Component)Component.m_237110_((String)"screenshot.success", (Object[])new Object[]{component}));
            }
            catch (Exception e) {
                LOGGER.warn("Failed to save render", (Throwable)e);
                messageAcceptor.accept((Component)Component.m_237110_((String)"screenshot.failure", (Object[])new Object[]{e.getMessage()}));
            }
            finally {
                image.close();
            }
        });
    }

    public void finalize() {
        RenderSystem.assertOnRenderThread();
        this.assertValid();
        Window window = this.mc.m_91268_();
        window.m_166450_(this.oldWindowWidth);
        window.m_166452_(this.oldWindowHeight);
        window.m_85378_(window.m_85449_());
    }

    @Override
    public void close() {
        if (this.finalTarget != null) {
            this.finalTarget.m_83930_();
            this.finalTarget = null;
        }
        if (this.cloudsTarget != null) {
            this.cloudsTarget.m_83930_();
            this.cloudsTarget = null;
        }
        if (this.transparencyTarget != null) {
            this.transparencyTarget.m_83930_();
            this.transparencyTarget = null;
        }
        if (this.finalComposite != null) {
            this.finalComposite.close();
            this.finalComposite = null;
        }
    }

    private static File getFile(File file) {
        String s = Util.m_241986_();
        int i = 1;
        File renderFile;
        while ((renderFile = new File(file, s + (String)(i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return renderFile;
    }
}

