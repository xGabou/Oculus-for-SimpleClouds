/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.misc.Interval;

@Deprecated
public class ANTLRInputStream
implements CharStream {
    public static final int READ_BUFFER_SIZE = 1024;
    public static final int INITIAL_BUFFER_SIZE = 1024;
    protected char[] data;
    protected int n;
    protected int p = 0;
    public String name;

    public ANTLRInputStream() {
    }

    public ANTLRInputStream(String input) {
        this.data = input.toCharArray();
        this.n = input.length();
    }

    public ANTLRInputStream(char[] data, int numberOfActualCharsInArray) {
        this.data = data;
        this.n = numberOfActualCharsInArray;
    }

    public ANTLRInputStream(Reader r) throws IOException {
        this(r, 1024, 1024);
    }

    public ANTLRInputStream(Reader r, int initialSize) throws IOException {
        this(r, initialSize, 1024);
    }

    public ANTLRInputStream(Reader r, int initialSize, int readChunkSize) throws IOException {
        this.load(r, initialSize, readChunkSize);
    }

    public ANTLRInputStream(InputStream input) throws IOException {
        this(new InputStreamReader(input), 1024);
    }

    public ANTLRInputStream(InputStream input, int initialSize) throws IOException {
        this(new InputStreamReader(input), initialSize);
    }

    public ANTLRInputStream(InputStream input, int initialSize, int readChunkSize) throws IOException {
        this(new InputStreamReader(input), initialSize, readChunkSize);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load(Reader r, int size, int readChunkSize) throws IOException {
        if (r == null) {
            return;
        }
        if (size <= 0) {
            size = 1024;
        }
        if (readChunkSize <= 0) {
            readChunkSize = 1024;
        }
        try {
            this.data = new char[size];
            int numRead = 0;
            int p = 0;
            do {
                if (p + readChunkSize > this.data.length) {
                    this.data = Arrays.copyOf(this.data, this.data.length * 2);
                }
                numRead = r.read(this.data, p, readChunkSize);
                p += numRead;
            } while (numRead != -1);
            this.n = p + 1;
        }
        finally {
            r.close();
        }
    }

    public void reset() {
        this.p = 0;
    }

    @Override
    public void consume() {
        if (this.p >= this.n) {
            assert (this.LA(1) == -1);
            throw new IllegalStateException("cannot consume EOF");
        }
        if (this.p < this.n) {
            ++this.p;
        }
    }

    @Override
    public int LA(int i) {
        if (i == 0) {
            return 0;
        }
        if (i < 0 && this.p + ++i - 1 < 0) {
            return -1;
        }
        if (this.p + i - 1 >= this.n) {
            return -1;
        }
        return this.data[this.p + i - 1];
    }

    public int LT(int i) {
        return this.LA(i);
    }

    @Override
    public int index() {
        return this.p;
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public int mark() {
        return -1;
    }

    @Override
    public void release(int marker) {
    }

    @Override
    public void seek(int index) {
        if (index <= this.p) {
            this.p = index;
            return;
        }
        index = Math.min(index, this.n);
        while (this.p < index) {
            this.consume();
        }
    }

    @Override
    public String getText(Interval interval) {
        int start = interval.a;
        int stop = interval.b;
        if (stop >= this.n) {
            stop = this.n - 1;
        }
        int count = stop - start + 1;
        if (start >= this.n) {
            return "";
        }
        return new String(this.data, start, count);
    }

    @Override
    public String getSourceName() {
        if (this.name == null || this.name.isEmpty()) {
            return "<unknown>";
        }
        return this.name;
    }

    public String toString() {
        return new String(this.data);
    }
}

