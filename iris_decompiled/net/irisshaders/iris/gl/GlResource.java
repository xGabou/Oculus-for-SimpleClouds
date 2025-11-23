/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl;

public abstract class GlResource {
    private final int id;
    private boolean isValid;

    protected GlResource(int id) {
        this.id = id;
        this.isValid = true;
    }

    public final void destroy() {
        this.destroyInternal();
        this.isValid = false;
    }

    protected abstract void destroyInternal();

    protected void assertValid() {
        if (!this.isValid) {
            throw new IllegalStateException("Tried to use a destroyed GlResource");
        }
    }

    protected int getGlId() {
        this.assertValid();
        return this.id;
    }
}

