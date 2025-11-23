/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.nio.charset.StandardCharsets;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.CodePointBuffer;
import oculus.org.antlr.v4.runtime.misc.Interval;

public abstract class CodePointCharStream
implements CharStream {
    protected final int size;
    protected final String name;
    protected int position;

    private CodePointCharStream(int position, int remaining, String name) {
        assert (position == 0);
        this.size = remaining;
        this.name = name;
        this.position = 0;
    }

    abstract Object getInternalStorage();

    public static CodePointCharStream fromBuffer(CodePointBuffer codePointBuffer) {
        return CodePointCharStream.fromBuffer(codePointBuffer, "<unknown>");
    }

    public static CodePointCharStream fromBuffer(CodePointBuffer codePointBuffer, String name) {
        switch (codePointBuffer.getType()) {
            case BYTE: {
                return new CodePoint8BitCharStream(codePointBuffer.position(), codePointBuffer.remaining(), name, codePointBuffer.byteArray(), codePointBuffer.arrayOffset());
            }
            case CHAR: {
                return new CodePoint16BitCharStream(codePointBuffer.position(), codePointBuffer.remaining(), name, codePointBuffer.charArray(), codePointBuffer.arrayOffset());
            }
            case INT: {
                return new CodePoint32BitCharStream(codePointBuffer.position(), codePointBuffer.remaining(), name, codePointBuffer.intArray(), codePointBuffer.arrayOffset());
            }
        }
        throw new UnsupportedOperationException("Not reached");
    }

    @Override
    public final void consume() {
        if (this.size - this.position == 0) {
            assert (this.LA(1) == -1);
            throw new IllegalStateException("cannot consume EOF");
        }
        ++this.position;
    }

    @Override
    public final int index() {
        return this.position;
    }

    @Override
    public final int size() {
        return this.size;
    }

    @Override
    public final int mark() {
        return -1;
    }

    @Override
    public final void release(int marker) {
    }

    @Override
    public final void seek(int index) {
        this.position = index;
    }

    @Override
    public final String getSourceName() {
        if (this.name == null || this.name.isEmpty()) {
            return "<unknown>";
        }
        return this.name;
    }

    public final String toString() {
        return this.getText(Interval.of(0, this.size - 1));
    }

    private static final class CodePoint32BitCharStream
    extends CodePointCharStream {
        private final int[] intArray;

        private CodePoint32BitCharStream(int position, int remaining, String name, int[] intArray, int arrayOffset) {
            super(position, remaining, name);
            this.intArray = intArray;
            assert (arrayOffset == 0);
        }

        @Override
        public String getText(Interval interval) {
            int startIdx = Math.min(interval.a, this.size);
            int len = Math.min(interval.b - interval.a + 1, this.size - startIdx);
            return new String(this.intArray, startIdx, len);
        }

        @Override
        public int LA(int i) {
            switch (Integer.signum(i)) {
                case -1: {
                    int offset = this.position + i;
                    if (offset < 0) {
                        return -1;
                    }
                    return this.intArray[offset];
                }
                case 0: {
                    return 0;
                }
                case 1: {
                    int offset = this.position + i - 1;
                    if (offset >= this.size) {
                        return -1;
                    }
                    return this.intArray[offset];
                }
            }
            throw new UnsupportedOperationException("Not reached");
        }

        @Override
        Object getInternalStorage() {
            return this.intArray;
        }
    }

    private static final class CodePoint16BitCharStream
    extends CodePointCharStream {
        private final char[] charArray;

        private CodePoint16BitCharStream(int position, int remaining, String name, char[] charArray, int arrayOffset) {
            super(position, remaining, name);
            this.charArray = charArray;
            assert (arrayOffset == 0);
        }

        @Override
        public String getText(Interval interval) {
            int startIdx = Math.min(interval.a, this.size);
            int len = Math.min(interval.b - interval.a + 1, this.size - startIdx);
            return new String(this.charArray, startIdx, len);
        }

        @Override
        public int LA(int i) {
            switch (Integer.signum(i)) {
                case -1: {
                    int offset = this.position + i;
                    if (offset < 0) {
                        return -1;
                    }
                    return this.charArray[offset] & 0xFFFF;
                }
                case 0: {
                    return 0;
                }
                case 1: {
                    int offset = this.position + i - 1;
                    if (offset >= this.size) {
                        return -1;
                    }
                    return this.charArray[offset] & 0xFFFF;
                }
            }
            throw new UnsupportedOperationException("Not reached");
        }

        @Override
        Object getInternalStorage() {
            return this.charArray;
        }
    }

    private static final class CodePoint8BitCharStream
    extends CodePointCharStream {
        private final byte[] byteArray;

        private CodePoint8BitCharStream(int position, int remaining, String name, byte[] byteArray, int arrayOffset) {
            super(position, remaining, name);
            assert (arrayOffset == 0);
            this.byteArray = byteArray;
        }

        @Override
        public String getText(Interval interval) {
            int startIdx = Math.min(interval.a, this.size);
            int len = Math.min(interval.b - interval.a + 1, this.size - startIdx);
            return new String(this.byteArray, startIdx, len, StandardCharsets.ISO_8859_1);
        }

        @Override
        public int LA(int i) {
            switch (Integer.signum(i)) {
                case -1: {
                    int offset = this.position + i;
                    if (offset < 0) {
                        return -1;
                    }
                    return this.byteArray[offset] & 0xFF;
                }
                case 0: {
                    return 0;
                }
                case 1: {
                    int offset = this.position + i - 1;
                    if (offset >= this.size) {
                        return -1;
                    }
                    return this.byteArray[offset] & 0xFF;
                }
            }
            throw new UnsupportedOperationException("Not reached");
        }

        @Override
        Object getInternalStorage() {
            return this.byteArray;
        }
    }
}

