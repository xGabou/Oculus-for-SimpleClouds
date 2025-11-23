/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 */
package dev.nonamecrackers2.simpleclouds.common.noise;

import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import net.minecraft.util.Mth;

public abstract class AbstractNoiseSettings<T extends AbstractNoiseSettings<T>>
implements NoiseSettings {
    private float[] packedParameters;

    public final T setParam(Param param, float value) {
        if (this.setParamRaw(param, value = Mth.m_14036_((float)value, (float)param.getMinInclusive(), (float)param.getMaxInclusive()))) {
            this.packParameters();
        }
        return (T)this;
    }

    public abstract float getParam(Param var1);

    protected abstract boolean setParamRaw(Param var1, float var2);

    protected void packParameters() {
        float[] layerSettings = new float[Param.values().length];
        for (int i = 0; i < Param.values().length; ++i) {
            Param param = Param.values()[i];
            layerSettings[i] = this.getParam(param);
        }
        this.packedParameters = layerSettings;
    }

    @Override
    public float[] packForShader() {
        if (this.packedParameters == null) {
            this.packParameters();
        }
        return this.packedParameters;
    }

    @Override
    public int layerCount() {
        return 1;
    }

    @Override
    public int getStartHeight() {
        return Mth.m_14143_((float)this.getParam(Param.HEIGHT_OFFSET));
    }

    @Override
    public int getEndHeight() {
        return this.getStartHeight() + Mth.m_14167_((float)this.getParam(Param.HEIGHT));
    }

    public static enum Param {
        HEIGHT(32.0f, 1.0f, 256.0f),
        VALUE_OFFSET(-0.5f, -8.0f, 8.0f),
        SCALE_X(30.0f, 0.1f, 3200.0f),
        SCALE_Y(10.0f, 0.1f, 3200.0f),
        SCALE_Z(30.0f, 0.1f, 3200.0f),
        FADE_DISTANCE(10.0f, 1.0f, 128.0f),
        HEIGHT_OFFSET(0.0f, 0.0f, 256.0f),
        VALUE_SCALE(1.0f, -10.0f, 10.0f);

        private final float defaultValue;
        private final float minInclusive;
        private final float maxInclusive;

        private Param(float value, float minInclusive, float maxInclusive) {
            this.defaultValue = value;
            this.minInclusive = minInclusive;
            this.maxInclusive = maxInclusive;
        }

        public float getDefaultValue() {
            return this.defaultValue;
        }

        public float getMinInclusive() {
            return this.minInclusive;
        }

        public float getMaxInclusive() {
            return this.maxInclusive;
        }
    }
}

