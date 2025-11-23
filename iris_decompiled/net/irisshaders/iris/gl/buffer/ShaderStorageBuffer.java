/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 */
package net.irisshaders.iris.gl.buffer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.buffer.ShaderStorageInfo;

public class ShaderStorageBuffer {
    protected final int index;
    protected final ShaderStorageInfo info;
    protected int id = IrisRenderSystem.createBuffers();

    public ShaderStorageBuffer(int index, ShaderStorageInfo info) {
        GLDebug.nameObject(33504, this.id, "SSBO " + index);
        this.index = index;
        this.info = info;
    }

    public final int getIndex() {
        return this.index;
    }

    public final long getSize() {
        return this.info.size();
    }

    protected void destroy() {
        IrisRenderSystem.bindBufferBase(37074, this.index, 0);
        IrisRenderSystem.deleteBuffers(this.id);
    }

    public void bind() {
        IrisRenderSystem.bindBufferBase(37074, this.index, this.id);
    }

    public void resizeIfRelative(int width, int height) {
        if (!this.info.relative()) {
            return;
        }
        IrisRenderSystem.deleteBuffers(this.id);
        int newId = GlStateManager._glGenBuffers();
        GlStateManager._glBindBuffer((int)37074, (int)newId);
        int newWidth = (int)((float)width * this.info.scaleX());
        int newHeight = (int)((float)height * this.info.scaleY());
        int finalSize = newHeight * newWidth * this.info.size();
        IrisRenderSystem.bufferStorage(37074, finalSize, 0);
        IrisRenderSystem.clearBufferSubData(37074, 33321, 0L, finalSize, 6403, 5120, new int[]{0});
        IrisRenderSystem.bindBufferBase(37074, this.index, newId);
        this.id = newId;
    }

    public int getId() {
        return this.id;
    }
}

