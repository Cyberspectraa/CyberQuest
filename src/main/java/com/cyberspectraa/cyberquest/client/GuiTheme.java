package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.CyberQuest;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * Medieval-fantasy GUI skin for CyberQuest.
 *
 * The four base textures used here come from the MIT-licensed
 * Adventure Production Kit by DaxxMods. CyberQuest keeps its own
 * rendering logic and uses that artwork as nine-slice building blocks.
 */
public final class GuiTheme {
    private static final ResourceLocation WOOD =
        texture("panel_wood");
    private static final ResourceLocation PARCHMENT =
        texture("panel_parchment");
    private static final ResourceLocation BANNER =
        texture("banner");
    private static final ResourceLocation BUTTON =
        texture("button");

    public static final int TEXT_LIGHT = 0xF7E6BA;
    public static final int TEXT_MUTED = 0xC5B58D;
    public static final int INK = 0x49301E;
    public static final int INK_MUTED = 0x6E5A3D;
    public static final int GOLD = 0xF0C95D;
    public static final int WAX = 0x8A3930;

    private GuiTheme() {
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(
            CyberQuest.MOD_ID,
            "textures/gui/" + name + ".png"
        );
    }

    public static void screenBackdrop(
        GuiGraphics graphics,
        int width,
        int height
    ) {
        graphics.fillGradient(
            0, 0, width, height,
            0xE016241B,
            0xEE090E0B
        );
        graphics.fill(
            0, 0, width, 2,
            0x553E6A45
        );
    }

    public static void woodPanel(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        graphics.fill(
            x + 2,
            y + 3,
            x + width + 2,
            y + height + 3,
            0x760D120E
        );
        nineSlice(
            graphics,
            WOOD,
            x,
            y,
            width,
            height,
            6,
            32,
            32
        );
    }

    public static void parchmentPanel(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        graphics.fill(
            x + 2,
            y + 3,
            x + width + 2,
            y + height + 3,
            0x660D120E
        );
        nineSlice(
            graphics,
            PARCHMENT,
            x,
            y,
            width,
            height,
            6,
            32,
            32
        );
    }

    public static void banner(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        graphics.fill(
            x + 2,
            y + 3,
            x + width + 2,
            y + height + 3,
            0x680A1110
        );
        nineSlice(
            graphics,
            BANNER,
            x,
            y,
            width,
            height,
            5,
            32,
            16
        );
    }

    public static void button(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        boolean hovered,
        boolean selected
    ) {
        nineSlice(
            graphics,
            BUTTON,
            x,
            y,
            width,
            height,
            5,
            32,
            20
        );

        if (selected) {
            graphics.fill(
                x + 5,
                y + 5,
                x + width - 5,
                y + height - 5,
                0x35D5A83B
            );
        }

        if (hovered) {
            graphics.fill(
                x + 4,
                y + 4,
                x + width - 4,
                y + height - 4,
                0x2EFFE6A0
            );
            graphics.renderOutline(
                x + 2,
                y + 2,
                width - 4,
                height - 4,
                0xFFF2D06C
            );
        }
    }

    public static void centered(
        GuiGraphics graphics,
        Font font,
        Component text,
        int centerX,
        int y,
        int color
    ) {
        graphics.drawString(
            font,
            text,
            centerX - font.width(text) / 2,
            y,
            color,
            false
        );
    }

    public static void centered(
        GuiGraphics graphics,
        Font font,
        FormattedCharSequence text,
        int centerX,
        int y,
        int color
    ) {
        graphics.drawString(
            font,
            text,
            centerX - font.width(text) / 2,
            y,
            color,
            false
        );
    }

    private static void nineSlice(
        GuiGraphics graphics,
        ResourceLocation texture,
        int x,
        int y,
        int width,
        int height,
        int requestedBorder,
        int textureWidth,
        int textureHeight
    ) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int borderX = Math.min(
            requestedBorder,
            Math.min(width / 2, textureWidth / 2)
        );
        int borderY = Math.min(
            requestedBorder,
            Math.min(height / 2, textureHeight / 2)
        );

        int destinationCenterWidth =
            width - borderX * 2;
        int destinationCenterHeight =
            height - borderY * 2;
        int sourceCenterWidth =
            textureWidth - borderX * 2;
        int sourceCenterHeight =
            textureHeight - borderY * 2;

        slice(graphics, texture, x, y,
            borderX, borderY,
            0, 0, borderX, borderY,
            textureWidth, textureHeight);

        slice(graphics, texture, x + borderX, y,
            destinationCenterWidth, borderY,
            borderX, 0, sourceCenterWidth, borderY,
            textureWidth, textureHeight);

        slice(graphics, texture, x + width - borderX, y,
            borderX, borderY,
            textureWidth - borderX, 0,
            borderX, borderY,
            textureWidth, textureHeight);

        slice(graphics, texture, x, y + borderY,
            borderX, destinationCenterHeight,
            0, borderY,
            borderX, sourceCenterHeight,
            textureWidth, textureHeight);

        slice(graphics, texture, x + borderX, y + borderY,
            destinationCenterWidth,
            destinationCenterHeight,
            borderX, borderY,
            sourceCenterWidth,
            sourceCenterHeight,
            textureWidth, textureHeight);

        slice(graphics, texture,
            x + width - borderX,
            y + borderY,
            borderX,
            destinationCenterHeight,
            textureWidth - borderX,
            borderY,
            borderX,
            sourceCenterHeight,
            textureWidth, textureHeight);

        slice(graphics, texture,
            x, y + height - borderY,
            borderX, borderY,
            0, textureHeight - borderY,
            borderX, borderY,
            textureWidth, textureHeight);

        slice(graphics, texture,
            x + borderX,
            y + height - borderY,
            destinationCenterWidth,
            borderY,
            borderX,
            textureHeight - borderY,
            sourceCenterWidth,
            borderY,
            textureWidth, textureHeight);

        slice(graphics, texture,
            x + width - borderX,
            y + height - borderY,
            borderX, borderY,
            textureWidth - borderX,
            textureHeight - borderY,
            borderX, borderY,
            textureWidth, textureHeight);
    }

    private static void slice(
        GuiGraphics graphics,
        ResourceLocation texture,
        int x,
        int y,
        int width,
        int height,
        int sourceX,
        int sourceY,
        int sourceWidth,
        int sourceHeight,
        int textureWidth,
        int textureHeight
    ) {
        if (width <= 0
                || height <= 0
                || sourceWidth <= 0
                || sourceHeight <= 0) {
            return;
        }

        graphics.blit(
            texture,
            x,
            y,
            width,
            height,
            (float) sourceX,
            (float) sourceY,
            sourceWidth,
            sourceHeight,
            textureWidth,
            textureHeight
        );
    }
}
