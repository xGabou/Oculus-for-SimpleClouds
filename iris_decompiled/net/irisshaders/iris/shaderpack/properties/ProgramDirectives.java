/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.properties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendInformation;
import net.irisshaders.iris.gl.framebuffer.ViewportData;
import net.irisshaders.iris.shaderpack.parsing.CommentDirective;
import net.irisshaders.iris.shaderpack.parsing.CommentDirectiveParser;
import net.irisshaders.iris.shaderpack.parsing.ConstDirectiveParser;
import net.irisshaders.iris.shaderpack.parsing.DispatchingDirectiveHolder;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.jetbrains.annotations.Nullable;

public class ProgramDirectives {
    private static final ImmutableList<String> LEGACY_RENDER_TARGETS = PackRenderTargetDirectives.LEGACY_RENDER_TARGETS;
    private final int[] drawBuffers;
    private final ViewportData viewportScale;
    @Nullable
    private final AlphaTest alphaTestOverride;
    private final Optional<BlendModeOverride> blendModeOverride;
    private final List<BufferBlendInformation> bufferBlendInformations;
    private final ImmutableSet<Integer> mipmappedBuffers;
    private final ImmutableMap<Integer, Boolean> explicitFlips;
    private boolean unknownDrawBuffers;

    private ProgramDirectives(int[] drawBuffers, ViewportData viewportScale, @Nullable AlphaTest alphaTestOverride, Optional<BlendModeOverride> blendModeOverride, List<BufferBlendInformation> bufferBlendInformations, ImmutableSet<Integer> mipmappedBuffers, ImmutableMap<Integer, Boolean> explicitFlips) {
        this.drawBuffers = drawBuffers;
        this.viewportScale = viewportScale;
        this.alphaTestOverride = alphaTestOverride;
        this.blendModeOverride = blendModeOverride;
        this.bufferBlendInformations = bufferBlendInformations;
        this.mipmappedBuffers = mipmappedBuffers;
        this.explicitFlips = explicitFlips;
        this.unknownDrawBuffers = false;
    }

    public ProgramDirectives(ProgramSource source, ShaderProperties properties, Set<Integer> supportedRenderTargets, @Nullable BlendModeOverride defaultBlendOverride) {
        Optional<CommentDirective> optionalDrawbuffersDirective = ProgramDirectives.findDrawbuffersDirective(source.getFragmentSource());
        Optional<CommentDirective> optionalRendertargetsDirective = ProgramDirectives.findRendertargetsDirective(source.getFragmentSource());
        Optional<CommentDirective> optionalCommentDirective = ProgramDirectives.getAppliedDirective(optionalDrawbuffersDirective, optionalRendertargetsDirective);
        this.drawBuffers = optionalCommentDirective.map(commentDirective -> {
            if (commentDirective.getType() == CommentDirective.Type.DRAWBUFFERS) {
                return ProgramDirectives.parseDigits(commentDirective.getDirective().toCharArray());
            }
            if (commentDirective.getType() == CommentDirective.Type.RENDERTARGETS) {
                return ProgramDirectives.parseDigitList(commentDirective.getDirective());
            }
            throw new IllegalStateException("Unhandled comment directive type!");
        }).orElseGet(() -> {
            this.unknownDrawBuffers = true;
            return new int[]{0};
        });
        if (properties != null) {
            this.viewportScale = (ViewportData)((Object)properties.getViewportScaleOverrides().getOrDefault((Object)source.getName(), (Object)ViewportData.defaultValue()));
            this.alphaTestOverride = (AlphaTest)((Object)properties.getAlphaTestOverrides().get((Object)source.getName()));
            BlendModeOverride blendModeOverride = (BlendModeOverride)properties.getBlendModeOverrides().get((Object)source.getName());
            List bufferBlendInformations = (List)properties.getBufferBlendOverrides().get((Object)source.getName());
            this.blendModeOverride = Optional.ofNullable(blendModeOverride != null ? blendModeOverride : defaultBlendOverride);
            this.bufferBlendInformations = bufferBlendInformations != null ? bufferBlendInformations : Collections.emptyList();
            this.explicitFlips = source.getParent().getPackDirectives().getExplicitFlips(source.getName());
        } else {
            this.viewportScale = ViewportData.defaultValue();
            this.alphaTestOverride = null;
            this.blendModeOverride = Optional.ofNullable(defaultBlendOverride);
            this.bufferBlendInformations = Collections.emptyList();
            this.explicitFlips = ImmutableMap.of();
        }
        HashSet mipmappedBuffers = new HashSet();
        DispatchingDirectiveHolder directiveHolder = new DispatchingDirectiveHolder();
        supportedRenderTargets.forEach(index -> {
            BooleanConsumer mipmapHandler = shouldMipmap -> {
                if (shouldMipmap) {
                    mipmappedBuffers.add(index);
                } else {
                    mipmappedBuffers.remove(index);
                }
            };
            directiveHolder.acceptConstBooleanDirective("colortex" + index + "MipmapEnabled", mipmapHandler);
            if (index < LEGACY_RENDER_TARGETS.size()) {
                directiveHolder.acceptConstBooleanDirective((String)LEGACY_RENDER_TARGETS.get(index.intValue()) + "MipmapEnabled", mipmapHandler);
            }
        });
        source.getFragmentSource().map(ConstDirectiveParser::findDirectives).ifPresent(directives -> {
            for (ConstDirectiveParser.ConstDirective directive : directives) {
                directiveHolder.processDirective(directive);
            }
        });
        this.mipmappedBuffers = ImmutableSet.copyOf(mipmappedBuffers);
    }

