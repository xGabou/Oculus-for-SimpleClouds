/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  com.mojang.blaze3d.platform.GlDebug
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.ChatFormatting
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.client.ConfigScreenHandler$ConfigScreenFactory
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.IExtensionPoint$DisplayTest
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  net.minecraftforge.fml.loading.FMLPaths
 *  net.minecraftforge.fml.loading.LoadingModList
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris;

import com.google.common.base.Throwables;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.InputConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import net.irisshaders.iris.IrisLogging;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.config.IrisConfig;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.gui.debug.DebugLoadFailedGridScreen;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.VanillaRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.DimensionId;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.discovery.ShaderpackDirectoryManager;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.Profile;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.texture.pbr.PBRTextureManager;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import org.jetbrains.annotations.NotNull;

@Mod(value="oculus")
public class Iris {
    public static final String MODID = "oculus";
    public static final String MODNAME = "Oculus";
    public static final IrisLogging logger = new IrisLogging("Oculus");
    private static final Map<String, String> shaderPackOptionQueue = new HashMap<String, String>();
    private static final String backupVersionNumber = "1.20.3";
    public static NamespacedId lastDimension = null;
    public static boolean testing = false;
    private static Path shaderpacksDirectory;
    private static ShaderpackDirectoryManager shaderpacksDirectoryManager;
    private static ShaderPack currentPack;
    private static String currentPackName;
    private static Optional<Exception> storedError;
    private static boolean initialized;
    private static PipelineManager pipelineManager;
    private static IrisConfig irisConfig;
    private static FileSystem zipFileSystem;
    private static KeyMapping reloadKeybind;
    private static KeyMapping toggleShadersKeybind;
    private static KeyMapping shaderpackScreenKeybind;
    private static KeyMapping wireframeKeybind;
    private static boolean resetShaderPackOptions;
    private static String IRIS_VERSION;
    private static boolean fallback;
    private static boolean loadPackWhenPossible;
    private static boolean renderSystemInit;

