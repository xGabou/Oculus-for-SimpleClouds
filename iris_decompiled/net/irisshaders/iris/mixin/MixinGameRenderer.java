/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.platform.GlUtil
 *  com.mojang.blaze3d.shaders.Program
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.ItemInHandRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.gl.program.IrisProgramTypes;
import net.irisshaders.iris.pathways.HandRenderer;
import net.irisshaders.iris.pipeline.ShaderRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GameRenderer.class})
public class MixinGameRenderer {
    @Shadow
    private boolean f_109070_;

    @Inject(method={"getPositionShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overridePositionShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (MixinGameRenderer.isSky()) {
            MixinGameRenderer.override(ShaderKey.SKY_BASIC, cir);
        } else if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_BASIC, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.BASIC, cir);
        }
    }

    @Inject(method={"getPositionColorShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overridePositionColorShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (MixinGameRenderer.isSky()) {
            MixinGameRenderer.override(ShaderKey.SKY_BASIC_COLOR, cir);
        } else if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_BASIC_COLOR, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.BASIC_COLOR, cir);
        }
    }

    @Inject(method={"getPositionTexShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overridePositionTexShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (MixinGameRenderer.isSky()) {
            MixinGameRenderer.override(ShaderKey.SKY_TEXTURED, cir);
        } else if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TEX, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.TEXTURED, cir);
        }
    }

    @Inject(method={"getPositionTexColorShader", "getPositionColorTexShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overridePositionTexColorShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (MixinGameRenderer.isSky()) {
            MixinGameRenderer.override(ShaderKey.SKY_TEXTURED_COLOR, cir);
        } else if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TEX_COLOR, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.TEXTURED_COLOR, cir);
        }
    }

    @Inject(method={"getParticleShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideParticleShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (MixinGameRenderer.isPhase(WorldRenderingPhase.RAIN_SNOW)) {
            MixinGameRenderer.override(ShaderKey.WEATHER, cir);
        } else if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_PARTICLES, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.PARTICLES, cir);
        }
    }

    @Inject(method={"getPositionTexColorNormalShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overridePositionTexColorNormalShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_CLOUDS, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.CLOUDS, cir);
        }
    }

    @Inject(method={"getRendertypeSolidShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideSolidShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TERRAIN_CUTOUT, cir);
        } else if (MixinGameRenderer.isBlockEntities() || MixinGameRenderer.isEntities()) {
            MixinGameRenderer.override(ShaderKey.MOVING_BLOCK, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.TERRAIN_SOLID, cir);
        }
    }

    @Inject(method={"getRendertypeCutoutShader", "getRendertypeCutoutMippedShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideCutoutShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TERRAIN_CUTOUT, cir);
        } else if (MixinGameRenderer.isBlockEntities() || MixinGameRenderer.isEntities()) {
            MixinGameRenderer.override(ShaderKey.MOVING_BLOCK, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.TERRAIN_CUTOUT, cir);
        }
    }

    @Inject(method={"getRendertypeTranslucentShader", "getRendertypeTranslucentNoCrumblingShader", "getRendertypeTranslucentMovingBlockShader", "getRendertypeTripwireShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideTranslucentShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TERRAIN_CUTOUT, cir);
        } else if (MixinGameRenderer.isBlockEntities() || MixinGameRenderer.isEntities()) {
            MixinGameRenderer.override(ShaderKey.MOVING_BLOCK, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.TERRAIN_TRANSLUCENT, cir);
        }
    }

    @Inject(method={"getRendertypeEntityCutoutShader", "getRendertypeEntityCutoutNoCullShader", "getRendertypeEntityCutoutNoCullZOffsetShader", "getRendertypeEntityDecalShader", "getRendertypeEntitySmoothCutoutShader", "getRendertypeArmorCutoutNoCullShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEntityCutoutShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_ENTITIES_CUTOUT, cir);
        } else if (HandRenderer.INSTANCE.isActive()) {
            MixinGameRenderer.override(HandRenderer.INSTANCE.isRenderingSolid() ? ShaderKey.HAND_CUTOUT_DIFFUSE : ShaderKey.HAND_WATER_DIFFUSE, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.BLOCK_ENTITY_DIFFUSE, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_CUTOUT_DIFFUSE, cir);
        }
    }

    @Inject(method={"getRendertypeEntityTranslucentShader", "getRendertypeEntityTranslucentCullShader", "getRendertypeItemEntityTranslucentCullShader", "getRendertypeBreezeWindShader", "getRendertypeEntityNoOutlineShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEntityTranslucentShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_ENTITIES_CUTOUT, cir);
        } else if (HandRenderer.INSTANCE.isActive()) {
            MixinGameRenderer.override(HandRenderer.INSTANCE.isRenderingSolid() ? ShaderKey.HAND_CUTOUT_DIFFUSE : ShaderKey.HAND_WATER_DIFFUSE, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.BE_TRANSLUCENT, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_TRANSLUCENT, cir);
        }
    }

    @Inject(method={"getRendertypeEnergySwirlShader", "getRendertypeEntityShadowShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEnergySwirlShadowShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_ENTITIES_CUTOUT, cir);
        } else if (HandRenderer.INSTANCE.isActive()) {
            MixinGameRenderer.override(HandRenderer.INSTANCE.isRenderingSolid() ? ShaderKey.HAND_CUTOUT : ShaderKey.HAND_TRANSLUCENT, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.BLOCK_ENTITY, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_CUTOUT, cir);
        }
    }

    @Inject(method={"getRendertypeGlintShader", "getRendertypeGlintDirectShader", "getRendertypeGlintTranslucentShader", "getRendertypeArmorGlintShader", "getRendertypeEntityGlintDirectShader", "getRendertypeEntityGlintShader", "getRendertypeArmorEntityGlintShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideGlintShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.GLINT, cir);
        }
    }

    @Inject(method={"getRendertypeEntitySolidShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEntitySolidDiffuseShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_ENTITIES_CUTOUT, cir);
        } else if (HandRenderer.INSTANCE.isActive()) {
            MixinGameRenderer.override(HandRenderer.INSTANCE.isRenderingSolid() ? ShaderKey.HAND_CUTOUT_DIFFUSE : ShaderKey.HAND_WATER_DIFFUSE, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.BLOCK_ENTITY_DIFFUSE, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_SOLID_DIFFUSE, cir);
        }
    }

    @Inject(method={"getRendertypeWaterMaskShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEntitySolidShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_ENTITIES_CUTOUT, cir);
        } else if (HandRenderer.INSTANCE.isActive()) {
            MixinGameRenderer.override(HandRenderer.INSTANCE.isRenderingSolid() ? ShaderKey.HAND_CUTOUT : ShaderKey.HAND_TRANSLUCENT, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.BLOCK_ENTITY, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_SOLID, cir);
        }
    }

    @Inject(method={"getRendertypeBeaconBeamShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideBeaconBeamShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_BEACON_BEAM, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.BEACON, cir);
        }
    }

    @Inject(method={"getRendertypeEntityAlphaShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEntityAlphaShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (!ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_ALPHA, cir);
        }
    }

    @Inject(method={"getRendertypeEyesShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEntityEyesShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_ENTITIES_CUTOUT, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.BLOCK_ENTITY, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_EYES, cir);
        }
    }

    @Inject(method={"getRendertypeEntityTranslucentEmissiveShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideEntityTranslucentEmissiveShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_ENTITIES_CUTOUT, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.BLOCK_ENTITY, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.ENTITIES_EYES_TRANS, cir);
        }
    }

    @Inject(method={"getRendertypeLeashShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideLeashShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_LEASH, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.LEASH, cir);
        }
    }

    @Inject(method={"getRendertypeLightningShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideLightningShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_LIGHTNING, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.LIGHTNING, cir);
        }
    }

    @Inject(method={"getRendertypeCrumblingShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideCrumblingShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (MixinGameRenderer.shouldOverrideShaders() && !ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.CRUMBLING, cir);
        }
    }

    @Inject(method={"getRendertypeTextShader", "getRendertypeTextSeeThroughShader", "getPositionColorTexLightmapShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideTextShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TEXT, cir);
        } else if (HandRenderer.INSTANCE.isActive()) {
            MixinGameRenderer.override(ShaderKey.HAND_TEXT, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.TEXT_BE, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.TEXT, cir);
        }
    }

    @Inject(method={"getRendertypeTextBackgroundShader", "getRendertypeTextBackgroundSeeThroughShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideTextBackgroundShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TEXT_BG, cir);
        } else {
            MixinGameRenderer.override(ShaderKey.TEXT_BG, cir);
        }
    }

    @Inject(method={"getRendertypeTextIntensityShader", "getRendertypeTextIntensitySeeThroughShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideTextIntensityShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_TEXT_INTENSITY, cir);
        } else if (HandRenderer.INSTANCE.isActive()) {
            MixinGameRenderer.override(ShaderKey.HAND_TEXT_INTENSITY, cir);
        } else if (MixinGameRenderer.isBlockEntities()) {
            MixinGameRenderer.override(ShaderKey.TEXT_INTENSITY_BE, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.TEXT_INTENSITY, cir);
        }
    }

    @Inject(method={"getRendertypeLinesShader"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$overrideLinesShader(CallbackInfoReturnable<ShaderInstance> cir) {
        if (ShadowRenderer.ACTIVE) {
            MixinGameRenderer.override(ShaderKey.SHADOW_LINES, cir);
        } else if (MixinGameRenderer.shouldOverrideShaders()) {
            MixinGameRenderer.override(ShaderKey.LINES, cir);
        }
    }

    private static boolean isBlockEntities() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        return pipeline != null && pipeline.getPhase() == WorldRenderingPhase.BLOCK_ENTITIES;
    }

    private static boolean isEntities() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        return pipeline != null && pipeline.getPhase() == WorldRenderingPhase.ENTITIES;
    }

    private static boolean isSky() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null) {
            return switch (pipeline.getPhase()) {
                case WorldRenderingPhase.CUSTOM_SKY, WorldRenderingPhase.SKY, WorldRenderingPhase.SUNSET, WorldRenderingPhase.SUN, WorldRenderingPhase.STARS, WorldRenderingPhase.VOID, WorldRenderingPhase.MOON -> true;
                default -> false;
            };
        }
        return false;
    }

    private static boolean isPhase(WorldRenderingPhase phase) {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline != null) {
            return pipeline.getPhase() == phase;
        }
        return false;
    }

    private static boolean shouldOverrideShaders() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof ShaderRenderingPipeline) {
            return ((ShaderRenderingPipeline)pipeline).shouldOverrideShaders();
        }
        return false;
    }

    private static void override(ShaderKey key, CallbackInfoReturnable<ShaderInstance> cir) {
        ShaderInstance override;
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        if (pipeline instanceof ShaderRenderingPipeline && (override = ((ShaderRenderingPipeline)pipeline).getShaderMap().getShader(key)) != null) {
            cir.setReturnValue((Object)override);
        }
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void iris$logSystem(Minecraft arg, ItemInHandRenderer arg2, ResourceManager arg3, RenderBuffers arg4, CallbackInfo ci) {
        Iris.logger.info("Hardware information:");
        Iris.logger.info("CPU: " + GlUtil.m_84819_());
        Iris.logger.info("GPU: " + GlUtil.m_84820_() + " (Supports OpenGL " + GlUtil.m_84821_() + ")");
        Iris.logger.info("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")");
    }

    @Redirect(method={"renderItemInHand"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ItemInHandRenderer;renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/player/LocalPlayer;I)V"))
    private void iris$disableVanillaHandRendering(ItemInHandRenderer itemInHandRenderer, float tickDelta, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LocalPlayer localPlayer, int light) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            return;
        }
        itemInHandRenderer.m_109314_(tickDelta, poseStack, bufferSource, localPlayer, light);
    }

    @Inject(method={"renderLevel"}, at={@At(value="TAIL")})
    private void iris$runColorSpace(float pGameRenderer0, long pLong1, PoseStack pPoseStack2, CallbackInfo ci) {
        Iris.getPipelineManager().getPipeline().ifPresent(WorldRenderingPipeline::finalizeGameRendering);
    }

    @Redirect(method={"reloadShaders"}, at=@At(value="INVOKE", target="Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"))
    private ArrayList<Program> iris$reloadGeometryShaders() {
        ArrayList programs = Lists.newArrayList();
        programs.addAll(IrisProgramTypes.GEOMETRY.m_85570_().values());
        programs.addAll(IrisProgramTypes.TESS_CONTROL.m_85570_().values());
        programs.addAll(IrisProgramTypes.TESS_EVAL.m_85570_().values());
        return programs;
    }
}

