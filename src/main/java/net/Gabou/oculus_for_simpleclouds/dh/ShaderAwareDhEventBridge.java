package net.Gabou.oculus_for_simpleclouds.dh;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeApplyShaderRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderPassEvent;
import dev.nonamecrackers2.simpleclouds.client.dh.event.SimpleCloudsAfterDhRenderHandler;
import dev.nonamecrackers2.simpleclouds.client.dh.event.SimpleCloudsBeforeDhRenderHandler;
import dev.nonamecrackers2.simpleclouds.client.dh.event.SimpleCloudsDhSetupHandler;
import net.minecraftforge.fml.ModList;

public final class ShaderAwareDhEventBridge {
    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }
        if (!ModList.get().isLoaded("distanthorizons")) {
            return;
        }

        try {
            DhApi.events.bind(DhApiBeforeRenderPassEvent.class, new SimpleCloudsDhSetupHandler());
            DhApi.events.bind(DhApiBeforeApplyShaderRenderEvent.class, new SimpleCloudsBeforeDhRenderHandler());
            DhApi.events.bind(DhApiAfterRenderEvent.class, new SimpleCloudsAfterDhRenderHandler());
            registered = true;
        } catch (Throwable t) {
            System.out.println("[OFSC WARN] Failed to register DH cloud render events: " + t);
            t.printStackTrace(System.out);
        }
    }

    private ShaderAwareDhEventBridge() {
    }
}
