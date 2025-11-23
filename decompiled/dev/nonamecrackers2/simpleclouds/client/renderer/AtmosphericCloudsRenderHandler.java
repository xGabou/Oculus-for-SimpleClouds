/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nullable
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.EffectInstance
 *  net.minecraft.client.renderer.PostChain
 *  net.minecraft.client.renderer.PostPass
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.tags.BiomeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffectInstance$FactorData
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraftforge.common.Tags$Biomes
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.joml.Matrix2f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 */
package dev.nonamecrackers2.simpleclouds.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.nonamecrackers2.simpleclouds.SimpleCloudsMod;
import dev.nonamecrackers2.simpleclouds.client.compat.SimpleCloudsCompatHelper;
import dev.nonamecrackers2.simpleclouds.mixin.MixinPostChain;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix2f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class AtmosphericCloudsRenderHandler {
    private static final ResourceLocation SHADER_LOC = SimpleCloudsMod.id("shaders/post/atmospheric_clouds.json");
    private static final Logger LOGGER = LogManager.getLogger((String)"simpleclouds/AtmosphericCloudsRenderHandler");
    private static final float SHIFT_MOVEMENT_SPEED = 0.005f;
    private static final float TRANSITION_SPEED = 0.001f;
    private static final int BIOME_CHECK_INTERVAL = 400;
    private static final Formation DEFAULT = new Formation(b -> ((Biome)b.m_203334_()).m_47554_() < 2.0f, 0.6f, 1.0f, 1.0f);
    private static final ImmutableList<Formation> FORMATIONS = (ImmutableList)Util.m_137537_(() -> {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)new Formation(b -> b.m_203656_(Tags.Biomes.IS_COLD_OVERWORLD) || b.m_203656_(Tags.Biomes.IS_DRY_OVERWORLD) || b.m_203656_(BiomeTags.f_215816_), 0.3f, 1.0f, 30.0f));
        builder.add((Object)new Formation(b -> b.m_203656_(Tags.Biomes.IS_HOT_OVERWORLD), 0.8f, 10.0f, 10.0f));
        builder.add((Object)new Formation(b -> b.m_203656_(Tags.Biomes.IS_PLAINS) || b.m_203656_(BiomeTags.f_207611_), 1.0f, 2.0f, 10.0f));
        builder.add((Object)DEFAULT);
        return builder.build();
    });
    private final Minecraft mc;
    @Nullable
    private PostChain postProcessingShader;
    private Vector2f windDirection = new Vector2f(1.0f, 0.0f);
    private float shiftMovement;
    private float shiftMovementO;
    private float transition;
    private float transitionO;
    private int tickCount;
    private Formation formation = DEFAULT;
    private Formation nextFormation;

    public AtmosphericCloudsRenderHandler(Minecraft mc) {
        this.mc = mc;
    }

    public void setWindDirection(Vector2f direction) {
        this.windDirection = new Vector2f((Vector2fc)direction);
    }

    public void tick() {
        ++this.tickCount;
        this.shiftMovementO = this.shiftMovement;
        this.shiftMovement += 0.005f;
        this.transitionO = this.transition;
        if (this.nextFormation != null) {
            this.transition += 0.001f;
            if (this.transition > 1.0f) {
                this.formation = this.nextFormation;
                this.nextFormation = null;
                this.transition = 0.0f;
                this.transitionO = 0.0f;
            }
        }
        if (this.mc.f_91073_ != null && this.tickCount % 400 == 0) {
            for (Formation formation : FORMATIONS) {
                if (!formation.rendersIn().test((Holder<Biome>)this.mc.f_91073_.m_204166_(this.mc.f_91063_.m_109153_().m_90588_()))) continue;
                if (this.formation == formation || this.nextFormation == formation || this.transition != 0.0f) break;
                this.nextFormation = formation;
                break;
            }
        }
    }

    public void render(PoseStack stack, Matrix4f projMat, float partialTick, double camX, double camY, double camZ, float r, float g, float b) {
        if (this.postProcessingShader != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            RenderSystem.disableBlend();
            RenderSystem.depthMask((boolean)false);
            Matrix4f invertedProjMat = new Matrix4f((Matrix4fc)projMat).invert();
            Matrix4f invertedModelViewMat = new Matrix4f((Matrix4fc)stack.m_85850_().m_252922_()).invert();
            float shiftMovement = Mth.m_14179_((float)partialTick, (float)this.shiftMovementO, (float)this.shiftMovement);
            float yaw = (float)Mth.m_14136_((double)this.windDirection.x, (double)this.windDirection.y);
            float transition = Mth.m_14179_((float)partialTick, (float)this.transitionO, (float)this.transition);
            float alpha = 1.0f;
            Entity cameraEntity = this.mc.f_91063_.m_109153_().m_90592_();
            if (cameraEntity instanceof LivingEntity) {
                MobEffectInstance instance;
                LivingEntity living = (LivingEntity)cameraEntity;
                Map map = living.m_21221_();
                if (map.containsKey(MobEffects.f_19610_)) {
                    MobEffectInstance instance2 = (MobEffectInstance)map.get(MobEffects.f_19610_);
                    alpha = instance2.m_267577_() ? 0.0f : 1.0f - Mth.m_14036_((float)((float)instance2.m_19557_() / 20.0f), (float)0.0f, (float)1.0f);
                } else if (map.containsKey(MobEffects.f_216964_) && (instance = (MobEffectInstance)map.get(MobEffects.f_216964_)).m_216895_().isPresent()) {
                    alpha = 1.0f - Mth.m_14036_((float)((MobEffectInstance.FactorData)instance.m_216895_().get()).m_238413_(living, partialTick), (float)0.0f, (float)1.0f);
                }
            }
            List<PostPass> passes = ((MixinPostChain)this.postProcessingShader).simpleclouds$getPostPasses();
            AtmosphericCloudsRenderHandler.updatePass(passes.get(0), invertedProjMat, invertedModelViewMat, camX, camY, camZ, yaw, shiftMovement, 1.0f - transition, this.formation, r, g, b, alpha);
            AtmosphericCloudsRenderHandler.updatePass(passes.get(1), invertedProjMat, invertedModelViewMat, camX, camY, camZ, yaw, shiftMovement, transition, this.nextFormation != null ? this.nextFormation : DEFAULT, r, g, b, alpha);
            this.postProcessingShader.m_110023_(partialTick);
            RenderSystem.depthMask((boolean)true);
        }
    }

    private static void updatePass(PostPass pass, Matrix4f invertedProjMat, Matrix4f invertedModelViewMat, double camX, double camY, double camZ, float yaw, float shiftMovement, float densityMult, Formation formation, float r, float g, float b, float a) {
        EffectInstance effect = pass.m_110074_();
        effect.m_108960_("InverseWorldProjMat").m_5679_(invertedProjMat);
        effect.m_108960_("InverseModelViewMat").m_5679_(invertedModelViewMat);
        Matrix2f transform = new Matrix2f().identity();
        transform.scale(formation.scaleX, formation.scaleZ);
        transform.rotateLocal(yaw);
        effect.m_108960_("Transform").m_142588_(transform.m00, transform.m01, transform.m10, transform.m11);
        effect.m_108960_("ShiftMovement").m_5985_(shiftMovement);
        effect.m_108960_("CloudDensity").m_5985_(formation.density * densityMult);
        effect.m_108960_("CloudColor").m_5805_(r, g, b, a);
    }

    public void init(ResourceManager manager) {
        this.close();
        try {
            RenderTarget main = SimpleCloudsCompatHelper.getMainRenderTarget();
            if (main == null) {
                LOGGER.warn("Main framebufer is null");
                return;
            }
            this.postProcessingShader = new PostChain(this.mc.m_91097_(), manager, main, SHADER_LOC);
            if (((MixinPostChain)this.postProcessingShader).simpleclouds$getPostPasses().size() != 2) {
                throw new IllegalArgumentException("Expected two post passes in shader");
            }
            this.postProcessingShader.m_110025_(main.f_83915_, main.f_83916_);
        }
        catch (JsonSyntaxException e) {
            LOGGER.warn("Failed to parse post shader: {}", (Object)SHADER_LOC, (Object)e);
            this.close();
        }
        catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Failed to load post shader: {}", (Object)SHADER_LOC, (Object)e);
            this.close();
        }
    }

    public void onResize(int width, int height) {
        RenderTarget main = SimpleCloudsCompatHelper.getMainRenderTarget();
        if (main == null) {
            return;
        }
        width = main.f_83915_;
        height = main.f_83916_;
        if (this.postProcessingShader != null) {
            this.postProcessingShader.m_110025_(width, height);
        }
    }

    public void close() {
        if (this.postProcessingShader != null) {
            this.postProcessingShader.close();
            this.postProcessingShader = null;
        }
        this.formation = DEFAULT;
        this.nextFormation = null;
        this.transition = 0.0f;
        this.transitionO = 0.0f;
        this.tickCount = 0;
        this.shiftMovement = 0.0f;
        this.shiftMovementO = 0.0f;
        this.windDirection = new Vector2f(1.0f, 0.0f);
    }

    record Formation(Predicate<Holder<Biome>> rendersIn, float density, float scaleX, float scaleZ) {
    }
}

