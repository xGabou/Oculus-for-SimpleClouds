/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;

public class IrisConfig {
    private static final String COMMENT = "This file stores configuration options for Iris, such as the currently active shaderpack";
    private final Path propertiesPath;
    private String shaderPackName = null;
    private boolean enableShaders = true;
    private boolean enableDebugOptions = false;
    private boolean disableUpdateMessage = false;

    public IrisConfig(Path propertiesPath) {
        this.propertiesPath = propertiesPath;
    }

    public void initialize() throws IOException {
        this.load();
        if (!Files.exists(this.propertiesPath, new LinkOption[0])) {
            this.save();
        }
    }

    public boolean isInternal() {
        return false;
    }

    public Optional<String> getShaderPackName() {
        return Optional.ofNullable(this.shaderPackName);
    }

    public void setShaderPackName(String name) {
        this.shaderPackName = name == null || name.equals("(internal)") || name.isEmpty() ? null : name;
    }

    public boolean areShadersEnabled() {
        return this.enableShaders;
    }

    public boolean areDebugOptionsEnabled() {
        return this.enableDebugOptions;
    }

    public boolean shouldDisableUpdateMessage() {
        return this.disableUpdateMessage;
    }

    public void setDebugEnabled(boolean enabled) {
        this.enableDebugOptions = enabled;
    }

    public void setShadersEnabled(boolean enabled) {
        this.enableShaders = enabled;
    }

    public void load() throws IOException {
        if (!Files.exists(this.propertiesPath, new LinkOption[0])) {
            return;
        }
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(this.propertiesPath, new OpenOption[0]);){
            properties.load(is);
        }
        this.shaderPackName = properties.getProperty("shaderPack");
        this.enableShaders = !"false".equals(properties.getProperty("enableShaders"));
        this.enableDebugOptions = "true".equals(properties.getProperty("enableDebugOptions"));
        this.disableUpdateMessage = "true".equals(properties.getProperty("disableUpdateMessage"));
        try {
            IrisVideoSettings.shadowDistance = Integer.parseInt(properties.getProperty("maxShadowRenderDistance", "32"));
            IrisVideoSettings.colorSpace = ColorSpace.valueOf(properties.getProperty("colorSpace", "SRGB"));
        }
        catch (IllegalArgumentException e) {
            Iris.logger.error("Shadow distance setting reset; value is invalid.");
            IrisVideoSettings.shadowDistance = 32;
            IrisVideoSettings.colorSpace = ColorSpace.SRGB;
            this.save();
        }
        if (this.shaderPackName != null && (this.shaderPackName.equals("(internal)") || this.shaderPackName.isEmpty())) {
            this.shaderPackName = null;
        }
    }

    public void save() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("shaderPack", this.getShaderPackName().orElse(""));
        properties.setProperty("enableShaders", this.enableShaders ? "true" : "false");
        properties.setProperty("enableDebugOptions", this.enableDebugOptions ? "true" : "false");
        properties.setProperty("disableUpdateMessage", this.disableUpdateMessage ? "true" : "false");
        properties.setProperty("maxShadowRenderDistance", String.valueOf(IrisVideoSettings.shadowDistance));
        properties.setProperty("colorSpace", IrisVideoSettings.colorSpace.name());
        try (OutputStream os = Files.newOutputStream(this.propertiesPath, new OpenOption[0]);){
            properties.store(os, COMMENT);
        }
    }
}

