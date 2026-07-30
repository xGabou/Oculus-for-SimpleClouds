package dev.bettersimpleclouds.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * Shared scaffolding for the mod's options screen: a searchable, scrollable list of option rows grouped under
 * section headers, with a per-row reset button and a global "Reset All". Subclasses only describe their options
 * (via {@link #buildRows(String)} helpers) - all widgetry, search filtering, tooltips and layout live here.
 *
 * <p>Values apply and persist immediately (each row's setter saves the config), matching how the video options
 * behave in Sodium's settings, so there is no OK/cancel juggling.</p>
 */
public abstract class OptionsScreenBase extends Screen {

    /** Height reserved above the list: title line + search bar. */
    private static final int HEADER_HEIGHT = 58;
    /** Height reserved below the list for the button row. */
    private static final int FOOTER_HEIGHT = 33;
    private static final int ROW_WIDTH = 340;
    private static final int CONTROL_WIDTH = 100;
    private static final int RESET_WIDTH = 20;

    protected final Screen parent;
    private EditBox searchBox;
    private OptionsList list;
    private String query = "";
    private boolean pendingRefresh;

    protected OptionsScreenBase(final Component title, final Screen parent) {
        super(title);
        this.parent = parent;
    }

    /** Builds the (already search-filtered) rows shown in the list; called on init and whenever the query changes. */
    protected abstract List<Row> buildRows(String query);

    /** Resets every option this screen owns back to its default (the per-row reset buttons cover single options). */
    protected abstract void resetAllToDefaults();

    @Override
    protected void init() {
        this.searchBox = new EditBox(this.font, this.width / 2 - 110, 30, 220, 18, Component.literal("Search"));
        this.searchBox.setHint(Component.literal("Search options...").withStyle(ChatFormatting.DARK_GRAY));
        this.searchBox.setMaxLength(64);
        this.searchBox.setValue(this.query);
        this.searchBox.setResponder(text -> {
            if (!text.equals(this.query)) {
                this.query = text;
                this.refreshList(true);
            }
        });
        this.addRenderableWidget(this.searchBox);

        this.list = new OptionsList(this.minecraft, this.width,
            this.height - HEADER_HEIGHT - FOOTER_HEIGHT, HEADER_HEIGHT, 24);
        this.addRenderableWidget(this.list);

        final int buttonY = this.height - 27;
        this.addRenderableWidget(Button.builder(Component.literal("Reset All"), b -> {
            this.resetAllToDefaults();
            this.refreshList(false);
        }).bounds(this.width / 2 - 155, buttonY, 150, 20)
            .tooltip(Tooltip.create(Component.literal("Reset every option on this screen to its default value.")))
            .build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
            .bounds(this.width / 2 + 5, buttonY, 150, 20).build());

        this.refreshList(false);
        this.setInitialFocus(this.searchBox);
    }

    /** Rebuilds the list rows for the current query, optionally resetting the scroll position. */
    protected final void refreshList(final boolean resetScroll) {
        final double scroll = resetScroll ? 0.0 : this.list.getScrollAmount();
        this.list.setRows(this.buildRows(this.query));
        this.list.setScrollAmount(scroll);
    }

    /**
     * Requests a rebuild on the next frame. Buttons that live <i>inside</i> the list must use this instead of
     * {@link #refreshList}: their onPress runs while the list's children are being dispatched to, so replacing the
     * entries immediately would mutate the collection mid-iteration.
     */
    protected final void refreshListLater() {
        this.pendingRefresh = true;
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        if (this.pendingRefresh) {
            this.pendingRefresh = false;
            this.refreshList(false);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
    }

    /** @return true if {@code query} matches any of the given texts (every whitespace-separated token must appear). */
    protected static boolean matches(final String query, final String... texts) {
        final String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty())
            return true;
        final StringBuilder all = new StringBuilder();
        for (final String t : texts)
            all.append(t.toLowerCase(Locale.ROOT)).append('\n');
        final String haystack = all.toString();
        for (final String token : q.split("\\s+"))
            if (!haystack.contains(token))
                return false;
        return true;
    }

    // ===================================================== rows =====================================================

    /** Adds a section header plus its option rows, dropping the header when no child matched the query. */
    protected static void addSection(final List<Row> out, final String query, final String title,
                                     final List<Row> children) {
        final List<Row> visible = children.stream().filter(r -> r.matchesQuery(query)).toList();
        if (visible.isEmpty())
            return;
        if (!out.isEmpty())
            out.add(new SpacerRow());
        out.add(new SectionRow(Component.literal(title)));
        out.addAll(visible);
    }

    /** One row of the options list. */
    public abstract static class Row extends ContainerObjectSelectionList.Entry<Row> {
        /** @return whether this row should stay visible for the given search query. */
        protected boolean matchesQuery(final String query) {
            return true;
        }
    }

    /** Thin empty row between sections. */
    protected static final class SpacerRow extends Row {
        @Override
        public void render(final GuiGraphics graphics, final int index, final int top, final int left, final int width,
                           final int height, final int mouseX, final int mouseY, final boolean hovering, final float partialTick) {}

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    /** Yellow centered section header. */
    protected static final class SectionRow extends Row {
        private final Component title;

        SectionRow(final Component title) {
            this.title = title.copy().withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
        }

        @Override
        public void render(final GuiGraphics graphics, final int index, final int top, final int left, final int width,
                           final int height, final int mouseX, final int mouseY, final boolean hovering, final float partialTick) {
            final Minecraft mc = Minecraft.getInstance();
            graphics.drawCenteredString(mc.font, this.title, left + width / 2, top + (height - 9) / 2 + 1, 0xFFFFFF);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    /** Small centered gray note (e.g. pointing at the video settings). Always visible regardless of search. */
    protected static final class InfoRow extends Row {
        private final Component text;

        InfoRow(final String text) {
            this.text = Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        }

        @Override
        public void render(final GuiGraphics graphics, final int index, final int top, final int left, final int width,
                           final int height, final int mouseX, final int mouseY, final boolean hovering, final float partialTick) {
            final Minecraft mc = Minecraft.getInstance();
            graphics.drawCenteredString(mc.font, this.text, left + width / 2, top + (height - 9) / 2 + 1, 0xFFFFFF);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    /**
     * Base for a labelled option row: name on the left, a control + reset button on the right, tooltip (the long
     * option description) when hovering the label. A row constructed with a non-null {@code missingRequirement}
     * renders greyed out with its controls inactive and the tooltip saying which mod is needed.
     */
    protected abstract static class OptionRow extends Row {
        protected final Component label;
        protected final String tooltip;
        protected final boolean disabled;
        private final String searchText;
        private List<net.minecraft.util.FormattedCharSequence> wrappedTooltip;

        protected OptionRow(final String label, final String tooltip, final String sectionForSearch) {
            this(label, tooltip, sectionForSearch, null);
        }

        protected OptionRow(final String label, final String tooltip, final String sectionForSearch,
                            final String missingRequirement) {
            this.disabled = missingRequirement != null;
            this.label = Component.literal(label);
            this.tooltip = this.disabled
                ? "DISABLED - requires " + missingRequirement + ", which isn't installed.\n\n" + tooltip
                : tooltip;
            this.searchText = label + "\n" + tooltip + "\n" + sectionForSearch;
        }

        /** Marks the given control widgets inactive (greyed, non-interactive) when this row is disabled. */
        protected final void applyDisabledState(final AbstractWidget... widgets) {
            if (this.disabled)
                for (final AbstractWidget w : widgets)
                    w.active = false;
        }

        @Override
        protected boolean matchesQuery(final String query) {
            return matches(query, this.searchText);
        }

        /** The widgets on the right edge, ordered left-to-right; laid out and rendered by {@link #render}. */
        protected abstract List<AbstractWidget> controls();

        @Override
        public void render(final GuiGraphics graphics, final int index, final int top, final int left, final int width,
                           final int height, final int mouseX, final int mouseY, final boolean hovering, final float partialTick) {
            final Minecraft mc = Minecraft.getInstance();
            final int textY = top + (height - 9) / 2 + 1;
            graphics.drawString(mc.font, this.label, left + 4, textY, this.disabled ? 0x808080 : 0xFFFFFF);

            int x = left + width;
            final List<AbstractWidget> controls = this.controls();
            for (int i = controls.size() - 1; i >= 0; i--) {
                final AbstractWidget w = controls.get(i);
                x -= w.getWidth() + (i < controls.size() - 1 ? 2 : 0);
                w.setX(x);
                w.setY(top + (height - w.getHeight()) / 2);
                w.render(graphics, mouseX, mouseY, partialTick);
            }

            // Tooltip when hovering the label half of the row (widgets carry their own tooltips).
            if (hovering && !this.tooltip.isEmpty() && mouseX < x - 4 && mc.screen != null) {
                if (this.wrappedTooltip == null)
                    this.wrappedTooltip = mc.font.split(Component.literal(this.tooltip), 260);
                mc.screen.setTooltipForNextRenderPass(this.wrappedTooltip);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.controls();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.controls();
        }
    }

    /** ON/OFF toggle row. */
    protected static final class BoolRow extends OptionRow {
        private final CycleButton<Boolean> button;
        private final Button reset;

        BoolRow(final String label, final String tooltip, final String section,
                final Supplier<Boolean> getter, final Consumer<Boolean> setter, final boolean defaultValue) {
            this(label, tooltip, section, getter, setter, defaultValue, null);
        }

        /** @param missingRequirement non-null = the mods this option needs aren't installed; row renders disabled. */
        BoolRow(final String label, final String tooltip, final String section,
                final Supplier<Boolean> getter, final Consumer<Boolean> setter, final boolean defaultValue,
                final String missingRequirement) {
            super(label, tooltip, section, missingRequirement);
            this.button = CycleButton.onOffBuilder(getter.get())
                .displayOnlyValue()
                .create(0, 0, CONTROL_WIDTH, 20, Component.empty(), (btn, value) -> setter.accept(value));
            this.reset = resetButton(() -> {
                setter.accept(defaultValue);
                this.button.setValue(defaultValue);
            });
            this.applyDisabledState(this.button, this.reset);
        }

        @Override
        protected List<AbstractWidget> controls() {
            return List.of(this.button, this.reset);
        }
    }

    /** Integer slider row (with step rounding and a custom value formatter). */
    protected static final class IntRow extends OptionRow {
        private final IntSlider slider;
        private final Button reset;

        IntRow(final String label, final String tooltip, final String section,
               final IntSupplier getter, final IntConsumer setter, final int defaultValue,
               final int min, final int max, final int step, final IntFunction<Component> formatter) {
            this(label, tooltip, section, getter, setter, defaultValue, min, max, step, formatter, null);
        }

        /** @param missingRequirement non-null = the mods this option needs aren't installed; row renders disabled. */
        IntRow(final String label, final String tooltip, final String section,
               final IntSupplier getter, final IntConsumer setter, final int defaultValue,
               final int min, final int max, final int step, final IntFunction<Component> formatter,
               final String missingRequirement) {
            super(label, tooltip, section, missingRequirement);
            this.slider = new IntSlider(getter.getAsInt(), min, max, step, setter, formatter);
            this.reset = resetButton(() -> this.slider.setIntValue(defaultValue));
            this.applyDisabledState(this.slider, this.reset);
        }

        @Override
        protected List<AbstractWidget> controls() {
            return List.of(this.slider, this.reset);
        }
    }

    /** The standard little "reset to default" button used by every option row. */
    protected static Button resetButton(final Runnable action) {
        return Button.builder(Component.literal("↺"), b -> action.run())
            .bounds(0, 0, RESET_WIDTH, 20)
            .tooltip(Tooltip.create(Component.literal("Reset to default")))
            .build();
    }

    /** Vanilla slider bound to an int option; applies + persists on drag/release. */
    protected static final class IntSlider extends AbstractSliderButton {
        private final int min;
        private final int max;
        private final int step;
        private final IntConsumer setter;
        private final IntFunction<Component> formatter;

        IntSlider(final int initial, final int min, final int max, final int step,
                  final IntConsumer setter, final IntFunction<Component> formatter) {
            super(0, 0, CONTROL_WIDTH, 20, Component.empty(), (double) (initial - min) / (max - min));
            this.min = min;
            this.max = max;
            this.step = Math.max(1, step);
            this.setter = setter;
            this.formatter = formatter;
            this.updateMessage();
        }

        private int intValue() {
            final int raw = this.min + (int) Math.round(this.value * (this.max - this.min));
            final int stepped = this.min + Math.round((float) (raw - this.min) / this.step) * this.step;
            return Math.max(this.min, Math.min(this.max, stepped));
        }

        void setIntValue(final int v) {
            this.value = (double) (Math.max(this.min, Math.min(this.max, v)) - this.min) / (this.max - this.min);
            this.applyValue();
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(this.formatter.apply(this.intValue()));
        }

        @Override
        protected void applyValue() {
            this.setter.accept(this.intValue());
        }
    }

    /** The scrollable option list itself. */
    protected final class OptionsList extends ContainerObjectSelectionList<Row> {
        OptionsList(final Minecraft minecraft, final int width, final int height, final int y, final int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        void setRows(final List<Row> rows) {
            this.replaceEntries(rows);
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + ROW_WIDTH / 2 + 10;
        }
    }

    /** Convenience for subclasses assembling row lists. */
    protected static List<Row> rows(final Row... rows) {
        return new ArrayList<>(List.of(rows));
    }
}
