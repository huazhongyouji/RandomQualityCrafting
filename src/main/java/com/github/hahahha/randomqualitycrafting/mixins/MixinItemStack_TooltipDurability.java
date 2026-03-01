package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.minecraft.EnumQuality;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class MixinItemStack_TooltipDurability {

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/EnumQuality;getDurabilityModifier()F"))
    private float redirectDurabilityModifier(EnumQuality quality) {
        return QualityCombinedConfig.getDurabilityModifier(quality);
    }
}