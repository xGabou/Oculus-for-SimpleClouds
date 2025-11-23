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
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;
import net.irisshaders.iris.shaderpack.texture.TextureStage;

public class SodiumParameters
extends Parameters {
    public final ShaderAttributeInputs inputs;
    public AlphaTest alpha;

    public SodiumParameters(Patch patch, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap, AlphaTest alpha, ShaderAttributeInputs inputs) {
        super(patch, textureMap);
        this.inputs = inputs;
        this.alpha = alpha;
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
        result = 31 * result + (this.inputs == null ? 0 : this.inputs.hashCode());
        result = 31 * result + (this.alpha == null ? 0 : this.alpha.hashCode());
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
        SodiumParameters other = (SodiumParameters)obj;
        if (this.inputs == null ? other.inputs != null : !this.inputs.equals(other.inputs)) {
            return false;
        }
        if (this.alpha == null) {
            return other.alpha == null;
        }
        return this.alpha.equals((Object)other.alpha);
    }
}

