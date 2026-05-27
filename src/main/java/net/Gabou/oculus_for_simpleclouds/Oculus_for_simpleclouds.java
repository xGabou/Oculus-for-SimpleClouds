package net.Gabou.oculus_for_simpleclouds;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.Gabou.oculus_for_simpleclouds.auth.AuthNetwork;
import net.Gabou.oculus_for_simpleclouds.auth.ClientLauncherGuards;
import net.Gabou.oculus_for_simpleclouds.auth.ServerAuth;
import net.Gabou.oculus_for_simpleclouds.dh.ShaderAwareDhEventBridge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import net.Gabou.oculus_for_simpleclouds.interiorfog.InteriorCloudClientEvents;
import net.Gabou.oculus_for_simpleclouds.interiorfog.InteriorCloudConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Oculus_for_simpleclouds.MODID)
public class Oculus_for_simpleclouds {

    public static boolean overWriteLogic = false;

    // Define mod id in a common place for everything to reference
    public static final String MODID = "oculus_for_simpleclouds";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Oculus_for_simpleclouds(FMLJavaModLoadingContext context) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, InteriorCloudConfig.SPEC, MODID + "-client.toml");
        context.getModEventBus().addListener(this::commonInit);
        context.getModEventBus().addListener(this::clientInit);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
    }

    private void commonInit(FMLCommonSetupEvent event) {
        event.enqueueWork(AuthNetwork::register);
    }

    private void clientInit(FMLClientSetupEvent event) {
        ClientLauncherGuards.enforce();
        MinecraftForge.EVENT_BUS.register(InteriorCloudClientEvents.class);
        event.enqueueWork(ShaderAwareDhEventBridge::register);
        if (CompatHelper.isOculusLoaded() && !overWriteLogic) {
            event.enqueueWork(SimpleCloudsIrisWeatherCompat::init);
        }
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            if (ServerAuth.onLogin(player.connection.connection, player)) {
                ServerAuth.sendChallenge(player);
            }
        }
    }

    private void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            ServerAuth.onLogout(player);
        }
    }

    private void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ServerAuth.onTick(event.getServer());
        }
    }
}
