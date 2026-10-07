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
        GuiTheme.screenBackdrop(
            graphics,
            width,
            height
        );

        Layout layout = layout();

        drawBook(graphics, layout);
        drawTabs(graphics, layout, mouseX, mouseY);

        List<JournalEntry> entries =
            visibleEntries();

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

        Component closeHint = Component.literal("Close: ")
            .append(
                ClientKeyMappings.QUEST_JOURNAL
                    .getTranslatedKeyMessage()
            );

        graphics.drawString(
            font,
            closeHint,
            layout.right() - font.width(closeHint) - 14,
            layout.bottom() - 14,
            GuiTheme.INK_MUTED,
            false
        );

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

        JournalTab[] tabs = JournalTab.values();
        int tabWidth = Math.max(
            64,
            (layout.width() - 42) / tabs.length
        );
        int tabY = layout.top() - 16;

        for (int i = 0; i < tabs.length; i++) {
            int x = layout.left() + 12
                + i * tabWidth;

            if (inside(
                    mouseX,
                    mouseY,
                    x,
                    tabY,
                    x + tabWidth - 5,
                    layout.top() + 5
            )) {
                tab = tabs[i];
                selected = 0;
                return true;
            }
        }

        List<JournalEntry> entries =
            visibleEntries();
        int listTop = layout.contentTop() + 22;
        int rowHeight = 25;

        for (int i = 0; i < entries.size(); i++) {
            int y = listTop + i * rowHeight;

            if (y > layout.bottom() - 34) {
                break;
            }

            if (inside(
                    mouseX,
                    mouseY,
                    layout.left() + 15,
                    y,
                    layout.seam() - 10,
                    y + rowHeight - 4
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

            if (!"COMPLETED".equals(
                    entry.status()
            )) {
                int buttonWidth = 82;
                int x = layout.right()
                    - buttonWidth - 18;
                int y = layout.bottom() - 36;

                if (inside(
                        mouseX,
                        mouseY,
                        x,
                        y,
                        x + buttonWidth,
                        y + 20
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
        Layout layout
    ) {
        GuiTheme.woodPanel(
            graphics,
            layout.left(),
            layout.top(),
            layout.width(),
            layout.height()
        );

        GuiTheme.parchmentPanel(
            graphics,
            layout.left() + 9,
            layout.contentTop(),
            layout.leftPageWidth(),
            layout.contentHeight()
        );

        GuiTheme.parchmentPanel(
            graphics,
            layout.seam() + 4,
            layout.contentTop(),
            layout.rightPageWidth(),
            layout.contentHeight()
        );

        GuiTheme.banner(
            graphics,
            layout.left() + 14,
            layout.top() + 8,
            layout.width() - 28,
            22
        );

        graphics.drawString(
            font,
            Component.literal(
                "ADVENTURER'S JOURNAL"
            ),
            layout.left() + 26,
            layout.top() + 15,
            GuiTheme.GOLD,
            false
        );

        graphics.drawString(
            font,
            Component.literal(
                "Living Threads"
            ),
            layout.right() - 96,
            layout.top() + 15,
            GuiTheme.TEXT_MUTED,
            false
        );

        graphics.fill(
            layout.seam(),
            layout.contentTop() + 7,
            layout.seam() + 1,
            layout.bottom() - 18,
            0x665D4228
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
            64,
            (layout.width() - 42) / tabs.length
        );
        int tabY = layout.top() - 16;

        for (int i = 0; i < tabs.length; i++) {
            JournalTab value = tabs[i];
            int x = layout.left() + 12
                + i * tabWidth;
            int right = x + tabWidth - 5;

            boolean selectedTab = value == tab;
            boolean hovered = inside(
                mouseX,
                mouseY,
                x,
                tabY,
                right,
                layout.top() + 5
            );

            GuiTheme.button(
                graphics,
                x,
                tabY,
                right - x,
                21,
                hovered,
                selectedTab
            );

            graphics.drawCenteredString(
                font,
                Component.literal(value.label),
                (x + right) / 2,
                tabY + 7,
                selectedTab
                    ? GuiTheme.GOLD
                    : GuiTheme.TEXT_LIGHT
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
        int x = layout.left() + 15;
        int right = layout.seam() - 10;
        int y = layout.contentTop() + 22;
        int rowHeight = 25;

        graphics.drawString(
            font,
            Component.literal(tab.heading),
            x,
            layout.contentTop() + 9,
            GuiTheme.INK_MUTED,
            false
        );

        for (int i = 0; i < entries.size(); i++) {
            if (y > layout.bottom() - 34) {
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
                y + rowHeight - 4
            );

            GuiTheme.button(
                graphics,
                x - 3,
                y - 2,
                right - x + 3,
                rowHeight - 1,
                hovered,
                current
            );

            String marker = switch (
                entry.category().toLowerCase(
                    Locale.ROOT
                )
            ) {
                case "story" -> "◆";
                case "contract" -> "✦";
                case "rumour", "rumor" -> "?";
                default -> "•";
            };

            graphics.drawString(
                font,
                Component.literal(marker),
                x + 4,
                y + 5,
                entry.tracked()
                    ? GuiTheme.GOLD
                    : GuiTheme.WAX,
                false
            );

            String title = trimToWidth(
                entry.title(),
                Math.max(30, right - x - 28)
            );

            graphics.drawString(
                font,
                Component.literal(title),
                x + 17,
                y + 3,
                GuiTheme.TEXT_LIGHT,
                false
            );

            String sub = entry.tracked()
                ? "tracked"
                : statusText(entry);

            graphics.drawString(
                font,
                Component.literal(sub),
                x + 17,
                y + 13,
                GuiTheme.TEXT_MUTED,
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
        int x = layout.seam() + 17;
        int right = layout.right() - 18;
        int textWidth = Math.max(80, right - x);
        int y = layout.contentTop() + 12;

        graphics.drawString(
            font,
            Component.literal(entry.title()),
            x,
            y,
            GuiTheme.INK,
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
            GuiTheme.WAX,
            false
        );
        y += 15;

        y = drawWrapped(
            graphics,
            entry.description(),
            x,
            y,
            textWidth,
            GuiTheme.INK_MUTED,
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
                GuiTheme.INK,
                8
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
                GuiTheme.INK,
                2
            );

            if (y > layout.bottom() - 99) {
                break;
            }
        }

        for (String objective : entry.objectives()) {
            if (y > layout.bottom() - 99) {
                break;
            }

            y = drawWrapped(
                graphics,
                "· " + objective,
                x,
                y,
                textWidth,
                GuiTheme.INK_MUTED,
                2
            );
        }

        if (y <= layout.bottom() - 76) {
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
                GuiTheme.INK,
                3
            );
        }

        int buttonWidth = 82;
        int buttonX =
            layout.right() - buttonWidth - 18;
        int buttonY = layout.bottom() - 36;

        boolean hovered = inside(
            mouseX,
            mouseY,
            buttonX,
            buttonY,
            buttonX + buttonWidth,
            buttonY + 20
        );

        GuiTheme.button(
            graphics,
            buttonX,
            buttonY,
            buttonWidth,
            20,
            hovered,
            entry.tracked()
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
            GuiTheme.TEXT_LIGHT
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

        GuiTheme.centered(
            graphics,
            font,
            Component.literal(message),
            (layout.left() + layout.right()) / 2,
            layout.contentTop()
                + layout.contentHeight() / 2,
            GuiTheme.INK_MUTED
        );
    }

    private int drawSectionTitle(
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
            0x775D4228
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
            700,
            Math.max(300, width - 30)
        );
        int bookHeight = Math.min(
            390,
            Math.max(205, height - 48)
        );
        int left = (width - bookWidth) / 2;
        int top = (height - bookHeight) / 2 + 5;

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
                    "journal".equals(entry.section())
                    && !"COMPLETED".equals(
                        entry.status()
                    );
                case RUMOURS ->
                    "rumour".equals(entry.section())
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

        private int contentTop() {
            return top + 34;
        }

        private int contentHeight() {
            return bottom - contentTop() - 9;
        }

        private int leftPageWidth() {
            return seam() - left - 13;
        }

        private int rightPageWidth() {
            return right - seam() - 13;
        }
    }
}
