package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.minecraft.EnumQuality;
import net.minecraft.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem_QualityAttributes {

    @Shadow
    private int maxDamage;

    @Inject(method = "getMaxDamage(Lnet/minecraft/EnumQuality;)I", at = @At("HEAD"), cancellable = true)
    private void onGetMaxDamage(EnumQuality quality, CallbackInfoReturnable<Integer> cir) {
        Item self = (Item)(Object)this;
        if (!self.hasQuality()) return;

        int baseDamage = this.maxDamage;
        if (quality == null) quality = EnumQuality.average;

        float modifier = QualityCombinedConfig.getDurabilityModifier(quality);
        int result = Math.max(Math.round(baseDamage * modifier), 1);
        cir.setReturnValue(result);
    }
}