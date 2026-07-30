package dev.bettersimpleclouds.immersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;

/**
 * Scattered, translucent "motes" that drift around the camera while it is inside a Simple Clouds cloud - the only
 * thing we draw inside; the cloud's own geometry provides the visible structure. The count and turbulence scale with
 * the cloud's storminess, so big storm clouds are busy and messy inside while small calm clouds have none at all.
 *
 * <p>Self-managed (spawn / drift / recycle) rather than vanilla particles, so density, lifetime and motion can track
 * the live cloud state every tick. Drawn through the vanilla particle shader (Iris/Sodium friendly), camera-facing,
 * depth-tested so the real cloud and world occlude them.</p>
 */
public final class InCloudParticles {

    private static final ResourceLocation MOTE_TEX =
        ResourceLocation.fromNamespaceAndPath("bettersimpleclouds", "textures/environment/cloud_mote.png");

    /** Mote count at full envelopment in the stormiest cloud (before the config multiplier). */
    private static final int MAX_MOTES = 360;
    /** Radius (blocks) of the sphere around the camera that motes occupy. */
    private static final float RADIUS = 14.0F;
    private static final float MIN_SPAWN_DIST = 1.5F;

    private final List<Mote> motes = new ArrayList<>();
    private final Random rng = new Random();

    /** One drifting speck of cloud. World-anchored so it streams past the camera with the wind. */
    private static final class Mote {
        double x, y, z, px, py, pz;
        float vx, vy, vz;
        float size, r, g, b, alpha;
        int age, life;
    }

    /** Advance the simulation once per client tick. */
    public void tick(final float envelopment, final float storminess) {
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        if (level == null) {
            this.motes.clear();
            return;
        }

        final float density = Mth.clamp(envelopment * storminess * BetterSimpleCloudsConfig.inCloudDensity(), 0.0F, 2.0F);
        final int target = Math.round(density * MAX_MOTES);
        // Common case (not in a cloud, nothing left to fade out): bail before any allocation or Simple Clouds lookup.
        if (target <= 0 && this.motes.isEmpty())
            return;

        final SimpleCloudsRenderer renderer = SimpleCloudsRenderer.getOptionalInstance().orElse(null);
        if (renderer == null) {
            this.motes.clear();
            return;
        }

        final Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        final Vector2f wind = CloudManager.get(level).calculateWindDirection();
        // Messier (faster, more turbulent) the stormier it is.
        final float windScale = 0.04F + 0.06F * storminess;
        final float turbulence = 0.004F + 0.05F * storminess;
        final float[] cloud = cloudColor(renderer.getCloudColor(1.0F));

        // Drift + age existing motes; recycle ones that die or leave the sphere.
        for (int i = this.motes.size() - 1; i >= 0; i--) {
            final Mote m = this.motes.get(i);
            m.px = m.x; m.py = m.y; m.pz = m.z;
            m.vx += (this.rng.nextFloat() - 0.5F) * turbulence;
            m.vy += (this.rng.nextFloat() - 0.5F) * turbulence * 0.6F;
            m.vz += (this.rng.nextFloat() - 0.5F) * turbulence;
            m.vx *= 0.92F; m.vy *= 0.92F; m.vz *= 0.92F; // damp so turbulence stays gentle
            m.x += m.vx + wind.x * windScale;
            m.y += m.vy;
            m.z += m.vz + wind.y * windScale;
            m.age++;
            final double dx = m.x - cam.x, dy = m.y - cam.y, dz = m.z - cam.z;
            if (m.age > m.life || dx * dx + dy * dy + dz * dz > (RADIUS * 1.3) * (RADIUS * 1.3)) {
                this.motes.remove(i);
            }
        }
        // Trim down quickly when density drops (e.g. leaving the cloud).
        while (this.motes.size() > target && !this.motes.isEmpty()) {
            this.motes.remove(this.motes.size() - 1);
        }
        // Spawn up to target.
        int toSpawn = Math.min(target - this.motes.size(), 24); // cap per-tick so it eases in
        while (toSpawn-- > 0) {
            this.motes.add(spawn(cam, cloud, storminess));
        }
    }

