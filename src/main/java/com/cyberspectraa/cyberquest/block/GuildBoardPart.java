package com.cyberspectraa.cyberquest.block;

import net.minecraft.util.StringRepresentable;

public enum GuildBoardPart implements StringRepresentable {
    BOTTOM_LEFT("bottom_left", 0, 0, 2),
    BOTTOM_RIGHT("bottom_right", 1, 0, 5),
    MIDDLE_LEFT("middle_left", 0, 1, 0),
    MIDDLE_RIGHT("middle_right", 1, 1, 4),
    TOP_LEFT("top_left", 0, 2, 3),
    TOP_RIGHT("top_right", 1, 2, 1);

    private final String serializedName;
    private final int x;
    private final int y;
    private final int slot;

    GuildBoardPart(String serializedName, int x, int y, int slot) {
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
