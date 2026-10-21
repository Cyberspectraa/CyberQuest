package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Compact tracked-quest summary. The panel grows with wrapped text instead of
 * silently dropping anything beyond its original two-line hard limit.
 */
public final class QuestHudOverlay {
    private static final int MARGIN = 8;
    private static final int CONTENT_INSET = 9;
    private static final int TOP_PADDING = 7;
    private static final int BOTTOM_PADDING = 7;
    private static final int LINE_SPACING = 1;
    private static final int SECTION_GAP = 3;

    private QuestHudOverlay() {
    }

    public static void render(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null
                || minecraft.screen != null
                || minecraft.options.hideGui) {
            return;
        }

        JournalEntry entry = ClientJournalState.trackedEntry();
        if (entry == null) {
            return;
        }

        int guiWidth = graphics.guiWidth();
        int guiHeight = graphics.guiHeight();
        int width = Math.min(Math.min(224, Math.max(158, guiWidth / 3)), guiWidth - MARGIN * 2);
        if (width < 90 || guiHeight < 48) {
            return;
        }

        Font font = minecraft.font;
        int textWidth = width - CONTENT_INSET * 2;
        List<HudLine> rows = new ArrayList<>();
        addWrapped(rows, font, entry.title(), textWidth, GuiTheme.INK, 0, false);

        rows.add(HudLine.gap());
        String lead = entry.currentLead().isBlank()
                ? "Follow the trail."
                : entry.currentLead();
        addWrapped(rows, font, lead, textWidth, GuiTheme.INK_MUTED, 0, false);

        // Actual active objectives belong in the tracked HUD, not just the
        // journal. Include their full text, with hanging indents for wrapping.
        if (!entry.objectives().isEmpty()) {
            rows.add(HudLine.gap());
            for (String objective : entry.objectives()) {
                if (!objective.isBlank()) {
                    addWrapped(rows, font, objective, textWidth - 11,
                            GuiTheme.INK_MUTED, 11, true);
                }
            }
        }

        int rowHeight = font.lineHeight + LINE_SPACING;
        int contentHeight = 0;
        for (HudLine row : rows) {
            contentHeight += row.text() == null ? SECTION_GAP : rowHeight;
        }

        int desiredHeight = Math.max(42, TOP_PADDING + contentHeight + BOTTOM_PADDING);
        int height = Math.min(desiredHeight, guiHeight - MARGIN * 2);
        boolean overflow = desiredHeight > height;
        int x = guiWidth - width - MARGIN;
        int y = MARGIN;

        GuiTheme.hud(graphics, x, y, width, height);

        int textY = y + TOP_PADDING;
        int contentBottom = y + height - BOTTOM_PADDING
                - (overflow ? rowHeight + 2 : 0);
        for (HudLine row : rows) {
            if (row.text() == null) {
                textY += SECTION_GAP;
                continue;
            }
            if (textY + font.lineHeight > contentBottom) {
                break;
            }
            if (row.bullet()) {
                graphics.drawString(font, "-",
                        x + CONTENT_INSET, textY, GuiTheme.INK_MUTED, false);
            }
            graphics.drawString(font, row.text(),
                    x + CONTENT_INSET + row.indent(), textY, row.color(), false);
            textY += rowHeight;
        }

        // On extremely small GUI scales or unusually long tracked quest
        // entries, never draw outside the screen: direct players to the full
        // quest journal rather than abruptly cutting text mid-sentence.
        if (overflow) {
            graphics.drawString(font, Component.literal("More in quest journal"),
                    x + CONTENT_INSET,
                    y + height - BOTTOM_PADDING - font.lineHeight,
                    GuiTheme.INK_MUTED, false);
        }
    }

    private static void addWrapped(List<HudLine> rows, Font font,
            String text, int width, int color, int indent, boolean bullet) {
        List<FormattedCharSequence> parts = font.split(
                Component.literal(text), Math.max(30, width));
        for (int i = 0; i < parts.size(); i++) {
            rows.add(new HudLine(parts.get(i), indent, bullet && i == 0, color));
        }
    }

    private record HudLine(FormattedCharSequence text, int indent,
                           boolean bullet, int color) {
        private static HudLine gap() {
            return new HudLine(null, 0, false, 0);
        }
    }
}
