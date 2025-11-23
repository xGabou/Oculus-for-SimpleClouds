/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

public class CodePointBuffer {
    private final Type type;
    private final ByteBuffer byteBuffer;
    private final CharBuffer charBuffer;
    private final IntBuffer intBuffer;

    private CodePointBuffer(Type type, ByteBuffer byteBuffer, CharBuffer charBuffer, IntBuffer intBuffer) {
        this.type = type;
        this.byteBuffer = byteBuffer;
        this.charBuffer = charBuffer;
        this.intBuffer = intBuffer;
    }

    public static CodePointBuffer withBytes(ByteBuffer byteBuffer) {
        return new CodePointBuffer(Type.BYTE, byteBuffer, null, null);
    }

    public static CodePointBuffer withChars(CharBuffer charBuffer) {
        return new CodePointBuffer(Type.CHAR, null, charBuffer, null);
    }

    public static CodePointBuffer withInts(IntBuffer intBuffer) {
        return new CodePointBuffer(Type.INT, null, null, intBuffer);
    }

    public int position() {
        switch (this.type) {
            case BYTE: {
                return this.byteBuffer.position();
            }
            case CHAR: {
                return this.charBuffer.position();
            }
            case INT: {
                return this.intBuffer.position();
            }
        }
        throw new UnsupportedOperationException("Not reached");
    }

    public void position(int newPosition) {
        switch (this.type) {
            case BYTE: {
                this.byteBuffer.position(newPosition);
                break;
            }
            case CHAR: {
                this.charBuffer.position(newPosition);
                break;
            }
            case INT: {
                this.intBuffer.position(newPosition);
            }
        }
    }

    public int remaining() {
        switch (this.type) {
            case BYTE: {
                return this.byteBuffer.remaining();
            }
            case CHAR: {
                return this.charBuffer.remaining();
            }
            case INT: {
                return this.intBuffer.remaining();
            }
        }
        throw new UnsupportedOperationException("Not reached");
    }

    public int get(int offset) {
        switch (this.type) {
            case BYTE: {
                return this.byteBuffer.get(offset);
            }
            case CHAR: {
                return this.charBuffer.get(offset);
            }
            case INT: {
                return this.intBuffer.get(offset);
            }
        }
        throw new UnsupportedOperationException("Not reached");
    }

    Type getType() {
        return this.type;
    }

    int arrayOffset() {
        switch (this.type) {
            case BYTE: {
                return this.byteBuffer.arrayOffset();
            }
            case CHAR: {
                return this.charBuffer.arrayOffset();
            }
            case INT: {
                return this.intBuffer.arrayOffset();
            }
        }
        throw new UnsupportedOperationException("Not reached");
    }

    byte[] byteArray() {
        assert (this.type == Type.BYTE);
        return this.byteBuffer.array();
    }

    char[] charArray() {
        assert (this.type == Type.CHAR);
        return this.charBuffer.array();
    }

    int[] intArray() {
        assert (this.type == Type.INT);
        return this.intBuffer.array();
    }

    public static Builder builder(int initialBufferSize) {
        return new Builder(initialBufferSize);
    }

    public static class Builder {
        private Type type = Type.BYTE;
        private ByteBuffer byteBuffer;
        private CharBuffer charBuffer;
        private IntBuffer intBuffer;
        private int prevHighSurrogate;

        private Builder(int initialBufferSize) {
            this.byteBuffer = ByteBuffer.allocate(initialBufferSize);
            this.charBuffer = null;
            this.intBuffer = null;
            this.prevHighSurrogate = -1;
        }

        Type getType() {
            return this.type;
        }

        ByteBuffer getByteBuffer() {
            return this.byteBuffer;
        }

        CharBuffer getCharBuffer() {
            return this.charBuffer;
        }

        IntBuffer getIntBuffer() {
            return this.intBuffer;
        }

