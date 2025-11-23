/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  javax.annotation.Nullable
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraftforge.client.event.RegisterShadersEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.shader.SingleSSBOShaderInstance;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleCloudsShaders {
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/SimpleCloudsShaders");
    private static SingleSSBOShaderInstance clouds;
    private static SingleSSBOShaderInstance cloudsTransparency;
    private static SingleSSBOShaderInstance stormFogShadowMap;
    private static SingleSSBOShaderInstance cloudsShadowMap;
    private static ShaderInstance cloudRegionTex;
    private static boolean shadersInitialized;
    @Nullable
    private static Throwable error;

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader((ShaderInstance)new SingleSSBOShaderInstance(event.getResourceProvider(), SimpleCloudsMod.id("clouds"), DefaultVertexFormat.f_85814_, "SideInfoBuffer"), s -> {
                clouds = (SingleSSBOShaderInstance)((Object)s);
            });
            event.registerShader((ShaderInstance)new SingleSSBOShaderInstance(event.getResourceProvider(), SimpleCloudsMod.id("clouds_transparency"), DefaultVertexFormat.f_85814_, "TransparentCubeInfoBuffer"), s -> {
                cloudsTransparency = (SingleSSBOShaderInstance)((Object)s);
            });
            event.registerShader((ShaderInstance)new SingleSSBOShaderInstance(event.getResourceProvider(), SimpleCloudsMod.id("storm_fog_shadow_map"), DefaultVertexFormat.f_85814_, "SideInfoBuffer"), s -> {
                stormFogShadowMap = (SingleSSBOShaderInstance)((Object)s);
            });
            event.registerShader((ShaderInstance)new SingleSSBOShaderInstance(event.getResourceProvider(), SimpleCloudsMod.id("clouds_shadow_map"), DefaultVertexFormat.f_85814_, "SideInfoBuffer"), s -> {
                cloudsShadowMap = (SingleSSBOShaderInstance)((Object)s);
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(), SimpleCloudsMod.id("cloud_region_tex"), DefaultVertexFormat.f_85817_), s -> {
                cloudRegionTex = s;
            });
            shadersInitialized = true;
            error = null;
        }
        catch (IOException e) {
            LOGGER.error("Failed to set up shaders: ", (Throwable)e);
            shadersInitialized = false;
            error = e;
        }
    }

    public static boolean areShadersInitialized() {
        return shadersInitialized;
    }

    @Nullable
    public static Throwable getError() {
        return error;
    }

    public static SingleSSBOShaderInstance getCloudsShader() {
        return Objects.requireNonNull(clouds, "Clouds shader not initialized yet");
    }

    public static SingleSSBOShaderInstance getCloudsTransparencyShader() {
        return Objects.requireNonNull(cloudsTransparency, "Clouds transparency shader not initialized yet");
    }

    public static SingleSSBOShaderInstance getStormFogShadowMapShader() {
        return Objects.requireNonNull(stormFogShadowMap, "Storm fog shadow map shader not initialized yet");
    }

    public static SingleSSBOShaderInstance getCloudsShadowMapShader() {
        return Objects.requireNonNull(cloudsShadowMap, "Clouds shadow map shader not initialized yet");
    }

    public static ShaderInstance getCloudRegionTexShader() {
        return Objects.requireNonNull(cloudRegionTex, "Cloud region tex shader not initialized yet");
    }
}

