package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class QuestJournalScreen extends Screen {
    private int selected;
    private Button previousButton;
    private Button nextButton;

    public QuestJournalScreen() {
        super(Component.literal("Quest Journal"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int bottom = height - 34;

        previousButton = addRenderableWidget(
            Button.builder(
                    Component.literal("< Previous"),
                    button -> change(-1)
                )
                .bounds(
                    centerX - 130,
                    bottom,
                    110,
                    20
                )
                .build()
        );

        nextButton = addRenderableWidget(
            Button.builder(
                    Component.literal("Next >"),
                    button -> change(1)
                )
                .bounds(
                    centerX + 20,
                    bottom,
                    110,
                    20
                )
                .build()
        );

        updateButtons();
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        renderBackground(graphics);

        int centerX = width / 2;

        graphics.drawCenteredString(
            font,
            title,
            centerX,
            18,
            0xFFFFFF
        );

        List<JournalEntry> entries =
            ClientJournalState.entries();

        if (entries.isEmpty()) {
            graphics.drawCenteredString(
                font,
                Component.literal(
                    "No active or completed quests yet."
                ),
                centerX,
                58,
                0xA0A0A0
            );

            super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
            );
            return;
        }

        selected = Math.max(
            0,
            Math.min(
                selected,
                entries.size() - 1
            )
        );

        JournalEntry entry = entries.get(selected);

        int panelLeft = Math.max(
            20,
            centerX - 190
        );
        int panelRight = Math.min(
            width - 20,
            centerX + 190
        );
        int panelTop = 40;
        int panelBottom = height - 48;

        graphics.fill(
            panelLeft,
            panelTop,
            panelRight,
            panelBottom,
            0xB0101010
        );

        int statusColor = switch (entry.status()) {
            case "READY" -> 0x55FF55;
            case "COMPLETED" -> 0xAAAAAA;
            default -> 0xFFFF55;
        };

        graphics.drawCenteredString(
            font,
            Component.literal(entry.title()),
            centerX,
            panelTop + 12,
            0xFFE08A
        );

        graphics.drawCenteredString(
            font,
            Component.literal(
                switch (entry.status()) {
                    case "READY" -> "Ready to turn in";
                    case "COMPLETED" -> "Completed";
                    default -> "In progress";
                }
            ),
            centerX,
            panelTop + 26,
            statusColor
        );

        int textX = panelLeft + 14;
        int textWidth =
            Math.max(80, panelRight - panelLeft - 28);
        int y = panelTop + 48;

        for (FormattedCharSequence line
                : font.split(
                    Component.literal(
                        entry.description()
                    ),
                    textWidth
                )) {
            graphics.drawString(
                font,
                line,
                textX,
                y,
                0xD0D0D0,
                false
            );
            y += 10;

            if (y > panelBottom - 55) {
                break;
            }
        }

        y += 8;

        graphics.drawString(
            font,
            Component.literal("Objectives")
                .withStyle(ChatFormatting.GOLD),
            textX,
            y,
            0xFFFFFF,
            false
        );
        y += 14;

        for (String objective
                : entry.objectives()) {
            for (FormattedCharSequence line
                    : font.split(
                        Component.literal("• " + objective),
                        textWidth
                    )) {
                if (y > panelBottom - 16) {
                    break;
                }

                graphics.drawString(
                    font,
                    line,
                    textX,
                    y,
                    0xE0E0E0,
                    false
                );
                y += 10;
            }

            y += 3;

            if (y > panelBottom - 16) {
                break;
            }
        }

        graphics.drawCenteredString(
            font,
            Component.literal(
                (selected + 1)
                    + " / " + entries.size()
            ),
            centerX,
            height - 31,
            0xB0B0B0
        );

        super.render(
            graphics,
            mouseX,
            mouseY,
            partialTick
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void change(int direction) {
        List<JournalEntry> entries =
            ClientJournalState.entries();

        if (entries.isEmpty()) {
            selected = 0;
        } else {
            selected = Math.floorMod(
                selected + direction,
                entries.size()
            );
        }

        updateButtons();
    }

    private void updateButtons() {
        boolean hasMultiple =
            ClientJournalState.entries().size() > 1;

        if (previousButton != null) {
            previousButton.active = hasMultiple;
        }

        if (nextButton != null) {
            nextButton.active = hasMultiple;
        }
    }
}
