/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderType
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.layer;

import java.util.Objects;
import java.util.Optional;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.irisshaders.iris.mixin.rendertype.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

public class OuterWrappedRenderType
extends RenderType
implements WrappableRenderType,
BlendingStateHolder {
    private final RenderStateShard extra;
    private final RenderType wrapped;

    public OuterWrappedRenderType(String name, RenderType wrapped, RenderStateShard extra) {
        super(name, wrapped.m_110508_(), wrapped.m_173186_(), wrapped.m_110507_(), wrapped.m_110405_(), OuterWrappedRenderType.shouldSortOnUpload(wrapped), () -> ((RenderType)wrapped).m_110185_(), () -> ((RenderType)wrapped).m_110188_());
        this.extra = extra;
        this.wrapped = wrapped;
    }

    public static OuterWrappedRenderType wrapExactlyOnce(String name, RenderType wrapped, RenderStateShard extra) {
        if (wrapped instanceof OuterWrappedRenderType) {
            wrapped = ((OuterWrappedRenderType)wrapped).unwrap();
        }
        return new OuterWrappedRenderType(name, wrapped, extra);
    }

    private static boolean shouldSortOnUpload(RenderType type) {
        return ((RenderTypeAccessor)type).shouldSortOnUpload();
    }

    public void m_110185_() {
        this.extra.m_110185_();
        super.m_110185_();
    }

    public void m_110188_() {
        super.m_110188_();
        this.extra.m_110188_();
    }

    @Override
    public RenderType unwrap() {
        return this.wrapped;
    }

    public Optional<RenderType> m_7280_() {
        return this.wrapped.m_7280_();
    }

    public boolean m_5492_() {
        return this.wrapped.m_5492_();
    }

    public boolean equals(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        OuterWrappedRenderType other = (OuterWrappedRenderType)object;
        return Objects.equals(this.wrapped, other.wrapped) && Objects.equals(this.extra, other.extra);
    }

    public int hashCode() {
        return this.wrapped.hashCode() + 1;
    }

    public String toString() {
        return "iris_wrapped:" + this.wrapped.toString();
    }

    @Override
    public TransparencyType getTransparencyType() {
        return ((BlendingStateHolder)this.wrapped).getTransparencyType();
    }

    @Override
    public void setTransparencyType(TransparencyType transparencyType) {
        ((BlendingStateHolder)this.wrapped).setTransparencyType(transparencyType);
    }
}

