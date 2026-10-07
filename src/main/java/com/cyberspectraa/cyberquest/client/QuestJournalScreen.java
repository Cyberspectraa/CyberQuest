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
    private static final int LEATHER = 0xFF2A1B13;
    private static final int LEATHER_DARK = 0xFF120B08;
    private static final int LEATHER_RED = 0xFF5B2A22;
    private static final int GOLD = 0xFFB58A43;
    private static final int GOLD_DARK = 0xFF6E4E25;
    private static final int PARCHMENT = 0xFFD7C294;
    private static final int PARCHMENT_DARK = 0xFFC1A979;
    private static final int PARCHMENT_LIGHT = 0xFFE4D2A7;
    private static final int INK = 0xFF342419;
    private static final int INK_FADED = 0xFF6B543C;
    private static final int WAX = 0xFF792D28;

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
        renderBackground(graphics);

        Layout layout = layout();

        drawBook(
            graphics,
            layout.left(),
            layout.top(),
            layout.right(),
            layout.bottom()
        );

        drawTabs(graphics, layout, mouseX, mouseY);

        List<JournalEntry> entries = visibleEntries();

        if (entries.isEmpty()) {
            selected = 0;
            drawEmptyState(graphics, layout);
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

        graphics.drawCenteredString(
            font,
            Component.literal("J"),
            layout.right() - 14,
            layout.bottom() - 15,
            INK_FADED
        );

        super.render(
            graphics,
            mouseX,
            mouseY,
            partialTick
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

        JournalTab[] tabs = JournalTab.values();
        int tabWidth = Math.max(
            60,
            (layout.width() - 34) / tabs.length
        );
        int tabY = layout.top() - 17;

        for (int i = 0; i < tabs.length; i++) {
            int x = layout.left() + 12
                + i * tabWidth;

            if (inside(
                    mouseX,
                    mouseY,
                    x,
                    tabY,
                    x + tabWidth - 4,
                    layout.top() + 4
            )) {
                tab = tabs[i];
                selected = 0;
                return true;
            }
        }

        List<JournalEntry> entries = visibleEntries();
        int listTop = layout.top() + 34;
        int rowHeight = 23;

        for (int i = 0; i < entries.size(); i++) {
            int y = listTop + i * rowHeight;

            if (y > layout.bottom() - 28) {
                break;
            }

            if (inside(
                    mouseX,
                    mouseY,
                    layout.left() + 13,
                    y,
                    layout.seam() - 8,
                    y + rowHeight - 3
            )) {
                selected = i;
                return true;
            }
        }

        if (!entries.isEmpty()) {
            JournalEntry entry = entries.get(
                Math.max(
                    0,
                    Math.min(
                        selected,
                        entries.size() - 1
                    )
                )
            );

            if (!"COMPLETED".equals(entry.status())) {
                int buttonWidth = 76;
                int x = layout.right()
                    - buttonWidth - 18;
                int y = layout.bottom() - 31;

                if (inside(
                        mouseX,
                        mouseY,
                        x,
                        y,
                        x + buttonWidth,
                        y + 18
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

    private void drawBook(
        GuiGraphics graphics,
        int left,
        int top,
        int right,
        int bottom
    ) {
        graphics.fill(
            left - 6,
            top - 5,
            right + 6,
            bottom + 6,
            LEATHER_DARK
        );
        graphics.fill(
            left - 3,
            top - 2,
            right + 3,
            bottom + 3,
            LEATHER
        );
        graphics.fill(
            left,
            top,
            right,
            bottom,
            GOLD_DARK
        );
        graphics.fill(
            left + 2,
            top + 2,
            right - 2,
            bottom - 2,
            PARCHMENT_DARK
        );
        graphics.fill(
            left + 6,
            top + 6,
            right - 6,
            bottom - 6,
            PARCHMENT
        );

        int seam = (left + right) / 2;

        graphics.fill(
            seam - 2,
            top + 7,
            seam + 2,
            bottom - 7,
            0x553F2A1B
        );
        graphics.fill(
            seam,
            top + 7,
            seam + 1,
            bottom - 7,
            0x337B5D37
        );

        for (int[] corner : new int[][]{
            {left + 3, top + 3},
            {right - 7, top + 3},
            {left + 3, bottom - 7},
            {right - 7, bottom - 7}
        }) {
            graphics.fill(
                corner[0],
                corner[1],
                corner[0] + 4,
                corner[1] + 4,
                GOLD
            );
        }

        graphics.drawCenteredString(
            font,
            Component.literal("Adventurer's Journal"),
            seam,
            top + 12,
            INK
        );
    }

    private void drawTabs(
        GuiGraphics graphics,
        Layout layout,
        int mouseX,
        int mouseY
    ) {
        JournalTab[] tabs = JournalTab.values();
        int tabWidth = Math.max(
            60,
            (layout.width() - 34) / tabs.length
        );
        int tabY = layout.top() - 17;

        for (int i = 0; i < tabs.length; i++) {
            JournalTab value = tabs[i];
            int x = layout.left() + 12
                + i * tabWidth;
            int right = x + tabWidth - 4;
            boolean selectedTab = value == tab;
            boolean hovered = inside(
                mouseX,
                mouseY,
                x,
                tabY,
                right,
                layout.top() + 4
            );

            graphics.fill(
                x,
                tabY,
                right,
                layout.top() + 4,
                selectedTab
                    ? LEATHER_RED
                    : LEATHER
            );
            graphics.fill(
                x + 2,
                tabY + 2,
                right - 2,
                tabY + 3,
                hovered || selectedTab
                    ? GOLD
                    : GOLD_DARK
            );

            graphics.drawCenteredString(
                font,
                Component.literal(value.label),
                (x + right) / 2,
                tabY + 7,
                selectedTab
                    ? 0xFFFFE6AD
                    : 0xFFD8C28F
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
        int x = layout.left() + 13;
        int right = layout.seam() - 8;
        int y = layout.top() + 34;
        int rowHeight = 23;

        graphics.drawString(
            font,
            Component.literal(tab.heading),
            x,
            layout.top() + 24,
            INK_FADED,
            false
        );

        for (int i = 0; i < entries.size(); i++) {
            if (y > layout.bottom() - 28) {
                break;
            }

            JournalEntry entry = entries.get(i);
            boolean current = i == selected;
            boolean hovered = inside(
                mouseX,
                mouseY,
                x,
                y,
                right,
                y + rowHeight - 3
            );

            if (current || hovered) {
                graphics.fill(
                    x - 3,
                    y - 2,
                    right,
                    y + rowHeight - 3,
                    current
                        ? 0x664E2D20
                        : 0x335C3B29
                );
            }

            graphics.fill(
                x - 3,
                y - 2,
                x - 1,
                y + rowHeight - 3,
                entry.tracked()
                    ? GOLD
                    : (current
                        ? WAX
                        : 0x445A442D)
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
                x + 1,
                y + 4,
                current ? WAX : INK_FADED,
                false
            );

            String title = trimToWidth(
                entry.title(),
                Math.max(30, right - x - 20)
            );

            graphics.drawString(
                font,
                Component.literal(title),
                x + 13,
                y + 2,
                INK,
                false
            );

            String sub = entry.tracked()
                ? "tracked"
                : statusText(entry);

            graphics.drawString(
                font,
                Component.literal(sub),
                x + 13,
                y + 12,
                INK_FADED,
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
        int x = layout.seam() + 12;
        int right = layout.right() - 14;
        int textWidth = Math.max(80, right - x);
        int y = layout.top() + 34;

        graphics.drawString(
            font,
            Component.literal(entry.title()),
            x,
            y,
            INK,
            false
        );
        y += 13;

        String category = entry.category().isBlank()
            ? "Thread"
            : titleCase(entry.category());

        String stage = entry.stageTitle().isBlank()
            ? ""
            : " — " + entry.stageTitle();

        String stageCount =
            entry.stageCount() > 1
                && !"COMPLETED".equals(entry.status())
            ? "  " + entry.stageNumber()
                + "/" + entry.stageCount()
            : "";

        graphics.drawString(
            font,
            Component.literal(
                category + stage + stageCount
            ),
            x,
            y,
            WAX,
            false
        );
        y += 15;

        y = drawWrapped(
            graphics,
            entry.description(),
            x,
            y,
            textWidth,
            INK_FADED,
            4
        );

        if ("COMPLETED".equals(entry.status())) {
            y += 7;
            y = drawSectionTitle(
                graphics,
                "Chronicle",
                x,
                y,
                right
            );

            String resolution =
                entry.completionText().isBlank()
                    ? "This matter has been resolved and entered into your chronicle."
                    : entry.completionText();

            drawWrapped(
                graphics,
                resolution,
                x,
                y,
                textWidth,
                INK,
                7
            );
            return;
        }

        y += 7;
        y = drawSectionTitle(
            graphics,
            "What We Know",
            x,
            y,
            right
        );

        List<String> knowledge =
            new ArrayList<>(entry.notes());

        if (knowledge.isEmpty()
                && entry.objectives().isEmpty()) {
            knowledge.add(
                "The matter is still unclear."
            );
        }

        for (String note : knowledge) {
            y = drawWrapped(
                graphics,
                "• " + note,
                x,
                y,
                textWidth,
                INK,
                2
            );

            if (y > layout.bottom() - 92) {
                break;
            }
        }

        for (String objective : entry.objectives()) {
            if (y > layout.bottom() - 92) {
                break;
            }

            y = drawWrapped(
                graphics,
                "· " + objective,
                x,
                y,
                textWidth,
                0xFF5D4932,
                2
            );
        }

        if (y <= layout.bottom() - 72) {
            y += 4;
            y = drawSectionTitle(
                graphics,
                "Current Lead",
                x,
                y,
                right
            );

            drawWrapped(
                graphics,
                entry.currentLead().isBlank()
                    ? "Continue following the trail."
                    : entry.currentLead(),
                x,
                y,
                textWidth,
                INK,
                3
            );
        }

        int buttonWidth = 76;
        int buttonX =
            layout.right() - buttonWidth - 18;
        int buttonY = layout.bottom() - 31;
        boolean hovered = inside(
            mouseX,
            mouseY,
            buttonX,
            buttonY,
            buttonX + buttonWidth,
            buttonY + 18
        );

        graphics.fill(
            buttonX,
            buttonY,
            buttonX + buttonWidth,
            buttonY + 18,
            LEATHER_DARK
        );
        graphics.fill(
            buttonX + 1,
            buttonY + 1,
            buttonX + buttonWidth - 1,
            buttonY + 17,
            entry.tracked()
                ? LEATHER_RED
                : LEATHER
        );
        graphics.fill(
            buttonX + 3,
            buttonY + 2,
            buttonX + buttonWidth - 3,
            buttonY + 3,
            hovered ? GOLD : GOLD_DARK
        );
        graphics.drawCenteredString(
            font,
            Component.literal(
                entry.tracked()
                    ? "Untrack"
                    : "Track"
            ),
            buttonX + buttonWidth / 2,
            buttonY + 6,
            0xFFFFE5A8
        );
    }

    private void drawEmptyState(
        GuiGraphics graphics,
        Layout layout
    ) {
        String message = switch (tab) {
            case JOURNAL ->
                "No active threads are written here.";
            case RUMOURS ->
                "You have heard no rumours worth recording.";
            case CHRONICLE ->
                "Your chronicle is still waiting for its first tale.";
        };

        graphics.drawCenteredString(
            font,
            Component.literal(message),
            (layout.left() + layout.right()) / 2,
            layout.top() + layout.height() / 2,
            INK_FADED
        );
    }

    private int drawSectionTitle(
        GuiGraphics graphics,
        String title,
        int x,
        int y,
        int right
    ) {
        graphics.fill(
            x,
            y + 10,
            right,
            y + 11,
            0x556E4E25
        );
        graphics.drawString(
            font,
            Component.literal(title),
            x,
            y,
            WAX,
            false
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
        if (text == null || text.isBlank()) {
            return y;
        }

        List<FormattedCharSequence> lines =
            font.split(
                Component.literal(text),
                width
            );

        int drawn = Math.min(
            maxLines,
            lines.size()
        );

        for (int i = 0; i < drawn; i++) {
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
        int bookWidth = Math.min(
            620,
            Math.max(270, width - 34)
        );
        int bookHeight = Math.min(
            360,
            Math.max(190, height - 52)
        );
        int left = (width - bookWidth) / 2;
        int top = (height - bookHeight) / 2 + 6;

        return new Layout(
            left,
            top,
            left + bookWidth,
            top + bookHeight
        );
    }

    private String trimToWidth(
        String value,
        int maximumWidth
    ) {
        if (font.width(value) <= maximumWidth) {
            return value;
        }

        return font.plainSubstrByWidth(
            value,
            Math.max(4, maximumWidth - 8)
        ) + "…";
    }

    private static String titleCase(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String normalized =
            value.trim().replace('_', ' ');

        return Character.toUpperCase(
            normalized.charAt(0)
        ) + normalized.substring(1);
    }

    private static String statusText(
        JournalEntry entry
    ) {
        return switch (entry.status()) {
            case "READY" -> "ready to resolve";
            case "COMPLETED" -> "resolved";
            default -> "in your journal";
        };
    }

    private static boolean inside(
        double mouseX,
        double mouseY,
        int left,
        int top,
        int right,
        int bottom
    ) {
        return mouseX >= left
            && mouseX < right
            && mouseY >= top
            && mouseY < bottom;
    }

    private enum JournalTab {
        JOURNAL("Journal", "Active Threads"),
        RUMOURS("Rumours", "Whispers & Leads"),
        CHRONICLE("Chronicle", "Resolved Tales");

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

    private record Layout(
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

        private int seam() {
            return left + width() * 42 / 100;
        }
    }
}
