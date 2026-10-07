package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.GuildBoardOfferData;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.network.packet.AcceptGuildContractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public final class GuildBoardScreen extends Screen {
    private final BlockPos pos;
    private final int guildReputation;
    private final List<GuildBoardOfferData> offers;

    public GuildBoardScreen(
        BlockPos pos,
        int guildReputation,
        List<GuildBoardOfferData> offers
    ) {
        super(Component.literal("Guild Board"));
        this.pos = pos;
        this.guildReputation =
            guildReputation;
        this.offers = offers == null
            ? List.of()
            : List.copyOf(offers);
    }

    public static void open(
        BlockPos pos,
        int guildReputation,
        List<GuildBoardOfferData> offers
    ) {
        Minecraft.getInstance().setScreen(
            new GuildBoardScreen(
                pos,
                guildReputation,
                offers
            )
        );
    }

    @Override
    protected void init() {
        super.init();

        Minecraft.getInstance()
            .getSoundManager()
            .play(
                SimpleSoundInstance.forUI(
                    SoundEvents.BOOK_PAGE_TURN,
                    0.95F
                )
            );
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

        int panelWidth = Math.min(
            430,
            width - 28
        );
        int left =
            (width - panelWidth) / 2;
        int top = Math.max(
            16,
            (height - 270) / 2
        );

        graphics.drawCenteredString(
            font,
            Component.literal("Guild Board"),
            width / 2,
            top,
            GuiTheme.LIGHT_TEXT
        );

        graphics.drawCenteredString(
            font,
            Component.literal(
                "Guild reputation: "
                    + guildReputation
                    + "  •  Notices renew each dawn"
            ),
            width / 2,
            top + 13,
            0xFFBFAE8A
        );

        int y = top + 34;

        for (GuildBoardOfferData offer
                : offers) {
            int rowHeight = 50;
            boolean hovered =
                mouseX >= left
                    && mouseX < left
                        + panelWidth
                    && mouseY >= y
                    && mouseY < y
                        + rowHeight;

            boolean available =
                "AVAILABLE".equals(
                    offer.state()
                );

            GuiTheme.entry(
                graphics,
                left,
                y,
                panelWidth,
                rowHeight,
                hovered && available
            );

            graphics.drawString(
                font,
                Component.literal(
                    offer.title()
                ),
                left + 10,
                y + 6,
                GuiTheme.INK,
                false
            );

            graphics.drawString(
                font,
                Component.literal(
                    font.plainSubstrByWidth(
                        offer.task(),
                        panelWidth - 20
                    )
                ),
                left + 10,
                y + 18,
                GuiTheme.INK_MUTED,
                false
            );

            String footer =
                offer.reward();

            if (!offer.timer().isBlank()) {
                footer += "  •  "
                    + offer.timer();
            }

            if (!offer.penalty().isBlank()) {
                footer += "  •  "
                    + offer.penalty();
            }

            graphics.drawString(
                font,
                Component.literal(
                    font.plainSubstrByWidth(
                        footer,
                        panelWidth - 110
                    )
                ),
                left + 10,
                y + 32,
                0xFF6B4A28,
                false
            );

            String stateText = switch (
                offer.state()
            ) {
                case "ACTIVE" -> "Accepted";
                case "COMPLETED" -> "Done";
                default -> hovered
                    ? "Accept"
                    : "Available";
            };

            graphics.drawString(
                font,
                Component.literal(stateText),
                left + panelWidth
                    - font.width(stateText)
                    - 10,
                y + 32,
                available
                    ? GuiTheme.WAX
                    : GuiTheme.INK_MUTED,
                false
            );

            y += rowHeight + 5;
        }

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

        int panelWidth = Math.min(
            430,
            width - 28
        );
        int left =
            (width - panelWidth) / 2;
        int top = Math.max(
            16,
            (height - 270) / 2
        );
        int y = top + 34;

        for (GuildBoardOfferData offer
                : offers) {
            int rowHeight = 50;

            if ("AVAILABLE".equals(
                    offer.state()
            )
                    && mouseX >= left
                    && mouseX < left
                        + panelWidth
                    && mouseY >= y
                    && mouseY < y
                        + rowHeight) {
                Minecraft.getInstance()
                    .getSoundManager()
                    .play(
                        SimpleSoundInstance.forUI(
                            SoundEvents.UI_BUTTON_CLICK,
                            1.0F
                        )
                    );

                QuestNetwork.sendToServer(
                    new AcceptGuildContractPacket(
                        pos,
                        offer.slot()
                    )
                );
                return true;
            }

            y += rowHeight + 5;
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
}
