package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class QuestHudOverlay {
    private static final int LEATHER = 0xD82A1B13;
    private static final int LEATHER_DARK = 0xE0160E0A;
    private static final int GOLD = 0xFFB58A43;
    private static final int PARCHMENT = 0xE8D6BF8F;
    private static final int INK = 0xFF342419;

    private QuestHudOverlay() {
    }

    public static void render(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.screen != null) {
            return;
        }

        JournalEntry entry =
            ClientJournalState.trackedEntry();

        if (entry == null) {
            return;
        }

        int width = Math.min(
            210,
            Math.max(150, graphics.guiWidth() / 3)
        );
        int left = graphics.guiWidth() - width - 8;
        int top = 8;
        int right = graphics.guiWidth() - 8;
        int bottom = top + 45;

        graphics.fill(
            left - 2,
            top - 2,
            right + 2,
            bottom + 2,
            LEATHER_DARK
        );
        graphics.fill(
            left,
            top,
            right,
            bottom,
            LEATHER
        );
        graphics.fill(
            left + 3,
            top + 3,
            right - 3,
            bottom - 3,
            PARCHMENT
        );
        graphics.fill(
            left + 3,
            top + 3,
            right - 3,
            top + 4,
            GOLD
        );

        graphics.drawString(
            minecraft.font,
            Component.literal(entry.title()),
            left + 8,
            top + 8,
            INK,
            false
        );

        List<FormattedCharSequence> lead =
            minecraft.font.split(
                Component.literal(
                    entry.currentLead().isBlank()
                        ? "Follow the trail."
                        : entry.currentLead()
                ),
                width - 16
            );

        int y = top + 22;
        for (int i = 0;
                i < Math.min(2, lead.size());
                i++) {
            graphics.drawString(
                minecraft.font,
                lead.get(i),
                left + 8,
                y,
                0xFF59402B,
                false
            );
            y += 9;
        }
    }
}
