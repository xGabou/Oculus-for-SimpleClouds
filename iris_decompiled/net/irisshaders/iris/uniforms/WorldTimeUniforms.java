/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 */
package net.irisshaders.iris.uniforms;

import java.util.Objects;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.DimensionId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class WorldTimeUniforms {
    private WorldTimeUniforms() {
    }

    public static void addWorldTimeUniforms(UniformHolder uniforms) {
        uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "worldTime", WorldTimeUniforms::getWorldDayTime).uniform1i(UniformUpdateFrequency.PER_TICK, "worldDay", WorldTimeUniforms::getWorldDay).uniform1i(UniformUpdateFrequency.PER_TICK, "moonPhase", () -> WorldTimeUniforms.getWorld().m_46941_());
    }

    static int getWorldDayTime() {
        long timeOfDay = WorldTimeUniforms.getWorld().m_46468_();
        if (Iris.getCurrentDimension() == DimensionId.END || Iris.getCurrentDimension() == DimensionId.NETHER) {
            return (int)(timeOfDay % 24000L);
        }
        long dayTime = WorldTimeUniforms.getWorld().m_6042_().f_63854_().orElse(timeOfDay % 24000L);
        return (int)dayTime;
    }

    private static int getWorldDay() {
        long timeOfDay = WorldTimeUniforms.getWorld().m_46468_();
        long day = timeOfDay / 24000L;
        return (int)day;
    }

    private static ClientLevel getWorld() {
        return Objects.requireNonNull(Minecraft.m_91087_().f_91073_);
    }
}

