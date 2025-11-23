/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.batchedentityrendering.impl;

import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;

public class BatchingDebugMessageHelper {
    public static String getDebugMessage(DrawCallTrackingRenderBuffers drawTracker) {
        int drawCalls = drawTracker.getDrawCalls();
        int renderTypes = drawTracker.getRenderTypes();
        if (drawCalls > 0) {
            int effectivenessTimes10 = renderTypes * 1000 / drawCalls;
            float effectiveness = (float)effectivenessTimes10 / 10.0f;
            return drawCalls + " draw calls / " + renderTypes + " render types = " + effectiveness + "% effective";
        }
        return "(no draw calls)";
    }
}

