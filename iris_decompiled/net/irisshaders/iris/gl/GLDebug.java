/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.AMDDebugOutput
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL43C
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLDebugMessageAMDCallback
 *  org.lwjgl.opengl.GLDebugMessageAMDCallbackI
 *  org.lwjgl.opengl.GLDebugMessageARBCallback
 *  org.lwjgl.opengl.GLDebugMessageARBCallbackI
 *  org.lwjgl.opengl.GLDebugMessageCallback
 *  org.lwjgl.opengl.GLDebugMessageCallbackI
 *  org.lwjgl.opengl.KHRDebug
 *  org.lwjgl.system.APIUtil
 */
package net.irisshaders.iris.gl;

import java.io.PrintStream;
import java.util.function.Consumer;
import net.irisshaders.iris.Iris;
import org.lwjgl.opengl.AMDDebugOutput;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageAMDCallback;
import org.lwjgl.opengl.GLDebugMessageAMDCallbackI;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.APIUtil;

public final class GLDebug {
    private static DebugState debugState;

    public static int setupDebugMessageCallback() {
        GLDebug.reloadDebugState();
        return GLDebug.setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
    }

    private static void trace(Consumer<String> output) {
        StackTraceElement[] elems;
        for (StackTraceElement ste : elems = GLDebug.filterStackTrace(new Throwable(), 4).getStackTrace()) {
            output.accept(ste.toString());
        }
    }

    public static Throwable filterStackTrace(Throwable throwable, int offset) {
        StackTraceElement[] elems = throwable.getStackTrace();
        StackTraceElement[] filtered = new StackTraceElement[elems.length];
        int j = 0;
        for (int i = offset; i < elems.length; ++i) {
            filtered[j++] = elems[i];
        }
        StackTraceElement[] newElems = new StackTraceElement[j];
        System.arraycopy(filtered, 0, newElems, 0, j);
        throwable.setStackTrace(newElems);
        return throwable;
    }

    private static void printTrace(final PrintStream stream) {
        GLDebug.trace(new Consumer<String>(){
            boolean first = true;

            @Override
            public void accept(String str) {
                if (this.first) {
                    GLDebug.printDetail(stream, "Stacktrace", str);
                    this.first = false;
                } else {
                    GLDebug.printDetailLine(stream, "Stacktrace", str);
                }
            }
        });
    }

