/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.OptionInstance$CaptionBasedToString
 *  net.minecraft.client.OptionInstance$IntRange
 *  net.minecraft.client.OptionInstance$TooltipSupplier
 *  net.minecraft.client.OptionInstance$ValueSet
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.network.chat.Component
 */
package net.irisshaders.iris.gui.option;

import java.io.IOException;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.option.ShadowDistanceOption;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class IrisVideoSettings {
    private static final Tooltip DISABLED_TOOLTIP = Tooltip.m_257550_((Component)Component.m_237115_((String)"options.iris.shadowDistance.disabled"));
    private static final Tooltip ENABLED_TOOLTIP = Tooltip.m_257550_((Component)Component.m_237115_((String)"options.iris.shadowDistance.enabled"));
    public static int shadowDistance = 32;
    public static ColorSpace colorSpace = ColorSpace.SRGB;
    public static final OptionInstance<Integer> RENDER_DISTANCE = new ShadowDistanceOption<Integer>("options.iris.shadowDistance", (OptionInstance.TooltipSupplier<Integer>)((OptionInstance.TooltipSupplier)mc -> {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        Tooltip tooltip = pipeline != null ? (pipeline.getForcedShadowRenderDistanceChunksForDisplay().isPresent() ? DISABLED_TOOLTIP : ENABLED_TOOLTIP) : ENABLED_TOOLTIP;
        return tooltip;
    }), (OptionInstance.CaptionBasedToString<Integer>)((OptionInstance.CaptionBasedToString)(arg, d) -> {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null) {
            d = pipeline.getForcedShadowRenderDistanceChunksForDisplay().orElse((int)d);
        }
        if ((double)d.intValue() <= 0.0) {
            return Component.m_237110_((String)"options.generic_value", (Object[])new Object[]{Component.m_237115_((String)"options.iris.shadowDistance"), "0 (disabled)"});
        }
        return Component.m_237110_((String)"options.generic_value", (Object[])new Object[]{Component.m_237115_((String)"options.iris.shadowDistance"), Component.m_237110_((String)"options.chunks", (Object[])new Object[]{d})});
    }), (OptionInstance.ValueSet<Integer>)new OptionInstance.IntRange(0, 32), IrisVideoSettings.getOverriddenShadowDistance(shadowDistance), integer -> {
        shadowDistance = integer;
        try {
            Iris.getIrisConfig().save();
        }
        catch (IOException e) {
            Iris.logger.fatal("Failed to save config!", e);
        }
    });

    public static int getOverriddenShadowDistance(int base) {
        return Iris.getPipelineManager().getPipeline().map(pipeline -> pipeline.getForcedShadowRenderDistanceChunksForDisplay().orElse(base)).orElse(base);
    }

    public static boolean isShadowDistanceSliderEnabled() {
        return Iris.getPipelineManager().getPipeline().map(pipeline -> !pipeline.getForcedShadowRenderDistanceChunksForDisplay().isPresent()).orElse(true);
    }
}

