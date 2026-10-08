package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.packet.GuildCardPacket;
import com.cyberspectraa.cyberquest.CyberQuest;
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
    private static final ResourceLocation CARD =
        new ResourceLocation(CyberQuest.MOD_ID, "textures/gui/guild_card.png");
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
        int left = (width - 224) / 2;
        int top = (height - 192) / 2;
        graphics.blit(CARD, left, top, 0, 0, 224, 192, 224, 192);
        int ink = 0xFF392719;
        int muted = 0xFF785638;

        // All lettering remains native Minecraft font, rendered above the
        // hand-edited vanilla parchment texture for readable live statistics.
        graphics.drawString(font, "GUILD MEMBERSHIP", left + 63, top + 34, ink, false);
        graphics.drawString(font, "Adventurers' Guild", left + 63, top + 46, muted, false);
        graphics.drawString(font, font.plainSubstrByWidth(card.playerName(), 164),
            left + 34, top + 67, ink, false);
        graphics.drawString(font, "Level: " + card.level(), left + 34, top + 83, ink, false);
        graphics.drawString(font, "Guild rank: " + card.guildRank(),
            left + 34, top + 98, ink, false);
        graphics.drawString(font, "Quests completed: " + card.questsCompleted(),
            left + 34, top + 113, ink, false);
        graphics.drawString(font, "Guild jobs: " + card.guildContractsCompleted(),
            left + 34, top + 128, ink, false);
        graphics.drawString(font, "Reputation: " + card.guildReputation(),
            left + 34, top + 143, muted, false);
        graphics.drawString(font, "Contracts: " + card.activeContracts() + "/2",
            left + 34, top + 157, muted, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
