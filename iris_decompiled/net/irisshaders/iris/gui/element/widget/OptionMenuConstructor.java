/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 */
package net.irisshaders.iris.gui.element.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.ShaderPackOptionList;
import net.irisshaders.iris.gui.element.screen.ElementWidgetScreenData;
import net.irisshaders.iris.gui.element.widget.AbstractElementWidget;
import net.irisshaders.iris.gui.element.widget.BooleanElementWidget;
import net.irisshaders.iris.gui.element.widget.LinkElementWidget;
import net.irisshaders.iris.gui.element.widget.ProfileElementWidget;
import net.irisshaders.iris.gui.element.widget.SliderElementWidget;
import net.irisshaders.iris.gui.element.widget.StringElementWidget;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuBooleanOptionElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElementScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuLinkElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuMainElementScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuProfileElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuStringOptionElement;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuSubElementScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class OptionMenuConstructor {
    private static final Map<Class<? extends OptionMenuElement>, WidgetProvider<OptionMenuElement>> WIDGET_CREATORS = new HashMap<Class<? extends OptionMenuElement>, WidgetProvider<OptionMenuElement>>();
    private static final Map<Class<? extends OptionMenuElementScreen>, ScreenDataProvider<OptionMenuElementScreen>> SCREEN_DATA_CREATORS = new HashMap<Class<? extends OptionMenuElementScreen>, ScreenDataProvider<OptionMenuElementScreen>>();

    private OptionMenuConstructor() {
    }

    public static <T extends OptionMenuElement> void registerWidget(Class<T> element, WidgetProvider<T> widget) {
        WIDGET_CREATORS.put(element, widget);
    }

    public static <T extends OptionMenuElementScreen> void registerScreen(Class<T> screen, ScreenDataProvider<T> data) {
        SCREEN_DATA_CREATORS.put(screen, data);
    }

    public static AbstractElementWidget<? extends OptionMenuElement> createWidget(OptionMenuElement element) {
        return WIDGET_CREATORS.getOrDefault(element.getClass(), e -> AbstractElementWidget.EMPTY).create(element);
    }

    public static ElementWidgetScreenData createScreenData(OptionMenuElementScreen screen) {
        return SCREEN_DATA_CREATORS.getOrDefault(screen.getClass(), s -> ElementWidgetScreenData.EMPTY).create(screen);
    }

    public static void constructAndApplyToScreen(OptionMenuContainer container, ShaderPackScreen packScreen, ShaderPackOptionList optionList, NavigationController navigation) {
        OptionMenuElementScreen screen = container.mainScreen;
        if (navigation.getCurrentScreen() != null && container.subScreens.containsKey(navigation.getCurrentScreen())) {
            screen = container.subScreens.get(navigation.getCurrentScreen());
        }
        ElementWidgetScreenData data = OptionMenuConstructor.createScreenData(screen);
        optionList.addHeader(data.heading(), data.backButton());
        optionList.addWidgets(screen.getColumnCount(), screen.elements.stream().map(element -> {
            AbstractElementWidget<? extends OptionMenuElement> widget = OptionMenuConstructor.createWidget(element);
            widget.init(packScreen, navigation);
            return widget;
        }).collect(Collectors.toList()));
    }

    static {
        OptionMenuConstructor.registerScreen(OptionMenuMainElementScreen.class, screen -> new ElementWidgetScreenData((Component)Component.m_237113_((String)Iris.getCurrentPackName()).m_130946_(Iris.isFallback() ? " (fallback)" : "").m_130940_(ChatFormatting.BOLD), false));
        OptionMenuConstructor.registerScreen(OptionMenuSubElementScreen.class, screen -> new ElementWidgetScreenData((Component)GuiUtil.translateOrDefault(Component.m_237113_((String)screen.screenId), "screen." + screen.screenId, new Object[0]), true));
        OptionMenuConstructor.registerWidget(OptionMenuBooleanOptionElement.class, BooleanElementWidget::new);
        OptionMenuConstructor.registerWidget(OptionMenuProfileElement.class, ProfileElementWidget::new);
        OptionMenuConstructor.registerWidget(OptionMenuLinkElement.class, LinkElementWidget::new);
        OptionMenuConstructor.registerWidget(OptionMenuStringOptionElement.class, element -> element.slider ? new SliderElementWidget((OptionMenuStringOptionElement)element) : new StringElementWidget((OptionMenuStringOptionElement)element));
    }

    public static interface WidgetProvider<T extends OptionMenuElement> {
        public AbstractElementWidget<T> create(T var1);
    }

    public static interface ScreenDataProvider<T extends OptionMenuElementScreen> {
        public ElementWidgetScreenData create(T var1);
    }
}

