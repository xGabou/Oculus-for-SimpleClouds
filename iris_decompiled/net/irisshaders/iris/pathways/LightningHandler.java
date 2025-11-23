/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.resources.ResourceLocation
 */
package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.irisshaders.iris.layer.InnerWrappedRenderType;
import net.irisshaders.iris.layer.LightningRenderStateShard;
import net.irisshaders.iris.pipeline.programs.ShaderAccess;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class LightningHandler
extends RenderType {
    public static final RenderType IRIS_LIGHTNING = new InnerWrappedRenderType("iris_lightning2", (RenderType)RenderType.m_173215_((String)"iris_lightning", (VertexFormat)DefaultVertexFormat.f_85815_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.m_110628_().m_173292_(f_173091_).m_110687_(f_110114_).m_110685_(f_110136_).m_110675_(f_110127_).m_110691_(false)), new LightningRenderStateShard());
    public static final Function<ResourceLocation, RenderType> MEKANISM_FLAME = Util.m_143827_(resourceLocation -> {
        RenderType.CompositeState state = RenderType.CompositeState.m_110628_().m_173292_(new RenderStateShard.ShaderStateShard(ShaderAccess::getMekanismFlameShader)).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(resourceLocation, false, false)).m_110685_(f_110139_).m_110691_(true);
        return LightningHandler.m_173215_((String)"mek_flame", (VertexFormat)DefaultVertexFormat.f_85819_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)state);
    });
    public static final RenderType MEKASUIT = LightningHandler.m_173215_((String)"mekasuit", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)131072, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.m_110628_().m_173292_(new RenderStateShard.ShaderStateShard(ShaderAccess::getMekasuitShader)).m_173290_((RenderStateShard.EmptyTextureStateShard)f_110146_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(true));

    public LightningHandler(String pRenderType0, VertexFormat pVertexFormat1, VertexFormat.Mode pVertexFormat$Mode2, int pInt3, boolean pBoolean4, boolean pBoolean5, Runnable pRunnable6, Runnable pRunnable7) {
        super(pRenderType0, pVertexFormat1, pVertexFormat$Mode2, pInt3, pBoolean4, pBoolean5, pRunnable6, pRunnable7);
    }
}

