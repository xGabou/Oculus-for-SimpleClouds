package net.Gabou.oculus_for_simpleclouds.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.CreateRegionFunction;
import dev.nonamecrackers2.simpleclouds.api.common.cloud.spawning.SpawnInfo;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import dev.nonamecrackers2.simpleclouds.common.cloud.region.CloudRegion;
import dev.nonamecrackers2.simpleclouds.common.cloud.spawning.CloudGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(value = CloudGenerator.class, remap = false)
public abstract class CloudGeneratorDimensionWhitelistMixin {

    @Shadow @Final protected CloudTypeSource cloudGetter;

    @Unique
    private static final String oculus_for_simpleclouds$defaultDimension = "minecraft:overworld";
    @Unique
    private static final Object oculus_for_simpleclouds$lock = new Object();
    @Unique
    private static final Gson oculus_for_simpleclouds$gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    @Unique
    private static final Map<String, Set<String>> oculus_for_simpleclouds$cloudDimensionWhitelist = new HashMap<>();
    @Unique
    private static final Path oculus_for_simpleclouds$whitelistPath =
            FMLPaths.CONFIGDIR.get().resolve("oculus_for_simpleclouds").resolve("cloud_dimension_whitelist.json");
    @Unique
    private static boolean oculus_for_simpleclouds$loaded;
    @Unique
    private static long oculus_for_simpleclouds$lastFileTimestamp = Long.MIN_VALUE;
    @Unique
    private static final ThreadLocal<String> oculus_for_simpleclouds$currentDimension = new ThreadLocal<>();

    @Inject(
            method = "initialize(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/Level;)V",
            at = @At("HEAD")
    )
    private void oculus_for_simpleclouds$initDimensionWhitelist(RandomSource random, Level level, CallbackInfo ci) {
        oculus_for_simpleclouds$refreshWhitelist(this.cloudGetter);
    }

    @Inject(
            method = "spawnCloud(Ljava/util/function/Supplier;IILnet/minecraft/world/level/Level;Ldev/nonamecrackers2/simpleclouds/api/common/cloud/spawning/CreateRegionFunction;)Ljava/util/Optional;",
            at = @At("HEAD")
    )
    private void oculus_for_simpleclouds$setDimensionForSpawn(
            Supplier<SpawnInfo> infoGetter,
            int nextSpawnInterval,
            int maxRegions,
            Level level,
            CreateRegionFunction regionFunc,
            CallbackInfoReturnable<Optional<CloudRegion>> cir
    ) {
        oculus_for_simpleclouds$refreshWhitelist(this.cloudGetter);
        if (level != null) {
            oculus_for_simpleclouds$currentDimension.set(level.dimension().location().toString());
        }
    }

    @Inject(
            method = "spawnCloud(Ljava/util/function/Supplier;IILnet/minecraft/world/level/Level;Ldev/nonamecrackers2/simpleclouds/api/common/cloud/spawning/CreateRegionFunction;)Ljava/util/Optional;",
            at = @At("RETURN")
    )
    private void oculus_for_simpleclouds$clearDimensionForSpawn(
            Supplier<SpawnInfo> infoGetter,
            int nextSpawnInterval,
            int maxRegions,
            Level level,
            CreateRegionFunction regionFunc,
            CallbackInfoReturnable<Optional<CloudRegion>> cir
    ) {
        oculus_for_simpleclouds$currentDimension.remove();
    }

    @Inject(
            method = "doInitialGen(IILnet/minecraft/world/level/Level;Z)V",
            at = @At("HEAD")
    )
    private void oculus_for_simpleclouds$setDimensionForInitialGen(int x, int z, Level level, boolean ignoreOtherRegions, CallbackInfo ci) {
        oculus_for_simpleclouds$refreshWhitelist(this.cloudGetter);
        if (level != null) {
            oculus_for_simpleclouds$currentDimension.set(level.dimension().location().toString());
        }
    }

    @Inject(
            method = "doInitialGen(IILnet/minecraft/world/level/Level;Z)V",
            at = @At("RETURN")
    )
    private void oculus_for_simpleclouds$clearDimensionForInitialGen(int x, int z, Level level, boolean ignoreOtherRegions, CallbackInfo ci) {
        oculus_for_simpleclouds$currentDimension.remove();
    }

