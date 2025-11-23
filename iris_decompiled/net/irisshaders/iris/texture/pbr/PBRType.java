/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FilenameUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.pbr;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

public enum PBRType {
    NORMAL("_n", 0x7F7FFFFF),
    SPECULAR("_s", 0);

    private static final PBRType[] VALUES;
    private final String suffix;
    private final int defaultValue;

    private PBRType(String suffix, int defaultValue) {
        this.suffix = suffix;
        this.defaultValue = defaultValue;
    }

    @Nullable
    public static String removeSuffix(String path) {
        int extensionIndex = FilenameUtils.indexOfExtension((String)path);
        String pathNoExtension = path.substring(0, extensionIndex);
        PBRType type = PBRType.fromFileLocation(pathNoExtension);
        if (type != null) {
            String suffix = type.getSuffix();
            String basePathNoExtension = pathNoExtension.substring(0, pathNoExtension.length() - suffix.length());
            return basePathNoExtension + path.substring(extensionIndex);
        }
        return null;
    }

    @Nullable
    public static PBRType fromFileLocation(String location) {
        for (PBRType type : VALUES) {
            if (!location.endsWith(type.getSuffix())) continue;
            return type;
        }
        return null;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public String appendSuffix(String path) {
        int extensionIndex = FilenameUtils.indexOfExtension((String)path);
        if (extensionIndex != -1) {
            return path.substring(0, extensionIndex) + this.suffix + path.substring(extensionIndex);
        }
        return path + this.suffix;
    }

    static {
        VALUES = PBRType.values();
    }
}

