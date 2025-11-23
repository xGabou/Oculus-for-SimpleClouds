/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.uniforms;

import java.util.OptionalLong;
import java.util.function.IntSupplier;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;

public final class SystemTimeUniforms {
    public static final Timer TIMER = new Timer();
    public static final FrameCounter COUNTER = new FrameCounter();

    private SystemTimeUniforms() {
    }

    public static void addSystemTimeUniforms(UniformHolder uniforms) {
        uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "frameCounter", COUNTER).uniform1f(UniformUpdateFrequency.PER_FRAME, "frameTime", TIMER::getLastFrameTime).uniform1f(UniformUpdateFrequency.PER_FRAME, "frameTimeCounter", TIMER::getFrameTimeCounter);
    }

    public static class FrameCounter
    implements IntSupplier {
        private int count = 0;

        private FrameCounter() {
        }

        @Override
        public int getAsInt() {
            return this.count;
        }

        public void beginFrame() {
            this.count = (this.count + 1) % 720720;
        }

        public void reset() {
            this.count = 0;
        }
    }

    public static final class Timer {
        private float frameTimeCounter;
        private float lastFrameTime;
        private OptionalLong lastStartTime;

        public Timer() {
            this.reset();
        }

        public void beginFrame(long frameStartTime) {
            long diffNs = frameStartTime - this.lastStartTime.orElse(frameStartTime);
            long diffMs = diffNs / 1000L / 1000L;
            this.lastFrameTime = (float)diffMs / 1000.0f;
            this.frameTimeCounter += this.lastFrameTime;
            if (this.frameTimeCounter >= 3600.0f) {
                this.frameTimeCounter = 0.0f;
            }
            this.lastStartTime = OptionalLong.of(frameStartTime);
        }

        public float getFrameTimeCounter() {
            return this.frameTimeCounter;
        }

        public float getLastFrameTime() {
            return this.lastFrameTime;
        }

        public void reset() {
            this.frameTimeCounter = 0.0f;
            this.lastFrameTime = 0.0f;
            this.lastStartTime = OptionalLong.empty();
        }
    }
}

