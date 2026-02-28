package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SlotCrafting.class)
public class MixinSlotCrafting_SimpleRandom {

    @Shadow(remap = false)
    private CraftingResult crafting_result;

    private static final Random RANDOM = new Random();
    private static double[] PROBABILITIES = null;

    private static double[] getProbabilities() {
        if (PROBABILITIES == null) {
            PROBABILITIES = QualityCombinedConfig.getProbabilities();
        }
        return PROBABILITIES;
    }

    @Inject(method = "onPickupFromSlot", at = @At("HEAD"))
    private void onPickupFromSlot(EntityPlayer player, ItemStack itemStack, CallbackInfo ci) {
        if (itemStack == null) return;

        // 跳过修复配方
        if (crafting_result != null && crafting_result.quality_override != null) {
            return;
        }

        Item item = itemStack.getItem();
        if (!item.hasQuality()) return;

        EnumQuality maxQuality = item.getMaxQuality();
        int maxOrdinal = maxQuality.ordinal();
        double[] probs = getProbabilities();

        // 计算总概率（配置中所有品质的概率，可能包含超出上限的）
        double totalProb = 0.0;
        for (double p : probs) {
            totalProb += p;
        }

        // 如果总概率为0（配置全0），则直接取最高品质
        if (totalProb <= 0.0) {
            itemStack.setQuality(maxQuality);
            return;
        }

        // 在0~1之间随机，遍历所有8个品质
        double r = RANDOM.nextDouble();
        double cumulative = 0.0;
        int selectedOrdinal = 0; // 默认
        for (int i = 0; i < probs.length; i++) {
            cumulative += probs[i];
            if (r < cumulative) {
                selectedOrdinal = i;
                break;
            }
        }

        // 如果选中的品质超出物品允许的最高品质，则降级到最高品质
        if (selectedOrdinal > maxOrdinal) {
            selectedOrdinal = maxOrdinal;
        }

        itemStack.setQuality(EnumQuality.values()[selectedOrdinal]);
    }
}