        public CodePointBuffer build() {
            switch (this.type) {
                case BYTE: {
                    this.byteBuffer.flip();
                    break;
                }
                case CHAR: {
                    this.charBuffer.flip();
                    break;
                }
                case INT: {
                    this.intBuffer.flip();
                }
            }
            return new CodePointBuffer(this.type, this.byteBuffer, this.charBuffer, this.intBuffer);
        }

        private static int roundUpToNextPowerOfTwo(int i) {
            int nextPowerOfTwo = 32 - Integer.numberOfLeadingZeros(i - 1);
            return (int)Math.pow(2.0, nextPowerOfTwo);
        }

        public void ensureRemaining(int remainingNeeded) {
            switch (this.type) {
                case BYTE: {
                    if (this.byteBuffer.remaining() >= remainingNeeded) break;
                    int newCapacity = Builder.roundUpToNextPowerOfTwo(this.byteBuffer.capacity() + remainingNeeded);
                    ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
                    this.byteBuffer.flip();
                    newBuffer.put(this.byteBuffer);
                    this.byteBuffer = newBuffer;
                    break;
                }
                case CHAR: {
                    if (this.charBuffer.remaining() >= remainingNeeded) break;
                    int newCapacity = Builder.roundUpToNextPowerOfTwo(this.charBuffer.capacity() + remainingNeeded);
                    CharBuffer newBuffer = CharBuffer.allocate(newCapacity);
                    this.charBuffer.flip();
                    newBuffer.put(this.charBuffer);
                    this.charBuffer = newBuffer;
                    break;
                }
                case INT: {
                    if (this.intBuffer.remaining() >= remainingNeeded) break;
                    int newCapacity = Builder.roundUpToNextPowerOfTwo(this.intBuffer.capacity() + remainingNeeded);
                    IntBuffer newBuffer = IntBuffer.allocate(newCapacity);
                    this.intBuffer.flip();
                    newBuffer.put(this.intBuffer);
                    this.intBuffer = newBuffer;
                }
            }
        }

        public void append(CharBuffer utf16In) {
            this.ensureRemaining(utf16In.remaining());
            if (!utf16In.hasArray()) {
                throw new UnsupportedOperationException("TODO");
            }
            this.appendArray(utf16In);
        }

        private void appendArray(CharBuffer utf16In) {
            assert (utf16In.hasArray());
            switch (this.type) {
                case BYTE: {
                    this.appendArrayByte(utf16In);
                    break;
                }
                case CHAR: {
                    this.appendArrayChar(utf16In);
                    break;
                }
                case INT: {
                    this.appendArrayInt(utf16In);
                }
            }
        }

        private void appendArrayByte(CharBuffer utf16In) {
            assert (this.prevHighSurrogate == -1);
            char[] in = utf16In.array();
            int inOffset = utf16In.arrayOffset() + utf16In.position();
            int inLimit = utf16In.arrayOffset() + utf16In.limit();
            byte[] outByte = this.byteBuffer.array();
            int outOffset = this.byteBuffer.arrayOffset() + this.byteBuffer.position();
            while (inOffset < inLimit) {
                char c = in[inOffset];
                if (c > '\u00ff') {
                    utf16In.position(inOffset - utf16In.arrayOffset());
                    this.byteBuffer.position(outOffset - this.byteBuffer.arrayOffset());
                    if (!Character.isHighSurrogate(c)) {
                        this.byteToCharBuffer(utf16In.remaining());
                        this.appendArrayChar(utf16In);
                        return;
                    }
                    this.byteToIntBuffer(utf16In.remaining());
                    this.appendArrayInt(utf16In);
                    return;
                }
                outByte[outOffset] = (byte)(c & 0xFF);
                ++inOffset;
                ++outOffset;
            }
            utf16In.position(inOffset - utf16In.arrayOffset());
            this.byteBuffer.position(outOffset - this.byteBuffer.arrayOffset());
        }

