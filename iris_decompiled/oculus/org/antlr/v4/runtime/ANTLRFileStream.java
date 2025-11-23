/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.io.IOException;
import oculus.org.antlr.v4.runtime.ANTLRInputStream;
import oculus.org.antlr.v4.runtime.misc.Utils;

@Deprecated
public class ANTLRFileStream
extends ANTLRInputStream {
    protected String fileName;

    public ANTLRFileStream(String fileName) throws IOException {
        this(fileName, null);
    }

    public ANTLRFileStream(String fileName, String encoding) throws IOException {
        this.fileName = fileName;
        this.load(fileName, encoding);
    }

    public void load(String fileName, String encoding) throws IOException {
        this.data = Utils.readFile(fileName, encoding);
        this.n = this.data.length;
    }

    @Override
    public String getSourceName() {
        return this.fileName;
    }
}

