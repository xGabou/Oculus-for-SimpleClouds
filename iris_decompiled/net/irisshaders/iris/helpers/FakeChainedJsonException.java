/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.ChainedJsonException
 */
package net.irisshaders.iris.helpers;

import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.minecraft.server.ChainedJsonException;

public class FakeChainedJsonException
extends ChainedJsonException {
    private final ShaderCompileException trueException;

    public FakeChainedJsonException(ShaderCompileException e) {
        super("", (Throwable)e);
        this.trueException = e;
    }

    public ShaderCompileException getTrueException() {
        return this.trueException;
    }
}

