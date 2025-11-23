/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL43C
 */
package net.irisshaders.iris.gl.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43C;

public interface DepthCopyStrategy {
    public static DepthCopyStrategy fastest(boolean combinedStencilRequired) {
        if (GL.getCapabilities().glCopyImageSubData != 0L) {
            return new Gl43CopyImage();
        }
        if (combinedStencilRequired) {
            return new Gl30BlitFbCombinedDepthStencil();
        }
        return new Gl20CopyTexture();
    }

    public boolean needsDestFramebuffer();

    public void copy(GlFramebuffer var1, int var2, GlFramebuffer var3, int var4, int var5, int var6);

    public static class Gl43CopyImage
    implements DepthCopyStrategy {
        private Gl43CopyImage() {
        }

        @Override
        public boolean needsDestFramebuffer() {
            return false;
        }

        @Override
        public void copy(GlFramebuffer sourceFb, int sourceTexture, GlFramebuffer destFb, int destTexture, int width, int height) {
            GL43C.glCopyImageSubData((int)sourceTexture, (int)3553, (int)0, (int)0, (int)0, (int)0, (int)destTexture, (int)3553, (int)0, (int)0, (int)0, (int)0, (int)width, (int)height, (int)1);
        }
    }

    public static class Gl30BlitFbCombinedDepthStencil
    implements DepthCopyStrategy {
        private Gl30BlitFbCombinedDepthStencil() {
        }

        @Override
        public boolean needsDestFramebuffer() {
            return true;
        }

        @Override
        public void copy(GlFramebuffer sourceFb, int sourceTexture, GlFramebuffer destFb, int destTexture, int width, int height) {
            IrisRenderSystem.blitFramebuffer(sourceFb.getId(), destFb.getId(), 0, 0, width, height, 0, 0, width, height, 1280, 9728);
        }
    }

    public static class Gl20CopyTexture
    implements DepthCopyStrategy {
        private Gl20CopyTexture() {
        }

        @Override
        public boolean needsDestFramebuffer() {
            return false;
        }

        @Override
        public void copy(GlFramebuffer sourceFb, int sourceTexture, GlFramebuffer destFb, int destTexture, int width, int height) {
            sourceFb.bindAsReadBuffer();
            int previousTexture = GlStateManagerAccessor.getTEXTURES()[GlStateManagerAccessor.getActiveTexture()].f_84801_;
            IrisRenderSystem.copyTexSubImage2D(destTexture, 3553, 0, 0, 0, 0, 0, width, height);
            RenderSystem.bindTexture((int)previousTexture);
        }
    }
}

