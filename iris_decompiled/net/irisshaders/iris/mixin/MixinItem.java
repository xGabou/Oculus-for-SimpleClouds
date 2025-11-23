/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  org.spongepowered.asm.mixin.Mixin
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.api.v0.item.IrisItemLightProvider;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={Item.class})
public class MixinItem
implements IrisItemLightProvider {
}

