package com.cyberspectraa.cyberquest.registry;

import com.cyberspectraa.cyberquest.CyberQuest;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            CyberQuest.MOD_ID
        );

    public static final RegistryObject<CreativeModeTab> CYBERQUEST =
        TABS.register(
            "cyberquest",
            () -> CreativeModeTab.builder()
                .title(
                    Component.translatable(
                        "itemGroup.cyberquest"
                    )
                )
                .icon(() ->
                    ModItems.GUILD_BOARD
                        .get()
                        .getDefaultInstance()
                )
                .displayItems(
                    (parameters, output) ->
                        output.accept(
                            ModItems.GUILD_BOARD.get()
                        );
                        output.accept(ModItems.GUILD_CARD.get());
                )
                .build()
        );

    private ModCreativeTabs() {
    }
}
