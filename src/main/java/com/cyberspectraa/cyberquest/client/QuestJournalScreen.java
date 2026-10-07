package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.network.packet.SetTrackedQuestPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QuestJournalScreen extends Screen {
    private JournalTab tab = JournalTab.JOURNAL;
    private int selected;

    public QuestJournalScreen() {
        super(Component.literal("Adventurer's Journal"));
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
                return true;
            }
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

            graphics.drawCenteredString(
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
                    ? "This tale has been entered into your chronicle."
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
                "The matter is still unclear."
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
                ? "Continue following the trail."
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

        graphics.drawCenteredString(
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
                "No tales recorded yet.";
        };

        String sub = switch (tab) {
            case JOURNAL ->
                "New adventures will be written here.";
            case RUMOURS ->
                "Whispers and leads you discover will appear here.";
            case CHRONICLE ->
                "Resolved adventures will become part of your story.";
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

        graphics.drawCenteredString(
            font,
            Component.literal(
                "Adventurer's Journal"
            ),
            layout.rightPageCenterX(),
            layout.pageTop() + 50,
            GuiTheme.INK
        );

        drawCenteredWrapped(
            graphics,
            "Choose a recorded thread to read its details.",
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
            graphics.drawCenteredString(
                font,
                lines.get(i),
                x + width / 2,
                y,
                color
            );
            y += 10;
        }
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
        int total =
            tabWidth * 3
                + gap * 2;

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
