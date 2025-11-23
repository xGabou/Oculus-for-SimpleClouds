/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.texture.mipmap;

import java.util.function.IntUnaryOperator;
import net.irisshaders.iris.texture.mipmap.ChannelMipmapGenerator;

public class DiscreteBlendFunction
implements ChannelMipmapGenerator.BlendFunction {
    protected final IntUnaryOperator typeFunc;

    public DiscreteBlendFunction(IntUnaryOperator typeFunc) {
        this.typeFunc = typeFunc;
    }

    public static int selectTargetType(int t0, int t1, int t2, int t3) {
        if (t0 != t1 && t0 != t2) {
            if (t2 == t3) {
                return t2;
            }
            if (t0 != t3 && (t1 == t2 || t1 == t3)) {
                return t1;
            }
        }
        return t0;
    }

    @Override
    public int blend(int v0, int v1, int v2, int v3) {
        int t0 = this.typeFunc.applyAsInt(v0);
        int t1 = this.typeFunc.applyAsInt(v1);
        int t2 = this.typeFunc.applyAsInt(v2);
        int t3 = this.typeFunc.applyAsInt(v3);
        int targetType = DiscreteBlendFunction.selectTargetType(t0, t1, t2, t3);
        int sum = 0;
        int amount = 0;
        if (t0 == targetType) {
            sum += v0;
            ++amount;
        }
        if (t1 == targetType) {
            sum += v1;
            ++amount;
        }
        if (t2 == targetType) {
            sum += v2;
            ++amount;
        }
        if (t3 == targetType) {
            sum += v3;
            ++amount;
        }
        return sum / amount;
    }
}

