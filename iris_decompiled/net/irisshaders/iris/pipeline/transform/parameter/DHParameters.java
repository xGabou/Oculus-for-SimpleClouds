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

public class DHParameters
extends Parameters {
    public DHParameters(Patch patch, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        super(patch, textureMap);
    }

    @Override
    public TextureStage getTextureStage() {
        return TextureStage.GBUFFERS_AND_SHADOW;
    }
}

