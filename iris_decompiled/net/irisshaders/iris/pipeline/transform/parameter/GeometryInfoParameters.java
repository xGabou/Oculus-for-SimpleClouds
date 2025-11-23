/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 */
package net.irisshaders.iris.pipeline.transform.parameter;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.pipeline.transform.Patch;
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;
import net.irisshaders.iris.shaderpack.texture.TextureStage;

public abstract class GeometryInfoParameters
extends Parameters {
    public final boolean hasGeometry;
    public final boolean hasTesselation;

    public GeometryInfoParameters(Patch patch, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap, boolean hasGeometry, boolean hasTesselation) {
        super(patch, textureMap);
        this.hasGeometry = hasGeometry;
        this.hasTesselation = hasTesselation;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.hasGeometry ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        GeometryInfoParameters other = (GeometryInfoParameters)obj;
        return this.hasGeometry == other.hasGeometry;
    }
}

