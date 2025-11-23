/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.option.menu;

import java.util.List;
import java.util.Optional;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElementScreen;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;

public class OptionMenuSubElementScreen
extends OptionMenuElementScreen {
    public final String screenId;

    public OptionMenuSubElementScreen(String screenId, OptionMenuContainer container, ShaderProperties shaderProperties, ShaderPackOptions shaderPackOptions, List<String> elementStrings, Optional<Integer> columnCount) {
        super(container, shaderProperties, shaderPackOptions, elementStrings, columnCount);
        this.screenId = screenId;
    }
}

