/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.api.v0;

import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import net.irisshaders.iris.api.v0.IrisApiConfig;
import net.irisshaders.iris.api.v0.IrisTextVertexSink;
import net.irisshaders.iris.apiimpl.IrisApiV0Impl;

public interface IrisApi {
    public static IrisApi getInstance() {
        return IrisApiV0Impl.INSTANCE;
    }

    public int getMinorApiRevision();

    public boolean isShaderPackInUse();

    public boolean isRenderingShadowPass();

    public Object openMainIrisScreenObj(Object var1);

    public String getMainScreenLanguageKey();

    public IrisApiConfig getConfig();

    public IrisTextVertexSink createTextVertexSink(int var1, IntFunction<ByteBuffer> var2);

    public float getSunPathRotation();
}

