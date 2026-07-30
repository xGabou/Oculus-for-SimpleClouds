package dev.bettersimpleclouds.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import dev.bettersimpleclouds.core.BetterSimpleCloudsConfig;
import dev.bettersimpleclouds.core.ModIds;
import dev.bettersimpleclouds.core.RequiredModsMixinPlugin;
import dev.bettersimpleclouds.spawning.BlockedCloudTypes;
import dev.bettersimpleclouds.spawning.KnownCloudTypes;

/**
 * Better Simple Clouds' options screen (Mods &rarr; Better Simple Clouds &rarr; Config): the cloud-type spawn
 * <b>blacklist</b> - every known cloud type with a Blocked/Allowed toggle, plus a field to block any custom id -
 * and the non-video extras (debug overlay). All the video/visual options deliberately live in Sodium's video
 * settings instead (Reese's Sodium Options shows them under "Better Simple Clouds"), so nothing is configured in
 * two places.
 */
public final class BscOptionsScreen extends OptionsScreenBase {

    private static final String BLACKLIST_SECTION = "Cloud Spawning - Blacklist";

    public BscOptionsScreen(final Screen parent) {
        super(Component.literal("Better Simple Clouds"), parent);
    }

    @Override
    protected List<Row> buildRows(final String query) {
        final List<Row> out = new ArrayList<>();
        if (query.isBlank())
            out.add(new InfoRow("Video & visual options live in Video Settings under \"Better Simple Clouds\"."));

        final List<Row> blacklist = new ArrayList<>();
        for (final ResourceLocation id : KnownCloudTypes.all())
            blacklist.add(new CloudTypeRow(id));
        blacklist.add(new AddTypeRow(this));
        addSection(out, query, BLACKLIST_SECTION, blacklist);

        addSection(out, query, "ReTerraForged", rows(
            new BoolRow("Guard Early Biome Sampling",
                "Guard against a server-start crash where another mod (e.g. Project Atmosphere) samples biomes "
                    + "before ReTerraForged's generator context is initialized (a NullPointerException in "
                    + "ReTerraForged's CellSampler). With this on, those premature samples return a neutral value "
                    + "instead of crashing; ReTerraForged works normally once worldgen is initialized. Leave on.",
                "ReTerraForged worldgen crash guard biome sampling",
                BetterSimpleCloudsConfig::reterraforgedGuardEarlySampling,
                BetterSimpleCloudsConfig::setReterraforgedGuardEarlySampling,
                true,
                RequiredModsMixinPlugin.allPresent(ModIds.RETERRAFORGED) ? null : "ReTerraForged")));

        addSection(out, query, "Debug", rows(
            new BoolRow("In-Cloud Debug Overlay",
                "Show a small diagnostic line at the top-left while in a world: the current envelopment and "
                    + "storminess, your camera Y, the detected cloud layer and the cloud type at the camera. "
                    + "Handy for finding clouds; leave off otherwise.",
                "Debug",
                BetterSimpleCloudsConfig::inCloudDebugOverlay,
                BetterSimpleCloudsConfig::setInCloudDebugOverlay,
                false)));
        return out;
    }

    @Override
    protected void resetAllToDefaults() {
        BetterSimpleCloudsConfig.setBlockedCloudTypeIds(BetterSimpleCloudsConfig.DEFAULT_BLOCKED_CLOUD_TYPES);
        BetterSimpleCloudsConfig.setReterraforgedGuardEarlySampling(true);
        BetterSimpleCloudsConfig.setInCloudDebugOverlay(false);
    }

    /** Adds or removes {@code id} on the persisted blacklist. */
    private static void setBlocked(final ResourceLocation id, final boolean blocked) {
        final List<String> current = new ArrayList<>(BetterSimpleCloudsConfig.blockedCloudTypeIds());
        final String key = id.toString();
        if (blocked && !current.contains(key))
            current.add(key);
        else if (!blocked)
            current.remove(key);
        BetterSimpleCloudsConfig.setBlockedCloudTypeIds(current);
    }

    /** One known cloud type with a Blocked/Allowed toggle. */
    private static final class CloudTypeRow extends OptionRow {
        private final CycleButton<Boolean> toggle;

        CloudTypeRow(final ResourceLocation id) {
            super(id.toString(),
                "Whether '" + id + "' clouds are blocked from ever spawning. Blocking re-rolls natural spawns to "
                    + "another type (cloud count unchanged), refuses the type from any source including other mods, "
                    + "and sweeps away existing ones within ~2 seconds. Applies immediately.",
                BLACKLIST_SECTION + " blocked allowed cloud type");
            this.toggle = CycleButton.booleanBuilder(
                    Component.literal("Blocked").withStyle(ChatFormatting.RED),
                    Component.literal("Allowed").withStyle(ChatFormatting.GREEN))
                .withInitialValue(BlockedCloudTypes.isBlocked(id))
                .displayOnlyValue()
                .create(0, 0, 100, 20, Component.empty(), (btn, blocked) -> setBlocked(id, blocked));
        }

        @Override
        protected List<AbstractWidget> controls() {
            return List.of(this.toggle);
        }
    }

    /** Free-form "block any id" row: an EditBox plus a Block button (for datapack/other-mod cloud types). */
    private static final class AddTypeRow extends Row {
        private final EditBox input;
        private final Button block;

        AddTypeRow(final BscOptionsScreen screen) {
            final Minecraft mc = Minecraft.getInstance();
            this.input = new EditBox(mc.font, 0, 0, 220, 18, Component.literal("Custom cloud type id"));
            this.input.setHint(Component.literal("namespace:cloud_type").withStyle(ChatFormatting.DARK_GRAY));
            this.input.setMaxLength(200);
            this.block = Button.builder(Component.literal("Block"), b -> {
                    final ResourceLocation id = ResourceLocation.tryParse(this.input.getValue().trim());
                    if (id != null) {
                        setBlocked(id, true);
                        this.input.setValue("");
                        screen.refreshListLater();
                    }
                })
                .bounds(0, 0, 100, 20)
                .tooltip(Tooltip.create(Component.literal(
                    "Block a cloud type that isn't listed above (e.g. from a datapack or another mod), by id.")))
                .build();
        }

        @Override
        protected boolean matchesQuery(final String query) {
            return matches(query, "block custom cloud type id add " + BLACKLIST_SECTION);
        }

        @Override
        public void render(final GuiGraphics graphics, final int index, final int top, final int left, final int width,
                           final int height, final int mouseX, final int mouseY, final boolean hovering, final float partialTick) {
            this.input.setX(left + 4);
            this.input.setY(top + (height - 18) / 2);
            this.input.render(graphics, mouseX, mouseY, partialTick);
            this.block.setX(left + width - this.block.getWidth());
            this.block.setY(top + (height - 20) / 2);
            this.block.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.input, this.block);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.input, this.block);
        }
    }
}
