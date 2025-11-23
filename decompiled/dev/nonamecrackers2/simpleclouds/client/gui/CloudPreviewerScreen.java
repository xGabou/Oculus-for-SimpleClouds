/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.layouts.FrameLayout
 *  net.minecraft.client.gui.layouts.GridLayout
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.util.Mth
 *  net.minecraftforge.client.event.RegisterClientReloadListenersEvent
 *  nonamecrackers2.crackerslib.client.gui.Popup
 *  nonamecrackers2.crackerslib.client.gui.Screen3D
 *  nonamecrackers2.crackerslib.client.gui.widget.CyclableButton
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package dev.nonamecrackers2.simpleclouds.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.weather.WeatherType;
import dev.nonamecrackers2.simpleclouds.client.cloud.ClientSideCloudTypeManager;
import dev.nonamecrackers2.simpleclouds.client.framebuffer.WeightedBlendingTarget;
import dev.nonamecrackers2.simpleclouds.client.gui.widget.LayerEditor;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.mesh.generator.SingleRegionCloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.CloudImageRenderer;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.SimpleCloudsConstants;
import dev.nonamecrackers2.simpleclouds.common.config.SimpleCloudsConfig;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractLayeredNoise;
import dev.nonamecrackers2.simpleclouds.common.noise.AbstractNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableLayeredNoise;
import dev.nonamecrackers2.simpleclouds.common.noise.ModifiableNoiseSettings;
import dev.nonamecrackers2.simpleclouds.common.noise.NoiseSettings;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import nonamecrackers2.crackerslib.client.gui.Popup;
import nonamecrackers2.crackerslib.client.gui.Screen3D;
import nonamecrackers2.crackerslib.client.gui.widget.CyclableButton;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloudPreviewerScreen
extends Screen3D {
    @Nullable
    private static SingleRegionCloudMeshGenerator generator;
    private static final Gson GSON;
    private static final Logger LOGGER;
    private static final int PADDING = 10;
    private static final Component WARNING_TOO_MANY_CUBES;
    private static final Component WEATHER_TYPE_TITLE;
    private static final Component STORMINESS_TITLE;
    private static final Component STORM_START_TITLE;
    private static final Component STORM_FADE_DISTANCE_TITLE;
    private static final Component TRANSPARENCY_FADE_TITLE;
    private static final Component LOAD;
    private static final Component EXPORT;
    private static final Component SELECT_A_CLOUD_TYPE;
    private static final Component EXPORT_CLOUD_TYPE;
    private static final Component FILE_ALREADY_EXISTS;
    private static final Component INFO;
    private Button addLayer;
    private Button removeLayer;
    @Nullable
    private Screen prev;
    private final List<ModifiableNoiseSettings> layers;
    private final List<LayerEditor> layerEditors = Lists.newArrayList();
    private int currentLayer;
    private WeatherType weatherType = WeatherType.NONE;
    private float storminess = 0.0f;
    private float stormStart = 16.0f;
    private float stormFadeDistance = 32.0f;
    private float transparencyFade = 0.0f;
    private final CloudInfo cloudType = new CloudInfo(){

        @Override
        public WeatherType weatherType() {
            return CloudPreviewerScreen.this.weatherType;
        }

        @Override
        public float storminess() {
            return CloudPreviewerScreen.this.storminess;
        }

        @Override
        public float stormStart() {
            return CloudPreviewerScreen.this.stormStart;
        }

        @Override
        public float stormFadeDistance() {
            return CloudPreviewerScreen.this.stormFadeDistance;
        }

        @Override
        public NoiseSettings noiseConfig() {
            if (CloudPreviewerScreen.this.layers.isEmpty()) {
                return NoiseSettings.EMPTY;
            }
            if (CloudPreviewerScreen.this.layers.size() > 1) {
                return new ModifiableLayeredNoise((Collection<ModifiableNoiseSettings>)CloudPreviewerScreen.this.layers);
            }
            return CloudPreviewerScreen.this.layers.get(0);
        }

        @Override
        public float transparencyFade() {
            return CloudPreviewerScreen.this.transparencyFade;
        }
    };
    private final File directory;
    private final File cloudTypeDirectory;
    private final File rendersDirectory;
    private int toolbarHeight;
    private boolean needsMeshRegen = true;
    private CyclableButton<WeatherType> weatherTypeButton;
    private EditBox storminessBox;
    private EditBox stormStartBox;
    private EditBox stormFadeDistanceBox;
    private EditBox transparencyFadeBox;
    private Button exportButton;

    public static void addCloudMeshListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((PreparableReloadListener)((ResourceManagerReloadListener)manager -> CloudPreviewerScreen.destroyMeshGenerator()));
    }

    public static void destroyMeshGenerator() {
        if (generator != null) {
            generator.close();
            generator = null;
        }
    }

    public CloudPreviewerScreen(Screen prev) {
        super((Component)Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.title"), 0.25f, 5000.0f);
        if (generator == null) {
            generator = CloudMeshGenerator.builder().testFacesFacingAway(true).createSingleRegion(SimpleCloudsConstants.EMPTY);
            generator.init(Minecraft.m_91087_().m_91098_());
        }
        this.prev = prev;
        this.layers = Lists.newArrayList();
        this.layers.add(new ModifiableNoiseSettings());
        this.directory = new File(Minecraft.m_91087_().f_91069_, "simpleclouds");
        this.cloudTypeDirectory = new File(this.directory, "cloudtypes");
        this.rendersDirectory = new File(this.directory, "renders");
    }

    private void swapToLayer(int index) {
        if (!this.layers.isEmpty()) {
            if (index >= this.layers.size()) {
                index = 0;
            } else if (index < 0) {
                index = this.layers.size() - 1;
            }
            if (this.currentLayer >= 0 && this.currentLayer < this.layerEditors.size()) {
                this.m_169411_((GuiEventListener)this.layerEditors.get(this.currentLayer));
            }
            this.currentLayer = index;
            this.m_142416_((GuiEventListener)this.layerEditors.get(this.currentLayer));
        }
    }

    private void jumpLayer(int jump) {
        this.swapToLayer(this.currentLayer + jump);
    }

    private void generateMesh() {
        generator.setCloudType(this.cloudType);
        generator.generateMesh();
        this.needsMeshRegen = false;
    }

    private EditBox valueEditor(float currentValue, EditBox box, Consumer<Float> valueSetter, float min, float max) {
        box.m_94144_(String.valueOf(currentValue));
        box.m_94151_(s -> {
            try {
                float parsed = Float.parseFloat(s);
                if (parsed < min || parsed > max) {
                    valueSetter.accept(Float.valueOf(Mth.m_14036_((float)parsed, (float)min, (float)max)));
                    box.m_94202_(ChatFormatting.RED.m_126665_().intValue());
                } else {
                    valueSetter.accept(Float.valueOf(Float.parseFloat(s)));
                    box.m_94202_(-1);
                }
            }
            catch (NumberFormatException e) {
                box.m_94202_(ChatFormatting.RED.m_126665_().intValue());
            }
            this.needsMeshRegen = true;
        });
        return box;
    }

    private void loadCloudType() {
        Popup.createOptionListPopup((Screen)this, list -> {
            for (Map.Entry<ResourceLocation, CloudType> entry : ClientSideCloudTypeManager.getInstance().getCloudTypes().entrySet()) {
                CloudType type = entry.getValue();
                if (!(type.noiseConfig() instanceof AbstractNoiseSettings) && !(type.noiseConfig() instanceof AbstractLayeredNoise)) continue;
                MutableComponent name = Component.m_237113_((String)entry.getKey().toString());
                if (!ClientSideCloudTypeManager.getInstance().getClientSideDataManager().getCloudTypes().containsKey(entry.getKey())) {
                    name.m_7220_((Component)Component.m_237113_((String)" (Server Side)").m_130940_(ChatFormatting.DARK_GRAY));
                }
                list.addObject((Component)name, (Object)type);
            }
        }, type -> {
            this.clearAllLayers();
            NoiseSettings patt10091$temp = type.noiseConfig();
            if (patt10091$temp instanceof AbstractNoiseSettings) {
                AbstractNoiseSettings settings = (AbstractNoiseSettings)patt10091$temp;
                this.addLayer(new ModifiableNoiseSettings(settings));
            } else {
                NoiseSettings patt10240$temp = type.noiseConfig();
                if (patt10240$temp instanceof AbstractLayeredNoise) {
                    AbstractLayeredNoise layeredSettings = (AbstractLayeredNoise)patt10240$temp;
                    for (AbstractNoiseSettings settings : layeredSettings.getNoiseLayers()) {
                        this.addLayer(new ModifiableNoiseSettings(settings));
                    }
                }
            }
            this.weatherType = type.weatherType();
            this.weatherTypeButton.setValue((Object)this.weatherType);
            this.storminess = type.storminess();
            this.storminessBox.m_94144_(String.valueOf(this.storminess));
            this.stormStart = type.stormStart();
            this.stormStartBox.m_94144_(String.valueOf(this.stormStart));
            this.stormFadeDistance = type.stormFadeDistance();
            this.stormFadeDistanceBox.m_94144_(String.valueOf(this.stormFadeDistance));
            this.transparencyFade = type.transparencyFade();
            this.transparencyFadeBox.m_94144_(String.valueOf(this.transparencyFade));
        }, (int)400, (int)100, (Component)SELECT_A_CLOUD_TYPE);
    }

    private void attemptToExportCloudType() {
        Popup.createTextFieldPopup((Screen)this, value -> {
            File file = new File(this.cloudTypeDirectory, value.replaceAll("[^a-zA-Z0-9\\.\\-]", "_").toLowerCase() + ".json");
            if (!file.exists()) {
                this.exportCloudType(file);
                Popup.createInfoPopup((Screen)this, (int)200, (Component)Component.m_237110_((String)"gui.simpleclouds.cloud_previewer.popup.exported.cloud_type", (Object[])new Object[]{file.getAbsolutePath()}));
            } else {
                Popup.createYesNoPopup((Screen)this, () -> {
                    this.exportCloudType(file);
                    Popup.createInfoPopup((Screen)this, (int)200, (Component)Component.m_237110_((String)"gui.simpleclouds.cloud_previewer.popup.exported.cloud_type", (Object[])new Object[]{file.getAbsolutePath()}));
                }, this::attemptToExportCloudType, (int)200, (Component)FILE_ALREADY_EXISTS);
            }
        }, (int)200, (Component)EXPORT_CLOUD_TYPE);
    }

    private void exportCloudType(File file) {
        if (!this.cloudTypeDirectory.exists()) {
            this.cloudTypeDirectory.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file);){
            GSON.toJson((JsonElement)this.cloudType.toJson(), (Appendable)writer);
        }
        catch (JsonIOException | IOException e) {
            LOGGER.error("Failed to export cloud type json file", e);
        }
        catch (JsonSyntaxException | IllegalStateException e) {
            LOGGER.error("An internal error occured while serializing cloud type", e);
        }
    }

    private void addLayer(ModifiableNoiseSettings layer) {
        this.layers.add(layer);
        Objects.requireNonNull(this.f_96547_);
        int n = Math.max(200, this.f_96543_ / 4);
        Objects.requireNonNull(this.f_96547_);
        this.layerEditors.add(new LayerEditor(layer, this.f_96541_, 10, 10 + 9, n, this.f_96544_ - 20 - this.toolbarHeight - 9, () -> {
            this.needsMeshRegen = true;
        }));
        this.swapToLayer(this.layers.indexOf(layer));
        this.addLayer.f_93623_ = this.layers.size() < 4;
        this.removeLayer.f_93623_ = true;
        this.exportButton.f_93623_ = true;
        this.needsMeshRegen = true;
    }

    private void clearAllLayers() {
        Iterator<LayerEditor> layerEditorIterator = this.layerEditors.iterator();
        while (layerEditorIterator.hasNext()) {
            this.m_169411_((GuiEventListener)layerEditorIterator.next());
            layerEditorIterator.remove();
        }
        this.layers.clear();
        this.addLayer.f_93623_ = true;
        this.removeLayer.f_93623_ = false;
        this.exportButton.f_93623_ = false;
        this.needsMeshRegen = true;
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (super.m_7933_(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 83) {
            if (!this.rendersDirectory.exists()) {
                this.rendersDirectory.mkdirs();
            }
            float oldFadeStart = generator.getFadeStart() / (float)generator.getCloudAreaMaxRadius();
            float oldFadeEnd = generator.getFadeEnd() / (float)generator.getCloudAreaMaxRadius();
            generator.setFadeDistances(0.001f, 1.0f);
            generator.generateMesh();
            try (CloudImageRenderer renderer = CloudImageRenderer.basicIsometric(this.rendersDirectory, generator);){
                renderer.setBgCol(0.0f, 1.0f, 0.0f);
                renderer.initialize();
                renderer.render();
                renderer.exportToRenderedImage(m -> this.f_96541_.execute(() -> Popup.createInfoPopup((Screen)this, (int)300, (Component)m)));
                renderer.finalize();
            }
            catch (Exception e) {
                LOGGER.warn("Failed to capture screenshot", (Throwable)e);
            }
            generator.setFadeDistances(oldFadeStart, oldFadeEnd);
            generator.generateMesh();
            return true;
        }
        return false;
    }

    protected void m_7856_() {
        int height;
        super.m_7856_();
        GridLayout layersToolbar = new GridLayout().m_267749_(5);
        GridLayout.RowHelper layersToolbarRow = layersToolbar.m_264606_(4);
        this.addLayer = (Button)layersToolbarRow.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237113_((String)"+").m_130940_(ChatFormatting.GREEN), b -> this.addLayer(new ModifiableNoiseSettings())).m_257505_(Tooltip.m_257550_((Component)Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.button.add_layer.title"))).m_252780_(20).m_253136_());
        this.addLayer.f_93623_ = this.layers.size() < 4;
        this.removeLayer = (Button)layersToolbarRow.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237113_((String)"-").m_130940_(ChatFormatting.RED), b -> {
            this.layers.remove(this.currentLayer);
            this.m_169411_((GuiEventListener)this.layerEditors.get(this.currentLayer));
            this.layerEditors.remove(this.currentLayer);
            this.swapToLayer(this.currentLayer);
            this.removeLayer.f_93623_ = !this.layers.isEmpty();
            this.exportButton.f_93623_ = !this.layers.isEmpty();
            this.addLayer.f_93623_ = true;
            this.needsMeshRegen = true;
        }).m_257505_(Tooltip.m_257550_((Component)Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.button.remove_layer.title"))).m_252780_(20).m_253136_());
        this.removeLayer.f_93623_ = !this.layers.isEmpty();
        layersToolbarRow.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237113_((String)"<"), b -> this.jumpLayer(-1)).m_257505_(Tooltip.m_257550_((Component)Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.button.previous_layer.title"))).m_252780_(20).m_253136_());
        layersToolbarRow.m_264139_((LayoutElement)Button.m_253074_((Component)Component.m_237113_((String)">"), b -> this.jumpLayer(1)).m_257505_(Tooltip.m_257550_((Component)Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.button.next_layer.title"))).m_252780_(20).m_253136_());
        layersToolbar.m_264036_();
        this.toolbarHeight = height = layersToolbar.m_93694_();
        FrameLayout.m_264460_((LayoutElement)layersToolbar, (int)10, (int)(this.f_96544_ - height - 10), (int)(this.f_96543_ / 2), (int)(height + 10), (float)0.0f, (float)0.5f);
        layersToolbar.m_264134_(x$0 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.m_142416_((GuiEventListener)x$0);
        });
        GridLayout secondaryToolbar = new GridLayout().m_267612_(5);
        GridLayout.RowHelper secondaryToolbarRow = secondaryToolbar.m_264606_(1);
        secondaryToolbarRow.m_264139_((LayoutElement)Button.m_253074_((Component)LOAD, b -> this.loadCloudType()).m_253046_(100, 20).m_253136_());
        this.exportButton = (Button)secondaryToolbarRow.m_264139_((LayoutElement)Button.m_253074_((Component)EXPORT, b -> this.attemptToExportCloudType()).m_253046_(100, 20).m_253136_());
        this.exportButton.f_93623_ = !this.layers.isEmpty();
        secondaryToolbar.m_264036_();
        FrameLayout.m_264460_((LayoutElement)secondaryToolbar, (int)10, (int)10, (int)(this.f_96543_ - 20), (int)(this.f_96544_ - 20), (float)1.0f, (float)1.0f);
        secondaryToolbar.m_264134_(x$0 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.m_142416_((GuiEventListener)x$0);
        });
        GridLayout gridLayout = new GridLayout();
        Objects.requireNonNull(this.f_96547_);
        GridLayout cloudTypeOptions = gridLayout.m_267750_(9 + 5);
        GridLayout.RowHelper cloudTypeOptionsRow = cloudTypeOptions.m_264606_(1);
        this.weatherTypeButton = (CyclableButton)cloudTypeOptionsRow.m_264139_((LayoutElement)new CyclableButton(0, 0, 100, (List)Lists.newArrayList((Object[])WeatherType.values()), (Object)this.weatherType));
        this.weatherTypeButton.setResponder(type -> {
            this.weatherType = type;
        });
        this.storminessBox = this.valueEditor(this.storminess, (EditBox)cloudTypeOptionsRow.m_264139_((LayoutElement)new EditBox(this.f_96547_, 0, 0, 100, 20, CommonComponents.f_237098_)), f -> {
            this.storminess = f.floatValue();
        }, 0.0f, 1.0f);
        this.stormStartBox = this.valueEditor(this.stormStart, (EditBox)cloudTypeOptionsRow.m_264139_((LayoutElement)new EditBox(this.f_96547_, 0, 0, 100, 20, CommonComponents.f_237098_)), f -> {
            this.stormStart = f.floatValue();
        }, 0.0f, 256.0f);
        this.stormFadeDistanceBox = this.valueEditor(this.stormFadeDistance, (EditBox)cloudTypeOptionsRow.m_264139_((LayoutElement)new EditBox(this.f_96547_, 0, 0, 100, 20, CommonComponents.f_237098_)), f -> {
            this.stormFadeDistance = f.floatValue();
        }, 0.0f, 1600.0f);
        this.transparencyFadeBox = this.valueEditor(this.transparencyFade, (EditBox)cloudTypeOptionsRow.m_264139_((LayoutElement)new EditBox(this.f_96547_, 0, 0, 100, 20, CommonComponents.f_237098_)), f -> {
            this.transparencyFade = f.floatValue();
        }, 0.0f, 32.0f);
        cloudTypeOptions.m_264036_();
        Objects.requireNonNull(this.f_96547_);
        Objects.requireNonNull(this.f_96547_);
        FrameLayout.m_264460_((LayoutElement)cloudTypeOptions, (int)10, (int)(10 + 9), (int)(this.f_96543_ - 20), (int)(this.f_96544_ - 20 - 9 * 2), (float)1.0f, (float)0.0f);
        cloudTypeOptions.m_264134_(x$0 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.m_142416_((GuiEventListener)x$0);
        });
        this.layerEditors.clear();
        for (int i = 0; i < this.layers.size(); ++i) {
            ModifiableNoiseSettings layer = this.layers.get(i);
            Objects.requireNonNull(this.f_96547_);
            int n = Math.max(200, this.f_96543_ / 4);
            Objects.requireNonNull(this.f_96547_);
            LayerEditor list = new LayerEditor(layer, this.f_96541_, 10, 10 + 9, n, this.f_96544_ - 20 - height - 9, () -> {
                this.needsMeshRegen = true;
            });
            if (i == this.currentLayer) {
                this.m_142416_((GuiEventListener)list);
            }
            this.layerEditors.add(list);
        }
    }

    public void m_86600_() {
        generator.worldTick();
    }

    public void m_88315_(GuiGraphics stack, int pMouseX, int pMouseY, float pPartialTick) {
        if (((Boolean)SimpleCloudsConfig.CLIENT.showCloudPreviewerInfoPopup.get()).booleanValue()) {
            Popup.createInfoPopup((Screen)this, (int)200, (Component)INFO);
            SimpleCloudsConfig.CLIENT.showCloudPreviewerInfoPopup.set((Object)false);
        }
        this.m_280273_(stack);
        super.m_88315_(stack, pMouseX, pMouseY, pPartialTick);
        stack.m_280430_(this.f_96547_, (Component)Component.m_237110_((String)"gui.simpleclouds.cloud_previewer.current_layer", (Object[])new Object[]{Component.m_237113_((String)(this.layers.isEmpty() ? "NONE" : String.valueOf(this.currentLayer + 1))).m_130948_(Style.f_131099_.m_131136_(Boolean.valueOf(true)))}), 10, 5, -1);
        Pair<CloudMeshGenerator.MeshGenStatus, CloudMeshGenerator.MeshGenStatus> status = generator.getMeshGenStatus();
        if (Stream.of((CloudMeshGenerator.MeshGenStatus)((Object)status.getLeft()), (CloudMeshGenerator.MeshGenStatus)((Object)status.getRight())).anyMatch(s -> s == CloudMeshGenerator.MeshGenStatus.MESH_POOL_OVERFLOW || s == CloudMeshGenerator.MeshGenStatus.CHUNK_OVERFLOW)) {
            int n = this.f_96543_ - this.f_96547_.m_92852_((FormattedText)WARNING_TOO_MANY_CUBES) - 5;
            Objects.requireNonNull(this.f_96547_);
            stack.m_280430_(this.f_96547_, WARNING_TOO_MANY_CUBES, n, this.f_96544_ - 9 - 5, -1);
        }
        int n = this.weatherTypeButton.m_252754_();
        int n2 = this.weatherTypeButton.m_252907_();
        Objects.requireNonNull(this.f_96547_);
        stack.m_280430_(this.f_96547_, WEATHER_TYPE_TITLE, n, n2 - 9 - 2, -1);
        int n3 = this.storminessBox.m_252754_();
        int n4 = this.storminessBox.m_252907_();
        Objects.requireNonNull(this.f_96547_);
        stack.m_280430_(this.f_96547_, STORMINESS_TITLE, n3, n4 - 9 - 2, -1);
        int n5 = this.stormStartBox.m_252754_();
        int n6 = this.stormStartBox.m_252907_();
        Objects.requireNonNull(this.f_96547_);
        stack.m_280430_(this.f_96547_, STORM_START_TITLE, n5, n6 - 9 - 2, -1);
        int n7 = this.stormFadeDistanceBox.m_252754_();
        int n8 = this.stormFadeDistanceBox.m_252907_();
        Objects.requireNonNull(this.f_96547_);
        stack.m_280430_(this.f_96547_, STORM_FADE_DISTANCE_TITLE, n7, n8 - 9 - 2, -1);
        int n9 = this.transparencyFadeBox.m_252754_();
        int n10 = this.transparencyFadeBox.m_252907_();
        Objects.requireNonNull(this.f_96547_);
        stack.m_280430_(this.f_96547_, TRANSPARENCY_FADE_TITLE, n9, n10 - 9 - 2, -1);
    }

    protected void render3D(PoseStack stack, MultiBufferSource buffers, int mouseX, int mouseY, float partialTick) {
        if (this.needsMeshRegen) {
            this.generateMesh();
        }
        SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getInstance();
        RenderTarget cloudTarget = renderer.getCloudTarget();
        cloudTarget.m_83954_(Minecraft.f_91002_);
        cloudTarget.m_83947_(false);
        SimpleCloudsRenderer.renderCloudsOpaque(generator, stack, RenderSystem.getProjectionMatrix(), Float.MAX_VALUE, Float.MAX_VALUE, partialTick, 1.0f, 1.0f, 1.0f, null, false);
        WeightedBlendingTarget target = renderer.getCloudTransparencyTarget();
        target.m_83954_(Minecraft.f_91002_);
        if (generator.transparencyEnabled()) {
            renderer.copyDepthFromCloudsToTransparency();
            target.m_83947_(false);
            SimpleCloudsRenderer.renderCloudsTransparency(generator, stack, RenderSystem.getProjectionMatrix(), Float.MAX_VALUE, Float.MAX_VALUE, partialTick, 1.0f, 1.0f, 1.0f, null, false);
        }
        renderer.doFinalCompositePass(stack, partialTick, RenderSystem.getProjectionMatrix());
        this.f_96541_.m_91385_().m_83947_(false);
    }

    public void m_7379_() {
        if (this.prev != null) {
            this.f_96541_.m_91152_(this.prev);
        } else {
            super.m_7379_();
        }
        CloudPreviewerScreen.destroyMeshGenerator();
    }

    static {
        GSON = new GsonBuilder().setPrettyPrinting().create();
        LOGGER = LogManager.getLogger();
        WARNING_TOO_MANY_CUBES = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.warning.too_many_cubes").m_130948_(Style.f_131099_.m_131140_(ChatFormatting.RED).m_131136_(Boolean.valueOf(true)));
        WEATHER_TYPE_TITLE = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.weather_type.title");
        STORMINESS_TITLE = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.storminess.title");
        STORM_START_TITLE = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.storm_start.title");
        STORM_FADE_DISTANCE_TITLE = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.storm_fade_distance.title");
        TRANSPARENCY_FADE_TITLE = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.transparency_fade.title");
        LOAD = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.load.title");
        EXPORT = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.export.title");
        SELECT_A_CLOUD_TYPE = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.popup.select.cloud_type");
        EXPORT_CLOUD_TYPE = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.popup.export.cloud_type");
        FILE_ALREADY_EXISTS = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.popup.export.exists");
        INFO = Component.m_237115_((String)"gui.simpleclouds.cloud_previewer.info");
    }
}

