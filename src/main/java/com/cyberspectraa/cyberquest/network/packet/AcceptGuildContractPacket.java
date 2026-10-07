package com.cyberspectraa.cyberquest.network.packet;

import com.cyberspectraa.cyberquest.guild.GuildContractManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record AcceptGuildContractPacket(
    BlockPos pos,
    int slot
) {
    public static void encode(
        AcceptGuildContractPacket packet,
        FriendlyByteBuf buffer
    ) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeVarInt(packet.slot());
    }

    public static AcceptGuildContractPacket decode(
        FriendlyByteBuf buffer
    ) {
        return new AcceptGuildContractPacket(
            buffer.readBlockPos(),
            buffer.readVarInt()
        );
    }

    public static void handle(
        AcceptGuildContractPacket packet,
        Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context =
            contextSupplier.get();
        ServerPlayer player =
            context.getSender();

        if (player != null) {
            GuildContractManager.accept(
                player,
                packet.pos(),
                packet.slot()
            );
        }

        context.setPacketHandled(true);
    }
}
