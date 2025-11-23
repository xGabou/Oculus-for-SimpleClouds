/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.Function
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 */
package net.irisshaders.iris.shaderpack.materialmap;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;

public class LegacyIdMap {
    private static final ImmutableList<String> COLORS = ImmutableList.of((Object)"white", (Object)"orange", (Object)"magenta", (Object)"light_blue", (Object)"yellow", (Object)"lime", (Object)"pink", (Object)"gray", (Object)"light_gray", (Object)"cyan", (Object)"purple", (Object)"blue", (Object[])new String[]{"brown", "green", "red", "black"});
    private static final ImmutableList<String> WOOD_TYPES = ImmutableList.of((Object)"oak", (Object)"birch", (Object)"jungle", (Object)"spruce", (Object)"acacia", (Object)"dark_oak");

    public static void addLegacyValues(Int2ObjectMap<List<BlockEntry>> blockIdMap) {
        LegacyIdMap.add(blockIdMap, 1, LegacyIdMap.block("stone"), LegacyIdMap.block("granite"), LegacyIdMap.block("diorite"), LegacyIdMap.block("andesite"));
        LegacyIdMap.add(blockIdMap, 2, LegacyIdMap.block("grass_block"));
        LegacyIdMap.add(blockIdMap, 4, LegacyIdMap.block("cobblestone"));
        LegacyIdMap.add(blockIdMap, 50, LegacyIdMap.block("torch"));
        LegacyIdMap.add(blockIdMap, 89, LegacyIdMap.block("glowstone"));
        LegacyIdMap.add(blockIdMap, 124, LegacyIdMap.block("redstone_lamp"));
        LegacyIdMap.add(blockIdMap, 12, LegacyIdMap.block("sand"));
        LegacyIdMap.add(blockIdMap, 24, LegacyIdMap.block("sandstone"));
        LegacyIdMap.add(blockIdMap, 41, LegacyIdMap.block("gold_block"));
        LegacyIdMap.add(blockIdMap, 42, LegacyIdMap.block("iron_block"));
        LegacyIdMap.add(blockIdMap, 57, LegacyIdMap.block("diamond_block"));
        LegacyIdMap.add(blockIdMap, -123, LegacyIdMap.block("emerald_block"));
        LegacyIdMap.addMany(blockIdMap, 35, COLORS, (Function<String, BlockEntry>)((Function)color -> LegacyIdMap.block(color + "_wool")));
        LegacyIdMap.add(blockIdMap, 9, LegacyIdMap.block("water"));
        LegacyIdMap.add(blockIdMap, 11, LegacyIdMap.block("lava"));
        LegacyIdMap.add(blockIdMap, 79, LegacyIdMap.block("ice"));
        LegacyIdMap.addMany(blockIdMap, 18, WOOD_TYPES, (Function<String, BlockEntry>)((Function)woodType -> LegacyIdMap.block(woodType + "_leaves")));
        LegacyIdMap.addMany(blockIdMap, 95, COLORS, (Function<String, BlockEntry>)((Function)color -> LegacyIdMap.block(color + "_stained_glass")));
        LegacyIdMap.addMany(blockIdMap, 160, COLORS, (Function<String, BlockEntry>)((Function)color -> LegacyIdMap.block(color + "_stained_glass_pane")));
        LegacyIdMap.add(blockIdMap, 31, LegacyIdMap.block("grass"), LegacyIdMap.block("seagrass"), LegacyIdMap.block("sweet_berry_bush"));
        LegacyIdMap.add(blockIdMap, 59, LegacyIdMap.block("wheat"), LegacyIdMap.block("carrots"), LegacyIdMap.block("potatoes"));
        LegacyIdMap.add(blockIdMap, 37, LegacyIdMap.block("dandelion"), LegacyIdMap.block("poppy"), LegacyIdMap.block("blue_orchid"), LegacyIdMap.block("allium"), LegacyIdMap.block("azure_bluet"), LegacyIdMap.block("red_tulip"), LegacyIdMap.block("pink_tulip"), LegacyIdMap.block("white_tulip"), LegacyIdMap.block("orange_tulip"), LegacyIdMap.block("oxeye_daisy"), LegacyIdMap.block("cornflower"), LegacyIdMap.block("lily_of_the_valley"), LegacyIdMap.block("wither_rose"));
        LegacyIdMap.add(blockIdMap, 175, LegacyIdMap.block("sunflower"), LegacyIdMap.block("lilac"), LegacyIdMap.block("tall_grass"), LegacyIdMap.block("large_fern"), LegacyIdMap.block("rose_bush"), LegacyIdMap.block("peony"), LegacyIdMap.block("tall_seagrass"));
        LegacyIdMap.add(blockIdMap, 51, LegacyIdMap.block("fire"));
        LegacyIdMap.add(blockIdMap, 111, LegacyIdMap.block("lily_pad"));
    }

    private static BlockEntry block(String name) {
        return new BlockEntry(new NamespacedId("minecraft", name), Collections.emptyMap());
    }

    private static void addMany(Int2ObjectMap<List<BlockEntry>> blockIdMap, int id, List<String> prefixes, Function<String, BlockEntry> toId) {
        ArrayList<BlockEntry> entries = new ArrayList<BlockEntry>();
        for (String prefix : prefixes) {
            entries.add((BlockEntry)((Object)toId.apply((Object)prefix)));
        }
        blockIdMap.put(id, entries);
    }

    private static void add(Int2ObjectMap<List<BlockEntry>> blockIdMap, int id, BlockEntry ... entries) {
        blockIdMap.put(id, Arrays.asList(entries));
    }
}

