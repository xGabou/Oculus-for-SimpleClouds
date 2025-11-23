/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  nonamecrackers2.crackerslib.common.compat.CompatHelper
 */
package dev.nonamecrackers2.simpleclouds.client.vivecraft;

import dev.nonamecrackers2.simpleclouds.client.renderer.SimpleCloudsRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;

public class SimpleCloudsReloadVivecraftCompatWrapper
implements ResourceManagerReloadListener {
    private final SimpleCloudsRenderer renderer;

    public SimpleCloudsReloadVivecraftCompatWrapper(SimpleCloudsRenderer renderer) {
        this.renderer = renderer;
    }

    public void m_6213_(ResourceManager manager) {
        if (!CompatHelper.isVrActive()) {
            this.renderer.m_6213_(manager);
        }
    }
}

