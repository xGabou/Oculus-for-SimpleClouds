/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

public interface IntStream {
    public static final int EOF = -1;
    public static final String UNKNOWN_SOURCE_NAME = "<unknown>";

    public void consume();

    public int LA(int var1);

    public int mark();

    public void release(int var1);

    public int index();

    public void seek(int var1);

    public int size();

    public String getSourceName();
}

