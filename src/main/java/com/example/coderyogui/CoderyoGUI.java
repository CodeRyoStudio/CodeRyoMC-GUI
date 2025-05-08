package com.example.coderyogui;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoderyoGUI extends JavaPlugin implements Listener {
    private GUIManager guiManager;
    private DataStorage dataStorage;
    private LanguageManager languageManager;
    private final Map<UUID, EditSession> editSessions = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.languageManager = new LanguageManager(this);
        this.guiManager = new GUIManager(this);
        this.dataStorage = new DataStorage(this);
        dataStorage.loadGUIs();

        CommandHandler commandHandler = new CommandHandler(this);
        getCommand("coderyogui").setExecutor(commandHandler);
        getCommand("coderyogui").setTabCompleter(new TabCompleterImpl(this));

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getLogger().info(languageManager.getTranslation(null, "message.plugin_enabled"));
    }

    @Override
    public void onDisable() {
        dataStorage.saveGUIsAsync();
        getLogger().info(languageManager.getTranslation(null, "message.plugin_disabled"));
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public EditSession getEditSession(UUID playerId) {
        return editSessions.get(playerId);
    }

    public void setEditSession(UUID playerId, EditSession session) {
        if (session == null) {
            editSessions.remove(playerId);
        } else {
            editSessions.put(playerId, session);
        }
    }
}