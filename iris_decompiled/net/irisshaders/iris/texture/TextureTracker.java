/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jetbrains.annotations.Nullable;

public class TextureTracker {
    public static final TextureTracker INSTANCE = new TextureTracker();
    private static Runnable bindTextureListener;
    private final Int2ObjectMap<AbstractTexture> textures = new Int2ObjectOpenHashMap();
    private boolean lockBindCallback;

    private TextureTracker() {
    }

    public void trackTexture(int id, AbstractTexture texture) {
        this.textures.put(id, (Object)texture);
    }

    @Nullable
    public AbstractTexture getTexture(int id) {
        return (AbstractTexture)this.textures.get(id);
    }

    public void onSetShaderTexture(int unit, int id) {
        if (this.lockBindCallback) {
            return;
        }
        if (unit == 0) {
            WorldRenderingPipeline pipeline;
            this.lockBindCallback = true;
            if (bindTextureListener != null) {
                bindTextureListener.run();
            }
            if ((pipeline = Iris.getPipelineManager().getPipelineNullable()) != null) {
                pipeline.onSetShaderTexture(id);
            }
            IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 0, id);
            this.lockBindCallback = false;
        }
    }

    public void onDeleteTexture(int id) {
        this.textures.remove(id);
    }

    static {
        StateUpdateNotifiers.bindTextureNotifier = listener -> {
            bindTextureListener = listener;
        };
    }
}

