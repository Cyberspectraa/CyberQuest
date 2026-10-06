package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.network.packet.RequestJournalPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = CyberQuest.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT
)
public final class ClientForgeEvents {
    private ClientForgeEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(
        TickEvent.ClientTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.screen != null) {
            return;
        }

        while (ClientKeyMappings.QUEST_JOURNAL
                .consumeClick()) {
            QuestNetwork.sendToServer(
                new RequestJournalPacket()
            );
        }
    }
}
