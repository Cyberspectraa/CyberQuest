package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.CyberQuest;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;

public final class GuiTheme {
    private static final ResourceLocation BOOK =
        texture("journal_open");
    private static final ResourceLocation TAB =
        texture("journal_tab");
    private static final ResourceLocation TAB_SELECTED =
        texture("journal_tab_selected");
    private static final ResourceLocation ENTRY =
        texture("journal_entry");
    private static final ResourceLocation ENTRY_SELECTED =
        texture("journal_entry_selected");
    private static final ResourceLocation QUEST_HUD =
        texture("quest_hud");

    public static final int BOOK_WIDTH = 448;
    public static final int BOOK_HEIGHT = 256;

    public static final int INK = 0x4A321F;
    public static final int INK_MUTED = 0x755C3D;
    public static final int WAX = 0x893A31;
    public static final int GOLD = 0xA97834;
    public static final int LIGHT_TEXT = 0xF2DFA8;

    private GuiTheme() {
    }

    /**
     * Minecraft's drawCenteredString always renders with a shadow.
     * For text on parchment, center manually and draw flat ink instead.
     */
    public static void drawCenteredNoShadow(
        GuiGraphics graphics, Font font, Component text,
        int centerX, int y, int color
    ) {
        graphics.drawString(
            font, text, centerX - font.width(text) / 2, y, color, false
        );
    }

    public static void drawCenteredNoShadow(
        GuiGraphics graphics, Font font, FormattedCharSequence text,
        int centerX, int y, int color
    ) {
        graphics.drawString(
            font, text, centerX - font.width(text) / 2, y, color, false
        );
    }

    private static ResourceLocation texture(
        String name
    ) {
        return new ResourceLocation(
            CyberQuest.MOD_ID,
            "textures/gui/" + name + ".png"
        );
    }

    public static void backdrop(
        GuiGraphics graphics,
        int width,
        int height
    ) {
        graphics.fillGradient(
            0,
            0,
            width,
            height,
            0xB20A100D,
            0xC8040706
        );
    }

    public static void book(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        graphics.blit(
            BOOK,
            x,
            y,
            width,
            height,
            0.0F,
            0.0F,
            BOOK_WIDTH,
            BOOK_HEIGHT,
            BOOK_WIDTH,
            BOOK_HEIGHT
        );
    }

    public static void tab(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        boolean selected
    ) {
        ResourceLocation texture =
            selected ? TAB_SELECTED : TAB;

        graphics.blit(
            texture,
            x,
            y,
            width,
            height,
            0.0F,
            0.0F,
            96,
            24,
            96,
            24
        );
    }

    public static void hud(
        GuiGraphics graphics, int x, int y, int width, int height
    ) {
        // Nine-slice the parchment so taller quest text does not stretch
        // decorative borders/corners into giant, blurry shapes.
        final int edge = 9;
        final int textureWidth = 210;
        final int textureHeight = 52;
        int middleWidth = width - edge * 2;
        int middleHeight = height - edge * 2;

        graphics.blit(QUEST_HUD, x, y, edge, edge,
                0.0F, 0.0F, edge, edge, textureWidth, textureHeight);
        graphics.blit(QUEST_HUD, x + edge, y, middleWidth, edge,
                edge, 0.0F, textureWidth - edge * 2, edge,
                textureWidth, textureHeight);
        graphics.blit(QUEST_HUD, x + width - edge, y, edge, edge,
                textureWidth - edge, 0.0F, edge, edge,
                textureWidth, textureHeight);

        graphics.blit(QUEST_HUD, x, y + edge, edge, middleHeight,
                0.0F, edge, edge, textureHeight - edge * 2,
                textureWidth, textureHeight);
        graphics.blit(QUEST_HUD, x + edge, y + edge, middleWidth, middleHeight,
                edge, edge, textureWidth - edge * 2, textureHeight - edge * 2,
                textureWidth, textureHeight);
        graphics.blit(QUEST_HUD, x + width - edge, y + edge, edge, middleHeight,
                textureWidth - edge, edge, edge, textureHeight - edge * 2,
                textureWidth, textureHeight);

        graphics.blit(QUEST_HUD, x, y + height - edge, edge, edge,
                0.0F, textureHeight - edge, edge, edge,
                textureWidth, textureHeight);
        graphics.blit(QUEST_HUD, x + edge, y + height - edge, middleWidth, edge,
                edge, textureHeight - edge, textureWidth - edge * 2, edge,
                textureWidth, textureHeight);
        graphics.blit(QUEST_HUD, x + width - edge, y + height - edge, edge, edge,
                textureWidth - edge, textureHeight - edge, edge, edge,
                textureWidth, textureHeight);
    }

    public static void entry(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        boolean selected
    ) {
        ResourceLocation texture =
            selected ? ENTRY_SELECTED : ENTRY;

        graphics.blit(
            texture,
            x,
            y,
            width,
            height,
            0.0F,
            0.0F,
            176,
            28,
            176,
            28
        );
    }
}
