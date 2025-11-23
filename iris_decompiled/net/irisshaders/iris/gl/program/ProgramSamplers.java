/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.UnmodifiableIterator
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 */
package net.irisshaders.iris.gl.program;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.program.GlUniform1iCall;
import net.irisshaders.iris.gl.sampler.GlSampler;
import net.irisshaders.iris.gl.sampler.SamplerBinding;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.sampler.SamplerLimits;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import net.irisshaders.iris.gl.texture.TextureAccess;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;

public class ProgramSamplers {
    private static ProgramSamplers active;
    private final ImmutableList<SamplerBinding> samplerBindings;
    private final ImmutableList<ValueUpdateNotifier> notifiersToReset;
    private List<GlUniform1iCall> initializer;

    private ProgramSamplers(ImmutableList<SamplerBinding> samplerBindings, ImmutableList<ValueUpdateNotifier> notifiersToReset, List<GlUniform1iCall> initializer) {
        this.samplerBindings = samplerBindings;
        this.notifiersToReset = notifiersToReset;
        this.initializer = initializer;
    }

    public static void clearActiveSamplers() {
        if (active != null) {
            active.removeListeners();
        }
        IrisRenderSystem.unbindAllSamplers();
    }

    public static Builder builder(int program, Set<Integer> reservedTextureUnits) {
        return new Builder(program, reservedTextureUnits);
    }

    public static CustomTextureSamplerInterceptor customTextureSamplerInterceptor(SamplerHolder samplerHolder, Object2ObjectMap<String, TextureAccess> customTextureIds) {
        return ProgramSamplers.customTextureSamplerInterceptor(samplerHolder, customTextureIds, (ImmutableSet<Integer>)ImmutableSet.of());
    }

    public static CustomTextureSamplerInterceptor customTextureSamplerInterceptor(SamplerHolder samplerHolder, Object2ObjectMap<String, TextureAccess> customTextureIds, ImmutableSet<Integer> flippedAtLeastOnceSnapshot) {
        return new CustomTextureSamplerInterceptor(samplerHolder, customTextureIds, flippedAtLeastOnceSnapshot);
    }

    public void update() {
        if (active != null) {
            active.removeListeners();
        }
        active = this;
        if (this.initializer != null) {
            for (GlUniform1iCall call : this.initializer) {
                RenderSystem.glUniform1i((int)call.location(), (int)call.value());
            }
            this.initializer = null;
        }
        int activeTexture = GlStateManagerAccessor.getActiveTexture();
        for (SamplerBinding samplerBinding : this.samplerBindings) {
            samplerBinding.update();
        }
        RenderSystem.activeTexture((int)(33984 + activeTexture));
    }

    public void removeListeners() {
        active = null;
        for (ValueUpdateNotifier notifier : this.notifiersToReset) {
            notifier.setListener(null);
        }
    }

    public static final class Builder
    implements SamplerHolder {
        private final int program;
        private final ImmutableSet<Integer> reservedTextureUnits;
        private final ImmutableList.Builder<SamplerBinding> samplers;
        private final ImmutableList.Builder<ValueUpdateNotifier> notifiersToReset;
        private final List<GlUniform1iCall> calls;
        private int remainingUnits;
        private int nextUnit;

        private Builder(int program, Set<Integer> reservedTextureUnits) {
            this.program = program;
            this.reservedTextureUnits = ImmutableSet.copyOf(reservedTextureUnits);
            this.samplers = ImmutableList.builder();
            this.notifiersToReset = ImmutableList.builder();
            this.calls = new ArrayList<GlUniform1iCall>();
            int maxTextureUnits = SamplerLimits.get().getMaxTextureUnits();
            for (int unit : reservedTextureUnits) {
                if (unit < maxTextureUnits) continue;
                throw new IllegalStateException("Cannot mark texture unit " + unit + " as reserved because that texture unit isn't available on this system! Only " + maxTextureUnits + " texture units are available.");
            }
            this.remainingUnits = maxTextureUnits - reservedTextureUnits.size();
            while (reservedTextureUnits.contains(this.nextUnit)) {
                ++this.nextUnit;
            }
        }

        @Override
        public void addExternalSampler(int textureUnit, String ... names) {
            if (!this.reservedTextureUnits.contains((Object)textureUnit)) {
                throw new IllegalArgumentException("Cannot add an externally-managed sampler for texture unit " + textureUnit + " since it isn't in the set of reserved texture units.");
            }
            for (String name : names) {
                int location = GlStateManager._glGetUniformLocation((int)this.program, (CharSequence)name);
                if (location == -1) continue;
                this.calls.add(new GlUniform1iCall(location, textureUnit));
            }
        }

        @Override
        public boolean hasSampler(String name) {
            return GlStateManager._glGetUniformLocation((int)this.program, (CharSequence)name) != -1;
        }

        @Override
        public boolean addDefaultSampler(TextureType type, IntSupplier texture, ValueUpdateNotifier notifier, GlSampler sampler, String ... names) {
            if (this.nextUnit != 0) {
                throw new IllegalStateException("Texture unit 0 is already used.");
            }
            return this.addDynamicSampler(TextureType.TEXTURE_2D, texture, sampler, true, notifier, names);
        }

        @Override
        public boolean addDynamicSampler(TextureType type, IntSupplier texture, GlSampler sampler, String ... names) {
            return this.addDynamicSampler(type, texture, sampler, false, null, names);
        }

        @Override
        public boolean addDynamicSampler(TextureType type, IntSupplier texture, ValueUpdateNotifier notifier, GlSampler sampler, String ... names) {
            return this.addDynamicSampler(type, texture, sampler, false, notifier, names);
        }

        private boolean addDynamicSampler(TextureType type, IntSupplier texture, GlSampler sampler, boolean used, ValueUpdateNotifier notifier, String ... names) {
            if (notifier != null) {
                this.notifiersToReset.add((Object)notifier);
            }
            for (String name : names) {
                int location = GlStateManager._glGetUniformLocation((int)this.program, (CharSequence)name);
                if (location == -1) continue;
                if (this.remainingUnits <= 0) {
                    throw new IllegalStateException("No more available texture units while activating sampler " + name);
                }
                this.calls.add(new GlUniform1iCall(location, this.nextUnit));
                used = true;
            }
            if (!used) {
                return false;
            }
            this.samplers.add((Object)new SamplerBinding(type, this.nextUnit, texture, sampler, notifier));
            --this.remainingUnits;
            ++this.nextUnit;
            while (this.remainingUnits > 0 && this.reservedTextureUnits.contains((Object)this.nextUnit)) {
                ++this.nextUnit;
            }
            return true;
        }

        public ProgramSamplers build() {
            return new ProgramSamplers((ImmutableList<SamplerBinding>)this.samplers.build(), (ImmutableList<ValueUpdateNotifier>)this.notifiersToReset.build(), this.calls);
        }
    }

