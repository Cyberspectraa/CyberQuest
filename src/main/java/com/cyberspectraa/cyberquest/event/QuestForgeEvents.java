package com.cyberspectraa.cyberquest.event;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import com.cyberspectraa.cyberquest.quest.QuestManager;
import com.cyberspectraa.cyberquest.quest.QuestNpcInteraction;
import com.cyberspectraa.cyberquest.quest.QuestRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = CyberQuest.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class QuestForgeEvents {
    private QuestForgeEvents() {
    }

    @SubscribeEvent
    public static void onReloadListeners(
        AddReloadListenerEvent event
    ) {
        event.addListener(new QuestRegistry());
    }

    @SubscribeEvent
    public static void onLogin(
        PlayerEvent.PlayerLoggedInEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        PlayerQuestData.ensure(player);
        QuestManager.refreshDynamicObjectives(player);
        QuestNetwork.syncJournal(player, false);
    }

    @SubscribeEvent
    public static void onClone(
        PlayerEvent.Clone event
    ) {
        if (!(event.getOriginal()
                instanceof ServerPlayer oldPlayer)
                || !(event.getEntity()
                instanceof ServerPlayer newPlayer)) {
            return;
        }

        PlayerQuestData.copy(
            oldPlayer,
            newPlayer
        );
    }

    @SubscribeEvent
    public static void onDeath(
        LivingDeathEvent event
    ) {
        if (event.getEntity().level().isClientSide
                || !(event.getSource().getEntity()
                    instanceof ServerPlayer player)) {
            return;
        }

        QuestManager.recordKill(
            player,
            event.getEntity()
        );
    }

    @SubscribeEvent
    public static void onAdvancement(
        AdvancementEvent.AdvancementEarnEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        QuestManager.recordAdvancement(
            player,
            event.getAdvancement().getId()
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(
        TickEvent.PlayerTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player
                    instanceof ServerPlayer player)
                || player.tickCount % 20 != 0) {
            return;
        }

        QuestManager.refreshDynamicObjectives(player);
    }

    @SubscribeEvent
    public static void onEntityInteract(
        PlayerInteractEvent.EntityInteract event
    ) {
        if (event.getHand() != InteractionHand.MAIN_HAND
                || !(event.getEntity()
                    instanceof ServerPlayer player)
                || player.level().isClientSide) {
            return;
        }

        QuestNpcInteraction.handle(
            player,
            event.getTarget()
        );
    }
}
