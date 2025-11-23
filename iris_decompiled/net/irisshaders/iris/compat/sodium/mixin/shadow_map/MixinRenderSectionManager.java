/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager
 *  me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists
 *  me.jellysquid.mods.sodium.client.render.viewport.Viewport
 *  net.minecraft.client.Camera
 *  org.jetbrains.annotations.NotNull
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.shadow_map;

import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import me.jellysquid.mods.sodium.client.render.viewport.Viewport;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.Camera;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderSectionManager.class})
public class MixinRenderSectionManager {
    @Shadow
    @NotNull
    private SortedRenderLists renderLists;
    @Unique
    @NotNull
    private SortedRenderLists shadowRenderLists = SortedRenderLists.empty();

    @Redirect(method={"createTerrainRenderList"}, at=@At(value="FIELD", target="Lme/jellysquid/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lme/jellysquid/mods/sodium/client/render/chunk/lists/SortedRenderLists;"), remap=false)
    private void useShadowRenderList(RenderSectionManager instance, SortedRenderLists value) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            this.shadowRenderLists = value;
        } else {
            this.renderLists = value;
        }
    }

    @Inject(method={"update"}, at={@At(value="INVOKE", target="Lme/jellysquid/mods/sodium/client/render/chunk/RenderSectionManager;createTerrainRenderList(Lnet/minecraft/client/Camera;Lme/jellysquid/mods/sodium/client/render/viewport/Viewport;IZ)V", shift=At.Shift.AFTER)}, cancellable=true, remap=false)
    private void cancelIfShadow(Camera camera, Viewport viewport, int frame, boolean spectator, CallbackInfo ci) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            ci.cancel();
        }
    }

    @Redirect(method={"getRenderLists", "getVisibleChunkCount", "renderLayer"}, at=@At(value="FIELD", target="Lme/jellysquid/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lme/jellysquid/mods/sodium/client/render/chunk/lists/SortedRenderLists;"), remap=false)
    private SortedRenderLists useShadowRenderList2(RenderSectionManager instance) {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? this.shadowRenderLists : this.renderLists;
    }

    @Redirect(method={"resetRenderLists"}, at=@At(value="FIELD", target="Lme/jellysquid/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lme/jellysquid/mods/sodium/client/render/chunk/lists/SortedRenderLists;"), remap=false)
    private void useShadowRenderList3(RenderSectionManager instance, SortedRenderLists value) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            this.shadowRenderLists = value;
        } else {
            this.renderLists = value;
        }
    }
}

