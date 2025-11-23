/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.loading.LoadingModList
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.irisshaders.iris.compat.dh;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.LoadingModList;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class DHCompat {
    private static boolean dhPresent = true;
    private static boolean lastIncompatible;
    private static MethodHandle deletePipeline;
    private static MethodHandle incompatible;
    private static MethodHandle getDepthTex;
    private static MethodHandle getFarPlane;
    private static MethodHandle getNearPlane;
    private static MethodHandle getDepthTexNoTranslucent;
    private static MethodHandle checkFrame;
    private static MethodHandle getRenderDistance;
    private Object compatInternalInstance;

    public DHCompat(IrisRenderingPipeline pipeline, boolean renderDHShadow) {
        try {
            if (dhPresent) {
                this.compatInternalInstance = Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal").getDeclaredConstructor(pipeline.getClass(), Boolean.TYPE).newInstance(pipeline, renderDHShadow);
                lastIncompatible = incompatible.invoke(this.compatInternalInstance);
            }
        }
        catch (Throwable e) {
            lastIncompatible = false;
            Throwable throwable = e.getCause();
            if (throwable instanceof ShaderCompileException) {
                ShaderCompileException sce = (ShaderCompileException)throwable;
                throw sce;
            }
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException)e;
                throw new RuntimeException("Unknown error loading Distant Horizons compatibility.", ite.getCause());
            }
            throw new RuntimeException("Unknown error loading Distant Horizons compatibility.", e);
        }
    }

    public static Matrix4f getProjection() {
        if (!dhPresent) {
            return new Matrix4f((Matrix4fc)CapturedRenderingState.INSTANCE.getGbufferProjection());
        }
        Matrix4f projection = new Matrix4f((Matrix4fc)CapturedRenderingState.INSTANCE.getGbufferProjection());
        return new Matrix4f().setPerspective(projection.perspectiveFov(), projection.m11() / projection.m00(), DHCompat.getNearPlane(), DHCompat.getFarPlane());
    }

    public static void run() {
        try {
            if (LoadingModList.get().getModFileById("distanthorizons") != null) {
                deletePipeline = MethodHandles.lookup().findVirtual(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "clear", MethodType.methodType(Void.TYPE));
                MethodHandle setupEventHandlers = MethodHandles.lookup().findStatic(Class.forName("net.irisshaders.iris.compat.dh.LodRendererEvents"), "setupEventHandlers", MethodType.methodType(Void.TYPE));
                getDepthTex = MethodHandles.lookup().findVirtual(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "getStoredDepthTex", MethodType.methodType(Integer.TYPE));
                getRenderDistance = MethodHandles.lookup().findStatic(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "getRenderDistance", MethodType.methodType(Integer.TYPE));
                incompatible = MethodHandles.lookup().findVirtual(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "incompatiblePack", MethodType.methodType(Boolean.TYPE));
                getFarPlane = MethodHandles.lookup().findStatic(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "getFarPlane", MethodType.methodType(Float.TYPE));
                getNearPlane = MethodHandles.lookup().findStatic(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "getNearPlane", MethodType.methodType(Float.TYPE));
                getDepthTexNoTranslucent = MethodHandles.lookup().findVirtual(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "getDepthTexNoTranslucent", MethodType.methodType(Integer.TYPE));
                checkFrame = MethodHandles.lookup().findStatic(Class.forName("net.irisshaders.iris.compat.dh.DHCompatInternal"), "checkFrame", MethodType.methodType(Boolean.TYPE));
                setupEventHandlers.invoke();
            } else {
                dhPresent = false;
            }
        }
        catch (Throwable e) {
            dhPresent = false;
            if (LoadingModList.get().getModFileById("distanthorizons") != null) {
                if (e instanceof ExceptionInInitializerError) {
                    ExceptionInInitializerError eiie = (ExceptionInInitializerError)e;
                    throw new RuntimeException("Failure loading DH compat.", eiie.getCause());
                }
                throw new RuntimeException("DH found, but one or more API methods are missing. Iris requires DH [2.0.4] or DH API version [1.1.0] or newer. Please make sure you are on the latest version of DH and Iris.", e);
            }
            Iris.logger.info("DH not found, and classes not found.");
        }
    }

    public static boolean lastPackIncompatible() {
        return dhPresent && DHCompat.hasRenderingEnabled() && lastIncompatible;
    }

    public static float getFarPlane() {
        if (!dhPresent) {
            return 0.01f;
        }
        try {
            return getFarPlane.invoke();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static float getNearPlane() {
        if (!dhPresent) {
            return 0.01f;
        }
        try {
            return getNearPlane.invoke();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int getRenderDistance() {
        if (!dhPresent) {
            return Minecraft.m_91087_().f_91066_.m_193772_();
        }
        try {
            return getRenderDistance.invoke();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkFrame() {
        if (!dhPresent) {
            return false;
        }
        try {
            return checkFrame.invoke();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasRenderingEnabled() {
        if (!dhPresent) {
            return false;
        }
        return DHCompat.checkFrame();
    }

    public void clearPipeline() {
        if (this.compatInternalInstance == null) {
            return;
        }
        try {
            deletePipeline.invoke(this.compatInternalInstance);
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public int getDepthTex() {
        if (this.compatInternalInstance == null) {
            return -1;
        }
        try {
            return getDepthTex.invoke(this.compatInternalInstance);
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public int getDepthTexNoTranslucent() {
        if (this.compatInternalInstance == null) {
            return -1;
        }
        try {
            return getDepthTexNoTranslucent.invoke(this.compatInternalInstance);
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Object getInstance() {
        return this.compatInternalInstance;
    }
}

