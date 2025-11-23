/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.RenderStateShard$TransparencyStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeRenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.mixin.CompositeStateAccessor;
import net.irisshaders.batchedentityrendering.mixin.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderType.CompositeRenderType.class})
public abstract class MixinCompositeRenderType
extends RenderType
implements BlendingStateHolder {
    private static final String INIT = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;IZZLnet/minecraft/client/renderer/RenderType$CompositeState;)V";
    @Unique
    private TransparencyType transparencyType;

    private MixinCompositeRenderType(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    @Inject(method={"<init>(Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;IZZLnet/minecraft/client/renderer/RenderType$CompositeState;)V"}, at={@At(value="RETURN")})
    private void batchedentityrendering$onCompositeInit(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, RenderType.CompositeState compositeState, CallbackInfo ci) {
        RenderStateShard.TransparencyStateShard transparency = ((CompositeStateAccessor)compositeState).getTransparency();
        this.transparencyType = "water_mask".equals(this.f_110133_) ? TransparencyType.WATER_MASK : ("lines".equals(this.f_110133_) ? TransparencyType.LINES : (transparency == RenderStateShardAccessor.getNO_TRANSPARENCY() ? TransparencyType.OPAQUE : (transparency == RenderStateShardAccessor.getGLINT_TRANSPARENCY() || transparency == RenderStateShardAccessor.getCRUMBLING_TRANSPARENCY() ? TransparencyType.DECAL : TransparencyType.GENERAL_TRANSPARENT)));
    }

    @Override
    public TransparencyType getTransparencyType() {
        return this.transparencyType;
    }

    @Override
    public void setTransparencyType(TransparencyType type) {
        this.transparencyType = type;
    }
}

