package com.cyberspectraa.cyberquest.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashSet;
import java.util.Set;

public final class PlayerQuestData {
    public static final String ROOT_KEY = "CyberQuest";
    public static final int DATA_VERSION = 2;

    private static final String VERSION_KEY = "Version";
    private static final String ACTIVE_KEY = "Active";
    private static final String COMPLETED_KEY = "Completed";
    private static final String OBJECTIVES_KEY = "Objectives";
    private static final String ACCEPTED_AT_KEY = "AcceptedAt";
    private static final String STAGE_KEY = "Stage";
    private static final String TRACKED_KEY = "Tracked";

    private PlayerQuestData() {
    }

    public static void ensure(ServerPlayer player) {
        root(player);
    }

    public static boolean isActive(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        return active(player).contains(questId.toString());
    }

    public static boolean isCompleted(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        return completedIds(player).contains(questId);
    }

    public static void start(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        CompoundTag root = root(player);
        CompoundTag active = root.getCompound(ACTIVE_KEY);

        CompoundTag quest = new CompoundTag();
        quest.putLong(
            ACCEPTED_AT_KEY,
            player.level().getGameTime()
        );
        quest.putInt(STAGE_KEY, 0);
        quest.put(OBJECTIVES_KEY, new CompoundTag());

        active.put(questId.toString(), quest);
        root.put(ACTIVE_KEY, active);

        if (!root.contains(TRACKED_KEY)
                || root.getString(TRACKED_KEY).isBlank()) {
            root.putString(TRACKED_KEY, questId.toString());
        }

        write(player, root);
    }

    public static void removeActive(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        CompoundTag root = root(player);
        CompoundTag active = root.getCompound(ACTIVE_KEY);
        active.remove(questId.toString());
        root.put(ACTIVE_KEY, active);

        if (questId.toString().equals(
                root.getString(TRACKED_KEY)
        )) {
            root.remove(TRACKED_KEY);
        }

        write(player, root);
    }

    public static void markCompleted(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        CompoundTag root = root(player);
        ListTag completed = root.getList(
            COMPLETED_KEY,
            Tag.TAG_STRING
        );

        String value = questId.toString();

        for (int i = 0; i < completed.size(); i++) {
            if (value.equals(completed.getString(i))) {
                return;
            }
        }

        completed.add(StringTag.valueOf(value));
        root.put(COMPLETED_KEY, completed);
        write(player, root);
    }

    public static void clearCompleted(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        CompoundTag root = root(player);
        ListTag completed = root.getList(
            COMPLETED_KEY,
            Tag.TAG_STRING
        );

        String value = questId.toString();

        for (int i = completed.size() - 1; i >= 0; i--) {
            if (value.equals(completed.getString(i))) {
                completed.remove(i);
            }
        }

        root.put(COMPLETED_KEY, completed);
        write(player, root);
    }

    public static int stageIndex(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        CompoundTag quest = activeQuest(player, questId);

        return quest == null
            ? 0
            : Math.max(0, quest.getInt(STAGE_KEY));
    }

    public static boolean advanceToStage(
        ServerPlayer player,
        ResourceLocation questId,
        int stageIndex
    ) {
        CompoundTag root = root(player);
        CompoundTag active = root.getCompound(ACTIVE_KEY);

        if (!active.contains(questId.toString())) {
            return false;
        }

        CompoundTag quest =
            active.getCompound(questId.toString());
        int previous = Math.max(
            0,
            quest.getInt(STAGE_KEY)
        );
        int safe = Math.max(0, stageIndex);

        if (previous == safe) {
            return false;
        }

        quest.putInt(STAGE_KEY, safe);
        quest.put(OBJECTIVES_KEY, new CompoundTag());
        active.put(questId.toString(), quest);
        root.put(ACTIVE_KEY, active);
        write(player, root);
        return true;
    }

    public static int progress(
        ServerPlayer player,
        ResourceLocation questId,
        String objectiveId
    ) {
        CompoundTag quest = activeQuest(player, questId);

        if (quest == null) {
            return 0;
        }

        CompoundTag objectives =
            quest.getCompound(OBJECTIVES_KEY);

        return Math.max(
            0,
            objectives.getInt(objectiveId)
        );
    }

    public static boolean setProgress(
        ServerPlayer player,
        ResourceLocation questId,
        String objectiveId,
        int value
    ) {
        CompoundTag root = root(player);
        CompoundTag active = root.getCompound(ACTIVE_KEY);

        if (!active.contains(questId.toString())) {
            return false;
        }

        CompoundTag quest =
            active.getCompound(questId.toString());
        CompoundTag objectives =
            quest.getCompound(OBJECTIVES_KEY);

        int safeValue = Math.max(0, value);
        int previous = Math.max(
            0,
            objectives.getInt(objectiveId)
        );

        if (previous == safeValue) {
            return false;
        }

        objectives.putInt(objectiveId, safeValue);
        quest.put(OBJECTIVES_KEY, objectives);
        active.put(questId.toString(), quest);
        root.put(ACTIVE_KEY, active);
        write(player, root);
        return true;
    }

