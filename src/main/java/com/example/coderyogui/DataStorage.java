package com.example.coderyogui;

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
            ConfigurationSection guiSection = guisSection.getConfigurationSection(name);
            int rows = guiSection.getInt("rows", 3);
            Map<Integer, GUIPage> pages = new HashMap<>();
            ConfigurationSection pagesSection = guiSection.getConfigurationSection("pages");
            if (pagesSection != null) {
                for (String pageId : pagesSection.getKeys(false)) {
                    ConfigurationSection pageSection = pagesSection.getConfigurationSection(pageId);
                    // 兼容舊數據：如果有 allow_take 或 allow_place，轉換為 allow_interact
                    boolean allowInteract = pageSection.getBoolean("allow_interact", 
                        pageSection.getBoolean("allow_take", false) || pageSection.getBoolean("allow_place", false));
                    Map<Integer, GUIItem> items = new HashMap<>();
                    ConfigurationSection itemsSection = pageSection.getConfigurationSection("items");
                    if (itemsSection != null) {
                        for (String slot : itemsSection.getKeys(false)) {
                            ConfigurationSection itemSection = itemsSection.getConfigurationSection(slot);
                            String material = itemSection.getString("material");
                            String itemName = itemSection.getString("name");
                            List<String> lore = itemSection.getStringList("lore");
                            boolean takeable = itemSection.getBoolean("takeable", false);
                            List<GUIAction> actions = new ArrayList<>();
                            List<Map<String, Object>> actionsList = (List<Map<String, Object>>) itemSection.get("actions");
                            if (actionsList != null) {
                                for (Map<String, Object> actionData : actionsList) {
                                    actions.add(new GUIAction(
                                        (String) actionData.get("type"),
                                        (String) actionData.get("value"),
                                        (Boolean) actionData.getOrDefault("as_console", false)
                                    ));
                                }
                            }
                            items.put(Integer.parseInt(slot), new GUIItem(material, itemName, lore, takeable, actions));
                        }
                    }
                    pages.put(Integer.parseInt(pageId), new GUIPage(items, allowInteract));
                }
            }
            // 確保至少有一頁
            if (pages.isEmpty()) {
                pages.put(1, new GUIPage());
                plugin.getLogger().warning("GUI " + name + " 缺少頁面數據，已自動添加 pageId=1");
            }
            plugin.getGuiManager().getGUIs().put(name, new CustomGUI(name, rows, pages));
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
                        itemSection.set("name", item.name());
                        itemSection.set("lore", item.lore());
                        itemSection.set("takeable", item.takeable());
                        List<Map<String, Object>> actions = new ArrayList<>();
                        for (GUIAction action : item.actions()) {
                            Map<String, Object> actionData = new HashMap<>();
                            actionData.put("type", action.type());
                            actionData.put("value", action.value());
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
}