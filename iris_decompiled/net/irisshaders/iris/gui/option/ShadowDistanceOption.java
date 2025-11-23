/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.OptionInstance$CaptionBasedToString
 *  net.minecraft.client.OptionInstance$TooltipSupplier
 *  net.minecraft.client.OptionInstance$ValueSet
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.AbstractWidget
 */
package net.irisshaders.iris.gui.option;

import java.util.function.Consumer;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;

public class ShadowDistanceOption<T>
extends OptionInstance<T> {
    private final OptionInstance.TooltipSupplier<T> tooltipSupplier;

    public ShadowDistanceOption(String string, OptionInstance.TooltipSupplier<T> arg, OptionInstance.CaptionBasedToString<T> arg2, OptionInstance.ValueSet<T> arg3, T object, Consumer<T> consumer) {
        super(string, arg, arg2, arg3, object, consumer);
        this.tooltipSupplier = arg;
    }

    public AbstractWidget m_231507_(Options options, int x, int y, int width) {
        AbstractWidget widget = super.m_231507_(options, x, y, width);
        widget.f_93623_ = IrisVideoSettings.isShadowDistanceSliderEnabled();
        return widget;
    }
}

