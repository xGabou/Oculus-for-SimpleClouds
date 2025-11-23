/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.transform;

public interface Transformations {
    public boolean contains(String var1);

    public void injectLine(InjectionPoint var1, String var2);

    public void replaceExact(String var1, String var2);

    public void replaceRegex(String var1, String var2);

    public String getPrefix();

    public void setPrefix(String var1);

    public void define(String var1, String var2);

    public static enum InjectionPoint {
        DEFINES,
        BEFORE_CODE,
        END;

    }
}

