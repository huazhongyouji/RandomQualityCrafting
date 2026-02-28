package com.github.hahahha.randomqualitycrafting.mixins;

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

    // 禁用右键切换品质
    @Inject(method = "onSlotClicked", at = @At("HEAD"), cancellable = true)
    private void onSlotClicked(EntityPlayer player, int button, Container container, CallbackInfo ci) {
        if (button == 1) {
            ci.cancel();
        }
    }

    // 强制返回1，移除“Right-click to select...”提示
    @Inject(method = "getNumCraftingResults", at = @At("HEAD"), cancellable = true)
    private void getNumCraftingResults(EntityPlayer player, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }
}