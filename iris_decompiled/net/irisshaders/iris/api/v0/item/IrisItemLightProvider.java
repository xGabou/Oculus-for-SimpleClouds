/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.joml.Vector3f
 */
package net.irisshaders.iris.api.v0.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public interface IrisItemLightProvider {
    public static final Vector3f DEFAULT_LIGHT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);

    default public int getLightEmission(Player player, ItemStack stack) {
        Item item = stack.m_41720_();
        if (item instanceof BlockItem) {
            BlockItem item2 = (BlockItem)item;
            return item2.m_40614_().m_49966_().m_60791_();
        }
        return 0;
    }

    default public Vector3f getLightColor(Player player, ItemStack stack) {
        return DEFAULT_LIGHT_COLOR;
    }
}

