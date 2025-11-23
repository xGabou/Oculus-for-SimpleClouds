/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 */
package net.irisshaders.iris.helpers;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class JomlConversions {
    public static Vector3d fromVec3(Vec3 vec) {
        return new Vector3d(vec.m_7096_(), vec.m_7098_(), vec.m_7094_());
    }
}

