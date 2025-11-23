/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LightningBolt
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.uniforms;

import java.util.Objects;
import java.util.stream.StreamSupport;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.irisshaders.iris.helpers.JomlConversions;
import net.irisshaders.iris.uniforms.CameraUniforms;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;

public class IrisExclusiveUniforms {
    private static final Vector3d ZERO = new Vector3d(0.0);

    public static void addIrisExclusiveUniforms(UniformHolder uniforms) {
        WorldInfoUniforms.addWorldInfoUniforms(uniforms);
        uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "currentColorSpace", () -> IrisVideoSettings.colorSpace.ordinal());
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "thunderStrength", IrisExclusiveUniforms::getThunderStrength);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerHealth", IrisExclusiveUniforms::getCurrentHealth);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerHealth", IrisExclusiveUniforms::getMaxHealth);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerHunger", IrisExclusiveUniforms::getCurrentHunger);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerHunger", () -> 20);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerArmor", IrisExclusiveUniforms::getCurrentArmor);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerArmor", () -> 50);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerAir", IrisExclusiveUniforms::getCurrentAir);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerAir", IrisExclusiveUniforms::getMaxAir);
        uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "firstPersonCamera", IrisExclusiveUniforms::isFirstPersonCamera);
        uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "isSpectator", IrisExclusiveUniforms::isSpectator);
        uniforms.uniform3d(UniformUpdateFrequency.PER_FRAME, "eyePosition", IrisExclusiveUniforms::getEyePosition);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "cloudTime", CapturedRenderingState.INSTANCE::getCloudTime);
        uniforms.uniform3d(UniformUpdateFrequency.PER_FRAME, "relativeEyePosition", () -> CameraUniforms.getUnshiftedCameraPosition().sub((Vector3dc)IrisExclusiveUniforms.getEyePosition()));
        uniforms.uniform3d(UniformUpdateFrequency.PER_FRAME, "playerLookVector", () -> {
            Entity patt2841$temp = Minecraft.m_91087_().f_91075_;
            if (patt2841$temp instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)patt2841$temp;
                return JomlConversions.fromVec3(livingEntity.m_20252_(CapturedRenderingState.INSTANCE.getTickDelta()));
            }
            return ZERO;
        });
        uniforms.uniform3d(UniformUpdateFrequency.PER_FRAME, "playerBodyVector", () -> JomlConversions.fromVec3(Minecraft.m_91087_().m_91288_().m_20156_()));
        Vector4f zero = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        uniforms.uniform4f(UniformUpdateFrequency.PER_TICK, "lightningBoltPosition", () -> {
            if (Minecraft.m_91087_().f_91073_ != null) {
                return StreamSupport.stream(Minecraft.m_91087_().f_91073_.m_104735_().spliterator(), false).filter(bolt -> bolt instanceof LightningBolt).findAny().map(bolt -> {
                    Vector3d unshiftedCameraPosition = CameraUniforms.getUnshiftedCameraPosition();
                    Vec3 vec3 = bolt.m_20318_(Minecraft.m_91087_().m_91297_());
                    return new Vector4f((float)(vec3.f_82479_ - unshiftedCameraPosition.x), (float)(vec3.f_82480_ - unshiftedCameraPosition.y), (float)(vec3.f_82481_ - unshiftedCameraPosition.z), 1.0f);
                }).orElse(zero);
            }
            return zero;
        });
    }

    private static float getThunderStrength() {
        return Math.clamp((float)0.0f, (float)1.0f, (float)Minecraft.m_91087_().f_91073_.m_46661_(CapturedRenderingState.INSTANCE.getTickDelta()));
    }

    private static float getCurrentHealth() {
        if (Minecraft.m_91087_().f_91074_ == null || !Minecraft.m_91087_().f_91072_.m_105295_().m_46409_()) {
            return -1.0f;
        }
        return Minecraft.m_91087_().f_91074_.m_21223_() / Minecraft.m_91087_().f_91074_.m_21233_();
    }

    private static float getCurrentHunger() {
        if (Minecraft.m_91087_().f_91074_ == null || !Minecraft.m_91087_().f_91072_.m_105295_().m_46409_()) {
            return -1.0f;
        }
        return (float)Minecraft.m_91087_().f_91074_.m_36324_().m_38702_() / 20.0f;
    }

    private static float getCurrentAir() {
        if (Minecraft.m_91087_().f_91074_ == null || !Minecraft.m_91087_().f_91072_.m_105295_().m_46409_()) {
            return -1.0f;
        }
        return (float)Minecraft.m_91087_().f_91074_.m_20146_() / (float)Minecraft.m_91087_().f_91074_.m_6062_();
    }

    private static float getCurrentArmor() {
        if (Minecraft.m_91087_().f_91074_ == null || !Minecraft.m_91087_().f_91072_.m_105295_().m_46409_()) {
            return -1.0f;
        }
        return (float)Minecraft.m_91087_().f_91074_.m_21230_() / 50.0f;
    }

    private static float getMaxAir() {
        if (Minecraft.m_91087_().f_91074_ == null || !Minecraft.m_91087_().f_91072_.m_105295_().m_46409_()) {
            return -1.0f;
        }
        return Minecraft.m_91087_().f_91074_.m_6062_();
    }

    private static float getMaxHealth() {
        if (Minecraft.m_91087_().f_91074_ == null || !Minecraft.m_91087_().f_91072_.m_105295_().m_46409_()) {
            return -1.0f;
        }
        return Minecraft.m_91087_().f_91074_.m_21233_();
    }

    private static boolean isFirstPersonCamera() {
        return switch (Minecraft.m_91087_().f_91066_.m_92176_()) {
            case CameraType.THIRD_PERSON_BACK, CameraType.THIRD_PERSON_FRONT -> false;
            default -> true;
        };
    }

    private static boolean isSpectator() {
        return Minecraft.m_91087_().f_91072_.m_105295_() == GameType.SPECTATOR;
    }

    private static Vector3d getEyePosition() {
        Objects.requireNonNull(Minecraft.m_91087_().m_91288_());
        Vec3 pos = Minecraft.m_91087_().m_91288_().m_20299_(CapturedRenderingState.INSTANCE.getTickDelta());
        return new Vector3d(pos.f_82479_, pos.f_82480_, pos.f_82481_);
    }

    public static class WorldInfoUniforms {
        public static void addWorldInfoUniforms(UniformHolder uniforms) {
            ClientLevel level = Minecraft.m_91087_().f_91073_;
            uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "bedrockLevel", () -> {
                if (level != null) {
                    return level.m_6042_().f_156647_();
                }
                return 0;
            });
            uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "cloudHeight", () -> {
                if (level != null) {
                    return level.m_104583_().m_108871_();
                }
                return 192.0;
            });
            uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "heightLimit", () -> {
                if (level != null) {
                    return level.m_6042_().f_156648_();
                }
                return 256;
            });
            uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "logicalHeightLimit", () -> {
                if (level != null) {
                    return level.m_6042_().f_63865_();
                }
                return 256;
            });
            uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "hasCeiling", () -> {
                if (level != null) {
                    return level.m_6042_().f_63856_();
                }
                return false;
            });
            uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "hasSkylight", () -> {
                if (level != null) {
                    return level.m_6042_().f_223549_();
                }
                return true;
            });
            uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "ambientLight", () -> {
                if (level != null) {
                    return level.m_6042_().f_63838_();
                }
                return 0.0f;
            });
        }
    }
}

