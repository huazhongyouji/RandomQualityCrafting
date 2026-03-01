package com.github.hahahha.randomqualitycrafting;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.ModResourceManager;
import net.xiaoyu233.fml.reload.event.MITEEvents;

public class RandomQualityCrafting implements ModInitializer {
    public static final String MOD_ID = "randomqualitycrafting";

    @Override
    public void onInitialize() {
        ModResourceManager.addResourcePackDomain(MOD_ID);
        MITEEvents.MITE_EVENT_BUS.register(new EventListen());
        QualityCombinedConfig.preload();
    }
}