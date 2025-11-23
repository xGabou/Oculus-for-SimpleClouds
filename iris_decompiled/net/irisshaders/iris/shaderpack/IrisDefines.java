/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.irisshaders.iris.shaderpack;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.parsing.BiomeCategories;
import net.irisshaders.iris.uniforms.BiomeUniforms;

public class IrisDefines {
    private static final Pattern SEMVER_PATTERN = Pattern.compile("(?<major>\\d+)\\.(?<minor>\\d+)\\.*(?<bugfix>\\d*)(.*)");

    private static void define(List<StringPair> defines, String key) {
        defines.add(new StringPair(key, ""));
    }

    private static void define(List<StringPair> defines, String key, String value) {
        defines.add(new StringPair(key, value));
    }

    public static ImmutableList<StringPair> createIrisReplacements() {
        ArrayList<StringPair> s = new ArrayList<StringPair>((Collection<StringPair>)StandardMacros.createStandardEnvironmentDefines());
        BiomeUniforms.getBiomeMap().forEach((biome, id) -> IrisDefines.define(s, "BIOME_" + biome.m_135782_().m_135815_().toUpperCase(Locale.ROOT), String.valueOf(id)));
        BiomeCategories[] categories = BiomeCategories.values();
        for (int i = 0; i < categories.length; ++i) {
            IrisDefines.define(s, "CAT_" + categories[i].name().toUpperCase(Locale.ROOT), String.valueOf(i));
        }
        return ImmutableList.copyOf(s);
    }
}

