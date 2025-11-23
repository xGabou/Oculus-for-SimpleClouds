/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.platform.GlStateManager
 *  net.minecraft.client.Minecraft
 *  org.lwjgl.BufferUtils
 */
package net.irisshaders.iris.gl.program;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.uniform.DynamicLocationalUniformHolder;
import net.irisshaders.iris.gl.uniform.Uniform;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformType;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;

public class ProgramUniforms {
    private static ProgramUniforms active;
    private final ImmutableList<Uniform> perTick;
    private final ImmutableList<Uniform> perFrame;
    private final ImmutableList<Uniform> dynamic;
    private final ImmutableList<ValueUpdateNotifier> notifiersToReset;
    long lastTick = -1L;
    int lastFrame = -1;
    private ImmutableList<Uniform> once;

    public ProgramUniforms(ImmutableList<Uniform> once, ImmutableList<Uniform> perTick, ImmutableList<Uniform> perFrame, ImmutableList<Uniform> dynamic, ImmutableList<ValueUpdateNotifier> notifiersToReset) {
        this.once = once;
        this.perTick = perTick;
        this.perFrame = perFrame;
        this.dynamic = dynamic;
        this.notifiersToReset = notifiersToReset;
    }

    private static long getCurrentTick() {
        if (Minecraft.m_91087_().f_91073_ == null) {
            return 0L;
        }
        return Minecraft.m_91087_().f_91073_.m_46467_();
    }

    public static void clearActiveUniforms() {
        if (active != null) {
            active.removeListeners();
        }
    }

    public static Builder builder(String name, int program) {
        return new Builder(name, program);
    }

    private static String getTypeName(int type) {
        Object typeName = type == 5126 ? "float" : (type == 5124 ? "int" : (type == 35676 ? "mat4" : (type == 35666 ? "vec4" : (type == 35675 ? "mat3" : (type == 35665 ? "vec3" : (type == 35674 ? "mat2" : (type == 35664 ? "vec2" : (type == 35667 ? "ivec2" : (type == 35669 ? "ivec4" : (type == 35679 ? "sampler3D" : (type == 35678 ? "sampler2D" : (type == 36306 ? "usampler2D" : (type == 36307 ? "usampler3D" : (type == 35677 ? "sampler1D" : (type == 35682 ? "sampler2DShadow" : (type == 35681 ? "sampler1DShadow" : (type == 36940 ? "image1D" : (type == 36941 ? "image2D" : (type == 36942 ? "image3D" : (type == 36951 ? "iimage1D" : (type == 36952 ? "iimage2D" : (type == 36953 ? "iimage3D" : (type == 36962 ? "uimage1D" : (type == 36963 ? "uimage2D" : (type == 36964 ? "uimage3D" : "(unknown:" + type + ")")))))))))))))))))))))))));
        return typeName;
    }

    private static UniformType getExpectedType(int type) {
        if (type == 5126) {
            return UniformType.FLOAT;
        }
        if (type == 5124) {
            return UniformType.INT;
        }
        if (type == 35670) {
            return UniformType.INT;
        }
        if (type == 35676) {
            return UniformType.MAT4;
        }
        if (type == 35666) {
            return UniformType.VEC4;
        }
        if (type == 35669) {
            return UniformType.VEC4I;
        }
        if (type == 35675) {
            return UniformType.MAT3;
        }
        if (type == 35665) {
            return UniformType.VEC3;
        }
        if (type == 35668) {
            return UniformType.VEC3I;
        }
        if (type == 35674) {
            return null;
        }
        if (type == 35664) {
            return UniformType.VEC2;
        }
        if (type == 35667) {
            return UniformType.VEC2I;
        }
        if (type == 35679) {
            return UniformType.INT;
        }
        if (type == 35678) {
            return UniformType.INT;
        }
        if (type == 36306) {
            return UniformType.INT;
        }
        if (type == 36307) {
            return UniformType.INT;
        }
        if (type == 35677) {
            return UniformType.INT;
        }
        if (type == 35682) {
            return UniformType.INT;
        }
        if (type == 35681) {
            return UniformType.INT;
        }
        return null;
    }

    private static boolean isSampler(int type) {
        return type == 35677 || type == 35678 || type == 36306 || type == 36307 || type == 35679 || type == 35681 || type == 35682;
    }

    private static boolean isImage(int type) {
        return type == 36940 || type == 36941 || type == 36962 || type == 36963 || type == 36964 || type == 36951 || type == 36952 || type == 36953 || type == 36942 || type == 36946 || type == 36947;
    }

    private void updateStage(ImmutableList<Uniform> uniforms) {
        for (Uniform uniform : uniforms) {
            uniform.update();
        }
    }

