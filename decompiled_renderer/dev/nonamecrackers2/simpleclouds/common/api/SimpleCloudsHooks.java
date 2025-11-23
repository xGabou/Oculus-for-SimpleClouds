/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.nonamecrackers2.simpleclouds.api.common.ScAPIHooks
 */
package dev.nonamecrackers2.simpleclouds.common.api;

import dev.nonamecrackers2.simpleclouds.api.common.ScAPIHooks;

public class SimpleCloudsHooks
implements ScAPIHooks {
    private boolean externalWeatherControl;

    public void setExternalWeatherControl(boolean control) {
        this.externalWeatherControl = control;
    }

    public boolean isExternalWeatherControlEnabled() {
        return this.externalWeatherControl;
    }
}

