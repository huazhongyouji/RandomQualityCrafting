package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.Compatibility;
import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import com.github.hahahha.randomqualitycrafting.util.QualityHelper;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(Slot.class)
public class MixinSlotRepair_Output {

    @Inject(method = "onPickupFromSlot", at = @At("HEAD"), cancellable = true)
    private void onPickupFromSlot(EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        if (QualityCombinedConfig.isITECompatibilityEnabled() && Compatibility.HAS_ITE) {
            return;
        }
        Slot slot = (Slot)(Object)this;
        if (slot.slotNumber != 2) return;

        if (stack == null || stack.stackTagCompound == null || !stack.stackTagCompound.getBoolean("rqc_recast")) {
            return;
        }

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

        IInventory inputSlots = null;
        IInventory outputSlot = null;
        try {
            Field inputField = ContainerRepair.class.getDeclaredField("inputSlots");
            inputField.setAccessible(true);
            inputSlots = (IInventory) inputField.get(repairContainer);

            Field outputField = ContainerRepair.class.getDeclaredField("outputSlot");
            outputField.setAccessible(true);
            outputSlot = (IInventory) outputField.get(repairContainer);
        } catch (Exception e) {
            return;
        }

        ItemStack toolStack = inputSlots.getStackInSlot(0);
        ItemStack materialStack = inputSlots.getStackInSlot(1);
        if (toolStack == null || materialStack == null) return;

        Item toolItem = toolStack.getItem();
        if (!toolItem.isDamageable() || !toolItem.hasQuality()) return;
        if (toolStack.getItemDamage() != 0) return;
        if (!QualityHelper.isMatchingIngot(toolItem, materialStack)) return;

        materialStack.stackSize--;
        if (materialStack.stackSize <= 0) {
            inputSlots.setInventorySlotContents(1, null);
        }
        inputSlots.setInventorySlotContents(0, null);

        stack.stackTagCompound.removeTag("rqc_recast");
        if (stack.stackTagCompound.hasNoTags()) {
            stack.setTagCompound(null);
        }

        outputSlot.setInventorySlotContents(0, null);
        repairContainer.detectAndSendChanges();
        player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "random.anvil_use", 1.0f, 1.0f);

        ci.cancel();
    }
}