        private void appendArrayChar(CharBuffer utf16In) {
            assert (this.prevHighSurrogate == -1);
            char[] in = utf16In.array();
            int inOffset = utf16In.arrayOffset() + utf16In.position();
            int inLimit = utf16In.arrayOffset() + utf16In.limit();
            char[] outChar = this.charBuffer.array();
            int outOffset = this.charBuffer.arrayOffset() + this.charBuffer.position();
            while (inOffset < inLimit) {
                char c = in[inOffset];
                if (Character.isHighSurrogate(c)) {
                    utf16In.position(inOffset - utf16In.arrayOffset());
                    this.charBuffer.position(outOffset - this.charBuffer.arrayOffset());
                    this.charToIntBuffer(utf16In.remaining());
                    this.appendArrayInt(utf16In);
                    return;
                }
                outChar[outOffset] = c;
                ++inOffset;
                ++outOffset;
            }
            utf16In.position(inOffset - utf16In.arrayOffset());
            this.charBuffer.position(outOffset - this.charBuffer.arrayOffset());
        }

        private void appendArrayInt(CharBuffer utf16In) {
            char[] in = utf16In.array();
            int inOffset = utf16In.arrayOffset() + utf16In.position();
            int inLimit = utf16In.arrayOffset() + utf16In.limit();
            int[] outInt = this.intBuffer.array();
            int outOffset = this.intBuffer.arrayOffset() + this.intBuffer.position();
            while (inOffset < inLimit) {
                char c = in[inOffset];
                ++inOffset;
                if (this.prevHighSurrogate != -1) {
                    if (Character.isLowSurrogate(c)) {
                        outInt[outOffset] = Character.toCodePoint((char)this.prevHighSurrogate, c);
                        ++outOffset;
                        this.prevHighSurrogate = -1;
                        continue;
                    }
                    outInt[outOffset] = this.prevHighSurrogate;
                    ++outOffset;
                    if (Character.isHighSurrogate(c)) {
                        this.prevHighSurrogate = c & 0xFFFF;
                        continue;
                    }
                    outInt[outOffset] = c & 0xFFFF;
                    ++outOffset;
                    this.prevHighSurrogate = -1;
                    continue;
                }
                if (Character.isHighSurrogate(c)) {
                    this.prevHighSurrogate = c & 0xFFFF;
                    continue;
                }
                outInt[outOffset] = c & 0xFFFF;
                ++outOffset;
            }
            if (this.prevHighSurrogate != -1) {
                outInt[outOffset] = this.prevHighSurrogate & 0xFFFF;
                ++outOffset;
            }
            utf16In.position(inOffset - utf16In.arrayOffset());
            this.intBuffer.position(outOffset - this.intBuffer.arrayOffset());
        }

        private void byteToCharBuffer(int toAppend) {
            this.byteBuffer.flip();
            CharBuffer newBuffer = CharBuffer.allocate(Math.max(this.byteBuffer.remaining() + toAppend, this.byteBuffer.capacity() / 2));
            while (this.byteBuffer.hasRemaining()) {
                newBuffer.put((char)(this.byteBuffer.get() & 0xFF));
            }
            this.type = Type.CHAR;
            this.byteBuffer = null;
            this.charBuffer = newBuffer;
        }

        private void byteToIntBuffer(int toAppend) {
            this.byteBuffer.flip();
            IntBuffer newBuffer = IntBuffer.allocate(Math.max(this.byteBuffer.remaining() + toAppend, this.byteBuffer.capacity() / 4));
            while (this.byteBuffer.hasRemaining()) {
                newBuffer.put(this.byteBuffer.get() & 0xFF);
            }
            this.type = Type.INT;
            this.byteBuffer = null;
            this.intBuffer = newBuffer;
        }

        private void charToIntBuffer(int toAppend) {
            this.charBuffer.flip();
            IntBuffer newBuffer = IntBuffer.allocate(Math.max(this.charBuffer.remaining() + toAppend, this.charBuffer.capacity() / 2));
            while (this.charBuffer.hasRemaining()) {
                newBuffer.put(this.charBuffer.get() & 0xFFFF);
            }
            this.type = Type.INT;
            this.charBuffer = null;
            this.intBuffer = newBuffer;
        }
    }

    public static enum Type {
        BYTE,
        CHAR,
        INT;

    }
}

