package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.network.GuildBoardOfferData;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.network.packet.AcceptGuildContractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class GuildBoardScreen extends Screen {
    private static final ResourceLocation NOTICE =
        new ResourceLocation(
            CyberQuest.MOD_ID,
            "textures/gui/guild_notice.png"
        );

    private final BlockPos pos;
    private final int guildReputation;
    private final GuildBoardOfferData offer;

    public GuildBoardScreen(
        BlockPos pos,
        int guildReputation,
        List<GuildBoardOfferData> offers
    ) {
        super(Component.literal("Guild Notice"));
        this.pos = pos;
        this.guildReputation = guildReputation;
        this.offer = offers == null || offers.isEmpty()
            ? null
            : offers.get(0);
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
                    0.92F
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

        int noticeWidth = Math.min(
            360,
            width - 32
        );
        int noticeHeight =
            noticeWidth * 160 / 256;
        int left =
            (width - noticeWidth) / 2;
        int top =
            (height - noticeHeight) / 2;

        graphics.blit(
            NOTICE,
            left,
            top,
            noticeWidth,
            noticeHeight,
            0.0F,
            0.0F,
            256,
            160,
            256,
            160
        );

        if (offer == null) {
            GuiTheme.drawCenteredNoShadow(
                graphics,
                font,
                Component.literal(
                    "No notice is pinned here."
                ),
                width / 2,
                top + noticeHeight / 2,
                GuiTheme.INK
            );

            super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
            );
            return;
        }

        int textLeft = left + 30;
        int textRight = left + noticeWidth - 30;
        int textWidth = textRight - textLeft;

        GuiTheme.drawCenteredNoShadow(
                graphics,
            font,
            Component.literal(offer.title()),
            width / 2,
            top + 26,
            GuiTheme.INK
        );

        GuiTheme.drawCenteredNoShadow(
                graphics,
            font,
            Component.literal(
                "Guild reputation: "
                    + guildReputation
            ),
            width / 2,
            top + 39,
            GuiTheme.INK_MUTED
        );

        int y = top + 60;

        List<FormattedCharSequence> taskLines =
            font.split(
                Component.literal(
                    offer.task()
                ),
                textWidth
            );

        for (int i = 0;
                i < Math.min(
                    4,
                    taskLines.size()
                );
                i++) {
            GuiTheme.drawCenteredNoShadow(
                graphics,
                font,
                taskLines.get(i),
                width / 2,
                y,
                GuiTheme.INK
            );
            y += 10;
        }

        y += 5;

        graphics.drawString(
            font,
            Component.literal(
                "Reward: "
                    + offer.reward()
            ),
            textLeft,
            y,
            0xFF6B4A28,
            false
        );
        y += 12;

        if (!offer.timer().isBlank()) {
            graphics.drawString(
                font,
                Component.literal(
                    "Deadline: "
                        + offer.timer()
                ),
                textLeft,
                y,
                GuiTheme.WAX,
                false
            );
            y += 11;
        }

        if (!offer.penalty().isBlank()) {
            graphics.drawString(
                font,
                Component.literal(
                    offer.penalty()
                ),
                textLeft,
                y,
                0xFF873C32,
                false
            );
        }

        String stateText = switch (
            offer.state()
        ) {
            case "ACTIVE" ->
                "Accepted — see your journal";
            case "COMPLETED" ->
                "Notice removed from board";
            default ->
                "Take Notice";
        };

        int buttonWidth = 118;
        int buttonHeight = 22;
        int buttonX =
            width / 2 - buttonWidth / 2;
        int buttonY =
            top + noticeHeight - 38;

        boolean hovered =
            mouseX >= buttonX
                && mouseX < buttonX + buttonWidth
                && mouseY >= buttonY
                && mouseY < buttonY + buttonHeight;

        GuiTheme.tab(
            graphics,
            buttonX,
            buttonY,
            buttonWidth,
            buttonHeight,
            hovered
                && "AVAILABLE".equals(
                    offer.state()
                )
        );

        GuiTheme.drawCenteredNoShadow(
                graphics,
            font,
            Component.literal(stateText),
            width / 2,
            buttonY + 7,
            "AVAILABLE".equals(offer.state())
                ? GuiTheme.LIGHT_TEXT
                : GuiTheme.INK_MUTED
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
        if (button != 0
                || offer == null
                || !"AVAILABLE".equals(
                    offer.state()
                )) {
            return super.mouseClicked(
                mouseX,
                mouseY,
                button
            );
        }

        int noticeWidth = Math.min(
            360,
            width - 32
        );
        int noticeHeight =
            noticeWidth * 160 / 256;
        int top =
            (height - noticeHeight) / 2;
        int buttonWidth = 118;
        int buttonHeight = 22;
        int buttonX =
            width / 2 - buttonWidth / 2;
        int buttonY =
            top + noticeHeight - 38;

        if (mouseX >= buttonX
                && mouseX < buttonX + buttonWidth
                && mouseY >= buttonY
                && mouseY < buttonY + buttonHeight) {
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
