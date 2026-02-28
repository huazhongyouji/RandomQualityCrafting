package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.util.QualityHelper;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(SlotRepairOrEnchantConsumable.class)
public class MixinSlotRepairOrEnchantConsumable {

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void onIsItemValid(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot)(Object)this;
        Container container = null;
        try {
            Field field = Slot.class.getDeclaredField("container");
            field.setAccessible(true);
            container = (Container) field.get(slot);
        } catch (Exception e) {
            return;
        }
        if (!(container instanceof ContainerRepair)) return;

        ContainerRepair repairContainer = (ContainerRepair) container;
        Slot toolSlot = repairContainer.getSlot(0);
        ItemStack toolStack = toolSlot.getStack();
        if (toolStack == null) return;

        Item toolItem = toolStack.getItem();

        if (!toolItem.isDamageable() || !toolItem.hasQuality() || toolStack.getItemDamage() != 0) return;

        if (QualityHelper.isMatchingIngot(toolItem, stack)) {
            cir.setReturnValue(true);
        }
    }
}