    public static boolean addProgress(
        ServerPlayer player,
        ResourceLocation questId,
        String objectiveId,
        int amount,
        int maximum
    ) {
        if (amount <= 0) {
            return false;
        }

        int current = progress(
            player,
            questId,
            objectiveId
        );

        return setProgress(
            player,
            questId,
            objectiveId,
            Math.min(
                Math.max(1, maximum),
                current + amount
            )
        );
    }

    public static ResourceLocation trackedQuestId(
        ServerPlayer player
    ) {
        CompoundTag root = root(player);
        String raw = root.getString(TRACKED_KEY);

        if (raw.isBlank()) {
            return null;
        }

        ResourceLocation id =
            ResourceLocation.tryParse(raw);

        if (id == null
                || !root.getCompound(ACTIVE_KEY)
                    .contains(raw)) {
            root.remove(TRACKED_KEY);
            write(player, root);
            return null;
        }

        return id;
    }

    public static boolean isTracked(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        ResourceLocation tracked =
            trackedQuestId(player);

        return tracked != null
            && tracked.equals(questId);
    }

    public static boolean setTrackedQuest(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        CompoundTag root = root(player);
        String previous = root.getString(TRACKED_KEY);

        if (questId == null) {
            if (previous.isBlank()) {
                return false;
            }

            root.remove(TRACKED_KEY);
            write(player, root);
            return true;
        }

        if (!root.getCompound(ACTIVE_KEY)
                .contains(questId.toString())) {
            return false;
        }

        if (questId.toString().equals(previous)) {
            return false;
        }

        root.putString(
            TRACKED_KEY,
            questId.toString()
        );
        write(player, root);
        return true;
    }

    public static Set<ResourceLocation> activeIds(
        ServerPlayer player
    ) {
        Set<ResourceLocation> result =
            new LinkedHashSet<>();

        for (String key : active(player).getAllKeys()) {
            ResourceLocation id =
                ResourceLocation.tryParse(key);

            if (id != null) {
                result.add(id);
            }
        }

        return result;
    }

    public static Set<ResourceLocation> completedIds(
        ServerPlayer player
    ) {
        Set<ResourceLocation> result =
            new LinkedHashSet<>();

        ListTag completed = root(player).getList(
            COMPLETED_KEY,
            Tag.TAG_STRING
        );

        for (int i = 0; i < completed.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(
                completed.getString(i)
            );

            if (id != null) {
                result.add(id);
            }
        }

        return result;
    }

    public static void resetAll(ServerPlayer player) {
        CompoundTag persistent =
            player.getPersistentData();
        persistent.remove(ROOT_KEY);
        ensure(player);
    }

    public static void copy(
        ServerPlayer oldPlayer,
        ServerPlayer newPlayer
    ) {
        CompoundTag oldPersistent =
            oldPlayer.getPersistentData();

        if (oldPersistent.contains(ROOT_KEY)) {
            newPlayer.getPersistentData().put(
                ROOT_KEY,
                oldPersistent.getCompound(ROOT_KEY).copy()
            );
        } else {
            ensure(newPlayer);
        }
    }

    private static CompoundTag active(
        ServerPlayer player
    ) {
        return root(player).getCompound(ACTIVE_KEY);
    }

    private static CompoundTag activeQuest(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        CompoundTag active = active(player);

        if (!active.contains(questId.toString())) {
            return null;
        }

        return active.getCompound(questId.toString());
    }

    private static CompoundTag root(ServerPlayer player) {
        CompoundTag persistent =
            player.getPersistentData();

        CompoundTag root = persistent.contains(ROOT_KEY)
            ? persistent.getCompound(ROOT_KEY)
            : new CompoundTag();

        int version = root.contains(VERSION_KEY)
            ? root.getInt(VERSION_KEY)
            : 0;

        if (version < DATA_VERSION) {
            migrate(root, version);
        }

        if (!root.contains(ACTIVE_KEY)) {
            root.put(ACTIVE_KEY, new CompoundTag());
        }

        if (!root.contains(COMPLETED_KEY)) {
            root.put(COMPLETED_KEY, new ListTag());
        }

        root.putInt(VERSION_KEY, DATA_VERSION);
        persistent.put(ROOT_KEY, root);
        return root;
    }

    private static void migrate(
        CompoundTag root,
        int oldVersion
    ) {
        if (oldVersion <= 0) {
            if (!root.contains(ACTIVE_KEY)) {
                root.put(ACTIVE_KEY, new CompoundTag());
            }
            if (!root.contains(COMPLETED_KEY)) {
                root.put(COMPLETED_KEY, new ListTag());
            }
        }

        if (oldVersion < 2
                && root.contains(ACTIVE_KEY)) {
            CompoundTag active =
                root.getCompound(ACTIVE_KEY);

            for (String key : active.getAllKeys()) {
                CompoundTag quest =
                    active.getCompound(key);

                if (!quest.contains(STAGE_KEY)) {
                    quest.putInt(STAGE_KEY, 0);
                }

                active.put(key, quest);
            }

            root.put(ACTIVE_KEY, active);
        }

        root.putInt(VERSION_KEY, DATA_VERSION);
    }

    private static void write(
        ServerPlayer player,
        CompoundTag root
    ) {
        player.getPersistentData().put(ROOT_KEY, root);
    }
}
