package com.cyberspectraa.cyberquest.guild;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

/** Server-wide, per-dimension paper removal; resets at each Minecraft dawn. */
public final class GuildBoardSavedData extends SavedData {
    private static final String FILE_ID = "cyberquest_guild_papers";
    private final Map<Long, Integer> removed = new HashMap<>();
    private long day = Long.MIN_VALUE;

    public static GuildBoardSavedData get(ServerLevel level) {
        GuildBoardSavedData data = level.getDataStorage()
            .computeIfAbsent(GuildBoardSavedData::load, GuildBoardSavedData::new, FILE_ID);
        data.refreshDay(level);
        return data;
    }

    private static GuildBoardSavedData load(CompoundTag tag) {
        GuildBoardSavedData data = new GuildBoardSavedData();
        data.day = tag.getLong("Day");
        ListTag entries = tag.getList("Boards", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            data.removed.put(entry.getLong("Pos"), entry.getInt("Mask"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("Day", day);
        ListTag entries = new ListTag();
        removed.forEach((pos, mask) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("Pos", pos);
            entry.putInt("Mask", mask);
            entries.add(entry);
        });
        tag.put("Boards", entries);
        return tag;
    }

    private void refreshDay(ServerLevel level) {
        long today = level.getDayTime() / 24000L;
        if (today != day) {
            day = today;
            removed.clear();
            setDirty();
        }
    }

    public boolean isRemoved(BlockPos board, int slot) {
        return (removed.getOrDefault(board.asLong(), 0) & (1 << slot)) != 0;
    }

    public boolean take(BlockPos board, int slot) {
        int oldMask = removed.getOrDefault(board.asLong(), 0);
        int bit = 1 << slot;
        if ((oldMask & bit) != 0) return false;
        removed.put(board.asLong(), oldMask | bit);
        setDirty();
        return true;
    }
}
