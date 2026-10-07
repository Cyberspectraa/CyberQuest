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

    public static void render(
        GuiGraphics graphics
    ) {
        Minecraft minecraft =
            Minecraft.getInstance();

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
            194,
            Math.max(
                158,
                graphics.guiWidth() / 4
            )
        );
        int height = 42;
        int x =
            graphics.guiWidth()
                - width - 8;
        int y = 8;

        GuiTheme.entry(
            graphics,
            x,
            y,
            width,
            height,
            true
        );

        graphics.drawString(
            minecraft.font,
            Component.literal(
                minecraft.font
                    .plainSubstrByWidth(
                        entry.title(),
                        width - 18
                    )
            ),
            x + 9,
            y + 7,
            GuiTheme.INK,
            false
        );

        List<FormattedCharSequence> lines =
            minecraft.font.split(
                Component.literal(
                    entry.currentLead()
                        .isBlank()
                        ? "Follow the trail."
                        : entry.currentLead()
                ),
                width - 18
            );

        int textY = y + 20;

        for (int i = 0;
                i < Math.min(
                    2,
                    lines.size()
                );
                i++) {
            graphics.drawString(
                minecraft.font,
                lines.get(i),
                x + 9,
                textY,
                GuiTheme.INK_MUTED,
                false
            );
            textY += 9;
        }
    }
}
