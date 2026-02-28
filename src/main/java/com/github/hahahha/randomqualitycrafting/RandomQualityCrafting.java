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

        // 预加载合并配置，生成配置文件
        QualityCombinedConfig.preload();
        // 可选：显式调用 getProbabilities 以确保概率部分也加载（但 preload 已包含 load，足够）
    }
}