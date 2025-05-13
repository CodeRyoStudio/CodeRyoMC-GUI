package com.coderyo.coderyogui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DataStorage {
    private final CoderyoGUI plugin;
    private final File file;
    private final YamlConfiguration config;

    public DataStorage(CoderyoGUI plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "guis.yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("無法創建 guis.yml: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void loadGUIs() {
        ConfigurationSection guisSection = config.getConfigurationSection("guis");
        if (guisSection == null) return;
        for (String name : guisSection.getKeys(false)) {
            try {
                ConfigurationSection guiSection = guisSection.getConfigurationSection(name);
                if (guiSection == null) {
                    plugin.getLogger().warning("無效的 GUI 配置: " + name);
                    continue;
                }
                int rows = guiSection.getInt("rows", 3);
                Map<Integer, GUIPage> pages = new HashMap<>();
                ConfigurationSection pagesSection = guiSection.getConfigurationSection("pages");
                if (pagesSection != null) {
                    for (String pageId : pagesSection.getKeys(false)) {
                        try {
                            ConfigurationSection pageSection = pagesSection.getConfigurationSection(pageId);
                            if (pageSection == null) {
                                plugin.getLogger().warning("無效的頁面配置: GUI " + name + ", page " + pageId);
                                continue;
                            }
                            boolean allowInteract = pageSection.getBoolean("allow_interact",
                                    pageSection.getBoolean("allow_take", false) || pageSection.getBoolean("allow_place", false));
                            Map<Integer, GUIItem> items = new HashMap<>();
                            ConfigurationSection itemsSection = pageSection.getConfigurationSection("items");
                            if (itemsSection != null) {
                                for (String slot : itemsSection.getKeys(false)) {
                                    try {
                                        ConfigurationSection itemSection = itemsSection.getConfigurationSection(slot);
                                        if (itemSection == null) {
                                            plugin.getLogger().warning("無效的物品配置: GUI " + name + ", page " + pageId + ", slot " + slot);
                                            continue;
                                        }
                                        String material = itemSection.getString("material");
                                        String itemName = itemSection.getString("name");
                                        List<String> lore = itemSection.getStringList("lore");
                                        boolean takeable = itemSection.getBoolean("takeable", false);
                                        List<GUIAction> actions = new ArrayList<>();
                                        List<Map<String, Object>> actionsList = (List<Map<String, Object>>) itemSection.get("actions");
                                        if (actionsList != null) {
                                            for (Map<String, Object> actionData : actionsList) {
                                                try {
                                                    String type = (String) actionData.get("type");
                                                    String value = (String) actionData.get("value");
                                                    if (type == null || value == null) {
                                                        plugin.getLogger().warning("無效的動作配置: GUI " + name + ", page " + pageId + ", slot " + slot);
                                                        continue;
                                                    }
                                                    Boolean asConsole = (Boolean) actionData.getOrDefault("as_console", false);
                                                    actions.add(new GUIAction(type, value, asConsole));
                                                } catch (Exception e) {
                                                    plugin.getLogger().warning("解析動作失敗: GUI " + name + ", page " + pageId + ", slot " + slot + ": " + e.getMessage());
                                                }
                                            }
                                        }
                                        items.put(Integer.parseInt(slot), new GUIItem(material, itemName, lore, takeable, actions));
                                    } catch (Exception e) {
                                        plugin.getLogger().warning("解析物品失敗: GUI " + name + ", page " + pageId + ", slot " + slot + ": " + e.getMessage());
                                    }
                                }
                            }
                            pages.put(Integer.parseInt(pageId), new GUIPage(items, allowInteract));
                        } catch (Exception e) {
                            plugin.getLogger().warning("解析頁面失敗: GUI " + name + ", page " + pageId + ": " + e.getMessage());
                        }
                    }
                }
                if (pages.isEmpty()) {
                    pages.put(1, new GUIPage());
                    plugin.getLogger().warning("GUI " + name + " 缺少頁面數據，已自動添加 pageId=1");
                }
                plugin.getGuiManager().getGUIs().put(name, new CustomGUI(name, rows, pages));
            } catch (Exception e) {
                plugin.getLogger().severe("解析 GUI 失敗: " + name + ": " + e.getMessage());
            }
        }
    }

    public void saveGUIsAsync() {
        CompletableFuture.runAsync(() -> {
            ConfigurationSection guisSection = config.createSection("guis");
            for (Map.Entry<String, CustomGUI> entry : plugin.getGuiManager().getGUIs().entrySet()) {
                ConfigurationSection guiSection = guisSection.createSection(entry.getKey());
                CustomGUI gui = entry.getValue();
                guiSection.set("rows", gui.rows());
                ConfigurationSection pagesSection = guiSection.createSection("pages");
                for (Map.Entry<Integer, GUIPage> pageEntry : gui.pages().entrySet()) {
                    ConfigurationSection pageSection = pagesSection.createSection(String.valueOf(pageEntry.getKey()));
                    GUIPage page = pageEntry.getValue();
                    pageSection.set("allow_interact", page.allowInteract());
                    ConfigurationSection itemsSection = pageSection.createSection("items");
                    for (Map.Entry<Integer, GUIItem> itemEntry : page.items().entrySet()) {
                        ConfigurationSection itemSection = itemsSection.createSection(String.valueOf(itemEntry.getKey()));
                        GUIItem item = itemEntry.getValue();
                        itemSection.set("material", item.material());
                        itemSection.set("name", sanitizeString(item.name()));
                        itemSection.set("lore", sanitizeLore(item.lore()));
                        itemSection.set("takeable", item.takeable());
                        List<Map<String, Object>> actions = new ArrayList<>();
                        for (GUIAction action : item.actions()) {
                            Map<String, Object> actionData = new HashMap<>();
                            actionData.put("type", action.type());
                            actionData.put("value", sanitizeString(action.value()));
                            if (action.asConsole()) actionData.put("as_console", true);
                            actions.add(actionData);
                        }
                        itemSection.set("actions", actions);
                    }
                }
            }
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("保存 YAML 失敗: " + e.getMessage());
            }
        });
    }

    private String sanitizeString(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\n\\r\\t:;&?]", "").trim().substring(0, Math.min(input.length(), 100));
    }

    private List<String> sanitizeLore(List<String> lore) {
        if (lore == null) return new ArrayList<>();
        List<String> sanitized = new ArrayList<>();
        for (String line : lore) {
            if (line != null) {
                sanitized.add(sanitizeString(line));
            }
        }
        return sanitized;
    }
}