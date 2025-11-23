/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL20C
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.irisshaders.iris.gl.shader;

import java.nio.ByteBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class ShaderWorkarounds {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void safeShaderSource(int glId, CharSequence source) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            ByteBuffer sourceBuffer = MemoryUtil.memUTF8((CharSequence)source, (boolean)true);
            PointerBuffer pointers = stack.mallocPointer(1);
            pointers.put(sourceBuffer);
            GL20C.nglShaderSource((int)glId, (int)1, (long)pointers.address0(), (long)0L);
            APIUtil.apiArrayFree((long)pointers.address0(), (int)1);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }
}

