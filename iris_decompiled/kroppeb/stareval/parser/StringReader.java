/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.parser;

import kroppeb.stareval.exception.UnexpectedCharacterException;

public class StringReader {
    private final String string;
    private int nextIndex = -1;
    private int lastIndex;
    private int mark;

    public StringReader(String string) {
        this.string = string;
        this.advanceOneCharacter();
        this.skipWhitespace();
        this.lastIndex = this.nextIndex;
        this.mark();
    }

    private void advanceOneCharacter() {
        this.lastIndex = this.nextIndex;
        if (this.nextIndex >= this.string.length()) {
            return;
        }
        ++this.nextIndex;
    }

    public void skipWhitespace() {
        while (this.nextIndex < this.string.length() && Character.isWhitespace(this.string.charAt(this.nextIndex))) {
            ++this.nextIndex;
        }
    }

    public char peek() {
        return this.string.charAt(this.nextIndex);
    }

    public void skipOneCharacter() {
        this.advanceOneCharacter();
    }

    public char read() {
        char current = this.peek();
        this.skipOneCharacter();
        return current;
    }

    public void read(char c) throws UnexpectedCharacterException {
        char read = this.read();
        if (read != c) {
            throw new UnexpectedCharacterException(c, read, this.getCurrentIndex());
        }
    }

    public boolean tryRead(char c) {
        if (!this.canRead()) {
            return false;
        }
        char read = this.peek();
        if (read != c) {
            return false;
        }
        this.skipOneCharacter();
        return true;
    }

    public void mark() {
        this.mark = this.lastIndex;
    }

    public String substring() {
        return this.string.substring(this.mark, this.lastIndex + 1);
    }

    public boolean canRead() {
        return this.nextIndex < this.string.length();
    }

    public int getCurrentIndex() {
        return this.lastIndex;
    }
}

