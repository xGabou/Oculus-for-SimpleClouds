/*
 * Decompiled with CFR 0.152.
 */
package de.odysseus.ithaka.digraph.io.dot;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;

public class DotAttribute {
    private final String name;
    private final String value;
    private final boolean quotes;

    public DotAttribute(String name, String value) {
        this.name = name;
        this.value = value;
        this.quotes = !DotAttribute.isIdentifier(value);
    }

    public DotAttribute(String name, Number value) {
        this.name = name;
        this.value = value.toString();
        this.quotes = false;
    }

    public DotAttribute(String name, boolean value) {
        this.name = name;
        this.value = String.valueOf(value);
        this.quotes = false;
    }

    public DotAttribute(String name, Color value) {
        this.name = name;
        this.value = String.format("#%6X", value.getRGB() & 0xFFFFFF);
        this.quotes = true;
    }

    private static boolean isIdentifier(String value) {
        if (!Character.isJavaIdentifierStart(value.charAt(0))) {
            return false;
        }
        for (char c : value.substring(1).toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) continue;
            return false;
        }
        return true;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void write(Writer writer) throws IOException {
        writer.write(this.name);
        writer.write(61);
        if (this.quotes) {
            writer.write(34);
        }
        writer.write(this.value);
        if (this.quotes) {
            writer.write(34);
        }
    }
}

