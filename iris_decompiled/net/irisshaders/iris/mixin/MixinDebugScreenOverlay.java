/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.DebugScreenOverlay
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Objects;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={DebugScreenOverlay.class})
public abstract class MixinDebugScreenOverlay {
    @Unique
    private static final List<BufferPoolMXBean> iris$pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
    @Unique
    private static final BufferPoolMXBean iris$directPool;

    @Unique
    private static String iris$humanReadableByteCountBin(long bytes) {
        long absB;
        long l = absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024L) {
            return bytes + " B";
        }
        long value = absB;
        StringCharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xFFFCCCCCCCCCCCCL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        return String.format("%.3f %ciB", (double)(value *= (long)Long.signum(bytes)) / 1024.0, Character.valueOf(ci.current()));
    }

    @Unique
    private static long iris$getNativeMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
    }

    @Inject(method={"getSystemInformation"}, at={@At(value="RETURN")})
    private void iris$appendShaderPackText(CallbackInfoReturnable<List<String>> cir) {
        List messages = (List)cir.getReturnValue();
        messages.add("");
        messages.add("[Oculus] Version: " + Iris.getFormattedVersion());
        messages.add("");
        if (Iris.getIrisConfig().areShadersEnabled()) {
            messages.add("[Oculus] Shaderpack: " + Iris.getCurrentPackName() + (Iris.isFallback() ? " (fallback)" : ""));
            Iris.getCurrentPack().ifPresent(pack -> messages.add("[Oculus] " + pack.getProfileInfo()));
            messages.add("[Oculus] Color space: " + IrisVideoSettings.colorSpace.name());
        } else {
            messages.add("[Oculus] Shaders are disabled");
        }
        messages.add(3, "Direct Buffers: +" + MixinDebugScreenOverlay.iris$humanReadableByteCountBin(iris$directPool.getMemoryUsed()));
    }

    @Inject(method={"getGameInformation"}, at={@At(value="RETURN")})
    private void iris$appendShadowDebugText(CallbackInfoReturnable<List<String>> cir) {
        List messages = (List)cir.getReturnValue();
        Iris.getPipelineManager().getPipeline().ifPresent(pipeline -> pipeline.addDebugText(messages));
    }

    static {
        BufferPoolMXBean found = null;
        for (BufferPoolMXBean pool : iris$pools) {
            if (!pool.getName().equals("direct")) continue;
            found = pool;
            break;
        }
        iris$directPool = Objects.requireNonNull(found);
    }
}

