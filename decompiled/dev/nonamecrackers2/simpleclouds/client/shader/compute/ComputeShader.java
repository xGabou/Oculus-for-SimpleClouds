/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.preprocessor.GlslPreprocessor
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.FileUtil
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraftforge.client.ForgeHooksClient
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL41
 *  org.lwjgl.opengl.GL42
 *  org.lwjgl.opengl.GL43
 */
package dev.nonamecrackers2.simpleclouds.client.shader.compute;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.BindingManager;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.ShaderStorageBufferObject;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.UniqueBinding;
import dev.nonamecrackers2.simpleclouds.client.shader.buffer.WithBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

public class ComputeShader {
    protected static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/ComputeShader");
    private static final Pattern LOCAL_GROUP_REPLACER = Pattern.compile("\\$\\{.*?\\}");
    private static final Map<String, CompiledShader> COMPILED_PROGRAMS = Maps.newHashMap();
    private static int maxGroupX = -1;
    private static int maxGroupY = -1;
    private static int maxGroupZ = -1;
    private static int maxLocalGroupX = -1;
    private static int maxLocalGroupY = -1;
    private static int maxLocalGroupZ = -1;
    private static int maxLocalInvocations = -1;
    private int id;
    private final CompiledShader compiledShader;
    private final String name;
    private final Map<String, WithBinding> unique = Maps.newHashMap();
    private final List<String> missingUniformErrors = Lists.newArrayList();

    private ComputeShader(int id, CompiledShader compiledShader, String name) {
        this.id = id;
        this.compiledShader = compiledShader;
        this.compiledShader.attachToShader(this);
        this.name = name;
    }

    public void close() {
        RenderSystem.assertOnRenderThread();
        LOGGER.debug("Closing compute shader id={}", (Object)this.id);
        this.unique.values().forEach(buffer -> {
            buffer.close();
            BindingManager.freeShaderStorageBinding(buffer.getBinding());
        });
        this.unique.clear();
        if (this.id != -1) {
            GlStateManager.glDeleteProgram((int)this.id);
            this.id = -1;
        }
        this.compiledShader.close();
        this.missingUniformErrors.clear();
    }

    public void forUniform(String name, BiConsumer<Integer, Integer> consumer) {
        RenderSystem.assertOnGameThreadOrInit();
        this.assertValid();
        int loc = Uniform.m_85624_((int)this.id, (CharSequence)name);
        if (loc == -1 && !this.missingUniformErrors.contains(name)) {
            LOGGER.warn("Could not find uniform with name '{}'", (Object)name);
            this.missingUniformErrors.add(name);
        } else {
            consumer.accept(this.id, loc);
        }
    }

    private void setSampler(String name, Runnable binder, int id) {
        RenderSystem.assertOnGameThreadOrInit();
        this.assertValid();
        ProgramManager.m_85578_((int)this.id);
        int loc = Uniform.m_85624_((int)this.id, (CharSequence)name);
        if (loc == -1 && !this.missingUniformErrors.contains(name)) {
            LOGGER.warn("Could not find sampler with name '{}'", (Object)name);
            this.missingUniformErrors.add(name);
        } else {
            Uniform.m_85616_((int)loc, (int)id);
            RenderSystem.activeTexture((int)(33984 + id));
            binder.run();
        }
        ProgramManager.m_85578_((int)0);
    }

    public void setSampler2D(String name, int texture, int id) {
        this.setSampler(name, () -> RenderSystem.bindTexture((int)texture), id);
    }

    public void setSampler3D(String name, int texture, int id) {
        this.setSampler(name, () -> GL11.glBindTexture((int)32879, (int)texture), id);
    }

    public void setSampler2DArray(String name, int texture, int id) {
        this.setSampler(name, () -> GL11.glBindTexture((int)35866, (int)texture), id);
    }

