package net.Gabou.oculus_for_simpleclouds;

import com.mojang.logging.LogUtils;
import net.Gabou.oculus_for_simpleclouds.auth.ClientLauncherBlocker;
import net.Gabou.oculus_for_simpleclouds.auth.ClientLauncherGuards;
import net.Gabou.oculus_for_simpleclouds.client.ClientResourceReloadHandler;
import net.Gabou.oculus_for_simpleclouds.client.FinalCloudCompositeHandler;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhEventBridge;
import net.Gabou.oculus_for_simpleclouds.interiorfog.InteriorCloudClientEvents;
import net.Gabou.oculus_for_simpleclouds.interiorfog.InteriorCloudConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.slf4j.Logger;

@Mod(Oculus_for_simpleclouds.MODID)
public class Oculus_for_simpleclouds {
    public static boolean overWriteLogic = false;
    public static final String MODID = "oculus_for_simpleclouds";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Oculus_for_simpleclouds(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, InteriorCloudConfig.SPEC, MODID + "-client.toml");

        modEventBus.addListener(this::clientInit);

        modEventBus.addListener(ClientResourceReloadHandler::registerReloadListeners);
    }

    private void clientInit(FMLClientSetupEvent event) {
        ClientLauncherGuards.enforce();
        ClientLauncherBlocker.install();

        NeoForge.EVENT_BUS.addListener(FinalCloudCompositeHandler::onRenderStage);
        NeoForge.EVENT_BUS.register(InteriorCloudClientEvents.class);

        if (ModList.get().isLoaded("distanthorizons")) {
            event.enqueueWork(ShaderAwareDhEventBridge::register);
        }
        if (CompatHelper.isIrisLoaded() && !overWriteLogic) {
            event.enqueueWork(SimpleCloudsIrisWeatherCompat::init);
        }
    }
}
