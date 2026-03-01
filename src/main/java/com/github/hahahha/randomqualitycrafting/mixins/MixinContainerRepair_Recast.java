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

@Mixin(ContainerRepair.class)
public abstract class MixinContainerRepair_Recast {

    @Shadow
    private IInventory inputSlots;

    @Shadow
    private IInventory outputSlot;

    @Shadow
    private int field_82861_i; // x
    @Shadow
    private int field_82858_j; // y
    @Shadow
    private int field_82859_k; // z

    @Shadow
    private int stackSizeToBeUsedInRepair;

    @Inject(method = "updateRepairOutput", at = @At("RETURN"))
    private void onUpdateRepairOutputReturn(CallbackInfo ci) {
        if (!QualityCombinedConfig.isRecastEnabled()) {
            return;
        }
        if (QualityCombinedConfig.isITECompatibilityEnabled() && Compatibility.HAS_ITE) {
            return;
        }
        EntityPlayer player = ((Container)(Object)this).player;
        if (player == null) return;

        ItemStack toolStack = inputSlots.getStackInSlot(0);
        ItemStack materialStack = inputSlots.getStackInSlot(1);

        if (outputSlot.getStackInSlot(0) != null) return;
        if (toolStack == null || materialStack == null) return;

        Item toolItem = toolStack.getItem();

        if (!toolItem.isDamageable() || !toolItem.hasQuality()) return;
        if (toolStack.getItemDamage() != 0) return;
        if (!QualityHelper.isMatchingIngot(toolItem, materialStack)) return;

        Material toolMaterial = QualityHelper.getItemMaterial(toolItem);
        if (toolMaterial == null) return;

        Material anvilMaterial = QualityHelper.getAnvilMaterial(player.worldObj, field_82861_i, field_82858_j, field_82859_k);
        if (anvilMaterial == null) return;

        if (!QualityHelper.canRepairOnAnvil(anvilMaterial, toolMaterial)) return;

        ItemStack resultStack = toolStack.copy();
        resultStack.setItemDamage(0);
        // 使用原版 NBT 操作
        if (resultStack.stackTagCompound == null) {
            resultStack.setTagCompound(new NBTTagCompound());
        }
        resultStack.stackTagCompound.setBoolean("rqc_recast", true);
        QualityHelper.applyRandomQuality(resultStack);

        this.stackSizeToBeUsedInRepair = 1;

        outputSlot.setInventorySlotContents(0, resultStack);
        ((Container)(Object)this).detectAndSendChanges();
    }
}