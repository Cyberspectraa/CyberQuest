package com.cyberspectraa.cyberquest.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class CyberNpcQuestCompat {
    public static final String NPC_ID_KEY = "CyberQuestNpcId";
    public static final String BINDINGS_KEY = "CyberQuestBindings";

    private CyberNpcQuestCompat() {
    }

    public static boolean isCyberNpc(Entity entity) {
        if (entity == null) {
            return false;
        }

        ResourceLocation id = ForgeRegistries.ENTITY_TYPES
            .getKey(entity.getType());

        return id != null
            && "cybernpc".equals(id.getNamespace())
            && (
                "cyber_npc".equals(id.getPath())
                || "zombie_cyber_npc".equals(id.getPath())
            );
    }

    public static boolean isQuestNpc(Entity entity) {
        if (!isCyberNpc(entity)) {
            return false;
        }

        try {
            Method method = entity.getClass()
                .getMethod("getNpcType");
            Object value = method.invoke(entity);

            return value != null
                && "QUEST".equalsIgnoreCase(
                    String.valueOf(value)
                );
        } catch (ReflectiveOperationException
                | RuntimeException ignored) {
            return false;
        }
    }

    public static String npcId(Entity entity) {
        if (entity == null) {
            return "";
        }

        return entity.getPersistentData()
            .getString(NPC_ID_KEY)
            .trim()
            .toLowerCase();
    }

    public static void setNpcId(
        Entity entity,
        String npcId
    ) {
        if (entity == null) {
            return;
        }

        String value = npcId == null
            ? ""
            : npcId.trim().toLowerCase();

        if (value.isBlank()) {
            entity.getPersistentData().remove(NPC_ID_KEY);
        } else {
            entity.getPersistentData().putString(
                NPC_ID_KEY,
                value
            );
        }
    }

    public static List<ResourceLocation> bindings(
        Entity entity
    ) {
        List<ResourceLocation> result =
            new ArrayList<>();

        if (entity == null) {
            return result;
        }

        ListTag list = entity.getPersistentData()
            .getList(BINDINGS_KEY, Tag.TAG_STRING);

        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(
                list.getString(i)
            );

            if (id != null) {
                result.add(id);
            }
        }

        return result;
    }

    public static boolean bind(
        Entity entity,
        ResourceLocation questId
    ) {
        if (entity == null || questId == null) {
            return false;
        }

        CompoundTag data = entity.getPersistentData();
        ListTag list = data.getList(
            BINDINGS_KEY,
            Tag.TAG_STRING
        );

        String value = questId.toString();

        for (int i = 0; i < list.size(); i++) {
            if (value.equals(list.getString(i))) {
                return false;
            }
        }

        list.add(StringTag.valueOf(value));
        data.put(BINDINGS_KEY, list);
        return true;
    }

    public static boolean unbind(
        Entity entity,
        ResourceLocation questId
    ) {
        if (entity == null || questId == null) {
            return false;
        }

        CompoundTag data = entity.getPersistentData();
        ListTag list = data.getList(
            BINDINGS_KEY,
            Tag.TAG_STRING
        );

        String value = questId.toString();
        boolean changed = false;

        for (int i = list.size() - 1; i >= 0; i--) {
            if (value.equals(list.getString(i))) {
                list.remove(i);
                changed = true;
            }
        }

        data.put(BINDINGS_KEY, list);
        return changed;
    }
}
