package dev.bettersimpleclouds.immersion;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;

/**
 * <b>Simple Clouds &mdash; In-Cloud Immersion</b> (client only). Registered on the NeoForge game event bus only
 * when {@code simpleclouds} is present, so every reference to its classes is safe.
 *
 * <p>Simple Clouds' clouds are solid volumes you can fly into, but inside one the base mod shows only the cloud's
 * own (blocky) geometry. This patch leaves that structure alone and adds scattered, translucent {@linkplain
 * InCloudParticles motes} drifting through the interior - busy and messy in storm clouds, none at all in small calm
 * clouds - so flying through a cloud feels alive. Driven by a single smooth {@link CloudEnvelopment} signal so
 * entering and leaving a cloud fades naturally.</p>
 *
 * <p>Everything self-disables when toggled off, when Simple Clouds reports no cloud at the camera, or when the
 * camera is outside the cloud layer &mdash; so there is zero cost in clear sky.</p>
 */
public final class InCloudImmersionPatch {

    private static final Logger LOGGER = LogUtils.getLogger();

    /** Fog only kicks in once the camera is clearly inside a cloud (envelopment above this), then ramps to full. */
    private static final float FOG_MIN_ENVELOPMENT = 0.5F;

    private final CloudEnvelopment envelopment = new CloudEnvelopment();
    private final InCloudParticles particles = new InCloudParticles();

    /** Advance the envelopment easing and the mote simulation once per client tick (frame-rate independent). */
    @SubscribeEvent
    public void onClientTick(final ClientTickEvent.Post event) {
        final Minecraft mc = Minecraft.getInstance();
        // Solid cloud faces (so clouds aren't see-through from inside) are forced by SimpleCloudsRendererSolidFacesMixin
        // at mesh-generation time - Simple Clouds re-applies its own culling flag every frame, so a per-tick setter
        // here would just be clobbered before it took effect.

        if (!BetterSimpleCloudsConfig.inCloudEnabled() || mc.level == null) {
            this.envelopment.reset();
            this.particles.reset();
            InteriorFillState.disable();
            return;
        }
        if (mc.isPaused())
            return; // hold steady while paused
        this.envelopment.tick();
        // Un-interpolated envelopment is fine for the per-tick simulation.
        final float env = this.envelopment.envelopment(1.0F);
        this.particles.tick(env, this.envelopment.storminess());
        // Hand the envelopment + config to the cloud mesh compute shader (drives the interior fill it bakes in).
        InteriorFillState.update(env);
    }

    /** Draw the in-cloud motes after weather (clouds/rain already drawn), before the hand. */
    @SubscribeEvent
    public void onRenderStage(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER)
            return;
        if (!BetterSimpleCloudsConfig.inCloudEnabled())
            return;
        final float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        final float env = this.envelopment.envelopment(partialTick);
        if (env <= 1.0E-3F) // not inside a cloud: nothing to draw (envelopment is only > 0 when Simple Clouds is live)
            return;

        // The interior fill is baked into Simple Clouds' own mesh (see CloudMeshGeneratorFillMixin); here we only add
        // the drifting motes as fine detail on top.
        this.particles.render(event.getPoseStack().last().pose(), partialTick, env);
    }

    /** Diagnostic overlay: shows whether the camera is being detected as inside a cloud, and where the clouds are. */
    @SubscribeEvent
    public void onRenderGui(final RenderGuiEvent.Post event) {
        if (!BetterSimpleCloudsConfig.inCloudEnabled() || !BetterSimpleCloudsConfig.inCloudDebugOverlay())
            return;
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.options.hideGui)
            return;
        final float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(false);
        event.getGuiGraphics().drawString(mc.font, this.envelopment.debugLine(partialTick), 4, 4, 0xFFE9B0, true);
    }

    /**
     * Tighten the fog to the in-cloud view distance while the camera is inside a cloud (the no-shaders view limiter;
     * under an Iris shaderpack the pack owns fog, so this may be ignored - the opaque shell handles that case). Only
     * ever shortens the fog, and only while genuinely enveloped, so it never touches normal outdoor fog.
     */
    @SubscribeEvent
    public void onRenderFog(final ViewportEvent.RenderFog event) {
        final float t = fogStrength();
        if (t <= 0.0F)
            return;
        final float viewDist = BetterSimpleCloudsConfig.inCloudViewDistanceBlocks();
        final float far = Mth.lerp(t, event.getFarPlaneDistance(), viewDist);
        if (far < event.getFarPlaneDistance()) {
            event.setFarPlaneDistance(far);
            event.setNearPlaneDistance(Math.min(event.getNearPlaneDistance(), far * 0.2F));
            event.setFogShape(FogShape.SPHERE);
        }
    }

    /** Tint that in-cloud fog toward the cloud's own colour (only while inside, ramped with envelopment). */
    @SubscribeEvent
    public void onComputeFogColor(final ViewportEvent.ComputeFogColor event) {
        final float t = fogStrength();
        if (t <= 0.0F)
            return;
        final SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getOptionalInstance().orElse(null);
        if (renderer == null)
            return;
        final float[] c = renderer.getCloudColor(1.0F);
        event.setRed(Mth.lerp(t, event.getRed(), c[0]));
        event.setGreen(Mth.lerp(t, event.getGreen(), c[1]));
        event.setBlue(Mth.lerp(t, event.getBlue(), c[2]));
    }

    /**
     * Fog blend factor for this frame: {@code 0} unless the camera is clearly inside a cloud and the fog option is on,
     * then ramping {@code 0->1} from {@link #FOG_MIN_ENVELOPMENT} to full envelopment. Keeps fog strictly in-cloud.
     */
    private float fogStrength() {
        // Cheapest checks first; no allocation. Envelopment is only > 0 when Simple Clouds is live and the camera is
        // inside a cloud, so it doubles as the "in cloud" + "renderer present" gate - no getOptionalInstance() needed
        // here on the every-frame path (the colour handler fetches the renderer only once it knows fog is active).
        if (!BetterSimpleCloudsConfig.inCloudEnabled() || !BetterSimpleCloudsConfig.inCloudFogEnabled())
            return 0.0F;
        final float env = this.envelopment.envelopment(1.0F);
        if (env <= FOG_MIN_ENVELOPMENT)
            return 0.0F;
        return Mth.clamp((env - FOG_MIN_ENVELOPMENT) / (1.0F - FOG_MIN_ENVELOPMENT), 0.0F, 1.0F);
    }

    public static void logReady() {
        LOGGER.info("[Simple Clouds x In-Cloud Immersion] ready.");
    }
}
