package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.Compatibility;
import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import com.github.hahahha.randomqualitycrafting.util.QualityHelper;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public class MixinSlotCrafting_SimpleRandom {

    @Shadow(remap = false)
    private CraftingResult crafting_result;

    @Inject(method = "onPickupFromSlot", at = @At("HEAD"))
    private void onPickupFromSlot(EntityPlayer player, ItemStack itemStack, CallbackInfo ci) {
        if (QualityCombinedConfig.isITECompatibilityEnabled() && Compatibility.HAS_ITE) {
            return;
        }
        if (itemStack == null) return;
        if (crafting_result != null && crafting_result.quality_override != null) {
            return;
        }
        QualityHelper.applyRandomQuality(itemStack);
    }
}