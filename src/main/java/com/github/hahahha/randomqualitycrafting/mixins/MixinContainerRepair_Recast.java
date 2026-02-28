package com.github.hahahha.randomqualitycrafting.mixins;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import com.github.hahahha.randomqualitycrafting.util.QualityHelper;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

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

    @Inject(method = "updateRepairOutput", at = @At("HEAD"), cancellable = true)
    private void onUpdateRepairOutput(CallbackInfo ci) {
        if (!QualityCombinedConfig.isRecastEnabled()) {
            return; // 开关关闭，不执行重铸逻辑
        }
        EntityPlayer player = ((Container)(Object)this).player;
        if (player == null) return;

        ItemStack toolStack = inputSlots.getStackInSlot(0);
        ItemStack materialStack = inputSlots.getStackInSlot(1);

        if (toolStack == null || materialStack == null) return;

        Item toolItem = toolStack.getItem();

        // 重铸条件：工具可损坏、有品质、耐久满、材料匹配
        if (!toolItem.isDamageable() || !toolItem.hasQuality()) return;
        if (toolStack.getItemDamage() != 0) return;
        if (!QualityHelper.isMatchingIngot(toolItem, materialStack)) return;

        Material toolMaterial = QualityHelper.getItemMaterial(toolItem);
        if (toolMaterial == null) return;

        Material anvilMaterial = QualityHelper.getAnvilMaterial(player.worldObj, field_82861_i, field_82858_j, field_82859_k);
        if (anvilMaterial == null) return;

        if (!QualityHelper.canRepairOnAnvil(anvilMaterial, toolMaterial)) return;

        // 生成预览物品
        ItemStack resultStack = toolStack.copy();
        resultStack.setItemDamage(0);
        QualityHelper.randomizeQuality(resultStack);

        // ========== 关键：设置原版消耗数量为 1 ==========
        try {
            Field field = ContainerRepair.class.getDeclaredField("stackSizeToBeUsedInRepair");
            field.setAccessible(true);
            field.setInt((ContainerRepair)(Object)this, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        outputSlot.setInventorySlotContents(0, resultStack);
        ((Container)(Object)this).detectAndSendChanges();

        ci.cancel();
    }
}