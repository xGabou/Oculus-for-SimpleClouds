/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.option.menu;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElement;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;

public abstract class OptionMenuOptionElement
extends OptionMenuElement {
    public final boolean slider;
    public final OptionMenuContainer container;
    public final String optionId;
    private final OptionValues packAppliedValues;

    public OptionMenuOptionElement(String elementString, OptionMenuContainer container, ShaderProperties shaderProperties, OptionValues packAppliedValues) {
        this.slider = shaderProperties.getSliderOptions().contains(elementString);
        this.container = container;
        this.optionId = elementString;
        this.packAppliedValues = packAppliedValues;
    }

    public OptionValues getAppliedOptionValues() {
        return this.packAppliedValues;
    }

    public OptionValues getPendingOptionValues() {
        MutableOptionValues values = this.getAppliedOptionValues().mutableCopy();
        values.addAll(Iris.getShaderPackOptionQueue());
        return values;
    }
}

