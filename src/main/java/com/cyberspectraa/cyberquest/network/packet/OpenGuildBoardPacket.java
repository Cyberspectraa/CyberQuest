package com.cyberspectraa.cyberquest.network.packet;

import com.cyberspectraa.cyberquest.client.GuildBoardScreen;
import com.cyberspectraa.cyberquest.network.GuildBoardOfferData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record OpenGuildBoardPacket(
    BlockPos pos,
    int guildReputation,
    List<GuildBoardOfferData> offers
) {
    public OpenGuildBoardPacket {
        offers = offers == null
            ? List.of()
            : List.copyOf(offers);
    }

    public static void encode(
        OpenGuildBoardPacket packet,
        FriendlyByteBuf buffer
    ) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeInt(packet.guildReputation());
        buffer.writeVarInt(packet.offers().size());

        for (GuildBoardOfferData offer
                : packet.offers()) {
            buffer.writeVarInt(offer.slot());
            buffer.writeUtf(offer.questId(), 160);
            buffer.writeUtf(offer.title(), 256);
            buffer.writeUtf(offer.task(), 512);
            buffer.writeUtf(offer.reward(), 256);
            buffer.writeUtf(offer.timer(), 128);
            buffer.writeUtf(offer.penalty(), 128);
            buffer.writeUtf(offer.state(), 32);
        }
    }

    public static OpenGuildBoardPacket decode(
        FriendlyByteBuf buffer
    ) {
        BlockPos pos = buffer.readBlockPos();
        int reputation = buffer.readInt();
        int size = Math.min(
            8,
            Math.max(0, buffer.readVarInt())
        );

        List<GuildBoardOfferData> offers =
            new ArrayList<>();

        for (int i = 0; i < size; i++) {
            offers.add(
                new GuildBoardOfferData(
                    buffer.readVarInt(),
                    buffer.readUtf(160),
                    buffer.readUtf(256),
                    buffer.readUtf(512),
                    buffer.readUtf(256),
                    buffer.readUtf(128),
                    buffer.readUtf(128),
                    buffer.readUtf(32)
                )
            );
        }

        return new OpenGuildBoardPacket(
            pos,
            reputation,
            offers
        );
    }

    public static void handle(
        OpenGuildBoardPacket packet,
        Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context =
            contextSupplier.get();

        DistExecutor.unsafeRunWhenOn(
            Dist.CLIENT,
            () -> () -> GuildBoardScreen.open(
                packet.pos(),
                packet.guildReputation(),
                packet.offers()
            )
        );

        context.setPacketHandled(true);
    }
}
