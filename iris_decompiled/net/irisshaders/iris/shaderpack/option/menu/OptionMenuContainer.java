/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.irisshaders.iris.shaderpack.option.menu;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.option.ProfileSet;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElementScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuMainElementScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuOptionElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuSubElementScreen;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;

public class OptionMenuContainer {
    public final OptionMenuElementScreen mainScreen;
    public final Map<String, OptionMenuElementScreen> subScreens = new HashMap<String, OptionMenuElementScreen>();
    private final List<OptionMenuOptionElement> usedOptionElements = new ArrayList<OptionMenuOptionElement>();
    private final List<String> usedOptions = new ArrayList<String>();
    private final List<String> unusedOptions = new ArrayList<String>();
    private final Map<List<OptionMenuElement>, Integer> unusedOptionDumpQueue = new HashMap<List<OptionMenuElement>, Integer>();
    private final ProfileSet profiles;

    public OptionMenuContainer(ShaderProperties shaderProperties, ShaderPackOptions shaderPackOptions, ProfileSet profiles) {
        this.profiles = profiles;
        this.mainScreen = new OptionMenuMainElementScreen(this, shaderProperties, shaderPackOptions, shaderProperties.getMainScreenOptions().orElseGet(() -> Collections.singletonList("*")), shaderProperties.getMainScreenColumnCount());
        this.unusedOptions.addAll((Collection<String>)shaderPackOptions.getOptionSet().getBooleanOptions().keySet());
        this.unusedOptions.addAll((Collection<String>)shaderPackOptions.getOptionSet().getStringOptions().keySet());
        Map<String, Integer> subScreenColumnCounts = shaderProperties.getSubScreenColumnCount();
        shaderProperties.getSubScreenOptions().forEach((screenKey, options) -> this.subScreens.put((String)screenKey, new OptionMenuSubElementScreen((String)screenKey, this, shaderProperties, shaderPackOptions, (List<String>)options, Optional.ofNullable((Integer)subScreenColumnCounts.get(screenKey)))));
        for (Map.Entry<List<OptionMenuElement>, Integer> entry : this.unusedOptionDumpQueue.entrySet()) {
            ArrayList<OptionMenuElement> elementsToInsert = new ArrayList<OptionMenuElement>();
            ArrayList unusedOptionsCopy = Lists.newArrayList(this.unusedOptions);
            for (String optionId : unusedOptionsCopy) {
                try {
                    OptionMenuElement element = OptionMenuElement.create(optionId, this, shaderProperties, shaderPackOptions);
                    if (element == null) continue;
                    elementsToInsert.add(element);
                    if (!(element instanceof OptionMenuOptionElement)) continue;
                    this.notifyOptionAdded(optionId, (OptionMenuOptionElement)element);
                }
                catch (IllegalArgumentException error) {
                    Iris.logger.warn(error);
                    elementsToInsert.add(OptionMenuElement.EMPTY);
                }
            }
            entry.getKey().addAll(entry.getValue(), elementsToInsert);
        }
    }

    public ProfileSet getProfiles() {
        return this.profiles;
    }

    public void queueForUnusedOptionDump(int index, List<OptionMenuElement> elementList) {
        this.unusedOptionDumpQueue.put(elementList, index);
    }

    public void notifyOptionAdded(String optionId, OptionMenuOptionElement option) {
        if (!this.usedOptions.contains(optionId)) {
            this.usedOptionElements.add(option);
            this.usedOptions.add(optionId);
        }
        this.unusedOptions.remove(optionId);
    }
}

