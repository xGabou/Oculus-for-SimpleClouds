/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor
 *  me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid
 *  net.minecraft.client.model.geom.ModelPart$Cube
 *  net.minecraft.core.Direction
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.compat.sodium.mixin.copyEntity;

import java.util.Set;
import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ModelPart.Cube.class})
public class CuboidMixin
implements ModelCuboidAccessor {
    @Unique
    private ModelCuboid sodium$cuboid;
    @Mutable
    @Shadow
    @Final
    private float f_104335_;

    @Redirect(method={"<init>"}, at=@At(value="FIELD", opcode=181, target="Lnet/minecraft/client/model/geom/ModelPart$Cube;minX:F", ordinal=0))
    private void onInit(ModelPart.Cube instance, float value, int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> renderDirections) {
        this.sodium$cuboid = new ModelCuboid(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, textureWidth, textureHeight, renderDirections);
        this.f_104335_ = value;
    }

    public ModelCuboid sodium$copy() {
        return this.sodium$cuboid;
    }
}

