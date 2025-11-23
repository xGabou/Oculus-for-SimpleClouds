/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.shader;

public class ShaderCompileException
extends RuntimeException {
    private final String filename;
    private final String error;

    public ShaderCompileException(String filename, String error) {
        super(filename + ": " + error);
        this.filename = filename;
        this.error = error;
    }

    public ShaderCompileException(String filename, Exception error) {
        super(error);
        this.filename = filename;
        this.error = error.getMessage();
    }

    @Override
    public String getMessage() {
        return this.filename + ": " + super.getMessage();
    }

    public String getError() {
        return this.error;
    }

    public String getFilename() {
        return this.filename;
    }
}

