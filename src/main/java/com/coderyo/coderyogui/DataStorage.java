package com.coderyo.coderyogui;

import com.coderyo.coderyogui.api.ActionType; // Added import
import com.coderyo.coderyogui.api.StringSanitizer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;

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
                plugin.getLogger().severe("Failed to create guis.yml: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void loadGUIs() {
        try {
            ConfigurationSection guisSection = config.getConfigurationSection("guis");
            if (guisSection == null) return;
            for (String guiName : guisSection.getKeys(false)) {
                try {
                    ConfigurationSection guiSection = guisSection.getConfigurationSection(guiName);
                    int rows = guiSection.getInt("rows", 3);
                    Map<Integer, GUIPage> pages = new HashMap<>();
                    ConfigurationSection pagesSection = guiSection.getConfigurationSection("pages");
                    if (pagesSection != null) {
                        for (String pageId : pagesSection.getKeys(false)) {
                            ConfigurationSection pageSection = pagesSection.getConfigurationSection(pageId);
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
                                            String type = (String) actionData.get("type");
                                            if (!ActionType.isValid(type)) {
                                                plugin.getLogger().warning("Invalid action type in GUI " + guiName + ": " + type);
                                                continue;
                                            }
                                            String value = (String) actionData.get("value");
                                            boolean asConsole = (Boolean) actionData.getOrDefault("as_console", false);
                                            actions.add(new GUIAction(ActionType.valueOf(type.toUpperCase()), value, asConsole));
                                        }
                                    }
                                    items.put(Integer.parseInt(slot), new GUIItem(material, itemName, lore, takeable, actions));
                                }
                            }
                            pages.put(Integer.parseInt(pageId), new GUIPage(items, allowInteract));
                        }
                    }
                    if (pages.isEmpty()) {
                        pages.put(1, new GUIPage());
                        plugin.getLogger().warning("GUI " + guiName + " has no pages, added pageId=1");
                    }
                    plugin.getGuiManager().getGUIs().put(guiName, new CustomGUI(guiName, rows, pages));
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load GUI " + guiName + ": " + e.getMessage());
                    plugin.getServer().getConsoleSender().sendMessage("§c[CoderyoGUI] Failed to load GUI " + guiName + ". Check logs.");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load guis.yml: " + e.getMessage());
            plugin.getServer().getConsoleSender().sendMessage("§c[CoderyoGUI] Invalid guis.yml detected. Check logs for details.");
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
                        itemSection.set("material", StringSanitizer.sanitize(item.material()));
                        itemSection.set("name", StringSanitizer.sanitize(item.name()));
                        itemSection.set("lore", item.lore().stream().map(StringSanitizer::sanitize).toList());
                        itemSection.set("takeable", item.takeable());
                        List<Map<String, Object>> actions = new ArrayList<>();
                        for (GUIAction action : item.actions()) {
                            if (!ActionType.isValid(action.type().name())) {
                                plugin.getLogger().warning("Skipping invalid action type: " + action.type() + " in GUI " + gui.name());
                                continue;
                            }
                            Map<String, Object> actionData = new HashMap<>();
                            actionData.put("type", action.type().name().toLowerCase());
                            actionData.put("value", StringSanitizer.sanitize(action.value()));
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
                plugin.getLogger().severe("Failed to save guis.yml: " + e.getMessage());
                plugin.getServer().getConsoleSender().sendMessage("§c[CoderyoGUI] Failed to save guis.yml. Check logs.");
            }
        });
    }
}