    @Inject(
            method = "createRegion(Ldev/nonamecrackers2/simpleclouds/api/common/cloud/spawning/SpawnInfo;FFFFLnet/minecraft/util/RandomSource;Z)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void oculus_for_simpleclouds$filterByDimensionAtCreateRegion(
            SpawnInfo info,
            float playerX,
            float playerZ,
            float x,
            float z,
            RandomSource random,
            boolean growTime,
            CallbackInfoReturnable<Optional<CloudRegion>> cir
    ) {
        if (info == null || info.cloudType() == null) {
            cir.setReturnValue(Optional.empty());
            return;
        }

        String dimensionId = oculus_for_simpleclouds$currentDimension.get();
        if (dimensionId == null || dimensionId.isBlank()) {
            return;
        }

        if (!oculus_for_simpleclouds$isAllowedInDimension(info.cloudType().toString(), dimensionId)) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Unique
    private static boolean oculus_for_simpleclouds$isAllowedInDimension(String cloudId, String dimensionId) {
        synchronized (oculus_for_simpleclouds$lock) {
            Set<String> allowed = oculus_for_simpleclouds$cloudDimensionWhitelist.get(cloudId);
            if (allowed == null || allowed.isEmpty()) {
                return oculus_for_simpleclouds$defaultDimension.equals(dimensionId);
            }
            return allowed.contains(dimensionId);
        }
    }

    @Unique
    private static void oculus_for_simpleclouds$refreshWhitelist(CloudTypeSource source) {
        synchronized (oculus_for_simpleclouds$lock) {
            try {
                long currentTimestamp = Files.exists(oculus_for_simpleclouds$whitelistPath)
                        ? Files.getLastModifiedTime(oculus_for_simpleclouds$whitelistPath).toMillis()
                        : Long.MIN_VALUE;

                if (!oculus_for_simpleclouds$loaded || currentTimestamp != oculus_for_simpleclouds$lastFileTimestamp) {
                    oculus_for_simpleclouds$loadWhitelistFromDisk();
                    oculus_for_simpleclouds$loaded = true;
                    oculus_for_simpleclouds$lastFileTimestamp = Files.exists(oculus_for_simpleclouds$whitelistPath)
                            ? Files.getLastModifiedTime(oculus_for_simpleclouds$whitelistPath).toMillis()
                            : Long.MIN_VALUE;
                }

                if (oculus_for_simpleclouds$ensureAllKnownTypesExist(source)) {
                    oculus_for_simpleclouds$writeWhitelistToDisk();
                    oculus_for_simpleclouds$lastFileTimestamp = Files.getLastModifiedTime(oculus_for_simpleclouds$whitelistPath).toMillis();
                }
            } catch (IOException ignored) {
                // Fail open for IO errors.
            }
        }
    }

    @Unique
    private static void oculus_for_simpleclouds$loadWhitelistFromDisk() throws IOException {
        oculus_for_simpleclouds$cloudDimensionWhitelist.clear();

        if (!Files.exists(oculus_for_simpleclouds$whitelistPath)) {
            Files.createDirectories(oculus_for_simpleclouds$whitelistPath.getParent());
            return;
        }

        String raw = Files.readString(oculus_for_simpleclouds$whitelistPath, StandardCharsets.UTF_8);
        JsonElement parsed = JsonParser.parseString(raw);
        if (!parsed.isJsonObject()) {
            return;
        }

        JsonObject root = parsed.getAsJsonObject();
        JsonObject cloudsObj = root.has("clouds") && root.get("clouds").isJsonObject()
                ? root.getAsJsonObject("clouds")
                : new JsonObject();

        for (Map.Entry<String, JsonElement> entry : cloudsObj.entrySet()) {
            Set<String> dimensions = new HashSet<>();
            JsonElement value = entry.getValue();
            if (value != null && value.isJsonArray()) {
                JsonArray array = value.getAsJsonArray();
                for (JsonElement element : array) {
                    if (!element.isJsonPrimitive()) {
                        continue;
                    }
                    String dimId = element.getAsString().trim();
                    if (ResourceLocation.tryParse(dimId) != null) {
                        dimensions.add(dimId);
                    }
                }
            }
            if (dimensions.isEmpty()) {
                dimensions.add(oculus_for_simpleclouds$defaultDimension);
            }
            oculus_for_simpleclouds$cloudDimensionWhitelist.put(entry.getKey(), dimensions);
        }
    }

    @Unique
    private static boolean oculus_for_simpleclouds$ensureAllKnownTypesExist(CloudTypeSource source) {
        CloudType[] knownTypes = source.getIndexedCloudTypes();
        if (knownTypes == null || knownTypes.length == 0) {
            return false;
        }

        boolean changed = false;
        for (CloudType type : knownTypes) {
            if (type == null || type.id() == null) {
                continue;
            }
            String id = type.id().toString();
            Set<String> dims = oculus_for_simpleclouds$cloudDimensionWhitelist.get(id);
            if (dims == null || dims.isEmpty()) {
                Set<String> defaultSet = new HashSet<>();
                defaultSet.add(oculus_for_simpleclouds$defaultDimension);
                oculus_for_simpleclouds$cloudDimensionWhitelist.put(id, defaultSet);
                changed = true;
            }
        }
        return changed;
    }

    @Unique
    private static void oculus_for_simpleclouds$writeWhitelistToDisk() throws IOException {
        Files.createDirectories(oculus_for_simpleclouds$whitelistPath.getParent());

        JsonObject root = new JsonObject();
        root.addProperty("_comment", "Cloud type -> allowed dimensions. Defaults to minecraft:overworld.");
        JsonObject clouds = new JsonObject();

        List<String> sortedCloudIds = new ArrayList<>(oculus_for_simpleclouds$cloudDimensionWhitelist.keySet());
        sortedCloudIds.sort(String::compareTo);
        for (String cloudId : sortedCloudIds) {
            Set<String> dims = oculus_for_simpleclouds$cloudDimensionWhitelist.get(cloudId);
            List<String> sortedDims = new ArrayList<>(dims == null ? Set.of() : dims);
            sortedDims.sort(String::compareTo);
            JsonArray array = new JsonArray();
            for (String dim : sortedDims) {
                if (ResourceLocation.tryParse(dim) != null) {
                    array.add(dim);
                }
            }
            if (array.size() == 0) {
                array.add(oculus_for_simpleclouds$defaultDimension);
            }
            clouds.add(cloudId, array);
        }

        root.add("clouds", clouds);
        Files.writeString(oculus_for_simpleclouds$whitelistPath, oculus_for_simpleclouds$gson.toJson(root), StandardCharsets.UTF_8);
    }
}
