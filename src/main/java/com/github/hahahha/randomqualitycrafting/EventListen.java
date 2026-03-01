package com.github.hahahha.randomqualitycrafting;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import com.google.common.eventbus.Subscribe;
import net.minecraft.EntityPlayer;
import net.minecraft.EnumQuality;
import net.minecraft.StatCollector;
import net.xiaoyu233.fml.reload.event.*;

public class EventListen {

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
        double diff = expected - 1.0;
        String diffPercent = String.format("%+.2f%%", diff * 100);

        String modName = StatCollector.translateToLocal("mod.randomqualitycrafting.name");
        String message = StatCollector.translateToLocalFormatted(
                "randomqualitycrafting.loginMessage",
                modName,
                diffPercent
        );
        player.addChatMessage(message);

        boolean iteCompatibility = QualityCombinedConfig.isITECompatibilityEnabled();
        if (Compatibility.HAS_ITE) {
            if (iteCompatibility) {
                String info = StatCollector.translateToLocal("randomqualitycrafting.info.ite_compatibility_on");
                player.addChatMessage(info);
            } else {
                String warn = StatCollector.translateToLocal("randomqualitycrafting.warn.ite_compatibility_off");
                player.addChatMessage(warn);
            }
        }
    }

    @Subscribe public void onItemRegister(ItemRegistryEvent event) {}
    @Subscribe public void onRecipeRegister(RecipeRegistryEvent event) {}
    @Subscribe public void handleChatCommand(HandleChatCommandEvent event) {}
    @Subscribe public void onEntityRegister(EntityRegisterEvent event) {}
    @Subscribe public void onEntityRendererRegistry(EntityRendererRegistryEvent event) {}
    @Subscribe public void onTileEntityRegister(TileEntityRegisterEvent event) {}
    @Subscribe public void onTileEntityRendererRegister(TileEntityRendererRegisterEvent event) {}
    @Subscribe public void onSoundsRegister(SoundsRegisterEvent event) {}
}