    public void update() {
        int currentFrame;
        if (active != null) {
            active.removeListeners();
        }
        active = this;
        this.updateStage(this.dynamic);
        if (this.once != null) {
            this.updateStage(this.once);
            this.updateStage(this.perTick);
            this.updateStage(this.perFrame);
            this.lastTick = ProgramUniforms.getCurrentTick();
            this.once = null;
            return;
        }
        long currentTick = ProgramUniforms.getCurrentTick();
        if (this.lastTick != currentTick) {
            this.lastTick = currentTick;
            this.updateStage(this.perTick);
        }
        if (this.lastFrame != (currentFrame = SystemTimeUniforms.COUNTER.getAsInt())) {
            this.lastFrame = currentFrame;
            this.updateStage(this.perFrame);
        }
    }

    public void removeListeners() {
        active = null;
        for (ValueUpdateNotifier notifier : this.notifiersToReset) {
            notifier.setListener(null);
        }
    }

    public static class Builder
    implements DynamicLocationalUniformHolder {
        private final String name;
        private final int program;
        private final Map<Integer, String> locations;
        private final Map<String, Uniform> once;
        private final Map<String, Uniform> perTick;
        private final Map<String, Uniform> perFrame;
        private final Map<String, Uniform> dynamic;
        private final Map<String, UniformType> uniformNames;
        private final Map<String, UniformType> externalUniformNames;
        private final List<ValueUpdateNotifier> notifiersToReset;

        protected Builder(String name, int program) {
            this.name = name;
            this.program = program;
            this.locations = new HashMap<Integer, String>();
            this.once = new HashMap<String, Uniform>();
            this.perTick = new HashMap<String, Uniform>();
            this.perFrame = new HashMap<String, Uniform>();
            this.dynamic = new HashMap<String, Uniform>();
            this.uniformNames = new HashMap<String, UniformType>();
            this.externalUniformNames = new HashMap<String, UniformType>();
            this.notifiersToReset = new ArrayList<ValueUpdateNotifier>();
        }

        @Override
        public Builder addUniform(UniformUpdateFrequency updateFrequency, Uniform uniform) {
            Objects.requireNonNull(uniform);
            switch (updateFrequency) {
                case ONCE: {
                    this.once.put(this.locations.get(uniform.getLocation()), uniform);
                    break;
                }
                case PER_TICK: {
                    this.perTick.put(this.locations.get(uniform.getLocation()), uniform);
                    break;
                }
                case PER_FRAME: {
                    this.perFrame.put(this.locations.get(uniform.getLocation()), uniform);
                }
            }
            return this;
        }

        @Override
        public OptionalInt location(String name, UniformType type) {
            int id = GlStateManager._glGetUniformLocation((int)this.program, (CharSequence)name);
            if (id == -1) {
                return OptionalInt.empty();
            }
            if (this.locations.containsKey(id) || this.uniformNames.containsKey(name)) {
                Iris.logger.warn("[" + this.name + "] Duplicate uniform: " + type.toString().toLowerCase() + " " + name);
                return OptionalInt.empty();
            }
            this.locations.put(id, name);
            this.uniformNames.put(name, type);
            return OptionalInt.of(id);
        }

        public ProgramUniforms buildUniforms() {
            int activeUniforms = GlStateManager.glGetProgrami((int)this.program, (int)35718);
            IntBuffer sizeBuf = BufferUtils.createIntBuffer((int)1);
            IntBuffer typeBuf = BufferUtils.createIntBuffer((int)1);
            for (int index = 0; index < activeUniforms; ++index) {
                String name = IrisRenderSystem.getActiveUniform(this.program, index, 128, sizeBuf, typeBuf);
                if (name.isEmpty()) continue;
                int size = sizeBuf.get(0);
                int type = typeBuf.get(0);
                UniformType provided = this.uniformNames.get(name);
                UniformType expected = ProgramUniforms.getExpectedType(type);
                if (provided == null || provided == expected) continue;
                Object expectedName = expected != null ? expected.toString() : "(unsupported type: " + ProgramUniforms.getTypeName(type) + ")";
                Iris.logger.error("[" + this.name + "] Wrong uniform type for " + name + ": Iris is providing " + provided + " but the program expects " + (String)expectedName + ". Disabling that uniform.");
                this.once.remove(name);
                this.perTick.remove(name);
                this.perFrame.remove(name);
                this.dynamic.remove(name);
            }
            return new ProgramUniforms((ImmutableList<Uniform>)ImmutableList.copyOf(this.once.values()), (ImmutableList<Uniform>)ImmutableList.copyOf(this.perTick.values()), (ImmutableList<Uniform>)ImmutableList.copyOf(this.perFrame.values()), (ImmutableList<Uniform>)ImmutableList.copyOf(this.dynamic.values()), (ImmutableList<ValueUpdateNotifier>)ImmutableList.copyOf(this.notifiersToReset));
        }

        @Override
        public Builder addDynamicUniform(Uniform uniform, ValueUpdateNotifier notifier) {
            Objects.requireNonNull(uniform);
            Objects.requireNonNull(notifier);
            this.dynamic.put(this.locations.get(uniform.getLocation()), uniform);
            this.notifiersToReset.add(notifier);
            return this;
        }

        @Override
        public UniformHolder externallyManagedUniform(String name, UniformType type) {
            this.externalUniformNames.put(name, type);
            return this;
        }
    }
}

