/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiFramebuffer
 *  org.lwjgl.opengl.GL32
 */
package net.irisshaders.iris.compat.dh;

import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiFramebuffer;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import org.lwjgl.opengl.GL32;

public class DhFrameBufferWrapper
implements IDhApiFramebuffer {
    private final GlFramebuffer framebuffer;

    public DhFrameBufferWrapper(GlFramebuffer framebuffer) {
        this.framebuffer = framebuffer;
    }

    public boolean overrideThisFrame() {
        return true;
    }

    public void bind() {
        this.framebuffer.bind();
    }

    public void addDepthAttachment(int i, boolean b) {
    }

    public int getId() {
        return this.framebuffer.getId();
    }

    public int getStatus() {
        this.bind();
        return GL32.glCheckFramebufferStatus((int)36160);
    }

    public void addColorAttachment(int i, int i1) {
    }

    public void destroy() {
    }
}

