package com.cyberspectraa.cyberquest;

import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.registry.ModBlocks;
import com.cyberspectraa.cyberquest.registry.ModCreativeTabs;
import com.cyberspectraa.cyberquest.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CyberQuest.MOD_ID)
public final class CyberQuest {
    public static final String MOD_ID = "cyberquest";
    public static final Logger LOGGER =
        LogUtils.getLogger();

    public CyberQuest() {
        IEventBus modBus =
            FMLJavaModLoadingContext.get()
                .getModEventBus();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        QuestNetwork.init();
    }
}
