package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class QuestHudOverlay {
    private QuestHudOverlay() {
    }

    public static void render(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.screen != null
                || minecraft.options.hideGui) {
            return;
        }

        JournalEntry entry =
            ClientJournalState.trackedEntry();

        if (entry == null) {
            return;
        }

        int width = Math.min(
            220,
            Math.max(
                160,
                graphics.guiWidth() / 3
            )
        );
        int left =
            graphics.guiWidth() - width - 8;
        int top = 8;

        GuiTheme.woodPanel(
            graphics,
            left,
            top,
            width,
            50
        );
        GuiTheme.banner(
            graphics,
            left + 6,
            top + 5,
            width - 12,
            16
        );

        graphics.drawString(
            minecraft.font,
            minecraft.font.plainSubstrByWidth(
                entry.title().toUpperCase(),
                width - 26
            ),
            left + 13,
            top + 9,
            GuiTheme.GOLD,
            false
        );

        List<FormattedCharSequence> lead =
            minecraft.font.split(
                Component.literal(
                    entry.currentLead().isBlank()
                        ? "Follow the trail."
                        : entry.currentLead()
                ),
                width - 18
            );

        int y = top + 27;

        for (int i = 0;
                i < Math.min(2, lead.size());
                i++) {
            graphics.drawString(
                minecraft.font,
                lead.get(i),
                left + 9,
                y,
                GuiTheme.TEXT_MUTED,
                false
            );
            y += 9;
        }
    }
}
