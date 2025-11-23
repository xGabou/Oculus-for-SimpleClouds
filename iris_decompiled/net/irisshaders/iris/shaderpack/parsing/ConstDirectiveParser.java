/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConstDirectiveParser {
    public static List<ConstDirective> findDirectives(String source) {
        ArrayList<ConstDirective> directives = new ArrayList<ConstDirective>();
        for (String line : source.split("\\R")) {
            ConstDirectiveParser.findDirectiveInLine(line).ifPresent(directives::add);
        }
        return directives;
    }

    public static Optional<ConstDirective> findDirectiveInLine(String line) {
        Type type;
        if (!(line.contains("const") && line.contains("=") && line.contains(";"))) {
            return Optional.empty();
        }
        if (!(line = line.trim()).startsWith("const")) {
            return Optional.empty();
        }
        if (!ConstDirectiveParser.startsWithWhitespace(line = line.substring("const".length()))) {
            return Optional.empty();
        }
        if ((line = line.trim()).startsWith("int")) {
            type = Type.INT;
            line = line.substring("int".length());
        } else if (line.startsWith("float")) {
            type = Type.FLOAT;
            line = line.substring("float".length());
        } else if (line.startsWith("vec2")) {
            type = Type.VEC2;
            line = line.substring("vec2".length());
        } else if (line.startsWith("ivec3")) {
            type = Type.IVEC3;
            line = line.substring("ivec3".length());
        } else if (line.startsWith("vec4")) {
            type = Type.VEC4;
            line = line.substring("vec4".length());
        } else if (line.startsWith("bool")) {
            type = Type.BOOL;
            line = line.substring("bool".length());
        } else {
            return Optional.empty();
        }
        if (!ConstDirectiveParser.startsWithWhitespace(line)) {
            return Optional.empty();
        }
        int equalsIndex = line.indexOf(61);
        if (equalsIndex == -1) {
            return Optional.empty();
        }
        String key = line.substring(0, equalsIndex).trim();
        if (!ConstDirectiveParser.isWord(key)) {
            return Optional.empty();
        }
        String remaining = line.substring(equalsIndex + 1);
        int semicolonIndex = remaining.indexOf(59);
        if (semicolonIndex == -1) {
            return Optional.empty();
        }
        String value = remaining.substring(0, semicolonIndex).trim();
        return Optional.of(new ConstDirective(type, key, value));
    }

    private static boolean startsWithWhitespace(String text) {
        return !text.isEmpty() && Character.isWhitespace(text.charAt(0));
    }

    private static boolean isWord(String text) {
        if (text.isEmpty()) {
            return false;
        }
        for (char character : text.toCharArray()) {
            if (Character.isDigit(character) || Character.isAlphabetic(character) || character == '_') continue;
            return false;
        }
        return true;
    }

    public static enum Type {
        INT,
        FLOAT,
        VEC2,
        IVEC3,
        VEC4,
        BOOL;

    }

    public static class ConstDirective {
        private final Type type;
        private final String key;
        private final String value;

        ConstDirective(Type type, String key, String value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        public Type getType() {
            return this.type;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return "ConstDirective { " + this.type + " " + this.key + " = " + this.value + "; }";
        }
    }
}

