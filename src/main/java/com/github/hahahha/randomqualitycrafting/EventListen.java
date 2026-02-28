package com.github.hahahha.randomqualitycrafting;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import com.google.common.eventbus.Subscribe;
import net.minecraft.EntityPlayer;
import net.minecraft.EnumQuality;
import net.minecraft.StatCollector;
import net.xiaoyu233.fml.reload.event.*;
import net.xiaoyu233.fml.reload.utils.IdUtil;

public class EventListen {
    //物品注册
    @Subscribe
    public void onItemRegister(ItemRegistryEvent event) {
    }

    //合成方式注册
    @Subscribe
    public void onRecipeRegister(RecipeRegistryEvent event) {
    }

    //玩家登录事件
    @Subscribe
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null) return;

        double[] probs = QualityCombinedConfig.getProbabilities();
        double expected = 0.0;
        for (int i = 0; i < probs.length; i++) {
            EnumQuality quality = EnumQuality.values()[i];
            float durabilityMod = QualityCombinedConfig.getDurabilityModifier(quality);
            expected += probs[i] * durabilityMod;
        }
        double diff = expected - 1.0; // 相对于平均品质的差值
        String diffPercent = String.format("%+.2f%%", diff * 100); // 例如 "+25.00%"

        String modName = StatCollector.translateToLocal("mod.randomqualitycrafting.name");
        String message = StatCollector.translateToLocalFormatted(
                "randomqualitycrafting.loginMessage",
                modName,
                diffPercent
        );
        player.addChatMessage(message);
    }

    //指令事件
    @Subscribe
    public void handleChatCommand(HandleChatCommandEvent event) {
    }

    //实体注册
    @Subscribe
    public void onEntityRegister(EntityRegisterEvent event) {
    }

    //实体渲染注册
    @Subscribe
    public void onEntityRendererRegistry(EntityRendererRegistryEvent event) {
    }

    //方块实体注册
    @Subscribe
    public void onTileEntityRegister(TileEntityRegisterEvent event) {
    }

    //方块实体渲染注册
    @Subscribe
    public void onTileEntityRendererRegister(TileEntityRendererRegisterEvent event) {
    }

    //声音注册
    @Subscribe
    public void onSoundsRegister(SoundsRegisterEvent event) {
    }

    public static int getNextEntityID() {
        return IdUtil.getNextEntityID();
    }
}
