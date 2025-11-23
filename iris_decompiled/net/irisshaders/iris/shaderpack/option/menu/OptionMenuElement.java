/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.irisshaders.iris.shaderpack.option.menu;

import com.google.common.collect.ImmutableMap;
import net.irisshaders.iris.shaderpack.option.MergedBooleanOption;
import net.irisshaders.iris.shaderpack.option.MergedStringOption;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuBooleanOptionElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuLinkElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuProfileElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuStringOptionElement;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;

public abstract class OptionMenuElement {
    public static final OptionMenuElement EMPTY = new OptionMenuElement(){};
    private static final String ELEMENT_EMPTY = "<empty>";
    private static final String ELEMENT_PROFILE = "<profile>";

    public static OptionMenuElement create(String elementString, OptionMenuContainer container, ShaderProperties shaderProperties, ShaderPackOptions shaderPackOptions) throws IllegalArgumentException {
        if (ELEMENT_EMPTY.equals(elementString)) {
            return EMPTY;
        }
        if (ELEMENT_PROFILE.equals(elementString)) {
            return container.getProfiles().size() > 0 ? new OptionMenuProfileElement(container.getProfiles(), shaderPackOptions.getOptionSet(), shaderPackOptions.getOptionValues()) : null;
        }
        if (elementString.startsWith("[") && elementString.endsWith("]")) {
            return new OptionMenuLinkElement(elementString.substring(1, elementString.length() - 1));
        }
        ImmutableMap<String, MergedBooleanOption> booleanOptions = shaderPackOptions.getOptionSet().getBooleanOptions();
        ImmutableMap<String, MergedStringOption> stringOptions = shaderPackOptions.getOptionSet().getStringOptions();
        if (booleanOptions.containsKey(elementString)) {
            return new OptionMenuBooleanOptionElement(elementString, container, shaderProperties, shaderPackOptions.getOptionValues(), ((MergedBooleanOption)booleanOptions.get(elementString)).getOption());
        }
        if (stringOptions.containsKey(elementString)) {
            return new OptionMenuStringOptionElement(elementString, container, shaderProperties, shaderPackOptions.getOptionValues(), ((MergedStringOption)stringOptions.get(elementString)).getOption());
        }
        throw new IllegalArgumentException("Unable to resolve shader pack option menu element \"" + elementString + "\" defined in shaders.properties");
    }
}