    public Iris() {
        try {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onKeyRegister);
            MinecraftForge.EVENT_BUS.addListener(this::onKeyInput);
            IRIS_VERSION = ((ModContainer)ModList.get().getModContainerById(MODID).get()).getModInfo().getVersion().toString();
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ShaderPackScreen((Screen)screen)));
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31", (a, b) -> true));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void loadShaderpackWhenPossible() {
        loadPackWhenPossible = true;
    }

    public static boolean isPackInUseQuick() {
        return pipelineManager.getPipelineNullable() instanceof IrisRenderingPipeline;
    }

    public void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(reloadKeybind);
        event.register(toggleShadersKeybind);
        event.register(shaderpackScreenKeybind);
    }

    public void onKeyInput(InputEvent.Key event) {
        Iris.handleKeybinds(Minecraft.m_91087_());
    }

    public static void onRenderSystemInit() {
        if (!initialized) {
            logger.warn("Iris::onRenderSystemInit was called, but Iris::onEarlyInitialize was not called. Trying to avoid a crash but this is an odd state.");
            return;
        }
        PBRTextureManager.INSTANCE.init();
        renderSystemInit = true;
        if (LoadingModList.get().getModFileById("distanthorizons") == null) {
            Iris.loadShaderpack();
        }
    }

    public static void duringRenderSystemInit() {
        Iris.setDebug(irisConfig.areDebugOptionsEnabled());
    }

    public static void onLoadingComplete() {
        if (!initialized) {
            logger.warn("Iris::onLoadingComplete was called, but Iris::onEarlyInitialize was not called. Trying to avoid a crash but this is an odd state.");
            return;
        }
        lastDimension = DimensionId.OVERWORLD;
        Iris.getPipelineManager().preparePipeline(DimensionId.OVERWORLD);
    }

    public static void handleKeybinds(Minecraft minecraft) {
        block13: {
            if (reloadKeybind.m_90859_()) {
                try {
                    Iris.reload();
                    if (minecraft.f_91074_ != null) {
                        minecraft.f_91074_.m_5661_((Component)Component.m_237115_((String)"iris.shaders.reloaded"), false);
                    }
                    break block13;
                }
                catch (Exception e) {
                    logger.error("Error while reloading Shaders for Oculus!", e);
                    if (minecraft.f_91074_ != null) {
                        minecraft.f_91074_.m_5661_((Component)Component.m_237110_((String)"iris.shaders.reloaded.failure", (Object[])new Object[]{Throwables.getRootCause((Throwable)e).getMessage()}).m_130940_(ChatFormatting.RED), false);
                    }
                    break block13;
                }
            }
            if (toggleShadersKeybind.m_90859_()) {
                try {
                    Iris.toggleShaders(minecraft, !irisConfig.areShadersEnabled());
                }
                catch (Exception e) {
                    logger.error("Error while toggling shaders!", e);
                    if (minecraft.f_91074_ != null) {
                        minecraft.f_91074_.m_5661_((Component)Component.m_237110_((String)"iris.shaders.toggled.failure", (Object[])new Object[]{Throwables.getRootCause((Throwable)e).getMessage()}).m_130940_(ChatFormatting.RED), false);
                    }
                    Iris.setShadersDisabled();
                    fallback = true;
                }
            } else if (shaderpackScreenKeybind.m_90859_()) {
                minecraft.m_91152_((Screen)new ShaderPackScreen(null));
            } else if (wireframeKeybind.m_90859_() && irisConfig.areDebugOptionsEnabled() && minecraft.f_91074_ != null && !Minecraft.m_91087_().m_91090_()) {
                minecraft.f_91074_.m_5661_((Component)Component.m_237113_((String)"No cheating; wireframe only in singleplayer!"), false);
            }
        }
    }

    public static boolean shouldActivateWireframe() {
        return irisConfig.areDebugOptionsEnabled() && wireframeKeybind.m_90857_();
    }

    public static void toggleShaders(Minecraft minecraft, boolean enabled) throws IOException {
        irisConfig.setShadersEnabled(enabled);
        irisConfig.save();
        Iris.reload();
        if (minecraft.f_91074_ != null) {
            minecraft.f_91074_.m_5661_((Component)(enabled ? Component.m_237110_((String)"iris.shaders.toggled", (Object[])new Object[]{currentPackName}) : Component.m_237115_((String)"iris.shaders.disabled")), false);
        }
    }

    public static void loadShaderpack() {
        if (irisConfig == null) {
            if (!initialized) {
                throw new IllegalStateException("Iris::loadShaderpack was called, but Iris::onInitializeClient wasn't called yet. How did this happen?");
            }
            throw new NullPointerException("Iris.irisConfig was null unexpectedly");
        }
        if (!irisConfig.areShadersEnabled()) {
            logger.info("Shaders are disabled because enableShaders is set to false in iris.properties");
            Iris.setShadersDisabled();
            return;
        }
        Optional<String> externalName = irisConfig.getShaderPackName();
        if (externalName.isEmpty()) {
            logger.info("Shaders are disabled because no valid shaderpack is selected");
            Iris.setShadersDisabled();
            return;
        }
        if (!Iris.loadExternalShaderpack(externalName.get())) {
            logger.warn("Falling back to normal rendering without shaders because the shaderpack could not be loaded");
            Iris.setShadersDisabled();
            fallback = true;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static boolean loadExternalShaderpack(String name) {
        Path shaderPackPath;
        Path shaderPackConfigTxt;
        Path shaderPackRoot;
        try {
            shaderPackRoot = Iris.getShaderpacksDirectory().resolve(name);
            shaderPackConfigTxt = Iris.getShaderpacksDirectory().resolve(name + ".txt");
        }
        catch (InvalidPathException e) {
            logger.error("Failed to load the shaderpack \"{}\" because it contains invalid characters in its path", name);
            return false;
        }
        if (!Iris.isValidShaderpack(shaderPackRoot)) {
            logger.error("Pack \"{}\" is not valid! Can't load it.", name);
            return false;
        }
        if (!Files.isDirectory(shaderPackRoot, new LinkOption[0]) && shaderPackRoot.toString().endsWith(".zip")) {
            Optional<Path> optionalPath;
            try {
                optionalPath = Iris.loadExternalZipShaderpack(shaderPackRoot);
            }
            catch (FileSystemNotFoundException | NoSuchFileException e) {
                logger.error("Failed to load the shaderpack \"{}\" because it does not exist in your shaderpacks folder!", name);
                return false;
            }
            catch (ZipException e) {
                logger.error("The shaderpack \"{}\" appears to be corrupted, please try downloading it again!", name);
                return false;
            }
            catch (IOException e) {
                logger.error("Failed to load the shaderpack \"{}\"!", name);
                logger.error("", e);
                return false;
            }
            if (!optionalPath.isPresent()) {
                logger.error("Could not load the shaderpack \"{}\" because it appears to lack a \"shaders\" directory", name);
                return false;
            }
            shaderPackPath = optionalPath.get();
        } else {
            if (!Files.exists(shaderPackRoot, new LinkOption[0])) {
                logger.error("Failed to load the shaderpack \"{}\" because it does not exist!", name);
                return false;
            }
            shaderPackPath = shaderPackRoot.resolve("shaders");
        }
        if (!Files.exists(shaderPackPath, new LinkOption[0])) {
            logger.error("Could not load the shaderpack \"{}\" because it appears to lack a \"shaders\" directory", name);
            return false;
        }
        Map changedConfigs = Iris.tryReadConfigProperties(shaderPackConfigTxt).map(properties -> properties).orElse(new HashMap());
        changedConfigs.putAll(shaderPackOptionQueue);
        Iris.clearShaderPackOptionQueue();
        if (resetShaderPackOptions) {
            changedConfigs.clear();
        }
        resetShaderPackOptions = false;
        try {
            currentPack = new ShaderPack(shaderPackPath, changedConfigs, StandardMacros.createStandardEnvironmentDefines());
            MutableOptionValues changedConfigsValues = currentPack.getShaderPackOptions().getOptionValues().mutableCopy();
            Properties configsToSave = new Properties();
            changedConfigsValues.getBooleanValues().forEach((k, v) -> configsToSave.setProperty((String)k, Boolean.toString(v)));
            changedConfigsValues.getStringValues().forEach(configsToSave::setProperty);
            Iris.tryUpdateConfigPropertiesFile(shaderPackConfigTxt, configsToSave);
        }
        catch (Exception e) {
            logger.error("Failed to load the shaderpack \"{}\"!", name);
            logger.error("", e);
            return false;
        }
        fallback = false;
        currentPackName = name;
        logger.info("Using shaderpack: " + name);
        return true;
    }

    private static Optional<Path> loadExternalZipShaderpack(Path shaderpackPath) throws IOException {
        FileSystem zipSystem;
        zipFileSystem = zipSystem = FileSystems.newFileSystem(shaderpackPath, Iris.class.getClassLoader());
        Path root = zipSystem.getRootDirectories().iterator().next();
        Path potentialShaderDir = zipSystem.getPath("shaders", new String[0]);
        if (Files.exists(potentialShaderDir, new LinkOption[0])) {
            return Optional.of(potentialShaderDir);
        }
        try (Stream<Path> stream = Files.walk(root, new FileVisitOption[0]);){
            Optional<Path> optional = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).filter(path -> path.endsWith("shaders")).findFirst();
            return optional;
        }
    }

    private static void setShadersDisabled() {
        currentPack = null;
        fallback = false;
        currentPackName = "(off)";
        logger.info("Shaders are disabled");
    }

    public static void setDebug(boolean enable) {
        int success;
        try {
            irisConfig.setDebugEnabled(enable);
            irisConfig.save();
        }
        catch (IOException e) {
            logger.fatal("Failed to save config!", e);
        }
        if (enable) {
            success = GLDebug.setupDebugMessageCallback();
        } else {
            GLDebug.reloadDebugState();
            GlDebug.m_84049_((int)Minecraft.m_91087_().f_91066_.f_92035_, (boolean)false);
            success = 1;
        }
        logger.info("Debug functionality is " + (enable ? "enabled, logging will be more verbose!" : "disabled."));
        if (Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().f_91074_.m_5661_((Component)Component.m_237115_((String)(success != 0 ? (enable ? "iris.shaders.debug.enabled" : "iris.shaders.debug.disabled") : "iris.shaders.debug.failure")), false);
            if (success == 2) {
                Minecraft.m_91087_().f_91074_.m_5661_((Component)Component.m_237115_((String)"iris.shaders.debug.restart"), false);
            }
        }
    }

    private static Optional<Properties> tryReadConfigProperties(Path path) {
        Properties properties = new Properties();
        if (Files.exists(path, new LinkOption[0])) {
            try (InputStream is = Files.newInputStream(path, new OpenOption[0]);){
                properties.load(is);
            }
            catch (IOException e) {
                return Optional.empty();
            }
        }
        return Optional.of(properties);
    }

    private static void tryUpdateConfigPropertiesFile(Path path, Properties properties) {
        try {
            if (properties.isEmpty()) {
                if (Files.exists(path, new LinkOption[0])) {
                    Files.delete(path);
                }
                return;
            }
            try (OutputStream out = Files.newOutputStream(path, new OpenOption[0]);){
                properties.store(out, null);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static boolean isValidToShowPack(Path pack) {
        return Files.isDirectory(pack, new LinkOption[0]) || pack.toString().endsWith(".zip");
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static boolean isValidShaderpack(Path pack) {
        if (Files.isDirectory(pack, new LinkOption[0])) {
            if (pack.equals(Iris.getShaderpacksDirectory())) {
                return false;
            }
            try (Stream<Path> stream = Files.walk(pack, new FileVisitOption[0]);){
                boolean bl = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).filter(path -> !path.equals(pack)).anyMatch(path -> path.endsWith("shaders"));
                return bl;
            }
            catch (IOException ignored) {
                return false;
            }
        }
        if (pack.toString().endsWith(".zip")) {
            try (FileSystem zipSystem = FileSystems.newFileSystem(pack, Iris.class.getClassLoader());){
                boolean bl;
                block26: {
                    Path root = zipSystem.getRootDirectories().iterator().next();
                    Stream<Path> stream = Files.walk(root, new FileVisitOption[0]);
                    try {
                        bl = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).anyMatch(path -> path.endsWith("shaders"));
                        if (stream == null) break block26;
                    }
                    catch (Throwable throwable) {
                        if (stream != null) {
                            try {
                                stream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    stream.close();
                }
                return bl;
            }
            catch (ZipError zipError) {
                logger.warn("The ZIP at " + pack + " is corrupt");
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return false;
    }

    public static Map<String, String> getShaderPackOptionQueue() {
        return shaderPackOptionQueue;
    }

    public static void queueShaderPackOptionsFromProfile(Profile profile) {
        Iris.getShaderPackOptionQueue().putAll(profile.optionValues);
    }

    public static void queueShaderPackOptionsFromProperties(Properties properties) {
        Iris.queueDefaultShaderPackOptionValues();
        properties.stringPropertyNames().forEach(key -> Iris.getShaderPackOptionQueue().put((String)key, properties.getProperty((String)key)));
    }

    public static void queueDefaultShaderPackOptionValues() {
        Iris.clearShaderPackOptionQueue();
        Iris.getCurrentPack().ifPresent(pack -> {
            OptionSet options = pack.getShaderPackOptions().getOptionSet();
            OptionValues values = pack.getShaderPackOptions().getOptionValues();
            options.getStringOptions().forEach((key, mOpt) -> {
                if (values.getStringValue((String)key).isPresent()) {
                    Iris.getShaderPackOptionQueue().put((String)key, mOpt.getOption().getDefaultValue());
                }
            });
            options.getBooleanOptions().forEach((key, mOpt) -> {
                if (values.getBooleanValue((String)key) != OptionalBoolean.DEFAULT) {
                    Iris.getShaderPackOptionQueue().put((String)key, Boolean.toString(mOpt.getOption().getDefaultValue()));
                }
            });
        });
    }

    public static void clearShaderPackOptionQueue() {
        Iris.getShaderPackOptionQueue().clear();
    }

    public static void resetShaderPackOptionsOnNextReload() {
        resetShaderPackOptions = true;
    }

    public static boolean shouldResetShaderPackOptionsOnNextReload() {
        return resetShaderPackOptions;
    }

    public static void reload() throws IOException {
        irisConfig.initialize();
        Iris.destroyEverything();
        Iris.loadShaderpack();
        if (Minecraft.m_91087_().f_91073_ != null) {
            Iris.getPipelineManager().preparePipeline(Iris.getCurrentDimension());
        }
    }

    private static void destroyEverything() {
        currentPack = null;
        Iris.getPipelineManager().destroyPipeline();
        if (zipFileSystem != null) {
            try {
                zipFileSystem.close();
            }
            catch (NoSuchFileException e) {
                logger.warn("Failed to close the shaderpack zip when reloading because it was deleted, proceeding anyways.");
            }
            catch (IOException e) {
                logger.error("Failed to close zip file system?", e);
            }
        }
    }

    public static NamespacedId getCurrentDimension() {
        ClientLevel level = Minecraft.m_91087_().f_91073_;
        if (level != null) {
            return new NamespacedId(level.m_46472_().m_135782_().m_135827_(), level.m_46472_().m_135782_().m_135815_());
        }
        return lastDimension;
    }

    private static WorldRenderingPipeline createPipeline(NamespacedId dimensionId) {
        if (currentPack == null) {
            return new VanillaRenderingPipeline();
        }
        ProgramSet programs = currentPack.getProgramSet(dimensionId);
        try {
            return new IrisRenderingPipeline(programs);
        }
        catch (Exception e) {
            if (irisConfig.areDebugOptionsEnabled()) {
                Minecraft.m_91087_().m_91152_((Screen)new DebugLoadFailedGridScreen(Minecraft.m_91087_().f_91080_, (Component)Component.m_237113_((String)(e instanceof ShaderCompileException ? "Failed to compile shaders" : "Exception")), e));
            } else if (Minecraft.m_91087_().f_91074_ != null) {
                Minecraft.m_91087_().f_91074_.m_5661_((Component)Component.m_237115_((String)(e instanceof ShaderCompileException ? "iris.load.failure.shader" : "iris.load.failure.generic")).m_7220_((Component)Component.m_237113_((String)"Copy Info").m_130938_(arg -> arg.m_131162_(Boolean.valueOf(true)).m_131140_(ChatFormatting.BLUE).m_131142_(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, e.getMessage())))), false);
            } else {
                storedError = Optional.of(e);
            }
            logger.error("Failed to create shader rendering pipeline, disabling shaders!", e);
            fallback = true;
            return new VanillaRenderingPipeline();
        }
    }

    @NotNull
    public static PipelineManager getPipelineManager() {
        block4: {
            if (pipelineManager == null) {
                pipelineManager = new PipelineManager(Iris::createPipeline);
            }
            if (loadPackWhenPossible && renderSystemInit) {
                loadPackWhenPossible = false;
                try {
                    Iris.reload();
                }
                catch (IOException e) {
                    logger.error("Error while reloading Shaders for Oculus!", e);
                    if (Minecraft.m_91087_().f_91074_ == null) break block4;
                    Minecraft.m_91087_().f_91074_.m_5661_((Component)Component.m_237110_((String)"iris.shaders.reloaded.failure", (Object[])new Object[]{Throwables.getRootCause((Throwable)e).getMessage()}).m_130940_(ChatFormatting.RED), false);
                }
            }
        }
        return pipelineManager;
    }

    public static Optional<Exception> getStoredError() {
        Optional<Exception> stored = storedError;
        storedError = Optional.empty();
        return stored;
    }

    @NotNull
    public static Optional<ShaderPack> getCurrentPack() {
        return Optional.ofNullable(currentPack);
    }

    public static String getCurrentPackName() {
        return currentPackName;
    }

    public static IrisConfig getIrisConfig() {
        return irisConfig;
    }

    public static boolean isFallback() {
        return fallback;
    }

    public static String getVersion() {
        if (IRIS_VERSION == null) {
            return "Version info unknown!";
        }
        return IRIS_VERSION;
    }

    public static String getFormattedVersion() {
        ChatFormatting color;
        Object version = Iris.getVersion();
        if (!FMLEnvironment.production) {
            color = ChatFormatting.GOLD;
            version = (String)version + " (Development Environment)";
        } else {
            color = ((String)version).endsWith("-dirty") || ((String)version).contains("unknown") || ((String)version).endsWith("-nogit") ? ChatFormatting.RED : (((String)version).contains("+rev.") ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GREEN);
        }
        return color + (String)version;
    }

    public static String getReleaseTarget() {
        SharedConstants.m_142977_();
        return SharedConstants.m_183709_().m_132498_() ? SharedConstants.m_183709_().m_132493_() : backupVersionNumber;
    }

    public static String getBackupVersionNumber() {
        return backupVersionNumber;
    }

    public static Path getShaderpacksDirectory() {
        if (shaderpacksDirectory == null) {
            shaderpacksDirectory = FMLPaths.GAMEDIR.get().resolve("shaderpacks");
        }
        return shaderpacksDirectory;
    }

    public static ShaderpackDirectoryManager getShaderpacksDirectoryManager() {
        if (shaderpacksDirectoryManager == null) {
            shaderpacksDirectoryManager = new ShaderpackDirectoryManager(Iris.getShaderpacksDirectory());
        }
        return shaderpacksDirectoryManager;
    }

    public static boolean loadedIncompatiblePack() {
        return DHCompat.lastPackIncompatible();
    }

    public static void onEarlyInitialize() {
        reloadKeybind = new KeyMapping("iris.keybind.reload", InputConstants.Type.KEYSYM, 82, "iris.keybinds");
        toggleShadersKeybind = new KeyMapping("iris.keybind.toggleShaders", InputConstants.Type.KEYSYM, 75, "iris.keybinds");
        shaderpackScreenKeybind = new KeyMapping("iris.keybind.shaderPackSelection", InputConstants.Type.KEYSYM, 79, "iris.keybinds");
        wireframeKeybind = new KeyMapping("iris.keybind.wireframe", InputConstants.Type.KEYSYM, InputConstants.f_84822_.m_84873_(), "iris.keybinds");
        DHCompat.run();
        try {
            if (!Files.exists(Iris.getShaderpacksDirectory(), new LinkOption[0])) {
                Files.createDirectories(Iris.getShaderpacksDirectory(), new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            logger.warn("Failed to create the shaderpacks directory!");
            logger.warn("", e);
        }
        irisConfig = new IrisConfig(FMLPaths.CONFIGDIR.get().resolve("oculus.properties"));
        try {
            irisConfig.initialize();
        }
        catch (IOException e) {
            logger.error("Failed to initialize Iris configuration, default values will be used instead");
            logger.error("", e);
        }
        initialized = true;
    }

    static {
        storedError = Optional.empty();
        resetShaderPackOptions = false;
        loadPackWhenPossible = false;
        renderSystemInit = false;
    }
}

