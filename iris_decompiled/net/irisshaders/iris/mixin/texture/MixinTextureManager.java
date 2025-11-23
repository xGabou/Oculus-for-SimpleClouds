/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.texture;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.irisshaders.iris.texture.format.TextureFormatLoader;
import net.irisshaders.iris.texture.pbr.PBRTextureManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TextureManager.class})
public class MixinTextureManager {
    @Inject(method={"method_18167", "m_244739_", "lambda$reload$5"}, at={@At(value="TAIL")}, remap=false)
    private void iris$onTailReloadLambda(ResourceManager resourceManager, Executor applyExecutor, CompletableFuture<?> future, Void void1, CallbackInfo ci) {
        TextureFormatLoader.reload(resourceManager);
        PBRTextureManager.INSTANCE.clear();
    }

    @Inject(method={"_dumpAllSheets(Ljava/nio/file/Path;)V"}, at={@At(value="RETURN")})
    private void iris$onInnerDumpTextures(Path path, CallbackInfo ci) {
        PBRTextureManager.INSTANCE.dumpTextures(path);
    }

    @Inject(method={"close()V"}, at={@At(value="TAIL")}, remap=false)
    private void iris$onTailClose(CallbackInfo ci) {
        PBRTextureManager.INSTANCE.close();
    }
}

