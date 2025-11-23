/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import oculus.org.antlr.v4.runtime.misc.IntegerList;
import oculus.org.antlr.v4.runtime.misc.IntervalSet;

public class Utils {
    public static <T> String join(Iterator<T> iter, String separator) {
        StringBuilder buf = new StringBuilder();
        while (iter.hasNext()) {
            buf.append(iter.next());
            if (!iter.hasNext()) continue;
            buf.append(separator);
        }
        return buf.toString();
    }

    public static <T> String join(T[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
            if (i >= array.length - 1) continue;
            builder.append(separator);
        }
        return builder.toString();
    }

    public static int numNonnull(Object[] data) {
        int n = 0;
        if (data == null) {
            return n;
        }
        for (Object o : data) {
            if (o == null) continue;
            ++n;
        }
        return n;
    }

    public static <T> void removeAllElements(Collection<T> data, T value) {
        if (data == null) {
            return;
        }
        while (data.contains(value)) {
            data.remove(value);
        }
    }

    public static String escapeWhitespace(String s, boolean escapeSpaces) {
        StringBuilder buf = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == ' ' && escapeSpaces) {
                buf.append('\u00b7');
                continue;
            }
            if (c == '\t') {
                buf.append("\\t");
                continue;
            }
            if (c == '\n') {
                buf.append("\\n");
                continue;
            }
            if (c == '\r') {
                buf.append("\\r");
                continue;
            }
            buf.append(c);
        }
        return buf.toString();
    }

    public static void writeFile(String fileName, String content) throws IOException {
        Utils.writeFile(fileName, content, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void writeFile(String fileName, String content, String encoding) throws IOException {
        File f = new File(fileName);
        FileOutputStream fos = new FileOutputStream(f);
        try (OutputStreamWriter osw = encoding != null ? new OutputStreamWriter((OutputStream)fos, encoding) : new OutputStreamWriter(fos);){
            osw.write(content);
        }
    }

    public static char[] readFile(String fileName) throws IOException {
        return Utils.readFile(fileName, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static char[] readFile(String fileName, String encoding) throws IOException {
        File f = new File(fileName);
        int size = (int)f.length();
        FileInputStream fis = new FileInputStream(fileName);
        char[] data = null;
        try (InputStreamReader isr = encoding != null ? new InputStreamReader((InputStream)fis, encoding) : new InputStreamReader(fis);){
            data = new char[size];
            int n = isr.read(data);
            if (n < data.length) {
                data = Arrays.copyOf(data, n);
            }
        }
        return data;
    }

    public static Map<String, Integer> toMap(String[] keys) {
        HashMap<String, Integer> m = new HashMap<String, Integer>();
        for (int i = 0; i < keys.length; ++i) {
            m.put(keys[i], i);
        }
        return m;
    }

    public static char[] toCharArray(IntegerList data) {
        if (data == null) {
            return null;
        }
        return data.toCharArray();
    }

    public static IntervalSet toSet(BitSet bits) {
        IntervalSet s = new IntervalSet(new int[0]);
        int i = bits.nextSetBit(0);
        while (i >= 0) {
            s.add(i);
            i = bits.nextSetBit(i + 1);
        }
        return s;
    }

    public static String expandTabs(String s, int tabSize) {
        if (s == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        int col = 0;
        block4: for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\n': {
                    col = 0;
                    buf.append(c);
                    continue block4;
                }
                case '\t': {
                    int n = tabSize - col % tabSize;
                    col += n;
                    buf.append(Utils.spaces(n));
                    continue block4;
                }
                default: {
                    ++col;
                    buf.append(c);
                }
            }
        }
        return buf.toString();
    }

    public static String spaces(int n) {
        return Utils.sequence(n, " ");
    }

    public static String newlines(int n) {
        return Utils.sequence(n, "\n");
    }

    public static String sequence(int n, String s) {
        StringBuilder buf = new StringBuilder();
        for (int sp = 1; sp <= n; ++sp) {
            buf.append(s);
        }
        return buf.toString();
    }

    public static int count(String s, char x) {
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != x) continue;
            ++n;
        }
        return n;
    }
}

