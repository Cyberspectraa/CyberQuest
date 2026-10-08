package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.packet.GuildCardPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

/**
 * Vanilla book background with Minecraft font: no generated GUI art or textures.
 */
public final class GuildCardScreen extends Screen {
    private static final ResourceLocation BOOK =
        new ResourceLocation("minecraft", "textures/gui/book.png");
    private final GuildCardPacket card;

    public GuildCardScreen(GuildCardPacket card) {
        super(Component.literal("Guild Card"));
        this.card = card;
    }

    public static void open(GuildCardPacket card) {
        Minecraft.getInstance().setScreen(new GuildCardScreen(card));
    }

    @Override
    protected void init() {
        super.init();
        Minecraft.getInstance().getSoundManager().play(
            SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        int left = (width - 192) / 2;
        int top = (height - 192) / 2;
        graphics.blit(BOOK, left, top, 0, 0, 192, 192, 256, 256);
        int ink = 0xFF38261B;
        int muted = 0xFF775439;
        graphics.drawCenteredString(font, "ADVENTURERS' GUILD", left + 93, top + 27, ink);
        graphics.drawCenteredString(font, "MEMBERSHIP CARD", left + 93, top + 42, muted);
        graphics.drawString(font, font.plainSubstrByWidth(card.playerName(), 136),
            left + 28, top + 66, ink, false);
        graphics.drawString(font, "Level: " + card.level(), left + 28, top + 84, ink, false);
        graphics.drawString(font, "Guild rank: " + card.guildRank(), left + 28, top + 99, ink, false);
        graphics.drawString(font, "Quests completed: " + card.questsCompleted(),
            left + 28, top + 114, ink, false);
        graphics.drawString(font, "Guild jobs: " + card.guildContractsCompleted(),
            left + 28, top + 129, ink, false);
        graphics.drawString(font, "Reputation: " + card.guildReputation(),
            left + 28, top + 144, muted, false);
        graphics.drawString(font, "Active contracts: " + card.activeContracts() + "/2",
            left + 28, top + 159, muted, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