    public static int setupDebugMessageCallback(PrintStream stream) {
        GLCapabilities caps = GL.getCapabilities();
        if (caps.OpenGL43) {
            Iris.logger.info("[GL] Using OpenGL 4.3 for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                stream.println("[LWJGL] OpenGL debug message");
                GLDebug.printDetail(stream, "ID", String.format("0x%X", id));
                GLDebug.printDetail(stream, "Source", GLDebug.getDebugSource(source));
                GLDebug.printDetail(stream, "Type", GLDebug.getDebugType(type));
                GLDebug.printDetail(stream, "Severity", GLDebug.getDebugSeverity(severity));
                GLDebug.printDetail(stream, "Message", GLDebugMessageCallback.getMessage((int)length, (long)message));
                GLDebug.printTrace(stream);
            });
            GL43C.glDebugMessageControl((int)4352, (int)4352, (int)37190, (int[])null, (boolean)true);
            GL43C.glDebugMessageControl((int)4352, (int)4352, (int)37191, (int[])null, (boolean)false);
            GL43C.glDebugMessageControl((int)4352, (int)4352, (int)37192, (int[])null, (boolean)false);
            GL43C.glDebugMessageControl((int)4352, (int)4352, (int)33387, (int[])null, (boolean)false);
            GL43C.glDebugMessageCallback((GLDebugMessageCallbackI)proc, (long)0L);
            if ((GL43C.glGetInteger((int)33310) & 2) == 0) {
                Iris.logger.warn("[GL] Warning: A non-debug context may not produce any debug output.");
                GL43C.glEnable((int)37600);
                return 2;
            }
            return 1;
        }
        if (caps.GL_KHR_debug) {
            Iris.logger.info("[GL] Using KHR_debug for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                stream.println("[LWJGL] OpenGL debug message");
                GLDebug.printDetail(stream, "ID", String.format("0x%X", id));
                GLDebug.printDetail(stream, "Source", GLDebug.getDebugSource(source));
                GLDebug.printDetail(stream, "Type", GLDebug.getDebugType(type));
                GLDebug.printDetail(stream, "Severity", GLDebug.getDebugSeverity(severity));
                GLDebug.printDetail(stream, "Message", GLDebugMessageCallback.getMessage((int)length, (long)message));
                GLDebug.printTrace(stream);
            });
            KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)37190, (int[])null, (boolean)true);
            KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)37191, (int[])null, (boolean)false);
            KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)37192, (int[])null, (boolean)false);
            KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)33387, (int[])null, (boolean)false);
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)proc, (long)0L);
            if (caps.OpenGL30 && (GL43C.glGetInteger((int)33310) & 2) == 0) {
                Iris.logger.warn("[GL] Warning: A non-debug context may not produce any debug output.");
                GL43C.glEnable((int)37600);
                return 2;
            }
            return 1;
        }
        if (caps.GL_ARB_debug_output) {
            Iris.logger.info("[GL] Using ARB_debug_output for error logging.");
            GLDebugMessageARBCallback proc = GLDebugMessageARBCallback.create((source, type, id, severity, length, message, userParam) -> {
                stream.println("[LWJGL] ARB_debug_output message");
                GLDebug.printDetail(stream, "ID", String.format("0x%X", id));
                GLDebug.printDetail(stream, "Source", GLDebug.getSourceARB(source));
                GLDebug.printDetail(stream, "Type", GLDebug.getTypeARB(type));
                GLDebug.printDetail(stream, "Severity", GLDebug.getSeverityARB(severity));
                GLDebug.printDetail(stream, "Message", GLDebugMessageARBCallback.getMessage((int)length, (long)message));
                GLDebug.printTrace(stream);
            });
            ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)37190, (int[])null, (boolean)true);
            ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)37191, (int[])null, (boolean)false);
            ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)37192, (int[])null, (boolean)false);
            ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)33387, (int[])null, (boolean)false);
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)proc, (long)0L);
            return 1;
        }
        if (caps.GL_AMD_debug_output) {
            Iris.logger.info("[GL] Using AMD_debug_output for error logging.");
            GLDebugMessageAMDCallback proc = GLDebugMessageAMDCallback.create((id, category, severity, length, message, userParam) -> {
                stream.println("[LWJGL] AMD_debug_output message");
                GLDebug.printDetail(stream, "ID", String.format("0x%X", id));
                GLDebug.printDetail(stream, "Category", GLDebug.getCategoryAMD(category));
                GLDebug.printDetail(stream, "Severity", GLDebug.getSeverityAMD(severity));
                GLDebug.printDetail(stream, "Message", GLDebugMessageAMDCallback.getMessage((int)length, (long)message));
                GLDebug.printTrace(stream);
            });
            AMDDebugOutput.glDebugMessageEnableAMD((int)0, (int)37190, (int[])null, (boolean)true);
            AMDDebugOutput.glDebugMessageEnableAMD((int)0, (int)37191, (int[])null, (boolean)false);
            AMDDebugOutput.glDebugMessageEnableAMD((int)0, (int)37192, (int[])null, (boolean)false);
            AMDDebugOutput.glDebugMessageEnableAMD((int)0, (int)33387, (int[])null, (boolean)false);
            AMDDebugOutput.glDebugMessageCallbackAMD((GLDebugMessageAMDCallbackI)proc, (long)0L);
            return 1;
        }
        Iris.logger.info("[GL] No debug output implementation is available, cannot return debug info.");
        return 0;
    }

    public static int disableDebugMessages() {
        GLCapabilities caps = GL.getCapabilities();
        if (caps.OpenGL43) {
            GL43C.glDebugMessageCallback(null, (long)0L);
            return 1;
        }
        if (caps.GL_KHR_debug) {
            KHRDebug.glDebugMessageCallback(null, (long)0L);
            if (caps.OpenGL30 && (GL43C.glGetInteger((int)33310) & 2) == 0) {
                GL43C.glDisable((int)37600);
            }
            return 1;
        }
        if (caps.GL_ARB_debug_output) {
            ARBDebugOutput.glDebugMessageCallbackARB(null, (long)0L);
            return 1;
        }
        if (caps.GL_AMD_debug_output) {
            AMDDebugOutput.glDebugMessageCallbackAMD(null, (long)0L);
            return 1;
        }
        Iris.logger.info("[GL] No debug output implementation is available, cannot disable debug info.");
        return 0;
    }

    private static void printDetail(PrintStream stream, String type, String message) {
        stream.printf("\t%s: %s\n", type, message);
    }

    private static void printDetailLine(PrintStream stream, String type, String message) {
        stream.append("    ");
        for (int i = 0; i < type.length(); ++i) {
            stream.append(" ");
        }
        stream.append(message).append("\n");
    }

    private static String getDebugSource(int source) {
        switch (source) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken((int)source);
    }

    private static String getDebugType(int type) {
        return switch (type) {
            case 33356 -> "ERROR";
            case 33357 -> "DEPRECATED BEHAVIOR";
            case 33358 -> "UNDEFINED BEHAVIOR";
            case 33359 -> "PORTABILITY";
            case 33360 -> "PERFORMANCE";
            case 33361 -> "OTHER";
            case 33384 -> "MARKER";
            default -> APIUtil.apiUnknownToken((int)type);
        };
    }

    private static String getDebugSeverity(int severity) {
        return switch (severity) {
            case 33387 -> "NOTIFICATION";
            case 37190 -> "HIGH";
            case 37191 -> "MEDIUM";
            case 37192 -> "LOW";
            default -> APIUtil.apiUnknownToken((int)severity);
        };
    }

    private static String getSourceARB(int source) {
        return switch (source) {
            case 33350 -> "API";
            case 33351 -> "WINDOW SYSTEM";
            case 33352 -> "SHADER COMPILER";
            case 33353 -> "THIRD PARTY";
            case 33354 -> "APPLICATION";
            case 33355 -> "OTHER";
            default -> APIUtil.apiUnknownToken((int)source);
        };
    }

    private static String getTypeARB(int type) {
        return switch (type) {
            case 33356 -> "ERROR";
            case 33357 -> "DEPRECATED BEHAVIOR";
            case 33358 -> "UNDEFINED BEHAVIOR";
            case 33359 -> "PORTABILITY";
            case 33360 -> "PERFORMANCE";
            case 33361 -> "OTHER";
            default -> APIUtil.apiUnknownToken((int)type);
        };
    }

    private static String getSeverityARB(int severity) {
        return switch (severity) {
            case 37190 -> "HIGH";
            case 37191 -> "MEDIUM";
            case 37192 -> "LOW";
            default -> APIUtil.apiUnknownToken((int)severity);
        };
    }

    private static String getCategoryAMD(int category) {
        return switch (category) {
            case 37193 -> "API ERROR";
            case 37194 -> "WINDOW SYSTEM";
            case 37195 -> "DEPRECATION";
            case 37196 -> "UNDEFINED BEHAVIOR";
            case 37197 -> "PERFORMANCE";
            case 37198 -> "SHADER COMPILER";
            case 37199 -> "APPLICATION";
            case 37200 -> "OTHER";
            default -> APIUtil.apiUnknownToken((int)category);
        };
    }

    private static String getSeverityAMD(int severity) {
        return switch (severity) {
            case 37190 -> "HIGH";
            case 37191 -> "MEDIUM";
            case 37192 -> "LOW";
            default -> APIUtil.apiUnknownToken((int)severity);
        };
    }

    public static void reloadDebugState() {
        debugState = Iris.getIrisConfig().areDebugOptionsEnabled() && (GL.getCapabilities().GL_KHR_debug || GL.getCapabilities().OpenGL43) ? new KHRDebugState() : new UnsupportedDebugState();
    }

    public static void nameObject(int id, int object, String name) {
        debugState.nameObject(id, object, name);
    }

    public static void pushGroup(int id, String name) {
        debugState.pushGroup(id, name);
    }

    public static void popGroup() {
        debugState.popGroup();
    }

    private static class KHRDebugState
    implements DebugState {
        private int stackSize;

        private KHRDebugState() {
        }

        @Override
        public void nameObject(int id, int object, String name) {
            KHRDebug.glObjectLabel((int)id, (int)object, (CharSequence)name);
        }

        @Override
        public void pushGroup(int id, String name) {
            KHRDebug.glPushDebugGroup((int)33354, (int)id, (CharSequence)name);
            ++this.stackSize;
        }

        @Override
        public void popGroup() {
            if (this.stackSize != 0) {
                KHRDebug.glPopDebugGroup();
                --this.stackSize;
            }
        }
    }

    private static interface DebugState {
        public void nameObject(int var1, int var2, String var3);

        public void pushGroup(int var1, String var2);

        public void popGroup();
    }

    private static class UnsupportedDebugState
    implements DebugState {
        private UnsupportedDebugState() {
        }

        @Override
        public void nameObject(int id, int object, String name) {
        }

        @Override
        public void pushGroup(int id, String name) {
        }

        @Override
        public void popGroup() {
        }
    }
}

