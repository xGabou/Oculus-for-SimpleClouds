/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.element;

import kroppeb.stareval.element.Element;
import kroppeb.stareval.element.ExpressionElement;

public interface PriorityOperatorElement
extends Element {
    public int getPriority();

    public ExpressionElement resolveWith(ExpressionElement var1);
}

