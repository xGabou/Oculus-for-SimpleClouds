/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3i
 *  org.lwjgl.opengl.ARBDirectStateAccess
 *  org.lwjgl.opengl.ARBDrawBuffersBlend
 *  org.lwjgl.opengl.EXTShaderImageLoadStore
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL30C
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.opengl.GL33C
 *  org.lwjgl.opengl.GL42C
 *  org.lwjgl.opengl.GL43C
 *  org.lwjgl.opengl.GL45C
 *  org.lwjgl.opengl.GL46C
 */
package net.irisshaders.iris.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.BooleanStateExtended;
import net.irisshaders.iris.gl.sampler.SamplerLimits;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ARBDrawBuffersBlend;
import org.lwjgl.opengl.EXTShaderImageLoadStore;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL42C;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.opengl.GL46C;

public class IrisRenderSystem {
    private static final int[] emptyArray = new int[SamplerLimits.get().getMaxTextureUnits()];
    private static Matrix4f backupProjection;
    private static DSAAccess dsaState;
    private static boolean hasMultibind;
    private static boolean supportsCompute;
    private static boolean supportsTesselation;
    private static int polygonMode;
    private static int backupPolygonMode;
    private static int[] samplers;

    public static void initRenderer() {
        if (GL.getCapabilities().OpenGL45) {
            dsaState = new DSACore();
            Iris.logger.info("OpenGL 4.5 detected, enabling DSA.");
        } else if (GL.getCapabilities().GL_ARB_direct_state_access) {
            dsaState = new DSAARB();
            Iris.logger.info("ARB_direct_state_access detected, enabling DSA.");
        } else {
            dsaState = new DSAUnsupported();
            Iris.logger.info("DSA support not detected.");
        }
        hasMultibind = GL.getCapabilities().OpenGL45 || GL.getCapabilities().GL_ARB_multi_bind;
        supportsCompute = GL.getCapabilities().glDispatchCompute != 0L;
        supportsTesselation = GL.getCapabilities().GL_ARB_tessellation_shader || GL.getCapabilities().OpenGL40;
        samplers = new int[SamplerLimits.get().getMaxTextureUnits()];
    }

