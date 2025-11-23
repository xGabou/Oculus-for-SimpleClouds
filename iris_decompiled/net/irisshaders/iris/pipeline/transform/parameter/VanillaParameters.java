/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 */
package net.irisshaders.iris.pipeline.transform.parameter;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.state.ShaderAttributeInputs;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.pipeline.transform.Patch;
import net.irisshaders.iris.pipeline.transform.parameter.GeometryInfoParameters;
import net.irisshaders.iris.shaderpack.texture.TextureStage;

public class VanillaParameters
extends GeometryInfoParameters {
    public final AlphaTest alpha;
    public final ShaderAttributeInputs inputs;
    public final boolean hasChunkOffset;
    private final boolean isLines;

    public VanillaParameters(Patch patch, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap, AlphaTest alpha, boolean isLines, boolean hasChunkOffset, ShaderAttributeInputs inputs, boolean hasGeometry, boolean hasTesselation) {
        super(patch, textureMap, hasGeometry, hasTesselation);
        this.alpha = alpha;
        this.isLines = isLines;
        this.hasChunkOffset = hasChunkOffset;
        this.inputs = inputs;
    }

    public boolean isLines() {
        return this.isLines;
    }

    @Override
    public AlphaTest getAlphaTest() {
        return this.alpha;
    }

    @Override
    public TextureStage getTextureStage() {
        return TextureStage.GBUFFERS_AND_SHADOW;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.alpha == null ? 0 : this.alpha.hashCode());
        result = 31 * result + (this.inputs == null ? 0 : this.inputs.hashCode());
        result = 31 * result + (this.hasChunkOffset ? 1231 : 1237);
        result = 31 * result + (this.isLines ? 1231 : 1237);
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
        VanillaParameters other = (VanillaParameters)obj;
        if (this.alpha == null ? other.alpha != null : !this.alpha.equals((Object)other.alpha)) {
            return false;
        }
        if (this.inputs == null ? other.inputs != null : !this.inputs.equals(other.inputs)) {
            return false;
        }
        if (this.hasChunkOffset != other.hasChunkOffset) {
            return false;
        }
        return this.isLines == other.isLines;
    }
}

