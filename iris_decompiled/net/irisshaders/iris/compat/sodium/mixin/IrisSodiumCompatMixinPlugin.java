/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.loading.LoadingModList
 *  org.objectweb.asm.tree.ClassNode
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package net.irisshaders.iris.compat.sodium.mixin;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IrisSodiumCompatMixinPlugin
implements IMixinConfigPlugin {
    public static boolean isBendyLibLoaded;
    public static boolean isRubidiumLoaded;

    public void onLoad(String mixinPackage) {
        isBendyLibLoaded = LoadingModList.get().getModFileById("bendylib") != null;
        isRubidiumLoaded = LoadingModList.get().getModFileById("rubidium") != null;
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!isRubidiumLoaded) {
            return false;
        }
        if (mixinClassName.endsWith(".copyEntity.ModelPartMixin") || mixinClassName.endsWith(".copyEntity.CuboidMixin")) {
            return !isBendyLibLoaded;
        }
        return true;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

