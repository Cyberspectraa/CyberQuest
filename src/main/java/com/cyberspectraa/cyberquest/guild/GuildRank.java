package com.cyberspectraa.cyberquest.guild;

import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.server.level.ServerPlayer;

/** Rank is earned through completed contracts and guild reputation. */
public final class GuildRank {
    private GuildRank() {}

    public static String forPlayer(ServerPlayer player) {
        int done = PlayerQuestData.guildContractsCompleted(player);
        int rep = PlayerQuestData.guildReputation(player);
        if (done >= 200 && rep >= 300) return "S Rank";
        if (done >= 100 && rep >= 175) return "A Rank";
        if (done >= 60 && rep >= 100) return "B Rank";
        if (done >= 30 && rep >= 50) return "C Rank";
        if (done >= 15 && rep >= 25) return "D Rank";
        if (done >= 5 && rep >= 10) return "E Rank";
        return "F Rank";
    }
}
