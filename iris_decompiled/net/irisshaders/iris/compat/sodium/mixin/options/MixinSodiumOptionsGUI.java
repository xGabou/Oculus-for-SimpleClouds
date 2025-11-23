/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI
 *  me.jellysquid.mods.sodium.client.gui.options.OptionPage
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin.options;

import com.google.common.collect.ImmutableList;
import java.util.List;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SodiumOptionsGUI.class})
public class MixinSodiumOptionsGUI
extends Screen {
    @Shadow(remap=false)
    @Final
    private List<OptionPage> pages;
    @Unique
    private OptionPage shaderPacks;

    protected MixinSodiumOptionsGUI(Component title) {
        super(title);
    }

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void iris$onInit(Screen prevScreen, CallbackInfo ci) {
        MutableComponent shaderPacksTranslated = Component.m_237115_((String)"options.iris.shaderPackSelection");
        this.shaderPacks = new OptionPage((Component)shaderPacksTranslated, ImmutableList.of());
        this.pages.add(this.shaderPacks);
    }

    @Inject(method={"setPage"}, at={@At(value="HEAD")}, remap=false, cancellable=true)
    private void iris$onSetPage(OptionPage page, CallbackInfo ci) {
        if (page == this.shaderPacks) {
            this.f_96541_.m_91152_((Screen)new ShaderPackScreen(this));
            ci.cancel();
        }
    }
}

