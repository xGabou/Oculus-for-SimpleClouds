/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue$Consumer
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 */
package dev.nonamecrackers2.simpleclouds.common.data;

import com.google.common.collect.ImmutableList;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class DualPathProvider
implements DataProvider {
    protected final List<PackOutput.PathProvider> paths;

    public DualPathProvider(PackOutput output, String loc) {
        this.paths = ImmutableList.of((Object)output.m_245269_(PackOutput.Target.RESOURCE_PACK, loc), (Object)output.m_245269_(PackOutput.Target.DATA_PACK, loc));
    }

    protected final void jsonForPaths(ResourceLocation id, MessagePassingQueue.Consumer<Path> consumer) {
        this.paths.forEach(p -> consumer.accept((Object)p.m_245731_(id)));
    }
}

