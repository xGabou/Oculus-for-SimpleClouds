/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.nonamecrackers2.simpleclouds.client.renderer.lightning;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LightningBolt {
    private static final int TOTAL_TIME = 60;
    private static final int STAY_DURATION = 10;
    private static final int FADE_IN_TIME = 2;
    private static final int FADE_OUT_TIME = 20;
    private static final float FLASH_INTENSITY = 2.0f;
    private static final float RESTRUCTURE_FADE_LIMIT = 0.1f;
    private static final int BRANCH_SEQUENCE_FADE_DURATION = 5;
    public static final int MAX_DEPTH = 16;
    public static final int MAX_BRANCHES = 8;
    public static final float MINIMUM_PITCH_ALLOWED = 0.0f;
    public static final float MAXIMUM_PITCH_ALLOWED = 180.0f;
    private final RandomSource random;
    private final int totalDepth;
    private final int branchCount;
    private final float maxBranchLength;
    private final float maxWidth;
    private final float maxPitch;
    private final float minPitch;
    private final Vector3f position;
    private List<Branch> root;
    private int tickCount;
    private float fade;
    private float fadeO;
    private float r;
    private float g;
    private float b;

    public LightningBolt(RandomSource random, Vector3f position, int depth, int branchCount, float maxBranchLength, float maxWidth, float minimumPitch, float maximumPitch, float r, float g, float b) {
        this.random = random;
        this.totalDepth = Mth.m_14045_((int)depth, (int)1, (int)16);
        this.branchCount = Mth.m_14045_((int)branchCount, (int)1, (int)8);
        this.maxBranchLength = maxBranchLength;
        this.maxWidth = maxWidth;
        this.minPitch = Mth.m_14036_((float)minimumPitch, (float)0.0f, (float)180.0f);
        this.maxPitch = Mth.m_14036_((float)maximumPitch, (float)minimumPitch, (float)180.0f);
        this.position = position;
        this.root = LightningBolt.buildBranchesWithChildren(random, depth, 0, branchCount, maxBranchLength, maxWidth, maxWidth, maximumPitch, minimumPitch);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private static float calculateWidthAtDepth(int maxDepth, int desiredDepth, float maxWidth) {
        float width = maxWidth;
        for (int i = 0; i < desiredDepth; ++i) {
            if (width <= 0.5f) {
                return 0.5f;
            }
            width -= maxWidth / (float)(maxDepth + 1);
        }
        return width;
    }

    private static List<Branch> buildBranchesWithChildren(RandomSource random, int totalDepth, int currentDepth, int branchCount, float maxBranchLength, float maxWidth, float width, float minPitch, float maxPitch) {
        if (currentDepth >= totalDepth) {
            return Lists.newArrayList();
        }
        ArrayList branches = Lists.newArrayList();
        for (int i = 0; i < branchCount; ++i) {
            int nextBranchCount;
            float pitch = (maxPitch - minPitch) * random.m_188501_() + minPitch;
            float yaw = 360.0f * random.m_188501_();
            float length = maxBranchLength / 4.0f + maxBranchLength * random.m_188501_();
            float nextWidth = Math.max(0.5f, width - maxWidth / (float)(totalDepth + 1));
            int range = Mth.m_14143_((float)((float)totalDepth - 1.0f / (float)totalDepth * ((float)currentDepth * (float)currentDepth)));
            int n = nextBranchCount = range <= 0 ? 0 : Math.min(random.m_188503_(range), branchCount);
            if ((float)currentDepth / (float)totalDepth < 0.5f) {
                nextBranchCount = Math.max(nextBranchCount, 1);
            }
            List<Branch> children = LightningBolt.buildBranchesWithChildren(random, totalDepth, currentDepth + 1, nextBranchCount, maxBranchLength, maxWidth, nextWidth, minPitch, maxPitch);
            Branch branch = new Branch(children, pitch, yaw, width, length);
            branches.add(branch);
        }
        return branches;
    }

    @Nullable
    private static List<Branch> getBranchesAtDepth(List<Branch> root, int atDepth, int currentDepth) {
        if (currentDepth == atDepth) {
            return root;
        }
        for (Branch branch : root) {
            List<Branch> list = LightningBolt.getBranchesAtDepth(branch.branches, atDepth, currentDepth + 1);
            if (list == null) continue;
            return list;
        }
        return null;
    }

    private List<Branch> getBranchesAtDepth(int depth) {
        List<Branch> branches = LightningBolt.getBranchesAtDepth(this.root, depth, 0);
        if (branches == null) {
            return Lists.newArrayList();
        }
        return branches;
    }

    public void tick() {
        ++this.tickCount;
        if (this.tickCount < 60) {
            this.fadeO = this.fade;
            this.fade = 1.0f;
            this.fade = this.tickCount < 10 ? Math.min(1.0f, (float)this.tickCount / 2.0f) : Math.max(0.0f, 1.0f - (float)(this.tickCount - 10) / 20.0f);
            this.fade *= (float)Math.pow(this.random.m_188501_(), 2.0);
            if (this.fade > 0.1f) {
                int maxDepth = this.totalDepth - Mth.m_14143_((float)((float)this.totalDepth * ((float)this.tickCount / 60.0f)));
                int depth = this.totalDepth - (maxDepth <= 1 ? 0 : this.random.m_188503_(maxDepth));
                float width = LightningBolt.calculateWidthAtDepth(this.totalDepth, depth, this.maxWidth);
                List<Branch> branches = this.getBranchesAtDepth(depth);
                if (!branches.isEmpty()) {
                    int index = branches.size() <= 1 ? 0 : this.random.m_188503_(branches.size());
                    Branch branch = branches.get(index);
                    branch.setBranches(LightningBolt.buildBranchesWithChildren(this.random, this.totalDepth - depth, 0, this.branchCount, this.maxBranchLength, this.maxWidth, width, this.minPitch, this.maxPitch));
                }
            }
        }
    }

    public boolean isDead() {
        return this.tickCount > 60;
    }

    public void render(PoseStack stack, VertexConsumer consumer, float partialTick, float r, float g, float b, float a) {
        float alpha = Mth.m_14179_((float)partialTick, (float)this.fadeO, (float)this.fade) * a;
        if (alpha <= 0.01f) {
            return;
        }
        stack.m_85836_();
        stack.m_252880_(this.position.x, this.position.y, this.position.z);
        float animFactor = ((float)this.tickCount + partialTick) / 5.0f;
        int depth = Mth.m_14143_((float)((float)this.totalDepth * animFactor));
        for (Branch branch : this.root) {
            LightningBolt.renderBranch(depth, 0, new Vector3f(), stack, consumer, r * this.r, g * this.g, b * this.b, alpha, branch);
        }
        stack.m_85849_();
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public float getFade(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)this.fadeO, (float)this.fade);
    }

    private static void renderBranch(int maxDepth, int currentDepth, Vector3f offset, PoseStack stack, VertexConsumer consumer, float r, float g, float b, float a, Branch branch) {
        if (currentDepth > maxDepth) {
            return;
        }
        stack.m_85836_();
        stack.m_252880_(offset.x, offset.y, offset.z);
        stack.m_252781_(Axis.f_252436_.m_252977_(branch.yaw));
        stack.m_252781_(Axis.f_252529_.m_252977_(branch.pitch));
        Matrix4f mat = stack.m_85850_().m_252922_();
        int layers = 4;
        for (int i = 0; i < layers; ++i) {
            float factor = (float)i / (float)layers;
            float width = branch.width - 4.0f * (branch.width / 4.0f) * factor;
            if (width <= 0.05f) continue;
            float length = branch.length - factor;
            float startingY = -factor * 0.5f;
            float alpha = (float)(i + 1) / (float)layers * 0.5f;
            LightningBolt.lightningBoltSection(mat, consumer, startingY, width, length, r, g, b, alpha * a);
        }
        float yawRadians = branch.yaw * ((float)Math.PI / 180);
        float pitchRadians = (90.0f - branch.pitch) * ((float)Math.PI / 180);
        float pitchCos = Mth.m_14089_((float)pitchRadians);
        Vector3f end = new Vector3f(Mth.m_14031_((float)yawRadians) * pitchCos, Mth.m_14031_((float)pitchRadians), Mth.m_14089_((float)yawRadians) * pitchCos).mul(-branch.length).add((Vector3fc)offset);
        stack.m_85849_();
        for (Branch child : branch.branches) {
            LightningBolt.renderBranch(maxDepth, currentDepth + 1, end, stack, consumer, r, g, b, a, child);
        }
    }

    private static void lightningBoltSection(Matrix4f poseMatrix, VertexConsumer consumer, float yStart, float width, float length, float r, float g, float b, float a) {
        float halfWidth = width / 2.0f;
        consumer.m_252986_(poseMatrix, halfWidth, yStart, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart - length, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart - length, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart - length, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart - length, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart - length, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart - length, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart - length, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart - length, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart - length, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart - length, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart - length, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart - length, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart, -halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, halfWidth, yStart, halfWidth).m_85950_(r, g, b, a).m_5752_();
        consumer.m_252986_(poseMatrix, -halfWidth, yStart, halfWidth).m_85950_(r, g, b, a).m_5752_();
    }

    public static class Branch {
        private List<Branch> branches;
        private final float pitch;
        private final float yaw;
        private final float width;
        private final float length;

        public Branch(List<Branch> branches, float pitch, float yaw, float width, float length) {
            this.branches = branches;
            this.pitch = pitch;
            this.yaw = yaw;
            this.width = width;
            this.length = length;
        }

        private void setBranches(List<Branch> branches) {
            this.branches = branches;
        }
    }
}

