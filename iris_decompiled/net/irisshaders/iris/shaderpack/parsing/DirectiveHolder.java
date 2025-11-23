/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 *  org.joml.Vector2f
 *  org.joml.Vector3i
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.shaderpack.parsing;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public interface DirectiveHolder {
    public void acceptUniformDirective(String var1, Runnable var2);

    public void acceptCommentStringDirective(String var1, Consumer<String> var2);

    public void acceptCommentIntDirective(String var1, IntConsumer var2);

    public void acceptCommentFloatDirective(String var1, FloatConsumer var2);

    public void acceptConstBooleanDirective(String var1, BooleanConsumer var2);

    public void acceptConstStringDirective(String var1, Consumer<String> var2);

    public void acceptConstIntDirective(String var1, IntConsumer var2);

    public void acceptConstFloatDirective(String var1, FloatConsumer var2);

    public void acceptConstVec2Directive(String var1, Consumer<Vector2f> var2);

    public void acceptConstIVec3Directive(String var1, Consumer<Vector3i> var2);

    public void acceptConstVec4Directive(String var1, Consumer<Vector4f> var2);
}

