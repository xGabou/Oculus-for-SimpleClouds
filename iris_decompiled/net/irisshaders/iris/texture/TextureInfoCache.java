/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.IntBuffer;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import org.jetbrains.annotations.Nullable;

public class TextureInfoCache {
    public static final TextureInfoCache INSTANCE = new TextureInfoCache();
    private final Int2ObjectMap<TextureInfo> cache = new Int2ObjectOpenHashMap();

    private TextureInfoCache() {
    }

    public TextureInfo getInfo(int id) {
        TextureInfo info = (TextureInfo)this.cache.get(id);
        if (info == null) {
            info = new TextureInfo(id);
            this.cache.put(id, (Object)info);
        }
        return info;
    }

    public void onTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
        if (level == 0) {
            int id = GlStateManagerAccessor.getTEXTURES()[GlStateManagerAccessor.getActiveTexture()].f_84801_;
            TextureInfo info = this.getInfo(id);
            info.internalFormat = internalformat;
            info.width = width;
            info.height = height;
        }
    }

    public void onDeleteTexture(int id) {
        this.cache.remove(id);
    }

    public static class TextureInfo {
        private final int id;
        private int internalFormat = -1;
        private int width = -1;
        private int height = -1;

        private TextureInfo(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public int getInternalFormat() {
            if (this.internalFormat == -1) {
                this.internalFormat = this.fetchLevelParameter(4099);
            }
            return this.internalFormat;
        }

        public int getWidth() {
            if (this.width == -1) {
                this.width = this.fetchLevelParameter(4096);
            }
            return this.width;
        }

        public int getHeight() {
            if (this.height == -1) {
                this.height = this.fetchLevelParameter(4097);
            }
            return this.height;
        }

        private int fetchLevelParameter(int pname) {
            int previousTextureBinding = GlStateManager._getInteger((int)32873);
            GlStateManager._bindTexture((int)this.id);
            int parameter = GlStateManager._getTexLevelParameter((int)3553, (int)0, (int)pname);
            GlStateManager._bindTexture((int)previousTextureBinding);
            return parameter;
        }
    }
}

