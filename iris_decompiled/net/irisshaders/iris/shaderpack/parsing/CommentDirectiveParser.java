/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.parsing;

import java.util.Optional;
import java.util.function.Supplier;
import net.irisshaders.iris.shaderpack.parsing.CommentDirective;

public class CommentDirectiveParser {
    private CommentDirectiveParser() {
    }

    public static Optional<CommentDirective> findDirective(String haystack, CommentDirective.Type type) {
        String needle = type.name();
        String prefix = needle + ":";
        String suffix = "*/";
        int indexOfPrefix = haystack.lastIndexOf(prefix);
        if (indexOfPrefix == -1) {
            return Optional.empty();
        }
        String before = haystack.substring(0, indexOfPrefix).trim();
        if (!before.endsWith("/*")) {
            return Optional.empty();
        }
        int indexOfSuffix = (haystack = haystack.substring(indexOfPrefix + prefix.length())).indexOf(suffix);
        if (indexOfSuffix == -1) {
            return Optional.empty();
        }
        haystack = haystack.substring(0, indexOfSuffix).trim();
        return Optional.of(new CommentDirective(CommentDirective.Type.valueOf(needle), haystack, indexOfPrefix));
    }

    private static class Tests {
        private Tests() {
        }

        private static <T> void test(String name, T expected, Supplier<T> testCase) {
            T actual;
            try {
                actual = testCase.get();
            }
            catch (Throwable e) {
                System.err.println("Test \"" + name + "\" failed with an exception:");
                e.printStackTrace();
                return;
            }
            if (!expected.equals(actual)) {
                System.err.println("Test \"" + name + "\" failed: Expected " + expected + ", got " + actual);
            } else {
                System.out.println("Test \"" + name + "\" passed");
            }
        }

        public static void main(String[] args) {
            Tests.test("normal text", Optional.empty(), () -> {
                String line = "Some normal text that doesn't contain a DRAWBUFFERS directive of any sort";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("partial directive", Optional.empty(), () -> {
                String line = "Some normal text that doesn't contain a /* DRAWBUFFERS: directive of any sort";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("bad spacing", Optional.of("321"), () -> {
                String line = "/*DRAWBUFFERS:321*/ OptiFine will detect this directive, but ShadersMod will not...";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("matchAtEnd", Optional.of("321"), () -> {
                String line = "A line containing a drawbuffers directive: /* DRAWBUFFERS:321 */";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("matchAtStart", Optional.of("31"), () -> {
                String line = "/* DRAWBUFFERS:31 */ This is a line containing a drawbuffers directive";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("matchInMiddle", Optional.of("31"), () -> {
                String line = "This is a line /* DRAWBUFFERS:31 */ containing a drawbuffers directive";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("emptyMatch", Optional.of(""), () -> {
                String line = "/* DRAWBUFFERS: */ This is a line containing an invalid but still matching drawbuffers directive";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("duplicates", Optional.of("3"), () -> {
                String line = "/* TEST:2 */ This line contains multiple directives, the last one should be used /* TEST:3 */";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
            Tests.test("multi-line", Optional.of("It works"), () -> {
                String lines = "/* Here's a random comment line */\n/* RENDERTARGETS:Duplicate handling? */\nuniform sampler2D test;\n/* RENDERTARGETS:Duplicate handling within a line? */ Let's see /* RENDERTARGETS:It works */\n";
                return CommentDirectiveParser.findDirective(lines, CommentDirective.Type.RENDERTARGETS).map(CommentDirective::getDirective);
            });
            Tests.test("bad spacing from BSL composite6", Optional.of("12"), () -> {
                String line = "    /*DRAWBUFFERS:12*/";
                return CommentDirectiveParser.findDirective(line, CommentDirective.Type.DRAWBUFFERS).map(CommentDirective::getDirective);
            });
        }
    }
}

