/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.nonamecrackers2.simpleclouds.client.renderer.rain;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PrecipitationQuad {
    public static final float MAX_LENGTH = 32.0f;
    public static final float MAX_WIDTH = 2.0f;
    public static final Map<Biome.Precipitation, ResourceLocation> TEXTURE_BY_PRECIPITATION = (Map)Util.m_137537_(() -> {
        ImmutableMap.Builder map = ImmutableMap.builder();
        map.put((Object)Biome.Precipitation.RAIN, (Object)new ResourceLocation("textures/environment/rain.png"));
        map.put((Object)Biome.Precipitation.SNOW, (Object)new ResourceLocation("textures/environment/snow.png"));
        return map.build();
    });
    private final Biome.Precipitation precipitation;
    private final BlockPos blockPos;
    private final Vector3f position;
    private final int lifeSpan;
    private final float initialWidth;
    private final Function<ClipContext, BlockHitResult> raycaster;
    private float length = 32.0f;
    private float xRot;
    private float yRot;
    private int tickCount;
    private float widthO;
    private float width;

    public PrecipitationQuad(Biome.Precipitation precipitation, Function<ClipContext, BlockHitResult> raycaster, BlockPos position, float xRot, float yRot, int lifeSpan, float initialWidth) {
        if (precipitation == Biome.Precipitation.NONE) {
            throw new IllegalArgumentException("Cannot be NONE precipitation type");
        }
        this.precipitation = precipitation;
        this.raycaster = raycaster;
        this.blockPos = position;
        this.position = new Vector3f((float)position.m_123341_() + 0.5f, (float)position.m_123342_() + 0.5f, (float)position.m_123343_() + 0.5f);
        this.xRot = xRot;
        this.yRot = yRot;
        this.lifeSpan = lifeSpan;
        this.initialWidth = Math.max(0.1f, initialWidth);
    }

    public Biome.Precipitation getPrecipitation() {
        return this.precipitation;
    }

    public Vector3f getPos() {
        return this.position;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getLength() {
        return this.length;
    }

    public float getXRot() {
        return this.xRot;
    }

    public void setXRot(float rot) {
        this.xRot = rot;
    }

    public float getYRot() {
        return this.yRot;
    }

    public void setYRot(float rot) {
        this.yRot = rot;
    }

    public int getTickCount() {
        return this.tickCount;
    }

    public boolean isDead() {
        return this.tickCount > this.lifeSpan;
    }

    public void tick() {
        ++this.tickCount;
        Vec3 start = new Vec3(this.position);
        float yawRadians = -this.yRot;
        float pitchRadians = this.xRot - 1.5707964f;
        float pitchCos = Mth.m_14089_((float)pitchRadians);
        Vec3 end = new Vec3((double)(Mth.m_14031_((float)yawRadians) * pitchCos), (double)Mth.m_14031_((float)pitchRadians), (double)(Mth.m_14089_((float)yawRadians) * pitchCos)).m_82490_(32.0).m_82549_(start);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null);
        BlockHitResult result = this.raycaster.apply(context);
        Vec3 hit = result.m_82450_();
        this.length = (float)start.m_82554_(hit);
        this.widthO = this.width;
        this.width = this.tickCount < this.lifeSpan - 20 ? this.initialWidth * Math.min(1.0f, (float)this.tickCount / 20.0f) : this.initialWidth * Math.min(1.0f, ((float)this.lifeSpan - (float)this.tickCount) / 20.0f);
    }

    public void render(PoseStack stack, VertexConsumer consumer, float partialTick, int packedLight, double camX, double camY, double camZ) {
        stack.m_252880_(this.position.x, this.position.y, this.position.z);
        Quaternionf inverseRotation = new Quaternionf();
        inverseRotation.rotateX(this.xRot);
        inverseRotation.rotateY(this.yRot);
        Vector3f adjustedCamPos = new Vector3f((float)camX, (float)camY, (float)camZ).sub((Vector3fc)this.position).rotate((Quaternionfc)inverseRotation).add((Vector3fc)this.position);
        float angleToCam = (float)Mth.m_14136_((double)(this.position.x - adjustedCamPos.x), (double)(this.position.z - adjustedCamPos.z));
        stack.m_252781_(inverseRotation.invert());
        stack.m_252781_(Axis.f_252436_.m_252961_(angleToCam));
        Matrix4f mat = stack.m_85850_().m_252922_();
        float vOffset = ((float)this.tickCount + partialTick) * (this.precipitation == Biome.Precipitation.RAIN ? -0.1f : -0.01f);
        float width = Mth.m_14179_((float)partialTick, (float)this.widthO, (float)this.width);
        float u1 = width / 2.0f * 0.5f + 0.5f;
        float u0 = 0.5f - width / 2.0f * 0.5f;
        consumer.m_252986_(mat, width / 2.0f, 0.0f, 0.0f).m_7421_(u0, vOffset).m_85950_(1.0f, 1.0f, 1.0f, 1.0f).m_85969_(packedLight).m_5752_();
        consumer.m_252986_(mat, -width / 2.0f, 0.0f, 0.0f).m_7421_(u1, vOffset).m_85950_(1.0f, 1.0f, 1.0f, 1.0f).m_85969_(packedLight).m_5752_();
        consumer.m_252986_(mat, -width / 2.0f, -this.length, 0.0f).m_7421_(u1, this.length / 10.0f + vOffset).m_85950_(1.0f, 1.0f, 1.0f, 1.0f).m_85969_(packedLight).m_5752_();
        consumer.m_252986_(mat, width / 2.0f, -this.length, 0.0f).m_7421_(u0, this.length / 10.0f + vOffset).m_85950_(1.0f, 1.0f, 1.0f, 1.0f).m_85969_(packedLight).m_5752_();
    }
}

