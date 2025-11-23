/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor
 *  me.jellysquid.mods.sodium.client.render.immediate.model.EntityRenderer
 *  me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid
 *  me.jellysquid.mods.sodium.client.render.immediate.model.ModelPartData
 *  me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils
 *  net.caffeinemc.mods.sodium.api.math.MatrixHelper
 *  net.caffeinemc.mods.sodium.api.util.ColorABGR
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.ModelPart$Cube
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.copyEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import me.jellysquid.mods.sodium.client.render.immediate.model.EntityRenderer;
import me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid;
import me.jellysquid.mods.sodium.client.render.immediate.model.ModelPartData;
import me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelPart.class})
public class ModelPartMixin
implements ModelPartData {
    @Shadow
    public float f_104200_;
    @Shadow
    public float f_104201_;
    @Shadow
    public float f_104202_;
    @Shadow
    public float f_233553_;
    @Shadow
    public float f_233554_;
    @Shadow
    public float f_233555_;
    @Shadow
    public float f_104204_;
    @Shadow
    public float f_104203_;
    @Shadow
    public float f_104205_;
    @Shadow
    public boolean f_104207_;
    @Shadow
    public boolean f_233556_;
    @Mutable
    @Shadow
    @Final
    private List<ModelPart.Cube> f_104212_;
    @Mutable
    @Shadow
    @Final
    private Map<String, ModelPart> f_104213_;
    @Unique
    private ModelPart[] sodium$children;
    @Unique
    private ModelCuboid[] sodium$cuboids;

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void onInit(List<ModelPart.Cube> cuboids, Map<String, ModelPart> children, CallbackInfo ci) {
        ModelCuboid[] copies = new ModelCuboid[cuboids.size()];
        for (int i = 0; i < cuboids.size(); ++i) {
            ModelCuboidAccessor accessor = (ModelCuboidAccessor)cuboids.get(i);
            copies[i] = accessor.sodium$copy();
        }
        this.sodium$cuboids = copies;
        this.sodium$children = (ModelPart[])children.values().toArray(ModelPart[]::new);
        this.f_104212_ = Collections.unmodifiableList(this.f_104212_);
        this.f_104213_ = Collections.unmodifiableMap(this.f_104213_);
    }

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRender(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        VertexBufferWriter writer = VertexConsumerUtils.convertOrLog((VertexConsumer)vertices);
        if (writer == null) {
            return;
        }
        ci.cancel();
        EntityRenderer.render((PoseStack)matrices, (VertexBufferWriter)writer, (ModelPart)((ModelPart)this), (int)light, (int)overlay, (int)ColorABGR.pack((float)red, (float)green, (float)blue, (float)alpha));
    }

    @Overwrite
    public void m_104299_(PoseStack matrixStack) {
        if (this.f_104200_ != 0.0f || this.f_104201_ != 0.0f || this.f_104202_ != 0.0f) {
            matrixStack.m_252880_(this.f_104200_ * 0.0625f, this.f_104201_ * 0.0625f, this.f_104202_ * 0.0625f);
        }
        if (this.f_104203_ != 0.0f || this.f_104204_ != 0.0f || this.f_104205_ != 0.0f) {
            MatrixHelper.rotateZYX((PoseStack.Pose)matrixStack.m_85850_(), (float)this.f_104205_, (float)this.f_104204_, (float)this.f_104203_);
        }
        if (this.f_233553_ != 1.0f || this.f_233554_ != 1.0f || this.f_233555_ != 1.0f) {
            matrixStack.m_85841_(this.f_233553_, this.f_233554_, this.f_233555_);
        }
    }

    public ModelCuboid[] getCuboids() {
        return this.sodium$cuboids;
    }

    public boolean isVisible() {
        return this.f_104207_;
    }

    public boolean isHidden() {
        return this.f_233556_;
    }

    public ModelPart[] getChildren() {
        return this.sodium$children;
    }
}

