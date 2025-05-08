package com.example.coderyogui;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.IllegalFormatException;
import java.util.logging.Logger;

public class LanguageManager {
    private final CoderyoGUI plugin;
    private final Logger logger;
    private final Map<String, Map<String, String>> languages = new HashMap<>();
    private final Map<UUID, String> playerLanguages = new HashMap<>();
    private final String defaultLanguage;
    private final Map<String, String> localeMappings;

    public LanguageManager(CoderyoGUI plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.defaultLanguage = plugin.getConfig().getString("default_language", "en");
        this.localeMappings = new HashMap<>();
        loadLocaleMappings();
        loadLanguages();
    }

    private void loadLocaleMappings() {
        localeMappings.put("zh_TW", "zh");
        localeMappings.put("zh_HK", "zh");
        localeMappings.put("en_US", "en");
        localeMappings.put("en_GB", "en");
        localeMappings.put("ja_JP", "ja");
        localeMappings.put("default", defaultLanguage);
    }

    private void loadLanguages() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        String[] supportedLanguages = {"en", "zh", "ja"};
        for (String lang : supportedLanguages) {
            File langFile = new File(langDir, lang + ".yml");
            if (!langFile.exists()) {
                try (InputStream is = plugin.getResource("lang/" + lang + ".yml")) {
                    if (is != null) {
                        plugin.saveResource("lang/" + lang + ".yml", false);
                        logger.info("Copied default language file: " + lang + ".yml");
                    } else {
                        logger.warning("Missing default language file in JAR: " + lang + ".yml");
                    }
                } catch (Exception e) {
                    logger.severe("Failed to copy default language file: " + lang + ".yml, " + e.getMessage());
                }
            }

            if (langFile.exists()) {
                try {
                    YamlConfiguration config = new YamlConfiguration();
                    config.load(langFile);
                    Map<String, String> translations = new HashMap<>();
                    flattenConfig(config, "", translations);
                    languages.put(lang, translations);
                    logger.info("Loaded language: " + lang);
                } catch (IOException | InvalidConfigurationException e) {
                    logger.severe("Failed to load language file: " + lang + ".yml, falling back to English. Error: " + e.getMessage());
                }
            }
        }

        if (!languages.containsKey("en")) {
            logger.warning("English language file (en.yml) is missing or invalid. Using key as fallback.");
        }
    }

    private void flattenConfig(YamlConfiguration config, String prefix, Map<String, String> translations) {
        for (String key : config.getKeys(true)) {
            if (!config.isConfigurationSection(key)) {
                String value = config.getString(key, key);
                translations.put(prefix + (prefix.isEmpty() ? "" : ".") + key, value);
            }
        }
    }

    public String getTranslation(Player player, String key, Object... args) {
        String lang = getPlayerLanguage(player);
        Map<String, String> translations = languages.getOrDefault(lang, languages.getOrDefault("en", new HashMap<>()));
        String translation = translations.getOrDefault(key, languages.getOrDefault("en", new HashMap<>()).getOrDefault(key, key));
        try {
            return ChatColor.translateAlternateColorCodes('&', String.format(translation, args));
        } catch (IllegalFormatException e) {
            logger.warning("Invalid format in translation key: " + key + ", value: " + translation);
            return ChatColor.translateAlternateColorCodes('&', translation);
        }
    }

    public String getPlayerLanguage(Player player) {
        if (player == null) {
            return defaultLanguage; // Use default language when player is null
        }
        String lang = playerLanguages.getOrDefault(player.getUniqueId(), null);
        if (lang != null && languages.containsKey(lang)) {
            return lang;
        }
        String locale = player.getLocale();
        lang = localeMappings.getOrDefault(locale, localeMappings.get("default"));
        return languages.containsKey(lang) ? lang : "en";
    }

    public boolean setPlayerLanguage(Player player, String lang) {
        if (!languages.containsKey(lang)) {
            return false;
        }
        playerLanguages.put(player.getUniqueId(), lang);
        updatePlayerInventory(player);
        return true;
    }

    private void updatePlayerInventory(Player player) {
        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        if (holder instanceof EditorHolder editorHolder) {
            CustomGUI gui = editorHolder.getGUI();
            int pageId = editorHolder.getPageId();
            int slot = editorHolder.getSlot();
            String title = player.getOpenInventory().getTitle();
            if (title.startsWith(ChatColor.stripColor(getTranslation(player, "gui.title.edit", "")))) {
                GUIEditor.openEditor(player, gui, pageId);
            } else if (title.equals(getTranslation(player, "gui.title.item_settings"))) {
                GUIEditor.openContextMenu(player, gui, slot, pageId);
            } else if (title.startsWith(getTranslation(player, "gui.title.select_item")) || title.startsWith(getTranslation(player, "gui.title.search_item", ""))) {
                GUIEditor.openItemSelect(player, gui, slot, pageId, editorHolder.getSearch(), editorHolder.getSearchPage());
            } else if (title.equals(getTranslation(player, "gui.title.select_rows"))) {
                GUIEditor.openRowSelect(player, gui, pageId);
            }
        } else if (holder instanceof MainMenuHolder menuHolder) {
            new MainMenuGUI(plugin, menuHolder.getPage()).open(player);
        } else if (holder instanceof GUIListHolder listHolder) {
            new GUIListGUI(plugin, listHolder.getPage(), listHolder.isEditMode(), listHolder.getSearch()).open(player);
        } else if (holder instanceof DeleteGUIListHolder deleteHolder) {
            new DeleteGUIListGUI(plugin, deleteHolder.getPage(), deleteHolder.getSearch()).open(player);
        } else if (holder instanceof DeleteConfirmHolder confirmHolder) {
            DeleteGUIListGUI.openConfirm(player, confirmHolder.getGuiName());
        } else if (holder instanceof GUIHolder guiHolder) {
            player.openInventory(guiHolder.getGUI().getPage(guiHolder.getPageId()));
        }
    }

    public List<String> getAvailableLanguages() {
        return new ArrayList<>(languages.keySet());
    }
}