package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.network.packet.SetTrackedQuestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QuestJournalScreen extends Screen {
    private JournalTab tab = JournalTab.JOURNAL;
    private static final int RACES_PER_PAGE = 5;
    private int selected;
    private int racePage;

    public QuestJournalScreen() {
        super(Component.literal("Adventurer's Journal"));
    }

    @Override
    protected void init() {
        super.init();
        playPageTurn(0.95F);
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        GuiTheme.backdrop(
            graphics,
            width,
            height
        );

        Layout layout = layout();

        GuiTheme.book(
            graphics,
            layout.bookX(),
            layout.bookY(),
            layout.bookWidth(),
            layout.bookHeight()
        );

        drawTabs(
            graphics,
            layout,
            mouseX,
            mouseY
        );

        if (tab == JournalTab.RACES) {
            drawRaceGuide(graphics, layout);
        } else {
        List<JournalEntry> entries =
            visibleEntries();

        if (entries.isEmpty()) {
            selected = 0;
            drawEmptyPage(
                graphics,
                layout
            );
        } else {
            selected = Math.max(
                0,
                Math.min(
                    selected,
                    entries.size() - 1
                )
            );

            drawEntryList(
                graphics,
                layout,
                entries,
                mouseX,
                mouseY
            );

            drawEntryDetails(
                graphics,
                layout,
                entries.get(selected),
                mouseX,
                mouseY
            );
        }
        }

        super.render(
            graphics,
            mouseX,
            mouseY,
            partialTick
        );
    }

    @Override
    public boolean keyPressed(
        int keyCode,
        int scanCode,
        int modifiers
    ) {
        if (ClientKeyMappings.QUEST_JOURNAL
                .matches(keyCode, scanCode)) {
            onClose();
            return true;
        }

        return super.keyPressed(
            keyCode,
            scanCode,
            modifiers
        );
    }

    @Override
    public boolean mouseClicked(
        double mouseX,
        double mouseY,
        int button
    ) {
        if (button != 0) {
            return super.mouseClicked(
                mouseX,
                mouseY,
                button
            );
        }

        Layout layout = layout();

        for (int i = 0;
                i < JournalTab.values().length;
                i++) {
            JournalTab value =
                JournalTab.values()[i];

            Rect rect =
                tabRect(layout, i);

            if (rect.contains(
                    mouseX,
                    mouseY
            )) {
                tab = value;
                selected = 0;
                racePage = 0;
                playPageTurn(1.0F);
                return true;
            }
        }

        if (tab == JournalTab.RACES) {
            return clickRaceGuide(layout, mouseX, mouseY);
        }

        List<JournalEntry> entries =
            visibleEntries();

        int rowY = layout.pageTop() + 27;
        int rowHeight = 27;

        for (int i = 0;
                i < entries.size();
                i++) {
            if (rowY + rowHeight
                    > layout.pageBottom() - 6) {
                break;
            }

            if (mouseX >= layout.leftPageX()
                    && mouseX
                        < layout.leftPageRight()
                    && mouseY >= rowY
                    && mouseY
                        < rowY + rowHeight - 2) {
                selected = i;
                playPageTurn(1.08F);
                return true;
            }

            rowY += rowHeight;
        }

        if (!entries.isEmpty()) {
            JournalEntry entry =
                entries.get(
                    Math.max(
                        0,
                        Math.min(
                            selected,
                            entries.size() - 1
                        )
                    )
                );

            if (!"COMPLETED".equals(
                    entry.status()
            )) {
                Rect track =
                    trackButton(layout);

                if (track.contains(
                        mouseX,
                        mouseY
                )) {
                    Minecraft.getInstance()
                        .getSoundManager()
                        .play(
                            SimpleSoundInstance.forUI(
                                SoundEvents.UI_BUTTON_CLICK,
                                1.0F
                            )
                        );
                    QuestNetwork.sendToServer(
                        new SetTrackedQuestPacket(
                            entry.tracked()
                                ? ""
                                : entry.questId()
                        )
                    );
                    return true;
                }
            }
        }

        return super.mouseClicked(
            mouseX,
            mouseY,
            button
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** Fourth parchment tab: browsable base races and acquired Undead. */
    private void drawRaceGuide(GuiGraphics graphics, Layout layout) {
        List<RaceJournalData.Entry> races = RaceJournalData.entries();
        if (races.isEmpty()) {
            drawEmptyPage(graphics, layout);
            return;
        }
        int pages = (races.size() + RACES_PER_PAGE - 1) / RACES_PER_PAGE;
        racePage = Math.max(0, Math.min(racePage, pages - 1));
        selected = Math.max(0, Math.min(selected, races.size() - 1));
        int first = racePage * RACES_PER_PAGE;

        graphics.drawString(font, Component.literal("Races"),
                layout.leftPageX(), layout.pageTop(), GuiTheme.WAX, false);
        graphics.fill(layout.leftPageX(), layout.pageTop() + 11,
                layout.leftPageRight(), layout.pageTop() + 12, 0x665D4228);

        int y = layout.pageTop() + 27;
        for (int i = first; i < Math.min(first + RACES_PER_PAGE, races.size()); i++) {
            RaceJournalData.Entry race = races.get(i);
            GuiTheme.entry(graphics, layout.leftPageX(), y,
                    layout.leftPageWidth(), 25, i == selected);
            graphics.drawString(font, Component.literal(
                    trimToWidth(race.name(), layout.leftPageWidth() - 18)),
                    layout.leftPageX() + 7, y + 3, GuiTheme.INK, false);
            graphics.drawString(font, Component.literal(race.status()),
                    layout.leftPageX() + 7, y + 13, GuiTheme.INK_MUTED, false);
            y += 27;
        }

        int controlsY = layout.pageBottom() - 16;
        if (racePage > 0) {
            graphics.drawString(font, Component.literal("< Prev"),
                    layout.leftPageX(), controlsY, GuiTheme.WAX, false);
        }
        graphics.drawString(font, Component.literal(
                (racePage + 1) + " / " + pages),
                layout.leftPageX() + layout.leftPageWidth() / 2 - 12,
                controlsY, GuiTheme.INK_MUTED, false);
        if (racePage + 1 < pages) {
            graphics.drawString(font, Component.literal("Next >"),
                    layout.leftPageRight() - 38,
                    controlsY, GuiTheme.WAX, false);
        }

        RaceJournalData.Entry race = races.get(selected);
        int x = layout.rightPageX();
        int right = layout.rightPageRight();
        int ty = layout.pageTop();
        int width = layout.rightPageWidth();
        graphics.drawString(font, Component.literal(race.name()),
                x, ty, GuiTheme.INK, false);
        ty += 15;
        ty = drawWrapped(graphics, race.status(), x, ty, width,
                GuiTheme.GOLD, 2);
        ty += 5;
        ty = drawWrapped(graphics, race.identity(), x, ty, width,
                GuiTheme.INK_MUTED, 4);
        ty += 4;
        ty = sectionTitle(graphics, "Traits", x, ty, right);
        ty = drawWrapped(graphics, race.traits(), x, ty, width,
                GuiTheme.INK, 5);
        ty += 3;
        ty = sectionTitle(graphics, "Ability", x, ty, right);
        ty = drawWrapped(graphics, race.active(), x, ty, width,
                GuiTheme.INK, 4);
        if (ty < layout.pageBottom() - 36) {
            ty += 4;
            ty = sectionTitle(graphics, "Weakness", x, ty, right);
            drawWrapped(graphics, race.caution(), x, ty, width,
                    GuiTheme.INK_MUTED, 3);
        }
    }

    private boolean clickRaceGuide(Layout layout, double x, double y) {
        List<RaceJournalData.Entry> races = RaceJournalData.entries();
        if (races.isEmpty()) return true;

        int pages = (races.size() + RACES_PER_PAGE - 1) / RACES_PER_PAGE;
        int bottomY = layout.pageBottom() - 20;
        if (y >= bottomY && y < layout.pageBottom()) {
            if (x >= layout.leftPageX()
                    && x <= layout.leftPageX() + 42 && racePage > 0) {
                racePage--;
                selected = racePage * RACES_PER_PAGE;
                playPageTurn(0.95F);
                return true;
            }
            if (x >= layout.leftPageRight() - 44
                    && x <= layout.leftPageRight() && racePage + 1 < pages) {
                racePage++;
                selected = racePage * RACES_PER_PAGE;
                playPageTurn(1.05F);
                return true;
            }
        }

        int rowY = layout.pageTop() + 27;
        int first = racePage * RACES_PER_PAGE;
        for (int i = first; i < Math.min(first + RACES_PER_PAGE, races.size()); i++) {
            if (x >= layout.leftPageX() && x < layout.leftPageRight()
                    && y >= rowY && y < rowY + 25) {
                selected = i;
                playPageTurn(1.0F);
                return true;
            }
            rowY += 27;
        }
        return true;
    }

    private void drawTabs(
        GuiGraphics graphics,
        Layout layout,
        int mouseX,
        int mouseY
    ) {
        JournalTab[] tabs =
            JournalTab.values();

        for (int i = 0; i < tabs.length; i++) {
            Rect rect =
                tabRect(layout, i);

            boolean selectedTab =
                tabs[i] == tab;

            GuiTheme.tab(
                graphics,
                rect.left(),
                rect.top(),
                rect.width(),
                rect.height(),
                selectedTab
            );

            int color = selectedTab
                ? 0xFFE8CA75
                : 0xFFE6D2A4;

            GuiTheme.drawCenteredNoShadow(
                graphics,
                font,
                Component.literal(
                    tabs[i].label
                ),
                rect.centerX(),
                rect.top() + 7,
                color
            );
        }
    }

    private void drawEntryList(
        GuiGraphics graphics,
        Layout layout,
        List<JournalEntry> entries,
        int mouseX,
        int mouseY
    ) {
        graphics.drawString(
            font,
            Component.literal(tab.heading),
            layout.leftPageX(),
            layout.pageTop(),
            GuiTheme.WAX,
            false
        );

        graphics.fill(
            layout.leftPageX(),
            layout.pageTop() + 11,
            layout.leftPageRight(),
            layout.pageTop() + 12,
            0x665D4228
        );

        int y = layout.pageTop() + 27;
        int rowHeight = 27;

        for (int i = 0;
                i < entries.size();
                i++) {
            if (y + rowHeight
                    > layout.pageBottom() - 6) {
                break;
            }

            JournalEntry entry =
                entries.get(i);
            boolean current =
                i == selected;

            GuiTheme.entry(
                graphics,
                layout.leftPageX(),
                y,
                layout.leftPageWidth(),
                25,
                current
            );

            String marker = switch (
                entry.category().toLowerCase(
                    Locale.ROOT
                )
            ) {
                case "story" -> "◆";
                case "contract" -> "§";
                case "rumour", "rumor" -> "?";
                default -> "•";
            };

            graphics.drawString(
                font,
                Component.literal(marker),
                layout.leftPageX() + 7,
                y + 5,
                entry.tracked()
                    ? GuiTheme.GOLD
                    : GuiTheme.WAX,
                false
            );

            String title =
                trimToWidth(
                    entry.title(),
                    layout.leftPageWidth() - 34
                );

            graphics.drawString(
                font,
                Component.literal(title),
                layout.leftPageX() + 18,
                y + 3,
                GuiTheme.INK,
                false
            );

            graphics.drawString(
                font,
                Component.literal(
                    entry.tracked()
                        ? "tracked"
                        : statusText(entry)
                ),
                layout.leftPageX() + 18,
                y + 13,
                GuiTheme.INK_MUTED,
                false
            );

            y += rowHeight;
        }
    }

    private void drawEntryDetails(
        GuiGraphics graphics,
        Layout layout,
        JournalEntry entry,
        int mouseX,
        int mouseY
    ) {
        int x = layout.rightPageX();
        int right =
            layout.rightPageRight();
        int textWidth =
            layout.rightPageWidth();
        int y = layout.pageTop();

        graphics.drawString(
            font,
            Component.literal(
                trimToWidth(
                    entry.title(),
                    textWidth
                )
            ),
            x,
            y,
            GuiTheme.INK,
            false
        );
        y += 12;

        if (!entry.timer().isBlank()) {
            graphics.drawString(
                font,
                Component.literal(entry.timer()),
                x,
                y,
                entry.timer().startsWith("Expired")
                    ? 0xFF9A2E27
                    : GuiTheme.GOLD,
                false
            );
            y += 11;
        }

        String category =
            entry.category().isBlank()
                ? "Thread"
                : titleCase(
                    entry.category()
                );

        String stage =
            entry.stageTitle().isBlank()
                ? ""
                : " — "
                    + entry.stageTitle();

        String stageCount =
            entry.stageCount() > 1
                && !"COMPLETED".equals(
                    entry.status()
                )
                ? "  "
                    + entry.stageNumber()
                    + "/"
                    + entry.stageCount()
                : "";

        graphics.drawString(
            font,
            Component.literal(
                trimToWidth(
                    category
                        + stage
                        + stageCount,
                    textWidth
                )
            ),
            x,
            y,
            GuiTheme.WAX,
            false
        );
        y += 14;

        y = drawWrapped(
            graphics,
            entry.description(),
            x,
            y,
            textWidth,
            GuiTheme.INK_MUTED,
            4
        );

        if ("COMPLETED".equals(
                entry.status()
        )) {
            y += 6;
            y = sectionTitle(
                graphics,
                "Chronicle",
                x,
                y,
                right
            );

            String resolution =
                entry.completionText().isBlank()
                    ? "Quest completed."
                    : entry.completionText();

            drawWrapped(
                graphics,
                resolution,
                x,
                y,
                textWidth,
                GuiTheme.INK,
                8
            );
            return;
        }

        y += 5;
        y = sectionTitle(
            graphics,
            "What We Know",
            x,
            y,
            right
        );

        List<String> knowledge =
            new ArrayList<>(
                entry.notes()
            );

        if (knowledge.isEmpty()
                && entry.objectives().isEmpty()) {
            knowledge.add(
                "No details yet."
            );
        }

        int leadReserve = 58;

        for (String note : knowledge) {
            if (y > layout.pageBottom()
                    - leadReserve) {
                break;
            }

            y = drawWrapped(
                graphics,
                "• " + note,
                x,
                y,
                textWidth,
                GuiTheme.INK,
                2
            );
        }

        for (String objective
                : entry.objectives()) {
            if (y > layout.pageBottom()
                    - leadReserve) {
                break;
            }

            y = drawWrapped(
                graphics,
                "· " + objective,
                x,
                y,
                textWidth,
                GuiTheme.INK_MUTED,
                1
            );
        }

        int leadY = Math.max(
            y + 4,
            layout.pageBottom() - 52
        );

        leadY = sectionTitle(
            graphics,
            "Current Lead",
            x,
            leadY,
            right
        );

        drawWrapped(
            graphics,
            entry.currentLead().isBlank()
                ? "Check your quest objectives."
                : entry.currentLead(),
            x,
            leadY,
            textWidth,
            GuiTheme.INK,
            2
        );

        Rect button =
            trackButton(layout);

        GuiTheme.tab(
            graphics,
            button.left(),
            button.top(),
            button.width(),
            button.height(),
            entry.tracked()
        );

        GuiTheme.drawCenteredNoShadow(
                graphics,
            font,
            Component.literal(
                entry.tracked()
                    ? "Untrack"
                    : "Track"
            ),
            button.centerX(),
            button.top() + 5,
            0xFFE8D7AA
        );
    }

    private void drawEmptyPage(
        GuiGraphics graphics,
        Layout layout
    ) {
        graphics.drawString(
            font,
            Component.literal(tab.heading),
            layout.leftPageX(),
            layout.pageTop(),
            GuiTheme.WAX,
            false
        );

        graphics.fill(
            layout.leftPageX(),
            layout.pageTop() + 11,
            layout.leftPageRight(),
            layout.pageTop() + 12,
            0x665D4228
        );

        String message = switch (tab) {
            case JOURNAL ->
                "No active threads.";
            case RUMOURS ->
                "No rumours recorded.";
            case CHRONICLE ->
                "No completed quests.";
            case RACES ->
                "No race information available.";
        };

        String sub = switch (tab) {
            case JOURNAL ->
                "Accepted quests appear here.";
            case RUMOURS ->
                "Talk to people to hear what is happening.";
            case CHRONICLE ->
                "Finished quests are kept here.";
            case RACES ->
                "Install CyberRaces to read the field guide.";
        };

        drawCenteredWrapped(
            graphics,
            message,
            layout.leftPageX(),
            layout.pageTop() + 66,
            layout.leftPageWidth(),
            GuiTheme.INK,
            2
        );

        drawCenteredWrapped(
            graphics,
            sub,
            layout.leftPageX(),
            layout.pageTop() + 88,
            layout.leftPageWidth(),
            GuiTheme.INK_MUTED,
            3
        );

        // Draw headings as flat ink on parchment; Minecraft's centred
        // string helper adds a dark drop shadow by default.
        String heading = "Adventurer's Journal";
        graphics.drawString(
            font,
            Component.literal(heading),
            layout.rightPageCenterX() - font.width(heading) / 2,
            layout.pageTop() + 50,
            GuiTheme.INK,
            false
        );

        drawCenteredWrapped(
            graphics,
            "Select an entry to read more.",
            layout.rightPageX(),
            layout.pageTop() + 72,
            layout.rightPageWidth(),
            GuiTheme.INK_MUTED,
            3
        );
    }

    private int sectionTitle(
        GuiGraphics graphics,
        String title,
        int x,
        int y,
        int right
    ) {
        graphics.drawString(
            font,
            Component.literal(title),
            x,
            y,
            GuiTheme.WAX,
            false
        );

        graphics.fill(
            x,
            y + 10,
            right,
            y + 11,
            0x665D4228
        );

        return y + 14;
    }

    private int drawWrapped(
        GuiGraphics graphics,
        String text,
        int x,
        int y,
        int width,
        int color,
        int maxLines
    ) {
        if (text == null
                || text.isBlank()) {
            return y;
        }

        List<FormattedCharSequence> lines =
            font.split(
                Component.literal(text),
                Math.max(20, width)
            );

        int count = Math.min(
            maxLines,
            lines.size()
        );

        for (int i = 0; i < count; i++) {
            graphics.drawString(
                font,
                lines.get(i),
                x,
                y,
                color,
                false
            );
            y += 10;
        }

        return y;
    }

    private void drawCenteredWrapped(
        GuiGraphics graphics,
        String text,
        int x,
        int y,
        int width,
        int color,
        int maxLines
    ) {
        List<FormattedCharSequence> lines =
            font.split(
                Component.literal(text),
                Math.max(20, width - 12)
            );

        int count = Math.min(
            maxLines,
            lines.size()
        );

        for (int i = 0; i < count; i++) {
            // Preserve centred alignment, but disable the text shadow.
            FormattedCharSequence line = lines.get(i);
            graphics.drawString(
                font,
                line,
                x + (width - font.width(line)) / 2,
                y,
                color,
                false
            );
            y += 10;
        }
    }

    private void playPageTurn(float pitch) {
        Minecraft.getInstance()
            .getSoundManager()
            .play(
                SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN,
                    pitch
                )
            );
    }

    private List<JournalEntry> visibleEntries() {
        List<JournalEntry> result =
            new ArrayList<>();

        for (JournalEntry entry
                : ClientJournalState.entries()) {
            if (tab.matches(entry)) {
                result.add(entry);
            }
        }

        return result;
    }

    private Layout layout() {
        float widthScale =
            (width - 18.0F)
                / GuiTheme.BOOK_WIDTH;
        float heightScale =
            (height - 36.0F)
                / GuiTheme.BOOK_HEIGHT;
        float scale = Math.min(
            1.0F,
            Math.min(
                widthScale,
                heightScale
            )
        );

        scale = Math.max(
            0.72F,
            scale
        );

        int bookWidth =
            Math.round(
                GuiTheme.BOOK_WIDTH
                    * scale
            );
        int bookHeight =
            Math.round(
                GuiTheme.BOOK_HEIGHT
                    * scale
            );

        int bookX =
            (width - bookWidth) / 2;
        int bookY =
            Math.max(
                21,
                (height - bookHeight) / 2
            );

        return new Layout(
            bookX,
            bookY,
            bookWidth,
            bookHeight
        );
    }

    private Rect tabRect(
        Layout layout,
        int index
    ) {
        int tabWidth = Math.max(
            72,
            Math.min(
                88,
                layout.bookWidth() / 5
            )
        );
        int tabHeight = 22;
        int gap = 5;
        int count = JournalTab.values().length;
        int total = tabWidth * count
                + gap * (count - 1);

        int start =
            layout.bookX()
                + (layout.bookWidth()
                    - total) / 2;

        return new Rect(
            start
                + index
                    * (tabWidth + gap),
            layout.bookY() - 15,
            start
                + index
                    * (tabWidth + gap)
                + tabWidth,
            layout.bookY() - 15
                + tabHeight
        );
    }

    private Rect trackButton(
        Layout layout
    ) {
        int width = 74;
        int height = 18;

        return new Rect(
            layout.rightPageRight()
                - width,
            layout.pageBottom()
                - height,
            layout.rightPageRight(),
            layout.pageBottom()
        );
    }

    private String trimToWidth(
        String value,
        int maximumWidth
    ) {
        if (value == null) {
            return "";
        }

        if (font.width(value)
                <= maximumWidth) {
            return value;
        }

        return font.plainSubstrByWidth(
            value,
            Math.max(
                4,
                maximumWidth - 8
            )
        ) + "…";
    }

    private static String titleCase(
        String value
    ) {
        if (value == null
                || value.isBlank()) {
            return "";
        }

        String normalized =
            value.trim().replace(
                '_',
                ' '
            );

        return Character.toUpperCase(
            normalized.charAt(0)
        ) + normalized.substring(1);
    }

    private static String statusText(
        JournalEntry entry
    ) {
        return switch (entry.status()) {
            case "READY" ->
                "ready to resolve";
            case "COMPLETED" ->
                "resolved";
            default ->
                "in your journal";
        };
    }

    private enum JournalTab {
        JOURNAL(
            "Journal",
            "Active Threads"
        ),
        RUMOURS(
            "Rumours",
            "Whispers & Leads"
        ),
        CHRONICLE(
            "Chronicle",
            "Resolved Tales"
        ),
        RACES(
            "Races",
            "Race Field Guide"
        );

        private final String label;
        private final String heading;

        JournalTab(
            String label,
            String heading
        ) {
            this.label = label;
            this.heading = heading;
        }

        private boolean matches(
            JournalEntry entry
        ) {
            return switch (this) {
                case JOURNAL ->
                    "journal".equals(
                        entry.section()
                    )
                    && !"COMPLETED".equals(
                        entry.status()
                    );
                case RUMOURS ->
                    "rumour".equals(
                        entry.section()
                    )
                    && !"COMPLETED".equals(
                        entry.status()
                    );
                case CHRONICLE ->
                    "COMPLETED".equals(
                        entry.status()
                    )
                    || "chronicle".equals(
                        entry.section()
                    );
                case RACES -> false;
            };
        }
    }

    private record Rect(
        int left,
        int top,
        int right,
        int bottom
    ) {
        private int width() {
            return right - left;
        }

        private int height() {
            return bottom - top;
        }

        private int centerX() {
            return left + width() / 2;
        }

        private boolean contains(
            double x,
            double y
        ) {
            return x >= left
                && x < right
                && y >= top
                && y < bottom;
        }
    }

    private record Layout(
        int bookX,
        int bookY,
        int bookWidth,
        int bookHeight
    ) {
        private int scaleX(int source) {
            return bookX
                + source
                    * bookWidth
                    / GuiTheme.BOOK_WIDTH;
        }

        private int scaleY(int source) {
            return bookY
                + source
                    * bookHeight
                    / GuiTheme.BOOK_HEIGHT;
        }

        private int leftPageX() {
            return scaleX(31);
        }

        private int leftPageRight() {
            return scaleX(203);
        }

        private int rightPageX() {
            return scaleX(245);
        }

        private int rightPageRight() {
            return scaleX(417);
        }

        private int pageTop() {
            return scaleY(34);
        }

        private int pageBottom() {
            return scaleY(220);
        }

        private int leftPageWidth() {
            return leftPageRight()
                - leftPageX();
        }

        private int rightPageWidth() {
            return rightPageRight()
                - rightPageX();
        }

        private int rightPageCenterX() {
            return rightPageX()
                + rightPageWidth() / 2;
        }
    }
}
