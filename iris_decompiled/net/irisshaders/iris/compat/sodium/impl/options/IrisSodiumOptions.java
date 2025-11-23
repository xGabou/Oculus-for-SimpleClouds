/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.gui.options.Option
 *  me.jellysquid.mods.sodium.client.gui.options.OptionFlag
 *  me.jellysquid.mods.sodium.client.gui.options.OptionImpact
 *  me.jellysquid.mods.sodium.client.gui.options.OptionImpl
 *  me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter
 *  me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl
 *  me.jellysquid.mods.sodium.client.gui.options.control.SliderControl
 *  me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage
 *  me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage
 *  net.minecraft.client.GraphicsStatus
 *  net.minecraft.client.Options
 *  net.minecraft.network.chat.Component
 */
package net.irisshaders.iris.compat.sodium.impl.options;

import java.io.IOException;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionFlag;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.sodium.impl.options.OptionImplExtended;
import net.irisshaders.iris.compat.sodium.impl.options.SupportedGraphicsMode;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

public class IrisSodiumOptions {
    public static OptionImpl<Options, Integer> createMaxShadowDistanceSlider(MinecraftOptionsStorage vanillaOpts) {
        OptionImpl maxShadowDistanceSlider = OptionImpl.createBuilder(Integer.TYPE, (OptionStorage)vanillaOpts).setName((Component)Component.m_237115_((String)"options.iris.shadowDistance")).setTooltip((Component)Component.m_237115_((String)"options.iris.shadowDistance.sodium_tooltip")).setControl(option -> new SliderControl((Option)option, 0, 32, 1, IrisSodiumOptions.translateVariableOrDisabled("options.chunks", "Disabled"))).setBinding((options, value) -> {
            IrisVideoSettings.shadowDistance = value;
            try {
                Iris.getIrisConfig().save();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }, options -> IrisVideoSettings.getOverriddenShadowDistance(IrisVideoSettings.shadowDistance)).setImpact(OptionImpact.HIGH).setEnabled(true).build();
        ((OptionImplExtended)maxShadowDistanceSlider).iris$dynamicallyEnable(IrisVideoSettings::isShadowDistanceSliderEnabled);
        return maxShadowDistanceSlider;
    }

    public static OptionImpl<Options, ColorSpace> createColorSpaceButton(MinecraftOptionsStorage vanillaOpts) {
        OptionImpl colorSpace = OptionImpl.createBuilder(ColorSpace.class, (OptionStorage)vanillaOpts).setName((Component)Component.m_237115_((String)"options.iris.colorSpace")).setTooltip((Component)Component.m_237115_((String)"options.iris.colorSpace.sodium_tooltip")).setControl(option -> new CyclingControl((Option)option, ColorSpace.class, new Component[]{Component.m_237113_((String)"sRGB"), Component.m_237113_((String)"DCI_P3"), Component.m_237113_((String)"Display P3"), Component.m_237113_((String)"REC2020"), Component.m_237113_((String)"Adobe RGB")})).setBinding((options, value) -> {
            IrisVideoSettings.colorSpace = value;
            try {
                Iris.getIrisConfig().save();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }, options -> IrisVideoSettings.colorSpace).setImpact(OptionImpact.LOW).setEnabled(true).build();
        return colorSpace;
    }

    static ControlValueFormatter translateVariableOrDisabled(String key, String disabled) {
        return v -> v == 0 ? Component.m_237113_((String)disabled) : Component.m_237110_((String)key, (Object[])new Object[]{v});
    }

    public static OptionImpl<Options, SupportedGraphicsMode> createLimitedVideoSettingsButton(MinecraftOptionsStorage vanillaOpts) {
        return OptionImpl.createBuilder(SupportedGraphicsMode.class, (OptionStorage)vanillaOpts).setName((Component)Component.m_237115_((String)"options.graphics")).setTooltip((Component)Component.m_237115_((String)"sodium.options.graphics_quality.tooltip")).setControl(option -> new CyclingControl((Option)option, SupportedGraphicsMode.class, new Component[]{Component.m_237115_((String)"options.graphics.fast"), Component.m_237115_((String)"options.graphics.fancy")})).setBinding((opts, value) -> opts.m_232060_().m_231514_((Object)value.toVanilla()), opts -> SupportedGraphicsMode.fromVanilla((GraphicsStatus)opts.m_232060_().m_231551_())).setImpact(OptionImpact.HIGH).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_RENDERER_RELOAD}).build();
    }
}

