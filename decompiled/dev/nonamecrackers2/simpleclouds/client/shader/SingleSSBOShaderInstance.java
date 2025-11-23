/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  org.lwjgl.opengl.GL43
 */
package dev.nonamecrackers2.simpleclouds.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.BindingManager;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.lwjgl.opengl.GL43;

public class SingleSSBOShaderInstance
extends ShaderInstance {
    private int binding = -1;

    public SingleSSBOShaderInstance(ResourceProvider provider, ResourceLocation shaderLocation, VertexFormat format, String ssboName) throws IOException {
        super(provider, shaderLocation, format);
        int index = GL43.glGetProgramResourceIndex((int)this.m_108943_(), (int)37606, (CharSequence)ssboName);
        if (index == -1) {
            throw new NullPointerException("Unknown block index with name '" + ssboName + "'");
        }
        this.binding = BindingManager.getAvailableShaderStorageBinding();
        GL43.glShaderStorageBlockBinding((int)this.m_108943_(), (int)index, (int)this.binding);
        BindingManager.useShaderStorageBinding(this.binding);
    }

    public int getShaderStorageBinding() {
        return this.binding;
    }

    public void close() {
        super.close();
        BindingManager.freeShaderStorageBinding(this.binding);
        this.binding = -1;
    }
}

