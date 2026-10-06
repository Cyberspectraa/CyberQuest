package com.cyberspectraa.cyberquest;

import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CyberQuest.MOD_ID)
public final class CyberQuest {
    public static final String MOD_ID = "cyberquest";
    public static final Logger LOGGER =
        LogUtils.getLogger();

    public CyberQuest() {
        QuestNetwork.init();
    }
}
