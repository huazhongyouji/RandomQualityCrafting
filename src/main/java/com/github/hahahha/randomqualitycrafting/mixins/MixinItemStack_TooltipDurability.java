package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.minecraft.EnumQuality;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class MixinItemStack_TooltipDurability {

    /**
     * 将工具提示中获取耐久系数的调用重定向到配置类，使显示的百分比与实际系数一致。
     */
    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/EnumQuality;getDurabilityModifier()F"))
    private float redirectDurabilityModifier(EnumQuality quality) {
        return QualityCombinedConfig.getDurabilityModifier(quality);
    }
}