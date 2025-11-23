/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  nonamecrackers2.crackerslib.common.compat.CompatHelper
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.client.vivecraft;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleCloudsVivecraftCompatHandler {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/SimpleCloudsVivecraftCompatHandler");
    private static final String VIVECRAFT_CLIENTDATAHOLDER_CLASSID = "org.vivecraft.client_vr.ClientDataHolderVR";
    @Nullable
    private static Method clientDataHolderVRGetter;
    private static boolean thrownError;

    @Nullable
    private static Object getClientDataHolderVR() {
        block6: {
            if (clientDataHolderVRGetter == null) {
                try {
                    clientDataHolderVRGetter = Class.forName(VIVECRAFT_CLIENTDATAHOLDER_CLASSID).getMethod("getInstance", new Class[0]);
                }
                catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
                    if (thrownError) break block6;
                    LOGGER.error("Error getting ClientDataHolderVR getInstance method", (Throwable)e);
                    thrownError = true;
                }
            }
        }
        try {
            return clientDataHolderVRGetter.invoke(null, new Object[0]);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (!thrownError) {
                LOGGER.error("Error getting ClientDataHolderVR", (Throwable)e);
                thrownError = true;
            }
            return null;
        }
    }

    public static RenderTarget getMainFrameBuffer() {
        block4: {
            Object clientDataHolder;
            if (CompatHelper.isVrActive() && (clientDataHolder = SimpleCloudsVivecraftCompatHandler.getClientDataHolderVR()) != null) {
                try {
                    Object renderer = clientDataHolder.getClass().getField("vrRenderer").get(clientDataHolder);
                    if (renderer != null) {
                        return (RenderTarget)renderer.getClass().getField("framebufferVrRender").get(renderer);
                    }
                }
                catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                    if (thrownError) break block4;
                    LOGGER.error("Error getting VR framebufer", (Throwable)e);
                    thrownError = true;
                }
            }
        }
        return Minecraft.m_91087_().m_91385_();
    }

    public static boolean renderThisPass() {
        block3: {
            Object clientDataHolder;
            if (CompatHelper.isVrActive() && (clientDataHolder = SimpleCloudsVivecraftCompatHandler.getClientDataHolderVR()) != null) {
                try {
                    Object renderPass = clientDataHolder.getClass().getField("currentPass").get(clientDataHolder);
                    return !renderPass.toString().equals("SCOPEL") && !renderPass.toString().equals("SCOPER");
                }
                catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                    if (thrownError) break block3;
                    LOGGER.error("Error getting renderpass", (Throwable)e);
                    thrownError = true;
                }
            }
        }
        return true;
    }

    public static boolean isPrimaryPass() {
        block3: {
            Object clientDataHolder;
            if (CompatHelper.isVrActive() && (clientDataHolder = SimpleCloudsVivecraftCompatHandler.getClientDataHolderVR()) != null) {
                try {
                    Object renderPass = clientDataHolder.getClass().getField("currentPass").get(clientDataHolder);
                    return renderPass.toString().equals("LEFT");
                }
                catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                    if (thrownError) break block3;
                    LOGGER.error("Error getting renderpass", (Throwable)e);
                    thrownError = true;
                }
            }
        }
        return true;
    }

    public static int getStormFogResolutionDivisor() {
        if (CompatHelper.isVrActive()) {
            return 8;
        }
        return 4;
    }
}

