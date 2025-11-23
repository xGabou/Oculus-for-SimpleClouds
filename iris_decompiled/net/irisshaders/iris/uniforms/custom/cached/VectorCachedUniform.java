/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.custom.cached.CachedUniform;
import org.jetbrains.annotations.NotNull;

public abstract class VectorCachedUniform<T>
extends CachedUniform {
    protected final T cached;
    private final Supplier<T> supplier;

    public VectorCachedUniform(String name, UniformUpdateFrequency updateFrequency, T cache, Supplier<@NotNull T> supplier) {
        super(name, updateFrequency);
        this.supplier = supplier;
        this.cached = cache;
    }

    protected abstract void setFrom(T var1);

    @Override
    protected boolean doUpdate() {
        T other = this.supplier.get();
        if (other == null) {
            Iris.logger.warn("Cached Uniform supplier gave null back");
            return false;
        }
        if (!this.cached.equals(other)) {
            this.setFrom(other);
            return true;
        }
        return false;
    }

    @Override
    public void writeTo(FunctionReturn functionReturn) {
        functionReturn.objectReturn = this.cached;
    }

    @Override
    public Type getType() {
        return Type.Float;
    }
}

