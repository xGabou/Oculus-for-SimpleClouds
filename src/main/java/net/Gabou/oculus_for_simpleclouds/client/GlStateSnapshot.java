package net.Gabou.oculus_for_simpleclouds.client;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Exact OpenGL state snapshot for OFSC's raw LWJGL fullscreen passes.
 *
 * <p>These passes intentionally avoid Minecraft's cached state wrappers. Restoring the exact raw
 * values is important: leaving an OFSC texture bound while Minecraft still believes its previous
 * texture is bound can corrupt later shaderpack passes, especially translucent water.</p>
 */
public final class GlStateSnapshot implements AutoCloseable {
    private final int program;
    private final int readFramebuffer;
    private final int drawFramebuffer;
    private final int vertexArray;
    private final int arrayBuffer;
    private final int activeTexture;
    private final int[] texture2dBindings;
    private final int[] viewport = new int[4];
    private final int[] scissorBox = new int[4];
    private final boolean depthTest;
    private final boolean blend;
    private final boolean cull;
    private final boolean scissor;
    private final boolean stencil;
    private final boolean depthMask;
    private final int depthFunc;
    private final int blendSrcRgb;
    private final int blendDstRgb;
    private final int blendSrcAlpha;
    private final int blendDstAlpha;
    private final int blendEquationRgb;
    private final int blendEquationAlpha;
    private final int cullMode;
    private final int frontFace;
    private final boolean[] colorMask = new boolean[4];
    private boolean restored;

    private GlStateSnapshot(int highestTextureUnit) {
        this.program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        this.readFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        this.drawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        this.vertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        this.arrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        this.activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        int supportedUnits = Math.max(1, GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));
        int unitCount = Math.max(1, Math.min(highestTextureUnit + 1, supportedUnits));
        this.texture2dBindings = new int[unitCount];
        for (int unit = 0; unit < unitCount; unit++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
            this.texture2dBindings[unit] = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        }
        GL13.glActiveTexture(this.activeTexture);

        readInt4(GL11.GL_VIEWPORT, this.viewport);
        readInt4(GL11.GL_SCISSOR_BOX, this.scissorBox);
        this.depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        this.blend = GL11.glIsEnabled(GL11.GL_BLEND);
        this.cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        this.scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        this.stencil = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
        this.depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        this.depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        this.blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        this.blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        this.blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        this.blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        this.blendEquationRgb = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
        this.blendEquationAlpha = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
        this.cullMode = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE);
        this.frontFace = GL11.glGetInteger(GL11.GL_FRONT_FACE);

        ByteBuffer mask = BufferUtils.createByteBuffer(4);
        GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, mask);
        for (int i = 0; i < this.colorMask.length; i++) {
            this.colorMask[i] = mask.get(i) != 0;
        }
    }

    public static GlStateSnapshot capture(int highestTextureUnit) {
        return new GlStateSnapshot(Math.max(0, highestTextureUnit));
    }

    public void prepareFullscreen() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColorMask(true, true, true, true);
    }

    @Override
    public void close() {
        if (this.restored) {
            return;
        }
        this.restored = true;

        GL20.glUseProgram(this.program);
        GL30.glBindVertexArray(this.vertexArray);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.arrayBuffer);

        for (int unit = 0; unit < this.texture2dBindings.length; unit++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture2dBindings[unit]);
        }
        GL13.glActiveTexture(this.activeTexture);

        GL20.glBlendEquationSeparate(this.blendEquationRgb, this.blendEquationAlpha);
        GL14.glBlendFuncSeparate(this.blendSrcRgb, this.blendDstRgb, this.blendSrcAlpha, this.blendDstAlpha);
        GL11.glDepthFunc(this.depthFunc);
        GL11.glDepthMask(this.depthMask);
        GL11.glColorMask(this.colorMask[0], this.colorMask[1], this.colorMask[2], this.colorMask[3]);
        GL11.glCullFace(this.cullMode);
        GL11.glFrontFace(this.frontFace);
        GL11.glScissor(this.scissorBox[0], this.scissorBox[1], this.scissorBox[2], this.scissorBox[3]);

        setEnabled(GL11.GL_DEPTH_TEST, this.depthTest);
        setEnabled(GL11.GL_BLEND, this.blend);
        setEnabled(GL11.GL_CULL_FACE, this.cull);
        setEnabled(GL11.GL_SCISSOR_TEST, this.scissor);
        setEnabled(GL11.GL_STENCIL_TEST, this.stencil);

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.readFramebuffer);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.drawFramebuffer);
        GL11.glViewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
    }

    private static void setEnabled(int capability, boolean enabled) {
        if (enabled) {
            GL11.glEnable(capability);
        } else {
            GL11.glDisable(capability);
        }
    }

    private static void readInt4(int parameter, int[] destination) {
        IntBuffer values = BufferUtils.createIntBuffer(4);
        GL11.glGetIntegerv(parameter, values);
        for (int i = 0; i < 4; i++) {
            destination[i] = values.get(i);
        }
    }
}
