/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 */
package net.irisshaders.iris.uniforms.transforms;

import java.util.function.Supplier;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.transforms.SmoothedFloat;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class SmoothedVec2f
implements Supplier<Vector2f> {
    private final SmoothedFloat x;
    private final SmoothedFloat y;

    public SmoothedVec2f(float halfLifeUp, float halfLifeDown, Supplier<Vector2i> unsmoothed, FrameUpdateNotifier updateNotifier) {
        this.x = new SmoothedFloat(halfLifeUp, halfLifeDown, () -> ((Vector2i)unsmoothed.get()).x, updateNotifier);
        this.y = new SmoothedFloat(halfLifeUp, halfLifeDown, () -> ((Vector2i)unsmoothed.get()).y, updateNotifier);
    }

    @Override
    public Vector2f get() {
        return new Vector2f(this.x.getAsFloat(), this.y.getAsFloat());
    }
}