    public static final class CustomTextureSamplerInterceptor
    implements SamplerHolder {
        private final SamplerHolder samplerHolder;
        private final Object2ObjectMap<String, TextureAccess> customTextureIds;
        private final ImmutableSet<String> deactivatedOverrides;

        private CustomTextureSamplerInterceptor(SamplerHolder samplerHolder, Object2ObjectMap<String, TextureAccess> customTextureIds, ImmutableSet<Integer> flippedAtLeastOnceSnapshot) {
            this.samplerHolder = samplerHolder;
            this.customTextureIds = customTextureIds;
            ImmutableSet.Builder deactivatedOverrides = new ImmutableSet.Builder();
            UnmodifiableIterator unmodifiableIterator = flippedAtLeastOnceSnapshot.iterator();
            while (unmodifiableIterator.hasNext()) {
                int deactivatedOverride = (Integer)unmodifiableIterator.next();
                deactivatedOverrides.add((Object)("colortex" + deactivatedOverride));
                if (deactivatedOverride >= PackRenderTargetDirectives.LEGACY_RENDER_TARGETS.size()) continue;
                deactivatedOverrides.add((Object)((String)PackRenderTargetDirectives.LEGACY_RENDER_TARGETS.get(deactivatedOverride)));
            }
            this.deactivatedOverrides = deactivatedOverrides.build();
        }

        private IntSupplier getOverride(IntSupplier existing, String ... names) {
            for (String name : names) {
                if (!this.customTextureIds.containsKey((Object)name) || this.deactivatedOverrides.contains((Object)name)) continue;
                return ((TextureAccess)this.customTextureIds.get((Object)name)).getTextureId();
            }
            return existing;
        }

        @Override
        public void addExternalSampler(int textureUnit, String ... names) {
            IntSupplier override = this.getOverride(null, names);
            if (override != null) {
                if (textureUnit == 0) {
                    this.samplerHolder.addDefaultSampler(override, names);
                } else {
                    this.samplerHolder.addDynamicSampler(override, names);
                }
            } else {
                this.samplerHolder.addExternalSampler(textureUnit, names);
            }
        }

        @Override
        public boolean hasSampler(String name) {
            return this.samplerHolder.hasSampler(name);
        }

        @Override
        public boolean addDefaultSampler(IntSupplier sampler, String ... names) {
            sampler = this.getOverride(sampler, names);
            return this.samplerHolder.addDefaultSampler(sampler, names);
        }

        @Override
        public boolean addDefaultSampler(TextureType type, IntSupplier texture, ValueUpdateNotifier notifier, GlSampler sampler, String ... names) {
            texture = this.getOverride(texture, names);
            return this.samplerHolder.addDefaultSampler(type, texture, notifier, sampler, names);
        }

        @Override
        public boolean addDynamicSampler(IntSupplier sampler, String ... names) {
            sampler = this.getOverride(sampler, names);
            return this.samplerHolder.addDynamicSampler(sampler, names);
        }

        @Override
        public boolean addDynamicSampler(TextureType type, IntSupplier texture, GlSampler sampler, String ... names) {
            texture = this.getOverride(texture, names);
            return this.samplerHolder.addDynamicSampler(type, texture, sampler, names);
        }

        @Override
        public boolean addDynamicSampler(IntSupplier sampler, ValueUpdateNotifier notifier, String ... names) {
            sampler = this.getOverride(sampler, names);
            return this.samplerHolder.addDynamicSampler(sampler, notifier, names);
        }

        @Override
        public boolean addDynamicSampler(TextureType type, IntSupplier texture, ValueUpdateNotifier notifier, GlSampler sampler, String ... names) {
            return false;
        }
    }
}

