/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package net.irisshaders.iris.gui.element.widget;

import java.util.Optional;
import net.irisshaders.iris.gui.element.widget.AbstractElementWidget;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElement;
import net.minecraft.network.chat.Component;

public abstract class CommentedElementWidget<T extends OptionMenuElement>
extends AbstractElementWidget<T> {
    public CommentedElementWidget(T element) {
        super(element);
    }

    public abstract Optional<Component> getCommentTitle();

    public abstract Optional<Component> getCommentBody();
}

