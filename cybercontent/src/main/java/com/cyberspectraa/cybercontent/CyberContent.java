package com.cyberspectraa.cybercontent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CyberContent.MOD_ID)
public final class CyberContent {
    public static final String MOD_ID = "cybercontent";

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(
            ForgeRegistries.ITEMS,
            MOD_ID
        );

    public static final RegistryObject<Item> CHOSO_PLUSH =
        ITEMS.register(
            "choso_plush",
            () -> new Item(
                new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
            )
        );

    public CyberContent() {
        IEventBus modBus =
            FMLJavaModLoadingContext.get()
                .getModEventBus();

        ITEMS.register(modBus);
    }

    @Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static final class ModEvents {
        private ModEvents() {
        }

        @SubscribeEvent
        public static void onCreativeTab(
            BuildCreativeModeTabContentsEvent event
        ) {
            if (event.getTabKey()
                    == CreativeModeTabs.INGREDIENTS) {
                event.accept(CHOSO_PLUSH);
            }
        }
    }
}
