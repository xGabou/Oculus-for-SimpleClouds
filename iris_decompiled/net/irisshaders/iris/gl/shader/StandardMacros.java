/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.GlUtil
 *  net.minecraft.Util
 *  net.minecraft.Util$OS
 *  net.minecraftforge.fml.loading.LoadingModList
 *  org.lwjgl.opengl.GL30C
 */
package net.irisshaders.iris.gl.shader;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.texture.format.TextureFormat;
import net.irisshaders.iris.texture.format.TextureFormatLoader;
import net.minecraft.Util;
import net.minecraftforge.fml.loading.LoadingModList;
import org.lwjgl.opengl.GL30C;

public class StandardMacros {
    private static final Pattern SEMVER_PATTERN = Pattern.compile("(?<major>\\d+)\\.(?<minor>\\d+)\\.*(?<bugfix>\\d*)(.*)");

    private static void define(List<StringPair> defines, String key) {
        defines.add(new StringPair(key, ""));
    }

    private static void define(List<StringPair> defines, String key, String value) {
        defines.add(new StringPair(key, value));
    }

    public static ImmutableList<StringPair> createStandardEnvironmentDefines() {
        ArrayList<StringPair> standardDefines = new ArrayList<StringPair>();
        StandardMacros.define(standardDefines, "MC_VERSION", StandardMacros.getMcVersion());
        StandardMacros.define(standardDefines, "MC_GL_VERSION", StandardMacros.getGlVersion(7938));
        StandardMacros.define(standardDefines, "MC_GLSL_VERSION", StandardMacros.getGlVersion(35724));
        StandardMacros.define(standardDefines, StandardMacros.getOsString());
        StandardMacros.define(standardDefines, StandardMacros.getVendor());
        StandardMacros.define(standardDefines, StandardMacros.getRenderer());
        StandardMacros.define(standardDefines, "IS_IRIS");
        if (LoadingModList.get().getModFileById("distanthorizons") != null && DHCompat.hasRenderingEnabled()) {
            StandardMacros.define(standardDefines, "DISTANT_HORIZONS");
        }
        StandardMacros.define(standardDefines, "DH_BLOCK_UNKNOWN", String.valueOf(0));
        StandardMacros.define(standardDefines, "DH_BLOCK_LEAVES", String.valueOf(1));
        StandardMacros.define(standardDefines, "DH_BLOCK_STONE", String.valueOf(2));
        StandardMacros.define(standardDefines, "DH_BLOCK_WOOD", String.valueOf(3));
        StandardMacros.define(standardDefines, "DH_BLOCK_METAL", String.valueOf(4));
        StandardMacros.define(standardDefines, "DH_BLOCK_DIRT", String.valueOf(5));
        StandardMacros.define(standardDefines, "DH_BLOCK_LAVA", String.valueOf(6));
        StandardMacros.define(standardDefines, "DH_BLOCK_DEEPSLATE", String.valueOf(7));
        StandardMacros.define(standardDefines, "DH_BLOCK_SNOW", String.valueOf(8));
        StandardMacros.define(standardDefines, "DH_BLOCK_SAND", String.valueOf(9));
        StandardMacros.define(standardDefines, "DH_BLOCK_TERRACOTTA", String.valueOf(10));
        StandardMacros.define(standardDefines, "DH_BLOCK_NETHER_STONE", String.valueOf(11));
        StandardMacros.define(standardDefines, "DH_BLOCK_WATER", String.valueOf(12));
        StandardMacros.define(standardDefines, "DH_BLOCK_GRASS", String.valueOf(13));
        StandardMacros.define(standardDefines, "DH_BLOCK_AIR", String.valueOf(14));
        StandardMacros.define(standardDefines, "DH_BLOCK_ILLUMINATED", String.valueOf(15));
        for (String glExtension : StandardMacros.getGlExtensions()) {
            StandardMacros.define(standardDefines, glExtension);
        }
        StandardMacros.define(standardDefines, "MC_NORMAL_MAP");
        StandardMacros.define(standardDefines, "MC_SPECULAR_MAP");
        StandardMacros.define(standardDefines, "MC_RENDER_QUALITY", "1.0");
        StandardMacros.define(standardDefines, "MC_SHADOW_QUALITY", "1.0");
        StandardMacros.define(standardDefines, "MC_HAND_DEPTH", Float.toString(0.125f));
        TextureFormat textureFormat = TextureFormatLoader.getFormat();
        if (textureFormat != null) {
            for (String define : textureFormat.getDefines()) {
                StandardMacros.define(standardDefines, define);
            }
        }
        StandardMacros.getRenderStages().forEach((stage, index) -> StandardMacros.define(standardDefines, stage, index));
        for (String irisDefine : StandardMacros.getIrisDefines()) {
            StandardMacros.define(standardDefines, irisDefine);
        }
        return ImmutableList.copyOf(standardDefines);
    }

