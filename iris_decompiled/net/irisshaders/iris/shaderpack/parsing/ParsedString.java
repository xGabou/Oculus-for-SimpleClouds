/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.parsing;

import org.jetbrains.annotations.Nullable;

public class ParsedString {
    private String text;

    public ParsedString(String text) {
        this.text = text;
    }

    public boolean takeLiteral(String token) {
        if (!this.text.startsWith(token)) {
            return false;
        }
        this.text = this.text.substring(token.length());
        return true;
    }

    public boolean takeSomeWhitespace() {
        if (this.text.isEmpty() || !Character.isWhitespace(this.text.charAt(0))) {
            return false;
        }
        this.text = this.text.trim();
        return true;
    }

    public boolean takeComments() {
        if (!this.text.startsWith("//")) {
            return false;
        }
        this.text = this.text.substring(2);
        while (this.text.startsWith("/")) {
            this.text = this.text.substring(1);
        }
        return true;
    }

    public boolean currentlyContains(String text) {
        return this.text.contains(text);
    }

    public boolean isEnd() {
        return this.text.isEmpty();
    }

    public String takeRest() {
        return this.text;
    }

    private String takeCharacters(int numChars) {
        String result = this.text.substring(0, numChars);
        this.text = this.text.substring(numChars);
        return result;
    }

    @Nullable
    public String takeWord() {
        if (this.isEnd()) {
            return null;
        }
        int position = 0;
        for (char character : this.text.toCharArray()) {
            if (!Character.isDigit(character) && !Character.isAlphabetic(character) && character != '_') break;
            ++position;
        }
        if (position == 0) {
            return null;
        }
        return this.takeCharacters(position);
    }

    @Nullable
    public String takeNumber() {
        int position;
        if (this.isEnd()) {
            return null;
        }
        for (position = 0; position < this.text.length() && (position + 1 >= this.text.length() || Character.isDigit(this.text.charAt(position)) || Character.isDigit(this.text.charAt(position + 1))); ++position) {
        }
        try {
            Float.parseFloat(this.text.substring(0, position));
        }
        catch (Exception e) {
            return null;
        }
        return this.takeCharacters(position);
    }

    @Nullable
    public String takeWordOrNumber() {
        String number = this.takeNumber();
        if (number == null) {
            return this.takeWord();
        }
        return number;
    }
}

