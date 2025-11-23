/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion
 *  dev.nonamecrackers2.simpleclouds.api.common.event.CloudRegionTickEvent
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec2
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 *  nonamecrackers2.crackerslib.common.util.primitives.PrimitiveHelper
 *  org.apache.commons.lang3.tuple.Pair
 *  org.joml.Matrix2f
 *  org.joml.Matrix2fc
 *  org.joml.Vector2f
 */
package dev.nonamecrackers2.simpleclouds.common.cloud.region;

import dev.nonamecrackers2.simpleclouds.api.common.cloud.region.ScAPICloudRegion;
import dev.nonamecrackers2.simpleclouds.api.common.event.CloudRegionTickEvent;
import dev.nonamecrackers2.simpleclouds.common.world.SpawnRegion;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import nonamecrackers2.crackerslib.common.util.primitives.PrimitiveHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix2f;
import org.joml.Matrix2fc;
import org.joml.Vector2f;

public class CloudRegion
implements ScAPICloudRegion {
    private final ResourceLocation cloudTypeId;
    private final float initialRadius;
    private final int orderWeight;
    private Vec2 movementDirection;
    private float maxSpeed;
    private float accelerationFactor;
    private float velX;
    private float velZ;
    private float posX;
    private float posXO;
    private float posZ;
    private float posZO;
    private float radius;
    private float radiusO;
    private float stretchFactor;
    private float stretchFactorO;
    private float rotation;
    private float rotationO;
    private int tickCount;
    private int existsForTicks;
    private int growTicks;
    private boolean priorVisible;

    public CloudRegion(ResourceLocation cloudTypeId, Vec2 movementDirection, float maxSpeed, float accelerationFactor, float posX, float posZ, float radius, float rotation, float stretchFactor, int existsForTicks, int growTicks, int orderWeight) {
        this.cloudTypeId = cloudTypeId;
        this.movementDirection = movementDirection;
        this.maxSpeed = maxSpeed;
        this.accelerationFactor = accelerationFactor;
        this.posX = posX;
        this.posZ = posZ;
        this.initialRadius = radius;
        this.radius = 0.0f;
        this.rotation = rotation;
        this.stretchFactor = Math.max(0.01f, stretchFactor);
        this.existsForTicks = Math.max(0, existsForTicks);
        this.growTicks = Mth.m_14045_((int)growTicks, (int)0, (int)existsForTicks);
        this.orderWeight = orderWeight;
    }

    public CloudRegion(FriendlyByteBuf buffer) {
        this.cloudTypeId = buffer.m_130281_();
        this.initialRadius = buffer.readFloat();
        this.movementDirection = new Vec2(buffer.readFloat(), buffer.readFloat());
        this.maxSpeed = buffer.readFloat();
        this.accelerationFactor = buffer.readFloat();
        this.velX = buffer.readFloat();
        this.velZ = buffer.readFloat();
        this.posXO = this.posX = buffer.readFloat();
        this.posZO = this.posZ = buffer.readFloat();
        this.radiusO = this.radius = buffer.readFloat();
        this.stretchFactorO = this.stretchFactor = buffer.readFloat();
        this.rotationO = this.rotation = buffer.readFloat();
        this.tickCount = buffer.m_130242_();
        this.existsForTicks = buffer.m_130242_();
        this.growTicks = buffer.m_130242_();
        this.orderWeight = buffer.m_130242_();
    }

    public CloudRegion(CompoundTag tag) throws IllegalArgumentException {
        this.cloudTypeId = (ResourceLocation)ResourceLocation.m_135837_((String)tag.m_128461_("id")).resultOrPartial(e -> {
            throw new IllegalArgumentException((String)e);
        }).get();
        this.initialRadius = tag.m_128457_("initial_radius");
        this.movementDirection = PrimitiveHelper.vec2FromTag((CompoundTag)tag.m_128469_("movement_direction"));
        this.maxSpeed = tag.m_128457_("max_speed");
        this.accelerationFactor = tag.m_128457_("acceleration_factor");
        this.orderWeight = tag.m_128451_("order_weight");
        CompoundTag vel = tag.m_128469_("velocity");
        this.velX = vel.m_128457_("x");
        this.velZ = vel.m_128457_("z");
        CompoundTag pos = tag.m_128469_("pos");
        this.posXO = this.posX = pos.m_128457_("x");
        this.posZO = this.posZ = pos.m_128457_("z");
        this.radiusO = this.radius = tag.m_128457_("radius");
        this.stretchFactorO = this.stretchFactor = tag.m_128457_("stretch_factor");
        this.rotationO = this.rotation = tag.m_128457_("rotation");
        this.tickCount = tag.m_128451_("tick_count");
        this.existsForTicks = tag.m_128451_("exists_for_ticks");
        this.growTicks = tag.m_128451_("grow_ticks");
    }

    public void toPacket(FriendlyByteBuf buffer) {
        buffer.m_130085_(this.cloudTypeId);
        buffer.writeFloat(this.initialRadius);
        buffer.writeFloat(this.movementDirection.f_82470_);
        buffer.writeFloat(this.movementDirection.f_82471_);
        buffer.writeFloat(this.maxSpeed);
        buffer.writeFloat(this.accelerationFactor);
        buffer.writeFloat(this.velX);
        buffer.writeFloat(this.velZ);
        buffer.writeFloat(this.posX);
        buffer.writeFloat(this.posZ);
        buffer.writeFloat(this.radius);
        buffer.writeFloat(this.stretchFactor);
        buffer.writeFloat(this.rotation);
        buffer.m_130130_(this.tickCount);
        buffer.m_130130_(this.existsForTicks);
        buffer.m_130130_(this.growTicks);
        buffer.m_130130_(this.orderWeight);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.m_128359_("id", this.cloudTypeId.toString());
        tag.m_128350_("initial_radius", this.initialRadius);
        tag.m_128365_("movement_direction", (Tag)PrimitiveHelper.vec2ToTag((Vec2)this.movementDirection));
        tag.m_128350_("max_speed", this.maxSpeed);
        tag.m_128350_("acceleration_factor", this.accelerationFactor);
        tag.m_128405_("order_weight", this.orderWeight);
        CompoundTag vel = new CompoundTag();
        vel.m_128350_("x", this.velX);
        vel.m_128350_("z", this.velZ);
        tag.m_128365_("velocity", (Tag)vel);
        CompoundTag pos = new CompoundTag();
        pos.m_128350_("x", this.posX);
        pos.m_128350_("z", this.posZ);
        tag.m_128365_("pos", (Tag)pos);
        tag.m_128350_("radius", this.radius);
        tag.m_128350_("stretch_factor", this.stretchFactor);
        tag.m_128350_("rotation", this.rotation);
        tag.m_128405_("tick_count", this.tickCount);
        tag.m_128405_("exists_for_ticks", this.existsForTicks);
        tag.m_128405_("grow_ticks", this.growTicks);
        return tag;
    }

    public void tick(RandomSource random, @Nullable Level level, boolean isVisible, float speed) {
        Vec2 movementDirection = this.movementDirection;
        float maxSpeed = this.maxSpeed * speed;
        float accelerationFactor = this.accelerationFactor * speed;
        if (level != null) {
            CloudRegionTickEvent event = new CloudRegionTickEvent(level, (ScAPICloudRegion)this);
            MinecraftForge.EVENT_BUS.post((Event)event);
            if (event.getModifiedMovementDirection() != null) {
                movementDirection = event.getModifiedMovementDirection();
            }
            if (event.getModifiedMaxSpeed() >= 0.0f) {
                maxSpeed = event.getModifiedMaxSpeed();
            }
            if (event.getModifiedAccelerationFactor() >= 0.0f) {
                accelerationFactor = event.getModifiedAccelerationFactor();
            }
        }
        this.radiusO = this.radius;
        this.stretchFactorO = this.stretchFactor;
        this.rotationO = this.rotation;
        float scale = this.tickCount < this.growTicks ? (float)this.tickCount / (float)this.growTicks : 1.0f - (float)(this.tickCount - this.growTicks) / (float)(this.existsForTicks - this.growTicks);
        this.radius = this.initialRadius * scale;
        this.tickCount += Math.max(1, Mth.m_14167_((float)((isVisible ? 1.0f : 20.0f) * speed)));
        this.posXO = this.posX;
        this.posZO = this.posZ;
        if (isVisible) {
            float targetVelX = Math.abs(movementDirection.f_82470_ * maxSpeed);
            float targetVelZ = Math.abs(movementDirection.f_82471_ * maxSpeed);
            this.velX = Mth.m_14036_((float)(this.velX + movementDirection.f_82470_ * accelerationFactor), (float)(-targetVelX), (float)targetVelX);
            this.velZ = Mth.m_14036_((float)(this.velZ + movementDirection.f_82471_ * accelerationFactor), (float)(-targetVelZ), (float)targetVelZ);
            this.posX += this.velX;
            this.posZ += this.velZ;
        }
        this.priorVisible = isVisible;
    }

    public ResourceLocation getCloudTypeId() {
        return this.cloudTypeId;
    }

    public int getOrderWeight() {
        return this.orderWeight;
    }

    public boolean intersects(SpawnRegion region) {
        return region.intersectsCircle(this.getWorldX(), this.getWorldZ(), this.getWorldRadius() / this.getStretch() + 1600.0f);
    }

    public boolean isDead() {
        return this.tickCount > this.existsForTicks;
    }

    public Vec2 getMovementDirection() {
        return this.movementDirection;
    }

    public void setMovementDirection(Vec2 direction) {
        this.movementDirection = direction;
    }

    public float getMaxSpeed() {
        return this.maxSpeed;
    }

    public void setMaxSpeed(float speed) {
        this.maxSpeed = speed;
    }

    public float getAccelerationFactor() {
        return this.accelerationFactor;
    }

    public void setAccelerationFactor(float factor) {
        this.accelerationFactor = factor;
    }

    public float getPosX(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.posXO, (float)this.posX);
    }

    public float getPosX() {
        return this.posX;
    }

    public float getWorldX() {
        return this.posX * 8.0f;
    }

    public float getPosZ(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.posZO, (float)this.posZ);
    }

    public float getPosZ() {
        return this.posZ;
    }

    public float getWorldZ() {
        return this.posZ * 8.0f;
    }

    public void moveTo(float x, float z) {
        this.posX = x;
        this.posXO = x;
        this.posZ = z;
        this.posZO = z;
    }

    public void moveToWorldPos(float x, float z) {
        this.moveTo(x / 8.0f, z / 8.0f);
    }

    public float getInitialRadius() {
        return this.initialRadius;
    }

    public float getInitialWorldRadius() {
        return this.initialRadius * 8.0f;
    }

    public float getRadius(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.radiusO, (float)this.radius);
    }

    public float getRadius() {
        return this.radius;
    }

    public float getWorldRadius() {
        return this.radius * 8.0f;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        this.radiusO = radius;
    }

    public void setWorldRadius(float radius) {
        this.setRadius(radius / 8.0f);
    }

    public float getStretch(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.stretchFactorO, (float)this.stretchFactor);
    }

    public float getStretch() {
        return this.stretchFactor;
    }

    public void setStretchFactor(float factor) {
        this.stretchFactor = factor;
        this.stretchFactorO = factor;
    }

    public float getRotation(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.rotationO, (float)this.rotation);
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        this.rotationO = rotation;
    }

    public boolean wasPriorVisible() {
        return this.priorVisible;
    }

    public int getExistForTicks() {
        return this.existsForTicks;
    }

    public int getGrowTicks() {
        return this.growTicks;
    }

    public Matrix2f createTransform(float partialTick) {
        Matrix2f transform = new Matrix2f().identity();
        transform.scale(this.getStretch(partialTick), 1.0f);
        transform.rotate(this.getRotation(partialTick));
        return transform;
    }

    private static CompositeResult circle(CloudRegion region, float x, float z) {
        float eff;
        Matrix2f transform = region.createTransform(1.0f);
        Vector2f pos = new Vector2f(x, z).sub(region.posX, region.posZ).mul((Matrix2fc)transform).add(region.posX, region.posZ);
        float d = pos.distance(region.posX, region.posZ);
        if (d > region.radius + 1.0f / (eff = 0.005f)) {
            return new CompositeResult(-1.0f, -1.0f, null);
        }
        if (d < region.radius) {
            return new CompositeResult(Math.min((region.radius - d) * eff, 1.0f), 0.0f, region);
        }
        return new CompositeResult(0.0f, Math.min((d - region.radius) * eff, 1.0f), region);
    }

    private static void composite(Result old, CompositeResult toComposite) {
        if (toComposite.innerFactor > 0.0f) {
            if (old.region != null && old.region.cloudTypeId.equals((Object)toComposite.regionAt.cloudTypeId)) {
                old.fade = Mth.m_14179_((float)toComposite.innerFactor, (float)old.fade, (float)1.0f);
            } else {
                old.region = toComposite.regionAt;
                old.fade = toComposite.innerFactor;
            }
        } else if (toComposite.outerFactor >= 0.0f && (old.region == null || !old.region.cloudTypeId.equals((Object)toComposite.regionAt.cloudTypeId))) {
            old.fade *= toComposite.outerFactor;
        }
    }

    public static Pair<CloudRegion, Float> calculateAt(List<CloudRegion> regions, float x, float z) {
        Result result = new Result();
        for (CloudRegion region : regions) {
            CloudRegion.composite(result, CloudRegion.circle(region, x, z));
        }
        return Pair.of((Object)result.region, (Object)Float.valueOf(result.fade));
    }

    private record CompositeResult(float innerFactor, float outerFactor, CloudRegion regionAt) {
    }

    private static class Result {
        @Nullable
        private CloudRegion region;
        private float fade;

        private Result() {
        }
    }
}

