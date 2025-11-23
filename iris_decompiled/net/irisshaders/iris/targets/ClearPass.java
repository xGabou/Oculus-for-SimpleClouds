/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.targets;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.minecraft.client.Minecraft;
import org.joml.Vector4f;

public class ClearPass {
    private final Vector4f color;
    private final IntSupplier viewportX;
    private final IntSupplier viewportY;
    private final GlFramebuffer framebuffer;
    private final int clearFlags;

    public ClearPass(Vector4f color, IntSupplier viewportX, IntSupplier viewportY, GlFramebuffer framebuffer, int clearFlags) {
        this.color = color;
        this.viewportX = viewportX;
        this.viewportY = viewportY;
        this.framebuffer = framebuffer;
        this.clearFlags = clearFlags;
    }

    public void execute(Vector4f defaultClearColor) {
        RenderSystem.viewport((int)0, (int)0, (int)this.viewportX.getAsInt(), (int)this.viewportY.getAsInt());
        this.framebuffer.bind();
        Vector4f color = Objects.requireNonNull(defaultClearColor);
        if (this.color != null) {
            color = this.color;
        }
        RenderSystem.clearColor((float)color.x, (float)color.y, (float)color.z, (float)color.w);
        RenderSystem.clear((int)this.clearFlags, (boolean)Minecraft.f_91002_);
    }

    public GlFramebuffer getFramebuffer() {
        return this.framebuffer;
    }
}

