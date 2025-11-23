/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package dev.nonamecrackers2.simpleclouds.common.noise;

import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class AbstractLayeredNoise<T extends AbstractNoiseSettings<T>>
implements NoiseSettings {
    protected final List<T> noiseLayers;
    protected int startHeight;
    protected int endHeight;

    public AbstractLayeredNoise(List<T> noiseLayers) {
        this.noiseLayers = noiseLayers;
        this.recalculateHeights();
    }

    public List<T> getNoiseLayers() {
        return this.noiseLayers;
    }

    @Override
    public float[] packForShader() {
        float[] values = new float[]{};
        for (NoiseSettings layer : this.noiseLayers) {
            values = ArrayUtils.addAll((float[])values, (float[])layer.packForShader());
        }
        return values;
    }

    @Override
    public int layerCount() {
        int count = 0;
        for (NoiseSettings layer : this.noiseLayers) {
            count += layer.layerCount();
        }
        return count;
    }

    protected void recalculateHeights() {
        MutableInt lowest = null;
        for (AbstractNoiseSettings layer : this.noiseLayers) {
            int startHeight = layer.getStartHeight();
            if (lowest == null) {
                lowest = new MutableInt(startHeight);
                continue;
            }
            if (lowest.getValue() <= startHeight) continue;
            lowest.setValue(startHeight);
        }
        this.startHeight = lowest == null ? 0 : lowest.getValue();
        MutableInt highest = null;
        for (AbstractNoiseSettings layer : this.noiseLayers) {
            int endHeight = layer.getEndHeight();
            if (highest == null) {
                highest = new MutableInt(endHeight);
                continue;
            }
            if (highest.getValue() >= endHeight) continue;
            highest.setValue(endHeight);
        }
        this.endHeight = highest == null ? 0 : highest.getValue();
    }

    @Override
    public int getStartHeight() {
        return this.startHeight;
    }

    @Override
    public int getEndHeight() {
        return this.endHeight;
    }
}

