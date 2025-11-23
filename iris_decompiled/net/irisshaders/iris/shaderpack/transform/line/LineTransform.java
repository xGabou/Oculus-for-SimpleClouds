/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.irisshaders.iris.shaderpack.transform.line;

import com.google.common.collect.ImmutableList;

public interface LineTransform {
    public static ImmutableList<String> apply(ImmutableList<String> lines, LineTransform transform) {
        ImmutableList.Builder newLines = ImmutableList.builder();
        int index = 0;
        for (String line : lines) {
            newLines.add((Object)transform.transform(index, line));
            ++index;
        }
        return newLines.build();
    }

    public String transform(int var1, String var2);
}

