/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.texture.mipmap.CustomMipmapGenerator;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jetbrains.annotations.Nullable;

public interface TextureFormat {
    public String name();

    @Nullable
    public String version();

    default public List<String> getDefines() {
        ArrayList<String> defines = new ArrayList<String>();
        String defineName = this.name().toUpperCase(Locale.ROOT).replaceAll("-", "_");
        String define = "MC_TEXTURE_FORMAT_" + defineName;
        defines.add(define);
        String version = this.version();
        if (version != null) {
            String defineVersion = version.replaceAll("[.-]", "_");
            String versionDefine = define + "_" + defineVersion;
            defines.add(versionDefine);
        }
        return defines;
    }

    public boolean canInterpolateValues(PBRType var1);

    default public void setupTextureParameters(PBRType pbrType, AbstractTexture texture) {
        if (!this.canInterpolateValues(pbrType)) {
            int minFilter = IrisRenderSystem.getTexParameteri(texture.m_117963_(), 3553, 10241);
            boolean mipmap = (minFilter & 0x100) == 1;
            IrisRenderSystem.texParameteri(texture.m_117963_(), 3553, 10241, mipmap ? 9984 : 9728);
            IrisRenderSystem.texParameteri(texture.m_117963_(), 3553, 10240, 9728);
        }
    }

    @Nullable
    public CustomMipmapGenerator getMipmapGenerator(PBRType var1);

    public static interface Factory {
        public TextureFormat createFormat(String var1, @Nullable String var2);
    }
}

