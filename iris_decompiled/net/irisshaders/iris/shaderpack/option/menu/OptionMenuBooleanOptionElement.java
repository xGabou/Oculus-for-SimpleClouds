/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.option.menu;

import net.irisshaders.iris.shaderpack.option.BooleanOption;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuOptionElement;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;

public class OptionMenuBooleanOptionElement
extends OptionMenuOptionElement {
    public final BooleanOption option;

    public OptionMenuBooleanOptionElement(String elementString, OptionMenuContainer container, ShaderProperties shaderProperties, OptionValues values, BooleanOption option) {
        super(elementString, container, shaderProperties, values);
        this.option = option;
    }
}

