package net.Gabou.oculus_for_simpleclouds;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Oculus_for_simpleclouds.MODID)
public class Oculus_for_simpleclouds {

    public static boolean overWriteLogic = false;

    // Define mod id in a common place for everything to reference
    public static final String MODID = "oculus_for_simpleclouds";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Oculus_for_simpleclouds(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::clientInit);

    }
    private void clientInit(FMLClientSetupEvent event) {
        if (CompatHelper.isIrisLoaded() && !overWriteLogic) {
            event.enqueueWork(SimpleCloudsIrisWeatherCompat::init);
        }
    }
}
