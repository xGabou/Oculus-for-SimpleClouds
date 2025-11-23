/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.irisshaders.iris.helpers.VertexBufferHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VertexBuffer.class})
public class MixinVertexBuffer
implements VertexBufferHelper {
    private static VertexBuffer current;
    private static VertexBuffer saved;

    @Inject(method={"unbind()V"}, at={@At(value="HEAD")})
    private static void unbindHelper(CallbackInfo ci) {
        current = null;
    }

    @Shadow
    public void m_85921_() {
        throw new IllegalStateException("not shadowed");
    }

    @Inject(method={"bind()V"}, at={@At(value="HEAD")})
    private void bindHelper(CallbackInfo ci) {
        current = (VertexBuffer)this;
    }

    @Override
    public void saveBinding() {
        saved = current;
    }

    @Override
    public void restoreBinding() {
        if (saved != null) {
            saved.m_85921_();
        } else {
            VertexBuffer.m_85931_();
        }
    }
}

