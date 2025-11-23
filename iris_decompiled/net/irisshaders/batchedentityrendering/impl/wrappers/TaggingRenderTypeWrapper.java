/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.batchedentityrendering.impl.wrappers;

import java.util.Objects;
import java.util.Optional;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.irisshaders.batchedentityrendering.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

public class TaggingRenderTypeWrapper
extends RenderType
implements WrappableRenderType {
    private final int tag;
    private final RenderType wrapped;

    public TaggingRenderTypeWrapper(String name, RenderType wrapped, int tag) {
        super(name, wrapped.m_110508_(), wrapped.m_173186_(), wrapped.m_110507_(), wrapped.m_110405_(), TaggingRenderTypeWrapper.shouldSortOnUpload(wrapped), () -> ((RenderType)wrapped).m_110185_(), () -> ((RenderType)wrapped).m_110188_());
        this.tag = tag;
        this.wrapped = wrapped;
    }

    private static boolean shouldSortOnUpload(RenderType type) {
        return ((RenderTypeAccessor)type).shouldSortOnUpload();
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
        TaggingRenderTypeWrapper other = (TaggingRenderTypeWrapper)object;
        return this.tag == other.tag && Objects.equals(this.wrapped, other.wrapped);
    }

    public int hashCode() {
        return this.wrapped.hashCode() + 1;
    }

    public String toString() {
        return "tagged(" + this.tag + "):" + this.wrapped.toString();
    }
}

