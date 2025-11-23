/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.element.tree.partial;

import java.util.ArrayList;
import java.util.List;
import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.element.tree.partial.PartialExpression;

public class UnfinishedArgsExpression
extends PartialExpression {
    public final List<ExpressionElement> tokens = new ArrayList<ExpressionElement>();

    @Override
    public String toString() {
        return "UnfinishedArgs{" + this.tokens + "}";
    }
}

