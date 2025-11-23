/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 */
package net.irisshaders.iris.pipeline.transform.parameter;

import io.github.douira.glsl_transformer.ast.transform.JobParameters;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.pipeline.transform.Patch;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.shaderpack.texture.TextureStage;

public abstract class Parameters
implements JobParameters {
    public final Patch patch;
    private final Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap;
    public PatchShaderType type;

    public Parameters(Patch patch, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        this.patch = patch;
        this.textureMap = textureMap;
    }

    public AlphaTest getAlphaTest() {
        return AlphaTest.ALWAYS;
    }

    public abstract TextureStage getTextureStage();

    public Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> getTextureMap() {
        return this.textureMap;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.patch == null ? 0 : this.patch.hashCode());
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        result = 31 * result + (this.textureMap == null ? 0 : this.textureMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Parameters other = (Parameters)obj;
        if (this.patch != other.patch) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.textureMap == null) {
            return other.textureMap == null;
        }
        return this.textureMap.equals(other.textureMap);
    }
}

