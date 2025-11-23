/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.uniform;

import net.irisshaders.iris.gl.state.ValueUpdateNotifier;

public abstract class Uniform {
    protected final int location;
    protected final ValueUpdateNotifier notifier;

    Uniform(int location) {
        this(location, null);
    }

    Uniform(int location, ValueUpdateNotifier notifier) {
        this.location = location;
        this.notifier = notifier;
    }

    public abstract void update();

    public final int getLocation() {
        return this.location;
    }

    public final ValueUpdateNotifier getNotifier() {
        return this.notifier;
    }
}

