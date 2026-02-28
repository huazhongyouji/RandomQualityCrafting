package com.github.hahahha.randomqualitycrafting.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.EnumQuality;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class QualityCombinedConfig {
    private static final String CONFIG_FILE = "config/randomqualitycrafting_config.json";
    private static QualityConfigData data = null;

    // ========== 内部数据类（与 JSON 结构完全对应） ==========
    private static class QualityConfigData {
        LinkedHashMap<String, Double> probabilities = new LinkedHashMap<>();
        LinkedHashMap<String, QualityAttribute> attributes = new LinkedHashMap<>();
        boolean enableRecast = true;  // 重铸功能开关，默认开启
    }

    public static class QualityAttribute {
        public float durability;
        public QualityAttribute() {}
        public QualityAttribute(float durability) { this.durability = durability; }
    }

    // ========== 公共访问方法 ==========
    public static boolean isRecastEnabled() {
        if (data == null) load();
        return data.enableRecast;
    }

    public static double[] getProbabilities() {
        if (data == null) load();
        EnumQuality[] qualities = EnumQuality.values();
        double[] array = new double[qualities.length];
        for (int i = 0; i < qualities.length; i++) {
            array[i] = data.probabilities.getOrDefault(qualities[i].getUnlocalizedName(), 0.0);
        }
        return array;
    }

    public static float getDurabilityModifier(EnumQuality quality) {
        if (data == null) load();
        QualityAttribute attr = data.attributes.get(quality.getUnlocalizedName());
        return attr != null ? attr.durability : getDefaultDurability(quality);
    }

    // ========== 加载/保存配置 ==========
    public static void preload() {
        if (data == null) load();
    }

    private static void load() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }

        try (Reader reader = new FileReader(configFile)) {
            Gson gson = new Gson();
            Type type = new TypeToken<QualityConfigData>(){}.getType();
            data = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            data = getDefaultData();
        }

        // 确保所有品质都有概率值
        Map<String, Double> defaultProb = getDefaultProbabilities();
        for (String key : defaultProb.keySet()) {
            data.probabilities.putIfAbsent(key, defaultProb.get(key));
        }

        // 概率归一化（确保总和为1）
        double sum = data.probabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(sum - 1.0) > 0.0001) {
            for (String key : data.probabilities.keySet()) {
                data.probabilities.put(key, data.probabilities.get(key) / sum);
            }
            saveConfig(configFile); // 保存归一化后的值
        }

        // 确保所有品质都有属性
        Map<String, QualityAttribute> defaultAttr = getDefaultAttributes();
        for (String key : defaultAttr.keySet()) {
            data.attributes.putIfAbsent(key, defaultAttr.get(key));
        }
    }

    private static void saveConfig(File configFile) {
        try (Writer writer = new FileWriter(configFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException ignored) {}
    }

    private static void createDefaultConfig(File configFile) {
        configFile.getParentFile().mkdirs();
        data = getDefaultData();
        saveConfig(configFile);
    }

    private static QualityConfigData getDefaultData() {
        QualityConfigData defaultData = new QualityConfigData();
        defaultData.probabilities = getDefaultProbabilities();
        defaultData.attributes = getDefaultAttributes();
        defaultData.enableRecast = true;
        return defaultData;
    }

    private static LinkedHashMap<String, Double> getDefaultProbabilities() {
        LinkedHashMap<String, Double> map = new LinkedHashMap<>();
        map.put("wretched", 0.05);
        map.put("poor",    0.10);
        map.put("average", 0.65);
        map.put("fine",    0.10);
        map.put("excellent", 0.05);
        map.put("superb",   0.03);
        map.put("masterwork", 0.015);
        map.put("legendary", 0.005);
        return map;
    }

    private static LinkedHashMap<String, QualityAttribute> getDefaultAttributes() {
        LinkedHashMap<String, QualityAttribute> map = new LinkedHashMap<>();
        map.put("wretched", new QualityAttribute(0.5f));
        map.put("poor",    new QualityAttribute(0.75f));
        map.put("average", new QualityAttribute(1.0f));
        map.put("fine",    new QualityAttribute(1.5f));
        map.put("excellent", new QualityAttribute(2.0f));
        map.put("superb",  new QualityAttribute(2.5f));
        map.put("masterwork", new QualityAttribute(3.0f));
        map.put("legendary", new QualityAttribute(3.5f));
        return map;
    }

    private static float getDefaultDurability(EnumQuality quality) {
        switch (quality) {
            case wretched: return 0.5f;
            case poor: return 0.75f;
            case average: return 1.0f;
            case fine: return 1.5f;
            case excellent: return 2.0f;
            case superb: return 2.5f;
            case masterwork: return 3.0f;
            case legendary: return 3.5f;
            default: return 1.0f;
        }
    }
}