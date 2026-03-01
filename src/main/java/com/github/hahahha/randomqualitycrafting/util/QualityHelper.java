package com.github.hahahha.randomqualitycrafting.util;

import com.github.hahahha.randomqualitycrafting.Compatibility;
import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.minecraft.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QualityHelper {
    private static final Random RANDOM = new Random();

    private static final Map<Material, Float> DURABILITY_CACHE = new HashMap<>();
    private static final Map<Material, Item> INGOT_CACHE = new HashMap<>();
    private static boolean ingotCacheBuilt = false;

    private static void ensureIngotCacheBuilt() {
        if (ingotCacheBuilt) return;
        ingotCacheBuilt = true;
        for (int i = 0; i < Item.itemsList.length; i++) {
            Item item = Item.itemsList[i];
            if (item == null) continue;
            if (item instanceof ItemIngot) {
                Material mat = item.getMaterialForRepairs();
                if (mat != null && !INGOT_CACHE.containsKey(mat)) {
                    INGOT_CACHE.put(mat, item);
                }
            }
        }
    }

    public static Item getIngotForMaterial(Material material) {
        if (material == null) return null;
        ensureIngotCacheBuilt();
        return INGOT_CACHE.get(material);
    }

    /**
     * 核心随机品质方法（合并后）
     */
    public static void applyRandomQuality(ItemStack stack) {
        if (stack == null) return;
        Item item = stack.getItem();
        if (!item.hasQuality()) return;

        EnumQuality maxQuality = item.getMaxQuality();
        double[] probs = QualityCombinedConfig.getProbabilities();

        double totalProb = 0.0;
        for (double p : probs) totalProb += p;
        if (totalProb <= 0.0) {
            stack.setQuality(maxQuality);
            return;
        }

        double r = RANDOM.nextDouble();
        double cumulative = 0.0;
        int selectedOrdinal = 0;
        for (int i = 0; i < probs.length; i++) {
            cumulative += probs[i];
            if (r < cumulative) {
                selectedOrdinal = i;
                break;
            }
        }
        if (selectedOrdinal > maxQuality.ordinal()) {
            selectedOrdinal = maxQuality.ordinal();
        }
        stack.setQuality(EnumQuality.values()[selectedOrdinal]);
    }

    public static Material getItemMaterial(Item item) {
        if (item instanceof ItemTool) {
            return ((ItemTool) item).getToolMaterial();
        } else if (item instanceof ItemArmor) {
            return ((ItemArmor) item).getArmorMaterial();
        } else {
            return item.getMaterialForRepairs();
        }
    }

    public static boolean isMatchingIngot(Item toolItem, ItemStack ingotStack) {
        if (toolItem == null || ingotStack == null) return false;
        Material toolMaterial = getItemMaterial(toolItem);
        if (toolMaterial == null) return false;
        Material ingotMaterial = ingotStack.getItem().getMaterialForRepairs();
        return ingotMaterial != null && toolMaterial == ingotMaterial;
    }

    public static float getMaterialDurability(Material material) {
        if (material == null) return 0;
        if (!material.isMetal()) return 0;

        Float cached = DURABILITY_CACHE.get(material);
        if (cached != null) return cached;

        try {
            Field field = Material.class.getDeclaredField("durability");
            field.setAccessible(true);
            float durability = field.getFloat(material);
            DURABILITY_CACHE.put(material, durability);
            return durability;
        } catch (Exception e) {
            // 反射失败，记录错误并返回0
            e.printStackTrace();
            return 0.0f;
        }
    }

    public static boolean canRepairOnAnvil(Material anvilMaterial, Material toolMaterial) {
        if (anvilMaterial == null || toolMaterial == null) return false;
        float anvilDur = getMaterialDurability(anvilMaterial);
        float toolDur = getMaterialDurability(toolMaterial);
        return anvilDur > 0 && toolDur > 0 && anvilDur >= toolDur;
    }

    public static Material getAnvilMaterial(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockAnvil) {
            return ((BlockAnvil) block).getMetalType();
        }
        return null;
    }
}