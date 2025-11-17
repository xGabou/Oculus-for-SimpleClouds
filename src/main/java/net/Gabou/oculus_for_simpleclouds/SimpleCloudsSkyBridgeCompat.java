package net.Gabou.oculus_for_simpleclouds;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.logging.LogUtils;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
final class SimpleCloudsSkyBridgeCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String SKY_BRIDGE_CLASS = "net.irisshaders.oculus.api.OculusSkyBridge";

    private static boolean listenerRegistered;
    private static boolean registeredLayer;
    private static Object bridgeInstance;
    private static Method registerMethod;

    private SimpleCloudsSkyBridgeCompat() {
    }

    static void init() {
        if (!ensureBridge()) {
            LOGGER.debug("Oculus sky bridge API not present; skipping Simple Clouds layer registration.");
            return;
        }
        if (listenerRegistered) {
            return;
        }
        listenerRegistered = true;
        MinecraftForge.EVENT_BUS.addListener(SimpleCloudsSkyBridgeCompat::onClientTick);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || registeredLayer) {
            return;
        }
        Optional<SimpleCloudsRenderer> renderer = SimpleCloudsRenderer.getOptionalInstance();
        if (renderer.isEmpty()) {
            return;
        }
        RenderTarget target = renderer.get().getCloudTarget();
        if (target == null) {
            return;
        }
        if (tryRegister(target)) {
            registeredLayer = true;
        }
    }

    private static boolean ensureBridge() {
        if (registerMethod != null && bridgeInstance != null) {
            return true;
        }
        try {
            Class<?> bridgeClass = Class.forName(SKY_BRIDGE_CLASS);
            Field instanceField = bridgeClass.getField("INSTANCE");
            Object instance = instanceField.get(null);
            Method method = bridgeClass.getMethod("registerSkyLayer", String.class, RenderTarget.class, boolean.class);
            bridgeInstance = instance;
            registerMethod = method;
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        } catch (ReflectiveOperationException ex) {
            LOGGER.warn("Failed to initialize Oculus sky bridge reflection support", ex);
            return false;
        }
    }

    private static boolean tryRegister(RenderTarget target) {
        if (!ensureBridge()) {
            return false;
        }
        try {
            registerMethod.invoke(bridgeInstance, "simpleclouds", target, Boolean.TRUE);
            LOGGER.info("Registered Simple Clouds render target with Oculus sky bridge.");
            return true;
        } catch (ReflectiveOperationException ex) {
            LOGGER.warn("Failed to register Simple Clouds layer with Oculus sky bridge", ex);
            return false;
        }
    }
}
