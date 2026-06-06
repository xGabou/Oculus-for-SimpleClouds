package net.Gabou.oculus_for_simpleclouds.auth;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
final class LauncherBlockedScreen extends Screen {
    private static final Component TITLE = Component.literal("Unsupported launcher detected");
    private final Component reason;

    LauncherBlockedScreen(String reason) {
        super(TITLE);
        this.reason = Component.literal(reason == null || reason.isBlank() ? "unknown reason" : reason);
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.addRenderableWidget(
                Button.builder(Component.literal("Quit Game"), button -> Minecraft.getInstance().close())
                        .bounds(this.width / 2 - 100, this.height - 44, 200, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        RenderSystem.enableBlend();

        int centerX = this.width / 2;
        int y = 48;
        graphics.drawCenteredString(this.font, this.title, centerX, y, 0xFFFFFF);
        y += 28;

        List<FormattedCharSequence> lines = this.font.split(
                Component.literal("This client has been blocked because a suspicious launcher was detected."),
                this.width - 80
        );
        for (FormattedCharSequence line : lines) {
            graphics.drawCenteredString(this.font, line, centerX, y, 0xE0E0E0);
            y += this.font.lineHeight + 2;
        }

        y += 8;
        graphics.drawCenteredString(this.font, this.reason, centerX, y, 0xFF8080);
        y += this.font.lineHeight + 12;
        graphics.drawCenteredString(this.font, "Close the game to continue.", centerX, y, 0xA0A0A0);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            minecraft.setScreen(this);
        }
    }
}