    private static Optional<CommentDirective> findDrawbuffersDirective(Optional<String> stageSource) {
        return stageSource.flatMap(fragment -> CommentDirectiveParser.findDirective(fragment, CommentDirective.Type.DRAWBUFFERS));
    }

    private static Optional<CommentDirective> findRendertargetsDirective(Optional<String> stageSource) {
        return stageSource.flatMap(fragment -> CommentDirectiveParser.findDirective(fragment, CommentDirective.Type.RENDERTARGETS));
    }

    private static int[] parseDigits(char[] directiveChars) {
        int[] buffers = new int[directiveChars.length];
        int index = 0;
        for (char buffer : directiveChars) {
            buffers[index++] = Character.digit(buffer, 10);
        }
        return buffers;
    }

    private static int[] parseDigitList(String digitListString) {
        return Arrays.stream(digitListString.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private static Optional<CommentDirective> getAppliedDirective(Optional<CommentDirective> optionalDrawbuffersDirective, Optional<CommentDirective> optionalRendertargetsDirective) {
        if (optionalDrawbuffersDirective.isPresent() && optionalRendertargetsDirective.isPresent()) {
            if (optionalDrawbuffersDirective.get().getLocation() > optionalRendertargetsDirective.get().getLocation()) {
                return optionalDrawbuffersDirective;
            }
            return optionalRendertargetsDirective;
        }
        if (optionalDrawbuffersDirective.isPresent()) {
            return optionalDrawbuffersDirective;
        }
        return optionalRendertargetsDirective;
    }

    public ProgramDirectives withOverriddenDrawBuffers(int[] drawBuffersOverride) {
        return new ProgramDirectives(drawBuffersOverride, this.viewportScale, this.alphaTestOverride, this.blendModeOverride, this.bufferBlendInformations, this.mipmappedBuffers, this.explicitFlips);
    }

    public int[] getDrawBuffers() {
        return this.drawBuffers;
    }

    public boolean hasUnknownDrawBuffers() {
        return this.unknownDrawBuffers;
    }

    public ViewportData getViewportScale() {
        return this.viewportScale;
    }

    public Optional<AlphaTest> getAlphaTestOverride() {
        return Optional.ofNullable(this.alphaTestOverride);
    }

    public Optional<BlendModeOverride> getBlendModeOverride() {
        return this.blendModeOverride;
    }

    public List<BufferBlendInformation> getBufferBlendOverrides() {
        return this.bufferBlendInformations;
    }

    public ImmutableSet<Integer> getMipmappedBuffers() {
        return this.mipmappedBuffers;
    }

    public ImmutableMap<Integer, Boolean> getExplicitFlips() {
        return this.explicitFlips;
    }
}

