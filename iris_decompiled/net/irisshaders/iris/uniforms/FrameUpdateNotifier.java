/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.uniforms;

import java.util.ArrayList;
import java.util.List;

public class FrameUpdateNotifier {
    private final List<Runnable> listeners = new ArrayList<Runnable>();

    public void addListener(Runnable onNewFrame) {
        this.listeners.add(onNewFrame);
    }

    public void onNewFrame() {
        this.listeners.forEach(Runnable::run);
    }
}

