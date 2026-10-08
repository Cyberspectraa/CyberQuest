package com.cyberspectraa.cyberquest.network.packet;

import com.cyberspectraa.cyberquest.client.GuildCardScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record GuildCardPacket(
    String playerName, int level, String guildRank,
    int questsCompleted, int guildContractsCompleted,
    int guildReputation, int activeContracts
) {
    public static void encode(GuildCardPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.playerName(), 64);
        buffer.writeVarInt(packet.level());
        buffer.writeUtf(packet.guildRank(), 32);
        buffer.writeVarInt(packet.questsCompleted());
        buffer.writeVarInt(packet.guildContractsCompleted());
        buffer.writeInt(packet.guildReputation());
        buffer.writeVarInt(packet.activeContracts());
    }

    public static GuildCardPacket decode(FriendlyByteBuf buffer) {
        return new GuildCardPacket(
            buffer.readUtf(64), buffer.readVarInt(), buffer.readUtf(32),
            buffer.readVarInt(), buffer.readVarInt(), buffer.readInt(), buffer.readVarInt()
        );
    }

    public static void handle(GuildCardPacket packet,
                              Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> GuildCardScreen.open(packet));
        context.setPacketHandled(true);
    }
}
