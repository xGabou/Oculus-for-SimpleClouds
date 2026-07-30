package dev.bettersimpleclouds.immersion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.mesh.generator.CloudMeshGenerator;
import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;

/**
 * Makes the cloud LOD cap ("Cap Cloud LOD (finer far)") actually take effect when it is changed.
 *
 * <p>The cap is baked into the cloud mesh generator's {@code LevelOfDetailConfig} at <b>construction</b> (by
 * {@link CloudLodCapMixin}). Simple Clouds only rebuilds that generator when one of <i>its own</i> tracked settings
 * changes (cloud mode, shaded clouds, transparency, the Simple Clouds LOD preset, fixed-section) - see
 * {@code CloudsRendererSettings.checkAndOrBeginInitialization}. A change to <i>our</i> cap matches none of those, and a
 * resource reload (F3+T) doesn't help either because {@code onResourceManagerReload} never nulls the generator, so the
 * reinit check still returns false. Net result: the cap only ever took effect on a cold start that happened to already
 * have the value set - i.e. it "did nothing" when toggled.</p>
 *
 * <p>Fix: watch the cap each frame at the head of {@link SimpleCloudsRenderer#baseTick()} (called every frame on the
 * render thread by Simple Clouds' own {@code LevelRenderer} mixin). When it changes, drop the current generator and
 * {@code requestReload()} - and since {@code baseTick} processes a pending reload on its very next line, the generator
 * is rebuilt this same frame (now null, so the reinit check always rebuilds it), re-running {@link CloudLodCapMixin}
 * with the new cap. The first observed value is just recorded (no reload): the generator built at startup already used
 * it. Gated on {@code simpleclouds}.</p>
 */
@Mixin(value = SimpleCloudsRenderer.class, remap = false)
public abstract class CloudLodCapRebuildMixin {

    @Shadow
    private CloudMeshGenerator meshGenerator;

    @Shadow
    public abstract void requestReload();

    @Unique
    private int bsc$lastLodCap = Integer.MIN_VALUE;

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void bsc$rebuildOnLodCapChange(final CallbackInfo ci) {
        final int cap = BetterSimpleCloudsConfig.cloudMaxLodLevel();
        if (cap == this.bsc$lastLodCap)
            return;
        final boolean first = this.bsc$lastLodCap == Integer.MIN_VALUE;
        this.bsc$lastLodCap = cap;
        if (first)
            return; // the generator built at startup already used the current cap value
        if (this.meshGenerator != null) {
            this.meshGenerator.close();
            this.meshGenerator = null;
        }
        this.requestReload();
    }
}
