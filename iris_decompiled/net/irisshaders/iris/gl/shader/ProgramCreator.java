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
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.shader.GlShader;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgramCreator {
    private static final Logger LOGGER = LogManager.getLogger(ProgramCreator.class);

    public static int create(String name, GlShader ... shaders) {
        int result;
        int program = GlStateManager.glCreateProgram();
        GlStateManager._glBindAttribLocation((int)program, (int)11, (CharSequence)"iris_Entity");
        GlStateManager._glBindAttribLocation((int)program, (int)11, (CharSequence)"mc_Entity");
        GlStateManager._glBindAttribLocation((int)program, (int)12, (CharSequence)"mc_midTexCoord");
        GlStateManager._glBindAttribLocation((int)program, (int)13, (CharSequence)"at_tangent");
        GlStateManager._glBindAttribLocation((int)program, (int)14, (CharSequence)"at_midBlock");
        GlStateManager._glBindAttribLocation((int)program, (int)0, (CharSequence)"Position");
        GlStateManager._glBindAttribLocation((int)program, (int)1, (CharSequence)"UV0");
        for (GlShader shader : shaders) {
            GLDebug.nameObject(33505, shader.getHandle(), shader.getName());
            GlStateManager.glAttachShader((int)program, (int)shader.getHandle());
        }
        GlStateManager.glLinkProgram((int)program);
        GLDebug.nameObject(33506, program, name);
        for (GlShader shader : shaders) {
            IrisRenderSystem.detachShader(program, shader.getHandle());
        }
        String log = IrisRenderSystem.getProgramInfoLog(program);
        if (!log.isEmpty()) {
            LOGGER.warn("Program link log for " + name + ": " + log);
        }
        if ((result = GlStateManager.glGetProgrami((int)program, (int)35714)) != 1) {
            throw new ShaderCompileException(name, log);
        }
        return program;
    }
}

