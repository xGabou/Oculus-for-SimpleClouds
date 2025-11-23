/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.Dumpable
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris.texture.pbr;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.targets.backed.NativeImageBackedSingleColorTexture;
import net.irisshaders.iris.texture.TextureTracker;
import net.irisshaders.iris.texture.pbr.PBRDumpable;
import net.irisshaders.iris.texture.pbr.PBRTextureHolder;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.irisshaders.iris.texture.pbr.loader.PBRTextureLoader;
import net.irisshaders.iris.texture.pbr.loader.PBRTextureLoaderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PBRTextureManager {
    public static final PBRTextureManager INSTANCE = new PBRTextureManager();
    private static Runnable normalTextureChangeListener;
    private static Runnable specularTextureChangeListener;
    private final Int2ObjectMap<PBRTextureHolder> holders = new Int2ObjectOpenHashMap();
    private final PBRTextureConsumerImpl consumer = new PBRTextureConsumerImpl();
    private NativeImageBackedSingleColorTexture defaultNormalTexture;
    private NativeImageBackedSingleColorTexture defaultSpecularTexture;
    private final PBRTextureHolder defaultHolder = new PBRTextureHolder(){

        @Override
        @NotNull
        public AbstractTexture normalTexture() {
            return PBRTextureManager.this.defaultNormalTexture;
        }

        @Override
        @NotNull
        public AbstractTexture specularTexture() {
            return PBRTextureManager.this.defaultSpecularTexture;
        }
    };

    private PBRTextureManager() {
    }

    private static void dumpTexture(Dumpable dumpable, ResourceLocation id, Path path) {
        try {
            dumpable.m_276079_(id, path);
        }
        catch (IOException e) {
            Iris.logger.error("Failed to dump texture {}", id, e);
        }
    }

    private static void closeTexture(AbstractTexture texture) {
        try {
            texture.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        texture.m_117964_();
    }

    public static void notifyPBRTexturesChanged() {
        if (normalTextureChangeListener != null) {
            normalTextureChangeListener.run();
        }
        if (specularTextureChangeListener != null) {
            specularTextureChangeListener.run();
        }
    }

    public void init() {
        this.defaultNormalTexture = new NativeImageBackedSingleColorTexture(PBRType.NORMAL.getDefaultValue());
        this.defaultSpecularTexture = new NativeImageBackedSingleColorTexture(PBRType.SPECULAR.getDefaultValue());
    }

    public PBRTextureHolder getHolder(int id) {
        PBRTextureHolder holder = (PBRTextureHolder)this.holders.get(id);
        if (holder == null) {
            return this.defaultHolder;
        }
        return holder;
    }

    public PBRTextureHolder getOrLoadHolder(int id) {
        PBRTextureHolder holder = (PBRTextureHolder)this.holders.get(id);
        if (holder == null) {
            holder = this.loadHolder(id);
            this.holders.put(id, (Object)holder);
        }
        return holder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PBRTextureHolder loadHolder(int id) {
        Class<?> clazz;
        PBRTextureLoader<?> loader;
        AbstractTexture texture = TextureTracker.INSTANCE.getTexture(id);
        if (texture != null && (loader = PBRTextureLoaderRegistry.INSTANCE.getLoader(clazz = texture.getClass())) != null) {
            int previousTextureBinding = GlStateManagerAccessor.getTEXTURES()[GlStateManagerAccessor.getActiveTexture()].f_84801_;
            this.consumer.clear();
            try {
                loader.load(texture, Minecraft.m_91087_().m_91098_(), this.consumer);
                PBRTextureHolder pBRTextureHolder = this.consumer.toHolder();
                return pBRTextureHolder;
            }
            catch (Exception e) {
                Iris.logger.debug("Failed to load PBR textures for texture " + id, e);
            }
            finally {
                GlStateManager._bindTexture((int)previousTextureBinding);
            }
        }
        return this.defaultHolder;
    }

    public void onDeleteTexture(int id) {
        PBRTextureHolder holder = (PBRTextureHolder)this.holders.remove(id);
        if (holder != null) {
            this.closeHolder(holder);
        }
    }

    public void dumpTextures(Path path) {
        for (PBRTextureHolder holder : this.holders.values()) {
            if (holder == this.defaultHolder) continue;
            this.dumpHolder(holder, path);
        }
    }

    private void dumpHolder(PBRTextureHolder holder, Path path) {
        PBRDumpable dumpable;
        AbstractTexture normalTexture = holder.normalTexture();
        AbstractTexture specularTexture = holder.specularTexture();
        if (normalTexture != this.defaultNormalTexture && normalTexture instanceof PBRDumpable) {
            dumpable = (PBRDumpable)normalTexture;
            PBRTextureManager.dumpTexture(dumpable, dumpable.getDefaultDumpLocation(), path);
        }
        if (specularTexture != this.defaultSpecularTexture && specularTexture instanceof PBRDumpable) {
            dumpable = (PBRDumpable)specularTexture;
            PBRTextureManager.dumpTexture(dumpable, dumpable.getDefaultDumpLocation(), path);
        }
    }

    public void clear() {
        for (PBRTextureHolder holder : this.holders.values()) {
            if (holder == this.defaultHolder) continue;
            this.closeHolder(holder);
        }
        this.holders.clear();
    }

    public void close() {
        this.clear();
        this.defaultNormalTexture.close();
        this.defaultSpecularTexture.close();
    }

    private void closeHolder(PBRTextureHolder holder) {
        AbstractTexture normalTexture = holder.normalTexture();
        AbstractTexture specularTexture = holder.specularTexture();
        if (normalTexture != this.defaultNormalTexture) {
            PBRTextureManager.closeTexture(normalTexture);
        }
        if (specularTexture != this.defaultSpecularTexture) {
            PBRTextureManager.closeTexture(specularTexture);
        }
    }

    static {
        StateUpdateNotifiers.normalTextureChangeNotifier = listener -> {
            normalTextureChangeListener = listener;
        };
        StateUpdateNotifiers.specularTextureChangeNotifier = listener -> {
            specularTextureChangeListener = listener;
        };
    }

    private class PBRTextureConsumerImpl
    implements PBRTextureLoader.PBRTextureConsumer {
        private AbstractTexture normalTexture;
        private AbstractTexture specularTexture;
        private boolean changed;

        private PBRTextureConsumerImpl() {
        }

        @Override
        public void acceptNormalTexture(@NotNull AbstractTexture texture) {
            this.normalTexture = texture;
            this.changed = true;
        }

        @Override
        public void acceptSpecularTexture(@NotNull AbstractTexture texture) {
            this.specularTexture = texture;
            this.changed = true;
        }

        public void clear() {
            this.normalTexture = PBRTextureManager.this.defaultNormalTexture;
            this.specularTexture = PBRTextureManager.this.defaultSpecularTexture;
            this.changed = false;
        }

        public PBRTextureHolder toHolder() {
            if (this.changed) {
                return new PBRTextureHolderImpl(this.normalTexture, this.specularTexture);
            }
            return PBRTextureManager.this.defaultHolder;
        }
    }

    private record PBRTextureHolderImpl(AbstractTexture normalTexture, AbstractTexture specularTexture) implements PBRTextureHolder
    {
    }
}

