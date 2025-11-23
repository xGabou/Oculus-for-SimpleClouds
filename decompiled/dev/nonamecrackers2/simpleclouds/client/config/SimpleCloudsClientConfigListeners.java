/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  nonamecrackers2.crackerslib.client.gui.Popup
 *  nonamecrackers2.crackerslib.common.config.listener.ConfigListener
 */
package dev.nonamecrackers2.simpleclouds.client.config;

import com.google.common.base.Joiner;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.CloudMode;
import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.SingleRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.gui.Popup;
import nonamecrackers2.crackerslib.common.config.listener.ConfigListener;

public class SimpleCloudsClientConfigListeners {
    public static void registerListener() {
        ConfigListener.builder((ModConfig.Type)ModConfig.Type.CLIENT, (String)"simpleclouds").addListener(SimpleCloudsConfig.CLIENT.cloudMode, (o, n) -> SimpleCloudsClientConfigListeners.requestReload(true)).addListener(SimpleCloudsConfig.CLIENT.shadedClouds, (o, n) -> SimpleCloudsClientConfigListeners.requestReload(false)).addListener(SimpleCloudsConfig.CLIENT.transparency, (o, n) -> SimpleCloudsClientConfigListeners.requestReload(false)).addListener(SimpleCloudsConfig.CLIENT.levelOfDetail, (o, n) -> SimpleCloudsClientConfigListeners.requestReload(false)).addListener(SimpleCloudsConfig.CLIENT.distantShadows, (o, n) -> SimpleCloudsClientConfigListeners.requestReload(false)).addListener(SimpleCloudsConfig.CLIENT.shadowDistance, (o, n) -> SimpleCloudsClientConfigListeners.requestReload(false)).addListener(SimpleCloudsConfig.CLIENT.concurrentComputeDispatches, (o, n) -> SimpleCloudsClientConfigListeners.requestReload(false)).addListener(SimpleCloudsConfig.CLIENT.singleModeCloudType, (o, n) -> SimpleCloudsClientConfigListeners.onSingleModeCloudTypeUpdated(n)).addListener(SimpleCloudsConfig.CLIENT.customRainSounds, (o, n) -> SimpleCloudsClientConfigListeners.reloadResources()).buildAndRegister();
    }

    public static void onCloudModeUpdatedFromServer(CloudMode mode) {
        SimpleCloudsConfig.SERVER.cloudMode.set((Object)mode);
        Popup.createInfoPopup(null, (int)300, (Component)Component.m_237115_((String)"gui.simpleclouds.reload_confirmation.server.info"), () -> SimpleCloudsRenderer.getInstance().requestReload());
    }

    public static void onSingleModeCloudTypeUpdatedFromServer(String type) {
        SimpleCloudsConfig.SERVER.singleModeCloudType.set((Object)type);
        CloudMeshGenerator cloudMeshGenerator = SimpleCloudsRenderer.getInstance().getMeshGenerator();
        if (cloudMeshGenerator instanceof SingleRegionCloudMeshGenerator) {
            SingleRegionCloudMeshGenerator generator = (SingleRegionCloudMeshGenerator)cloudMeshGenerator;
            ClientSideCloudTypeManager.getInstance().getCloudTypeFromRawId(type).ifPresentOrElse(t -> generator.setCloudType((CloudInfo)t), () -> generator.setCloudType(SimpleCloudsConstants.EMPTY));
        }
    }

    public static void onSingleModeCloudTypeUpdated(String type) {
        Minecraft.m_91087_().execute(() -> {
            if (ClientCloudManager.isAvailableServerSide()) {
                return;
            }
            ResourceLocation loc = ResourceLocation.m_135820_((String)type);
            Map<ResourceLocation, CloudType> types = ClientSideCloudTypeManager.getInstance().getCloudTypes();
            if (loc != null && types.containsKey(loc) && ClientSideCloudTypeManager.isValidClientSideSingleModeCloudType(types.get(loc))) {
                CloudMeshGenerator patt3926$temp = SimpleCloudsRenderer.getInstance().getMeshGenerator();
                if (patt3926$temp instanceof SingleRegionCloudMeshGenerator) {
                    SingleRegionCloudMeshGenerator generator = (SingleRegionCloudMeshGenerator)patt3926$temp;
                    generator.setCloudType(types.get(loc));
                }
            } else {
                MutableComponent valid = Component.m_237113_((String)Joiner.on((String)", ").join(types.values().stream().filter(t -> ClientSideCloudTypeManager.isValidClientSideSingleModeCloudType(t)).map(t -> t.id().toString()).iterator())).m_130940_(ChatFormatting.YELLOW);
                Popup.createInfoPopup(null, (int)300, (Component)Component.m_237110_((String)"gui.simpleclouds.unknown_or_invalid_client_side_cloud_type.info", (Object[])new Object[]{loc == null ? type : loc.toString(), valid}));
            }
        });
    }

    public static void requestReload(boolean skipIfServerAvailable) {
        Minecraft.m_91087_().execute(() -> {
            if (skipIfServerAvailable && ClientCloudManager.isAvailableServerSide()) {
                return;
            }
            Popup.createYesNoPopup(null, () -> SimpleCloudsRenderer.getInstance().requestReload(), (int)300, (Component)Component.m_237115_((String)"gui.simpleclouds.requires_reload.info"));
            Popup.clearQueue();
        });
    }

    public static void reloadResources() {
        Minecraft.m_91087_().execute(() -> Popup.createYesNoPopup(null, () -> Minecraft.m_91087_().m_91391_(), (int)300, (Component)Component.m_237115_((String)"gui.simpleclouds.requires_reload_resource_packs.info")));
    }
}

