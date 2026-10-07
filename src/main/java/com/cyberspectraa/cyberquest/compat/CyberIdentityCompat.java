package com.cyberspectraa.cyberquest.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class CyberIdentityCompat {
    private CyberIdentityCompat() {
    }

    public static String race(ServerPlayer player) {
        CompoundTag root = player.getPersistentData()
            .getCompound("CyberRaces");
        return normalize(root.getString("Race"));
    }

    public static String evolution(ServerPlayer player) {
        CompoundTag root = player.getPersistentData()
            .getCompound("CyberRaces");
        return normalize(root.getString("Evolution"));
    }

    public static String cyberClass(ServerPlayer player) {
        CompoundTag root = player.getPersistentData()
            .getCompound("CyberClasses");
        return normalize(root.getString("Class"));
    }

    public static String classAdvancement(
        ServerPlayer player
    ) {
        CompoundTag root = player.getPersistentData()
            .getCompound("CyberClasses");
        return normalize(
            root.getString("Advancement")
        );
    }

    private static String normalize(String value) {
        return value == null
            ? ""
            : value.trim().toLowerCase();
    }
}
