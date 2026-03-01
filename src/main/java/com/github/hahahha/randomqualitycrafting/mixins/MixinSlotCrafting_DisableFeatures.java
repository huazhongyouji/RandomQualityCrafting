package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.Compatibility;
import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.minecraft.Container;
import net.minecraft.EntityPlayer;
import net.minecraft.SlotCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlotCrafting.class)
public class MixinSlotCrafting_DisableFeatures {

    @Inject(method = "onSlotClicked", at = @At("HEAD"), cancellable = true)
    private void onSlotClicked(EntityPlayer player, int button, Container container, CallbackInfo ci) {
        if (button == 1 && !(QualityCombinedConfig.isITECompatibilityEnabled() && Compatibility.HAS_ITE)) {
            ci.cancel();
        }
    }

    @Inject(method = "getNumCraftingResults", at = @At("HEAD"), cancellable = true)
    private void getNumCraftingResults(EntityPlayer player, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }
}