    public ShaderStorageBufferObject createAndBindSSBO(String name, int usage) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.assertValid();
        if (this.unique.containsKey(name)) {
            throw new IllegalArgumentException("Buffer with name '" + name + "' is already defined");
        }
        int index = GL43.glGetProgramResourceIndex((int)this.id, (int)37606, (CharSequence)name);
        if (index == -1) {
            throw new NullPointerException("Unknown block index with name '" + name + "'");
        }
        int binding = BindingManager.getAvailableShaderStorageBinding();
        GL43.glShaderStorageBlockBinding((int)this.id, (int)index, (int)binding);
        int bufferId = GlStateManager._glGenBuffers();
        GL30.glBindBufferBase((int)37074, (int)binding, (int)bufferId);
        ShaderStorageBufferObject buffer = new ShaderStorageBufferObject(bufferId, binding, usage);
        this.unique.put(name, buffer);
        BindingManager.useShaderStorageBinding(binding);
        return buffer;
    }

    public int findAndUseSSBOBinding(String name) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.assertValid();
        if (this.unique.containsKey(name)) {
            throw new IllegalArgumentException("Buffer with name '" + name + "' is already defined");
        }
        int index = GL43.glGetProgramResourceIndex((int)this.id, (int)37606, (CharSequence)name);
        if (index == -1) {
            throw new NullPointerException("Unknown block index with name '" + name + "'");
        }
        int binding = BindingManager.getAvailableShaderStorageBinding();
        GL43.glShaderStorageBlockBinding((int)this.id, (int)index, (int)binding);
        this.unique.put(name, new UniqueBinding(binding));
        BindingManager.useShaderStorageBinding(binding);
        return binding;
    }

    public void setImageUnit(String name, int unit) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.assertValid();
        int loc = GL20.glGetUniformLocation((int)this.id, (CharSequence)name);
        if (loc == -1) {
            throw new NullPointerException("Unknown image with name '" + name + "'");
        }
        GL41.glProgramUniform1i((int)this.id, (int)loc, (int)unit);
    }

    private WithBinding getUniqueObject(String name) {
        RenderSystem.assertOnRenderThread();
        this.assertValid();
        return Objects.requireNonNull(this.unique.get(name), "Unknown buffer with name '" + name + "'");
    }

    public ShaderStorageBufferObject getShaderStorageBuffer(String name) {
        WithBinding unique = this.getUniqueObject(name);
        if (!(unique instanceof ShaderStorageBufferObject)) {
            throw new ClassCastException("Object with name '" + name + "' is not an SSBO object!");
        }
        return (ShaderStorageBufferObject)unique;
    }

    public int getShaderStorageBinding(String name) {
        return this.getUniqueObject(name).getBinding();
    }

    public void dispatch(int groupX, int groupY, int groupZ, boolean wait) {
        RenderSystem.assertOnRenderThread();
        this.assertValid();
        if (maxGroupX == -1 || maxGroupY == -1 || maxGroupZ == -1) {
            maxGroupX = GL30.glGetIntegeri((int)37310, (int)0);
            maxGroupY = GL30.glGetIntegeri((int)37310, (int)1);
            maxGroupZ = GL30.glGetIntegeri((int)37310, (int)2);
            LOGGER.debug("Max work group sizes: x={}, y={}, z={}", (Object)maxGroupX, (Object)maxGroupY, (Object)maxGroupZ);
        }
        if (groupX > maxGroupX || groupY > maxGroupY || groupZ > maxGroupZ) {
            throw new IllegalArgumentException("Work group count too large! Wanted: x=" + groupX + ", y=" + groupY + ", z=" + groupZ + "; Max allowed: x=" + maxGroupX + ", y=" + maxGroupY + ", z=" + maxGroupZ);
        }
        if (groupX <= 0 || groupY <= 0 || groupZ <= 0) {
            throw new IllegalArgumentException("Work group count must be greater than zero!");
        }
        ProgramManager.m_85578_((int)this.id);
        GL43.glDispatchCompute((int)groupX, (int)groupY, (int)groupZ);
        if (wait) {
            GL42.glMemoryBarrier((int)12324);
        }
        ProgramManager.m_85578_((int)0);
    }

    public void dispatchAndWait(int groupX, int groupY, int groupZ) {
        this.dispatch(groupX, groupY, groupZ, true);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        return "ComputeShader[id=" + this.id + ", name=" + this.name + "]";
    }

    private void assertValid() {
        if (!this.isValid()) {
            throw new IllegalStateException("Compute shader is no longer valid!");
        }
    }

    public boolean isValid() {
        return this.id != -1 && this.compiledShader.getId() != -1;
    }

    public static ComputeShader loadShader(ResourceLocation loc, ResourceProvider provider, int localX, int localY, int localZ) throws IOException {
        return ComputeShader.loadShader(loc, provider, localX, localY, localZ, (ImmutableMap<String, String>)ImmutableMap.of());
    }

    public static ComputeShader loadShader(ResourceLocation loc, final ResourceProvider provider, final int localX, final int localY, final int localZ, final ImmutableMap<String, String> parameters) throws IOException {
        CompiledShader compiledShader;
        if (maxLocalGroupX == -1 || maxLocalGroupY == -1 || maxLocalGroupZ == -1) {
            maxLocalGroupX = GL30.glGetIntegeri((int)37311, (int)0);
            maxLocalGroupY = GL30.glGetIntegeri((int)37311, (int)1);
            maxLocalGroupZ = GL30.glGetIntegeri((int)37311, (int)2);
            LOGGER.debug("Max local group sizes: x={}, y={}, z={}", (Object)maxLocalGroupX, (Object)maxLocalGroupY, (Object)maxLocalGroupZ);
        }
        if (maxLocalInvocations == -1) {
            maxLocalInvocations = GL11.glGetInteger((int)37099);
            LOGGER.debug("Max local group invocations: {}", (Object)maxLocalInvocations);
        }
        if (localX > maxLocalGroupX || localY > maxLocalGroupY || localZ > maxLocalGroupZ) {
            throw new IOException("Local group count too large! Wanted: x=" + localX + ", y=" + localY + ", z=" + localZ + "; Max allowed: x=" + maxLocalGroupX + ", y=" + maxLocalGroupY + ", z=" + maxLocalGroupZ);
        }
        if (localX <= 0 || localY <= 0 || localZ <= 0) {
            throw new IOException("Local group size must be greater than zero!");
        }
        if (localX * localY * localZ > maxLocalInvocations) {
            throw new IOException("The amount of local invocations (X * Y * Z) is greater than the maximum allowed! Wanted: " + localX * localY * localZ + "; Allowed: " + maxLocalInvocations);
        }
        String rawLoc = loc.toString();
        String compiledShaderId = rawLoc + ",params:" + parameters + ",local_size_x=" + localX + ",local_size_y=" + localY + ",local_size_z=" + localZ;
        if (COMPILED_PROGRAMS.containsKey(compiledShaderId)) {
            compiledShader = COMPILED_PROGRAMS.get(compiledShaderId);
        } else {
            String path = "shaders/compute/" + loc.m_135815_() + ".comp";
            ResourceLocation finalLoc = new ResourceLocation(loc.m_135827_(), path);
            Resource resource = provider.m_215593_(finalLoc);
            try (InputStream inputStream = resource.m_215507_();){
                final String fullPath = FileUtil.m_179922_((String)path);
                compiledShader = ComputeShader.compileShader(loc.toString(), compiledShaderId, inputStream, resource.m_215506_(), new GlslPreprocessor(){
                    private final Set<String> importedPaths = Sets.newHashSet();

                    public List<String> m_166461_(String file) {
                        file = LOCAL_GROUP_REPLACER.matcher(file).replaceAll(result -> {
                            String group = result.group();
                            for (Map.Entry entry : parameters.entrySet()) {
                                String param = "${" + (String)entry.getKey() + "}";
                                if (!param.equals(group)) continue;
                                return (String)entry.getValue();
                            }
                            switch (group) {
                                case "${LOCAL_SIZE_X}": {
                                    return String.valueOf(localX);
                                }
                                case "${LOCAL_SIZE_Y}": {
                                    return String.valueOf(localY);
                                }
                                case "${LOCAL_SIZE_Z}": {
                                    return String.valueOf(localZ);
                                }
                            }
                            LOGGER.error("Unknown variable '{}'" + group);
                            return group;
                        });
                        return super.m_166461_(file);
                    }

                    public String m_142138_(boolean isRelative, String importPath) {
                        String string;
                        block9: {
                            ResourceLocation glslImport = ForgeHooksClient.getShaderImportLocation((String)fullPath, (boolean)isRelative, (String)importPath);
                            if (!this.importedPaths.add(glslImport.toString())) {
                                return null;
                            }
                            BufferedReader reader = provider.m_215597_(glslImport);
                            try {
                                string = IOUtils.toString((Reader)reader);
                                if (reader == null) break block9;
                            }
                            catch (Throwable throwable) {
                                try {
                                    if (reader != null) {
                                        try {
                                            ((Reader)reader).close();
                                        }
                                        catch (Throwable throwable2) {
                                            throwable.addSuppressed(throwable2);
                                        }
                                    }
                                    throw throwable;
                                }
                                catch (IOException ioexception) {
                                    LOGGER.error("Could not open GLSL import {}: {}", (Object)glslImport, (Object)ioexception.getMessage());
                                    return "#error " + ioexception.getMessage();
                                }
                            }
                            ((Reader)reader).close();
                        }
                        return string;
                    }
                });
                COMPILED_PROGRAMS.put(compiledShaderId, compiledShader);
            }
        }
        int programId = ProgramManager.m_85577_();
        ComputeShader shader = new ComputeShader(programId, compiledShader, loc.toString());
        GlStateManager.glLinkProgram((int)programId);
        int i = GlStateManager.glGetProgrami((int)programId, (int)35714);
        if (i == 0) {
            throw new RuntimeException("An error occured when linking program containing computer shader " + loc + ". Log output: " + GlStateManager.glGetProgramInfoLog((int)programId, (int)32768));
        }
        return shader;
    }

    private static CompiledShader compileShader(String loc, String id, InputStream inputStream, String packId, GlslPreprocessor preprocessor) throws IOException {
        RenderSystem.assertOnRenderThread();
        String file = IOUtils.toString((InputStream)inputStream, (Charset)StandardCharsets.UTF_8);
        if (file == null) {
            throw new IOException("Could not load compute shader '" + loc + "'");
        }
        int shaderId = GlStateManager.glCreateShader((int)37305);
        GlStateManager.glShaderSource((int)shaderId, (List)preprocessor.m_166461_(file));
        GlStateManager.glCompileShader((int)shaderId);
        if (GlStateManager.glGetShaderi((int)shaderId, (int)35713) == 0) {
            String error = StringUtils.trim((String)GL20.glGetShaderInfoLog((int)shaderId, (int)32768));
            throw new IOException("Couldn't compile compute shader (" + packId + ", " + loc + ") : " + error);
        }
        return new CompiledShader(shaderId, id);
    }

    public static void destroyCompiledShaders() {
        Iterator<CompiledShader> iterator = COMPILED_PROGRAMS.values().iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().destroy()) continue;
            iterator.remove();
        }
    }

    public static class CompiledShader {
        private final String shaderId;
        private int id;
        private int references;

        protected CompiledShader(int id, String shaderId) {
            this.id = id;
            this.shaderId = shaderId;
        }

        public int getId() {
            return this.id;
        }

        public void attachToShader(ComputeShader shader) {
            if (this.id != -1) {
                ++this.references;
                RenderSystem.assertOnRenderThread();
                GlStateManager.glAttachShader((int)shader.getId(), (int)this.id);
                LOGGER.debug("Attached compiled shader id={} to compute shader id={}, total references={}", (Object)this.id, (Object)shader.getId(), (Object)this.references);
            }
        }

        public void close() {
            if (this.id == -1) {
                return;
            }
            --this.references;
            if (this.references <= 0 && this.destroy()) {
                COMPILED_PROGRAMS.remove(this.shaderId);
            }
        }

        private boolean destroy() {
            if (this.id != -1) {
                RenderSystem.assertOnRenderThread();
                GlStateManager.glDeleteShader((int)this.id);
                LOGGER.debug("Destroyed compiled shader id={}", (Object)this.id);
                this.id = -1;
                this.references = 0;
                return true;
            }
            return false;
        }
    }
}

