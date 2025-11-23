/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gui;

import java.util.ArrayDeque;
import java.util.Deque;
import net.irisshaders.iris.gui.element.ShaderPackOptionList;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;

public class NavigationController {
    private final OptionMenuContainer container;
    private final Deque<String> history = new ArrayDeque<String>();
    private ShaderPackOptionList optionList;
    private String currentScreen = null;

    public NavigationController(OptionMenuContainer container) {
        this.container = container;
    }

    public void back() {
        if (!this.history.isEmpty()) {
            this.history.removeLast();
            this.currentScreen = !this.history.isEmpty() ? this.history.getLast() : null;
        } else {
            this.currentScreen = null;
        }
        this.rebuild();
    }

    public void open(String screen) {
        this.currentScreen = screen;
        this.history.addLast(screen);
        this.rebuild();
    }

    public void rebuild() {
        if (this.optionList != null) {
            this.optionList.rebuild();
        }
    }

    public void refresh() {
        if (this.optionList != null) {
            this.optionList.refresh();
        }
    }

    public boolean hasHistory() {
        return !this.history.isEmpty();
    }

    public void setActiveOptionList(ShaderPackOptionList optionList) {
        this.optionList = optionList;
    }

    public String getCurrentScreen() {
        return this.currentScreen;
    }
}

