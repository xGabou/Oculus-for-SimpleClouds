/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import oculus.org.antlr.v4.runtime.CharStream;
import oculus.org.antlr.v4.runtime.CodePointBuffer;
import oculus.org.antlr.v4.runtime.CodePointCharStream;

public final class CharStreams {
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private CharStreams() {
    }

    public static CharStream fromPath(Path path) throws IOException {
        return CharStreams.fromPath(path, StandardCharsets.UTF_8);
    }

    public static CharStream fromPath(Path path, Charset charset) throws IOException {
        long size = Files.size(path);
        try (SeekableByteChannel channel = Files.newByteChannel(path, new OpenOption[0]);){
            CodePointCharStream codePointCharStream = CharStreams.fromChannel(channel, charset, 4096, CodingErrorAction.REPLACE, path.toString(), size);
            return codePointCharStream;
        }
    }

    public static CharStream fromFileName(String fileName) throws IOException {
        return CharStreams.fromPath(Paths.get(fileName, new String[0]), StandardCharsets.UTF_8);
    }

    public static CharStream fromFileName(String fileName, Charset charset) throws IOException {
        return CharStreams.fromPath(Paths.get(fileName, new String[0]), charset);
    }

    public static CharStream fromStream(InputStream is) throws IOException {
        return CharStreams.fromStream(is, StandardCharsets.UTF_8);
    }

    public static CharStream fromStream(InputStream is, Charset charset) throws IOException {
        return CharStreams.fromStream(is, charset, -1L);
    }

    public static CharStream fromStream(InputStream is, Charset charset, long inputSize) throws IOException {
        try (ReadableByteChannel channel = Channels.newChannel(is);){
            CodePointCharStream codePointCharStream = CharStreams.fromChannel(channel, charset, 4096, CodingErrorAction.REPLACE, "<unknown>", inputSize);
            return codePointCharStream;
        }
    }

    public static CharStream fromChannel(ReadableByteChannel channel) throws IOException {
        return CharStreams.fromChannel(channel, StandardCharsets.UTF_8);
    }

    public static CharStream fromChannel(ReadableByteChannel channel, Charset charset) throws IOException {
        return CharStreams.fromChannel(channel, 4096, CodingErrorAction.REPLACE, "<unknown>");
    }

    public static CodePointCharStream fromReader(Reader r) throws IOException {
        return CharStreams.fromReader(r, "<unknown>");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CodePointCharStream fromReader(Reader r, String sourceName) throws IOException {
        try {
            CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder(4096);
            CharBuffer charBuffer = CharBuffer.allocate(4096);
            while (r.read(charBuffer) != -1) {
                charBuffer.flip();
                codePointBufferBuilder.append(charBuffer);
                charBuffer.compact();
            }
            CodePointCharStream codePointCharStream = CodePointCharStream.fromBuffer(codePointBufferBuilder.build(), sourceName);
            return codePointCharStream;
        }
        finally {
            r.close();
        }
    }

    public static CodePointCharStream fromString(String s) {
        return CharStreams.fromString(s, "<unknown>");
    }

    public static CodePointCharStream fromString(String s, String sourceName) {
        CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder(s.length());
        CharBuffer cb = CharBuffer.allocate(s.length());
        cb.put(s);
        cb.flip();
        codePointBufferBuilder.append(cb);
        return CodePointCharStream.fromBuffer(codePointBufferBuilder.build(), sourceName);
    }

    public static CodePointCharStream fromChannel(ReadableByteChannel channel, int bufferSize, CodingErrorAction decodingErrorAction, String sourceName) throws IOException {
        return CharStreams.fromChannel(channel, StandardCharsets.UTF_8, bufferSize, decodingErrorAction, sourceName, -1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CodePointCharStream fromChannel(ReadableByteChannel channel, Charset charset, int bufferSize, CodingErrorAction decodingErrorAction, String sourceName, long inputSize) throws IOException {
        try {
            ByteBuffer utf8BytesIn = ByteBuffer.allocate(bufferSize);
            CharBuffer utf16CodeUnitsOut = CharBuffer.allocate(bufferSize);
            if (inputSize == -1L) {
                inputSize = bufferSize;
            } else if (inputSize > Integer.MAX_VALUE) {
                throw new IOException(String.format("inputSize %d larger than max %d", inputSize, Integer.MAX_VALUE));
            }
            CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder((int)inputSize);
            CharsetDecoder decoder = charset.newDecoder().onMalformedInput(decodingErrorAction).onUnmappableCharacter(decodingErrorAction);
            boolean endOfInput = false;
            while (!endOfInput) {
                int bytesRead = channel.read(utf8BytesIn);
                endOfInput = bytesRead == -1;
                utf8BytesIn.flip();
                CoderResult result = decoder.decode(utf8BytesIn, utf16CodeUnitsOut, endOfInput);
                if (result.isError() && decodingErrorAction.equals(CodingErrorAction.REPORT)) {
                    result.throwException();
                }
                utf16CodeUnitsOut.flip();
                codePointBufferBuilder.append(utf16CodeUnitsOut);
                utf8BytesIn.compact();
                utf16CodeUnitsOut.compact();
            }
            CoderResult flushResult = decoder.flush(utf16CodeUnitsOut);
            if (flushResult.isError() && decodingErrorAction.equals(CodingErrorAction.REPORT)) {
                flushResult.throwException();
            }
            utf16CodeUnitsOut.flip();
            codePointBufferBuilder.append(utf16CodeUnitsOut);
            CodePointBuffer codePointBuffer = codePointBufferBuilder.build();
            CodePointCharStream codePointCharStream = CodePointCharStream.fromBuffer(codePointBuffer, sourceName);
            return codePointCharStream;
        }
        finally {
            channel.close();
        }
    }
}