    public static String getMcVersion() {
        String version = Iris.getReleaseTarget();
        if (version == null) {
            throw new IllegalStateException("Could not get the current minecraft version!");
        }
        String[] splitVersion = version.split("\\.");
        if (splitVersion.length < 2) {
            Iris.logger.error("Could not parse game version \"" + version + "\"");
            splitVersion = Iris.getBackupVersionNumber().split("\\.");
        }
        String major = splitVersion[0];
        Object minor = splitVersion[1];
        Object bugfix = splitVersion.length < 3 ? "00" : splitVersion[2];
        if (((String)minor).length() == 1) {
            minor = "0" + (String)minor;
        }
        if (((String)bugfix).length() == 1) {
            bugfix = "0" + (String)bugfix;
        }
        return major + (String)minor + (String)bugfix;
    }

    public static String getGlVersion(int name) {
        String info = GlStateManager._getString((int)name);
        Matcher matcher = SEMVER_PATTERN.matcher(Objects.requireNonNull(info));
        if (!matcher.matches()) {
            throw new IllegalStateException("Could not parse GL version from \"" + info + "\"");
        }
        String major = StandardMacros.group(matcher, "major");
        String minor = StandardMacros.group(matcher, "minor");
        String bugfix = StandardMacros.group(matcher, "bugfix");
        if (bugfix == null) {
            bugfix = "0";
        }
        if (major == null || minor == null) {
            throw new IllegalStateException("Could not parse GL version from \"" + info + "\"");
        }
        return major + minor + bugfix;
    }

    public static String group(Matcher matcher, String name) {
        try {
            return matcher.group(name);
        }
        catch (IllegalArgumentException | IllegalStateException exception) {
            return null;
        }
    }

    public static String getOsString() {
        return switch (Util.m_137581_()) {
            case Util.OS.OSX -> "MC_OS_MAC";
            case Util.OS.LINUX -> "MC_OS_LINUX";
            case Util.OS.WINDOWS -> "MC_OS_WINDOWS";
            default -> "MC_OS_UNKNOWN";
        };
    }

    public static String getVendor() {
        String vendor = Objects.requireNonNull(GlUtil.m_84818_()).toLowerCase(Locale.ROOT);
        if (vendor.startsWith("ati")) {
            return "MC_GL_VENDOR_ATI";
        }
        if (vendor.startsWith("intel")) {
            return "MC_GL_VENDOR_INTEL";
        }
        if (vendor.startsWith("nvidia")) {
            return "MC_GL_VENDOR_NVIDIA";
        }
        if (vendor.startsWith("amd")) {
            return "MC_GL_VENDOR_AMD";
        }
        if (vendor.startsWith("x.org")) {
            return "MC_GL_VENDOR_XORG";
        }
        return "MC_GL_VENDOR_OTHER";
    }

    public static String getRenderer() {
        String renderer = Objects.requireNonNull(GlUtil.m_84820_()).toLowerCase(Locale.ROOT);
        if (renderer.startsWith("amd")) {
            return "MC_GL_RENDERER_RADEON";
        }
        if (renderer.startsWith("ati")) {
            return "MC_GL_RENDERER_RADEON";
        }
        if (renderer.startsWith("radeon")) {
            return "MC_GL_RENDERER_RADEON";
        }
        if (renderer.startsWith("gallium")) {
            return "MC_GL_RENDERER_GALLIUM";
        }
        if (renderer.startsWith("intel")) {
            return "MC_GL_RENDERER_INTEL";
        }
        if (renderer.startsWith("geforce")) {
            return "MC_GL_RENDERER_GEFORCE";
        }
        if (renderer.startsWith("nvidia")) {
            return "MC_GL_RENDERER_GEFORCE";
        }
        if (renderer.startsWith("quadro")) {
            return "MC_GL_RENDERER_QUADRO";
        }
        if (renderer.startsWith("nvs")) {
            return "MC_GL_RENDERER_QUADRO";
        }
        if (renderer.startsWith("mesa")) {
            return "MC_GL_RENDERER_MESA";
        }
        return "MC_GL_RENDERER_OTHER";
    }

    public static Set<String> getGlExtensions() {
        int numExtensions = GL30C.glGetInteger((int)33309);
        String[] extensions = new String[numExtensions];
        for (int i = 0; i < numExtensions; ++i) {
            extensions[i] = GL30C.glGetStringi((int)7939, (int)i);
        }
        return Arrays.stream(extensions).map(s -> "MC_" + s).collect(Collectors.toSet());
    }

    public static Map<String, String> getRenderStages() {
        HashMap<String, String> stages = new HashMap<String, String>();
        for (WorldRenderingPhase phase : WorldRenderingPhase.values()) {
            stages.put("MC_RENDER_STAGE_" + phase.name(), String.valueOf(phase.ordinal()));
        }
        return stages;
    }

    public static List<String> getIrisDefines() {
        ArrayList<String> defines = new ArrayList<String>();
        return defines;
    }
}

