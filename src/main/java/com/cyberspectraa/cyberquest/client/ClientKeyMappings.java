package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
    modid = CyberQuest.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
)
public final class ClientKeyMappings {
    public static final KeyMapping QUEST_JOURNAL =
        new KeyMapping(
            "key.cyberquest.journal",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories.cyberquest"
        );

    private ClientKeyMappings() {
    }

    @SubscribeEvent
    public static void register(
        RegisterKeyMappingsEvent event
    ) {
        event.register(QUEST_JOURNAL);
    }
}
