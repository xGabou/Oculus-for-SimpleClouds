/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.shaderpack.properties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.shaderpack.parsing.DirectiveHolder;
import org.joml.Vector4f;

public class PackRenderTargetDirectives {
    public static final ImmutableList<String> LEGACY_RENDER_TARGETS = ImmutableList.of((Object)"gcolor", (Object)"gdepth", (Object)"gnormal", (Object)"composite", (Object)"gaux1", (Object)"gaux2", (Object)"gaux3", (Object)"gaux4");
    public static final Set<Integer> BASELINE_SUPPORTED_RENDER_TARGETS;
    private final Int2ObjectMap<RenderTargetSettings> renderTargetSettings = new Int2ObjectOpenHashMap();

    PackRenderTargetDirectives(Set<Integer> supportedRenderTargets) {
        supportedRenderTargets.forEach(index -> this.renderTargetSettings.put(index.intValue(), (Object)new RenderTargetSettings()));
    }

    public IntList getBuffersToBeCleared() {
        IntArrayList buffersToBeCleared = new IntArrayList();
        this.renderTargetSettings.forEach((arg_0, arg_1) -> PackRenderTargetDirectives.lambda$getBuffersToBeCleared$1((IntList)buffersToBeCleared, arg_0, arg_1));
        return buffersToBeCleared;
    }

    public Map<Integer, RenderTargetSettings> getRenderTargetSettings() {
        return Collections.unmodifiableMap(this.renderTargetSettings);
    }

    public void acceptDirectives(DirectiveHolder directives) {
        Optional.ofNullable((RenderTargetSettings)this.renderTargetSettings.get(7)).ifPresent(colortex7 -> directives.acceptCommentStringDirective("GAUX4FORMAT", format -> {
            if ("RGBA32F".equals(format)) {
                colortex7.requestedFormat = InternalTextureFormat.RGBA32F;
            } else if ("RGB32F".equals(format)) {
                colortex7.requestedFormat = InternalTextureFormat.RGB32F;
            } else if ("RGB16".equals(format)) {
                colortex7.requestedFormat = InternalTextureFormat.RGB16;
            } else {
                Iris.logger.warn("Ignoring GAUX4FORMAT directive /* GAUX4FORMAT:" + format + "*/ because " + format + " must be RGBA32F, RGB32F, or RGB16. Use `const int colortex7Format = " + format + ";` + instead.");
            }
        }));
        Optional.ofNullable((RenderTargetSettings)this.renderTargetSettings.get(1)).ifPresent(gdepth -> directives.acceptUniformDirective("gdepth", () -> {
            if (gdepth.requestedFormat == InternalTextureFormat.RGBA) {
                gdepth.requestedFormat = InternalTextureFormat.RGBA32F;
            }
        }));
        this.renderTargetSettings.forEach((index, settings) -> {
            this.acceptBufferDirectives(directives, (RenderTargetSettings)settings, "colortex" + index);
            if (index < LEGACY_RENDER_TARGETS.size()) {
                this.acceptBufferDirectives(directives, (RenderTargetSettings)settings, (String)LEGACY_RENDER_TARGETS.get(index.intValue()));
            }
        });
    }

    private void acceptBufferDirectives(DirectiveHolder directives, RenderTargetSettings settings, String bufferName) {
        directives.acceptConstStringDirective(bufferName + "Format", format -> {
            Optional<InternalTextureFormat> internalFormat = InternalTextureFormat.fromString(format);
            if (internalFormat.isPresent()) {
                settings.requestedFormat = internalFormat.get();
            } else {
                Iris.logger.warn("Unrecognized internal texture format " + format + " specified for " + bufferName + "Format, ignoring.");
            }
        });
        directives.acceptConstBooleanDirective(bufferName + "Clear", shouldClear -> {
            settings.clear = shouldClear;
        });
        directives.acceptConstVec4Directive(bufferName + "ClearColor", clearColor -> {
            settings.clearColor = clearColor;
        });
    }

    private static /* synthetic */ void lambda$getBuffersToBeCleared$1(IntList buffersToBeCleared, Integer index, RenderTargetSettings settings) {
        if (settings.shouldClear()) {
            buffersToBeCleared.add(index.intValue());
        }
    }

    static {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (int i = 0; i < 16; ++i) {
            builder.add((Object)i);
        }
        BASELINE_SUPPORTED_RENDER_TARGETS = builder.build();
    }

    public static final class RenderTargetSettings {
        private InternalTextureFormat requestedFormat = InternalTextureFormat.RGBA;
        private boolean clear = true;
        private Vector4f clearColor = null;

        public InternalTextureFormat getInternalFormat() {
            return this.requestedFormat;
        }

        public boolean shouldClear() {
            return this.clear;
        }

        public Optional<Vector4f> getClearColor() {
            return Optional.ofNullable(this.clearColor);
        }

        public String toString() {
            return "RenderTargetSettings{requestedFormat=" + this.requestedFormat + ", clear=" + this.clear + ", clearColor=" + this.clearColor + "}";
        }
    }
}