    public static void getIntegerv(int pname, int[] params) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glGetIntegerv((int)pname, (int[])params);
    }

    public static void getFloatv(int pname, float[] params) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glGetFloatv((int)pname, (float[])params);
    }

    public static void generateMipmaps(int texture, int mipmapTarget) {
        RenderSystem.assertOnRenderThreadOrInit();
        dsaState.generateMipmaps(texture, mipmapTarget);
    }

    public static void bindAttributeLocation(int program, int index, CharSequence name) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glBindAttribLocation((int)program, (int)index, (CharSequence)name);
    }

    public static void texImage1D(int texture, int target, int level, int internalformat, int width, int border, int format, int type, @Nullable ByteBuffer pixels) {
        RenderSystem.assertOnRenderThreadOrInit();
        IrisRenderSystem.bindTextureForSetup(target, texture);
        GL30C.glTexImage1D((int)target, (int)level, (int)internalformat, (int)width, (int)border, (int)format, (int)type, (ByteBuffer)pixels);
    }

    public static void texImage2D(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, @Nullable ByteBuffer pixels) {
        RenderSystem.assertOnRenderThreadOrInit();
        IrisRenderSystem.bindTextureForSetup(target, texture);
        GL32C.glTexImage2D((int)target, (int)level, (int)internalformat, (int)width, (int)height, (int)border, (int)format, (int)type, (ByteBuffer)pixels);
    }

    public static void texImage3D(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, @Nullable ByteBuffer pixels) {
        RenderSystem.assertOnRenderThreadOrInit();
        IrisRenderSystem.bindTextureForSetup(target, texture);
        GL30C.glTexImage3D((int)target, (int)level, (int)internalformat, (int)width, (int)height, (int)depth, (int)border, (int)format, (int)type, (ByteBuffer)pixels);
    }

    public static void uniformMatrix4fv(int location, boolean transpose, FloatBuffer matrix) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniformMatrix4fv((int)location, (boolean)transpose, (FloatBuffer)matrix);
    }

    public static void copyTexImage2D(int target, int level, int internalFormat, int x, int y, int width, int height, int border) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glCopyTexImage2D((int)target, (int)level, (int)internalFormat, (int)x, (int)y, (int)width, (int)height, (int)border);
    }

    public static void uniform1f(int location, float v0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniform1f((int)location, (float)v0);
    }

    public static void uniform2f(int location, float v0, float v1) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniform2f((int)location, (float)v0, (float)v1);
    }

    public static void uniform2i(int location, int v0, int v1) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniform2i((int)location, (int)v0, (int)v1);
    }

    public static void uniform3f(int location, float v0, float v1, float v2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniform3f((int)location, (float)v0, (float)v1, (float)v2);
    }

    public static void uniform3i(int location, int v0, int v1, int v2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniform3i((int)location, (int)v0, (int)v1, (int)v2);
    }

    public static void uniform4f(int location, float v0, float v1, float v2, float v3) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniform4f((int)location, (float)v0, (float)v1, (float)v2, (float)v3);
    }

    public static void uniform4i(int location, int v0, int v1, int v2, int v3) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniform4i((int)location, (int)v0, (int)v1, (int)v2, (int)v3);
    }

    public static void texParameteriv(int texture, int target, int pname, int[] params) {
        RenderSystem.assertOnRenderThreadOrInit();
        dsaState.texParameteriv(texture, target, pname, params);
    }

    public static void texParameterivDirect(int target, int pname, int[] params) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glTexParameteriv((int)target, (int)pname, (int[])params);
    }

    public static void copyTexSubImage2D(int destTexture, int target, int i, int i1, int i2, int i3, int i4, int width, int height) {
        dsaState.copyTexSubImage2D(destTexture, target, i, i1, i2, i3, i4, width, height);
    }

    public static void texParameteri(int texture, int target, int pname, int param) {
        RenderSystem.assertOnRenderThreadOrInit();
        dsaState.texParameteri(texture, target, pname, param);
    }

    public static void texParameterf(int texture, int target, int pname, float param) {
        RenderSystem.assertOnRenderThreadOrInit();
        dsaState.texParameterf(texture, target, pname, param);
    }

    public static String getProgramInfoLog(int program) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL32C.glGetProgramInfoLog((int)program);
    }

    public static String getShaderInfoLog(int shader) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL32C.glGetShaderInfoLog((int)shader);
    }

    public static void drawBuffers(int framebuffer, int[] buffers) {
        RenderSystem.assertOnRenderThreadOrInit();
        dsaState.drawBuffers(framebuffer, buffers);
    }

    public static void readBuffer(int framebuffer, int buffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        dsaState.readBuffer(framebuffer, buffer);
    }

    public static String getActiveUniform(int program, int index, int size, IntBuffer type, IntBuffer name) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL32C.glGetActiveUniform((int)program, (int)index, (int)size, (IntBuffer)type, (IntBuffer)name);
    }

    public static void readPixels(int x, int y, int width, int height, int format, int type, float[] pixels) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glReadPixels((int)x, (int)y, (int)width, (int)height, (int)format, (int)type, (float[])pixels);
    }

    public static void bufferData(int target, float[] data, int usage) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glBufferData((int)target, (float[])data, (int)usage);
    }

    public static int bufferStorage(int target, float[] data, int usage) {
        RenderSystem.assertOnRenderThreadOrInit();
        return dsaState.bufferStorage(target, data, usage);
    }

    public static void bufferStorage(int target, long size, int flags) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45C.glBufferStorage((int)target, (long)size, (int)flags);
    }

    public static void bindBufferBase(int target, Integer index, int buffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL43C.glBindBufferBase((int)target, (int)index, (int)buffer);
    }

    public static void vertexAttrib4f(int index, float v0, float v1, float v2, float v3) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glVertexAttrib4f((int)index, (float)v0, (float)v1, (float)v2, (float)v3);
    }

    public static void detachShader(int program, int shader) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glDetachShader((int)program, (int)shader);
    }

    public static void framebufferTexture2D(int fb, int fbtarget, int attachment, int target, int texture, int levels) {
        dsaState.framebufferTexture2D(fb, fbtarget, attachment, target, texture, levels);
    }

    public static int getTexParameteri(int texture, int target, int pname) {
        RenderSystem.assertOnRenderThreadOrInit();
        return dsaState.getTexParameteri(texture, target, pname);
    }

    public static void bindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        RenderSystem.assertOnRenderThreadOrInit();
        if (GL.getCapabilities().OpenGL42 || GL.getCapabilities().GL_ARB_shader_image_load_store) {
            GL42C.glBindImageTexture((int)unit, (int)texture, (int)level, (boolean)layered, (int)layer, (int)access, (int)format);
        } else {
            EXTShaderImageLoadStore.glBindImageTextureEXT((int)unit, (int)texture, (int)level, (boolean)layered, (int)layer, (int)access, (int)format);
        }
    }

    public static int getMaxImageUnits() {
        if (GL.getCapabilities().OpenGL42 || GL.getCapabilities().GL_ARB_shader_image_load_store) {
            return GlStateManager._getInteger((int)36664);
        }
        if (GL.getCapabilities().GL_EXT_shader_image_load_store) {
            return GlStateManager._getInteger((int)36664);
        }
        return 0;
    }

    public static boolean supportsSSBO() {
        return GL.getCapabilities().OpenGL44 || GL.getCapabilities().GL_ARB_shader_storage_buffer_object && GL.getCapabilities().GL_ARB_buffer_storage;
    }

    public static boolean supportsImageLoadStore() {
        return GL.getCapabilities().glBindImageTexture != 0L || GL.getCapabilities().OpenGL42 || (GL.getCapabilities().GL_ARB_shader_image_load_store || GL.getCapabilities().GL_EXT_shader_image_load_store) && GL.getCapabilities().GL_ARB_buffer_storage;
    }

    public static void genBuffers(int[] buffers) {
        GL43C.glGenBuffers((int[])buffers);
    }

    public static void clearBufferSubData(int glShaderStorageBuffer, int glR8, long offset, long size, int glRed, int glByte, int[] ints) {
        GL43C.glClearBufferSubData((int)glShaderStorageBuffer, (int)glR8, (long)offset, (long)size, (int)glRed, (int)glByte, (int[])ints);
    }

    public static void getProgramiv(int program, int value, int[] storage) {
        GL32C.glGetProgramiv((int)program, (int)value, (int[])storage);
    }

    public static void dispatchCompute(int workX, int workY, int workZ) {
        GL45C.glDispatchCompute((int)workX, (int)workY, (int)workZ);
    }

    public static void dispatchCompute(Vector3i workGroups) {
        GL45C.glDispatchCompute((int)workGroups.x, (int)workGroups.y, (int)workGroups.z);
    }

    public static void memoryBarrier(int barriers) {
        RenderSystem.assertOnRenderThreadOrInit();
        if (supportsCompute) {
            GL45C.glMemoryBarrier((int)barriers);
        }
    }

    public static boolean supportsBufferBlending() {
        return GL.getCapabilities().GL_ARB_draw_buffers_blend || GL.getCapabilities().OpenGL40;
    }

    public static void disableBufferBlend(int buffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glDisablei((int)3042, (int)buffer);
        ((BooleanStateExtended)GlStateManagerAccessor.getBLEND().f_84577_).setUnknownState();
    }

    public static void enableBufferBlend(int buffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glEnablei((int)3042, (int)buffer);
        ((BooleanStateExtended)GlStateManagerAccessor.getBLEND().f_84577_).setUnknownState();
    }

    public static void blendFuncSeparatei(int buffer, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        RenderSystem.assertOnRenderThreadOrInit();
        ARBDrawBuffersBlend.glBlendFuncSeparateiARB((int)buffer, (int)srcRGB, (int)dstRGB, (int)srcAlpha, (int)dstAlpha);
    }

    public static void bindTextureToUnit(int target, int unit, int texture) {
        dsaState.bindTextureToUnit(target, unit, texture);
    }

    public static int getUniformBlockIndex(int program, String uniformBlockName) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL32C.glGetUniformBlockIndex((int)program, (CharSequence)uniformBlockName);
    }

    public static void uniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL32C.glUniformBlockBinding((int)program, (int)uniformBlockIndex, (int)uniformBlockBinding);
    }

    public static void setShadowProjection(Matrix4f shadowProjection) {
        backupProjection = RenderSystem.getProjectionMatrix();
        RenderSystem.setProjectionMatrix((Matrix4f)shadowProjection, (VertexSorting)VertexSorting.f_276633_);
    }

    public static void restorePlayerProjection() {
        RenderSystem.setProjectionMatrix((Matrix4f)backupProjection, (VertexSorting)VertexSorting.f_276450_);
        backupProjection = null;
    }

    public static void blitFramebuffer(int source, int dest, int offsetX, int offsetY, int width, int height, int offsetX2, int offsetY2, int width2, int height2, int bufferChoice, int filter) {
        dsaState.blitFramebuffer(source, dest, offsetX, offsetY, width, height, offsetX2, offsetY2, width2, height2, bufferChoice, filter);
    }

    public static int createFramebuffer() {
        return dsaState.createFramebuffer();
    }

    public static int createTexture(int target) {
        return dsaState.createTexture(target);
    }

    public static void bindTextureForSetup(int glType, int glId) {
        GL30C.glBindTexture((int)glType, (int)glId);
    }

    public static boolean supportsCompute() {
        return supportsCompute;
    }

    public static boolean supportsTesselation() {
        return supportsTesselation;
    }

    public static int genSampler() {
        return GL33C.glGenSamplers();
    }

    public static void destroySampler(int glId) {
        GL33C.glDeleteSamplers((int)glId);
    }

    public static void bindSamplerToUnit(int unit, int sampler) {
        if (samplers[unit] == sampler) {
            return;
        }
        GL33C.glBindSampler((int)unit, (int)sampler);
        IrisRenderSystem.samplers[unit] = sampler;
    }

    public static void unbindAllSamplers() {
        boolean usedASampler = false;
        for (int i = 0; i < samplers.length; ++i) {
            if (samplers[i] == 0) continue;
            usedASampler = true;
            if (!hasMultibind) {
                GL33C.glBindSampler((int)i, (int)0);
            }
            IrisRenderSystem.samplers[i] = 0;
        }
        if (usedASampler && hasMultibind) {
            GL45C.glBindSamplers((int)0, (int[])emptyArray);
        }
    }

    public static void samplerParameteri(int sampler, int pname, int param) {
        GL33C.glSamplerParameteri((int)sampler, (int)pname, (int)param);
    }

    public static void samplerParameterf(int sampler, int pname, float param) {
        GL33C.glSamplerParameterf((int)sampler, (int)pname, (float)param);
    }

    public static void samplerParameteriv(int sampler, int pname, int[] params) {
        GL33C.glSamplerParameteriv((int)sampler, (int)pname, (int[])params);
    }

    public static long getVRAM() {
        if (GL.getCapabilities().GL_NVX_gpu_memory_info) {
            return (long)GL32C.glGetInteger((int)36937) * 1024L;
        }
        return 0x100000000L;
    }

    public static void deleteBuffers(int glId) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL43C.glDeleteBuffers((int)glId);
    }

    public static void setPolygonMode(int mode) {
        if (mode != polygonMode) {
            polygonMode = mode;
            GL43C.glPolygonMode((int)1032, (int)mode);
        }
    }

    public static void overridePolygonMode() {
        backupPolygonMode = polygonMode;
        IrisRenderSystem.setPolygonMode(6914);
    }

    public static void restorePolygonMode() {
        IrisRenderSystem.setPolygonMode(backupPolygonMode);
        backupPolygonMode = 6914;
    }

    public static void dispatchComputeIndirect(long offset) {
        GL43C.glDispatchComputeIndirect((long)offset);
    }

    public static void bindBuffer(int target, int buffer) {
        GL46C.glBindBuffer((int)target, (int)buffer);
    }

    public static int createBuffers() {
        return dsaState.createBuffers();
    }

    static {
        polygonMode = 6914;
        backupPolygonMode = 6914;
    }

    public static class DSACore
    extends DSAARB {
    }

    public static interface DSAAccess {
        public void generateMipmaps(int var1, int var2);

        public void texParameteri(int var1, int var2, int var3, int var4);

        public void texParameterf(int var1, int var2, int var3, float var4);

        public void texParameteriv(int var1, int var2, int var3, int[] var4);

        public void readBuffer(int var1, int var2);

        public void drawBuffers(int var1, int[] var2);

        public int getTexParameteri(int var1, int var2, int var3);

        public void copyTexSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

        public void bindTextureToUnit(int var1, int var2, int var3);

        public int bufferStorage(int var1, float[] var2, int var3);

        public void blitFramebuffer(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

        public void framebufferTexture2D(int var1, int var2, int var3, int var4, int var5, int var6);

        public int createFramebuffer();

        public int createTexture(int var1);

        public int createBuffers();
    }

    public static class DSAARB
    extends DSAUnsupported {
        @Override
        public void generateMipmaps(int texture, int target) {
            ARBDirectStateAccess.glGenerateTextureMipmap((int)texture);
        }

        @Override
        public void texParameteri(int texture, int target, int pname, int param) {
            ARBDirectStateAccess.glTextureParameteri((int)texture, (int)pname, (int)param);
        }

        @Override
        public void texParameterf(int texture, int target, int pname, float param) {
            ARBDirectStateAccess.glTextureParameterf((int)texture, (int)pname, (float)param);
        }

        @Override
        public void texParameteriv(int texture, int target, int pname, int[] params) {
            ARBDirectStateAccess.glTextureParameteriv((int)texture, (int)pname, (int[])params);
        }

        @Override
        public void readBuffer(int framebuffer, int buffer) {
            ARBDirectStateAccess.glNamedFramebufferReadBuffer((int)framebuffer, (int)buffer);
        }

        @Override
        public void drawBuffers(int framebuffer, int[] buffers) {
            ARBDirectStateAccess.glNamedFramebufferDrawBuffers((int)framebuffer, (int[])buffers);
        }

        @Override
        public int getTexParameteri(int texture, int target, int pname) {
            return ARBDirectStateAccess.glGetTextureParameteri((int)texture, (int)pname);
        }

        @Override
        public void copyTexSubImage2D(int destTexture, int target, int i, int i1, int i2, int i3, int i4, int width, int height) {
            ARBDirectStateAccess.glCopyTextureSubImage2D((int)destTexture, (int)i, (int)i1, (int)i2, (int)i3, (int)i4, (int)width, (int)height);
        }

        @Override
        public void bindTextureToUnit(int target, int unit, int texture) {
            if (GlStateManagerAccessor.getTEXTURES()[unit].f_84801_ == texture) {
                return;
            }
            ARBDirectStateAccess.glBindTextureUnit((int)unit, (int)texture);
            GlStateManagerAccessor.getTEXTURES()[unit].f_84801_ = texture;
        }

        @Override
        public int bufferStorage(int target, float[] data, int usage) {
            int buffer = GL45C.glCreateBuffers();
            GL45C.glNamedBufferData((int)buffer, (float[])data, (int)usage);
            return buffer;
        }

        @Override
        public int createBuffers() {
            return ARBDirectStateAccess.glCreateBuffers();
        }

        @Override
        public void blitFramebuffer(int source, int dest, int offsetX, int offsetY, int width, int height, int offsetX2, int offsetY2, int width2, int height2, int bufferChoice, int filter) {
            ARBDirectStateAccess.glBlitNamedFramebuffer((int)source, (int)dest, (int)offsetX, (int)offsetY, (int)width, (int)height, (int)offsetX2, (int)offsetY2, (int)width2, (int)height2, (int)bufferChoice, (int)filter);
        }

        @Override
        public void framebufferTexture2D(int fb, int fbtarget, int attachment, int target, int texture, int levels) {
            ARBDirectStateAccess.glNamedFramebufferTexture((int)fb, (int)attachment, (int)texture, (int)levels);
        }

        @Override
        public int createFramebuffer() {
            return ARBDirectStateAccess.glCreateFramebuffers();
        }

        @Override
        public int createTexture(int target) {
            return ARBDirectStateAccess.glCreateTextures((int)target);
        }
    }

    public static class DSAUnsupported
    implements DSAAccess {
        @Override
        public void generateMipmaps(int texture, int target) {
            GlStateManager._bindTexture((int)texture);
            GL32C.glGenerateMipmap((int)target);
        }

        @Override
        public void texParameteri(int texture, int target, int pname, int param) {
            IrisRenderSystem.bindTextureForSetup(target, texture);
            GL32C.glTexParameteri((int)target, (int)pname, (int)param);
        }

        @Override
        public void texParameterf(int texture, int target, int pname, float param) {
            IrisRenderSystem.bindTextureForSetup(target, texture);
            GL32C.glTexParameterf((int)target, (int)pname, (float)param);
        }

        @Override
        public void texParameteriv(int texture, int target, int pname, int[] params) {
            IrisRenderSystem.bindTextureForSetup(target, texture);
            GL32C.glTexParameteriv((int)target, (int)pname, (int[])params);
        }

        @Override
        public void readBuffer(int framebuffer, int buffer) {
            GlStateManager._glBindFramebuffer((int)36160, (int)framebuffer);
            GL32C.glReadBuffer((int)buffer);
        }

        @Override
        public void drawBuffers(int framebuffer, int[] buffers) {
            GlStateManager._glBindFramebuffer((int)36160, (int)framebuffer);
            GL32C.glDrawBuffers((int[])buffers);
        }

        @Override
        public int getTexParameteri(int texture, int target, int pname) {
            IrisRenderSystem.bindTextureForSetup(target, texture);
            return GL32C.glGetTexParameteri((int)target, (int)pname);
        }

        @Override
        public void copyTexSubImage2D(int destTexture, int target, int i, int i1, int i2, int i3, int i4, int width, int height) {
            int previous = GlStateManagerAccessor.getTEXTURES()[GlStateManagerAccessor.getActiveTexture()].f_84801_;
            GlStateManager._bindTexture((int)destTexture);
            GL32C.glCopyTexSubImage2D((int)target, (int)i, (int)i1, (int)i2, (int)i3, (int)i4, (int)width, (int)height);
            GlStateManager._bindTexture((int)previous);
        }

        @Override
        public void bindTextureToUnit(int target, int unit, int texture) {
            int activeTexture = GlStateManager._getActiveTexture();
            GlStateManager._activeTexture((int)(33984 + unit));
            IrisRenderSystem.bindTextureForSetup(target, texture);
            GlStateManager._activeTexture((int)activeTexture);
        }

        @Override
        public int bufferStorage(int target, float[] data, int usage) {
            int buffer = GlStateManager._glGenBuffers();
            GlStateManager._glBindBuffer((int)target, (int)buffer);
            IrisRenderSystem.bufferData(target, data, usage);
            GlStateManager._glBindBuffer((int)target, (int)0);
            return buffer;
        }

        @Override
        public void blitFramebuffer(int source, int dest, int offsetX, int offsetY, int width, int height, int offsetX2, int offsetY2, int width2, int height2, int bufferChoice, int filter) {
            GlStateManager._glBindFramebuffer((int)36008, (int)source);
            GlStateManager._glBindFramebuffer((int)36009, (int)dest);
            GL32C.glBlitFramebuffer((int)offsetX, (int)offsetY, (int)width, (int)height, (int)offsetX2, (int)offsetY2, (int)width2, (int)height2, (int)bufferChoice, (int)filter);
        }

        @Override
        public void framebufferTexture2D(int fb, int fbtarget, int attachment, int target, int texture, int levels) {
            GlStateManager._glBindFramebuffer((int)fbtarget, (int)fb);
            GL32C.glFramebufferTexture2D((int)fbtarget, (int)attachment, (int)target, (int)texture, (int)levels);
        }

        @Override
        public int createFramebuffer() {
            int framebuffer = GlStateManager.glGenFramebuffers();
            GlStateManager._glBindFramebuffer((int)36160, (int)framebuffer);
            return framebuffer;
        }

        @Override
        public int createTexture(int target) {
            int texture = GlStateManager._genTexture();
            GlStateManager._bindTexture((int)texture);
            return texture;
        }

        @Override
        public int createBuffers() {
            int value = GlStateManager._glGenBuffers();
            return value;
        }
    }
}

