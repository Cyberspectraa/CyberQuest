package com.cyberspectraa.cyberquest.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

import java.util.Locale;

/**
 * Soft CyberClasses bridge. The NBT contract matches
 * ClassQuestUnlockManager but CyberQuest remains loadable without
 * CyberClasses installed.
 */
public final class CyberClassQuestCompat {
    private static final String ROOT = "CyberClasses";
    private static final String CLASS = "Class";
    private static final String UNLOCKS = "QuestUnlocks";

    private CyberClassQuestCompat() {
    }

    public static boolean grantUnlock(
        ServerPlayer player,
        String requiredClass,
        String unlockId
    ) {
        if (player == null
                || unlockId == null
                || unlockId.isBlank()
                || !ModList.get()
                    .isLoaded("cyberclasses")) {
            return false;
        }

        String currentClass =
            CyberIdentityCompat.cyberClass(player);
        String scope = normalize(requiredClass);

        if (scope.isBlank()) {
            scope = currentClass;
        }

        if (scope.isBlank()
                || !scope.equals(currentClass)) {
            return false;
        }

        String unlock = normalize(unlockId);
        CompoundTag persistent =
            player.getPersistentData();
        CompoundTag root =
            persistent.contains(
                ROOT,
                Tag.TAG_COMPOUND
            )
                ? persistent.getCompound(ROOT)
                : new CompoundTag();

        if (!scope.equals(
                normalize(root.getString(CLASS))
        )) {
            return false;
        }

        CompoundTag questUnlocks =
            root.contains(
                UNLOCKS,
                Tag.TAG_COMPOUND
            )
                ? root.getCompound(UNLOCKS)
                : new CompoundTag();

        ListTag values =
            questUnlocks.getList(
                scope,
                Tag.TAG_STRING
            );

        for (int i = 0; i < values.size(); i++) {
            if (unlock.equals(
                    normalize(
                        values.getString(i)
                    )
            )) {
                return false;
            }
        }

        values.add(
            StringTag.valueOf(unlock)
        );
        questUnlocks.put(scope, values);
        root.put(UNLOCKS, questUnlocks);
        persistent.put(ROOT, root);
        return true;
    }

    private static String normalize(
        String value
    ) {
        return value == null
            ? ""
            : value.trim()
                .toLowerCase(Locale.ROOT);
    }
}
