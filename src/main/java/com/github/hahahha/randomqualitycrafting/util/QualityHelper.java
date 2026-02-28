package com.github.hahahha.randomqualitycrafting.util;

import com.github.hahahha.randomqualitycrafting.config.QualityCombinedConfig;
import net.minecraft.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QualityHelper {
    private static final Random RANDOM = new Random();

    // 材质耐久值缓存
    private static final Map<Material, Float> DURABILITY_CACHE = new HashMap<>();

    // 材质到对应锭物品的缓存（自动构建）
    private static final Map<Material, Item> INGOT_CACHE = new HashMap<>();
    private static boolean ingotCacheBuilt = false;

    // 初始化锭缓存（延迟构建，只在需要时）
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

    /**
     * 获取材质对应的锭物品（用于未来扩展，当前未使用）
     */
    public static Item getIngotForMaterial(Material material) {
        if (material == null) return null;
        ensureIngotCacheBuilt();
        return INGOT_CACHE.get(material);
    }

    // ========== 品质随机化（与现有逻辑一致） ==========
    public static void randomizeQuality(ItemStack stack) {
        if (stack == null) return;
        Item item = stack.getItem();
        if (!item.hasQuality()) return;

        EnumQuality maxQuality = item.getMaxQuality();
        int maxOrdinal = maxQuality.ordinal();
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
        if (selectedOrdinal > maxOrdinal) selectedOrdinal = maxOrdinal;
        stack.setQuality(EnumQuality.values()[selectedOrdinal]);
    }

    // ========== 材质相关 ==========
    public static Material getItemMaterial(Item item) {
        if (item instanceof ItemTool) {
            return ((ItemTool) item).getToolMaterial();
        } else if (item instanceof ItemArmor) {
            return ((ItemArmor) item).getArmorMaterial();
        } else {
            return item.getMaterialForRepairs();
        }
    }

    /**
     * 判断材料槽中的锭是否与工具材质匹配
     */
    public static boolean isMatchingIngot(Item toolItem, ItemStack ingotStack) {
        if (toolItem == null || ingotStack == null) return false;
        Material toolMaterial = getItemMaterial(toolItem);
        if (toolMaterial == null) return false;
        Material ingotMaterial = ingotStack.getItem().getMaterialForRepairs();
        return ingotMaterial != null && toolMaterial == ingotMaterial;
    }

    /**
     * 获取材质的耐久值（通过反射 + 缓存）
     * 非金属返回 0，异常时返回 0（避免误判）
     */
    public static float getMaterialDurability(Material material) {
        if (material == null) return 0;
        if (!material.isMetal()) return 0; // 非金属不支持重铸

        Float cached = DURABILITY_CACHE.get(material);
        if (cached != null) return cached;

        try {
            Field field = Material.class.getDeclaredField("durability");
            field.setAccessible(true);
            float durability = field.getFloat(material);
            DURABILITY_CACHE.put(material, durability);
            return durability;
        } catch (Exception e) {
            // 反射失败，记录错误并返回 0，保证重铸不可用
            e.printStackTrace();
            return 0.0f;
        }
    }

    /**
     * 判断铁砧材质是否足够用于重铸（需要铁砧耐久 ≥ 工具耐久）
     */
    public static boolean canRepairOnAnvil(Material anvilMaterial, Material toolMaterial) {
        if (anvilMaterial == null || toolMaterial == null) return false;
        float anvilDur = getMaterialDurability(anvilMaterial);
        float toolDur = getMaterialDurability(toolMaterial);
        return anvilDur > 0 && toolDur > 0 && anvilDur >= toolDur;
    }

    /**
     * 从铁砧方块获取其金属材质
     */
    public static Material getAnvilMaterial(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockAnvil) {
            return ((BlockAnvil) block).getMetalType();
        }
        return null;
    }
}