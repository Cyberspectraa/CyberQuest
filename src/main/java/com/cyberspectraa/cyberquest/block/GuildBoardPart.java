package com.cyberspectraa.cyberquest.block;

import net.minecraft.util.StringRepresentable;

/**
 * Six physical sections of the Guild Board.
 *
 * The enum names are retained for save/binary compatibility with the
 * previous 2-wide x 3-tall layout, while their offsets now form a
 * 3-wide x 2-tall landscape board.
 */
public enum GuildBoardPart implements StringRepresentable {
    BOTTOM_LEFT("bottom_left", 0, 0, 2),
    BOTTOM_RIGHT("bottom_right", 1, 0, 5),
    MIDDLE_LEFT("middle_left", 2, 0, 3),
    MIDDLE_RIGHT("middle_right", 0, 1, 0),
    TOP_LEFT("top_left", 1, 1, 4),
    TOP_RIGHT("top_right", 2, 1, 1);

    private final String serializedName;
    private final int x;
    private final int y;
    private final int slot;

    GuildBoardPart(
        String serializedName,
        int x,
        int y,
        int slot
    ) {
        this.serializedName = serializedName;
        this.x = x;
        this.y = y;
        this.slot = slot;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int slot() { return slot; }
    public boolean isMaster() { return this == BOTTOM_LEFT; }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
