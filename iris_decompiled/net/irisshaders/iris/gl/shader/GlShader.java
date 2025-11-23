/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.irisshaders.iris.gl.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.GlResource;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.gl.shader.ShaderWorkarounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlShader
extends GlResource {
    private static final Logger LOGGER = LogManager.getLogger(GlShader.class);
    private final String name;

    public GlShader(ShaderType type, String name, String src) {
        super(GlShader.createShader(type, name, src));
        this.name = name;
    }

    private static int createShader(ShaderType type, String name, String src) {
        int result;
        int handle = GlStateManager.glCreateShader((int)type.id);
        ShaderWorkarounds.safeShaderSource(handle, src);
        GlStateManager.glCompileShader((int)handle);
        GLDebug.nameObject(33505, handle, name + "(" + type.name().toLowerCase(Locale.ROOT) + ")");
        String log = IrisRenderSystem.getShaderInfoLog(handle);
        if (!log.isEmpty()) {
            LOGGER.warn("Shader compilation log for " + name + ": " + log);
        }
        if ((result = GlStateManager.glGetShaderi((int)handle, (int)35713)) != 1) {
            throw new ShaderCompileException(name, log);
        }
        return handle;
    }

    public String getName() {
        return this.name;
    }

    public int getHandle() {
        return this.getGlId();
    }

    @Override
    protected void destroyInternal() {
        GlStateManager.glDeleteShader((int)this.getGlId());
    }
}

