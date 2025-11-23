/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package dev.nonamecrackers2.simpleclouds.client.shader.buffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.ShaderStorageBufferObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class BindingManager {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/BindingManager");
    private static final IntList ALL_SHADER_STORAGE_BINDINGS = new IntArrayList();
    private static final IntList ALL_IMAGE_BINDINGS = new IntArrayList();
    private static int maxSSBOBindings = -1;
    private static int maxImageUnits = -1;

    public static void printDebug() {
        LOGGER.debug("Binded SSBOs: {}", (Object)ALL_SHADER_STORAGE_BINDINGS);
        LOGGER.debug("Binded image units: {}", (Object)ALL_IMAGE_BINDINGS);
    }

    public static void fillReport(CrashReport report) {
        CrashReportCategory category = report.m_127514_("Simple Clouds Compute Shaders");
        category.m_128159_("Binded SSBOS", (Object)ALL_SHADER_STORAGE_BINDINGS);
        category.m_128159_("Binded Image Units", (Object)ALL_IMAGE_BINDINGS);
    }

    public static int getAvailableShaderStorageBinding() {
        RenderSystem.assertOnRenderThread();
        if (maxSSBOBindings == -1) {
            maxSSBOBindings = GL11.glGetInteger((int)37085);
        }
        for (int i = maxSSBOBindings - 1; i > 0; --i) {
            if (ALL_SHADER_STORAGE_BINDINGS.contains(i)) continue;
            return i;
        }
        throw new NullPointerException("No available buffer binding. Total available buffer bindings: " + maxSSBOBindings + ", used: " + ALL_SHADER_STORAGE_BINDINGS.size());
    }

    public static void useShaderStorageBinding(int binding) {
        RenderSystem.assertOnRenderThread();
        if (ALL_SHADER_STORAGE_BINDINGS.contains(binding)) {
            throw new IllegalArgumentException("Binding " + binding + " is already in use");
        }
        ALL_SHADER_STORAGE_BINDINGS.add(binding);
    }

    public static void freeShaderStorageBinding(int binding) {
        RenderSystem.assertOnRenderThread();
        ALL_SHADER_STORAGE_BINDINGS.remove((Object)binding);
    }

    public static int getAvailableImageUnit() {
        RenderSystem.assertOnRenderThread();
        if (maxImageUnits == -1) {
            maxImageUnits = GL11.glGetInteger((int)36664);
        }
        for (int i = maxImageUnits - 1; i > 0; --i) {
            if (ALL_IMAGE_BINDINGS.contains(i)) continue;
            return i;
        }
        throw new NullPointerException("No available image binding. Total available image units: " + maxImageUnits);
    }

    public static int useImageUnit(int unit) {
        RenderSystem.assertOnRenderThread();
        ALL_IMAGE_BINDINGS.add(unit);
        return unit;
    }

    public static void freeImageUnit(int unit) {
        RenderSystem.assertOnRenderThread();
        ALL_IMAGE_BINDINGS.remove((Object)unit);
    }

    public static ShaderStorageBufferObject createSSBO(int usage) {
        RenderSystem.assertOnRenderThreadOrInit();
        int binding = BindingManager.getAvailableShaderStorageBinding();
        int bufferId = GlStateManager._glGenBuffers();
        GL30.glBindBufferBase((int)37074, (int)binding, (int)bufferId);
        ShaderStorageBufferObject buffer = new ShaderStorageBufferObject(bufferId, binding, usage);
        BindingManager.useShaderStorageBinding(binding);
        return buffer;
    }

    public static void freeSSBO(ShaderStorageBufferObject buffer) {
        RenderSystem.assertOnRenderThread();
        BindingManager.freeShaderStorageBinding(buffer.getBinding());
        buffer.close();
    }
}