    private Mote spawn(final Vec3 cam, final float[] cloud, final float storminess) {
        final Mote m = new Mote();
        // Random point in the sphere shell [MIN_SPAWN_DIST, RADIUS].
        final Vector3f dir = new Vector3f(
            this.rng.nextFloat() * 2 - 1, this.rng.nextFloat() * 2 - 1, this.rng.nextFloat() * 2 - 1);
        if (dir.lengthSquared() < 1.0E-4F)
            dir.set(0, 1, 0);
        dir.normalize();
        final float dist = MIN_SPAWN_DIST + this.rng.nextFloat() * (RADIUS - MIN_SPAWN_DIST);
        m.x = cam.x + dir.x * dist;
        m.y = cam.y + dir.y * dist;
        m.z = cam.z + dir.z * dist;
        m.px = m.x; m.py = m.y; m.pz = m.z;
        // Bigger, wispier, more varied motes in storms; small fine specks in calmer cloud.
        m.size = 0.10F + this.rng.nextFloat() * (0.15F + 0.45F * storminess);
        final float shade = 0.85F + this.rng.nextFloat() * 0.15F;
        m.r = cloud[0] * shade;
        m.g = cloud[1] * shade;
        m.b = cloud[2] * shade;
        m.alpha = 0.06F + this.rng.nextFloat() * (0.10F + 0.16F * storminess); // "clear" - low opacity
        m.life = 50 + this.rng.nextInt(70);
        return m;
    }

    public void reset() {
        this.motes.clear();
    }

    /** Render all motes as camera-facing billboards. */
    public void render(final Matrix4f poseMatrix, final float partialTick, final float envelopment) {
        if (this.motes.isEmpty())
            return;
        final Minecraft mc = Minecraft.getInstance();
        final Camera camera = mc.gameRenderer.getMainCamera();
        final Vec3 cam = camera.getPosition();

        // Camera basis for billboarding (face the viewer).
        final Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
        final Vector3f up = new Vector3f(camera.getUpVector());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderTexture(0, MOTE_TEX);

        final BufferBuilder builder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
        for (final Mote m : this.motes) {
            final float ix = (float) (Mth.lerp(partialTick, m.px, m.x) - cam.x);
            final float iy = (float) (Mth.lerp(partialTick, m.py, m.y) - cam.y);
            final float iz = (float) (Mth.lerp(partialTick, m.pz, m.z) - cam.z);
            final float lifeFade = lifeFade(m.age + partialTick, m.life);
            final float a = Mth.clamp(m.alpha * lifeFade * envelopment, 0.0F, 1.0F);
            if (a <= 0.002F)
                continue;
            final float hx = m.size, hy = m.size;
            quad(builder, poseMatrix, ix, iy, iz, right, up, hx, hy, m.r, m.g, m.b, a);
        }
        final MeshData mesh = builder.build();
        if (mesh != null)
            BufferUploader.drawWithShader(mesh);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static void quad(final BufferBuilder b, final Matrix4f mat, final float cx, final float cy, final float cz,
                             final Vector3f right, final Vector3f up, final float hx, final float hy,
                             final float r, final float g, final float bl, final float a) {
        final float rx = right.x * hx, ry = right.y * hx, rz = right.z * hx;
        final float ux = up.x * hy, uy = up.y * hy, uz = up.z * hy;
        corner(b, mat, cx - rx - ux, cy - ry - uy, cz - rz - uz, 0.0F, 0.0F, r, g, bl, a);
        corner(b, mat, cx - rx + ux, cy - ry + uy, cz - rz + uz, 0.0F, 1.0F, r, g, bl, a);
        corner(b, mat, cx + rx + ux, cy + ry + uy, cz + rz + uz, 1.0F, 1.0F, r, g, bl, a);
        corner(b, mat, cx + rx - ux, cy + ry - uy, cz + rz - uz, 1.0F, 0.0F, r, g, bl, a);
    }

    private static void corner(final BufferBuilder b, final Matrix4f mat, final float x, final float y, final float z,
                               final float u, final float v, final float r, final float g, final float bl,
                               final float a) {
        b.addVertex(mat, x, y, z).setUv(u, v).setColor(r, g, bl, a).setLight(0x00F000F0);
    }

    /** Smooth fade in over the first ~10 ticks of life and out over the last ~15. */
    private static float lifeFade(final float age, final int life) {
        final float in = Mth.clamp(age / 10.0F, 0.0F, 1.0F);
        final float out = Mth.clamp((life - age) / 15.0F, 0.0F, 1.0F);
        return Math.min(in, out);
    }

    /** Lift the (possibly near-black at night) cloud colour off pure black so motes stay faintly visible. */
    private static float[] cloudColor(final float[] c) {
        final float floor = 0.30F;
        return new float[] {
            floor + (1.0F - floor) * c[0],
            floor + (1.0F - floor) * c[1],
            floor + (1.0F - floor) * c[2],
        };
    }
}
