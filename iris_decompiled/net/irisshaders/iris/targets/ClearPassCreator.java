/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  org.joml.Vector2i
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.targets;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import net.irisshaders.iris.shaderpack.properties.PackShadowDirectives;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.irisshaders.iris.targets.ClearPass;
import net.irisshaders.iris.targets.ClearPassInformation;
import net.irisshaders.iris.targets.RenderTarget;
import net.irisshaders.iris.targets.RenderTargets;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class ClearPassCreator {
    public static ImmutableList<ClearPass> createClearPasses(RenderTargets renderTargets, boolean fullClear, PackRenderTargetDirectives renderTargetDirectives) {
        int maxDrawBuffers = GlStateManager._getInteger((int)34852);
        HashMap<Vector2i, Map> clearByColor = new HashMap<Vector2i, Map>();
        renderTargetDirectives.getRenderTargetSettings().forEach((bufferI, settings) -> {
            int buffer = bufferI;
            if (fullClear || settings.shouldClear()) {
                Vector4f defaultClearColor = buffer == 0 ? null : (buffer == 1 ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f) : new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
                RenderTarget target = renderTargets.get(buffer);
                if (target == null) {
                    return;
                }
                Vector4f clearColor = settings.getClearColor().orElse(defaultClearColor);
                clearByColor.computeIfAbsent(new Vector2i(target.getWidth(), target.getHeight()), size -> new HashMap()).computeIfAbsent(new ClearPassInformation(clearColor, target.getWidth(), target.getHeight()), color -> new IntArrayList()).add(buffer);
            }
        });
        ArrayList clearPasses = new ArrayList();
        clearByColor.forEach((passSize, vector4fIntListMap) -> vector4fIntListMap.forEach((clearInfo, buffers) -> {
            int startIndex = 0;
            while (startIndex < buffers.size()) {
                int[] clearBuffers = new int[Math.min(buffers.size() - startIndex, maxDrawBuffers)];
                for (int i = 0; i < clearBuffers.length; ++i) {
                    clearBuffers[i] = buffers.getInt(startIndex);
                    ++startIndex;
                }
                clearPasses.add(new ClearPass(clearInfo.getColor(), clearInfo::getWidth, clearInfo::getHeight, renderTargets.createClearFramebuffer(true, clearBuffers), 16384));
                clearPasses.add(new ClearPass(clearInfo.getColor(), clearInfo::getWidth, clearInfo::getHeight, renderTargets.createClearFramebuffer(false, clearBuffers), 16384));
            }
        }));
        return ImmutableList.copyOf(clearPasses);
    }

    public static ImmutableList<ClearPass> createShadowClearPasses(ShadowRenderTargets renderTargets, boolean fullClear, PackShadowDirectives renderTargetDirectives) {
        if (renderTargets == null) {
            return ImmutableList.of();
        }
        int maxDrawBuffers = GlStateManager._getInteger((int)34852);
        HashMap<Vector4f, IntList> clearByColor = new HashMap<Vector4f, IntList>();
        for (int i = 0; i < renderTargets.getRenderTargetCount(); ++i) {
            if (renderTargets.get(i) == null) continue;
            PackShadowDirectives.SamplingSettings settings = (PackShadowDirectives.SamplingSettings)renderTargetDirectives.getColorSamplingSettings().get(i);
            if (!fullClear && !settings.getClear()) continue;
            Vector4f clearColor2 = settings.getClearColor();
            clearByColor.computeIfAbsent(clearColor2, color -> new IntArrayList()).add(i);
        }
        ArrayList clearPasses = new ArrayList();
        clearByColor.forEach((clearColor, buffers) -> {
            int startIndex = 0;
            while (startIndex < buffers.size()) {
                int[] clearBuffers = new int[Math.min(buffers.size() - startIndex, maxDrawBuffers)];
                for (int i = 0; i < clearBuffers.length; ++i) {
                    clearBuffers[i] = buffers.getInt(startIndex);
                    ++startIndex;
                }
                clearPasses.add(new ClearPass((Vector4f)clearColor, renderTargets::getResolution, renderTargets::getResolution, renderTargets.createFramebufferWritingToAlt(clearBuffers), 16384));
                clearPasses.add(new ClearPass((Vector4f)clearColor, renderTargets::getResolution, renderTargets::getResolution, renderTargets.createFramebufferWritingToMain(clearBuffers), 16384));
            }
        });
        return ImmutableList.copyOf(clearPasses);
    }
}

