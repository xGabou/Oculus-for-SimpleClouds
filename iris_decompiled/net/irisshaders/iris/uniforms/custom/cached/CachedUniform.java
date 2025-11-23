/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.uniforms.custom.cached;

import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.uniforms.custom.cached.BooleanCachedUniform;
import net.irisshaders.iris.uniforms.custom.cached.Float2VectorCachedUniform;
import net.irisshaders.iris.uniforms.custom.cached.Float3VectorCachedUniform;
import net.irisshaders.iris.uniforms.custom.cached.Float4VectorCachedUniform;
import net.irisshaders.iris.uniforms.custom.cached.FloatCachedUniform;
import net.irisshaders.iris.uniforms.custom.cached.IntCachedUniform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class CachedUniform
implements VariableExpression {
    private final String name;
    private final UniformUpdateFrequency updateFrequency;
    private boolean changed = true;

    public CachedUniform(String name, UniformUpdateFrequency updateFrequency) {
        this.name = name;
        this.updateFrequency = updateFrequency;
    }

    public static CachedUniform forExpression(String name, Type type, Expression expression, FunctionContext context) {
        FunctionReturn held = new FunctionReturn();
        UniformUpdateFrequency frequency = UniformUpdateFrequency.CUSTOM;
        if (type.equals(Type.Boolean)) {
            return new BooleanCachedUniform(name, frequency, () -> {
                expression.evaluateTo(context, held);
                return held.booleanReturn;
            });
        }
        if (type.equals(Type.Int)) {
            return new IntCachedUniform(name, frequency, () -> {
                expression.evaluateTo(context, held);
                return held.intReturn;
            });
        }
        if (type.equals(Type.Float)) {
            return new FloatCachedUniform(name, frequency, () -> {
                expression.evaluateTo(context, held);
                return held.floatReturn;
            });
        }
        if (type.equals(VectorType.VEC2)) {
            return new Float2VectorCachedUniform(name, frequency, () -> {
                expression.evaluateTo(context, held);
                return (Vector2f)held.objectReturn;
            });
        }
        if (type.equals(VectorType.VEC3)) {
            return new Float3VectorCachedUniform(name, frequency, () -> {
                expression.evaluateTo(context, held);
                return (Vector3f)held.objectReturn;
            });
        }
        if (type.equals(VectorType.VEC4)) {
            return new Float4VectorCachedUniform(name, frequency, () -> {
                expression.evaluateTo(context, held);
                return (Vector4f)held.objectReturn;
            });
        }
        throw new IllegalArgumentException("Custom uniforms of type: " + type + " are currently not supported");
    }

    public void markUnchanged() {
        this.changed = false;
    }

    public void update() {
        this.doUpdate();
        this.changed = true;
    }

    protected abstract boolean doUpdate();

    public abstract void push(int var1);

    public void pushIfChanged(int location) {
        if (this.changed) {
            this.push(location);
        }
    }

    @Override
    public void evaluateTo(FunctionContext context, FunctionReturn functionReturn) {
        this.writeTo(functionReturn);
    }

    public abstract void writeTo(FunctionReturn var1);

    public abstract Type getType();

    public String getName() {
        return this.name;
    }

    public UniformUpdateFrequency getUpdateFrequency() {
        return this.updateFrequency;
    }
}

