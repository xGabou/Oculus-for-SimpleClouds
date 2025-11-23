/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderPassEvent
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam
 *  org.lwjgl.opengl.GL11
 */
package dev.nonamecrackers2.simpleclouds.client.dh.event;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderPassEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import dev.nonamecrackers2.simpleclouds.client.dh.SimpleCloudsDhCompatHandler;
import org.lwjgl.opengl.GL11;

public class SimpleCloudsDhSetupHandler
extends DhApiBeforeRenderPassEvent {
    public void beforeRender(DhApiEventParam<DhApiRenderParam> event) {
        int id = GL11.glGetInteger((int)36006);
        if (id == 0) {
            throw new RuntimeException("Received no binded framebuffer, expected DH's framebuffer");
        }
        SimpleCloudsDhCompatHandler._updateDhFramebufferId(id);
    }
}

