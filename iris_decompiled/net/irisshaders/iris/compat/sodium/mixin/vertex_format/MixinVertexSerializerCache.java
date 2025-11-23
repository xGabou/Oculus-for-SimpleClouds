/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  it.unimi.dsi.fastutil.longs.Long2ReferenceMap
 *  me.jellysquid.mods.sodium.client.render.vertex.serializers.VertexSerializerRegistryImpl
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription
 *  net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.vertex_format;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import me.jellysquid.mods.sodium.client.render.vertex.serializers.VertexSerializerRegistryImpl;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.EntityToTerrainVertexSerializer;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.GlyphExtVertexSerializer;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.IrisEntityToTerrainVertexSerializer;
import net.irisshaders.iris.compat.sodium.impl.vertex_format.ModelToEntityVertexSerializer;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VertexSerializerRegistryImpl.class}, remap=false)
public abstract class MixinVertexSerializerCache {
    @Shadow
    @Final
    private Long2ReferenceMap<VertexSerializer> cache;

    @Shadow
    protected static long createKey(VertexFormatDescription a, VertexFormatDescription b) {
        return 0L;
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void putSerializerIris(CallbackInfo ci) {
        this.cache.put(MixinVertexSerializerCache.createKey(VertexFormatRegistry.instance().get(DefaultVertexFormat.f_85812_), VertexFormatRegistry.instance().get(IrisVertexFormats.ENTITY)), (Object)new ModelToEntityVertexSerializer());
        this.cache.put(MixinVertexSerializerCache.createKey(VertexFormatRegistry.instance().get(IrisVertexFormats.ENTITY), VertexFormatRegistry.instance().get(IrisVertexFormats.TERRAIN)), (Object)new IrisEntityToTerrainVertexSerializer());
        this.cache.put(MixinVertexSerializerCache.createKey(VertexFormatRegistry.instance().get(DefaultVertexFormat.f_85812_), VertexFormatRegistry.instance().get(IrisVertexFormats.TERRAIN)), (Object)new EntityToTerrainVertexSerializer());
        this.cache.put(MixinVertexSerializerCache.createKey(VertexFormatRegistry.instance().get(DefaultVertexFormat.f_85820_), VertexFormatRegistry.instance().get(IrisVertexFormats.GLYPH)), (Object)new GlyphExtVertexSerializer());
    }
}

